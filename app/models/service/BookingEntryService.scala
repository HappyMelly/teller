package models.service

import com.github.tototoshi.slick.MySQLJodaSupport._
import models._
import models.database._
import org.joda.time.{LocalDate, DateTime}
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Set of methods for managing booking entries in database
  */
class BookingEntryService extends HasDatabaseConfig[JdbcProfile]
  with AccountTable
  with BookingEntryTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._
  private val entries = TableQuery[BookingEntries]

  // Define a query that does left outer joins on the to/from accounts’ optional person/organisation records.
  // For now, only the names are retrieved; if the web page requires hyperlinks, then a richer structure is needed.
  lazy val bookingEntriesQuery = for {
    (entry, brand) ← entries.filter(_.deleted === false) joinLeft TableQuery[Brands] on (_.brandId === _.id)
    ((fromAccount, fromPerson), fromOrganisation) ← TableQuery[Accounts] joinLeft
      TableQuery[People] on (_.personId === _.id) joinLeft
      TableQuery[Organisations] on (_._1.organisationId === _.id)
    if fromAccount.id === entry.fromId
    ((toAccount, toPerson), toOrganisation) ← TableQuery[Accounts] joinLeft
      TableQuery[People] on (_.personId === _.id) joinLeft
      TableQuery[Organisations] on (_._1.organisationId === _.id)
    if toAccount.id === entry.toId
  } yield (fromAccount, toAccount, entry, fromPerson, fromOrganisation, toPerson, toOrganisation, brand)

  type BookingEntriesQueryResult = (Account, Account, BookingEntry, Option[Person],
    Option[Organisation], Option[Person], Option[Organisation], Option[Brand])

  val mapBookingEntryResult: (BookingEntriesQueryResult ⇒ BookingEntrySummary) = {
    case (fromAccount, toAccount, entry, fromPerson, fromOrganisation, toPerson, toOrganisation, brand) ⇒
      val from = Account.accountHolderName(fromPerson.map(_.firstName), fromPerson.map(_.lastName), fromOrganisation.map(_.name))
      val to = Account.accountHolderName(toPerson.map(_.firstName), toPerson.map(_.lastName), toOrganisation.map(_.name))
      val owes = entry.source.isPositiveOrZero
      BookingEntrySummary(entry.bookingNumber.get, entry.bookingDate, entry.source, entry.sourcePercentage, from,
        entry.fromAmount, owes, to, entry.toAmount, brand.map(_.identifier), brand.map(_.name),
        entry.summary, entry.fromId, entry.toId)
  }

  /**
    * Soft-deletes the given booking entry by marking it as deleted
    */
  def delete(id: Long): Unit = db.run(entries.filter(_.id === id).map(_.deleted).update(true))


  /**
    * Returns a list of entries in reverse chronological order of date created.
    */
  def findAll: Future[List[BookingEntrySummary]] =
    db.run(bookingEntriesQuery.sortBy(_._3.bookingNumber.desc).result).map(_.toList.map(mapBookingEntryResult))

  /**
    * Returns an entry for the given booking number
    *
    * @param bookingNumber Booking number
    */
  def findByBookingNumber(bookingNumber: Int): Future[Option[BookingEntry]] =
    db.run(Entries.filtered.filter(_.bookingNumber === bookingNumber).result).map(_.headOption)


  /**
    * Returns a list of entries for the given account, in reverse chronological order of date created.
    */
  def find(accountId: Long, from: Option[LocalDate], to: Option[LocalDate]): Future[List[BookingEntrySummary]] = {
    val baseQuery = bookingEntriesQuery.filter(row ⇒ row._1.id === accountId || row._2.id === accountId)

    val fromQuery = from.map { fromDate ⇒
      baseQuery.filter(row ⇒ row._3.bookingDate >= fromDate)
    }.getOrElse(baseQuery)

    val toQuery = to.map { toDate ⇒
      fromQuery.filter(row ⇒ row._3.bookingDate <= toDate)
    }.getOrElse(fromQuery)

    db.run(toQuery.sortBy(_._3.bookingNumber.desc).result).map(_.toList.map(mapBookingEntryResult))
  }

  /**
    * Updates a booking entry without changing its ID, owner, booking number, booking date or date created.
    */
  def update(e: BookingEntry): Unit = {
    e.id.map { id ⇒
      val sourceAmount: BigDecimal = e.source.getAmount
      val toAmount: BigDecimal = e.toAmount.getAmount
      val fromAmount: BigDecimal = e.fromAmount.getAmount
      val updateTuple = (e.summary, e.source.getCurrencyUnit.getCode, sourceAmount, e.sourcePercentage, e.fromId,
        e.fromAmount.getCurrencyUnit.getCode, fromAmount, e.toId, e.toAmount.getCurrencyUnit.getCode, toAmount,
        e.brandId, e.reference, e.referenceDate, e.description, e.url, e.attachmentKey, e.transactionTypeId)
      db.run(entries.filter(_.id === id).map(_.forUpdate).update(updateTuple))
    }
  }
}

object BookingEntryService {
  private val _instance = new BookingEntryService

  def get: BookingEntryService = _instance
}