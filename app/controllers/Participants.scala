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

import models.UserRole.Role
import models.UserRole.Role._
import models._
import models.brand.Settings
import models.service.{EventService, PersonService, Services}
import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages
import play.api.libs.json._
import securesocial.core.RuntimeEnvironment
import views.Countries

import scala.concurrent.Future

class Participants(environment: RuntimeEnvironment[ActiveUser])
    extends JsonController
    with Security
    with Services
    with Activities
    with Utilities {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  def newPersonForm(eventId: Long, userName: String) = {
    Form(mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "birthday" -> optional(jodaLocalDate),
      "emailAddress" -> email,
      "city" -> nonEmptyText,
      "country" -> nonEmptyText.verifying(
        "Unknown country",
        (country: String) ⇒ Countries.all.exists(_._1 == country)),
      "role" -> optional(text))({
      (firstName, lastName, birthday, email, city, country, role) ⇒
        ParticipantData(None, eventId, firstName, lastName, birthday, email,
            Address(None, None, None, Some(city), None, None, country),
            organisation = None, comment = None, role,
            DateTime.now(), userName, DateTime.now(), userName)
      })({
        (p: ParticipantData) ⇒
          Some((p.firstName, p.lastName, p.birthday, p.emailAddress,
            p.address.city.getOrElse(""), p.address.countryCode, p.role))
      }))
  }

  /**
    * HTML form mapping for creating and editing.
    */
  def personForm(eventId: Long, editorName: String) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "emailAddress" -> play.api.data.Forms.email,
      "birthday" -> optional(jodaLocalDate),
      "address" -> People.addressMapping,
      "role" -> optional(text))({
      (id, firstName, lastName, email, birthday, address, role) ⇒
        ParticipantData(id, eventId, firstName, lastName, birthday, email,
          address, organisation = None, comment = None, role,
          DateTime.now(), editorName, DateTime.now(), editorName)
    })({
      (p: ParticipantData) ⇒
        Some((p.id, p.firstName, p.lastName, p.emailAddress, p.birthday,
          p.address, p.role))
    }))
  }

  def existingPersonForm(eventId: Long) = {
    Form(mapping(
      "participantId" -> longNumber.verifying(
        "error.person.notExist",
        (participantId: Long) ⇒ PersonService.get.find(participantId).nonEmpty),
      "evaluationId" -> optional(longNumber),
      "role" -> optional(text))({
      (participantId, evaluationId, role) ⇒
        Participant(None, eventId, participantId, evaluationId,
            certificate = None, issued = None, organisation = None,
            comment = None, role = role)
      })({
      (p: Participant) ⇒ Some(p.personId, p.evaluationId, p.role)
      }))
  }

  /**
    * Returns a list of participants without evaluations for a particular event
    *
    * @param eventId Event identifier
    * @return
    */
  def participants(eventId: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit val personWrites = new Writes[Person] {
      def writes(person: Person): JsValue = {
        Json.obj(
          "id" -> person.id.get,
          "name" -> person.fullName)
      }
    }

    implicit handler ⇒ implicit user ⇒
      EventService.get.find(eventId).map { event ⇒
        val participants = event.participants
        val evaluations = evaluationService.findByEvent(eventId)
        Ok(Json.toJson(participants.filterNot(p ⇒ evaluations.exists(e ⇒ Some(e.attendeeId) == p.id))))
      }.getOrElse(NotFound("Unknown event"))
  }

  /**
    * Renders the profile of the person who is a participant
   * @param eventId Event identifier
   * @param personId Person identifier
   */
  def person(eventId: Long, personId: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), eventId) {
    implicit request => implicit handler => implicit user => implicit event =>
      participantService.find(personId, eventId) map { participant =>
        personService.find(participant.personId) map { person =>
          Future.successful(Ok(views.html.v2.participant.personDetails(user, person, eventId)))
        } getOrElse Future.successful(NotFound("Unknown person"))
      } getOrElse Future.successful(NotFound("Unknown participant"))
  }

  /**
   * Returns true if a link to certificate should be shown
   * @param settings Brand settings
   * @param event Event
   * @param status Evaluation status
   */
  protected def showCertificate(settings: Settings, event: Event, status: Option[EvaluationStatus.Value]): Boolean = {
    settings.certificates && !event.free &&
      (status.isEmpty || status.exists(_ == EvaluationStatus.Pending) || status.exists(_ == EvaluationStatus.Approved))
  }

  /**
    * Returns url to a profile of the person who is the participant
    * @param person Person
    * @param eventId Event identifier
    */
  private def personDetailsUrl(person: Person, eventId: Long): String = if (person.virtual)
    routes.Participants.person(eventId, person.identifier).url
  else
    routes.People.details(person.identifier).url

  /**
   * Get JSON with evaluation data
   * @param data Data to convert to JSON
   * @return
   */
  private def evaluation(data: ParticipantView): JsValue = {
    Json.obj(
      "id" -> data.evaluationId,
      "impression" -> data.impression,
      "status" -> data.status.map(status ⇒
        Json.obj(
          "label" -> Messages("models.EvaluationStatus." + status),
          "value" -> status.id)),
      "creation" -> data.date.map(_.toString("yyyy-MM-dd")),
      "handled" -> data.handled.map(_.toString))
  }
}
