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
package controllers.api

import javax.inject.Inject

import models._
import models.repository.Repositories
import org.joda.time.DateTime
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.libs.json._
import services.integrations.EmailComponent

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Evaluations API
 */
class EvaluationsApi @Inject() (val services: Repositories,
                                override val messagesApi: MessagesApi,
                                val email: EmailComponent) extends ApiAuthentication(services, messagesApi) {

  /** HTML form mapping for creating and editing. */
  def evaluationForm(appName: String, edit: Boolean = false) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "event_id" -> longNumber,
    "participant_id" -> longNumber,
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
        Some(e.id, e.eventId, e.attendeeId, e.reasonToRegister, e.actionItems, e.changesToContent,
          e.facilitatorReview, e.changesToHost, e.facilitatorImpression, e.recommendationScore, e.changesToEvent)
    }))

  /** HTML form mapping for creating and editing. */
  def updatedEvaluationForm(appName: String, edit: Boolean = false) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "event_id" -> longNumber,
    "participant_id" -> longNumber,
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
      Some(e.id, e.eventId, e.attendeeId, e.reasonToRegister, e.actionItems, e.changesToContent,
        e.facilitatorReview, e.changesToHost, e.facilitatorImpression,
        e.recommendationScore, e.changesToEvent, e.contentImpression,
        e.hostImpression)
  }))

  /**
   * Create an evaluation through API call
   */
  def create = TokenSecuredAction(readWrite = true) { implicit request => implicit token ⇒

    val name = token.appName
    val updatedForm = request.body.toString.contains("reason_to_register")
    val form: Form[Evaluation] = if (updatedForm)
      updatedEvaluationForm(name).bindFromRequest()
    else
      evaluationForm(name).bindFromRequest()
    form.fold(
      formWithErrors ⇒ {
        val json = Json.toJson(APIError.formValidationError(formWithErrors.errors))
        Logger.info(formWithErrors.errors.toString())
        badRequest(Json.prettyPrint(json))
      },
      evaluation ⇒ {
        (for {
          mayBeEvaluation <- services.evaluation.findByAttendee(evaluation.attendeeId)
          attendee <- services.attendee.find(evaluation.attendeeId, evaluation.eventId)
        } yield (mayBeEvaluation, attendee)) flatMap {
          case (Some(existingEvaluation), _) =>
            val json = Json.toJson(new APIError(ErrorCode.DuplicateObjectError, "error.evaluation.exist"))
            Logger.info(s"Evaluation for event ${evaluation.eventId} and person ${evaluation.attendeeId} already exists")
            badRequest(Json.prettyPrint(json))
          case (_, None) =>
            val json = Json.toJson(new APIError(ErrorCode.ObjectNotExistError, "error.participant.notExist"))
            Logger.info(s"Attendee for event ${evaluation.eventId} does not exist")
            badRequest(Json.prettyPrint(json))
          case (_, Some(attendee)) =>
            val url = request.host + controllers.routes.Evaluations.confirm("").url
            evaluation.add(url, withConfirmation = true, email, services) flatMap { createdEvaluation =>
              val message = "new evaluation for " + attendee.fullName
              Activity.insert(name, Activity.Predicate.Created, message)(services)
              jsonOk(Json.obj("evaluation_id" -> createdEvaluation.id.get))
            }
        }
      })
  }

  /**
   * Confirms the given evaluation if exists
    *
    * @param confirmationId Confirmation unique id
   */
  def confirm(confirmationId: String) = TokenSecuredAction(readWrite = true) { implicit request ⇒ implicit token ⇒
    services.evaluation.findByConfirmationId(confirmationId) flatMap {
      case None => jsonNotFound("Unknown evaluation")
      case Some(x) =>
        x.confirm(email, services)
        val msg = "participant %s confirmed evaluation %s".format(x.attendeeId, x.eventId)
        Activity.insert(token.appName, Activity.Predicate.Confirmed, msg)(services)

        jsonOk(Json.obj("success" -> "The evaluation is confirmed"))
    }
  }
}
