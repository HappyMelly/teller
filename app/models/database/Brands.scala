/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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

import models.database.PortableJodaSupport._
import models.Brand
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

/**
 * `Brand` database table mapping.
 */
private[models] class Brands(tag: Tag) extends Table[Brand](tag, "BRAND") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def code = column[String]("CODE")
  def uniqueName = column[String]("UNIQUE_NAME")
  def name = column[String]("NAME")
  def coordinatorId = column[Long]("COORDINATOR_ID")

  def description = column[Option[String]]("DESCRIPTION")
  def picture = column[Option[String]]("PICTURE")
  def generateCert = column[Boolean]("GENERATE_CERT")
  def tagLine = column[Option[String]]("TAGLINE")
  def webSite = column[Option[String]]("WEB_SITE")
  def blog = column[Option[String]]("BLOG")
  def contactEmail = column[String]("CONTACT_EMAIL")
  def evaluationHookUrl = column[Option[String]]("EVALUATION_HOOK_URL")
  def active = column[Boolean]("ACTIVE")
  def created = column[DateTime]("CREATED")
  def createdBy = column[String]("CREATED_BY")
  def updated = column[DateTime]("UPDATED")
  def updatedBy = column[String]("UPDATED_BY")

  def coordinator = foreignKey("COORDINATOR_FK", coordinatorId, TableQuery[People])(_.id)

  def * = (id.?, code, uniqueName, name, coordinatorId, description, picture,
    generateCert, tagLine, webSite, blog, contactEmail, evaluationHookUrl, active,
    created, createdBy, updated, updatedBy) <> ((Brand.apply _).tupled, Brand.unapply)

  def forUpdate = (code, uniqueName, name, coordinatorId, description,
    picture, tagLine, webSite, blog, contactEmail, evaluationHookUrl, updated, updatedBy)

  def uniqueCodeIndex = index("IDX_CODE", code, unique = true)
  def uniqueNameIndex = index("IDX_UNIQUE_NAME", uniqueName, unique = true)
}
