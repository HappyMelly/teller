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

import models.{ Participant, Evaluation, Person, Event, LoginIdentity, Activity, Address, Photo }
import models.{ Brand, ParticipantView, EvaluationStatus }
import org.joda.time.{ LocalDate, DateTime }
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.UserRole.Role._
import securesocial.core.SecuredRequest
import play.api.i18n.Messages
import play.api.libs.json._

case class ParticipantData(id: Option[Long],
  eventId: Long,
  firstName: String,
  lastName: String,
  birthDate: Option[LocalDate],
  emailAddress: String,
  city: String,
  country: String,
  created: DateTime = DateTime.now(),
  createdBy: String,
  updated: DateTime,
  updatedBy: String) {

  lazy val event: Option[Event] = Event.find(eventId)
}

object Participants extends Controller with Security {

  def newPersonForm(implicit request: SecuredRequest[_]) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "eventId" -> longNumber.verifying(
        "error.event.invalid",
        (eventId: Long) ⇒ Event.canManage(eventId, request.user.asInstanceOf[LoginIdentity].userAccount)),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "birthDate" -> optional(jodaLocalDate),
      "emailAddress" -> email,
      "city" -> nonEmptyText,
      "country" -> nonEmptyText,
      "created" -> ignored(DateTime.now()),
      "createdBy" -> ignored(request.user.fullName),
      "updated" -> ignored(DateTime.now()),
      "updatedBy" -> ignored(request.user.fullName))(ParticipantData.apply)(ParticipantData.unapply))
  }

  def existingPersonForm(implicit request: SecuredRequest[_]) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "eventId" -> longNumber.verifying(
        "error.event.invalid",
        (eventId: Long) ⇒ Event.canManage(eventId, request.user.asInstanceOf[LoginIdentity].userAccount)),
      "participantId" -> longNumber.verifying(
        "error.person.invalid",
        (participantId: Long) ⇒ !Person.find(participantId).isEmpty))(Participant.apply)(Participant.unapply))
  }

  /**
   * Returns a list of participants
   */
  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒
      val account = request.user.asInstanceOf[LoginIdentity].userAccount
      val brands = Brand.findForUser(account)
      Ok(views.html.participant.index(request.user, brands))
  }

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
                "url" -> routes.Events.details(data.event.id.get).url,
                "title" -> data.event.title),
              "location" -> Json.obj(
                "country" -> data.event.location.countryCode.toLowerCase,
                "city" -> data.event.location.city),
              "schedule" -> Json.obj(
                "start" -> data.event.schedule.start.toString,
                "end" -> data.event.schedule.end.toString),
              "impression" -> data.impression.map(value ⇒ Messages(s"evaluation.impression.${value}")),
              "status" -> data.status.map(status ⇒
                Json.obj(
                  "label" -> Messages("models.EvaluationStatus." + status),
                  "value" -> status.id)),
              "creation" -> data.date.map(_.toString("yyyy-MM-dd")),
              "handled" -> data.handled.map(_.get.toString),
              "certificate" -> data.certificate.map(_.toString),
              "actions" -> {
                data.evaluationId match {
                  case Some(id) ⇒ Json.obj(
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
                      if (account.editor || brand.brand.coordinatorId == account.personId)
                        routes.Evaluations.edit(id).url
                      else ""
                    },
                    "view" -> routes.Evaluations.details(id).url,
                    "remove" -> routes.Evaluations.delete(id).url)
                  case None ⇒ Json.obj()
                }
              })
          }
        }
        val participants = Participant.findAll(Some(brandCode))
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
        Ok(Json.toJson(participants.filterNot(p ⇒ evaluations.exists(_.participantId == p.id))))
      }.getOrElse(NotFound("Unknown event"))
  }

  /**
   * Create page.
   */
  def add = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒

      val account = request.user.asInstanceOf[LoginIdentity].userAccount
      val events = Event.findByUser(account)
      var people = Person.findActive
      Ok(views.html.participant.form(request.user, None, events, people, newPersonForm(request), existingPersonForm(request)))
  }

  def create = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒
      val form: Form[Participant] = existingPersonForm.bindFromRequest

      form.fold(
        formWithErrors ⇒ {
          val account = request.user.asInstanceOf[LoginIdentity].userAccount
          val events = Event.findByUser(account)
          var people = Person.findActive
          BadRequest(views.html.participant.form(request.user, None, events, people,
            newPersonForm(request), formWithErrors))
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
      val form: Form[ParticipantData] = newPersonForm.bindFromRequest

      form.fold(
        formWithErrors ⇒ {
          val account = request.user.asInstanceOf[LoginIdentity].userAccount
          val events = Event.findByUser(account)
          var people = Person.findActive
          BadRequest(views.html.participant.form(request.user, None, events, people,
            formWithErrors, existingPersonForm(request), false))
        },
        participant ⇒ {
          val address = Address(None, None, None, Some(participant.city), None, None, participant.country)
          val person = Person(None, participant.firstName, participant.lastName, participant.emailAddress,
            Photo(None, None), address, None, None, None, None, None, None, false, false, None, None, true,
            participant.created, participant.createdBy, participant.updated, participant.updatedBy)
          val newPerson = person.insert
          val eventParticipant = Participant(None, participant.eventId, newPerson.id.get)
          Participant.insert(eventParticipant)
          val activityObject = Messages("activity.participant.create", person.fullName, participant.event.get.title)
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, activityObject)
          Redirect(routes.Participants.add).flashing("success" -> activity.toString)
        })
  }
}
