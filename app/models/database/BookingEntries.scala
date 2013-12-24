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

package models.database

import com.github.tototoshi.slick.JodaSupport._

import models.JodaMoney._
import models.BookingEntry
import org.joda.time.{ LocalDate, DateTime }
import play.api.db.slick.Config.driver.simple._

/**
 * `BookingEntry` database table mapping.
 */
object BookingEntries extends Table[BookingEntry]("BOOKING_ENTRY") {

  def filtered = Query(BookingEntries).filter(_.deleted === false)

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def ownerId = column[Long]("OWNER_ID")
  def bookingDate = column[LocalDate]("BOOKING_DATE")
  def bookingNumber = column[Int]("BOOKING_NUMBER")
  def summary = column[String]("SUMMARY")

  def sourceCurrency = column[String]("SOURCE_CURRENCY", O.DBType("CHAR(3)"))
  def sourceAmount = column[BigDecimal]("SOURCE_AMOUNT", O.DBType("DECIMAL(13,3)"))
  def sourcePercentage = column[Int]("SOURCE_PERCENTAGE")

  def fromId = column[Long]("FROM_ID")
  def fromCurrency = column[String]("FROM_CURRENCY", O.DBType("CHAR(3)"))
  def fromAmount = column[BigDecimal]("FROM_AMOUNT", O.DBType("DECIMAL(13,3)"))

  def toId = column[Long]("TO_ID")
  def toCurrency = column[String]("TO_CURRENCY", O.DBType("CHAR(3)"))
  def toAmount = column[BigDecimal]("TO_AMOUNT", O.DBType("DECIMAL(13,3)"))

  def brandId = column[Long]("BRAND_ID")
  def reference = column[Option[String]]("REFERENCE")
  def referenceDate = column[LocalDate]("REFERENCE_DATE")
  def description = column[Option[String]]("DESCRIPTION")
  def url = column[Option[String]]("URL")
  def transactionTypeId = column[Option[Long]]("TRANSACTION_TYPE_ID")

  def created = column[DateTime]("CREATED")

  def owner = foreignKey("BOOKING_OWNER_FK", ownerId, People)(_.id)
  def from = foreignKey("BOOKING_FROM_FK", fromId, Accounts)(_.id)
  def to = foreignKey("BOOKING_TO_FK", toId, Accounts)(_.id)
  def brand = foreignKey("BOOKING_BRAND_FK", brandId, Brands)(_.id)
  def transactionType = foreignKey("TRANSACTION_TYPE_FK", transactionTypeId, TransactionTypes)(_.id)

  def deleted = column[Boolean]("DELETED")

  def * = id.? ~ ownerId ~ bookingDate ~ bookingNumber.? ~ summary ~
    sourceCurrency ~ sourceAmount ~ sourcePercentage ~ fromId ~ fromCurrency ~ fromAmount ~ toId ~ toCurrency ~ toAmount ~
    brandId ~ reference ~ referenceDate ~ description ~ url ~ transactionTypeId ~ created <> (
      { (e) ⇒
        e match {
          case (id, ownerId, bookingDate, bookingNumber, summary,
            sourceCurrency, sourceAmount, sourcePercentage, fromId, fromCurrency, fromAmount, toId, toCurrency, toAmount,
            brandId, reference, referenceDate, description, url, transactionTypeId, created) ⇒

            BookingEntry(id, ownerId, bookingDate, bookingNumber, summary,
              sourceCurrency -> sourceAmount, sourcePercentage, fromId, fromCurrency -> fromAmount, toId, toCurrency -> toAmount,
              brandId, reference, referenceDate, description, url, transactionTypeId, created)
        }
      },
      { (e: BookingEntry) ⇒
        Some((e.id, e.ownerId, e.bookingDate, e.bookingNumber, e.summary,
          e.source.getCurrencyUnit.getCode, e.source.getAmount, e.sourcePercentage,
          e.fromId, e.fromAmount.getCurrencyUnit.getCode, e.fromAmount.getAmount,
          e.toId, e.toAmount.getCurrencyUnit.getCode, e.toAmount.getAmount,
          e.brandId, e.reference, e.referenceDate, e.description, e.url, e.transactionTypeId, e.created))
      })

  def forInsert = * returning id

}
