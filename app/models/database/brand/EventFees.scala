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
import models.brand.EventFee
import play.api.db.slick.Config.driver.simple._

/**
 * Connects EventFee object with its database representation
 */
private[models] object EventFees extends Table[EventFee]("EVENT_FEE") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def brand = column[String]("BRAND", O.DBType("CHAR(5)"))
  def country = column[String]("COUNTRY", O.DBType("CHAR(2)"))
  def feeCurrency = column[String]("FEE_CURRENCY", O.DBType("CHAR(3)"))
  def feeAmount = column[BigDecimal]("FEE_AMOUNT", O.DBType("DECIMAL(13,3)"))

  def * = id.? ~ brand ~ country ~ feeCurrency ~ feeAmount <> ({
    _ match {
      case (id, brand, country, feeCurrency, feeAmount) ⇒
        EventFee(id, brand, country, feeCurrency -> feeAmount)
    }
  }, { (f: EventFee) ⇒
    Some(f.id, f.brand, f.country, f.fee.getCurrencyUnit.getCode, f.fee.getAmount)
  })

}
