/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models

import models.service.Services
import org.joda.money.{CurrencyUnit, Money}
import org.joda.time.LocalDate
import play.api.i18n.Messages
import services.CurrencyConverter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

/**
 * Represents a (financial) Account. An account has an `AccountHolder`, which is either a `Person`, `Organisation` or
 * the `Levy`. Accounts have a currency set upon activation and may only be deactivated when their balance is zero.
 */
case class Account(id: Option[Long] = None,
    organisationId: Option[Long] = None,
    personId: Option[Long] = None,
    currency: CurrencyUnit = CurrencyUnit.EUR,
    active: Boolean = false) extends ActivityRecorder with Services {

  /**
   * Returns identifier of the object
   */
  def identifier: Long = id.getOrElse(0)

  /**
   * Returns string identifier which can be understood by human
   *
   * For example, for object 'Person' human identifier is "[FirstName] [LastName]"
   */
  def humanIdentifier: String = accountHolder.name

  /**
   * Returns type of this object
   */
  def objectType: String = Activity.Type.Account

  /** Resolves the holder for this account **/
  def accountHolder = (organisationId, personId) match {
    case (Some(o), None) ⇒
      val org = orgService.find(o).map {
        case None => throw new IllegalStateException(s"Organisation with id $o (account holder for account ${id.getOrElse("(NEW)")}) does not exist")
        case Some(value) => value
      }
      Await.result(org, 3.seconds)
    case (None, Some(p)) ⇒
      val person = personService.find(p).map {
        case None => throw new IllegalStateException(s"Person with id $p (account holder for account ${id.getOrElse("(NEW)")}) does not exist")
        case Some(value) => value
      }
      Await.result(person, 3.seconds)
    case (None, None) ⇒ Levy
    case _ ⇒ throw new IllegalStateException(s"Account $id has both organisation and person for holder")
  }

  def levy: Boolean = organisationId.isEmpty && personId.isEmpty

  /**
   * Returns a set of people involved in this account, either as the direct account holder or organisation member,
   * where the board members are ‘participants’ of the levy account.
   */
  def participants: Set[Person] = accountHolder match {
    case organisation: Organisation ⇒ organisation.people.toSet
    case person: Person ⇒ Set(person)
    case Levy ⇒ Await.result(personService.findActiveAdmins, 3.seconds)
  }

  def balance: Money = Await.result(accountService.findBalance(id.get, currency), 3.seconds)

  /**
   * Checks if the given user has permission to edit this account, including (de)activation:
   * - An account for a person may only be edited by that person
   * - An account for an organisation may only be edited by members of that organisation, or admins
   * - The Levy account may only be edited by admins
   */
  def editableBy(user: UserAccount) = {
    accountHolder match {
      case organisation: Organisation ⇒ user.admin || organisation.people.map(_.id.get).contains(user.personId)
      case person: Person ⇒ user.admin || person.id.get == user.personId
      case Levy ⇒ user.admin
    }
  }

  /**
   * Returns true if this account may be deleted.
   */
  lazy val deletable: Boolean = {
    val hasBookingEntries = id.exists { accountId ⇒
      Await.result(accountService.hasEntries(accountId), 3.seconds)
    }
    !active && !hasBookingEntries
  }

  /** Activates this account and sets the balance currency **/
  def activate(currency: CurrencyUnit): Unit = {
    if (active) throw new IllegalStateException("Cannot activate an already active account")
    assert(balance.isZero, "Inactive account's balance should be zero")
    accountService.updateStatus(this.identifier, active = true, currency)
  }

  /** Deactivates this account  **/
  def deactivate(): Unit = {
    if (!active) throw new IllegalStateException("Cannot deactivate an already inactive account")
    if (!balance.isZero) throw new IllegalStateException("Cannot deactivate with non-zero balance")
    accountService.updateStatus(this.identifier, active = false, currency)
  }

  def delete(): Unit = {
    assert(deletable, "Attempt to delete account that is active or has booking entries")
    accountService.delete(identifier)
  }

  def summary: AccountSummary = {
    AccountSummary(id.get, accountHolder.name, currency, active)
  }

}

/**
 * Account summary for use in views.
 *
 * @param id Account ID
 * @param name Account holder name
 */
case class AccountSummary(id: Long, name: String, currency: CurrencyUnit, active: Boolean)

case class AccountSummaryWithBalance(id: Long, name: String, balance: Money)

case class AccountSummaryWithAdjustment(id: Long, name: String, balance: Money, balanceConverted: Money, adjustment: Money)

object Account extends Services {

