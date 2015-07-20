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

import models.{ Participant, Evaluation, EvaluationPair }
import models.database.{ Evaluations, People, Participants, Events }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

class EvaluationService {

  private val evaluations = TableQuery[Evaluations]

  /**
   * Inserts evaluation to database and updates all related records
   * @param eval Evaluation
   * @return Return the updated evaluation with id
   */
  def add(eval: Evaluation): Evaluation = DB.withTransaction { implicit session ⇒
    val id = (evaluations returning evaluations.map(_.id)) += eval
    val participant = Participant.find(eval.personId, eval.eventId).get
    participant.copy(evaluationId = Some(id)).update
    eval.copy(id = Some(id))
  }

  /**
   * Returns evaluation with related event if exists; otherwise, None
   * @param id Evaluation id
   * @return
   */
  def find(id: Long): Option[EvaluationPair] = DB.withSession {
    implicit session ⇒
      val query = for {
        x ← evaluations if x.id === id
        y ← TableQuery[Events] if y.id === x.eventId
      } yield (x, y)
      query.firstOption.map(EvaluationPair.tupled)
  }

  /**
   * Returns evaluation if it exists; otherwise, None
   * @param confirmationId Confirmation unique id
   */
  def find(confirmationId: String): Option[Evaluation] = DB.withSession {
    implicit session ⇒
      evaluations.filter(_.confirmationId === confirmationId).firstOption
  }

  /**
   * Returns a list of evaluations for the given events
   * @param eventIds a list of event ids
   */
  def findByEvents(eventIds: List[Long]) = DB.withSession { implicit session ⇒
    if (eventIds.nonEmpty) {
      val baseQuery = for {
        e ← TableQuery[Events] if e.id inSet eventIds
        part ← TableQuery[Participants] if part.eventId === e.id
        p ← TableQuery[People] if p.id === part.personId
        ev ← evaluations if ev.id === part.evaluationId
      } yield (e, p, ev)
      baseQuery.list
    } else {
      List()
    }
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
   * Updates the given evaluation in database
   * @param eval Evaluation
   * @return Returns the given evaluation
   */
  def update(eval: Evaluation): Evaluation = DB.withSession {
    implicit session ⇒
      val updateTuple = (eval.eventId, eval.personId, eval.question1,
        eval.question2, eval.question3, eval.question4, eval.question5,
        eval.question6, eval.question7, eval.question8, eval.status,
        eval.handled, eval.updated, eval.updatedBy)
      evaluations.filter(_.id === eval.id).map(_.forUpdate).update(updateTuple)
      eval
  }

}

object EvaluationService {
  private val instance = new EvaluationService

  def get: EvaluationService = instance
}