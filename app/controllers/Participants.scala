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

import models.{ Participant, Evaluation, Person, Event, LoginIdentity, Activity }
import models.{ Brand, ParticipantView, EvaluationStatus, UserAccount, ParticipantData }
import org.joda.time.DateTime
import play.api.data._
import play.api.data.Forms._
import models.UserRole.Role._
import securesocial.core.SecuredRequest
import play.api.i18n.Messages
import play.api.libs.json._
import play.mvc.Controller
import views.Countries

object Participants extends Controller with Security {

  def newPersonForm(account: UserAccount, userName: String) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "eventId" -> longNumber.verifying(
        "error.event.invalid",
        (eventId: Long) ⇒ Event.canManage(eventId, account)),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "birthDate" -> optional(jodaLocalDate),
      "emailAddress" -> email,
      "city" -> nonEmptyText,
      "country" -> nonEmptyText.verifying(
        "Unknown country",
        (country: String) ⇒ Countries.all.exists(_._1 == country)),
      "created" -> ignored(DateTime.now()),
      "createdBy" -> ignored(userName),
      "updated" -> ignored(DateTime.now()),
      "updatedBy" -> ignored(userName))(ParticipantData.apply)(ParticipantData.unapply))
  }

  def existingPersonForm(implicit request: SecuredRequest[_]) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "eventId" -> longNumber.verifying(
        "error.event.invalid",
        (eventId: Long) ⇒ Event.canManage(eventId, request.user.asInstanceOf[LoginIdentity].userAccount)),
      "participantId" -> longNumber.verifying(
        "error.person.invalid",
        (participantId: Long) ⇒ !Person.find(participantId).isEmpty),
      "evaluationId" -> optional(longNumber))(Participant.apply)(Participant.unapply))
  }

  /**
   * Returns a list of participants
   */
  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒
      val account = request.user.asInstanceOf[LoginIdentity].userAccount
      val brands = Brand.findForUser(account)
      val brandCode = request.session.get("brandCode").getOrElse("")
      Ok(views.html.participant.index(request.user, brands, brandCode))
  }

  /**
   * Returns JSON data about participants together with their evaluations
   * and events
   */
  def participantsByBrand(brandCode: String) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒
      Brand.find(brandCode).map { brand ⇒
        val account = request.user.asInstanceOf[LoginIdentity].userAccount

        implicit val participantViewWrites = new Writes[ParticipantView] {
          def writes(data: ParticipantView): JsValue = {
            Json.obj(
              "person" -> Json.obj(
                "url" -> routes.People.details(data.person.id.get).url,
                "name" -> data.person.fullName),
              "event" -> Json.obj(
                "id" -> data.event.id,
                "url" -> routes.Events.details(data.event.id.get).url,
                "title" -> data.event.title,
                "longTitle" -> data.event.longTitle,
                "facilitatedByMe" -> data.event.facilitatorIds.contains(account.personId)),
              "location" -> Json.obj(
                "country" -> data.event.location.countryCode.toLowerCase,
                "city" -> data.event.location.city),
              "schedule" -> Json.obj(
                "start" -> data.event.schedule.start.toString,
                "end" -> data.event.schedule.end.toString),
              "evaluation" -> evaluation(data),
              "actions" -> {
                data.evaluationId match {
                  case Some(id) ⇒ Json.obj(
                    "certificate" -> certificateActions(id, brand.brand, data),
                    "evaluation" -> evaluationActions(id, brand.brand, data, account),
                    "participant" -> participantActions(data, account))
                  case None ⇒ if (!data.event.archived) {
                    Json.obj(
                      "evaluation" -> Json.obj(
                        "add" -> routes.Evaluations.add(data.event.id, data.person.id).url),
                      "participant" -> participantActions(data, account))
                  } else {
                    Json.obj(
                      "participant" -> participantActions(data, account))
                  }
                }
              })
          }
        }
        val participants = Participant.findByBrand(Some(brandCode))
        Ok(Json.toJson(participants)).withSession("brandCode" -> brandCode)
      }.getOrElse(NotFound("Unknown brand"))
  }

  /**
   * Returns JSON data about participants together with their evaluations
   */
  def participantsByEvent(eventId: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒
      Event.find(eventId).map { event ⇒
        val account = request.user.asInstanceOf[LoginIdentity].userAccount
        val brand = Brand.find(event.brandCode).get
        implicit val participantViewWrites = new Writes[ParticipantView] {
          def writes(data: ParticipantView): JsValue = {
            Json.obj(
              "person" -> Json.obj(
                "url" -> routes.People.details(data.person.id.get).url,
                "name" -> data.person.fullName,
                "id" -> data.person.id.get),
              "evaluation" -> evaluation(data),
              "actions" -> {
                data.evaluationId match {
                  case Some(id) ⇒ Json.obj(
                    "certificate" -> certificateActions(id, brand.brand, data),
                    "evaluation" -> evaluationActions(id, brand.brand, data, account),
                    "participant" -> participantActions(data, account))
                  case None ⇒ if (!data.event.archived) {
                    Json.obj(
                      "evaluation" -> Json.obj(
                        "add" -> routes.Evaluations.add(data.event.id, data.person.id).url),
                      "participant" -> participantActions(data, account))
                  } else {
                    Json.obj(
                      "participant" -> participantActions(data, account))
                  }
                }
              })
          }
        }
        val participants = Participant.findByEvent(eventId)
        Ok(Json.toJson(participants))
      }.getOrElse(NotFound("Unknown brand"))
  }

  /**
   * Returns a list of participants without evaluations for a particular event
   */
  def participants(eventId: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit val personWrites = new Writes[Person] {
      def writes(person: Person): JsValue = {
        Json.obj(
          "id" -> person.id.get,
          "name" -> person.fullName)
      }
    }

    implicit handler ⇒
      Event.find(eventId).map { event ⇒
        val participants = event.participants
        val evaluations = Evaluation.findByEvent(eventId)
        Ok(Json.toJson(participants.filterNot(p ⇒ evaluations.exists(e ⇒ Some(e.participantId) == p.id))))
      }.getOrElse(NotFound("Unknown event"))
  }

  /**
   * Create page.
   */
  def add = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒

      val account = request.user.asInstanceOf[LoginIdentity].userAccount
      val events = findEvents(account)
      val people = Person.findActive
      Ok(views.html.participant.form(request.user, None, events, people,
        newPersonForm(account, request.user.fullName), existingPersonForm(request)))
  }

  def create = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒
      val form: Form[Participant] = existingPersonForm.bindFromRequest

      form.fold(
        formWithErrors ⇒ {
          val account = request.user.asInstanceOf[LoginIdentity].userAccount
          val events = findEvents(account)
          val people = Person.findActive
          BadRequest(views.html.participant.form(request.user, None, events, people,
            newPersonForm(account, request.user.fullName), formWithErrors))
        },
        participant ⇒ {
          Participant.insert(participant)
          val activityObject = Messages("activity.participant.create",
            participant.participant.get.fullName, participant.event.get.title)
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, activityObject)
          Redirect(routes.Participants.add).flashing("success" -> activity.toString)
        })
  }

  def createParticipantAndPerson = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒
      val account = request.user.asInstanceOf[LoginIdentity].userAccount
      val form: Form[ParticipantData] = newPersonForm(account, request.user.fullName).bindFromRequest

      form.fold(
        formWithErrors ⇒ {
          val events = findEvents(account)
          val people = Person.findActive
          BadRequest(views.html.participant.form(request.user, None, events, people,
            formWithErrors, existingPersonForm(request), showExistingPersonForm = false))
        },
        data ⇒ {
          Participant.create(data)
          val activityObject = Messages("activity.participant.create", data.firstName + " " + data.lastName, data.event.get.title)
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, activityObject)
          Redirect(routes.Participants.add()).flashing("success" -> activity.toString)
        })
  }

  def delete(eventId: Long, personId: Long) = SecuredDynamicAction("event", "edit") { implicit request ⇒
    implicit handler ⇒
      Participant.find(personId, eventId).map { value ⇒

        val activityObject = Messages("activity.participant.delete", value.participant.get.fullName, value.event.get.title)
        value.delete()
        val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, activityObject)

        Redirect(routes.Participants.index).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  private def evaluation(data: ParticipantView): JsValue = {
    Json.obj(
      "impression" -> data.impression.map { value ⇒
        Json.obj(
          "value" -> value,
          "caption" -> Messages(s"evaluation.impression.$value"))
      },
      "status" -> data.status.map(status ⇒
        Json.obj(
          "label" -> Messages("models.EvaluationStatus." + status),
          "value" -> status.id)),
      "creation" -> data.date.map(_.toString("yyyy-MM-dd")),
      "handled" -> data.handled.map(_.get.toString),
      "certificate" -> data.status.map(status ⇒
        if (status == EvaluationStatus.Approved) {
          data.certificate.map(id ⇒
            Json.obj(
              "id" -> id.get,
              "url" -> routes.Certificates.certificate(id.get).url)).getOrElse(Json.obj())
        } else Json.obj()))
  }

  /** Return a list of possible actions for a certificate */
  private def certificateActions(evaluationId: Long, brand: Brand, data: ParticipantView): JsValue = {
    Json.obj(
      "generate" -> {
        if (data.status.get == EvaluationStatus.Approved && brand.generateCert)
          routes.Certificates.create(evaluationId).url
        else ""
      })
  }

  /** Return a list of possible actions for an evaluation */
  private def evaluationActions(id: Long, brand: Brand, data: ParticipantView, account: UserAccount): JsValue = {
    Json.obj(
      "approve" -> {
        if (data.status.get != EvaluationStatus.Approved)
          routes.Evaluations.approve(id).url
        else ""
      },
      "reject" -> {
        if (data.status.get != EvaluationStatus.Rejected)
          routes.Evaluations.reject(id).url
        else ""
      },
      "edit" -> {
        if (account.editor || brand.coordinatorId == account.personId)
          routes.Evaluations.edit(id).url
        else ""
      },
      "view" -> routes.Evaluations.details(id).url,
      "remove" -> routes.Evaluations.delete(id).url)
  }

  /** Return a list of possible actions for a participant */
  private def participantActions(data: ParticipantView, account: UserAccount): JsValue = {
    Json.obj("view" -> routes.People.details(data.person.id.get).url,
      "edit" -> {
        if (account.editor || data.person.virtual)
          routes.People.edit(data.person.id.get).url
        else ""
      },
      "remove" -> {
        if (account.editor || data.person.virtual)
          routes.People.details(data.person.id.get).url
        else ""
      },
      "removeParticipation" -> routes.Participants.delete(data.event.id.get, data.person.id.get).url)
  }

  private def findEvents(account: UserAccount): List[Event] = {
    if (account.editor) {
      Event.findActive
    } else {
      Event.findByCoordinator(account.personId)
    }
  }
}
