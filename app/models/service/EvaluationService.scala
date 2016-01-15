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

import models._
import models.database.EvaluationTable
import models.database.event.AttendeeTable
import models.event.AttendeeView
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import scala.concurrent.Future

class EvaluationService extends HasDatabaseConfig[JdbcProfile]
  with AttendeeTable
  with EvaluationTable
  with Services {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._
  private val evaluations = TableQuery[Evaluations]

  /**
   * Inserts evaluation to database and updates all related records
   * @param eval Evaluation
   * @return Return the updated evaluation with id
   */
  def add(eval: Evaluation): Future[Evaluation] = {
    val query = evaluations returning evaluations.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    val actions = (for {
      evaluation <- query += eval
      _ <- attendeeService.updateEvaluationIdQuery(evaluation.attendeeId, evaluation.id)
    } yield evaluation).transactionally
    db.run(actions)
  }

  /**
    * Deletes the given evaluation from database
    * @param evaluation Evaluation
    */
  def delete(evaluation: Evaluation): Unit = {
    val actions = (for {
      _ <- attendeeService.updateEvaluationIdQuery(evaluation.attendeeId, None)
      _ <- evaluations.filter(_.id === evaluation.id).delete
    } yield ()).transactionally
    db.run(actions)
  }

  /**
   * Returns the requested evaluation
   * @param id Evaluation id
   */
  def find(id: Long) = db.run(evaluations.filter(_.id === id).result).map(_.headOption)

  /**
    * Returns evaluation with the related attendee if exists; otherwise, None
    * @param id Evaluation id
    */
  def findWithAttendee(id: Long): Future[Option[EvaluationAttendeeView]] = {
    val query = for {
      x ← evaluations if x.id === id
      y ← TableQuery[Attendees] if y.id === x.attendeeId
    } yield (x, y)
    db.run(query.result).map(_.headOption.map(EvaluationAttendeeView.tupled))
  }

  /**
   * Returns evaluation with related event if exists; otherwise, None
   * @param id Evaluation id
   * @return
   */
  def findWithEvent(id: Long): Future[Option[EvaluationEventView]] = {
    val query = for {
      x ← evaluations if x.id === id
      y ← TableQuery[Events] if y.id === x.eventId
    } yield (x, y)
    db.run(query.result).map(_.headOption.map(EvaluationEventView.tupled))
  }

  /**
    * Returns evaluation for the given attendee if exists
    * @param attendeeId Attendee identifier
    */
  def findByAttendee(attendeeId: Long): Future[Option[Evaluation]] = {
    db.run(evaluations.filter(_.attendeeId === attendeeId).result).map(_.headOption)
  }

  /**
   * Returns evaluation if it exists; otherwise, None
   * @param confirmationId Confirmation unique id
   */
  def findByConfirmationId(confirmationId: String): Future[Option[Evaluation]] = {
    db.run(evaluations.filter(_.confirmationId === confirmationId).result).map(_.headOption)
  }

  /**
   * Returns list of evaluation for the given event
   * @param eventId Event id
   */
  def findByEvent(eventId: Long): Future[List[Evaluation]] = {
    db.run(evaluations.filter(_.eventId === eventId).result).map(_.toList)
  }
  
  /**
   * Returns a list of evaluations for the given events
   * @param eventIds a list of event ids
   */
  def findByEvents(eventIds: List[Long]): Future[List[Evaluation]] = {
    if (eventIds.nonEmpty) {
      val query = for {
        e ← TableQuery[Events] if e.id inSet eventIds
        a ← TableQuery[Attendees] if a.eventId === e.id
        ev ← evaluations if ev.id === a.evaluationId
      } yield ev
      db.run(query.result).map(_.toList)
    } else {
      Future.successful(List())
    }
  }
  
  /**
   * Returns a list of evaluations for the given events
   * @param eventIds a list of event ids
   */
  def findByEventsWithAttendees(eventIds: List[Long]) = {
    if (eventIds.nonEmpty) {
      val query = for {
        e ← TableQuery[Events] if e.id inSet eventIds
        a ← TableQuery[Attendees] if a.eventId === e.id
        ev ← evaluations if ev.id === a.evaluationId
      } yield (e, a, ev)
      db.run(query.result).map(_.toList)
    } else {
      Future.successful(List())
    }
  }

  /**
    * Returns participants and their evaluations for a set of events
    *
    * @param events Event identifiers
    */
  def findEvaluationsByEvents(events: List[Long]): Future[List[AttendeeView]] = {

    val baseQuery = for {
      ((part, e), ev) ← TableQuery[Attendees] join
        TableQuery[Events] on (_.eventId === _.id) joinLeft
        TableQuery[Evaluations] on (_._1.evaluationId === _.id)
    } yield (part, e, ev)

    val eventQuery = baseQuery.filter(_._2.id inSet events)
    db.run(eventQuery.result).map(_.toList.map(AttendeeView.tupled)).flatMap { list =>
      val withEvaluation = list.filterNot(obj ⇒ obj.evaluation.isEmpty).distinct
      val withoutEvaluation = list.filter(obj ⇒ obj.evaluation.isEmpty).
        map(obj ⇒ AttendeeView(obj.attendee, obj.event, None))
      Future.successful(withEvaluation.union(withoutEvaluation.distinct))
    }
  }

  /**
   * Returns list of unhandled (Pending, Unconfirmed) evaluations for the given
   *  events
   * @param events List of events
   */
  def findUnhandled(events: List[Event]) = {
    if (events.nonEmpty) {
      val query = for {
        e ← TableQuery[Events] if e.id inSet events.map(_.identifier)
        a ← TableQuery[Attendees] if a.eventId === e.id
        ev ← evaluations if ev.id === a.evaluationId && (ev.status === EvaluationStatus.Unconfirmed || ev.status === EvaluationStatus.Pending)
      } yield (e, a, ev)
      db.run(query.result).map(_.toList)
    } else {
      Future.successful(List())
    }
  }

  /**
   * Updates the given evaluation in database
   * @param eval Evaluation
   * @return Returns the given evaluation
   */
  def update(eval: Evaluation): Future[Evaluation] = {
    val updateTuple = (eval.eventId, eval.attendeeId, eval.reasonToRegister,
      eval.actionItems, eval.changesToContent, eval.facilitatorReview, eval.changesToHost,
      eval.facilitatorImpression, eval.recommendationScore, eval.changesToEvent,
      eval.contentImpression, eval.hostImpression, eval.status,
      eval.handled, eval.recordInfo.updated, eval.recordInfo.updatedBy)
    db.run(evaluations.filter(_.id === eval.id).map(_.forUpdate).update(updateTuple))
  }

}

object EvaluationService {
  private val instance = new EvaluationService

  def get: EvaluationService = instance
}