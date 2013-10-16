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
import models.database.{ Accounts, Organisations, BookingEntries, People }
import org.joda.time.LocalDate
import org.joda.money.Money
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB._

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
  referenceDate: Option[LocalDate],
  description: Option[String],
  url: Option[String])

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

  /**
   * Returns a list of entries in reverse chronological order of booking date.
   */
  def findAll: List[BookingEntrySummary] = withSession { implicit session ⇒

    // Define a query that does left outer joins on the to/from accounts’ optional person/organisation records.
    // For now, only the names are retrieved; if the web page requires hyperlinks, then a richer structure is needed.
    val query = for {
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
    } yield (entry.bookingNumber, entry.bookingDate, entry.sourceCurrency -> entry.sourceAmount, entry.sourcePercentage,
      fromPerson.firstName.?, fromPerson.lastName.?, fromOrganisation.name.?, entry.fromCurrency -> entry.fromAmount,
      toPerson.firstName.?, toPerson.lastName.?, toOrganisation.name.?, entry.toCurrency -> entry.toAmount,
      brand.code, entry.summary)

    query.sortBy(_._2.desc).mapResult {
      case (number, date, source, sourcePercentage,
        fromPersonFirstName, fromPersonLastName, fromOrganisation, fromAmount,
        toPersonFirstName, toPersonLastName, toOrganisation, toAmount,
        brandCode, summary) ⇒
        val from = accountHolderName(fromPersonFirstName, fromPersonLastName, fromOrganisation)
        val to = accountHolderName(toPersonFirstName, toPersonLastName, toOrganisation)
        val owes = source.getAmount.signum >= 0
        BookingEntrySummary(number, date, source, sourcePercentage, from, fromAmount, owes, to, toAmount, brandCode, summary)
    }.list
  }

  private def accountHolderName(firstName: Option[String], lastName: Option[String], organisation: Option[String]): String =
    (firstName, lastName, organisation) match {
      case (Some(first), Some(last), None) ⇒ first + " " + last
      case (None, None, Some(name)) ⇒ name
      case (None, None, None) ⇒ "Levy"
      case _ ⇒ "(unknown)"
    }
}