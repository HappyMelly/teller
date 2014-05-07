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

import models.database.{ EventParticipants, People, Events, Evaluations }
import play.api.db.slick.Config.driver.simple._
import org.joda.time.{ DateTime, LocalDate }
import play.api.db.slick.DB
import play.api.Play.current

case class EventParticipant(id: Option[Long], eventId: Long, participantId: Long) {
  lazy val event: Option[Event] = Event.find(eventId)
  lazy val participant: Option[Person] = Person.find(participantId)
}

case class ParticipantView(person: Person,
  event: Event,
  evaluationId: Option[Long],
  secondEvaluationId: Option[Long],
  impression: Option[Int],
  status: Option[EvaluationStatus.Value],
  date: Option[DateTime],
  handled: Option[Option[LocalDate]],
  certificate: Option[Option[String]])

object EventParticipant {

  def findAll(brandCode: Option[String]): List[ParticipantView] = DB.withSession { implicit session: Session ⇒
    val baseQuery = for {
      ((((part, p), e), ev), ev2) ← EventParticipants innerJoin People on (_.personId === _.id) innerJoin Events on (_._1.eventId === _.id) leftJoin Evaluations on (_._1._1.eventId === _.eventId) leftJoin Evaluations on (_._1._1._1.personId === _.participantId)
    } yield (p, e, ev.id.?, ev2.id.?, ev.question6.?, ev.status.?, ev.created.?, ev.handled.?, ev.certificate.?)

    val brandQuery = brandCode.map { value ⇒
      baseQuery.filter(_._2.brandCode === value)
    }.getOrElse(baseQuery)
    val rawList = brandQuery.mapResult(ParticipantView.tupled).list
    val withEvaluation = rawList.filter(obj ⇒ obj.evaluationId == obj.secondEvaluationId)
    val withoutEvaluation = rawList.filter(obj ⇒ obj.evaluationId.isEmpty || obj.secondEvaluationId.isEmpty).
      map(obj ⇒ ParticipantView(obj.person, obj.event, None, None, None, None, None, None, None))
    withEvaluation.union(withoutEvaluation)
  }

  def insert(participant: EventParticipant): EventParticipant = DB.withSession { implicit session: Session ⇒
    val id = EventParticipants.forInsert.insert(participant)
    participant.copy(id = Some(id))
  }

  def delete(id: Long): Unit = DB.withSession { implicit session: Session ⇒
    EventParticipants.where(_.id === id).mutate(_.delete())
  }
}

