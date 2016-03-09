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
import models.core.payment.CreditCard
import org.joda.time.DateTime
import slick.driver.JdbcProfile

private[models] trait CreditCardTable {

  protected val driver: JdbcProfile
  import driver.api._

  class CreditCards(tag: Tag) extends Table[CreditCard](tag, "CREDIT_CARD") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def customerId = column[Long]("CUSTOMER_ID")
    def remoteId = column[String]("REMOTE_ID")
    def brand = column[String]("BRAND", O.Length(10, varying = true))
    def number = column[String]("NUMBER", O.Length(4, varying = false))
    def expMonth = column[Int]("EXP_MONTH")
    def expYear = column[Int]("EXP_YEAR")
    def active = column[Boolean]("ACTIVE")
    def created = column[DateTime]("CREATED")

    type CreditCardFields = (Option[Long], Long, String, String, String, Int, Int, Boolean, DateTime)

    def * = (id.?, customerId, remoteId, brand, number, expMonth, expYear, active, created) <> (
      (c: CreditCardFields) ⇒ CreditCard(c._1, c._2, c._3, c._4, c._5, c._6, c._7, c._8, c._9),
      (c: CreditCard) ⇒ Some(c.id, c.customerId, c.remoteId, c.brand, c.number, c.expMonth, c.expYear,
        c.active, c.created))

  }

}