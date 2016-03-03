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
package models.database.brand

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.brand.CreditLimit
import org.joda.time.LocalDate
import slick.driver.JdbcProfile

private[models] trait CreditLimitTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * Connects PeerCredit object with its database representation
    */
  class PeerCredits(tag: Tag) extends Table[CreditLimit](tag, "CREDIT_LIMIT") {

    def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
    def brandId = column[Long]("BRAND_ID")
    def amount = column[Int]("AMOUNT")
    def month = column[LocalDate]("MONTH")

    type CreditLimitFields = (Option[Long], Long, Int, LocalDate)

    def * = (id, brandId, amount, month) <>(
      (c: CreditLimitFields) => CreditLimit(c._1, c._2, c._3, c._4),
      (c: CreditLimit) => Some((c.id, c.brandId, c.amount, c.month)))

  }

}