  def accountHolderName(firstName: Option[String], lastName: Option[String], organisation: Option[String]): String =
    (firstName, lastName, organisation) match {
      case (Some(first), Some(last), None) ⇒ first + " " + last
      case (None, None, Some(name)) ⇒ name
      case (None, None, None) ⇒ Levy.name
      case _ ⇒ throw new IllegalStateException(s"Invalid combination of first, last and organisation names ($firstName, $lastName, $organisation)")
    }

  /**
   * Returns the total balance for a set of accounts, which must all have the specified currency.
   */
  def calculateTotalBalance(currency: CurrencyUnit, accounts: List[AccountSummaryWithAdjustment]): Money = {
    accounts.foldLeft(Money.zero(currency)) { (balance, account) ⇒
      balance.plus(account.balanceConverted)
    }
  }

  /**
   * Balances all of the accounts by inserting booking entries that adjust all of the counts, resetting the total to
   * zero (plus the surplus left after rounding). Each booking entry is from the Levy to
   */
  def balanceAccounts(ownerId: Long)(implicit messages: Messages): Future[List[BookingEntry]] = {
    (for {
      levy <- accountService.get(Levy)
      accounts <- accountService.findAllForAdjustment(levy.currency)
    } yield (levy, accounts)) flatMap { case (levy, accounts) =>
      assert(levy.id.isDefined, "Levy must have an ID")
      import scala.concurrent.duration._
      // Don’t create any booking entries for zero adjustments.
      val accountsToAdjust = accounts.filter(!_.adjustment.isZero)
      val futureBookingEntries = accountsToAdjust.map { account ⇒
        val sourceAmount = account.adjustment
        val bookingEntry = CurrencyConverter.convert(sourceAmount, account.balance.getCurrencyUnit).map { toAmount ⇒
          val entry = adjustmentBookingEntry(ownerId, account.id, levy, account.adjustment, toAmount)
          Await.result(accountService.insertEntry(entry), 2.seconds)
        }
        Await.result(bookingEntry, Duration.create(2, "seconds"))
      }
      Future.successful(futureBookingEntries)
    }
  }

  /**
   * Creates a booking entry for a single account to use when balancing accounts. The booking entry’s ‘from amount’ is
   * zero, making this an unbalanced transaction, because the purpose is to re-balance other transactions.
   *
   * @param ownerId The ID of the person who is balancing accounts.
   * @param accountId The ID of the target account that this booking entry will adjust.
   * @param levy The Happy Melly Levy account, which the booking entry adjusts from.
   * @param adjustment The amount of money that the target account will be adjusted by, to balance accounts.
   * @param adjustmentConverted The adjustment amount converted to the target account’s currency.
   */
  def adjustmentBookingEntry(ownerId: Long,
                             accountId: Long,
                             levy: Account,
                             adjustment: Money,
                             adjustmentConverted: Money)(implicit messages: Messages): BookingEntry = {
    val today = LocalDate.now()
    val fromAmount = Money.zero(levy.currency)
    val summary = Messages("models.BookingEntry.summary.balance")
    BookingEntry(None, ownerId, today, None, summary, adjustment, BookingEntry.DefaultSourcePercentage, levy.id.get,
      fromAmount, accountId, adjustmentConverted, None, None, today)
  }

  /**
   * Calculates the adjustment per account, for balancing the accounts. For the initial implementation, this is just
   * the equal share of the total balance.
   */
  def calculateAdjustment(totalBalance: Money, accounts: List[AccountSummaryWithAdjustment]): Money = {
    totalBalance.dividedBy(accounts.size.toLong, java.math.RoundingMode.DOWN)
  }

  /**
   * Returns a summary of accounts for balancing accounts, which converts balances to the levy account’s balance
   * and calculates a balancing adjustment for each account.
   */
  def findAllForAdjustment(currency: CurrencyUnit): Future[List[AccountSummaryWithAdjustment]] = {
    val futureAccounts = Await.result(accountService.findAllActiveWithBalance, 3.seconds).map { account ⇒
      CurrencyConverter.convert(account.balance, currency).map { convertedBalance ⇒
        AccountSummaryWithAdjustment(account.id, account.name, account.balance, convertedBalance, Money.zero(currency))
      }
    }
    Future.sequence(futureAccounts).map { accountsWithConvertedBalance ⇒
      val totalBalance = calculateTotalBalance(currency, accountsWithConvertedBalance)
      val adjustment = calculateAdjustment(totalBalance, accountsWithConvertedBalance)
      accountsWithConvertedBalance.map(_.copy(adjustment = adjustment))
    }
  }
}
