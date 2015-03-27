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

  /**
   * Inserts evaluation to database and updates all related records
   * @param eval Evaluation
   * @return Return the updated evaluation with id
   */
  def add(eval: Evaluation): Evaluation = DB.withTransaction { implicit session ⇒
    val id = Evaluations.forInsert.insert(eval)
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
        x ← Evaluations if x.id === id
        y ← Events if y.id === x.eventId
      } yield (x, y)
      query.firstOption.map(EvaluationPair.tupled)
  }

  /**
   * Returns a list of evaluations for the given events
   * @param eventIds a list of event ids
   */
  def findByEvents(eventIds: List[Long]) = DB.withSession { implicit session: Session ⇒
    if (eventIds.length > 0) {
      val baseQuery = for {
        e ← Events if e.id inSet eventIds
        part ← Participants if part.eventId === e.id
        p ← People if p.id === part.personId
        ev ← Evaluations if ev.id === part.evaluationId
      } yield (e, p, ev)
      baseQuery.list
    } else {
      List()
    }
  }

}

object EvaluationService {
  private val instance = new EvaluationService

  def get: EvaluationService = instance
}