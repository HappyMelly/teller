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

/**
 * This class represents a participant in the system. Participant is a person which took part in any event.
 * A participant could evaluate the event.
 *
 * @param id Unique record identifier
 * @param eventId Event identifier
 * @param personId Person identifier
 * @param evaluationId Evaluation identifier (if the participant filled in an evaluation)
 * @param organisation Organization name, used in email notification only. Not available in HM Teller UI
 * @param comment Comment, used in email notification only. Not available in HM Teller UI
 */
case class Participant(
  id: Option[Long],
  eventId: Long,
  personId: Long,
  evaluationId: Option[Long],
  organisation: Option[String],
  comment: Option[String]) {

  lazy val event: Option[Event] = Event.find(eventId)
  lazy val person: Option[Person] = Person.find(personId)
  lazy val evaluation: Option[Evaluation] = Evaluation.find(evaluationId.getOrElse(0))

  /**
   * A participant is a person that's why only an event and evaluation are updatable
   * @return
   */
  def update: Participant = DB.withSession { implicit session: Session ⇒
    val updateTuple = (eventId, evaluationId)
    val updateQuery = Participants.filter(_.id === this.id).map(_.forUpdate)
    updateQuery.update(updateTuple)
    this
  }

  /**
   * Delete a participant and related evaluation
   */
  def delete(): Unit = DB.withSession { implicit session: Session ⇒
    Evaluation.findByEventAndPerson(this.personId, this.eventId).map { value ⇒
      value.delete()
    }
    Participants.where(_.id === this.id).mutate(_.delete())
  }
}

/**
 * This class represent a row in a table with participants
 * @param person Participant personal data
 * @param event Event info
 * @param evaluationId Evaluation identifier
 * @param impression A level of impression (taken from the evaluation)
 * @param status A status of evaluation (pending, approved or rejected)
 * @param date Date of the evaluation creation
 * @param handled Date when the evaluation was approved/rejected
 * @param certificate A certificate identifier
 */
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
  birthday: Option[LocalDate],
  emailAddress: String,
  address: Address,
  organisation: Option[String],
  comment: Option[String],
  created: DateTime = DateTime.now(),
  createdBy: String,
  updated: DateTime,
  updatedBy: String) {

  lazy val event: Option[Event] = Event.find(eventId)
}

object Participant {

  /**
   * Find if a person took part in an event
   * @param personId Person identifier
   * @param eventId Event identifier
   * @return
   */
  def find(personId: Long, eventId: Long): Option[Participant] = DB.withSession { implicit session: Session ⇒
    Query(Participants).filter(_.personId === personId).filter(_.eventId === eventId).firstOption
  }

  /**
   * Find all participants for all events of the specified brand
   * @param brandCode Brand code
   * @return
   */
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

  /**
   * Find all participants for the specified event
   * @param eventId Event identifier
   * @return
   */
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

  /**
   * Retrieve the participants for a set of events
   * @param eventIds a list of event ids
   * @return
   */
  def findByEvents(eventIds: List[Long]) = DB.withSession { implicit session: Session ⇒
    val baseQuery = for {
      e ← Events if e.id inSet eventIds
      part ← Participants if part.eventId === e.id
      p ← People if p.id === part.personId
    } yield (e, p)
    baseQuery.list
  }

  /**
   * Create a new person and a participant record
   * @param data Data containing records about a person and an event
   * @return
   */
  def create(data: ParticipantData): Participant = DB.withSession { implicit session: Session ⇒
    val virtual = true
    val active = false
    val person = Person(None, data.firstName, data.lastName, data.birthday,
      Photo(None, None), signature = false, data.address, None, None, PersonRole.NoRole,
      SocialProfile(objectId = 0, objectType = ProfileType.Person, email = data.emailAddress), None, None, virtual, active,
      DateStamp(data.created, data.createdBy, data.updated, data.updatedBy))
    val newPerson = person.insert
    val eventParticipant = Participant(None, data.eventId, newPerson.id.get, evaluationId = None, data.organisation,
      data.comment)
    Participant.insert(eventParticipant)
  }

  /**
   * Insert a participant to database
   * @param participant Object to insert
   * @return
   */
  def insert(participant: Participant): Participant = DB.withSession { implicit session: Session ⇒
    val id = Participants.forInsert.insert(participant)
    participant.copy(id = Some(id))
  }

}
