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

import com.github.tototoshi.slick.JodaSupport._
import models.JodaMoney._
import models.payment.Record
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

private[models] object PaymentRecords extends Table[Record]("PAYMENT_RECORD") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def remoteId = column[String]("REMOTE_ID")
  def payerId = column[Long]("PAYER_ID")
  def objectId = column[Long]("OBJECT_ID")
  def person = column[Boolean]("PERSON")
  def description = column[String]("DESCRIPTION")
  def feeCurrency = column[String]("FEE_CURRENCY")
  def fee = column[BigDecimal]("FEE", O.DBType("DECIMAL(13,3)"))
  def created = column[DateTime]("CREATED")

  def * = id.? ~ remoteId ~ payerId ~ objectId ~ person ~ description ~
    feeCurrency ~ fee ~ created <> ({ r ⇒
      Record(r._1, r._2, r._3, r._4, r._5, r._6, r._7 -> r._8, r._9)
    }, { (r: Record) ⇒
      Some(r.id, r.remoteId, r.payerId, r.objectId, r.person, r.description,
        r.fee.getCurrencyUnit.getCode, r.fee.getAmount, r.created)
    })

  def forInsert = * returning id
}
