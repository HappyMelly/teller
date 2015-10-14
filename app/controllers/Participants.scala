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
import models.service.{EventService, PersonService, Services}
import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages
import play.api.libs.json._
import securesocial.core.RuntimeEnvironment
import views.Countries
import views.ViewHelpers.dateInterval

class Participants(environment: RuntimeEnvironment[ActiveUser])
    extends JsonController
    with Security
    with Services
    with Utilities {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  def newPersonForm(account: UserAccount, userName: String) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "eventId" -> longNumber.verifying(
        "error.event.invalid",
        (eventId: Long) ⇒ Event.canManage(eventId, account)),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "birthday" -> optional(jodaLocalDate),
      "emailAddress" -> email,
      "city" -> nonEmptyText,
      "country" -> nonEmptyText.verifying(
        "Unknown country",
        (country: String) ⇒ Countries.all.exists(_._1 == country)),
      "role" -> optional(text))({
        (id, eventId, firstName, lastName, birthday, email, city, country, role) ⇒
          ParticipantData(id, eventId, firstName, lastName, birthday, email,
            Address(None, None, None, Some(city), None, None, country),
            organisation = None, comment = None, role,
            DateTime.now(), userName, DateTime.now(), userName)
      })({
        (p: ParticipantData) ⇒
          Some((p.id, p.eventId, p.firstName, p.lastName, p.birthday, p.emailAddress,
            p.address.city.getOrElse(""), p.address.countryCode, p.role))
      }))
  }

  def existingPersonForm(implicit user: ActiveUser) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "brandId" -> longNumber(min = 1),
      "eventId" -> longNumber.verifying(
        "error.event.invalid",
        (eventId: Long) ⇒ Event.canManage(eventId, user.account)),
      "participantId" -> longNumber.verifying(
        "error.person.notExist",
        (participantId: Long) ⇒ PersonService.get.find(participantId).nonEmpty),
      "evaluationId" -> optional(longNumber),
      "role" -> optional(text))({
        (id, brandId, eventId, participantId, evaluationId, role) ⇒
          Participant(id, eventId, participantId, evaluationId,
            certificate = None, issued = None, organisation = None,
            comment = None, role = role)
      })({
        (p: Participant) ⇒
          Some(p.id, p.event.get.brandId, p.eventId,
            p.personId, p.evaluationId, p.role)
      }))
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
        Ok(views.html.v2.participant.index(user, brand, brands))
      } { Redirect(routes.Dashboard.index()) }
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
        val coordinator = account.editor || x.coordinators.exists(_._1.id == Some(account.personId))
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
          if (coordinator) {
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
        val coordinator = account.editor || x.coordinators.exists(_._1.id == Some(account.personId))
        implicit val participantViewWrites = new Writes[ParticipantView] {
          def writes(data: ParticipantView): JsValue = {
            Json.obj(
              "person" -> Json.obj(
                "url" -> routes.People.details(data.person.id.get).url,
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
   * Returns the details of the given participant
   * @param eventId Event identifier
   * @param personId Person identifier
   * @return
   */
  def details(eventId: Long, personId: Long) = SecuredDynamicAction("event", "add") {
    implicit request => implicit handler => implicit user =>
      participantService.find(personId, eventId) map { participant =>
        val evaluation = participant.evaluationId map { evaluationId =>
          evaluationService.find(evaluationId).flatMap(x => Some(x.eval))
        } getOrElse None
        val virtual = personService.find(personId).map(_.virtual).getOrElse(true)
        Ok(views.html.v2.participant.details(participant, evaluation, virtual, user.account.isCoordinatorNow))
      } getOrElse BadRequest("Participant does not exist")
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
   * Render a Create page
   *
   * @param brandId Brand id to add participant to
   * @param eventId An identifier of the event to add participant to
   * @param ref An identifier of a page where a user should be redirected
   * @return
   */
  def add(brandId: Option[Long],
    eventId: Option[Long],
    ref: Option[String]) = SecuredDynamicAction("event", "add") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒

        val account = user.account
        val brands = Brand.findByUser(account)
        val people = personService.findActive
        val selectedBrand = brandId.getOrElse {
          request.session.get("brandId").map(_.toLong).getOrElse(0L)
        }
        Ok(views.html.participant.form(user, id = None, brands, people,
          newPersonForm(account, user.name), existingPersonForm(user),
          showExistingPersonForm = true, Some(selectedBrand), eventId, ref))
  }

  /**
   * Add a new participant to the event from a list of people inside the Teller
   *
   * @param ref An identifier of a page where a user should be redirected
   * @return
   */
  def create(ref: Option[String]) = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val form: Form[Participant] = existingPersonForm.bindFromRequest

      form.fold(
        formWithErrors ⇒ {
          val account = user.account
          val brands = Brand.findByUser(account)
          val people = personService.findActive
          val chosenEventId = formWithErrors("eventId").value.map(_.toLong).getOrElse(0L)
          BadRequest(views.html.participant.form(user, None, brands, people,
            newPersonForm(account, user.name), formWithErrors,
            showExistingPersonForm = true, formWithErrors("brandId").value.flatMap(x ⇒ Some(x.toLong)),
            Some(chosenEventId),
            ref))
        },
        participant ⇒ {
          participantService.find(participant.personId, participant.eventId) match {
            case Some(p) ⇒ {
              val account = user.account
              val brands = Brand.findByUser(account)
              val people = personService.findActive
              val chosenEventId = form("eventId").value.map(_.toLong).getOrElse(0L)
              BadRequest(views.html.participant.form(user, None, brands, people,
                newPersonForm(account, user.name), form.withError("participantId", "error.participant.exist"),
                showExistingPersonForm = true, form("brandId").value.flatMap(x ⇒ Some(x.toLong)),
                Some(chosenEventId), ref))
            }
            case _ ⇒ {
              Participant.insert(participant)
              val activityObject = Messages("activity.participant.create",
                participant.person.get.fullName,
                participant.event.get.title)
              val activity = Activity.create(user.name,
                Activity.Predicate.Created,
                activityObject)
              val route = ref match {
                case Some("event") ⇒ routes.Events.details(participant.eventId).url + "#participant"
                case _ ⇒ routes.Dashboard.index().url
              }
              Redirect(route).flashing("success" -> activity.toString)
            }
          }
        })
  }

  /**
   * Add a new person to the system and a new participant to the event
   *
   * @param ref An identifier of a page where a user should be redirected
   * @return
   */
  def createParticipantAndPerson(ref: Option[String]) = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val account = user.account
      val form: Form[ParticipantData] = newPersonForm(account, user.name).bindFromRequest

      form.fold(
        formWithErrors ⇒ {
          val people = personService.findActive
          val brands = Brand.findByUser(account)
          val chosenEventId = formWithErrors("eventId").value.map(_.toLong).getOrElse(0L)
          BadRequest(views.html.participant.form(user, None, brands, people,
            formWithErrors, existingPersonForm(user),
            showExistingPersonForm = false,
            formWithErrors("brandId").value.flatMap(x ⇒ Some(x.toLong)),
            Some(chosenEventId), ref))
        },
        data ⇒ {
          Participant.create(data)
          val activityObject = Messages("activity.participant.create",
            data.firstName + " " + data.lastName,
            data.event.get.title)
          val activity = Activity.create(user.name,
            Activity.Predicate.Created,
            activityObject)
          val route = ref match {
            case Some("event") ⇒ routes.Events.details(data.eventId).url + "#participant"
            case _ ⇒ routes.Dashboard.index().url
          }
          Redirect(route).flashing("success" -> activity.toString)
        })
  }

  /**
   * Delete a participant from the event
   * @param eventId Event identifier
   * @param personId Person identifier
   * @param ref An identifier of a page where a user should be redirected
   * @return
   */
  def delete(eventId: Long, personId: Long, ref: Option[String]) = SecuredDynamicAction("event", DynamicRole.Facilitator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
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
   * Get JSON with evaluation data
   * @param data Data to convert to JSON
   * @return
   */
  private def evaluation(data: ParticipantView): JsValue = {
    Json.obj(
      "impression" -> data.impression,
      "status" -> data.status.map(status ⇒
        Json.obj(
          "label" -> Messages("models.EvaluationStatus." + status),
          "value" -> status.id)),
      "creation" -> data.date.map(_.toString("yyyy-MM-dd")),
      "handled" -> data.handled.map(_.toString))
  }
}
