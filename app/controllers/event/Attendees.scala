package controllers.event

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
import play.api.i18n.Messages
import play.api.libs.json.{JsValue, Json, Writes}
import securesocial.core.RuntimeEnvironment
import views.Countries

import scala.concurrent.Future

/**
  * Created by sery0ga on 04/01/16.
  */
class Attendees(environment: RuntimeEnvironment[ActiveUser])
  extends JsonController
  with Security
  with Services
  with Activities
  with Utilities {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

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
      Future.successful(Ok(views.html.v2.attendee.form(user, None, eventId, form(eventId, user.person.fullName))))
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
          attendeeService.insert(data)
          Future.successful(
            Redirect(controllers.routes.Events.details(eventId)).flashing("success" -> "Attendee was successfully added"))
        }
      )
  }

  /**
    * Deletes the given attendee
    * @param eventId Event identifier
    * @param attendeeId Attendee identifier
    */
  def delete(eventId: Long, attendeeId: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), eventId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      attendeeService.find(attendeeId, eventId) map { attendee =>
        attendeeService.delete(attendeeId, eventId)
        Future.successful(jsonSuccess("Attendee was successfully deleted"))
      } getOrElse Future.successful(jsonNotFound("Unknown attendee"))

  }

  /**
    * Returns the details of the given participant
    * @param eventId Event identifier
    * @param attendeeId Attendee identifier
    */
  def details(eventId: Long, attendeeId: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), eventId) {
    implicit request => implicit handler => implicit user => implicit event =>
      attendeeService.find(attendeeId, eventId) map { attendee =>
        val evaluation = attendee.evaluationId map { evaluationId =>
          evaluationService.findWithEvent(evaluationId).flatMap(x => Some(x.eval))
        } getOrElse None
        val identical = evaluation.map { x =>
          if (x.status == EvaluationStatus.Unconfirmed || x.status == EvaluationStatus.Pending) {
            x.identical()
          } else
            None
        } getOrElse None
        Future.successful(Ok(views.html.v2.attendee.details(attendee, evaluation, user.account.isCoordinatorNow,
          identical)))
      } getOrElse Future.successful(BadRequest("Attendee does not exist"))
  }

  /**
    * Renders edit form for attendee
    * @param eventId Event identifier
    * @param attendeeId Attendee identifier
    */
  def edit(eventId: Long, attendeeId: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), eventId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      attendeeService.find(attendeeId, eventId).map { attendee =>
        if (attendee.personId.isEmpty) {
            val filledForm = form(eventId, user.name).fill(attendee)
            Future.successful(Ok(views.html.v2.attendee.form(user, Some(attendeeId), eventId, filledForm)))
        } else {
          Future.successful(Forbidden("You are not allowed to edit this attendee"))
        }
      } getOrElse Future.successful(NotFound("Unknown attendee"))
  }

  /**
    * Renders list of attendees
    * @param brandId Brand identifier
    */
  def index(brandId: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      roleDiffirentiator(user.account, Some(brandId)) { (view, brands) =>
        Ok(views.html.v2.attendee.forBrandCoordinators(user, view.brand, brands))
      } { (view, brands) =>
        Ok(views.html.v2.attendee.forFacilitators(user, view.get.brand, brands))
      } { Redirect(controllers.routes.Dashboard.index()) }
  }

  /**
    * Returns JSON data about participants together with their evaluations
    * and events
    *
    * @param brandId Brand identifier
    */
  def list(brandId: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    brandService.findWithCoordinators(brandId) map { view ⇒
      val withSettings = brandService.findWithSettings(brandId).get
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
            "evaluation" -> evaluation(data),
            "attendee" -> Json.obj(
              "person" -> data.attendee.identifier,
              "event" -> data.event.identifier,
              "license" -> url,
              "certificate" -> Json.obj(
                "show" -> showCertificate(withSettings.settings, data.event, data.status),
                "number" -> data.attendee.certificate)))
        }
      }
      val personId = account.personId
      val attendees =
        if (coordinator & user.account.isCoordinatorNow) {
          attendeeService.findByBrand(withSettings.brand.id)
        } else if (License.licensedSince(personId, brandId).nonEmpty) {
          val events = eventService.findByFacilitator(personId, withSettings.brand.id).map(_.id.get)
          evaluationService.findEvaluationsByEvents(events)
        } else {
          List[AttendeeView]()
        }
      Ok(Json.toJson(attendees)).withSession("brandId" -> brandId.toString)
    } getOrElse Ok(Json.toJson(List[String]()))
  }

  /**
    * Returns JSON data about participants together with their evaluations
    */
  def listByEvent(eventId: Long) = AsyncSecuredEventAction(List(Role.Coordinator, Role.Facilitator), eventId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒ implicit event =>
      val view = brandService.findWithSettings(event.brandId).get
      implicit val participantViewWrites = new Writes[AttendeeView] {
        def writes(data: AttendeeView): JsValue = {
          Json.obj(
            "person" -> Json.obj(
              "url" -> attendeeDetailsUrl(data.attendee, data.event.identifier),
              "name" -> data.attendee.fullName,
              "id" -> data.attendee.identifier),
            "evaluation" -> evaluation(data),
            "participant" -> Json.obj(
              "person" -> data.attendee.identifier,
              "event" -> data.event.identifier,
              "certificate" -> Json.obj(
                "show" -> showCertificate(view.settings, data.event, data.status),
                "number" -> data.attendee.certificate)))
        }
      }
      val participants = evaluationService.findEvaluationsByEvents(List(eventId))
      Future.successful(Ok(Json.toJson(participants)))
  }

  /**
    * Updates the given attendee
    * @param eventId Event identifier
    * @param attendeeId Attendee identifier
    */
  def update(eventId: Long, attendeeId: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), eventId) {
    implicit request => implicit handler => implicit user => implicit event =>
      attendeeService.find(attendeeId, eventId).map { attendee =>
        if (attendee.personId.isEmpty) {
          form(eventId, user.name).bindFromRequest.fold(
            errors => Future.successful(
              BadRequest(views.html.v2.attendee.form(user, Some(attendeeId), eventId, errors))),
            data => {
              attendeeService.update(data.copy(id = attendee.id, personId = attendee.personId))
              Future.successful(
                Redirect(controllers.routes.Events.details(eventId)).flashing("success" -> "Attendee was successfully updated"))
            }
          )
        } else {
          Future.successful(
            Redirect(controllers.routes.Events.details(eventId)).flashing("error" -> "You are not allowed to update this attendee"))
        }
      } getOrElse Future.successful(
        Redirect(controllers.routes.Events.details(eventId)).flashing("error" -> "Unknown person"))

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
    * @param attendee Person
    * @param eventId Event identifier
    */
  private def attendeeDetailsUrl(attendee: Attendee, eventId: Long): Option[String] = attendee.personId.map { personId =>
    controllers.routes.People.details(personId).url
  }

  /**
    * Get JSON with evaluation data
    * @param data Data to convert to JSON
    * @return
    */
  private def evaluation(data: AttendeeView): JsValue = {
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
