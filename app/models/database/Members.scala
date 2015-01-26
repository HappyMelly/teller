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
import models.Member
import org.joda.time.{ DateTime, LocalDate }
import play.api.db.slick.Config.driver.simple._

object Members extends Table[Member]("MEMBER") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def objectId = column[Option[Long]]("OBJECT_ID")
  def person = column[Boolean]("PERSON")
  def funder = column[Boolean]("FUNDER")
  def feeCurrency = column[String]("FEE_CURRENCY")
  def fee = column[BigDecimal]("FEE", O.DBType("DECIMAL(13,3)"))
  def since = column[LocalDate]("SINCE")
  def created = column[DateTime]("CREATED")
  def createdBy = column[Long]("CREATED_BY")
  def updated = column[DateTime]("UPDATED")
  def updatedBy = column[Long]("UPDATED_BY")

  def * = id.? ~ objectId ~ person ~ funder ~ feeCurrency ~ fee ~ since ~
    created ~ createdBy ~ updated ~ updatedBy <> ({
      m ⇒
        Member(m._1, m._2, m._3, m._4, m._5 -> m._6, m._7, m._8, m._9, m._10,
          m._11)
    }, {
      (m: Member) ⇒
        Some(m.id, m.objectId, m.person, m.funder, m.fee.getCurrencyUnit.getCode,
          m.fee.getAmount, m.since, m.created, m.createdBy, m.updated, m.updatedBy)
    })

  def forInsert = * returning id
}
