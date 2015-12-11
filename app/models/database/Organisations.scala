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
import models.{ DateStamp, Organisation }
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

/**
 * `Organisation` database table mapping.
 */
private[models] class Organisations(tag: Tag) extends Table[Organisation](tag, "ORGANISATION") {

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

  def webSite = column[Option[String]]("WEB_SITE")
  def blog = column[Option[String]]("BLOG")
  def contactEmail = column[Option[String]]("CONTACT_EMAIL")
  def customerId = column[Option[String]]("CUSTOMER_ID")

  def about = column[Option[String]]("ABOUT", O.DBType("TEXT"))
  def logo = column[Boolean]("LOGO")

  def active = column[Boolean]("ACTIVE")
  def created = column[DateTime]("CREATED")
  def createdBy = column[String]("CREATED_BY")
  def updated = column[DateTime]("UPDATED")
  def updatedBy = column[String]("UPDATED_BY")

  type OrganisationsFields = (Option[Long], String, Option[String], Option[String], Option[String], Option[String],
    Option[String], String, Option[String], Option[String],
    Option[String], Option[String], Option[String], Option[String],
    Option[String], Boolean,
    Boolean, DateTime, String, DateTime, String)

  def * = (id.?, name, street1, street2, city, province, postCode,
    countryCode, vatNumber, registrationNumber, webSite, blog, contactEmail,
    customerId, about, logo, active, created, createdBy, updated,
    updatedBy) <> (
      (o: OrganisationsFields) ⇒
        Organisation(o._1, o._2, o._3, o._4, o._5, o._6, o._7, o._8, o._9, o._10,
          o._11, o._12, o._13, o._14, o._15, o._16, o._17,
          DateStamp(o._18, o._19, o._20, o._21)),
      (o: Organisation) ⇒
        Some(o.id, o.name, o.street1, o.street2, o.city,
          o.province, o.postCode, o.countryCode, o.vatNumber, o.registrationNumber,
          o.webSite, o.blog, o.contactEmail, o.customerId, o.about, o.logo, o.active,
          o.dateStamp.created, o.dateStamp.createdBy, o.dateStamp.updated,
          o.dateStamp.updatedBy))

  def forUpdate = (id.?, name, street1, street2, city, province, postCode,
    countryCode, vatNumber, registrationNumber, webSite, blog, contactEmail,
    customerId, about, active, updated, updatedBy)
}
