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
import models.{ Details, Location, Schedule, Event }
import org.joda.time.{ LocalDate, DateTime }
import play.api.db.slick.Config.driver.simple._

/**
 * `Event` database table mapping.
 */
private[models] object Events extends Table[Event]("EVENT") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def brandCode = column[String]("BRAND_CODE")
  def title = column[String]("TITLE")

  def spokenLanguage = column[String]("SPOKEN_LANGUAGE")
  def materialsLanguage = column[Option[String]]("MATERIALS_LANGUAGE")
  def city = column[String]("CITY")
  def countryCode = column[String]("COUNTRY_CODE")

  def description = column[Option[String]]("DESCRIPTION", O.DBType("TEXT"))
  def specialAttention = column[Option[String]]("SPECIAL_ATTENTION", O.DBType("TEXT"))
  def start = column[LocalDate]("START_DATE")
  def end = column[LocalDate]("END_DATE")
  def hoursPerDay = column[Int]("HOURS_PER_DAY")
  def totalHours = column[Int]("TOTAL_HOURS")

  def webSite = column[Option[String]]("WEB_SITE")
  def registrationPage = column[Option[String]]("REGISTRATION_PAGE")

  def notPublic = column[Boolean]("NOT_PUBLIC")
  def archived = column[Boolean]("ARCHIVED")

  def created = column[DateTime]("CREATED")
  def createdBy = column[String]("CREATED_BY")
  def updated = column[DateTime]("UPDATED")
  def updatedBy = column[String]("UPDATED_BY")

  def brand = foreignKey("BRAND_FK", brandCode, Brands)(_.code)

  def * = id.? ~ brandCode ~ title ~ spokenLanguage ~ materialsLanguage ~ city ~ countryCode ~
    description ~ specialAttention ~ start ~ end ~ hoursPerDay ~ totalHours ~ webSite ~ registrationPage ~
    notPublic ~ archived ~ created ~ createdBy ~ updated ~ updatedBy <> (
      e ⇒ Event(e._1, e._2, e._3, e._4, e._5, Location(e._6, e._7), Schedule(e._10, e._11, e._12, e._13), Details(e._8, e._9, e._14, e._15), e._16, e._17, e._18, e._19, e._20, e._21, Event.getFacilitatorIds(e._1.getOrElse(0))),
      (e: Event) ⇒ Some((e.id, e.brandCode, e.title, e.spokenLanguage, e.materialsLanguage, e.location.city, e.location.countryCode, e.details.description, e.details.specialAttention, e.schedule.start, e.schedule.end, e.schedule.hoursPerDay, e.schedule.totalHours, e.details.webSite, e.details.registrationPage, e.notPublic, e.archived, e.created, e.createdBy, e.updated, e.updatedBy)))

  def forInsert = * returning id

  def forUpdate = brandCode ~ title ~ spokenLanguage ~ materialsLanguage ~ city ~ countryCode ~
    description ~ specialAttention ~ start ~ end ~ hoursPerDay ~ totalHours ~ webSite ~ registrationPage ~
    notPublic ~ archived ~ updated ~ updatedBy
}