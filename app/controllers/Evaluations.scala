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
import models.UserRole.Role._
import models.admin.Translation
import models.service.EventService
import org.joda.time._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json
import services.notifiers.Notifiers

object Evaluations extends EvaluationsController with Security with Notifiers {

  /** HTML form mapping for creating and editing. */
  def evaluationForm(userName: String, edit: Boolean = false) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "eventId" -> longNumber.verifying(
      "An event doesn't exist", (eventId: Long) ⇒ EventService.get.find(eventId).isDefined),
    "participantId" -> {
      if (edit) of(participantIdOnEditFormatter) else of(participantIdFormatter)
    },
    "question1" -> nonEmptyText,
    "question2" -> nonEmptyText,
    "question3" -> nonEmptyText,
    "question4" -> nonEmptyText,
    "question5" -> nonEmptyText,
    "question6" -> number(min = 0, max = 10),
    "question7" -> number(min = 0, max = 10),
    "question8" -> nonEmptyText,
    "status" -> statusMapping,
    "handled" -> optional(jodaLocalDate),
    "created" -> ignored(DateTime.now),
    "createdBy" -> ignored(userName),
    "updated" -> ignored(DateTime.now),
    "updatedBy" -> ignored(userName))(Evaluation.apply)(Evaluation.unapply))

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
        val en = Translation.find("EN").get
        Ok(views.html.evaluation.form(user, None, evaluationForm(user.fullName), events, eventId, participantId, en))
  }

  /**
   * Add form submits to this action
   * @return
   */
  def create = SecuredDynamicAction("evaluation", "add") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val form: Form[Evaluation] = evaluationForm(user.fullName).bindFromRequest
      form.fold(
        formWithErrors ⇒ {
          val account = user.account
          val events = findEvents(account)
          val en = Translation.find("EN").get
          BadRequest(views.html.evaluation.form(user, None, formWithErrors, events, None, None, en))
        },
        evaluation ⇒ {
          val eval = evaluation.create
          val activity = eval.activity(user.person, Activity.Predicate.Created).insert
          Redirect(routes.Participants.index()).flashing("success" -> activity.toString)
        })
  }

  /**
   * Delete an evaluation
   * @param id Unique evaluation identifier
   * @param ref Identifier of a page where a user should be redirected
   * @return
   */
  def delete(id: Long, ref: Option[String] = None) = SecuredDynamicAction("evaluation", "manage") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      Evaluation.find(id).map {
        evaluation ⇒
          evaluation.delete()
          val activity = evaluation.activity(user.person, Activity.Predicate.Deleted).insert

          val route = ref match {
            case Some("index") ⇒ routes.Participants.index().url
            case _ ⇒ routes.Events.details(evaluation.eventId).url + "#participant"
          }
          Redirect(route).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  /**
   * Move an evaluation to another event
   * @param id Unique evaluation identifier
   * @return
   */
  def move(id: Long) = SecuredDynamicAction("evaluation", "manage") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      Evaluation.find(id).map { evaluation ⇒
        val form = Form(single(
          "eventId" -> longNumber))
        val (eventId) = form.bindFromRequest
        form.bindFromRequest.fold (
          f ⇒ BadRequest(Json.obj("error" -> "Event is not chosen")),
          eventId ⇒ {
            if (eventId == evaluation.eventId) {
              val activity = evaluation.activity(
                user.person,
                Activity.Predicate.Updated).insert
              Ok(Json.obj("success" -> activity.toString))
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
                  val activity = evaluation.activity(
                    user.person,
                    Activity.Predicate.Updated).insert
                  Ok(Json.obj("success" -> activity.toString))
                }.getOrElse(NotFound)
              }.getOrElse(NotFound)
            }
          })
      }.getOrElse(NotFound)
  }

  /**
   * Renders a Details page
   *
   * @param id Unique evaluation identifier
   */
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      Evaluation.find(id).map { evaluation ⇒
        val brand = Brand.find(evaluation.event.brandCode).get
        val en = Translation.find("EN").get
        Ok(views.html.evaluation.details(user, evaluation, en, brand.brand))
      }.getOrElse(NotFound)

  }

  /**
   * Renders an Edit page
   *
   * @param id Unique evaluation identifier
   */
  def edit(id: Long) = SecuredDynamicAction("evaluation", "edit") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      Evaluation.find(id).map { evaluation ⇒
        val account = user.account
        val events = findEvents(account)
        val en = Translation.find("EN").get

        Ok(views.html.evaluation.form(user, Some(evaluation),
          evaluationForm(user.fullName).fill(evaluation), events, None, None, en))
      }.getOrElse(NotFound)

  }

  /**
   * Update an evaluation
   *
   * @param id Unique evaluation identifier
   * @return
   */
  def update(id: Long) = SecuredDynamicAction("evaluation", "edit") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      Evaluation.find(id).map { existingEvaluation ⇒
        val form: Form[Evaluation] = evaluationForm(user.fullName, edit = true).bindFromRequest
        form.fold(
          formWithErrors ⇒ {
            val account = user.account
            val events = findEvents(account)
            val en = Translation.find("EN").get

            BadRequest(views.html.evaluation.form(user, Some(existingEvaluation), form, events, None, None, en))
          },
          evaluation ⇒ {
            val eval = evaluation.copy(id = Some(id)).update
            val activity = eval.activity(
              user.person,
              Activity.Predicate.Updated).insert

            Redirect(routes.Participants.index()).flashing("success" -> activity.toString)
          })
      }.getOrElse(NotFound)
  }

  /**
   * Approve form submits to this action
   *
   * @param id Evaluation identifier
   * @param ref Identifier of a page where a user should be redirected
   */
  def approve(id: Long, ref: Option[String] = None) = SecuredDynamicAction("evaluation", "manage") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        Evaluation.find(id).map { ev ⇒

          ev.approve(user.person)

          val activity = ev.activity(
            user.person,
            Activity.Predicate.Approved).insert

          val route = ref match {
            case Some("index") ⇒ routes.Participants.index().url
            case Some("evaluation") ⇒ routes.Evaluations.details(id).url
            case _ ⇒ routes.Events.details(ev.eventId).url + "#participant"
          }
          Redirect(route).flashing("success" -> activity.toString)
        }.getOrElse(NotFound)
  }

  /**
   * Reject form submits to this action
   *
   * @param id Evaluation identifier
   * @param ref Identifier of a page where a user should be redirected
   */
  def reject(id: Long, ref: Option[String] = None) = SecuredDynamicAction("evaluation", "manage") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Evaluation.find(id).map { existingEvaluation ⇒
        existingEvaluation.reject()

        val activity = existingEvaluation.activity(
          user.person,
          Activity.Predicate.Rejected).insert

        val brand = Brand.find(existingEvaluation.event.brandCode).get
        val participant = existingEvaluation.participant
        val subject = s"Your ${brand.brand.name} certificate"
        email.send(Set(participant),
          Some(existingEvaluation.event.facilitators.toSet),
          Some(Set(brand.coordinator)), subject,
          mail.html.rejected(brand.brand, participant, user.person).toString(),
          richMessage = true)

        val route = ref match {
          case Some("index") ⇒ routes.Participants.index().url
          case Some("evaluation") ⇒ routes.Evaluations.details(id).url
          case _ ⇒ routes.Events.details(existingEvaluation.eventId).url + "#participant"
        }
        Redirect(route).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  /**
   * Retrieve active events which a user is able to see
   *
   * @param account User object
   */
  private def findEvents(account: UserAccount): List[Event] = {
    if (account.editor) {
      EventService.get.findByParameters(
        brandCode = None,
        archived = Some(false),
        confirmed = Some(true))
    } else {
      val brands = Brand.findByCoordinator(account.personId)
      if (brands.length > 0) {
        val brandCodes = brands.map(_.code)
        val events = EventService.get.findByParameters(
          brandCode = None,
          archived = Some(false),
          confirmed = Some(true))
        events.filter(e ⇒ brandCodes.exists(_ == e.brandCode))
      } else {
        List[Event]()
      }
    }
  }
}
