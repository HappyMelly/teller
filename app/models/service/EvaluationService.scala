/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing
 * Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models.service

import models.database.event.Attendees
import models.database.{Evaluations, Events, Participants, People}
import models._
import models.event.AttendeeView
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

class EvaluationService extends Services {

  private val evaluations = TableQuery[Evaluations]

  /**
   * Inserts evaluation to database and updates all related records
   * @param eval Evaluation
   * @return Return the updated evaluation with id
   */
  def add(eval: Evaluation): Evaluation = DB.withTransaction { implicit session ⇒
    val id = (evaluations returning evaluations.map(_.id)) += eval
    attendeeService._updateEvaluation(eval.attendeeId, Some(id))
    eval.copy(id = Some(id))
  }

  /**
    * Deletes the given evaluation from database
    * @param evaluation Evaluation
    */
  def delete(evaluation: Evaluation): Unit = DB.withTransaction { implicit session =>
    attendeeService._updateEvaluation(evaluation.attendeeId, None)
    evaluations.filter(_.id === evaluation.id).delete
  }

  /**
   * Returns the requested evaluation
   * @param id Evaluation id
   */
  def find(id: Long) = DB.withSession { implicit session ⇒
    TableQuery[Evaluations].filter(_.id === id).firstOption
  }

  /**
    * Returns evaluation with the related attendee if exists; otherwise, None
    * @param id Evaluation id
    */
  def findWithAttendee(id: Long): Option[EvaluationAttendeeView] = DB.withSession {
    implicit session ⇒
      val query = for {
        x ← evaluations if x.id === id
        y ← TableQuery[Attendees] if y.id === x.attendeeId
      } yield (x, y)
      query.firstOption.map(EvaluationAttendeeView.tupled)
  }

  /**
   * Returns evaluation with related event if exists; otherwise, None
   * @param id Evaluation id
   * @return
   */
  def findWithEvent(id: Long): Option[EvaluationEventView] = DB.withSession {
    implicit session ⇒
      val query = for {
        x ← evaluations if x.id === id
        y ← TableQuery[Events] if y.id === x.eventId
      } yield (x, y)
      query.firstOption.map(EvaluationEventView.tupled)
  }

  /**
    * Returns evaluation for the given attendee if exists
    * @param attendeeId Attendee identifier
    */
  def findByAttendee(attendeeId: Long): Option[Evaluation] = DB.withSession { implicit session =>
    evaluations.filter(_.attendeeId === attendeeId).firstOption
  }

  /**
   * Returns evaluation if it exists; otherwise, None
   * @param confirmationId Confirmation unique id
   */
  def findByConfirmationId(confirmationId: String): Option[Evaluation] = DB.withSession {
    implicit session ⇒
      evaluations.filter(_.confirmationId === confirmationId).firstOption
  }

  /**
   * Returns list of evaluation for the given event
   * @param eventId Event id
   */
  def findByEvent(eventId: Long): List[Evaluation] = DB.withSession {
    implicit session ⇒
      evaluations.filter(_.eventId === eventId).list
  }
  
  /**
   * Returns a list of evaluations for the given events
   * @param eventIds a list of event ids
   */
  def findByEvents(eventIds: List[Long]): List[Evaluation] = DB.withSession { implicit session ⇒
    if (eventIds.nonEmpty) {
      val baseQuery = for {
        e ← TableQuery[Events] if e.id inSet eventIds
        part ← TableQuery[Participants] if part.eventId === e.id
        ev ← evaluations if ev.id === part.evaluationId
      } yield ev
      baseQuery.list
    } else {
      List()
    }
  }
  
  /**
   * Returns a list of evaluations for the given events
   * @param eventIds a list of event ids
   */
  def findByEventsWithAttendees(eventIds: List[Long]) = DB.withSession { implicit session ⇒
    if (eventIds.nonEmpty) {
      val baseQuery = for {
        e ← TableQuery[Events] if e.id inSet eventIds
        a ← TableQuery[Attendees] if a.eventId === e.id
        ev ← evaluations if ev.id === a.evaluationId
      } yield (e, a, ev)
      baseQuery.list
    } else {
      List()
    }
  }

  /**
    * Returns participants and their evaluations for a set of events
    *
    * @param events Event identifiers
    */
  def findEvaluationsByEvents(events: List[Long]): List[AttendeeView] = DB.withSession { implicit session ⇒
    import models.database.PortableJodaSupport._
    import models.database.Evaluations.evaluationStatusTypeMapper

    val baseQuery = for {
      ((part, e), ev) ← TableQuery[Attendees] innerJoin
        TableQuery[Events] on (_.eventId === _.id) leftJoin
        TableQuery[Evaluations] on (_._1.evaluationId === _.id)
    } yield (part, e, ev.id.?, ev.facilitatorImpression.?, ev.status.?, ev.created.?, ev.handled, ev.confirmationId)

    val eventQuery = baseQuery.filter(_._2.id inSet events)
    val rawList = eventQuery.mapResult(AttendeeView.tupled).list
    val withEvaluation = rawList.filterNot(obj ⇒ obj.evaluationId.isEmpty).distinct
    val withoutEvaluation = rawList.filter(obj ⇒ obj.evaluationId.isEmpty).
      map(obj ⇒ AttendeeView(obj.attendee, obj.event, None, None, None, None, None, None))
    withEvaluation.union(withoutEvaluation.distinct)
  }

  /**
   * Returns list of unhandled (Pending, Unconfirmed) evaluations for the given
   *  events
   * @param events List of events
   */
  def findUnhandled(events: List[Event]) = DB.withSession {
    implicit session =>
      import models.database.Evaluations._

      if (events.nonEmpty) {
        val baseQuery = for {
          e ← TableQuery[Events] if e.id inSet events.map(_.identifier)
          part ← TableQuery[Participants] if part.eventId === e.id
          p ← TableQuery[People] if p.id === part.personId
          ev ← evaluations if ev.id === part.evaluationId && (ev.status === EvaluationStatus.Unconfirmed || ev.status === EvaluationStatus.Pending)
        } yield (e, p, ev)
        baseQuery.list
      } else {
        List()
      }
  }

  /**
   * Updates the given evaluation in database
   * @param eval Evaluation
   * @return Returns the given evaluation
   */
  def update(eval: Evaluation): Evaluation = DB.withSession { implicit session ⇒
    val updateTuple = (eval.eventId, eval.attendeeId, eval.reasonToRegister,
      eval.actionItems, eval.changesToContent, eval.facilitatorReview, eval.changesToHost,
      eval.facilitatorImpression, eval.recommendationScore, eval.changesToEvent,
      eval.contentImpression, eval.hostImpression, eval.status,
      eval.handled, eval.recordInfo.updated, eval.recordInfo.updatedBy)
    evaluations.filter(_.id === eval.id).map(_.forUpdate).update(updateTuple)
    eval
  }

}

object EvaluationService {
  private val instance = new EvaluationService

  def get: EvaluationService = instance
}