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

import controllers.Forms._
import mail.reminder.EvaluationReminder
import models.UserRole.DynamicRole
import models.UserRole.Role._
import models.event.Comparator
import models.event.Comparator.FieldChange
import models.service.Services
import models.{Location, Schedule, _}
import org.joda.time.LocalDate
import play.api.data.Forms._
import play.api.data._
import play.api.data.format.Formatter
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc._
import securesocial.core.RuntimeEnvironment
import services.integrations.Integrations
import views.Countries

import scala.concurrent.Future

class Events(environment: RuntimeEnvironment[ActiveUser])
    extends Controller
    with Security
    with Services
    with Integrations
    with Activities
    with Utilities {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  val dateRangeFormatter = new Formatter[LocalDate] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {
      // "data" lets you access all form data values
      try {
        val start = LocalDate.parse(data.get("schedule.start").get)
        try {
          val end = LocalDate.parse(data.get("schedule.end").get)
          if (start.isAfter(end)) {
            Left(List(FormError("schedule.start", "error.date.range"), FormError("schedule.end", "error.date.range")))
          } else {
            Right(end)
          }
        } catch {
          case e: IllegalArgumentException ⇒ Left(List(FormError("schedule.end", "Invalid date")))
        }
      } catch {
        // The list is empty because we've already handled a date parse error inside the form (jodaLocalDate formatter)
        case e: IllegalArgumentException ⇒ Left(List())
      }
    }

    override def unbind(key: String, value: LocalDate): Map[String, String] = {
      Map(key -> value.toString)
    }
  }

  /**
   * HTML form mapping for an event’s invoice.
   */
  val invoiceForm = Form(tuple(
    "invoiceBy" -> longNumber,
    "number" -> optional(nonEmptyText)))

  /**
   * HTML form mapping for creating and editing.
   */
  def eventForm = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "eventTypeId" -> longNumber.verifying("Wrong event type", _ > 0),
    "brandId" -> longNumber.verifying("Wrong brand", _ > 0),
    "title" -> text.verifying("Empty title", _.nonEmpty),
    "language" -> mapping(
      "spoken" -> language,
      "secondSpoken" -> optional(language),
      "materials" -> optional(language))(Language.apply)(Language.unapply),
    "location" -> mapping(
      "city" -> text.verifying("Empty city name", _.nonEmpty),
      "country" -> text.verifying("Unknown country", _.nonEmpty))(Location.apply)(Location.unapply),
    "details" -> mapping(
      "description" -> optional(text),
      "specialAttention" -> optional(text))(Details.apply)(Details.unapply),
    "organizer" -> mapping(
      "id" -> longNumber.verifying("Unknown organizer", _ > 0),
      "webSite" -> optional(webUrl),
      "registrationPage" -> optional(text))(Organizer.apply)(Organizer.unapply),
    "schedule" -> mapping(
      "start" -> jodaLocalDate,
      "end" -> of(dateRangeFormatter),
      "hoursPerDay" -> number(1, 24),
      "totalHours" -> number(1))(Schedule.apply)(Schedule.unapply),
    "notPublic" -> default(boolean, false),
    "archived" -> default(boolean, false),
    "confirmed" -> default(boolean, false),
    "free" -> default(boolean, false),
    "followUp" -> boolean,
    "invoice" -> longNumber.verifying("No organization to invoice", _ > 0),
    "facilitatorIds" -> list(longNumber).verifying(
      Messages("error.event.nofacilitators"), (ids: List[Long]) ⇒ ids.nonEmpty))(
      { (id, eventTypeId, brandId, title, language, location, details, organizer,
        schedule, notPublic, archived, confirmed, free, followUp, invoiceTo,
        facilitatorIds) ⇒
        {
          val event = Event(id, eventTypeId, brandId, title, language, location,
            details, organizer, schedule, notPublic, archived, confirmed, free,
            followUp, 0.0f, None)
          val invoice = EventInvoice.empty.copy(eventId = id, invoiceTo = invoiceTo)
          event.facilitatorIds_=(facilitatorIds)
          EventView(event, invoice)
        }
      })({ (view: EventView) ⇒
        Some((view.event.id, view.event.eventTypeId, view.event.brandId,
          view.event.title, view.event.language, view.event.location,
          view.event.details, view.event.organizer, view.event.schedule,
          view.event.notPublic, view.event.archived, view.event.confirmed,
          view.event.free, view.event.followUp, view.invoice.invoiceTo,
          view.event.facilitatorIds))

      }))

  /**
   * Create page.
   */
  def add = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val defaultDetails = Details(Some(""), Some(""))
      val organizer = Organizer(0, Some(""), Some(""))
      val defaultSchedule = Schedule(LocalDate.now(), LocalDate.now().plusDays(1), 8, 0)
      val defaultInvoice = EventInvoice(Some(0), Some(0), 0, Some(0), Some(""))
      val default = Event(None, 0, 0, "", Language("", None, Some("English")),
        Location("", ""), defaultDetails, organizer, defaultSchedule,
        notPublic = false, archived = false, confirmed = false, free = false,
        followUp = true, 0.0f, None)
      val view = EventView(default, defaultInvoice)
      val brands = Brand.findByUser(user.account).filter(_.active)
      Ok(views.html.v2.event.form(user, None, brands, true, eventForm.fill(view)))
  }

  /**
   * Duplicate an event
   * @param id Event Id
   * @return
   */
  def duplicate(id: Long) = SecuredDynamicAction("event", DynamicRole.Facilitator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      eventService.findWithInvoice(id) map { view ⇒
        val brands = Brand.findByUser(user.account)
        Ok(views.html.v2.event.form(user, None, brands, false, eventForm.fill(view)))
      } getOrElse NotFound
  }

  /**
   * Create form submits to this action.
   */
  def create = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val form = eventForm.bindFromRequest
      form.fold(
        formWithErrors ⇒ formError(user, formWithErrors, None),
        view ⇒ {
          validateEvent(view.event, user.account) map { errors ⇒
            formError(user,
              form.withError("facilitatorIds", Messages("error.event.invalidLicense")),
              None)
          } getOrElse {
            val inserted = eventService.insert(view)
            val log = activity(inserted.event, user.person).created.insert()
            sendEmailNotification(view.event, List.empty, log)
            Redirect(routes.Events.index(inserted.event.brandId)).flashing("success" -> log.toString)
          }
        })
  }

  /**
   * Cancel the given event
   * @param id Event ID
   */
  def cancel(id: Long) = SecuredDynamicAction("event", DynamicRole.Facilitator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒

        case class CancellationData(reason: Option[String],
          participants: Option[Int],
          details: Option[String])

        def cancelForm = Form(mapping(
          "reason" -> optional(text),
          "participantNumber" -> optional(number),
          "details" -> optional(text))(CancellationData.apply)(CancellationData.unapply))

        eventService.find(id) map { event ⇒
          if (event.deletable) {
            cancelForm.bindFromRequest.fold(
              failure ⇒
                Redirect(routes.Dashboard.index()).flashing("error" -> "Something goes wrong :("),
              data ⇒ {
                event.cancel(user.person.id.get, data.reason,
                  data.participants, data.details)
                val log = activity(event, user.person).deleted.insert()
                sendEmailNotification(event, List.empty, log)
                Redirect(routes.Dashboard.index()).flashing("success" -> log.toString)
              })
          } else {
            Redirect(routes.Events.details(id)).flashing("error" -> Messages("error.event.nonDeletable"))
          }
        } getOrElse NotFound
  }

  /**
   * Confirm form submits to this action.
   * @param id Event ID
   */
  def confirm(id: Long) = AsyncSecuredDynamicAction("event", DynamicRole.Facilitator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        eventService.find(id) map { event ⇒
          eventService.confirm(id)
          val log = activity(event, user.person).confirmed.insert()
          success(id, log.toString)
        } getOrElse Future.successful(NotFound)
  }

  /**
   * Updates invoice data for the given event
   *
   * @param id Event ID
   * @return
   */
  def invoice(id: Long) = AsyncSecuredDynamicAction("event", DynamicRole.Coordinator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        eventService.findWithInvoice(id) map { view ⇒
          invoiceForm.bindFromRequest.fold(
            formWithErrors ⇒ error(id, "Invoice data are wrong. Please try again"),
            invoiceData ⇒ {
              val (invoiceBy, number) = invoiceData
              orgService.find(invoiceBy) map { _ ⇒
                val invoice = view.invoice.copy(invoiceBy = Some(invoiceBy),
                  number = number)
                eventInvoiceService.update(invoice)
                activity(view.event, user.person).updated.insert()
                success(id, "Invoice data was successfully updated")
              } getOrElse Future.successful(NotFound("Organisation not found"))
            })
        } getOrElse Future.successful(NotFound)
  }

  /**
   * Details page.
   * @param id Event ID
   */
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      eventService.findWithInvoice(id) map { x ⇒
        roleDiffirentiator(user.account, Some(x.event.brandId)) { (brand, brands) =>
          val orgs = user.person.organisations
          val eventType = eventTypeService.find(x.event.eventTypeId).get
          val fees = feeService.findByBrand(x.event.brandId)
          val printableFees = fees.
            map(x ⇒ (Countries.name(x.country), x.fee.toString)).
            sortBy(_._1)
          val event = fees.find(_.country == x.event.location.countryCode) map { y ⇒
            Event.withFee(x.event, y.fee, eventType.maxHours)
          } getOrElse x.event
          Ok(views.html.v2.event.details(user, brand, brands, orgs,
            EventView(event, x.invoice), eventType.name, printableFees))
        } { (brand, brands) =>
          val eventType = eventTypeService.find(x.event.eventTypeId).get
          val fees = feeService.findByBrand(x.event.brandId)
          val printableFees = fees.
            map(x ⇒ (Countries.name(x.country), x.fee.toString)).
            sortBy(_._1)
          val event = fees.find(_.country == x.event.location.countryCode) map { y ⇒
            Event.withFee(x.event, y.fee, eventType.maxHours)
          } getOrElse x.event
          Ok(views.html.v2.event.details(user, brand.get, brands, List(),
            EventView(event, x.invoice), eventType.name, printableFees))
        } { Redirect(routes.Dashboard.index()) }
      } getOrElse NotFound
  }

  /**
   * Edit page.
   * @param id Event ID
   */
  def edit(id: Long) = SecuredDynamicAction("event", DynamicRole.Facilitator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒

        eventService.findWithInvoice(id) map { view ⇒
          val account = user.account
          val brands = Brand.findByUser(account)
          Ok(views.html.v2.event.form(user,
            Some(id),
            brands,
            emptyForm = false,
            eventForm.fill(view)))
        } getOrElse NotFound
  }

  /**
   * Renders list of events
   * @param brandId Brand identifier
   */
  def index(brandId: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      roleDiffirentiator(user.account, Some(brandId)) { (brand, brands) =>
        val facilitators = License.allLicensees(brand.identifier).
          map(l ⇒ (l.identifier, l.fullName)).sortBy(_._2)
        Ok(views.html.v2.event.index(user, brand, brands, facilitators))
      } { (brand, brands) =>
        Ok(views.html.v2.event.index(user, brand.get, brands, List()))
      } { Redirect(routes.Dashboard.index()) }
  }

  /**
   * Get a list of events in JSON format, filtered by parameters
   * @param brandId Brand identifier
   * @param future This flag defines if we want to get future/past events
   * @param public This flag defines if we want to get public/private events
   * @param archived This flag defines if we want to get archived/current events
   * @return
   */
  def events(brandId: Option[Long],
    facilitator: Option[Long],
    future: Option[Boolean],
    public: Option[Boolean],
    archived: Option[Boolean]) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val events = facilitator map {
        eventService.findByFacilitator(_, brandId, future, public, archived)
      } getOrElse {
        eventService.findByParameters(brandId, future, public, archived)
      }
      eventService.applyFacilitators(events)

      val account = user.account
      // we do not show private events of other facilitators to anyone except
      // brand coordinator or Editor
      val filteredEvents: List[Event] = if (account.editor)
        events
      else if (account.coordinator)
        events.filter(!_.notPublic) :::
          events.filter(e ⇒ e.notPublic &&
            account.brands.exists(_.identifier == e.brandId))
      else
        events.filter(!_.notPublic) :::
          events.filter(e ⇒ e.notPublic &&
            e.facilitators.exists(_.identifier == account.personId))

      val views = eventService.withInvoices(filteredEvents)

      implicit val personWrites = new Writes[Person] {
        def writes(data: Person): JsValue = {
          Json.obj(
            "name" -> data.fullName,
            "url" -> routes.People.details(data.id.get).url)
        }
      }
      implicit val eventWrites = new Writes[EventView] {
        def writes(data: EventView): JsValue = {
          Json.obj(
            "event" -> Json.obj(
              "id" -> data.event.id,
              "url" -> routes.Events.details(data.event.id.get).url,
              "title" -> data.event.title),
            "location" -> Json.obj(
              "online" -> data.event.location.online,
              "country" -> data.event.location.countryCode.toLowerCase,
              "city" -> data.event.location.city),
            "facilitators" -> data.event.facilitators,
            "schedule" -> Json.obj(
              "start" -> data.event.schedule.start.toString,
              "end" -> data.event.schedule.end.toString),
            "totalHours" -> data.event.schedule.totalHours,
            "confirmed" -> data.event.confirmed,
            "invoice" -> Json.obj(
              "free" -> data.event.free,
              "invoice" -> (if (data.invoice.invoiceBy.isEmpty) { "No" } else { "Yes" })),
            "actions" -> {
              Json.obj(
                "edit" -> routes.Events.edit(data.event.id.get).url,
                "duplicate" -> routes.Events.duplicate(data.event.id.get).url,
                "cancel" -> routes.Events.cancel(data.event.id.get).url)
            })
        }
      }
      Ok(Json.toJson(views))
  }

  /**
   * Renders form with cancelation reason
   * @param id Event identifier
   */
  def reason(id: Long) = SecuredRestrictedAction(Viewer) { implicit request =>
    implicit handler => implicit user =>
      Ok(views.html.v2.event.reason(id))
  }

  /**
   * Edit form submits to this action.
   * @param id Event ID
   */
  def update(id: Long) = SecuredDynamicAction("event", DynamicRole.Facilitator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val form = eventForm.bindFromRequest
      form.fold(
        formWithErrors ⇒ formError(user, formWithErrors, Some(id)),
        received ⇒ {
          validateEvent(received.event, user.account) map { errors ⇒
            formError(user,
              form.withError("facilitatorIds", Messages("error.event.invalidLicense")),
              Some(id))
          } getOrElse {
            val existingView = eventService.findWithInvoice(id).get

            val updated = received.copy(event = received.event.copy(id = Some(id)),
              invoice = received.invoice.copy(id = existingView.invoice.id))
            updated.event.facilitatorIds_=(received.event.facilitatorIds)

            // it's important to compare before updating as with lazy
            // initialization invoice and facilitators data
            // for an old event will be destroyed
            val changes = Comparator.compare(existingView, updated)
            eventService.update(updated)

            val log = activity(updated.event, user.person).updated.insert()
            sendEmailNotification(updated.event, changes, log)

            Redirect(routes.Events.details(id)).flashing("success" -> log.toString)
          }
        })
  }

  /**
   * Send requests for evaluation to participants of the event
   * @param id Event ID
   */
  def sendRequest(id: Long) = SecuredDynamicAction("event", DynamicRole.Facilitator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      case class EvaluationRequestData(participantIds: List[Long], body: String)
      val form = Form(mapping(
        "participantIds" -> list(longNumber),
        "body" -> nonEmptyText.verifying(
          "The letter's body doesn't contains a link",
          (b: String) ⇒ {
            val url = """https?:\/\/""".r findFirstIn b
            url.isDefined
          }))(EvaluationRequestData.apply)(EvaluationRequestData.unapply)).bindFromRequest

      eventService.find(id).map { event ⇒
        form.fold(
          formWithErrors ⇒ {
            Redirect(routes.Events.details(id)).flashing("error" -> "Provided data are wrong. Please, check a request form.")
          },
          requestData ⇒ {
            val participantIds = event.participants.map(_.id.get)
            if (requestData.participantIds.forall(p ⇒ participantIds.contains(p))) {
              import scala.util.matching.Regex
              val namePattern = new Regex("""(PARTICIPANT_NAME_TOKEN)""", "name")
              val brand = brandService.find(event.brandId).get
              requestData.participantIds.foreach { id ⇒
                val participant = personService.find(id).get
                val body = namePattern replaceAllIn (requestData.body, m ⇒ participant.fullName)
                EvaluationReminder.sendEvaluationRequest(participant, brand, body)
              }

              val activity = Activity.insert(user.name, Activity.Predicate.Sent, event.title)
              Redirect(routes.Events.details(id)).flashing("success" -> activity.toString)
            } else {
              Redirect(routes.Events.details(id)).flashing("error" -> "Some people are not participants of the event.")
            }
          })
      }.getOrElse(NotFound)
  }

  /**
   * Returns none if the given event is valid; otherwise returns a list with errors
   *
   * @param event Event
   * @param account User account
   */
  protected def validateEvent(event: Event, account: UserAccount): Option[List[(String, String)]] = {
    if (checker(account).isBrandFacilitator(event.brandId)) {
      val licenseErrors = validateLicenses(event) map { x ⇒ List(x) } getOrElse List()
      val eventTypeErrors = validateEventType(event) map { x ⇒ List(x) } getOrElse List()
      val errors = licenseErrors ++ eventTypeErrors
      if (errors.isEmpty)
        None
      else
        Some(errors)
    } else {
      Some(List(("brandId", "error.brand.invalid")))
    }
  }

  /**
   * Returns error if none of facilitators has a valid license
   *
   * @param event Event object
   */
  protected def validateLicenses(event: Event): Option[(String, String)] = {
    val validLicensees = licenseService.licensees(event.brandId)
    if (event.facilitatorIds.forall(id ⇒ validLicensees.exists(_.id.get == id))) {
      None
    } else {
      Some(("facilitatorIds", "error.event.invalidLicense"))
    }
  }

  /**
   * Returns error if event type doesn't exist or doesn't belong to the brand
   *
   * @param event Event object
   */
  protected def validateEventType(event: Event): Option[(String, String)] = {
    val eventType = eventTypeService.find(event.eventTypeId)
    eventType map { x ⇒
      if (x.brandId != event.brandId)
        Some(("eventTypeId", "error.eventType.wrongBrand"))
      else
        None
    } getOrElse Some(("eventTypeId", "error.eventType.notFound"))
  }

  /**
   * Returns new resource checker
   *
   * @param account User account
   */
  protected def checker(account: UserAccount): DynamicResourceChecker =
    new DynamicResourceChecker(account)

  /**
   * Returns event form with highlighted errors
   * @param user User object
   * @param form Form with errors
   * @param eventId Event identifier if exists
   */
  protected def formError(user: ActiveUser,
    form: Form[EventView],
    eventId: Option[Long])(implicit request: Request[Any],
      handler: AuthorisationHandler,
      token: play.filters.csrf.CSRF.Token) = {
    val brands = Brand.findByUser(user.account)
    BadRequest(views.html.v2.event.form(user, eventId, brands, false, form))
  }

  /**
   * Sends an e-mail notification for an event to the given recipients
   *
   * @param event Event
   * @param changes Changes if the event was updated
   * @param activity Activity description
   * @param request Request which is passed to view
   */
  protected def sendEmailNotification(event: Event,
    changes: List[FieldChange],
    activity: BaseActivity)(implicit request: RequestHeader): Unit = {

    brandService.findWithCoordinators(event.brandId) foreach { x ⇒
      val recipients = x.coordinators.filter(_._2.notification.event).map(_._1)
      val subject = s"${activity.description} event"
      email.send(recipients.toSet, None, None, subject,
        mail.templates.html.event(event, x.brand, changes).toString, richMessage = true)
    }
  }

  /**
   * Return redirect object with success message for the given event
   *
   * @param id Event identifier
   * @param msg Message
   */
  private def success(id: Long, msg: String) =
    Future.successful(
      Redirect(routes.Events.details(id)).flashing("success" -> msg))

  /**
   * Return redirect object with error message for the given event
   *
   * @param id Event identifier
   * @param msg Message
   */
  private def error(id: Long, msg: String) =
    Future.successful(
      Redirect(routes.Events.details(id)).flashing("error" -> msg))
}
