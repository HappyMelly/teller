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
import models.{ Brand, BrandStatus }
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

/**
 * `Brand` database table mapping.
 */
private[models] object Brands extends Table[Brand]("BRAND") {

  implicit val brandStatusTypeMapper = MappedTypeMapper.base[BrandStatus.Value, String](
    { status ⇒ status.toString },
    { status ⇒ BrandStatus.withName(status) })

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def code = column[String]("CODE")
  def name = column[String]("NAME")
  def coordinatorId = column[Long]("COORDINATOR_ID")

  def description = column[Option[String]]("DESCRIPTION")
  def status = column[BrandStatus.Value]("STATUS")
  def picture = column[Option[String]]("PICTURE")
  def created = column[DateTime]("CREATED")
  def createdBy = column[String]("CREATED_BY")

  def updated = column[DateTime]("UPDATED")
  def updatedBy = column[String]("UPDATED_BY")

  def coordinator = foreignKey("COORDINATOR_FK", coordinatorId, People)(_.id)

  def * = id.? ~ code ~ name ~ coordinatorId ~ description ~ status ~ picture ~
    created ~ createdBy ~ updated ~ updatedBy <> (Brand.apply _, Brand.unapply _)

  def forInsert = * returning id

  def forUpdate = code ~ name ~ coordinatorId ~ description ~ status ~ picture ~ updated ~ updatedBy

  def uniqueCode = index("IDX_CODE", code, unique = true)
}
