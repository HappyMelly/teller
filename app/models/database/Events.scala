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
import models._
import org.joda.time.LocalDate
import play.api.db.slick.Config.driver.simple._
import scala.slick.collection.heterogenous._
import scala.slick.collection.heterogenous.syntax._

/**
 * `Event` database table mapping.
 */
private[models] class Events(tag: Tag) extends Table[Event](tag, "EVENT") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def eventTypeId = column[Long]("EVENT_TYPE_ID")
  def brandId = column[Long]("BRAND_ID")
  def title = column[String]("TITLE")

  def spokenLanguage = column[String]("SPOKEN_LANGUAGE")
  def secondSpokenLanguage = column[Option[String]]("SECOND_SPOKEN_LANGUAGE")
  def materialsLanguage = column[Option[String]]("MATERIALS_LANGUAGE")
  def city = column[String]("CITY")
  def countryCode = column[String]("COUNTRY_CODE")

  def description = column[Option[String]]("DESCRIPTION", O.DBType("TEXT"))
  def specialAttention = column[Option[String]]("SPECIAL_ATTENTION", O.DBType("TEXT"))

  def organizerId = column[Long]("ORGANIZER_ID")
  def webSite = column[Option[String]]("WEB_SITE")
  def registrationPage = column[Option[String]]("REGISTRATION_PAGE")

  def start = column[LocalDate]("START_DATE")
  def end = column[LocalDate]("END_DATE")
  def hoursPerDay = column[Int]("HOURS_PER_DAY")
  def totalHours = column[Int]("TOTAL_HOURS")

  def notPublic = column[Boolean]("NOT_PUBLIC")
  def archived = column[Boolean]("ARCHIVED")
  def confirmed = column[Boolean]("CONFIRMED")
  def free = column[Boolean]("FREE")

  def rating = column[Float]("RATING", O.DBType("FLOAT(6,2)"))

  def brand = foreignKey("BRAND_FK", brandId, TableQuery[Brands])(_.id)

  // type EventsFields = (Option[Long], Long, Long, String, String, Option[String], Option[String], String, String, Option[String], Option[String], Option[String], Option[String], LocalDate, LocalDate, Int, Int, Boolean, Boolean, Boolean, Boolean, Float)

  def * = (id.? :: eventTypeId :: brandId :: title :: spokenLanguage ::
    secondSpokenLanguage :: materialsLanguage :: city :: countryCode ::
    description :: specialAttention :: organizerId :: webSite ::
    registrationPage :: start :: end :: hoursPerDay ::
    totalHours :: notPublic :: archived :: confirmed :: free :: rating ::
    HNil) <> (createEvent, extractEvent)

  type EventHList = Option[Long] :: Long :: Long :: String :: String ::
    Option[String] :: Option[String] :: String :: String :: Option[String] ::
    Option[String] :: Long :: Option[String] :: Option[String] :: LocalDate ::
    LocalDate :: Int :: Int :: Boolean :: Boolean :: Boolean :: Boolean ::
    Float :: HNil

  def createEvent(e: EventHList): Event =  e match {
    case id ::
      eventTypeId ::
      brandId ::
      title ::
      spokenLanguage ::
      secondSpokenLanguage ::
      materialsLanguage ::
      city ::
      countryCode ::
      description ::
      specialAttention ::
      organizerId ::
      webSite ::
      registrationPage ::
      start ::
      end ::
      hoursPerDay ::
      totalHours ::
      notPublic ::
      archived ::
      confirmed ::
      free ::
      rating ::
      HNil =>
    Event(id, eventTypeId, brandId, title,
      Language(spokenLanguage, secondSpokenLanguage, materialsLanguage),
      Location(city, countryCode),
      Details(description, specialAttention),
      Organizer(organizerId, webSite, registrationPage),
      Schedule(start, end, hoursPerDay, totalHours),
      notPublic, archived, confirmed, free, rating)
  }

  def extractEvent(e: Event): Option[EventHList] =
    Some((e.id :: e.eventTypeId :: e.brandId :: e.title ::
      e.language.spoken :: e.language.secondSpoken :: e.language.materials ::
      e.location.city :: e.location.countryCode :: e.details.description ::
      e.details.specialAttention :: e.organizer.id ::
      e.organizer.webSite :: e.organizer.registrationPage ::
      e.schedule.start :: e.schedule.end :: e.schedule.hoursPerDay ::
      e.schedule.totalHours :: e.notPublic :: e.archived :: e.confirmed :: e.free ::
      e.rating :: HNil))

  def forInsert = (eventTypeId, brandId, title, spokenLanguage,
    secondSpokenLanguage, materialsLanguage, city, countryCode, description,
    specialAttention, organizerId, webSite, registrationPage, start, end,
    hoursPerDay, totalHours, notPublic, archived, confirmed, free)

  def forUpdate = (eventTypeId, brandId, title, spokenLanguage,
    secondSpokenLanguage, materialsLanguage, city, countryCode, description,
    specialAttention, organizerId, webSite, registrationPage, start, end,
    hoursPerDay, totalHours, notPublic, archived, confirmed, free)
}
