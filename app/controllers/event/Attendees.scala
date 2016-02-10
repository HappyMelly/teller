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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers.event

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers._
import models.UserRole.Role
import models.UserRole.Role._
import models._
import models.brand.Settings
import models.event.{Attendee, AttendeeView}
import models.service.Services
import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{MessagesApi, Messages}
import play.api.libs.json.{JsValue, Json, Writes}
import play.twirl.api.Html
import services.TellerRuntimeEnvironment
import views.Countries

import scala.concurrent.Future

/**
  * Created by sery0ga on 04/01/16.
  */
class Attendees @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment,
                                        override val messagesApi: MessagesApi,
                                        val services: Services,
                                        deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, services)(messagesApi, env)
  with Activities
  with BrandAware {

  /**
    * HTML form mapping for creating and editing.
    */
  def form(eventId: Long, editorName: String) = Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> play.api.data.Forms.email,
      "dateOfBirth" -> optional(jodaLocalDate),
      "street1" -> optional(text),
      "street2" -> optional(text),
      "city" -> optional(text),
      "province" -> optional(text),
      "postCode" -> optional(text),
      "country" -> optional(nonEmptyText),
      "role" -> optional(text),
      "recordInfo" -> mapping(
        "created" -> ignored(DateTime.now()),
        "createdBy" -> ignored(editorName),
        "updated" -> ignored(DateTime.now()),
        "updatedBy" -> ignored(editorName))(DateStamp.apply)(DateStamp.unapply))({
      (id, firstName, lastName, email, dateOfBirth, street1, street2, city, province, postCode, country, role, recordInfo) ⇒
        Attendee(id, eventId, None, firstName, lastName, email, dateOfBirth, country, city, street1, street2, province,
          postCode, None, None, None, None, None, role, recordInfo)
    })({
      (p: Attendee) ⇒ Some((p.id, p.firstName, p.lastName, p.email, p.dateOfBirth, p.street_1, p.street_2, p.city,
        p.province, p.postcode, p.countryCode, p.role, p.recordInfo))
    }))

  /**
    * Render a Create page
    *
    * @param eventId An identifier of the event to add participant to
    */
  def add(eventId: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), eventId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      ok(views.html.v2.attendee.form(user, None, eventId, form(eventId, user.person.fullName)))
  }

  /**
    * Adds a new participant to the event from a list of people inside the Teller
    *
    * @param eventId Event identifier
    */
  def create(eventId: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), eventId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      form(eventId, user.name).bindFromRequest.fold(
        errors => Future.successful(BadRequest(views.html.v2.attendee.form(user, None, eventId, errors))),
        data => {
          services.attendeeService.insert(data)
          redirect(controllers.routes.Events.details(eventId), "success" -> "Attendee was added")
        }
      )
  }

  /**
    * Deletes the given attendee
    *
    * @param eventId Event identifier
    * @param attendeeId Attendee identifier
    */
  def delete(eventId: Long, attendeeId: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), eventId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      services.attendeeService.find(attendeeId, eventId) flatMap {
        case None => jsonNotFound("Unknown attendee")
        case Some(attendee) =>
          services.attendeeService.delete(attendeeId, eventId) flatMap { _ =>
            jsonSuccess("Attendee was deleted")
          }
      }
  }

  /**
    * Returns the details of the given participant
    *
    * @param eventId Event identifier
    * @param attendeeId Attendee identifier
    */
  def details(eventId: Long, attendeeId: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), eventId) {
    implicit request => implicit handler => implicit user => implicit event =>
      services.attendeeService.find(attendeeId, eventId) flatMap {
        case None => badRequest(Html("Attendee not found"))
        case Some(attendee) =>
          findEvaluation(attendee.evaluationId) flatMap {
            case None => Future.successful((None, None))
            case Some(evaluation) => identicalEvaluation(evaluation) map { identical =>
              (Some(evaluation), identical) }
          } flatMap { pair =>
            ok(views.html.v2.attendee.details(attendee, pair._1, user.account.isCoordinatorNow, pair._2))
          }
      }
  }

  /**
    * Renders edit form for attendee
    *
    * @param eventId Event identifier
    * @param attendeeId Attendee identifier
    */
  def edit(eventId: Long, attendeeId: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), eventId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      services.attendeeService.find(attendeeId, eventId) flatMap {
        case None => notFound(Html("Attendee not found"))
        case Some(attendee) =>
          if (attendee.personId.isEmpty) {
            val filledForm = form(eventId, user.name).fill(attendee)
            ok(views.html.v2.attendee.form(user, Some(attendeeId), eventId, filledForm))
          } else {
            forbidden(Html("You are not allowed to edit this attendee"))
          }
      }
  }

  /**
    * Renders list of attendees
    *
    * @param brandId Brand identifier
    */
  def index(brandId: Long) = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      roleDiffirentiator(user.account, Some(brandId)) { (view, brands) =>
        ok(views.html.v2.attendee.forBrandCoordinators(user, view.brand, brands))
      } { (view, brands) =>
        ok(views.html.v2.attendee.forFacilitators(user, view.get.brand, brands))
      } {
        redirect(controllers.routes.Dashboard.index())
      }
  }

  /**
    * Returns JSON data about participants together with their evaluations
    * and events
    *
    * @param brandId Brand identifier
    */
  def list(brandId: Long) = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    (for {
      withCoordinators <- services.brandService.findWithCoordinators(brandId)
      withSettings <- services.brandService.findWithSettings(brandId)
    } yield (withCoordinators, withSettings)) flatMap {
      case (None, _) => ok(Json.toJson(List[String]()))
      case (_, None) => ok(Json.toJson(List[String]()))
      case (Some(view), Some(withSettings)) =>
        val account = user.account
        val coordinator = view.coordinators.exists(_._1.id == Some(account.personId))
        implicit val participantViewWrites = new Writes[AttendeeView] {
          def writes(data: AttendeeView): JsValue = {
            val url: String = data.attendee.personId map { personId =>
              controllers.routes.People.details(personId).url
            } getOrElse {
              controllers.routes.Licenses.addForAttendee(data.attendee.identifier, data.attendee.eventId).url
            }
            Json.obj(
              "person" -> Json.obj(
                "url" -> attendeeDetailsUrl(data.attendee, data.event.identifier),
                "name" -> data.attendee.fullName),
              "event" -> Json.obj(
                "id" -> data.event.id,
                "url" -> controllers.routes.Events.details(data.event.id.get).url,
                "title" -> data.event.title,
                "longTitle" -> data.event.longTitle),
              "location" -> s"${data.event.location.city}, ${Countries.name(data.event.location.countryCode)}",
              "schedule" -> data.event.schedule.formatted,
              "evaluation" -> evaluation(data.evaluation),
              "attendee" -> Json.obj(
                "person" -> data.attendee.identifier,
                "event" -> data.event.identifier,
                "license" -> url,
                "certificate" -> Json.obj(
                  "show" -> showCertificate(withSettings.settings, data.event, data.evaluation.map(_.status)),
                  "number" -> data.attendee.certificate)))
          }
        }
        val personId = account.personId
        val result = if (coordinator & user.account.isCoordinatorNow)
          services.attendeeService.findByBrand(withSettings.brand.id)
        else
          services.licenseService.activeLicense(brandId, personId) flatMap {
            case None => Future.successful(List[AttendeeView]())
            case Some(_) =>
              services.eventService.findByFacilitator(personId, withSettings.brand.id) flatMap { events =>
                services.evaluationService.findEvaluationsByEvents(events.map(_.identifier))
              }
          }
        result flatMap { attendees =>
          ok(Json.toJson(attendees))
        }
    }
  }

  /**
    * Returns JSON data about participants together with their evaluations
    */
  def listByEvent(eventId: Long) = AsyncSecuredEventAction(List(Role.Coordinator, Role.Facilitator), eventId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒ implicit event =>
      (for {
        view <- services.brandService.findWithSettings(event.brandId)
        participants <- services.evaluationService.findEvaluationsByEvents(List(eventId))
      } yield (view, participants)) flatMap {
        case (None, participants) => ok(Json.toJson(List[String]()))
        case (Some(view), participants) =>
          implicit val participantViewWrites = new Writes[AttendeeView] {
            def writes(data: AttendeeView): JsValue = {
              Json.obj(
                "person" -> Json.obj(
                  "url" -> attendeeDetailsUrl(data.attendee, data.event.identifier),
                  "name" -> data.attendee.fullName,
                  "id" -> data.attendee.identifier),
                "evaluation" -> evaluation(data.evaluation),
                "participant" -> Json.obj(
                  "person" -> data.attendee.identifier,
                  "event" -> data.event.identifier,
                  "certificate" -> Json.obj(
                    "show" -> showCertificate(view.settings, data.event, data.evaluation.map(_.status)),
                    "number" -> data.attendee.certificate)))
            }
          }
          ok(Json.toJson(participants))
      }
  }

  /**
    * Updates the given attendee
    *
    * @param eventId Event identifier
    * @param attendeeId Attendee identifier
    */
  def update(eventId: Long, attendeeId: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), eventId) {
    implicit request => implicit handler => implicit user => implicit event =>
      form(eventId, user.name).bindFromRequest.fold(
        errors => badRequest(views.html.v2.attendee.form(user, Some(attendeeId), eventId, errors)),
        data =>
          services.attendeeService.find(attendeeId, eventId) flatMap {
            case None => redirect(controllers.routes.Events.details(eventId), "error" -> "Unknown person")
            case Some(attendee) =>
              if (attendee.personId.nonEmpty)
                redirect(controllers.routes.Events.details(eventId), "error" -> "You are not allowed to update this attendee")
              else
                services.attendeeService.update(data.copy(id = attendee.id, personId = attendee.personId)) flatMap { _ =>
                  redirect(controllers.routes.Events.details(eventId), "success" -> "Attendee was successfully updated")
                }

          }
      )
  }

  /**
    * Returns an evaluation if exists
    *
    * @param evaluationId Evaluation identifier
    */
  protected def findEvaluation(evaluationId: Option[Long]): Future[Option[Evaluation]] = evaluationId map { id =>
      services.evaluationService.findWithEvent(id).map {
        case None => None
        case Some(view) => Some(view.eval)
      }
    } getOrElse Future.successful(None)


  /**
    * Returns identical evaluation for the given evaluation
    *
    * @param evaluation Evaluation
    */
  protected def identicalEvaluation(evaluation: Evaluation): Future[Option[Evaluation]] =
    if (evaluation.status == EvaluationStatus.Unconfirmed || evaluation.status == EvaluationStatus.Pending) {
      evaluation.identical(services)
    } else
      Future.successful(None)

  /**
    * Returns true if a link to certificate should be shown
    *
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
    *
    * @param attendee Person
    * @param eventId Event identifier
    */
  private def attendeeDetailsUrl(attendee: Attendee, eventId: Long): Option[String] = attendee.personId.map { personId =>
    controllers.routes.People.details(personId).url
  }

  /**
    * Get JSON with evaluation data
    *
    * @param data Data to convert to JSON
    * @return
    */
  private def evaluation(data: Option[Evaluation]): JsValue = {
    Json.obj(
      "id" -> data.map(_.identifier),
      "impression" -> data.map(_.impression),
      "status" -> data.map(_.status).map(status ⇒
        Json.obj(
          "label" -> Messages("models.EvaluationStatus." + status),
          "value" -> status.id)),
      "creation" -> data.map(_.recordInfo.created).map(_.toString("yyyy-MM-dd")),
      "handled" -> data.map(_.handled.map(_.toString)))
  }
}
