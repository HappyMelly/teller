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

import models.UserRole._
import models._
import models.event.Attendee
import models.service.{BrandWithCoordinators, EventService, Services}
import org.joda.time._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc.Action
import securesocial.core.RuntimeEnvironment
import services.integrations.Integrations

class Evaluations(environment: RuntimeEnvironment[ActiveUser])
    extends EvaluationsController
    with Security
    with Integrations
    with Services
    with Activities
    with Utilities {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  /** HTML form mapping for creating and editing. */
  def evaluationForm(userName: String, edit: Boolean = false) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "eventId" -> longNumber.verifying(
      "An event doesn't exist", (eventId: Long) ⇒ EventService.get.find(eventId).isDefined),
    "participantId" -> {
      if (edit) of(participantIdOnEditFormatter) else of(participantIdFormatter)
    },
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
    "status" -> statusMapping,
    "handled" -> optional(jodaLocalDate),
    "validationId" -> optional(ignored("")),
    "recordInfo" -> mapping(
      "created" -> ignored(DateTime.now()),
      "createdBy" -> ignored(userName),
      "updated" -> ignored(DateTime.now()),
      "updatedBy" -> ignored(userName))(DateStamp.apply)(DateStamp.unapply))(
      Evaluation.apply)(Evaluation.unapply))

  /**
   * Show add page
   *
   * @param eventId Optional unique event identifier to create evaluation for
   * @param participantId Optional unique person identifier to create evaluation for
   * @return
   */
  def add(eventId: Option[Long], participantId: Option[Long]) = SecuredRestrictedAction(Role.Coordinator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        val account = user.account
        val events = findEvents(account)
        Ok(views.html.evaluation.form(user, None, evaluationForm(user.name), events, eventId, participantId))
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
            sendApprovalConfirmation(user.person, view, event)

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
   * @return
   */
  def create = SecuredRestrictedAction(Role.Coordinator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val form: Form[Evaluation] = evaluationForm(user.name).bindFromRequest
      form.fold(
        formWithErrors ⇒ {
          val account = user.account
          val events = findEvents(account)
          BadRequest(views.html.evaluation.form(user, None, formWithErrors, events, None, None))
        },
        evaluation ⇒ {
          val defaultHook = request.host + routes.Evaluations.confirm("").url
          val eval = evaluation.add(defaultHook, withConfirmation = true)
          val log = activity(eval, user.person).created.insert()
          Redirect(routes.Events.details(evaluation.eventId)).flashing("success" -> log.toString)
        })
  }

  /**
   * Delete an evaluation
   * @param id Unique evaluation identifier
   */
  def delete(id: Long) = SecuredEvaluationAction(List(Role.Facilitator, Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      evaluationService.find(id).map { x ⇒
        x.delete()
        // recalculate ratings
        Event.ratingActor ! x.eventId
        Facilitator.ratingActor ! x.eventId
        val log = activity(x, user.person).deleted.insert()
        val route = routes.Events.details(x.eventId).url
        Redirect(route).flashing("success" -> log.toString)
      }.getOrElse(NotFound)
  }

  /**
   * Move an evaluation to another event
   * @param id Unique evaluation identifier
   */
  def move(id: Long) = SecuredEvaluationAction(List(Role.Facilitator, Role.Coordinator), id) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒ implicit event =>

        evaluationService.find(id).map { evaluation ⇒
          val form = Form(single(
            "eventId" -> longNumber))
          val (eventId) = form.bindFromRequest
          form.bindFromRequest.fold(
            f ⇒ BadRequest(Json.obj("error" -> "Event is not chosen")),
            eventId ⇒ {
              if (eventId == evaluation.eventId) {
                val log = activity(evaluation, user.person).updated.insert()
                Ok(Json.obj("success" -> log.toString))
              } else {
                eventService.find(eventId).map { event ⇒
                  participantService.find(evaluation.attendeeId, evaluation.eventId).map { oldParticipant ⇒
                    // first we need to check if this event has already the participant
                    participantService.find(evaluation.attendeeId, eventId).map { participant ⇒
                      // if yes, we reassign an evaluation
                      participant.copy(evaluationId = Some(id)).update
                      oldParticipant.copy(evaluationId = None).update
                    }.getOrElse {
                      // if no, we move a participant
                      oldParticipant.copy(eventId = eventId).update
                    }
                    evaluation.copy(eventId = eventId).update
                    val log = activity(evaluation, user.person).updated.insert()
                    Ok(Json.obj("success" -> log.toString))
                  }.getOrElse(NotFound)
                }.getOrElse(NotFound)
              }
            })
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
   * Renders an Edit page
   *
   * @param id Unique evaluation identifier
   */
  def edit(id: Long) = SecuredEvaluationAction(List(Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>

      evaluationService.find(id).map { evaluation ⇒
        val account = user.account
        val events = findEvents(account)

        Ok(views.html.evaluation.form(user, Some(evaluation),
          evaluationForm(user.name).fill(evaluation), events, None, None))
      }.getOrElse(NotFound)

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
   * Update an evaluation
   *
   * @param id Unique evaluation identifier
   * @return
   */
  def update(id: Long) = SecuredEvaluationAction(List(Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>

      evaluationService.find(id).map { existingEvaluation ⇒
        val form: Form[Evaluation] = evaluationForm(user.name, edit = true).bindFromRequest
        form.fold(
          formWithErrors ⇒ {
            val account = user.account
            val events = findEvents(account)

            BadRequest(views.html.evaluation.form(user, Some(existingEvaluation), form, events, None, None))
          },
          evaluation ⇒ {
            val eval = evaluation.copy(id = Some(id)).update()
            val log = activity(eval, user.person).updated.insert()
            Redirect(routes.Events.details(evaluation.eventId)).flashing("success" -> log.toString)
          })
      }.getOrElse(NotFound)
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
   * @param view Evaluation
   * @param event Related event
   */
  protected def sendApprovalConfirmation(approver: Person, view: EvaluationAttendeeView, event: Event) = {
    brandService.findWithSettings(event.brandId) foreach { withSettings =>
      val coordinators = brandService.coordinators(event.brandId)
      participantService.find(view.evaluation.attendeeId, view.evaluation.eventId) foreach { data ⇒
        val bcc = coordinators.filter(_._2.notification.evaluation).map(_._1)
        if (data.certificate.isEmpty && withSettings.settings.certificates && !event.free) {
          val cert = new Certificate(view.evaluation.handled, event, view.attendee)
          cert.generateAndSend(BrandWithCoordinators(withSettings.brand, coordinators), approver)
          data.copy(certificate = Some(cert.id), issued = cert.issued).update
        } else if (data.certificate.isEmpty) {
          val body = mail.templates.evaluation.html.approvedNoCert(withSettings.brand, view.attendee, approver).toString()
          val subject = s"Your ${withSettings.brand.name} event's evaluation approval"
          email.send(Set(view.attendee),
            Some(event.facilitators.toSet),
            Some(bcc.toSet),
            subject, body, from = withSettings.brand.name, richMessage = true, None)
        } else {
          val cert = new Certificate(view.evaluation.handled, event, view.attendee, renew = true)
          cert.send(BrandWithCoordinators(withSettings.brand, coordinators), approver)
        }
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

  /**
   * Retrieve active events which a user is able to see
   *
   * @param account User object
   */
  private def findEvents(account: UserAccount): List[Event] = {
    val brands = brandService.findByCoordinator(account.personId)
    if (brands.nonEmpty) {
      val events = EventService.get.findByParameters(
        brandId = None,
        archived = Some(false))
      events.filter(e ⇒ brands.exists(_.brand.id == Some(e.brandId)))
    } else {
      List[Event]()
    }
  }
}
