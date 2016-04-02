/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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

package controllers.cm

import javax.inject.{Inject, Named}

import akka.actor.ActorRef
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.{Activities, BrandAware, Security, Utilities}
import models.UserRole._
import models._
import models.cm._
import models.cm.facilitator.Endorsement
import models.repository.Repositories
import org.joda.time._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import services.TellerRuntimeEnvironment
import services.integrations.{EmailComponent, Integrations}

import scala.concurrent.Future

class Evaluations @Inject() (override implicit val env: TellerRuntimeEnvironment,
                             override val messagesApi: MessagesApi,
                             val email: EmailComponent,
                             val repos: Repositories,
                             @Named("evaluation-mailer") mailer: ActorRef,
                             @Named("event-rating") eventActor: ActorRef,
                             @Named("facilitator-rating") facilitatorActor: ActorRef,
                             deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env)
  with I18nSupport
  with Integrations
  with Activities
  with BrandAware {

  /** HTML form mapping for creating and editing. */
  def evaluationForm(userName: String) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "eventId" -> ignored(0L),
    "attendeeId" -> ignored(0L),
    "reasonToRegister" -> nonEmptyText,
    "actionItems" -> nonEmptyText,
    "changesToContent" -> nonEmptyText,
    "facilitatorReview" -> nonEmptyText,
    "changesToHost" -> nonEmptyText,
    "facilitatorImpression" -> number(min = 0, max = 10),
    "recommendationScore" -> number(min = 0, max = 10),
    "changesToEvent" -> nonEmptyText,
    "contentImpression" -> optional(number),
    "hostImpression" -> optional(number),
    "status" -> ignored(EvaluationStatus.Pending),
    "handled" -> optional(jodaLocalDate),
    "validationId" -> optional(ignored("")),
    "recordInfo" -> mapping(
      "created" -> ignored(DateTime.now()),
      "createdBy" -> ignored(userName),
      "updated" -> ignored(DateTime.now()),
      "updatedBy" -> ignored(userName))(DateStamp.apply)(DateStamp.unapply))(
      Evaluation.apply)(Evaluation.unapply))

  /**
   * Renders evaluation add form
   *
   * @param eventId Event identifier to create evaluation for
   * @param attendeeId Attendee identifier to create evaluation for
   */
  def add(eventId: Long, attendeeId: Long) = EventAction(List(Role.Coordinator), eventId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      repos.cm.rep.event.attendee.find(attendeeId, eventId) flatMap {
        case None => redirect(controllers.cm.routes.Events.details(eventId), "error" -> "Unknown attendee")
        case Some(attendee) =>
          ok(views.html.v2.evaluation.form(user, evaluationForm(user.name), attendee))
      }
  }

  /**
    * Approve form submits to this action
   *
   * @param id Evaluation identifier
   */
  def approve(id: Long) = EvaluationAction(List(Role.Facilitator, Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      repos.cm.evaluation.findWithAttendee(id) flatMap {
        case None => notFound("")
        case Some(view) =>
          if (view.evaluation.approvable) {
            view.evaluation.approve(repos) flatMap { evaluation =>
              // recalculate ratings
              eventActor ! evaluation.eventId
              facilitatorActor ! evaluation.eventId
              mailer ! ("approve", user.person, evaluation, view.attendee, event)

              jsonOk(Json.obj("date" -> evaluation.handled))
            }
          } else {
            val error = view.evaluation.status match {
              case EvaluationStatus.Unconfirmed ⇒ "error.evaluation.approve.unconfirmed"
              case _ ⇒ "error.evaluation.approve.approved"
            }
            jsonBadRequest(Messages(error))
          }
      }
  }

  /**
   * Add form submits to this action
    *
    * @param eventId Event identifier to create evaluation for
   * @param attendeeId Attendee identifier to create evaluation for
   */
  def create(eventId: Long, attendeeId: Long) = EventAction(List(Role.Coordinator), eventId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      repos.cm.rep.event.attendee.find(attendeeId, eventId) flatMap {
        case None => redirect(controllers.cm.routes.Events.details(eventId), "error" -> "Unknown attendee")
        case Some(attendee) =>
          val form: Form[Evaluation] = evaluationForm(user.name).bindFromRequest
          form.fold(
            errors ⇒ badRequest(views.html.v2.evaluation.form(user, errors, attendee)),
            evaluation ⇒ {
              val modified = evaluation.copy(eventId = eventId, attendeeId = attendeeId)
              modified.add(false, repos, mailer) flatMap { eval =>
                redirect(controllers.cm.routes.Events.details(eventId), "success" -> "Attendee was added")
              }
            })
      }
  }

  /**
   * Delete an evaluation
    *
    * @param id Unique evaluation identifier
   */
  def delete(id: Long) = EvaluationAction(List(Role.Facilitator, Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      repos.cm.evaluation.find(id) flatMap {
        case None => notFound("")
        case Some(evaluation) =>
          repos.cm.evaluation.delete(evaluation) flatMap { _ =>
            // recalculate ratings
            eventActor ! evaluation.eventId
            facilitatorActor ! evaluation.eventId
            val msg = "Evaluation was successfully deleted"
            redirect(controllers.cm.routes.Events.details(evaluation.eventId), "success" -> msg)
          }
      }
  }

  /**
   * Renders an evaluation page
   *
   * @param id Unique evaluation identifier
   */
  def details(id: Long) = RestrictedAction(List(Role.Coordinator, Role.Facilitator)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      repos.cm.evaluation.findWithEvent(id) flatMap {
        case None => notFound("")
        case Some(x) =>
          (for {
            attendee <- repos.cm.rep.event.attendee.find(x.eval.attendeeId, x.eval.eventId)
            endorsementData <- endorsementPair(x, user.person.identifier, id)
          } yield (attendee, endorsementData)) flatMap {
            case (None, _) => notFound("Attendee not found")
            case (Some(attendee), (facilitator, endorsement)) =>
              roleDiffirentiator(user.account, Some(x.event.brandId)) { (view, brands) =>
                ok(views.html.v2.evaluation.details(user, view.brand, brands, x,
                  attendee.fullName,
                  view.settings.certificates,
                  facilitator,
                  endorsement))
              } { (view, brands) =>
                ok(views.html.v2.evaluation.details(user, view.get.brand, brands, x,
                  attendee.fullName,
                  view.get.settings.certificates,
                  facilitator,
                  endorsement))
              } { redirect(controllers.core.routes.Dashboard.index()) }
          }
      }
  }

  /**
   * Reject form submits to this action
   *
   * @param id Evaluation identifier
   */
  def reject(id: Long) = EvaluationAction(List(Role.Facilitator, Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      repos.cm.evaluation.findWithAttendee(id) flatMap {
        case None => notFound("")
        case Some(view) =>
          if (view.evaluation.rejectable) {
            view.evaluation.reject(repos) flatMap { evaluation =>
              // recalculate ratings
              eventActor ! view.evaluation.eventId
              facilitatorActor ! view.evaluation.eventId
              mailer ! ("reject", user.person, view.attendee, event)

              jsonOk(Json.obj("date" -> evaluation.handled))
            }
          } else {
            val error = view.evaluation.status match {
              case EvaluationStatus.Unconfirmed ⇒ "error.evaluation.reject.unconfirmed"
              case _ ⇒ "error.evaluation.reject.rejected"
            }
            jsonBadRequest(Messages(error))
          }
      }
  }

  /**
   * Sends a request to a participant to confirm the evaluation
    *
    * @param id Evaluation id
   */
  def sendConfirmationRequest(id: Long) = EvaluationAction(List(Role.Facilitator, Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      repos.cm.evaluation.find(id) flatMap {
        case None => jsonNotFound("Evaluation not found")
        case Some(evaluation) =>
          mailer ! ("confirm", evaluation)
          jsonSuccess("Confirmation request was sent")
      }
  }

  /**
    * Returns endorsement and related flag if the given person is a facilitator
    *
    * @param view Evaluation view
    * @param personId Person identifier
    * @param id Evaluation identifier
    * @return
    */
  protected def endorsementPair(view: EvaluationEventView, personId: Long, id: Long): Future[(Boolean, Option[Endorsement])] = {
    if (view.event.facilitatorIds(repos).contains(personId))
      repos.person.findEndorsementByEvaluation(id, personId) map { endorsement => (true, endorsement) }
    else
      Future.successful((false, None))
  }


}

object Evaluations {

  def confirmationUrl(token: String): String =
    Utilities.fullUrl(controllers.cm.routes.PublicEvaluations.confirm(token).url)
}
