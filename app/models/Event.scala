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

package models

import org.joda.time.{ LocalDate, DateTime }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import scala.slick.lifted.Query
import models.database.{ EventFacilitator, OrganisationMemberships, Events }

case class Schedule(start: LocalDate, end: LocalDate, hoursPerDay: Int)
case class Location(city: String, countryCode: String)
/**
 * An event such as a Management 3.0 course or a DARE Festival.
 */
case class Event(
  id: Option[Long],
  brandId: Long,
  title: String,
  spokenLanguage: String,
  materialsLanguage: Option[String],
  location: Location,
  description: Option[String],
  specialAttention: Option[String],
  schedule: Schedule,
  webSite: Option[String],
  registrationPage: Option[String],
  isPrivate: Boolean = false,
  isArchived: Boolean = false,
  created: DateTime = DateTime.now(),
  createdBy: String,
  updated: DateTime,
  updatedBy: String) {

  lazy val facilitators: List[Person] = DB.withSession { implicit session: Session ⇒
    val query = for {
      facilitation ← EventFacilitator if facilitation.eventId === this.id
      person ← facilitation.facilitator
    } yield person
    query.sortBy(_.lastName.toLowerCase).list
  }

  def insert: Event = DB.withSession { implicit session: Session ⇒
    val id = Events.forInsert.insert(this)
    this.copy(id = Some(id))
  }

  def delete(): Unit = Event.delete(this.id.get)

  def update: Event = DB.withSession { implicit session: Session ⇒
    val updateTuple = (brandId, title, spokenLanguage, materialsLanguage, location.city, location.countryCode, description, specialAttention,
      schedule.start, schedule.end, schedule.hoursPerDay, webSite, registrationPage, isPrivate, isArchived, updated, updatedBy)
    val updateQuery = Events.filter(_.id === this.id).map(_.forUpdate)
    updateQuery.update(updateTuple)
    this
  }
}

object Event {

  /**
   * Deletes an event.
   */
  def delete(id: Long): Unit = DB.withSession { implicit session: Session ⇒
    Events.where(_.id === id).mutate(_.delete())
  }

  def find(id: Long): Option[Event] = DB.withSession { implicit session: Session ⇒
    Query(Events).filter(_.id === id).list.headOption
  }

  def findAll: List[Event] = DB.withSession { implicit session: Session ⇒
    Query(Events).sortBy(_.title.toLowerCase).list
  }

}

