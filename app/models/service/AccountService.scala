package models.service

import models.JodaMoney._
import models._
import models.database._
import org.joda.money.{Money, CurrencyUnit}
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import services.CurrencyConverter
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.math.BigDecimal.RoundingMode
import scala.language.postfixOps

/**
  * Set of methods for working with accounts
  */
class AccountService extends HasDatabaseConfig[JdbcProfile]
  with AccountTable
  with BrandTable
  with BookingEntryTable
  with OrganisationTable
  with PersonTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._
  private val accounts = TableQuery[Accounts]
  private val entries = TableQuery[BookingEntries]

  /**
    * Deletes the given account from database
    *
    * @param accountId Account identifier
    */
  def delete(accountId: Long): Future[Int] =
    db.run(accounts.filter(_.id === accountId).delete)


  /**
    * Returns an account for the given holder
    *
    * @param holder Holder
    */
  def get(holder: AccountHolder): Future[Account] = {
    val query = holder match {
      case o: Organisation ⇒ accounts.filter(_.organisationId === o.id)
      case p: Person ⇒ accounts.filter(_.personId === p.id)
      case Levy ⇒ accounts.filter(_.organisationId isEmpty).filter(_.personId isEmpty)
    }
    db.run(query.result).map(_.head)
  }

  /**
    * Returns the given account
    *
    * @param id Account identifier
    */
  def get(id: Long): Future[Account] = db.run(accounts.filter(_.id === id).result).map(_.head)

  /**
    * Returns the given account if exists
    *
    * @param id Account identifier
    */
  def find(id: Long): Future[Option[Account]] = db.run(accounts.filter(_.id === id).result).map(_.headOption)


  /**
    * Returns a summary list of active accounts.
    */
  def findAllActive: Future[List[AccountSummary]] = {
    val query = for {
      ((account, person), organisation) ← accounts joinLeft
        TableQuery[People] on (_.personId === _.id) joinLeft
        TableQuery[Organisations] on (_._1.organisationId === _.id)
      if account.active === true
    } yield (account, person, organisation)

    db.run(query.result).map(_.toList.map {
      case (account, Some(person), Some(organisation)) ⇒ {
        val holder = Account.accountHolderName(Some(person.firstName), Some(person.lastName), Some(organisation.name))
        AccountSummary(account.identifier, holder, account.currency, account.active)
      }
    }.sortBy(_.name.toLowerCase))
  }

  /**
    * Returns a summary list of active accounts with balances.
    */
  def findAllActiveWithBalance: Future[List[AccountSummaryWithBalance]] = {

    val bookingEntriesQuery = entries.filter(_.deleted === false)

    // Sum booking entries’ credits and debits, grouped by account ID.
    val creditQuery = bookingEntriesQuery.filter(_.sourceAmount > BigDecimal(0)).groupBy(_.toId).map {
      case (accountId, entry) ⇒
        accountId -> entry.map(_.toAmount).sum
    }
    val debitBackwardQuery = bookingEntriesQuery.filter(_.sourceAmount < BigDecimal(0)).groupBy(_.toId).map {
      case (accountId, entry) ⇒
        accountId -> entry.map(_.toAmount).sum
    }
    val debitQuery = bookingEntriesQuery.filter(_.sourceAmount > BigDecimal(0)).groupBy(_.fromId).map {
      case (accountId, entry) ⇒
        accountId -> entry.map(_.fromAmount).sum
    }
    val creditBackwardQuery = bookingEntriesQuery.filter(_.sourceAmount < BigDecimal(0)).groupBy(_.fromId).map {
      case (accountId, entry) ⇒
        accountId -> entry.map(_.fromAmount).sum
    }
    val actions = for {
      c <- creditQuery.result
      cb <- creditBackwardQuery.result
      d <- debitQuery.result
      db <- debitBackwardQuery.result
    } yield (c, cb, d, db)

    db.run(actions) map { case (credits, creditsBackward, debits, debitsBackward) =>
      (credits.toMap.mapValues(_.getOrElse(BigDecimal(0))),
        creditsBackward.toMap.mapValues(_.getOrElse(BigDecimal(0))),
        debits.toMap.mapValues(_.getOrElse(BigDecimal(0))),
        debitsBackward.toMap.mapValues(_.getOrElse(BigDecimal(0)))
        )
    } flatMap { case (credits, creditsBackward, debits, debitsBackward) =>
        findAllActive map { accounts =>
          accounts.map { account =>
            val accountDebit = debits.getOrElse(account.id, BigDecimal(0))
            val accountDebitBackward = debitsBackward.getOrElse(account.id, BigDecimal(0))
            val accountCredit = credits.getOrElse(account.id, BigDecimal(0))
            val accountCreditBackward = creditsBackward.getOrElse(account.id, BigDecimal(0))
            val balance = (accountDebit - accountDebitBackward - accountCredit + accountCreditBackward).setScale(account.currency.getDecimalPlaces, RoundingMode.DOWN)
            AccountSummaryWithBalance(account.id, account.name, Money.of(account.currency, balance.bigDecimal))
          }
        }
    }
  }

  /**
    * Calculates the balance for a certain account by subtracting the total amount sent from the total amount received.
    *
    * @param accountId The ID of the account to find the balance for.
    * @return The current balance for the account.
    */
  def findBalance(accountId: Long, currency: CurrencyUnit): Future[Money] = {
    val creditBackwardQuery = for {
      entry ← Entries.filtered if entry.fromId === accountId && entry.sourceAmount < BigDecimal(0)
    } yield entry.fromAmount
    val debitQuery = for {
      entry ← Entries.filtered if entry.fromId === accountId && entry.sourceAmount > BigDecimal(0)
    } yield entry.fromAmount

    val debitBackwardQuery = for {
      entry ← Entries.filtered if entry.toId === accountId && entry.sourceAmount < BigDecimal(0)
    } yield entry.toAmount
    val creditQuery = for {
      entry ← Entries.filtered if entry.toId === accountId && entry.sourceAmount > BigDecimal(0)
    } yield entry.toAmount

    val actions = for {
      credit <- creditQuery.sum.result
      creditBackward <- creditBackwardQuery.sum.result
      debit <- debitQuery.sum.result
      debitBackward <- debitBackwardQuery.sum.result
    } yield (credit, creditBackward, debit, debitBackward)
    db.run(actions) map { result =>
      val credit = result._1.getOrElse(BigDecimal(0))
      val creditBackward = result._2.getOrElse(BigDecimal(0))
      val debit = result._3.getOrElse(BigDecimal(0))
      val debitBackward = result._4.getOrElse(BigDecimal(0))

      val balance = (debit - debitBackward - credit + creditBackward).setScale(currency.getDecimalPlaces, RoundingMode.DOWN)
      Money.of(currency, balance.bigDecimal)
    }
  }


  /**
    * Returns a summary of accounts for balancing accounts, which converts balances to the levy account’s balance
    * and calculates a balancing adjustment for each account.
    */
  def findAllForAdjustment(currency: CurrencyUnit): Future[List[AccountSummaryWithAdjustment]] = {
    val futureAccounts = findAllActiveWithBalance map { _.map { account =>
        CurrencyConverter.convert(account.balance, currency).map { convertedBalance ⇒
          AccountSummaryWithAdjustment(account.id, account.name, account.balance, convertedBalance, Money.zero(currency))
        }
      }
    }
    futureAccounts flatMap { value =>
      Future.sequence(value).map { accountsWithConvertedBalance ⇒
        val totalBalance = Account.calculateTotalBalance(currency, accountsWithConvertedBalance)
        val adjustment = Account.calculateAdjustment(totalBalance, accountsWithConvertedBalance)
        accountsWithConvertedBalance.map(_.copy(adjustment = adjustment))
      }
    }
  }

  /**
    * Inserts the given entry to database
    * @param entry Entry
    */
  def insertEntry(entry: BookingEntry): Future[BookingEntry] = {
    val result = db.run(entries.map(_.bookingNumber).max.result).map(_.map(_ + 1).getOrElse(1001))
    result flatMap { nextBookingNumber =>
      val query = entries returning entries.map(_.id) into ((value, id) => value.copy(id = Some(id)))
      db.run(query += entry.copy(bookingNumber = Some(nextBookingNumber)))
    }
  }

  /**
    * Returns true if the given account has booking entries
    *
    * @param accountId Account identifier
    */
  def hasEntries(accountId: Long): Future[Boolean] =
    db.run(entries.filter(e ⇒ e.ownerId === accountId || e.fromId === accountId || e.toId === accountId).exists.result)

  /**
    * Updates the status of the given account
    *
    * @param accountId Account identifier
    * @param active Status
    * @param currency Currencty
    */
  def updateStatus(accountId: Long, active: Boolean, currency: CurrencyUnit): Unit = {
    val query = accounts.filter(_.id === accountId).map(v => (v.active, v.currency))
    db.run(query.update((active, currency)))
  }

}

object AccountService {
  private val _instance = new AccountService

  def get: AccountService = _instance
}
