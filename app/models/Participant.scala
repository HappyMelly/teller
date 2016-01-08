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

import models.database.PortableJodaSupport._
import models.database.{ Participants, People, Events, Evaluations }
import models.database.Evaluations.evaluationStatusTypeMapper
import models.service.{EvaluationService, PersonService, EventService}
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
    certificate: Option[String],
    issued: Option[LocalDate],
    organisation: Option[String],
    comment: Option[String],
    role: Option[String]) {

  lazy val event: Option[Event] = EventService.get.find(eventId)
  lazy val person: Option[Person] = PersonService.get.find(personId)
  lazy val evaluation: Option[Evaluation] = EvaluationService.get.find(evaluationId.getOrElse(0))

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
 * @param confirmationToken Token used in a confirmation url to identify an evaluation
 */
case class ParticipantView(person: Person,
    event: Event,
    evaluationId: Option[Long],
    impression: Option[Int],
    status: Option[EvaluationStatus.Value],
    date: Option[DateTime],
    handled: Option[LocalDate],
    certificate: Option[String],
    confirmationToken: Option[String]) {

  override def equals(other: Any): Boolean =
    other match {
      case that: ParticipantView ⇒
        (that canEqual this) &&
          person.id == that.person.id &&
          event.id == that.event.id

      case _ ⇒ false
    }

  def canEqual(other: Any): Boolean = other.isInstanceOf[ParticipantView]

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
    role: Option[String],
    created: DateTime = DateTime.now(),
    createdBy: String,
    updated: DateTime,
    updatedBy: String) {

  lazy val event: Option[Event] = EventService.get.find(eventId)
}

object Participant {


  /**
   * Returns participants and their evaluations for a set of events
   *
   * @param events Event identifiers
   */
  def findEvaluationsByEvents(events: List[Long]): List[ParticipantView] = DB.withSession {
    implicit session ⇒
      val baseQuery = for {
        (((part, p), e), ev) ← TableQuery[Participants] innerJoin
          TableQuery[People] on (_.personId === _.id) innerJoin
          TableQuery[Events] on (_._1.eventId === _.id) leftJoin
          TableQuery[Evaluations] on (_._1._1.evaluationId === _.id)
      } yield (p, e, ev.id.?, ev.facilitatorImpression.?, ev.status.?, ev.created.?, ev.handled, part.certificate, ev.confirmationId)

      val eventQuery = baseQuery.filter(_._2.id inSet events)
      val rawList = eventQuery.mapResult(ParticipantView.tupled).list
      val withEvaluation = rawList.filterNot(obj ⇒ obj.evaluationId.isEmpty).distinct
      val withoutEvaluation = rawList.filter(obj ⇒ obj.evaluationId.isEmpty).
        map(obj ⇒ ParticipantView(obj.person, obj.event, None, None, None, None, None, obj.certificate, None))
      withEvaluation.union(withoutEvaluation.distinct)
  }

}
