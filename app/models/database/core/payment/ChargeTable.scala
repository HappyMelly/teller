/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models.database.core.payment

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.core.payment.Charge
import org.joda.time.DateTime
import slick.driver.JdbcProfile

private[models] trait ChargeTable {

  protected val driver: JdbcProfile
  import driver.api._

  class Charges(tag: Tag) extends Table[Charge](tag, "CHARGE") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def remoteId = column[String]("REMOTE_ID")
    def customerId = column[Long]("CUSTOMER_ID")
    def description = column[String]("DESCRIPTION")
    def amount = column[Float]("AMOUNT")
    def vat = column[Float]("VAT")
    def created = column[DateTime]("CREATED")

    type ChargeFields = (Option[Long], String, Long, String, Float, Float, DateTime)

    def * = (id.?, remoteId, customerId, description, amount, vat, created) <> (
      (c: ChargeFields) ⇒ Charge(c._1, c._2, c._3, c._4, c._5, c._6, c._7),
      (c: Charge) ⇒ Some(c.id, c.remoteId, c.customerId, c.description, c.amount, c.vat, c.created))

  }

}