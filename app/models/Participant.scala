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

import models.database.{ Participants, People, Events, Evaluations }
import org.joda.time.{ DateTime, LocalDate }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

case class Participant(
  id: Option[Long],
  eventId: Long,
  participantId: Long,
  evaluationId: Option[Long]) {

  lazy val event: Option[Event] = Event.find(eventId)
  lazy val participant: Option[Person] = Person.find(participantId)
  lazy val evaluation: Option[Evaluation] = Evaluation.find(evaluationId.getOrElse(0))

  def update: Participant = DB.withSession { implicit session: Session ⇒
    val updateTuple = (evaluationId)
    val updateQuery = Participants.filter(_.id === this.id).map(_.forUpdate)
    updateQuery.update(updateTuple)
    this
  }

  def delete(): Unit = DB.withSession { implicit session: Session ⇒
    Evaluation.findByEventAndPerson(this.participantId, this.eventId).map { value ⇒
      value.delete
    }
    Participants.where(_.id === this.id).mutate(_.delete)
  }
}

case class ParticipantView(person: Person,
  event: Event,
  evaluationId: Option[Long],
  impression: Option[Int],
  status: Option[EvaluationStatus.Value],
  date: Option[DateTime],
  handled: Option[Option[LocalDate]],
  certificate: Option[Option[String]]) {

  override def equals(other: Any): Boolean =
    other match {
      case that: ParticipantView ⇒
        (that canEqual this) &&
          person.id == that.person.id &&
          event.id == that.event.id

      case _ ⇒ false
    }

  def canEqual(other: Any): Boolean =
    other.isInstanceOf[ParticipantView]

  override def hashCode: Int =
    41 * (41 + person.id.get.toInt) + event.id.get.toInt
}

/** This object is used to get data from a form **/
case class ParticipantData(id: Option[Long],
  eventId: Long,
  firstName: String,
  lastName: String,
  birthDate: Option[LocalDate],
  emailAddress: String,
  address: Address,
  created: DateTime = DateTime.now(),
  createdBy: String,
  updated: DateTime,
  updatedBy: String) {

  lazy val event: Option[Event] = Event.find(eventId)
}

object Participant {

  def find(personId: Long, eventId: Long): Option[Participant] = DB.withSession { implicit session: Session ⇒
    Query(Participants).filter(_.personId === personId).filter(_.eventId === eventId).firstOption
  }

  def findByBrand(brandCode: Option[String]): List[ParticipantView] = DB.withSession { implicit session: Session ⇒
    val baseQuery = for {
      (((part, p), e), ev) ← Participants innerJoin People on (_.personId === _.id) innerJoin Events on (_._1.eventId === _.id) leftJoin Evaluations on (_._1._1.evaluationId === _.id)
    } yield (p, e, ev.id.?, ev.question6.?, ev.status.?, ev.created.?, ev.handled.?, ev.certificate.?)

    val brandQuery = brandCode.map { value ⇒
      baseQuery.filter(_._2.brandCode === value)
    }.getOrElse(baseQuery)
    val rawList = brandQuery.mapResult(ParticipantView.tupled).list
    val withEvaluation = rawList.filterNot(obj ⇒ obj.evaluationId.isEmpty).distinct
    val withoutEvaluation = rawList.filter(obj ⇒ obj.evaluationId.isEmpty).
      map(obj ⇒ ParticipantView(obj.person, obj.event, None, None, None, None, None, None))
    withEvaluation.union(withoutEvaluation.distinct)
  }

  def findByEvent(eventId: Long): List[ParticipantView] = DB.withSession { implicit session: Session ⇒
    val baseQuery = for {
      (((part, p), e), ev) ← Participants innerJoin People on (_.personId === _.id) innerJoin Events on (_._1.eventId === _.id) leftJoin Evaluations on (_._1._1.evaluationId === _.id)
    } yield (p, e, ev.id.?, ev.question6.?, ev.status.?, ev.created.?, ev.handled.?, ev.certificate.?)

    val eventQuery = baseQuery.filter(_._2.id === eventId)
    val rawList = eventQuery.mapResult(ParticipantView.tupled).list
    val withEvaluation = rawList.filterNot(obj ⇒ obj.evaluationId.isEmpty).distinct
    val withoutEvaluation = rawList.filter(obj ⇒ obj.evaluationId.isEmpty).
      map(obj ⇒ ParticipantView(obj.person, obj.event, None, None, None, None, None, None))
    withEvaluation.union(withoutEvaluation.distinct)
  }

  def create(data: ParticipantData): Participant = DB.withSession { implicit session: Session ⇒
    val virtual = true
    val active = false
    val person = Person(None, data.firstName, data.lastName, data.emailAddress, data.birthDate,
      Photo(None, None), signature = false, data.address, None, None, PersonRole.NoRole, SocialProfile(personId = 0),
      None, None, virtual, active,
      DateStamp(data.created, data.createdBy, data.updated, data.updatedBy))
    val newPerson = person.insert
    val eventParticipant = Participant(None, data.eventId, newPerson.id.get, None)
    Participant.insert(eventParticipant)
  }

  def insert(participant: Participant): Participant = DB.withSession { implicit session: Session ⇒
    val id = Participants.forInsert.insert(participant)
    participant.copy(id = Some(id))
  }

}
