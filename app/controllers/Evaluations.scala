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

package controllers

import javax.inject.Inject

import models.UserRole._
import models._
import models.event.Attendee
import models.service.{BrandWithCoordinators, Services}
import org.joda.time._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc.Action
import services.TellerRuntimeEnvironment
import services.integrations.Integrations

import scala.concurrent.Future

class Evaluations @Inject() (override implicit val env: TellerRuntimeEnvironment)
    extends JsonController
    with Security
    with Integrations
    with Services
    with Activities
    with Utilities {

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
  def add(eventId: Long, attendeeId: Long) = AsyncSecuredEventAction(List(Role.Coordinator), eventId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      attendeeService.find(attendeeId, eventId) map { attendee =>
        Future.successful(Ok(views.html.v2.evaluation.form(user, evaluationForm(user.name), attendee)))
      } getOrElse Future.successful(
        Redirect(routes.Events.details(eventId)).flashing("error" -> "Unknown attendee")
      )
  }

  /**
   * Approve form submits to this action
   *
   * @param id Evaluation identifier
   */
  def approve(id: Long) = SecuredEvaluationAction(List(Role.Facilitator, Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      evaluationService.findWithAttendee(id).map { view ⇒
        if (view.evaluation.approvable) {
          val evaluation = view.evaluation.approve
            // recalculate ratings
            Event.ratingActor ! evaluation.eventId
            Facilitator.ratingActor ! evaluation.eventId

            activity(evaluation, user.person).approved.insert()
            sendApprovalConfirmation(user.person, evaluation, view.attendee, event)

            jsonOk(Json.obj("date" -> evaluation.handled))
          } else {
          val error = view.evaluation.status match {
              case EvaluationStatus.Unconfirmed ⇒ "error.evaluation.approve.unconfirmed"
              case _ ⇒ "error.evaluation.approve.approved"
            }
            jsonBadRequest(Messages(error))
          }
        }.getOrElse(NotFound)
  }

  /**
   * Add form submits to this action
   * @param eventId Event identifier to create evaluation for
   * @param attendeeId Attendee identifier to create evaluation for
   */
  def create(eventId: Long, attendeeId: Long) = AsyncSecuredEventAction(List(Role.Coordinator), eventId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      attendeeService.find(attendeeId, eventId) map { attendee =>
        val form: Form[Evaluation] = evaluationForm(user.name).bindFromRequest
        form.fold(
          errors ⇒ Future.successful(BadRequest(views.html.v2.evaluation.form(user, errors, attendee))),
          evaluation ⇒ {
            val defaultHook = request.host + routes.Evaluations.confirm("").url
            val eval = evaluation.copy(eventId = eventId, attendeeId = attendeeId).add(defaultHook)
            val log = activity(eval, user.person).created.insert()
            Future.successful(Redirect(routes.Events.details(eventId)).flashing("success" -> log.toString))
          })
      } getOrElse Future.successful(
        Redirect(routes.Events.details(eventId)).flashing("error" -> "Unknown attendee")
      )
  }

  /**
   * Delete an evaluation
   * @param id Unique evaluation identifier
   */
  def delete(id: Long) = SecuredEvaluationAction(List(Role.Facilitator, Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      evaluationService.find(id).map { evaluation ⇒
        evaluationService.delete(evaluation)
        // recalculate ratings
        Event.ratingActor ! evaluation.eventId
        Facilitator.ratingActor ! evaluation.eventId
        activity(evaluation, user.person).deleted.insert()
        val msg = "Evaluation was successfully deleted"
        Redirect(routes.Events.details(evaluation.eventId).url).flashing("success" -> msg)
      }.getOrElse(NotFound)
  }

  /**
   * Renders an evaluation page
   *
   * @param id Unique evaluation identifier
   */
  def details(id: Long) = SecuredRestrictedAction(List(Role.Coordinator, Role.Facilitator)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      evaluationService.findWithEvent(id) map { x ⇒
        val attendee = attendeeService.find(x.eval.attendeeId, x.eval.eventId).get
        val personId = user.person.identifier
        val (facilitator, endorsement) = if (x.event.facilitatorIds.contains(personId))
          (true, personService.findEndorsementByEvaluation(id, personId))
        else
          (false, None)
        roleDiffirentiator(user.account, Some(x.event.brandId)) { (view, brands) =>
          Ok(views.html.v2.evaluation.details(user, view.brand, brands, x,
            attendee.fullName,
            view.settings.certificates,
            facilitator,
            endorsement))
        } { (view, brands) =>
          Ok(views.html.v2.evaluation.details(user, view.get.brand, brands, x,
            attendee.fullName,
            view.get.settings.certificates,
            facilitator,
            endorsement))
        } { Redirect(routes.Dashboard.index()) }
      } getOrElse NotFound

  }

  /**
   * Reject form submits to this action
   *
   * @param id Evaluation identifier
   */
  def reject(id: Long) = SecuredEvaluationAction(List(Role.Facilitator, Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      evaluationService.findWithAttendee(id).map { view ⇒
        if (view.evaluation.rejectable) {
          val evaluation = view.evaluation.reject()

          // recalculate ratings
          Event.ratingActor ! view.evaluation.eventId
          Facilitator.ratingActor ! view.evaluation.eventId

          val log = activity(view.evaluation, user.person).rejected.insert()
          sendRejectionConfirmation(user.person, view.attendee, view.evaluation.event)

          jsonOk(Json.obj("date" -> evaluation.handled))
        } else {
          val error = view.evaluation.status match {
            case EvaluationStatus.Unconfirmed ⇒ "error.evaluation.reject.unconfirmed"
            case _ ⇒ "error.evaluation.reject.rejected"
          }
          jsonBadRequest(Messages(error))
        }
      }.getOrElse(NotFound)
  }

  /**
   * Sends a request to a participant to confirm the evaluation
   * @param id Evaluation id
   */
  def sendConfirmationRequest(id: Long) = SecuredEvaluationAction(List(Role.Facilitator, Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      evaluationService.find(id) map { evaluation ⇒
        val defaultHook = request.host + routes.Evaluations.confirm("").url
        evaluation.sendConfirmationRequest(defaultHook)
        jsonSuccess("Confirmation request was sent")
      } getOrElse jsonNotFound("Evaluation not found")
  }

  /**
   * Confirms the given evaluation
   * @param confirmationId Confirmation unique id
   */
  def confirm(confirmationId: String) = Action { implicit request ⇒
    evaluationService.findByConfirmationId(confirmationId) map { x ⇒
      x.confirm()
      Ok(views.html.evaluation.confirmed())
    } getOrElse NotFound(views.html.evaluation.notfound())
  }

  /**
   * Sends confirmation email that evaluation was approved
   * @param approver Person who approved the given evaluation
   * @param evaluation Evaluation
   * @param attendee Attendee
   * @param event Related event
   */
  protected def sendApprovalConfirmation(approver: Person, evaluation: Evaluation, attendee: Attendee, event: Event) = {
    brandService.findWithSettings(event.brandId) foreach { withSettings =>
      val coordinators = brandService.coordinators(event.brandId)
      val bcc = coordinators.filter(_._2.notification.evaluation).map(_._1)
      if (attendee.certificate.isEmpty && withSettings.settings.certificates && !event.free) {
        val cert = new Certificate(evaluation.handled, event, attendee)
        cert.generateAndSend(BrandWithCoordinators(withSettings.brand, coordinators), approver)
        attendeeService.updateCertificate(attendee.copy(certificate = Some(cert.id), issued = cert.issued))
      } else if (attendee.certificate.isEmpty) {
        val body = mail.templates.evaluation.html.approvedNoCert(withSettings.brand, attendee, approver).toString()
        val subject = s"Your ${withSettings.brand.name} event's evaluation approval"
        email.send(Set(attendee),
          Some(event.facilitators.toSet),
          Some(bcc.toSet),
          subject, body, from = withSettings.brand.name, richMessage = true, None)
      } else {
        val cert = new Certificate(evaluation.handled, event, attendee, renew = true)
        cert.send(BrandWithCoordinators(withSettings.brand, coordinators), approver)
      }
    }
  }

  /**
   * Sends confirmation email that evaluation was rejected
   * @param rejector Person who rejected the evaluation
   * @param attendee Attendee
   * @param event Related event
   */
  protected def sendRejectionConfirmation(rejector: Person, attendee: Attendee, event: Event) = {
    brandService.findWithCoordinators(event.brandId) foreach { x ⇒
      val bcc = x.coordinators.filter(_._2.notification.evaluation).map(_._1)
      val subject = s"Your ${x.brand.name} certificate"
      email.send(Set(attendee),
        Some(event.facilitators.toSet),
        Some(bcc.toSet), subject,
        mail.templates.evaluation.html.rejected(x.brand, attendee, rejector).toString(),
        richMessage = true)
    }
  }
}
