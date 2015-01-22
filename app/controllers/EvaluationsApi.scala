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
package controllers

import models._
import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.mvc._
import play.api.data.Form
import play.api.libs.json._

/**
 * Evaluations API
 */
trait EvaluationsApi extends EvaluationsController with ApiAuthentication {

  /** HTML form mapping for creating and editing. */
  def evaluationForm(userName: String, edit: Boolean = false) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "event_id" -> longNumber.verifying(
      "error.event.notExist", (eventId: Long) ⇒ eventService.find(eventId).isDefined),
    "participant_id" -> longNumber.verifying(
      "error.person.notExist", (participantId: Long) ⇒ personService.find(participantId).isDefined),
    "question1" -> nonEmptyText,
    "question2" -> nonEmptyText,
    "question3" -> nonEmptyText,
    "question4" -> nonEmptyText,
    "question5" -> nonEmptyText,
    "question6" -> number(min = 0, max = 10),
    "question7" -> number(min = 0, max = 10),
    "question8" -> nonEmptyText)({
      (id, event_id, participant_id, question1, question2, question3, question4, question5, question6, question7,
      question8) ⇒
        Evaluation(id, event_id, participant_id, question1, question2, question3, question4, question5,
          question6, question7, question8, EvaluationStatus.Pending, None, None, DateTime.now, userName, DateTime.now, userName)
    })({
      (e: Evaluation) ⇒
        Some(e.id, e.eventId, e.personId, e.question1, e.question2, e.question3, e.question4,
          e.question5, e.question6, e.question7, e.question8)
    }))

  /**
   * Create an evaluation through API call
   */
  def create = TokenSecuredActionWithIdentity { (request: Request[AnyContent], identity: LoginIdentity) ⇒
    val person = identity.person
    val form: Form[Evaluation] = evaluationForm(identity.person.fullName).bindFromRequest()(request)

    form.fold(
      formWithErrors ⇒ {
        val json = Json.toJson(APIError.formValidationError(formWithErrors.errors))
        BadRequest(Json.prettyPrint(json))
      },
      evaluation ⇒ {
        if (Evaluation.findByEventAndPerson(evaluation.personId, evaluation.eventId).isDefined) {
          val json = Json.toJson(new APIError(ErrorCode.DuplicateObjectError, "error.evaluation.exist"))
          BadRequest(Json.prettyPrint(json))
        } else if (eventService.find(evaluation.eventId).get.participants.find(_.id.get == evaluation.personId).isEmpty) {
          val json = Json.toJson(new APIError(ErrorCode.ObjectNotExistError, "error.participant.notExist"))
          BadRequest(Json.prettyPrint(json))
        } else {
          val createdEvaluation = evaluation.create

          Activity.insert(person.fullName, Activity.Predicate.Created, "new evaluation")
          Ok(Json.obj("evaluation_id" -> createdEvaluation.id.get))
        }
      })
  }
}

object EvaluationsApi extends EvaluationsApi with ApiAuthentication
