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

import be.objectify.deadbolt.scala.cache.HandlerCache
import controllers.Forms._
import mail.reminder.EvaluationReminder
import models.UserRole.Role
import models.event.Comparator
import models.event.Comparator.FieldChange
import models.service.Services
import models.{Location, Schedule, _}
import org.joda.time.LocalDate
import play.api.data.Forms._
import play.api.data._
import play.api.data.format.Formatter
import play.api.i18n.{MessagesApi, I18nSupport, Messages}
import play.api.libs.json._
import play.api.mvc._
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions, DeadboltHandler}
import services.TellerRuntimeEnvironment
import services.integrations.Integrations
import views.Countries

import scala.concurrent.Future

class Events @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment,
                                     val messagesApi: MessagesApi,
                                     deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder)
  with Integrations
  with Activities
  with Utilities
  with I18nSupport {

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
  def add = AsyncSecuredRestrictedAction(List(Role.Coordinator, Role.Facilitator)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      brandService.findByUser(user.account) flatMap { brands =>
        val defaultDetails = Details(Some(""), Some(""))
        val organizer = Organizer(0, Some(""), Some(""))
        val defaultSchedule = Schedule(LocalDate.now(), LocalDate.now().plusDays(1), 8, 0)
        val defaultInvoice = EventInvoice(Some(0), Some(0), 0, Some(0), Some(""))
        val default = Event(None, 0, 0, "", Language("", None, Some("English")),
          Location("", ""), defaultDetails, organizer, defaultSchedule,
          notPublic = false, archived = false, confirmed = false, free = false,
          followUp = true, 0.0f, None)
        val view = EventView(default, defaultInvoice)
        ok(views.html.v2.event.form(user, None, brands.filter(_.active), true, eventForm.fill(view)))

      }
  }

  /**
   * Duplicate an event
    *
    * @param id Event Id
   * @return
   */
  def duplicate(id: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), id) { implicit request ⇒
    implicit handler => implicit user ⇒ implicit event =>
      (for {
        e <- eventService.findWithInvoice(id)
        b <- brandService.findByUser(user.account)
      } yield (e, b)) flatMap {
        case (None, _) => notFound("Event not found")
        case (Some(view), brands) =>
          ok(views.html.v2.event.form(user, None, brands, false, eventForm.fill(view)))
      }
  }

  /**
   * Create form submits to this action.
   */
  def create = AsyncSecuredRestrictedAction(List(Role.Coordinator, Role.Facilitator)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val form = eventForm.bindFromRequest
      form.fold(
        formWithErrors ⇒ formError(user, formWithErrors, None),
        view ⇒ {
          validateEvent(view.event, user.account) flatMap {
            case Some(errors) =>
              formError(user, form.withError("facilitatorIds", Messages("error.event.invalidLicense")), None)
            case None =>
              eventService.insert(view) flatMap { inserted =>
                val log = activity(inserted.event, user.person).created.insert()
                sendEmailNotification(view.event, List.empty, log)
                redirect(routes.Events.index(inserted.event.brandId), "success" -> "Event was added")
              }
          }
        })
  }

  /**
   * Cancel the given event
    *
    * @param id Event ID
   */
  def cancel(id: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>

        case class CancellationData(reason: Option[String],
          participants: Option[Int],
          details: Option[String])

        def cancelForm = Form(mapping(
          "reason" -> optional(text),
          "participantNumber" -> optional(number),
          "details" -> optional(text))(CancellationData.apply)(CancellationData.unapply))

      if (event.deletable) {
        cancelForm.bindFromRequest.fold(
          failure ⇒ redirect(routes.Dashboard.index(), "error" -> "Something goes wrong :("),
          data ⇒ {
            event.cancel(user.person.id.get, data.reason,
              data.participants, data.details)
            val log = activity(event, user.person).deleted.insert()
            sendEmailNotification(event, List.empty, log)
            redirect(routes.Dashboard.index(), "success" -> "Event was cancelled")
          })
      } else {
        redirect(routes.Events.details(id), "error" -> Messages("error.event.nonDeletable"))
      }
  }

  /**
   * Confirm form submits to this action.
    *
    * @param id Event ID
   */
  def confirm(id: Long) = AsyncSecuredEventAction(List(Role.Facilitator), id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒ implicit event =>
      eventService.confirm(id) flatMap { _ =>
        val log = activity(event, user.person).confirmed.insert()
        success(id, log.toString)
      }
  }

  /**
   * Updates invoice data for the given event
   *
   * @param id Event ID
   * @return
   */
  def invoice(id: Long) = AsyncSecuredEventAction(List(Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      eventService.findWithInvoice(id) flatMap {
        case None => notFound("")
        case Some(view) =>
          invoiceForm.bindFromRequest.fold(
            formWithErrors ⇒ error(id, "Invoice data are wrong. Please try again"),
            invoiceData ⇒ {
              val (invoiceBy, number) = invoiceData
              orgService.find(invoiceBy) flatMap {
                case None => notFound("Organisation not found")
                case Some(_) =>
                  val invoice = view.invoice.copy(invoiceBy = Some(invoiceBy), number = number)
                  eventInvoiceService.update(invoice)
                  activity(view.event, user.person).updated.insert()
                  success(id, "Invoice data was successfully updated")
              }
            })
      }
  }

  /**
    * Details page.
    *
    * @param id Event ID
   */
  def details(id: Long) = AsyncSecuredRestrictedAction(List(Role.Coordinator, Role.Facilitator)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      eventService.findWithInvoice(id) flatMap {
        case None => notFound("")
        case Some(x) =>
          (for {
            eventType <- eventTypeService.find(x.event.eventTypeId)
            fees <- feeService.findByBrand(x.event.brandId)
          } yield (eventType, fees)) flatMap { case (Some(eventType), fees) =>
            roleDiffirentiator(user.account, Some(x.event.brandId)) { (view, brands) =>
              val orgs = user.person.organisations
              val printableFees = fees.
                map(x ⇒ (Countries.name(x.country), x.fee.toString)).
                sortBy(_._1)
              val event = fees.find(_.country == x.event.location.countryCode) map { y ⇒
                Event.withFee(x.event, y.fee, eventType.maxHours)
              } getOrElse x.event
              ok(views.html.v2.event.details(user, view.brand, brands, orgs,
                EventView(event, x.invoice), eventType.name, printableFees))
            } { (view, brands) =>
              val printableFees = fees.
                map(x ⇒ (Countries.name(x.country), x.fee.toString)).
                sortBy(_._1)
              val event = fees.find(_.country == x.event.location.countryCode) map { y ⇒
                Event.withFee(x.event, y.fee, eventType.maxHours)
              } getOrElse x.event
              ok(views.html.v2.event.details(user, view.get.brand, brands, List(),
                EventView(event, x.invoice), eventType.name, printableFees))
            } {
              redirect(routes.Dashboard.index())
            }
          }

      }
  }

  def detailsButtons(id: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒ implicit event =>
      ok(views.html.v2.event.detailsButtons(event))
  }

  /**
   * Edit page.
    *
    * @param id Event ID
   */
  def edit(id: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      (for {
        e <- eventService.findWithInvoice(id)
        b <- brandService.findByUser(user.account)
      } yield (e, b)) flatMap {
        case (None, _) => notFound("Event not found")
        case (Some(view), brands) =>
          ok(views.html.v2.event.form(user, None, brands, emptyForm = false, eventForm.fill(view)))
      }
  }

  /**
   * Renders list of events
    *
    * @param brandId Brand identifier
   */
  def index(brandId: Long) = AsyncSecuredRestrictedAction(List(Role.Facilitator, Role.Coordinator)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      roleDiffirentiator(user.account, Some(brandId)) { (view, brands) =>
        licenseService.allLicensees(brandId) flatMap { facilitators =>
          val names = facilitators.map(l ⇒ (l.identifier, l.fullName)).sortBy(_._2)
          ok(views.html.v2.event.index(user, view.brand, brands, names))
        }
      } { (view, brands) =>
        ok(views.html.v2.event.index(user, view.get.brand, brands, List()))
      } { redirect(routes.Dashboard.index()) }
  }

  /**
   * Get a list of events in JSON format, filtered by parameters
    *
    * @param brandId Brand identifier
   * @param future This flag defines if we want to get future/past events
   * @param public This flag defines if we want to get public/private events
   * @param archived This flag defines if we want to get archived/current events
   * @return
   */
  def events(brandId: Long,
             facilitator: Option[Long],
             future: Option[Boolean],
             public: Option[Boolean],
             archived: Option[Boolean]) = AsyncSecuredRestrictedAction(Role.Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
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
              "countryName" -> data.event.location.countryName,
              "city" -> data.event.location.city),
            "facilitators" -> data.event.facilitators,
            "schedule" -> Json.obj(
              "start" -> data.event.schedule.start.toString,
              "end" -> data.event.schedule.end.toString,
              "formatted" -> data.event.schedule.formatted),
            "totalHours" -> data.event.schedule.totalHours,
            "confirmed" -> data.event.confirmed,
            "invoice" -> Json.obj(
              "free" -> data.event.free,
              "invoice" -> (if (data.invoice.invoiceBy.isEmpty) { "No" } else { "Yes" })),
            "actions" -> {
              Json.obj(
                "event_id" -> data.event.id,
                "edit" -> routes.Events.edit(data.event.id.get).url,
                "duplicate" -> routes.Events.duplicate(data.event.id.get).url,
                "cancel" -> routes.Events.cancel(data.event.id.get).url)
            },
            "materials" -> data.event.materialsLanguage
          )
        }
      }

      roleDiffirentiator(user.account, Some(brandId)) { (brand, brands) =>
        val result = facilitator map {
          eventService.findByFacilitator(_, Some(brandId), future, public, archived)
        } getOrElse {
          eventService.findByParameters(Some(brandId), future, public, archived)
        }
        result flatMap { events =>
          eventService.applyFacilitators(events)
          eventService.withInvoices(events) flatMap { views =>
            ok(Json.toJson(views))
          }
        }
      } { (brand, brands) =>
        eventService.findByFacilitator(user.person.identifier, Some(brandId), future, public, archived) flatMap { events =>
          eventService.applyFacilitators(events)
          eventService.withInvoices(events) flatMap { views =>
            ok(Json.toJson(views))
          }
        }
      } {
        ok(Json.toJson(List[EventView]()))
      }
  }

  /**
    * Renders form with cancellation reason
    *
    * @param id Event identifier
   */
  def reason(id: Long) = SecuredRestrictedAction(Role.Viewer) { implicit request =>
    implicit handler => implicit user =>
      Ok(views.html.v2.event.reason(id))
  }

  /**
   * Edit form submits to this action.
    *
    * @param id Event ID
   */
  def update(id: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒ implicit event =>

      val form = eventForm.bindFromRequest
      form.fold(
        errors ⇒ formError(user, errors, Some(id)),
        received ⇒ {
          validateEvent(received.event, user.account) flatMap {
            case Some(errors) ⇒
              formError(user, form.withError("facilitatorIds", Messages("error.event.invalidLicense")), Some(id))
            case None =>
              eventService.findWithInvoice(id) flatMap { case Some(view) =>
                val updated = received.copy(event = received.event.copy(id = Some(id)),
                  invoice = received.invoice.copy(id = view.invoice.id))
                updated.event.facilitatorIds_=(received.event.facilitatorIds)

                // it's important to compare before updating as with lazy
                // initialization invoice and facilitators data
                // for an old event will be destroyed
                val changes = Comparator.compare(view, updated)
                eventService.update(updated)

                val log = activity(updated.event, user.person).updated.insert()
                sendEmailNotification(updated.event, changes, log)

                redirect(routes.Events.details(id), "success" -> "Event was updated")
              }
          }
        })
  }

  /**
   * Send requests for evaluation to participants of the event
    *
    * @param id Event ID
   */
  def sendRequest(id: Long) = AsyncSecuredEventAction(List(Role.Facilitator, Role.Coordinator), id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒ implicit event =>
      case class EvaluationRequestData(attendeeIds: List[Long], body: String)
      val form = Form(mapping(
        "participantIds" -> list(longNumber),
        "body" -> nonEmptyText.verifying(
          "The letter's body doesn't contains a link",
          (b: String) ⇒ {
            val url = """https?:\/\/""".r findFirstIn b
            url.isDefined
          }))(EvaluationRequestData.apply)(EvaluationRequestData.unapply)).bindFromRequest

      form.fold(
        formWithErrors ⇒
          redirect(routes.Events.details(id), "error" -> "Provided data are wrong. Please, check a request form."),
        requestData ⇒ {
          (for {
            a <- attendeeService.findByEvents(List(event.identifier))
            b <- brandService.get(event.brandId)
          } yield (a, b)) flatMap { case (unfilteredAttendees, brand) =>
            val attendees = unfilteredAttendees.map(_._2)
            if (requestData.attendeeIds.forall(p ⇒ attendees.exists(_.identifier == p))) {
              import scala.util.matching.Regex
              val namePattern = new Regex( """(PARTICIPANT_NAME_TOKEN)""", "name")
              attendees.filter(a => requestData.attendeeIds.contains(a.identifier)).foreach { attendee ⇒
                val body = namePattern replaceAllIn(requestData.body, m ⇒ attendee.fullName)
                EvaluationReminder.sendEvaluationRequest(attendee, brand, body)
              }

              val activity = Activity.insert(user.name, Activity.Predicate.Sent, event.title)
              redirect(routes.Events.details(id), "error" -> activity.toString)
            } else {
                redirect(routes.Events.details(id), "error" -> "Some people are not the attendees of the event")
            }
          }
        })
  }

  /**
   * Returns none if the given event is valid; otherwise returns a list with errors
   *
   * @param event Event
   * @param account User account
   */
  protected def validateEvent(event: Event, account: UserAccount): Future[Option[List[(String, String)]]] = {
    (for {
      l <- validateLicenses(event)
      e <- validateEventType(event)
    } yield (l, e)) map {
      case (None, None) => List()
      case (None, Some(e)) => List(e)
      case (Some(l), None) => List(l)
      case (Some(l), Some(e)) => List(l, e)
    } map { errors =>
      if (errors.isEmpty) None else Some(errors)
    }
  }

  /**
   * Returns error if none of facilitators has a valid license
   *
   * @param event Event object
   */
  protected def validateLicenses(event: Event): Future[Option[(String, String)]] = {
    licenseService.licensees(event.brandId) map { licenses =>
      if (event.facilitatorIds.forall(id ⇒ licenses.exists(_.id.get == id))) {
        None
      } else {
        Some(("facilitatorIds", "error.event.invalidLicense"))
      }
    }
  }

  /**
   * Returns error if event type doesn't exist or doesn't belong to the brand
   *
   * @param event Event object
   */
  protected def validateEventType(event: Event): Future[Option[(String, String)]] = {
    eventTypeService.find(event.eventTypeId) map {
      case None => Some(("eventTypeId", "error.eventType.notFound"))
      case Some(x) =>
        if (x.brandId != event.brandId)
          Some(("eventTypeId", "error.eventType.wrongBrand"))
        else
          None
    }
  }

  /**
   * Returns event form with highlighted errors
    *
    * @param user User object
   * @param form Form with errors
   * @param eventId Event identifier if exists
   */
  protected def formError(user: ActiveUser,
    form: Form[EventView],
    eventId: Option[Long])(implicit request: Request[Any],
      handler: be.objectify.deadbolt.scala.DeadboltHandler,
      token: play.filters.csrf.CSRF.Token) = {
    brandService.findByUser(user.account) flatMap { brands =>
      badRequest(views.html.v2.event.form(user, eventId, brands, false, form))
    }
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
    activity: BaseActivity)(implicit request: RequestHeader): Future[Unit] = {

    (for {
      b <- brandService.findWithCoordinators(event.brandId)
      e <- eventTypeService.get(event.eventTypeId)
    } yield (b, e)) map { case (Some(x), eventType) =>
      val recipients = x.coordinators.filter(_._2.notification.event).map(_._1)
      val subject = s"${activity.description} event"
      email.send(recipients.toSet, None, None, subject,
        mail.templates.html.event(event, eventType.name, x.brand, changes).toString, richMessage = true)
    }
  }

  /**
   * Return redirect object with success message for the given event
   *
   * @param id Event identifier
   * @param msg Message
   */
  private def success(id: Long, msg: String) = redirect(routes.Events.details(id), "success" -> msg)

  /**
   * Return redirect object with error message for the given event
   *
   * @param id Event identifier
   * @param msg Message
   */
  private def error(id: Long, msg: String) = redirect(routes.Events.details(id), "error" -> msg)
}
