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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers.apiv2

import controllers.EvaluationsController
import models._
import org.joda.time.DateTime
import play.api.Play
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._

/**
 * Evaluations API
 */
trait EvaluationsApi extends EvaluationsController with ApiAuthentication {

  /** HTML form mapping for creating and editing. */
  def evaluationForm(appName: String, edit: Boolean = false) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "event_id" -> longNumber.verifying(
      "error.event.notExist", (eventId: Long) ⇒ eventService.find(eventId).isDefined),
    "participant_id" -> longNumber.verifying(
      "error.person.notExist",
      (participantId: Long) ⇒ personService.find(participantId).isDefined),
    "question1" -> nonEmptyText,
    "question2" -> nonEmptyText,
    "question3" -> nonEmptyText,
    "question4" -> nonEmptyText,
    "question5" -> nonEmptyText,
    "question6" -> number(min = 0, max = 10),
    "question7" -> number(min = 0, max = 10),
    "question8" -> nonEmptyText)({
      (id, event_id, participant_id, question1, question2, question3, question4,
      question5, question6, question7, question8) ⇒
        Evaluation(id, event_id, participant_id, question1, question2, question3,
          question4, question5, question6, question7, question8, None, None,
          EvaluationStatus.Pending, None, None,
          DateStamp(DateTime.now, appName, DateTime.now, appName))
    })({
      (e: Evaluation) ⇒
        Some(e.id, e.eventId, e.personId, e.reasonToRegister, e.actionItems, e.changesToContent,
          e.facilitatorReview, e.changesToHost, e.facilitatorImpression, e.recommendationScore, e.changesToEvent)
    }))

  /** HTML form mapping for creating and editing. */
  def updatedEvaluationForm(appName: String, edit: Boolean = false) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "event_id" -> longNumber.verifying(
      "error.event.notExist", (eventId: Long) ⇒ eventService.find(eventId).isDefined),
    "participant_id" -> longNumber.verifying(
      "error.person.notExist",
      (participantId: Long) ⇒ personService.find(participantId).isDefined),
    "reason_to_register" -> nonEmptyText,
    "action_items" -> nonEmptyText,
    "changes_to_content" -> nonEmptyText,
    "facilitator_review" -> nonEmptyText,
    "changes_to_host" -> nonEmptyText,
    "facilitator_impression" -> number(min = 0, max = 10),
    "recommendation_score" -> number(min = 0, max = 10),
    "changes_to_event" -> nonEmptyText,
    "content_impression" -> optional(number(min = 0, max = 10)),
    "host_impression" -> optional(number(min = 0, max = 10)))({
    (id, event_id, participant_id, reasonToRegister, actionItems, changesToContent,
     facilitatorReview, changesToHost, facilitatorImpression, recommendationScore,
     changesToEvent, contentImpression, hostImpression) ⇒
      Evaluation(id, event_id, participant_id, reasonToRegister, actionItems,
        changesToContent, facilitatorReview, changesToHost, facilitatorImpression,
        recommendationScore, changesToEvent, contentImpression, hostImpression,
        EvaluationStatus.Pending, None, None,
        DateStamp(DateTime.now, appName, DateTime.now, appName))
  })({
    (e: Evaluation) ⇒
      Some(e.id, e.eventId, e.personId, e.reasonToRegister, e.actionItems, e.changesToContent,
        e.facilitatorReview, e.changesToHost, e.facilitatorImpression,
        e.recommendationScore, e.changesToEvent, e.contentImpression,
        e.hostImpression)
  }))

  /**
   * Create an evaluation through API call
   */
  def create = TokenSecuredAction(readWrite = true) { implicit request ⇒
    implicit token ⇒

      val name = token.appName
      val updatedForm = request.body.toString.contains("reason_to_register")
      val form: Form[Evaluation] = if (updatedForm)
        updatedEvaluationForm(name).bindFromRequest()
      else
        evaluationForm(name).bindFromRequest()
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
            val url = request.host + controllers.routes.Evaluations.confirm("").url
            val createdEvaluation = evaluation.add(url, withConfirmation = true)
            val message = "new evaluation for " + createdEvaluation.participant.fullName
            Activity.insert(name, Activity.Predicate.Created, message)
            jsonOk(Json.obj("evaluation_id" -> createdEvaluation.id.get))
          }
        })
  }

  /**
   * Confirms the given evaluation if exists
   * @param confirmationId Confirmation unique id
   */
  def confirm(confirmationId: String) = TokenSecuredAction(readWrite = true) {
    implicit request ⇒
      implicit token ⇒
        evaluationService.find(confirmationId) map { x ⇒
          x.confirm()
          val msg = "participant %s confirmed evaluation %s".format(x.personId, x.eventId)
          Activity.insert(token.appName, Activity.Predicate.Confirmed, msg)

          jsonOk(Json.obj("success" -> "The evaluation is confirmed"))
        } getOrElse jsonNotFound("Unknown evaluation")
  }
}

object EvaluationsApi extends EvaluationsApi with ApiAuthentication
