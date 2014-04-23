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

package models

import models.database.EventParticipants
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

case class EventParticipant(id: Option[Long], eventId: Long, participantId: Long) {

  lazy val event: Option[Event] = Event.find(eventId)
  lazy val participant: Option[Person] = Person.find(participantId)
}

object EventParticipant {

  def insert(participant: EventParticipant): EventParticipant = DB.withSession { implicit session: Session ⇒
    val id = EventParticipants.forInsert.insert(participant)
    participant.copy(id = Some(id))
  }

  def delete(id: Long): Unit = DB.withSession { implicit session: Session ⇒
    EventParticipants.where(_.id === id).mutate(_.delete())
  }
}

