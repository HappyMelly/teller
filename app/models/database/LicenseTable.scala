/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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

package models.database

import models.JodaMoney._
import models.License
import org.joda.time.LocalDate
import slick.driver.JdbcProfile

private[models] trait LicenseTable extends BrandTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * `License` database table mapping.
    */
  class Licenses(tag: Tag) extends Table[License](tag, "LICENSE") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def licenseeId = column[Long]("LICENSEE_ID")
    def brandId = column[Long]("BRAND_ID")
    def version = column[String]("VERSION")
    def signed = column[LocalDate]("SIGNED")
    def start = column[LocalDate]("START")
    def end = column[LocalDate]("END")
    def confirmed = column[Boolean]("CONFIRMED")
    def feeCurrency = column[String]("FEE_CURRENCY")
    def feeAmount = column[BigDecimal]("FEE_AMOUNT", O.DBType("DECIMAL(13,3)"))
    def feePaidCurrency = column[Option[String]]("FEE_PAID_CURRENCY")
    def feePaidAmount = column[Option[BigDecimal]]("FEE_PAID_AMOUNT", O.DBType("DECIMAL(13,3)"))
    def licensee = foreignKey("LICENSEE_FK", licenseeId, TableQuery[People])(_.id)
    def brand = foreignKey("BRAND_FK", brandId, TableQuery[Brands])(_.id)

    type LicensesFields = (Option[Long], Long, Long, String, LocalDate, LocalDate, LocalDate, Boolean, String, BigDecimal, Option[String], Option[BigDecimal])

    def * = (id.?, licenseeId, brandId, version, signed, start, end, confirmed,
      feeCurrency, feeAmount, feePaidCurrency, feePaidAmount) <>(
      (l: LicensesFields) ⇒
        License(l._1, l._2, l._3, l._4, l._5, l._6, l._7, l._8, l._9 -> l._10, l._11 -> l._12),
      (l: License) ⇒
        Some((l.id, l.licenseeId, l.brandId, l.version, l.signed, l.start,
          l.end, l.confirmed, l.fee.getCurrencyUnit.getCode,
          BigDecimal(l.fee.getAmount), l.feePaid.map(_.getCurrencyUnit.getCode),
          l.feePaid.map(x ⇒ BigDecimal(x.getAmount)))))

    def forJoin = (id.?, licenseeId.?, brandId.?, start.?, end.?)

  }

}
