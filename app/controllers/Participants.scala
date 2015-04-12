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
import models._
import models.admin.Translation
import models.service.{ EventService, PersonService, Services }
import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages
import play.api.libs.json._
import play.mvc.Controller
import views.Countries

object Participants extends Controller with Security with Services {

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
        (country: String) ⇒ Countries.all.exists(_._1 == country)))({
        (id, eventId, firstName, lastName, birthday, email, city, country) ⇒
          ParticipantData(id, eventId, firstName, lastName, birthday, email,
            Address(None, None, None, Some(city), None, None, country), organisation = None, comment = None,
            DateTime.now(), userName, DateTime.now(), userName)
      }) ({
        (p: ParticipantData) ⇒
          Some((p.id, p.eventId, p.firstName, p.lastName, p.birthday, p.emailAddress,
            p.address.city.getOrElse(""), p.address.countryCode))
      }))
  }

  def existingPersonForm(implicit user: UserIdentity) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "brandId" -> longNumber(min = 1),
      "eventId" -> longNumber.verifying(
        "error.event.invalid",
        (eventId: Long) ⇒ Event.canManage(eventId, user.account)),
      "participantId" -> longNumber.verifying(
        "error.person.notExist",
        (participantId: Long) ⇒ PersonService.get.find(participantId).nonEmpty),
      "evaluationId" -> optional(longNumber))({
        (id, brandId, eventId, participantId, evaluationId) ⇒
          Participant(id, eventId, participantId, evaluationId,
            certificate = None, issued = None, organisation = None, comment = None)
      })({
        (p: Participant) ⇒
          Some(p.id, p.event.get.brandId, p.eventId,
            p.personId, p.evaluationId)
      }))
  }

  /**
   * Returns a list of participants
   */
  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val account = user.account
      val brands = Brand.findByUser(account)
      val brandId = request.session.get("brandId").map(_.toLong).getOrElse(0L)
      Ok(views.html.participant.index(user, brands, brandId))
  }

  /**
   * Returns JSON data about participants together with their evaluations
   * and events
   *
   * @param brandId Brand identifier
   */
  def participantsByBrand(brandId: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      // TODO: check for a valid brand from Brand.findForUser
      brandService.find(brandId) map { brand ⇒
        val account = user.account
        val en = translationService.find("EN").get
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
              "location" -> Json.obj(
                "country" -> data.event.location.countryCode.toLowerCase,
                "city" -> data.event.location.city),
              "schedule" -> Json.obj(
                "start" -> data.event.schedule.start.toString,
                "end" -> data.event.schedule.end.toString),
              "evaluation" -> evaluation(data, en),
              "actions" -> {
                data.evaluationId match {
                  case Some(id) ⇒ Json.obj(
                    "certificate" -> certificateActions(brand, data, "index"),
                    "evaluation" -> evaluationActions(id, brand, data, account, "index"),
                    "participant" -> participantActions(data, account, "index"))
                  case None ⇒ if (!data.event.archived) {
                    Json.obj(
                      "certificate" -> certificateActions(brand, data, "event"),
                      "evaluation" -> Json.obj(
                        "add" -> {
                          if (account.editor || brand.coordinatorId == account.personId) {
                            routes.Evaluations.add(data.event.id, data.person.id).url
                          } else ""
                        }),
                      "participant" -> participantActions(data, account, "index"))
                  } else {
                    Json.obj(
                      "participant" -> participantActions(data, account, "index"))
                  }
                }
              })
          }
        }
        val personId = account.personId
        //TODO change to brand team
        val participants =
          if (account.editor || brand.coordinatorId == personId) {
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
      EventService.get.find(eventId).map { event ⇒
        val account = user.account
        val brand = brandService.find(event.brandId).get
        val en = translationService.find("EN").get
        implicit val participantViewWrites = new Writes[ParticipantView] {
          def writes(data: ParticipantView): JsValue = {
            Json.obj(
              "person" -> Json.obj(
                "url" -> routes.People.details(data.person.id.get).url,
                "name" -> data.person.fullName,
                "id" -> data.person.id.get),
              "evaluation" -> evaluation(data, en),
              "actions" -> {
                data.evaluationId match {
                  case Some(id) ⇒ Json.obj(
                    "certificate" -> certificateActions(brand, data, "event"),
                    "evaluation" -> evaluationActions(id, brand, data, account, "event"),
                    "participant" -> participantActions(data, account, "event"))
                  case None ⇒ if (!data.event.archived) {
                    Json.obj(
                      "certificate" -> certificateActions(brand, data, "event"),
                      "evaluation" -> Json.obj(
                        "add" -> {
                          if (account.editor || brand.coordinatorId == account.personId) {
                            routes.Evaluations.add(data.event.id, data.person.id).url
                          } else ""
                        }),
                      "participant" -> participantActions(data, account, "event"))
                  } else {
                    Json.obj(
                      "participant" -> participantActions(data, account, "event"))
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
          newPersonForm(account, user.fullName), existingPersonForm(user),
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
            newPersonForm(account, user.fullName), formWithErrors,
            showExistingPersonForm = true, formWithErrors("brandId").value.flatMap(x ⇒ Some(x.toLong)),
            Some(chosenEventId),
            ref))
        },
        participant ⇒ {
          Participant.find(participant.personId, participant.eventId) match {
            case Some(p) ⇒ {
              val account = user.account
              val brands = Brand.findByUser(account)
              val people = personService.findActive
              val chosenEventId = form("eventId").value.map(_.toLong).getOrElse(0L)
              BadRequest(views.html.participant.form(user, None, brands, people,
                newPersonForm(account, user.fullName), form.withError("participantId", "error.participant.exist"),
                showExistingPersonForm = true, form("brandId").value.flatMap(x ⇒ Some(x.toLong)),
                Some(chosenEventId), ref))
            }
            case _ ⇒ {
              Participant.insert(participant)
              val activityObject = Messages("activity.participant.create",
                participant.person.get.fullName,
                participant.event.get.title)
              val activity = Activity.create(user.fullName,
                Activity.Predicate.Created,
                activityObject)
              val route = ref match {
                case Some("event") ⇒ routes.Events.details(participant.eventId).url + "#participant"
                case _ ⇒ routes.Participants.index().url
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
      val form: Form[ParticipantData] = newPersonForm(account, user.fullName).bindFromRequest

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
          val activity = Activity.create(user.fullName,
            Activity.Predicate.Created,
            activityObject)
          val route = ref match {
            case Some("event") ⇒ routes.Events.details(data.eventId).url + "#participant"
            case _ ⇒ routes.Participants.index().url
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
  def delete(eventId: Long, personId: Long, ref: Option[String]) = SecuredDynamicAction("event", "edit") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Participant.find(personId, eventId).map { value ⇒

        val activityObject = Messages("activity.participant.delete", value.person.get.fullName, value.event.get.title)
        value.delete()
        val activity = Activity.create(user.fullName,
          Activity.Predicate.Deleted,
          activityObject)
        val route = ref match {
          case Some("event") ⇒ routes.Events.details(eventId).url + "#participant"
          case _ ⇒ routes.Participants.index().url
        }
        Redirect(route).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  /**
   * Get JSON with evaluation data
   * @param data Data to convert to JSON
   * @param en English translation of Evaluation module
   * @return
   */
  private def evaluation(data: ParticipantView, en: Translation): JsValue = {
    Json.obj(
      "impression" -> data.impression.map { value ⇒
        Json.obj(
          "value" -> value,
          "caption" -> en.impressions.value(value))
      },
      "status" -> data.status.map(status ⇒
        Json.obj(
          "label" -> Messages("models.EvaluationStatus." + status),
          "value" -> status.id)),
      "creation" -> data.date.map(_.toString("yyyy-MM-dd")),
      "handled" -> data.handled.map(_.get.toString),
      "certificate" -> data.certificate.map { id: String ⇒
        Json.obj(
          "id" -> id,
          "url" -> routes.Certificates.certificate(id).url)
      })
  }

  /** Return a list of possible actions for a certificate */
  private def certificateActions(brand: Brand, data: ParticipantView, page: String): JsValue = {
    Json.obj(
      "generate" -> {
        if (brand.generateCert)
          routes.Certificates.create(data.event.id.get, data.person.id.get, Some(page)).url
        else ""
      })
  }

  /** Return a list of possible actions for an evaluation */
  private def evaluationActions(id: Long, brand: Brand, data: ParticipantView, account: UserAccount, page: String): JsValue = {
    Json.obj(
      "approve" -> {
        if (Evaluation.approvable(data.status.get))
          routes.Evaluations.approve(id, Some(page)).url
        else ""
      },
      "reject" -> {
        if (Evaluation.rejectable(data.status.get))
          routes.Evaluations.reject(id, Some(page)).url
        else ""
      },
      "move" -> routes.Evaluations.move(id).url,
      "edit" -> {
        if (account.editor || brand.coordinatorId == account.personId)
          routes.Evaluations.edit(id).url
        else ""
      },
      "view" -> routes.Evaluations.details(id).url,
      "remove" -> routes.Evaluations.delete(id, Some(page)).url)
  }

  /** Return a list of possible actions for a participant */
  private def participantActions(data: ParticipantView, account: UserAccount, page: String): JsValue = {
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
      "removeParticipation" -> routes.Participants.delete(data.event.id.get, data.person.id.get, Some(page)).url)
  }
}
