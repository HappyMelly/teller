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
import models.Organisation
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

/**
 * `Organisation` database table mapping.
 */
private[models] object Organisations extends Table[Organisation]("ORGANISATION") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")

  def street1 = column[Option[String]]("STREET_1")
  def street2 = column[Option[String]]("STREET_2")
  def city = column[Option[String]]("CITY")
  def province = column[Option[String]]("PROVINCE")
  def postCode = column[Option[String]]("POST_CODE")

  def countryCode = column[String]("COUNTRY_CODE")
  def vatNumber = column[Option[String]]("VAT_NUMBER")
  def registrationNumber = column[Option[String]]("REGISTRATION_NUMBER")
  def legalEntity = column[Boolean]("LEGAL_ENTITY")

  def webSite = column[Option[String]]("WEB_SITE")
  def blog = column[Option[String]]("BLOG")

  def active = column[Boolean]("ACTIVE")
  def created = column[DateTime]("CREATED")
  def createdBy = column[String]("CREATED_BY")
  def updated = column[DateTime]("UPDATED")
  def updatedBy = column[String]("UPDATED_BY")

  def * = id.? ~ name ~ street1 ~ street2 ~ city ~ province ~ postCode ~ countryCode ~ vatNumber ~ registrationNumber ~
    legalEntity ~ webSite ~ blog ~ active ~ created ~ createdBy ~ updated ~ updatedBy <> (Organisation.apply _, Organisation.unapply _)

  def forInsert = * returning id

  def forUpdate = id.? ~ name ~ street1 ~ street2 ~ city ~ province ~ postCode ~ countryCode ~ vatNumber ~ registrationNumber ~
    legalEntity ~ webSite ~ blog ~ updated ~ updatedBy
}