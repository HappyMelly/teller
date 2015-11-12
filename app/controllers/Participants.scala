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

import models.UserRole.Role._
import models.UserRole.Role
import models._
import models.service.{EventService, PersonService, Services}
import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages
import play.api.libs.json._
import scala.concurrent.Future
import securesocial.core.RuntimeEnvironment
import views.Countries
import views.ViewHelpers.dateInterval

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
   * Render a Create page
   *
   * @param eventId An identifier of the event to add participant to
   */
  def add(eventId: Long) = AsyncSecuredEventAction(eventId, Role.Facilitator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒ implicit event =>
        val people = personService.findActive
        Future.successful(
          Ok(views.html.v2.participant.form(user, eventId, people,
            newPersonForm(eventId, user.name), existingPersonForm(eventId),
            showExistingPersonForm = true)))
  }

  /**
   * Adds a new participant to the event from a list of people inside the Teller
   *
   * @param eventId Event identifier
   */
  def create(eventId: Long) = AsyncSecuredEventAction(eventId, Role.Facilitator) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      val form: Form[Participant] = existingPersonForm(eventId).bindFromRequest

      form.fold(
        formWithErrors ⇒ {
          val people = personService.findActive
          Future.successful(
            BadRequest(views.html.v2.participant.form(user, eventId, people,
              newPersonForm(eventId, user.name), formWithErrors,
              showExistingPersonForm = true)))
        },
        participant ⇒ {
          participantService.find(participant.personId, eventId) match {
            case Some(p) ⇒
              val people = personService.findActive
              Future.successful(
                BadRequest(views.html.v2.participant.form(user, eventId, people,
                  newPersonForm(eventId, user.name), form.withError("participantId", "error.participant.exist"),
                  showExistingPersonForm = true)))
            case _ ⇒
              Participant.insert(participant)
              val activityObject = Messages("activity.participant.create",
                participant.person.get.fullName,
                event.title)
              val activity = Activity.create(user.name,
                Activity.Predicate.Created,
                activityObject)
              val route = routes.Events.details(eventId).url
              Future.successful(
                Redirect(route).flashing("success" -> activity.toString))
          }
        })
  }

  /**
   * Adds a new person to the system and a new participant to the given event
   *
   * @param eventId Event identifier
   */
  def createParticipantAndPerson(eventId: Long) = AsyncSecuredEventAction(eventId, Role.Facilitator) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>

      newPersonForm(eventId, user.name).bindFromRequest.fold(
        formWithErrors ⇒ {
          val people = personService.findActive
          Future.successful(
            BadRequest(views.html.v2.participant.form(user, eventId, people,
            formWithErrors, existingPersonForm(eventId),
            showExistingPersonForm = false)))
        },
        data ⇒ {
          Participant.create(data)
          val activityObject = Messages("activity.participant.create",
            data.firstName + " " + data.lastName,
            data.event.get.title)
          val activity = Activity.create(user.name,
            Activity.Predicate.Created,
            activityObject)
          val route = routes.Events.details(eventId).url
          Future.successful(
            Redirect(route).flashing("success" -> activity.toString))
        })
  }

  /**
   * Delete a participant from the event
   * @param eventId Event identifier
   * @param personId Person identifier
   * @param ref An identifier of a page where a user should be redirected
   * @return
   */
  def delete(eventId: Long, personId: Long, ref: Option[String]) = SecuredEventAction(eventId, Role.Facilitator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒ implicit event =>
        participantService.find(personId, eventId).map { value ⇒

          val activityObject = Messages("activity.participant.delete", value.person.get.fullName, value.event.get.title)
          value.delete()
          val activity = Activity.create(user.name,
            Activity.Predicate.Deleted,
            activityObject)
          val route = ref match {
            case Some("event") ⇒ routes.Events.details(eventId).url + "#participant"
            case _ ⇒ routes.Dashboard.index().url
          }
          Redirect(route).flashing("success" -> activity.toString)
        }.getOrElse(NotFound)
  }

  /**
   * Deletes the person who is a participant. Only virtual people could be
   *  deleted by this method
   * @param eventId Event identifier
   * @param personId Person identifier
   */
  def deletePerson(eventId: Long, personId: Long) = AsyncSecuredEventAction(eventId, Role.Facilitator) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      personService.find(personId) map { person ⇒
        if (person.virtual) {
          personService.delete(personId)
          val log = activity(person, user.person).deleted.insert()
          Future.successful(jsonOk(Json.obj("redirect" -> routes.Events.details(eventId).url)))
        } else {
          Future.successful(Forbidden("You are not allowed to delete this person"))
        }
      } getOrElse Future.successful(jsonNotFound("Unknown person"))

  }

  /**
   * Returns the details of the given participant
   * @param eventId Event identifier
   * @param personId Person identifier
   */
  def details(eventId: Long, personId: Long) = AsyncSecuredEventAction(eventId, Role.Facilitator) {
    implicit request => implicit handler => implicit user => implicit event =>
      participantService.find(personId, eventId) map { participant =>
        val evaluation = participant.evaluationId map { evaluationId =>
          evaluationService.findWithEvent(evaluationId).flatMap(x => Some(x.eval))
        } getOrElse None
        val identical = evaluation.map { x =>
          if (x.status == EvaluationStatus.Unconfirmed || x.status == EvaluationStatus.Pending) {
            x.identical()
          } else
            None
        } getOrElse None
        val virtual = personService.find(personId).map(_.virtual).getOrElse(true)
        Future.successful(Ok(views.html.v2.participant.details(participant,
          evaluation,
          virtual,
          user.account.isCoordinatorNow,
          identical)))
      } getOrElse Future.successful(BadRequest("Participant does not exist"))
  }

  /**
   * Renders edit form for the person who is also a participant. Only virtual
   *  people could be edited through this form
   * @param eventId Event identifier
   * @param personId Person identifier
   */
  def edit(eventId: Long, personId: Long) = AsyncSecuredEventAction(eventId, Role.Facilitator) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      personService.findComplete(personId) map { person ⇒
        if (person.virtual) {
          participantService.find(personId, eventId) map { participant =>
            val data = ParticipantData(person.id, eventId, person.firstName,
              person.lastName, person.birthday, person.socialProfile.email,
              person.address, participant.organisation, None, participant.role,
              person.dateStamp.created, person.dateStamp.createdBy,
              person.dateStamp.updated, person.dateStamp.updatedBy)
            val form = personForm(eventId, user.name).fill(data)
            Future.successful(Ok(views.html.v2.participant.editForm(user, personId, eventId, form)))
          } getOrElse Future.successful(NotFound("Unknown participant"))
        } else {
          Future.successful(Forbidden("You are not allowed to edit this person"))
        }
      } getOrElse Future.successful(NotFound("Unknown person"))
  }

  /**
   * Renders list of participants
   * @param brandId Brand identifier
   */
  def index(brandId: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      roleDiffirentiator(user.account, Some(brandId)) { (brand, brands) =>
        Ok(views.html.v2.participant.index(user, brand, brands))
      } { (brand, brands) =>
        Ok(views.html.v2.participant.index(user, brand.get, brands))
      } { Redirect(routes.Dashboard.index()) }
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
        Ok(Json.toJson(participants.filterNot(p ⇒ evaluations.exists(e ⇒ Some(e.personId) == p.id))))
      }.getOrElse(NotFound("Unknown event"))
  }

  /**
   * Returns JSON data about participants together with their evaluations
   * and events
   *
   * @param brandId Brand identifier
   */
  def participantsByBrand(brandId: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      brandService.findWithCoordinators(brandId) map { x ⇒
        val brand = x.brand
        val account = user.account
        val coordinator = x.coordinators.exists(_._1.id == Some(account.personId))
        implicit val participantViewWrites = new Writes[ParticipantView] {
          def writes(data: ParticipantView): JsValue = {
            Json.obj(
              "person" -> Json.obj(
                "url" -> personDetailsUrl(data.person, data.event.identifier),
                "name" -> data.person.fullName),
              "event" -> Json.obj(
                "id" -> data.event.id,
                "url" -> routes.Events.details(data.event.id.get).url,
                "title" -> data.event.title,
                "longTitle" -> data.event.longTitle),
              "location" -> s"${data.event.location.city}, ${Countries.name(data.event.location.countryCode)}",
              "schedule" -> dateInterval(data.event.schedule.start, data.event.schedule.end),
              "evaluation" -> evaluation(data),
              "participant" -> Json.obj(
                "person" -> data.person.identifier,
                "event" -> data.event.identifier,
                "certificate" -> Json.obj(
                  "show" -> showCertificate(brand, data.event, data.status),
                  "number" -> data.certificate)))
          }
        }
        val personId = account.personId
        val participants =
          if (coordinator & user.account.isCoordinatorNow) {
            Participant.findByBrand(brand.id)
          } else if (License.licensedSince(personId, brand.id.get).nonEmpty) {
            val events = eventService.findByFacilitator(personId, brand.id).map(_.id.get)
            Participant.findEvaluationsByEvents(events)
          } else {
            List[ParticipantView]()
          }
        Ok(Json.toJson(participants)).withSession("brandId" -> brand.id.get.toString)
      } getOrElse Ok(Json.toJson(List[String]()))
  }

  /**
   * Returns JSON data about participants together with their evaluations
   */
  def participantsByEvent(eventId: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      eventService.find(eventId).map { event ⇒
        val account = user.account
        val x = brandService.findWithCoordinators(event.brandId).get
        val brand = x.brand
        val coordinator = x.coordinators.exists(_._1.id == Some(account.personId))
        implicit val participantViewWrites = new Writes[ParticipantView] {
          def writes(data: ParticipantView): JsValue = {
            Json.obj(
              "person" -> Json.obj(
                "url" -> personDetailsUrl(data.person, data.event.identifier),
                "name" -> data.person.fullName,
                "id" -> data.person.id.get),
              "evaluation" -> evaluation(data),
              "participant" -> Json.obj(
                "person" -> data.person.identifier,
                "event" -> data.event.identifier,
                "certificate" -> Json.obj(
                  "show" -> showCertificate(brand, data.event, data.status),
                  "number" -> data.certificate)))
          }
        }
        val participants = Participant.findByEvent(eventId)
        Ok(Json.toJson(participants))
      }.getOrElse(NotFound("Unknown brand"))
  }

  /**
   * Renders the profile of the person who is a participant
   * @param eventId Event identifier
   * @param personId Person identifier
   */
  def person(eventId: Long, personId: Long) = AsyncSecuredEventAction(eventId, Role.Facilitator) {
    implicit request => implicit handler => implicit user => implicit event =>
      participantService.find(personId, eventId) map { participant =>
        personService.find(participant.personId) map { person =>
          Future.successful(Ok(views.html.v2.participant.personDetails(user, person, eventId)))
        } getOrElse Future.successful(NotFound("Unknown person"))
      } getOrElse Future.successful(NotFound("Unknown participant"))
  }

  /**
   * Updates the given person who is also a participant of the given event.
   *  Only virtual people could be updated this way.
   * @param eventId Event identifier
   * @param personId Person identifier
   */
  def update(eventId: Long, personId: Long) = AsyncSecuredEventAction(eventId, Role.Facilitator) {
    implicit request => implicit handler => implicit user => implicit event =>
      personService.findComplete(personId) map { person ⇒
        if (person.virtual) {
          participantService.find(personId, eventId) map { participant =>
            personForm(eventId, user.name).bindFromRequest.fold(
              errors => Future.successful(
                BadRequest(views.html.v2.participant.editForm(user, personId, eventId, errors))
              ),
              data => {
                val updated = person.copy(firstName = data.firstName,
                  lastName = data.lastName, birthday = data.birthday)
                updated.address_=(data.address)
                updated.socialProfile_=(updated.socialProfile.copy(email = data.emailAddress))
                personService.update(updated)
                Future.successful(
                  Redirect(routes.Participants.person(eventId, personId)).flashing("success" -> "Participant was successfully updated"))
              }
            )
          } getOrElse Future.successful(
            Redirect(routes.Events.details(eventId)).flashing("error" -> "Unknown participant"))
        } else {
          Future.successful(
            Redirect(routes.Events.details(eventId)).flashing("error" -> "You are not allowed to update this person"))
        }
      } getOrElse Future.successful(
        Redirect(routes.Events.details(eventId)).flashing("error" -> "Unknown person"))
  }

  /**
   * Returns true if a link to certificate should be shown
   * @param brand Brand
   * @param event Event
   * @param status Evaluation status
   */
  protected def showCertificate(brand: Brand, event: Event, status: Option[EvaluationStatus.Value]): Boolean = {
    brand.generateCert && !event.free &&
      (status.exists(_ == EvaluationStatus.Pending) || status.exists(_ == EvaluationStatus.Approved))
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
