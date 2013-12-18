/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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

import models.JodaMoney._
import models.database._
import org.joda.time.{ DateTime, LocalDate }
import org.joda.money.{ CurrencyUnit, Money }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.Some
import services.CurrencyConverter
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import java.math.RoundingMode

/**
 * A financial (accounting) bookkeeping entry, which represents money owed from one account to another.
 */
case class BookingEntry(
  id: Option[Long],
  ownerId: Long,
  bookingDate: LocalDate,
  bookingNumber: Option[Int],
  summary: String,

  source: Money,
  sourcePercentage: Int,
  fromId: Long,
  fromAmount: Money,
  toId: Long,
  toAmount: Money,

  brandId: Long,
  reference: Option[String],
  referenceDate: LocalDate,
  description: Option[String] = None,
  url: Option[String] = None,
  transactionTypeId: Option[Long] = None,

  created: DateTime = DateTime.now()) {

  def from = Account.find(fromId).get

  def to = Account.find(toId).get

  def owner = Person.find(ownerId).get

  def brand = Brand.find(brandId).get

  def owes = source.isPositiveOrZero

  def transactionType = transactionTypeId.flatMap(TransactionType.find(_))

  def insert: BookingEntry = DB.withSession { implicit session: Session ⇒
    val nextBookingNumber = Some(BookingEntry.nextBookingNumber)
    val id = BookingEntries.forInsert.insert(this.copy(bookingNumber = nextBookingNumber))
    this.copy(id = Some(id), bookingNumber = nextBookingNumber)
  }

  /**
   * Returns the source amount with the percentage applied, used to calculate from and to amounts.
   */
  def sourceProRata: Money = {
    val percentage = BigDecimal(sourcePercentage) / BigDecimal(100)
    source.multipliedBy(percentage.underlying(), RoundingMode.DOWN)
  }

  /**
   * Creates a copy of this `BookingEntry` with the value of `fromAmount` and `toAmount` set by converting
   * `source` to the currency for the `from` and `to` accounts respectively, using today’s exchange rate,
   * and applying the source percentage.
   *
   * Returns a `Future` because WS calls are potentially involved.
   */
  def withSourceConverted: Future[BookingEntry] = {
    for {
      fromAmountConverted ← CurrencyConverter.convert(sourceProRata, from.currency)
      toAmountConverted ← CurrencyConverter.convert(sourceProRata, to.currency)
    } yield copy(fromAmount = fromAmountConverted, toAmount = toAmountConverted)
  }

}

/**
 * A view on a booking entry for the overview page.
 */
case class BookingEntrySummary(
  bookingNumber: Int,
  bookingDate: LocalDate,

  source: Money,
  sourcePercentage: Int,
  from: String,
  fromAmount: Money,
  owes: Boolean,
  to: String,
  toAmount: Money,

  brandCode: String,
  summary: String)

object BookingEntry {

  def blank = BookingEntry(None, 0L, LocalDate.now, None, "", Money.of(CurrencyUnit.EUR, 0f), 100,
    0, Money.zero(CurrencyUnit.EUR), 0, Money.zero(CurrencyUnit.EUR), 0, None, LocalDate.now)

  def findByBookingNumber(bookingNumber: Int): Option[BookingEntry] = DB.withSession { implicit session: Session ⇒
    Query(BookingEntries).filter(_.bookingNumber === bookingNumber).firstOption
  }

  // Define a query that does left outer joins on the to/from accounts’ optional person/organisation records.
  // For now, only the names are retrieved; if the web page requires hyperlinks, then a richer structure is needed.
  lazy val bookingEntriesQuery = for {
    entry ← BookingEntries
    brand ← entry.brand
    ((fromAccount, fromPerson), fromOrganisation) ← Accounts leftJoin
      People on (_.personId === _.id) leftJoin
      Organisations on (_._1.organisationId === _.id)
    if fromAccount.id === entry.fromId
    ((toAccount, toPerson), toOrganisation) ← Accounts leftJoin
      People on (_.personId === _.id) leftJoin
      Organisations on (_._1.organisationId === _.id)
    if toAccount.id === entry.toId
  } yield (fromAccount.id, toAccount.id, entry.created, entry.bookingNumber, entry.bookingDate,
    entry.sourceCurrency -> entry.sourceAmount, entry.sourcePercentage,
    fromPerson.firstName.?, fromPerson.lastName.?, fromOrganisation.name.?, entry.fromCurrency -> entry.fromAmount,
    toPerson.firstName.?, toPerson.lastName.?, toOrganisation.name.?, entry.toCurrency -> entry.toAmount,
    brand.code, entry.summary)

  type BookingEntriesQueryResult = (Long, Long, DateTime, Int, LocalDate, (String, BigDecimal), Int, Option[String], Option[String], Option[String], (String, BigDecimal), Option[String], Option[String], Option[String], (String, BigDecimal), String, String)

  val mapBookingEntryResult: (BookingEntriesQueryResult ⇒ BookingEntrySummary) = {
    case (fromId, toId, created, number, date, source, sourcePercentage, fromPersonFirstName, fromPersonLastName, fromOrganisation,
      fromAmount, toPersonFirstName, toPersonLastName, toOrganisation, toAmount, brandCode, summary) ⇒ {
      val from = Account.accountHolderName(fromPersonFirstName, fromPersonLastName, fromOrganisation)
      val to = Account.accountHolderName(toPersonFirstName, toPersonLastName, toOrganisation)
      val owes = source.isPositiveOrZero
      BookingEntrySummary(number, date, source, sourcePercentage, from, fromAmount, owes, to, toAmount, brandCode, summary)
    }
  }

  /**
   * Returns a list of entries in reverse chronological order of date created.
   */
  def findAll: List[BookingEntrySummary] = DB.withSession { implicit session: Session ⇒
    bookingEntriesQuery.sortBy(_._4.desc).mapResult(mapBookingEntryResult).list
  }

  /**
   * Returns a list of entries for the given account, in reverse chronological order of date created.
   */
  def findByAccountId(accountId: Long): List[BookingEntrySummary] = DB.withSession { implicit session: Session ⇒
    bookingEntriesQuery.filter(row ⇒ row._1 === accountId || row._2 === accountId).sortBy(_._4.desc).mapResult(mapBookingEntryResult).list
  }

  private def nextBookingNumber: Int = DB.withSession { implicit session: Session ⇒
    Query(BookingEntries.map(_.bookingNumber).max).first().map(_ + 1).getOrElse(1001)
  }

}
