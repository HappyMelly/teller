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

import Forms._
import models._
import play.api.mvc._
import play.api.libs.json._
import securesocial.core.SecuredRequest
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import org.joda.time.{ LocalDate, DateTime }
import models.UserRole.Role._
import play.api.data.format.Formatter
import models.Location
import models.Schedule
import services.EmailService

object Events extends Controller with Security {

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

  val eventTypeFormatter = new Formatter[Long] {

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Long] = {
      // "data" lets you access all form data values
      try {
        val eventTypeId = data.get("eventTypeId").get.toLong
        try {
          val brandCode = data.get("brandCode").get
          if (EventType.exists(eventTypeId)) {
            val event = EventType.find(eventTypeId).get
            if (event.brand.code == brandCode) {
              Right(eventTypeId)
            } else {
              Left(List(FormError("eventTypeId", "Selected event type doesn't belong to a selected brand")))
            }
          } else {
            Left(List(FormError("eventTypeId", "Unknown event type")))
          }
        } catch {
          case e: IllegalArgumentException ⇒ Left(List(FormError("brandCode", "Select a brand")))
        }
      } catch {
        case e: IllegalArgumentException ⇒ Left(List(FormError("eventTypeId", "Select an event type")))
      }
    }

    def unbind(key: String, value: Long): Map[String, String] = {
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
      "Such organization doesn't exist", (invoiceTo: Long) ⇒ Organisation.find(invoiceTo).isDefined),
    "invoiceBy" -> optional(longNumber).verifying(
      "Such organization doesn't exist", (invoiceBy: Option[Long]) ⇒ invoiceBy.map{ value ⇒ Organisation.find(value).isDefined }.getOrElse(true)),
    "number" -> optional(nonEmptyText))(EventInvoice.apply)(EventInvoice.unapply)

  /**
   * HTML form mapping for creating and editing.
   */
  def eventForm(implicit request: SecuredRequest[_]) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "eventTypeId" -> of(eventTypeFormatter),
    "brandCode" -> nonEmptyText.verifying(
      "error.brand.invalid", (brandCode: String) ⇒ Brand.canManage(brandCode, request.user.asInstanceOf[LoginIdentity].userAccount)),
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
    "invoice" -> invoiceMapping,
    "created" -> ignored(DateTime.now()),
    "createdBy" -> ignored(request.user.fullName),
    "updated" -> ignored(DateTime.now()),
    "updatedBy" -> ignored(request.user.fullName),
    "facilitatorIds" -> list(longNumber).verifying(
      "error.event.nofacilitators", (ids: List[Long]) ⇒ !ids.isEmpty))(
      { (id, eventTypeId, brandCode, title, language, location, details, schedule, notPublic, archived, confirmed,
        invoice, created, createdBy, updated, updatedBy, facilitatorIds) ⇒
        {
          val event = Event(id, eventTypeId, brandCode, title, language, location, details, schedule, notPublic,
            archived, confirmed, created, createdBy, updated, updatedBy)
          event.invoice_=(invoice)
          event.facilitatorIds_=(facilitatorIds)
          event
        }
      })({ (e: Event) ⇒
        Some((e.id, e.eventTypeId, e.brandCode, e.title, e.language, e.location, e.details, e.schedule, e.notPublic,
          e.archived, e.confirmed, e.invoice, e.created, e.createdBy, e.updated, e.updatedBy, e.facilitatorIds))

      }))

  /**
   * Sends an e-mail notification for an event to the given recipients.
   *
   */
  def sendEmailNotification(event: Event, changes: List[Event.FieldChange], activity: Activity,
    recipient: Person)(implicit request: RequestHeader): Unit = {
    val subject = s"${activity.description} event"
    EmailService.send(Set(recipient), None, None, subject, mail.html.event(event, changes).toString, richMessage = true)
  }

  /**
   * Create page.
   */
  def add = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒

      val defaultDetails = Details(Some(""), Some(""), Some(""), Some(""))
      val defaultSchedule = Schedule(LocalDate.now(), LocalDate.now().plusDays(1), 8, 0)
      val defaultInvoice = EventInvoice(Some(0), Some(0), 0, Some(0), Some(""))
      val default = Event(None, 0, "", "", Language("", None, Some("English")), Location("", ""), defaultDetails, defaultSchedule,
        notPublic = false, archived = false, confirmed = false, DateTime.now(), "", DateTime.now(), "")
      default.invoice_=(defaultInvoice)
      val account = request.user.asInstanceOf[LoginIdentity].userAccount
      val brands = Brand.findByUser(account)
      Ok(views.html.event.form(request.user, None, brands, account.personId, true, eventForm.fill(default)))
  }

  /**
   * Duplicate an event
   * @param id Event Id
   * @return
   */
  def duplicate(id: Long) = SecuredDynamicAction("event", "edit") { implicit request ⇒
    implicit handler ⇒

      Event.find(id).map {
        event ⇒
          val account = request.user.asInstanceOf[LoginIdentity].userAccount
          val brands = Brand.findByUser(account)
          Ok(views.html.event.form(request.user, None, brands, account.personId, false, eventForm.fill(event)))
      }.getOrElse(NotFound)
  }

  /**
   * Create form submits to this action.
   */
  def create = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒

      val form = eventForm.bindFromRequest
      form.fold(
        formWithErrors ⇒ {
          val account = request.user.asInstanceOf[LoginIdentity].userAccount
          val brands = Brand.findByUser(account)
          BadRequest(views.html.event.form(request.user, None, brands, account.personId, false, formWithErrors))
        },
        event ⇒ {
          val validLicensees = License.licensees(event.brandCode)
          val coordinator = Brand.find(event.brandCode).get.coordinator
          if (event.facilitatorIds.forall(id ⇒ { validLicensees.exists(_.id.get == id) || coordinator.id.get == id })) {
            val addedEvent = event.insert
            val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, addedEvent.title)
            sendEmailNotification(addedEvent, List.empty, activity, Brand.find(event.brandCode).get.coordinator)
            Redirect(routes.Events.index()).flashing("success" -> activity.toString)
          } else {
            val account = request.user.asInstanceOf[LoginIdentity].userAccount
            val brands = Brand.findByUser(account)
            BadRequest(views.html.event.form(request.user, None, brands, account.personId, false,
              form.withError("facilitatorIds", "Some facilitators do not have valid licenses")))
          }
        })
  }

  /**
   * Delete an event.
   * @param id Event ID
   */
  def delete(id: Long) = SecuredDynamicAction("event", "edit") { implicit request ⇒
    implicit handler ⇒

      Event.find(id).map { event ⇒
        if (event.deletable) {
          Event.delete(id)
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, event.title)
          sendEmailNotification(event, List.empty, activity, Brand.find(event.brandCode).get.coordinator)
          Redirect(routes.Events.index()).flashing("success" -> activity.toString)
        } else {
          Redirect(routes.Events.details(id)).flashing("error" -> Messages("error.event.nonDeletable"))
        }
      }.getOrElse(NotFound)
  }

  /**
   * Update an invoice data for an event
   *
   * @param id Event ID
   * @return
   */
  def invoice(id: Long) = SecuredDynamicAction("event", "admin") { implicit request ⇒
    implicit handler ⇒

      Event.find(id).map { event ⇒
        val form = Form(invoiceMapping).bindFromRequest
        form.fold(
          formWithErrors ⇒ {
            Redirect(routes.Events.details(id)).flashing("error" -> "Invoice data are wrong. Please try again")
          },
          eventInvoice ⇒ {
            val invoice = EventInvoice.findByEvent(id)
            EventInvoice.update(eventInvoice.copy(id = invoice.id).copy(eventId = invoice.eventId))
            Redirect(routes.Events.details(id)).flashing("success" -> "Invoice data was successfully updated")
          })
      }.getOrElse(NotFound)
  }

  /**
   * Details page.
   * @param id Event ID
   */
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒

      Event.find(id).map {
        event ⇒
          val legalEntities = Organisation.find(legalEntitiesOnly = true)
          Ok(views.html.event.details(request.user, legalEntities, event))
      }.getOrElse(NotFound)
  }

  /**
   * Edit page.
   * @param id Event ID
   */
  def edit(id: Long) = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒

      Event.find(id).map {
        event ⇒
          val account = request.user.asInstanceOf[LoginIdentity].userAccount
          val brands = Brand.findByUser(account)
          Ok(views.html.event.form(request.user, Some(id), brands, account.personId, emptyForm = false, eventForm.fill(event)))
      }.getOrElse(NotFound)
  }

  /**
   * List page.
   */
  def index = SecuredDynamicAction("event", "view") { implicit request ⇒
    implicit handler ⇒

      val person = request.user.asInstanceOf[LoginIdentity].userAccount.person.get
      val personalLicense = person.licenses.find(_.license.active).map(_.brand.code).getOrElse("")
      val brands = Brand.findAll.sortBy(_.name)
      val facilitators = brands.map(b ⇒
        (b.code, License.allLicensees(b.code).map(l ⇒ (l.id.get, l.fullName))))

      implicit val facilitatorWrites = new Writes[(Long, String)] {
        def writes(data: (Long, String)): JsValue = {
          Json.obj(
            "id" -> data._1,
            "name" -> data._2)
        }
      }
      implicit val facilitatorsWrites = new Writes[(String, List[(Long, String)])] {
        def writes(data: (String, List[(Long, String)])): JsValue = {
          Json.obj(
            "code" -> data._1,
            "facilitators" -> data._2)
        }
      }
      Ok(views.html.event.index(request.user, brands, Json.toJson(facilitators), person.id.get, personalLicense))
  }

  /**
   * Get a list of events in JSON format, filtered by parameters
   * @param brandCode Brand string identifier
   * @param future This flag defines if we want to get future/past events
   * @param public This flag defines if we want to get public/private events
   * @param archived This flag defines if we want to get archived/current events
   * @return
   */
  def events(brandCode: Option[String],
    facilitator: Option[Long],
    future: Option[Boolean],
    public: Option[Boolean],
    archived: Option[Boolean]) = SecuredDynamicAction("event", "view") { implicit request ⇒
    implicit handler ⇒
      val events = facilitator map {
        Event.findByFacilitator(_, brandCode, future, public, archived)
      } getOrElse {
        Event.findByParameters(brandCode, future, public, archived)
      }
      val account = request.user.asInstanceOf[LoginIdentity].userAccount
      EventsCollection.facilitators(events)
      EventsCollection.invoices(events)

      implicit val personWrites = new Writes[Person] {
        def writes(data: Person): JsValue = {
          Json.obj(
            "name" -> data.fullName,
            "url" -> routes.People.details(data.id.get).url)
        }
      }
      implicit val eventWrites = new Writes[Event] {
        def writes(data: Event): JsValue = {
          Json.obj(
            "event" -> Json.obj(
              "id" -> data.id,
              "url" -> routes.Events.details(data.id.get).url,
              "title" -> data.title),
            "brand" -> Json.obj(
              "code" -> data.brandCode,
              "url" -> routes.Brands.details(data.brandCode).url),
            "location" -> Json.obj(
              "country" -> data.location.countryCode.toLowerCase,
              "city" -> data.location.city),
            "facilitators" -> data.facilitators,
            "schedule" -> Json.obj(
              "start" -> data.schedule.start.toString,
              "end" -> data.schedule.end.toString),
            "totalHours" -> data.schedule.totalHours,
            "materialsLanguage" -> data.materialsLanguage,
            "confirmed" -> data.confirmed,
            "invoice" -> (if (data.invoice.invoiceBy.isEmpty) { "No" } else { "Yes" }),
            "actions" -> {
              Json.obj(
                "edit" -> {
                  if (account.editor || data.facilitators.exists(_.id.get == account.personId)) {
                    routes.Events.edit(data.id.get).url
                  } else ""
                },
                "duplicate" -> {
                  if (account.editor || data.facilitators.exists(_.id.get == account.personId)) {
                    routes.Events.duplicate(data.id.get).url
                  } else ""
                })
            })
        }
      }
      Ok(Json.toJson(events))
  }
  /**
   * Edit form submits to this action.
   * @param id Event ID
   */
  def update(id: Long) = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒

      val form = eventForm.bindFromRequest
      form.fold(
        formWithErrors ⇒ {
          val account = request.user.asInstanceOf[LoginIdentity].userAccount
          val brands = Brand.findByUser(account)
          BadRequest(views.html.event.form(request.user, Some(id), brands, account.personId, false, formWithErrors))
        },
        event ⇒ {
          val validLicensees = License.licensees(event.brandCode)
          val coordinator = Brand.find(event.brandCode).get.coordinator
          if (event.facilitatorIds.forall(id ⇒ { validLicensees.exists(_.id.get == id) || coordinator.id.get == id })) {
            val existingEvent = Event.find(id).get

            val updatedEvent = event.copy(id = Some(id))
            updatedEvent.invoice_=(event.invoice.copy(id = existingEvent.invoice.id))
            updatedEvent.facilitatorIds_=(event.facilitatorIds)

            // it's important to compare before updating as with lazy initialization invoice and facilitators data
            // for an old event will be destroyed
            val changes = Event.compare(existingEvent, updatedEvent)
            updatedEvent.update

            val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, event.title)
            sendEmailNotification(updatedEvent, changes, activity, Brand.find(event.brandCode).get.coordinator)

            Redirect(routes.Events.index()).flashing("success" -> activity.toString)
          } else {
            val account = request.user.asInstanceOf[LoginIdentity].userAccount
            val brands = Brand.findByUser(account)
            BadRequest(views.html.event.form(request.user, Some(id), brands, account.personId, false,
              form.withError("facilitatorIds", "Some facilitators do not have valid licenses")))
          }
        })
  }

  /**
   * Confirm form submits to this action.
   * @param id Event ID
   */
  def confirm(id: Long) = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒
      Event.find(id).map {
        event ⇒
          event.copy(confirmed = true).update
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Confirmed, event.title)
          Redirect(routes.Events.details(id)).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  /**
   * Send requests for evaluation to participants of the event
   * @param id Event ID
   */
  def sendRequest(id: Long) = SecuredDynamicAction("evaluation", "manage") { implicit request ⇒
    implicit handler ⇒
      case class EvaluationRequestData(participantIds: List[Long], body: String)
      val form = Form(mapping(
        "participantIds" -> list(longNumber),
        "body" -> nonEmptyText.verifying(
          "The letter's body doesn't contains a link",
          (b: String) ⇒ {
            val url = """https?:\/\/""".r findFirstIn b
            url.isDefined
          }))(EvaluationRequestData.apply)(EvaluationRequestData.unapply)).bindFromRequest

      Event.find(id).map { event ⇒
        form.fold(
          formWithErrors ⇒ {
            Redirect(routes.Events.details(id)).flashing("error" -> "Provided data are wrong. Please, check a request form.")
          },
          requestData ⇒ {
            val participantIds = event.participants.map(_.id.get)
            if (requestData.participantIds.forall(p ⇒ participantIds.contains(p))) {
              import scala.util.matching.Regex
              val namePattern = new Regex("""(PARTICIPANT_NAME_TOKEN)""", "name")
              val brand = Brand.find(event.brandCode).get
              requestData.participantIds.foreach { id ⇒
                val participant = Person.find(id).get
                val body = namePattern replaceAllIn (requestData.body, m ⇒ participant.fullName)
                val subject = s"Evaluation Request"
                EmailService.send(Set(participant), None, None, subject,
                  mail.html.evaluationRequest(brand.brand, participant, body).toString(), richMessage = true)
              }

              val activity = Activity.insert(request.user.fullName, Activity.Predicate.Sent, event.title)
              Redirect(routes.Events.details(id)).flashing("success" -> activity.toString)
            } else {
              Redirect(routes.Events.details(id)).flashing("error" -> "Some people are not participants of the event.")
            }
          })
      }.getOrElse(NotFound)
  }
}
