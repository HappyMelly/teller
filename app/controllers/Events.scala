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
import models.UserRole.DynamicRole
import models.UserRole.Role._
import models.brand.EventType
import models.event.Comparator
import models.event.Comparator.FieldChange
import models.service.Services
import models.{ Location, Schedule, _ }
import org.joda.time.{ DateTime, LocalDate }
import play.api.data.Forms._
import play.api.data._
import play.api.data.format.Formatter
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc._
import services.integrations.Integrations
import views.Countries

trait Events extends Controller
  with Security
  with Services
  with Integrations
  with Activities {

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
  val invoiceMapping = mapping(
    "id" -> ignored(Option.empty[Long]),
    "eventId" -> ignored(Option.empty[Long]),
    "invoiceTo" -> longNumber.verifying(
      "Such organization doesn't exist", (invoiceTo: Long) ⇒ orgService.find(invoiceTo).isDefined),
    "invoiceBy" -> optional(longNumber).verifying(
      "Such organization doesn't exist", (invoiceBy: Option[Long]) ⇒ invoiceBy.map { value ⇒
        orgService.find(value).isDefined
      }.getOrElse(true)),
    "number" -> optional(nonEmptyText))(EventInvoice.apply)(EventInvoice.unapply)

  /**
   * HTML form mapping for creating and editing.
   */
  def eventForm(implicit user: UserIdentity) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "eventTypeId" -> longNumber(min = 1),
    "brandId" -> longNumber(min = 1),
    "title" -> nonEmptyText(1, 254),
    "language" -> mapping(
      "spoken" -> language,
      "secondSpoken" -> optional(language),
      "materials" -> optional(language))(Language.apply)(Language.unapply),
    "location" -> mapping(
      "city" -> nonEmptyText,
      "country" -> nonEmptyText) (Location.apply)(Location.unapply),
    "details" -> mapping(
      "description" -> optional(text),
      "specialAttention" -> optional(text),
      "webSite" -> optional(webUrl),
      "registrationPage" -> optional(text))(Details.apply)(Details.unapply),
    "schedule" -> mapping(
      "start" -> jodaLocalDate,
      "end" -> of(dateRangeFormatter),
      "hoursPerDay" -> number(1, 24),
      "totalHours" -> number(1))(Schedule.apply)(Schedule.unapply),
    "notPublic" -> default(boolean, false),
    "archived" -> default(boolean, false),
    "confirmed" -> default(boolean, false),
    "free" -> default(boolean, false),
    "invoice" -> invoiceMapping,
    "facilitatorIds" -> list(longNumber).verifying(
      Messages("error.event.nofacilitators"), (ids: List[Long]) ⇒ ids.nonEmpty))(
      { (id, eventTypeId, brandId, title, language, location, details, schedule,
        notPublic, archived, confirmed, free, invoice, facilitatorIds) ⇒
        {
          val event = Event(id, eventTypeId, brandId, title, language,
            location, details, schedule, notPublic, archived, confirmed, free,
            0.0f, None)
          event.facilitatorIds_=(facilitatorIds)
          EventView(event, invoice)
        }
      })({ (view: EventView) ⇒
        Some((view.event.id, view.event.eventTypeId, view.event.brandId,
          view.event.title, view.event.language, view.event.location,
          view.event.details, view.event.schedule, view.event.notPublic,
          view.event.archived, view.event.confirmed, view.event.free,
          view.invoice, view.event.facilitatorIds))

      }))

  /**
   * Create page.
   */
  def add = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val defaultDetails = Details(Some(""), Some(""), Some(""), Some(""))
      val defaultSchedule = Schedule(LocalDate.now(), LocalDate.now().plusDays(1), 8, 0)
      val defaultInvoice = EventInvoice(Some(0), Some(0), 0, Some(0), Some(""))
      val default = Event(None, 0, 0, "", Language("", None, Some("English")),
        Location("", ""), defaultDetails, defaultSchedule,
        notPublic = false, archived = false, confirmed = false, free = false,
        0.0f, None)
      val view = EventView(default, defaultInvoice)
      val account = user.account
      val brands = Brand.findByUser(account)
      Ok(views.html.event.form(user, None, brands, account.personId, true, eventForm.fill(view)))
  }

  /**
   * Duplicate an event
   * @param id Event Id
   * @return
   */
  def duplicate(id: Long) = SecuredDynamicAction("event", DynamicRole.Facilitator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      eventService.findWithInvoice(id) map { view ⇒
        val account = user.account
        val brands = Brand.findByUser(account)
        Ok(views.html.event.form(user, None, brands, account.personId, false, eventForm.fill(view)))
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
            Redirect(routes.Events.index()).flashing("success" -> log.toString)

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
                Redirect(routes.Events.index()).flashing("error" -> "Something goes wrong :("),
              data ⇒ {
                event.cancel(user.person.id.get, data.reason,
                  data.participants, data.details)
                val log = activity(event, user.person).deleted.insert()
                sendEmailNotification(event, List.empty, log)
                Redirect(routes.Events.index()).flashing("success" -> log.toString)
              })
          } else {
            Redirect(routes.Events.details(id)).flashing("error" -> Messages("error.event.nonDeletable"))
          }
        } getOrElse NotFound
  }

  /**
   * Update an invoice data for an event
   *
   * @param id Event ID
   * @return
   */
  def invoice(id: Long) = SecuredDynamicAction("event", DynamicRole.Coordinator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      eventService.find(id) map { event ⇒
        val form = Form(invoiceMapping).bindFromRequest
        form.fold(
          formWithErrors ⇒ {
            Redirect(routes.Events.details(id)).flashing("error" -> "Invoice data are wrong. Please try again")
          },
          eventInvoice ⇒ {
            val invoice = EventInvoice.findByEvent(id)
            EventInvoice.update(eventInvoice.copy(id = invoice.id).copy(eventId = invoice.eventId))
            activity(event, user.person).updated.insert()
            Redirect(routes.Events.details(id)).flashing("success" -> "Invoice data was successfully updated")
          })
      } getOrElse NotFound
  }

  /**
   * Details page.
   * @param id Event ID
   */
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      eventService.findWithInvoice(id) map { x ⇒
        val acc = user.account
        //@TODO only funders must be retrieved
        val funders = if (acc.editor) orgService.findAll else List()
        val eventType = eventTypeService.find(x.event.eventTypeId).get
        val canFacilitate = acc.editor || x.event.isFacilitator(acc.personId) ||
          brandService.isCoordinator(x.event.brandId, acc.personId)
        val fees = feeService.findByBrand(x.event.brandId)
        val printableFees = fees.
          map(x ⇒ (Countries.name(x.country), x.fee.toString)).
          sortBy(_._1)
        val brand = brandService.find(x.event.brandId).get
        val event = fees.find(_.country == x.event.location.countryCode) map { y ⇒
          Event.withFee(x.event, y.fee, eventType.maxHours)
        } getOrElse x.event
        Ok(views.html.event.details(user,
          canFacilitate,
          funders,
          EventView(event, x.invoice),
          eventType.name,
          brand.name,
          printableFees))
      } getOrElse NotFound
  }

  /**
   * Edit page.
   * @param id Event ID
   */
  def edit(id: Long) = SecuredDynamicAction("event", DynamicRole.Facilitator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      eventService.findWithInvoice(id) map { view ⇒
        val account = user.account
        val brands = Brand.findByUser(account)
        Ok(views.html.event.form(user, Some(id), brands, account.personId, emptyForm = false, eventForm.fill(view)))
      } getOrElse NotFound
  }

  /**
   * List page.
   */
  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val person = user.person
      val personalLicense = person.licenses.find(_.license.active).map(_.brand.code).getOrElse("")
      val brands = brandService.findAll
      val facilitators = brands.map(b ⇒
        (b.id.get, License.allLicensees(b.id.get).map(l ⇒ (l.id.get, l.fullName))))

      implicit val facilitatorWrites = new Writes[(Long, String)] {
        def writes(data: (Long, String)): JsValue = {
          Json.obj(
            "id" -> data._1,
            "name" -> data._2)
        }
      }
      implicit val facilitatorsWrites = new Writes[(Long, List[(Long, String)])] {
        def writes(data: (Long, List[(Long, String)])): JsValue = {
          Json.obj(
            "brandId" -> data._1,
            "facilitators" -> data._2)
        }
      }
      Ok(views.html.event.index(user, brands, Json.toJson(facilitators), person.id.get, personalLicense))
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
            account.brands.exists(_.code == e.brandId))
      else
        events.filter(!_.notPublic) :::
          events.filter(e ⇒ e.notPublic &&
            e.facilitators.exists(_.id.get == account.personId))

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
              "country" -> data.event.location.countryCode.toLowerCase,
              "city" -> data.event.location.city),
            "facilitators" -> data.event.facilitators,
            "schedule" -> Json.obj(
              "start" -> data.event.schedule.start.toString,
              "end" -> data.event.schedule.end.toString),
            "totalHours" -> data.event.schedule.totalHours,
            "materialsLanguage" -> data.event.materialsLanguage,
            "confirmed" -> data.event.confirmed,
            "invoice" -> Json.obj(
              "free" -> data.event.free,
              "invoice" -> (if (data.invoice.invoiceBy.isEmpty) { "No" } else { "Yes" })),
            "actions" -> {
              Json.obj(
                "edit" -> {
                  if (account.editor || data.event.facilitators.exists(_.id.get == account.personId)) {
                    routes.Events.edit(data.event.id.get).url
                  } else ""
                },
                "duplicate" -> {
                  if (account.editor || data.event.facilitators.exists(_.id.get == account.personId)) {
                    routes.Events.duplicate(data.event.id.get).url
                  } else ""
                },
                "cancel" -> {
                  if (account.editor || data.event.facilitators.exists(_.id.get == account.personId)) {
                    routes.Events.cancel(data.event.id.get).url
                  } else ""
                })
            })
        }
      }
      Ok(Json.toJson(views))
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
   * Confirm form submits to this action.
   * @param id Event ID
   */
  def confirm(id: Long) = SecuredDynamicAction("event", DynamicRole.Facilitator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      eventService.findWithInvoice(id).map { view ⇒
        val updated = view.copy(event = view.event.copy(id = Some(id)).copy(confirmed = true))
        updated.event.facilitatorIds_=(view.event.facilitatorIds)
        eventService.update(updated)
        val log = activity(updated.event, user.person).confirmed.insert()
        Redirect(routes.Events.details(id)).flashing("success" -> log.toString)
      }.getOrElse(NotFound)
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
                val subject = s"Evaluation Request"
                email.send(Set(participant), None, None, subject,
                  mail.evaluation.html.request(brand, participant, body).toString(), richMessage = true)
              }

              val activity = Activity.insert(user.fullName, Activity.Predicate.Sent, event.title)
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
  protected def formError(user: UserIdentity,
    form: Form[EventView],
    eventId: Option[Long])(implicit flash: play.api.mvc.Flash,
      request: Request[Any],
      handler: AuthorisationHandler,
      token: play.filters.csrf.CSRF.Token) = {
    val account = user.account
    val brands = Brand.findByUser(account)
    BadRequest(views.html.event.form(user, eventId, brands, account.personId, false, form))
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
    activity: Activity)(implicit request: RequestHeader): Unit = {

    brandService.findWithCoordinators(event.brandId) foreach { x ⇒
      val recipients = x.coordinators.filter(_._2.notification.event).map(_._1)
      val subject = s"${activity.description} event"
      email.send(recipients.toSet, None, None, subject,
        mail.html.event(event, x.brand, changes).toString, richMessage = true)
    }
  }
}

object Events extends Events