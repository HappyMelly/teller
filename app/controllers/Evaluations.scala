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

import models.UserRole.DynamicRole
import models.UserRole.Role._
import models._
import models.service.{ EventService, Services }
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
    with Activities {

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
  def add(eventId: Option[Long], participantId: Option[Long]) = SecuredDynamicAction("evaluation", "add") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        val account = user.account
        val events = findEvents(account)
        Ok(views.html.evaluation.form(user, None, evaluationForm(user.name), events, eventId, participantId))
  }

  /**
   * Add form submits to this action
   * @return
   */
  def create = SecuredDynamicAction("evaluation", "add") { implicit request ⇒
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
          Redirect(routes.Participants.index()).flashing("success" -> log.toString)
        })
  }

  /**
   * Delete an evaluation
   * @param id Unique evaluation identifier
   * @param ref Identifier of a page where a user should be redirected
   * @return
   */
  def delete(id: Long, ref: Option[String] = None) = SecuredDynamicAction("evaluation", DynamicRole.Facilitator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒

        Evaluation.find(id).map { x ⇒
          x.delete()
          // recalculate ratings
          Event.ratingActor ! x.eventId
          Facilitator.ratingActor ! x.eventId

          val log = activity(x, user.person).deleted.insert()

          val route = ref match {
            case Some("index") ⇒ routes.Participants.index().url
            case _ ⇒ routes.Events.details(x.eventId).url + "#participant"
          }
          Redirect(route).flashing("success" -> log.toString)
        }.getOrElse(NotFound)
  }

  /**
   * Move an evaluation to another event
   * @param id Unique evaluation identifier
   * @return
   */
  def move(id: Long) = SecuredDynamicAction("evaluation", DynamicRole.Facilitator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒

        Evaluation.find(id).map { evaluation ⇒
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
                EventService.get.find(eventId).map { event ⇒
                  Participant.find(evaluation.personId, evaluation.eventId).map { oldParticipant ⇒
                    // first we need to check if this event has already the participant
                    Participant.find(evaluation.personId, eventId).map { participant ⇒
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
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      evaluationService.find(id) map { x ⇒
        val brand = brandService.find(x.event.brandId).get
        val participant = personService.find(x.eval.personId).get
        val personId = user.person.identifier
        val (facilitator, endorsement) = if (x.event.facilitatorIds.contains(personId))
          (true, personService.findEndorsementByEvaluation(id, personId))
        else
          (false, None)
        Ok(views.html.evaluation.details(user, x,
          participant.fullName,
          brand.generateCert,
          facilitator,
          endorsement))
      } getOrElse NotFound

  }

  /**
   * Renders an Edit page
   *
   * @param id Unique evaluation identifier
   */
  def edit(id: Long) = SecuredDynamicAction("evaluation", DynamicRole.Coordinator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      Evaluation.find(id).map { evaluation ⇒
        val account = user.account
        val events = findEvents(account)

        Ok(views.html.evaluation.form(user, Some(evaluation),
          evaluationForm(user.name).fill(evaluation), events, None, None))
      }.getOrElse(NotFound)

  }

  /**
   * Update an evaluation
   *
   * @param id Unique evaluation identifier
   * @return
   */
  def update(id: Long) = SecuredDynamicAction("evaluation", DynamicRole.Coordinator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      Evaluation.find(id).map { existingEvaluation ⇒
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
            Redirect(routes.Participants.index()).flashing("success" -> log.toString)
          })
      }.getOrElse(NotFound)
  }

  /**
   * Approve form submits to this action
   *
   * @param id Evaluation identifier
   * @param ref Identifier of a page where a user should be redirected
   */
  def approve(id: Long, ref: Option[String] = None) = SecuredDynamicAction("evaluation", DynamicRole.Facilitator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        evaluationService.find(id).map { x ⇒
          val route: String = ref match {
            case Some("index") ⇒ routes.Participants.index().url
            case Some("evaluation") ⇒ routes.Evaluations.details(id).url
            case _ ⇒ routes.Events.details(x.eval.eventId).url + "#participant"
          }
          if (x.eval.approvable) {
            val evaluation = x.eval.approve
            // recalculate ratings
            Event.ratingActor ! evaluation.eventId
            Facilitator.ratingActor ! evaluation.eventId

            val log = activity(evaluation, user.person).approved.insert()
            sendApprovalConfirmation(user.person, evaluation, x.event)

            Redirect(route).flashing("success" -> log.toString)
          } else {
            val error = x.eval.status match {
              case EvaluationStatus.Unconfirmed ⇒ "error.evaluation.approve.unconfirmed"
              case _ ⇒ "error.evaluation.approve.approved"
            }
            Redirect(route).flashing("error" -> Messages(error))
          }
        }.getOrElse(NotFound)
  }

  /**
   * Reject form submits to this action
   *
   * @param id Evaluation identifier
   * @param ref Identifier of a page where a user should be redirected
   */
  def reject(id: Long, ref: Option[String] = None) = SecuredDynamicAction("evaluation", DynamicRole.Facilitator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      evaluationService.find(id).map { x ⇒
        val route: String = ref match {
          case Some("index") ⇒ routes.Participants.index().url
          case Some("evaluation") ⇒ routes.Evaluations.details(id).url
          case _ ⇒ routes.Events.details(x.eval.eventId).url + "#participant"
        }
        if (x.eval.rejectable) {
          x.eval.reject()

          // recalculate ratings
          Event.ratingActor ! x.eval.eventId
          Facilitator.ratingActor ! x.eval.eventId

          val log = activity(x.eval, user.person).rejected.insert()
          sendRejectionConfirmation(user.person, x.eval.participant, x.event)

          Redirect(route).flashing("success" -> log.toString)
        } else {
          val error = x.eval.status match {
            case EvaluationStatus.Unconfirmed ⇒ "error.evaluation.reject.unconfirmed"
            case _ ⇒ "error.evaluation.reject.rejected"
          }
          Redirect(route).flashing("error" -> Messages(error))
        }
      }.getOrElse(NotFound)
  }

  /**
   * Confirms the given evaluation
   * @param confirmationId Confirmation unique id
   */
  def confirm(confirmationId: String) = Action { implicit request ⇒
    evaluationService.find(confirmationId) map { x ⇒
      x.confirm()
      Ok(views.html.evaluation.confirmed())
    } getOrElse NotFound(views.html.evaluation.notfound())
  }

  /**
   * Sends confirmation email that evaluation was approved
   * @param approver Person who approved the given evaluation
   * @param ev Evaluation
   * @param event Related event
   */
  protected def sendApprovalConfirmation(approver: Person,
    ev: Evaluation,
    event: Event) = {
    brandService.findWithCoordinators(event.brandId) foreach { x ⇒
      Participant.find(ev.personId, ev.eventId) foreach { data ⇒
        val bcc = x.coordinators.filter(_._2.notification.evaluation).map(_._1)
        if (data.certificate.isEmpty && x.brand.generateCert && !event.free) {
          val cert = new Certificate(ev.handled, event, ev.participant)
          cert.generateAndSend(x, approver)
          data.copy(certificate = Some(cert.id), issued = cert.issued).update
        } else if (data.certificate.isEmpty) {
          val body = mail.templates.evaluation.html.approvedNoCert(x.brand, ev.participant, approver).toString()
          val subject = s"Your ${x.brand.name} event's evaluation approval"
          email.send(Set(ev.participant),
            Some(event.facilitators.toSet),
            Some(bcc.toSet),
            subject, body, richMessage = true, None)
        } else {
          val cert = new Certificate(ev.handled, event, ev.participant, renew = true)
          cert.send(x, approver)
        }
      }
    }
  }

  /**
   * Sends confirmation email that evaluation was rejected
   * @param rejector Person who rejected the evaluation
   * @param participant Participant
   * @param event Related event
   */
  protected def sendRejectionConfirmation(rejector: Person,
    participant: Person,
    event: Event) = {
    brandService.findWithCoordinators(event.brandId) foreach { x ⇒
      val bcc = x.coordinators.filter(_._2.notification.evaluation).map(_._1)
      val subject = s"Your ${x.brand.name} certificate"
      email.send(Set(participant),
        Some(event.facilitators.toSet),
        Some(bcc.toSet), subject,
        mail.templates.evaluation.html.rejected(x.brand, participant, rejector).toString(),
        richMessage = true)
    }
  }

  /**
   * Retrieve active events which a user is able to see
   *
   * @param account User object
   */
  private def findEvents(account: UserAccount): List[Event] = {
    if (account.editor) {
      EventService.get.findByParameters(
        brandId = None,
        archived = Some(false))
    } else {
      val brands = brandService.findByCoordinator(account.personId)
      if (brands.length > 0) {
        val events = EventService.get.findByParameters(
          brandId = None,
          archived = Some(false))
        events.filter(e ⇒ brands.exists(_.id == Some(e.brandId)))
      } else {
        List[Event]()
      }
    }
  }
}
