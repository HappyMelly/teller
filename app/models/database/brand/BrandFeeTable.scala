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

package models.database.brand

import models.JodaMoney._
import models.cm.brand.BrandFee
import slick.driver.JdbcProfile

private[models] trait BrandFeeTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * Connects EventFee object with its database representation
    */
  class BrandFees(tag: Tag) extends Table[BrandFee](tag, "BRAND_FEE") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def brandId = column[Long]("BRAND_ID")
    def country = column[String]("COUNTRY", O.Length(2))
    def feeCurrency = column[String]("FEE_CURRENCY", O.Length(3))
    def feeAmount = column[BigDecimal]("FEE_AMOUNT")

    type BrandFeesFields = (Option[Long], Long, String, String, BigDecimal)

    def * = (id.?, brandId, country, feeCurrency, feeAmount) <>(
      (f: BrandFeesFields) ⇒ BrandFee(f._1, f._2, f._3, f._4 -> f._5),
      (f: BrandFee) ⇒ Some(f.id, f.brand, f.country,
        f.fee.getCurrencyUnit.getCode, BigDecimal(f.fee.getAmount)))

  }

}