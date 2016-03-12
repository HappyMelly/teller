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
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.event.{EventForms, Helpers}
import models._
import models.UserRole.Role
import models.cm.event.Comparator
import Comparator.FieldChange
import models.repository.Repositories
import models.cm.{Location, Schedule, _}
import org.joda.time.LocalDate
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json._
import play.api.mvc._
import services.TellerRuntimeEnvironment
import services.integrations._
import views.Countries

import scala.concurrent.Future

class Events @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment,
                                     override val messagesApi: MessagesApi,
                                     val repos: Repositories,
                                     val email: Email,
                                     deadbolt: DeadboltActions,
                                     handlers: HandlerCache,
                                     actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env)
  with Activities
  with BrandAware
  with Integrations
  with Helpers
  with I18nSupport {

  /**
   * Create page.
   */
  def add = RestrictedAction(List(Role.Coordinator, Role.Facilitator)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      repos.cm.brand.findByUser(user.account) flatMap { brands =>
        val defaultDetails = Details(Some(""), Some(""))
        val organizer = Organizer(0, Some(""), Some(""))
        val defaultSchedule = Schedule(LocalDate.now(), LocalDate.now().plusDays(1), 8, 0)
        val defaultInvoice = EventInvoice(Some(0), Some(0), 0, Some(0), Some(""))
        val default = Event(None, 0, 0, "", Language("", None, Some("English")),
          Location("", ""), defaultDetails, organizer, defaultSchedule,
          notPublic = false, archived = false, confirmed = false, free = false,
          followUp = true)
        val view = EventView(default, defaultInvoice)
        ok(views.html.v2.event.form(user, None, brands.filter(_.active), true, EventForms.event(repos).fill(view)))

      }
  }

  /**
   * Duplicate an event
    *
    * @param id Event Id
   * @return
   */
  def duplicate(id: Long) = EventAction(List(Role.Facilitator, Role.Coordinator), id) { implicit request ⇒
    implicit handler => implicit user ⇒ implicit event =>
      (for {
        e <- repos.cm.event.findWithInvoice(id)
        b <- repos.cm.brand.findByUser(user.account)
      } yield (e, b)) flatMap {
        case (None, _) => notFound("Event not found")
        case (Some(view), brands) =>
          ok(views.html.v2.event.form(user, None, brands, false, EventForms.event(repos).fill(view)))
      }
  }

  /**
   * Create form submits to this action.
   */
  def create = RestrictedAction(List(Role.Coordinator, Role.Facilitator)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val form = EventForms.event(repos).bindFromRequest
      form.fold(
        formWithErrors ⇒ formError(user, formWithErrors, None),
        view ⇒ {
          validateEvent(view.event, user.account) flatMap {
            case Some(errors) =>
              formError(user, form.withError("facilitatorIds", Messages("error.event.invalidLicense")), None)
            case None =>
              repos.cm.event.insert(view) flatMap { inserted =>
                val log = activity(inserted.event, user.person).created.insert(repos)
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
  def cancel(id: Long) = EventAction(List(Role.Facilitator, Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>

        case class CancellationData(reason: Option[String],
          participants: Option[Int],
          details: Option[String])

        def cancelForm = Form(mapping(
          "reason" -> optional(text),
          "participantNumber" -> optional(number),
          "details" -> optional(text))(CancellationData.apply)(CancellationData.unapply))

      if (event.deletable(repos)) {
        cancelForm.bindFromRequest.fold(
          failure ⇒ redirect(core.routes.Dashboard.index(), "error" -> "Something goes wrong :("),
          data ⇒ {
            event.cancel(user.person.id.get, data.reason, data.participants, data.details, repos)
            val log = activity(event, user.person).deleted.insert(repos)
            sendEmailNotification(event, List.empty, log)
            redirect(core.routes.Dashboard.index(), "success" -> "Event was cancelled")
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
  def confirm(id: Long) = EventAction(List(Role.Facilitator, Role.Coordinator), id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒ implicit event =>
      repos.cm.event.confirm(id) flatMap { _ =>
        val log = activity(event, user.person).confirmed.insert(repos)
        success(id, log.toString)
      }
  }


  /**
    * Details page.
    *
    * @param id Event ID
   */
  def details(id: Long) = RestrictedAction(List(Role.Coordinator, Role.Facilitator)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      repos.cm.event.findWithInvoice(id) flatMap {
        case None => notFound("Event not found")
        case Some(x) =>
          val query = for {
            eventType <- repos.cm.rep.brand.eventType.get(x.event.eventTypeId)
            fees <- repos.fee.findByBrand(x.event.brandId)
            invoiceOrgs <- repos.org.find(List(x.invoice.invoiceTo, x.invoice.invoiceBy.getOrElse(0L)))
            attendees <- repos.cm.rep.event.attendee.findByEvents(List(id))
          } yield (eventType, fees, invoiceOrgs, attendees.isEmpty)
          query flatMap { case (eventType, fees, invoiceOrgs, deletable) =>
            val printableFees = fees.map(x ⇒ (Countries.name(x.country), x.fee.toString)).sortBy(_._1)
            val event = fees.find(_.country == x.event.location.countryCode) map { y ⇒
              Event.withFee(x.event, y.fee, eventType.maxHours)
            } getOrElse x.event
            val invoiceView = invoice(x.invoice, invoiceOrgs)

            implicit val service: Repositories = repos
            roleDiffirentiator(user.account, Some(x.event.brandId)) { (view, brands) =>
              repos.person.memberships(user.person.identifier) flatMap { orgs =>
                ok(views.html.v2.event.details(user, view.brand, brands, orgs, event,
                  invoiceView, eventType.name, printableFees, deletable))
              }
            } { (view, brands) =>
              ok(views.html.v2.event.details(user, view.get.brand, brands, List(), event,
                invoiceView, eventType.name, printableFees, deletable))
            } {
              redirect(core.routes.Dashboard.index())
            }
          }

      }
  }

  def detailsButtons(id: Long) = EventAction(List(Role.Facilitator, Role.Coordinator), id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒ implicit event =>
      repos.cm.rep.event.attendee.findByEvents(List(id)) flatMap { attendees =>
        ok(views.html.v2.event.detailsButtons(event, attendees.isEmpty))
      }

  }

  /**
   * Edit page.
    *
    * @param id Event ID
   */
  def edit(id: Long) = EventAction(List(Role.Facilitator, Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      (for {
        e <- repos.cm.event.findWithInvoice(id)
        b <- repos.cm.brand.findByUser(user.account)
      } yield (e, b)) flatMap {
        case (None, _) => notFound("Event not found")
        case (Some(view), brands) =>
          ok(views.html.v2.event.form(user, Some(id), brands, emptyForm = false, EventForms.event(repos).fill(view)))
      }
  }

  /**
   * Renders list of events
    *
    * @param brandId Brand identifier
   */
  def index(brandId: Long) = RestrictedAction(List(Role.Facilitator, Role.Coordinator)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      roleDiffirentiator(user.account, Some(brandId)) { (view, brands) =>
        repos.cm.license.allLicensees(brandId) flatMap { facilitators =>
          val names = facilitators.map(l ⇒ (l.identifier, l.fullName)).sortBy(_._2)
          ok(views.html.v2.event.index(user, view.brand, brands, names))
        }
      } { (view, brands) =>
        ok(views.html.v2.event.index(user, view.get.brand, brands, List()))
      } { redirect(core.routes.Dashboard.index()) }
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
             archived: Option[Boolean]) = RestrictedAction(Role.Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      implicit val personWrites = new Writes[Person] {
        def writes(data: Person): JsValue = {
          Json.obj(
            "name" -> data.fullName,
            "url" -> core.routes.People.details(data.id.get).url)
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
            "facilitators" -> data.event.facilitators(repos),
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
          repos.cm.event.findByFacilitator(_, Some(brandId), future, public, archived)
        } getOrElse {
          repos.cm.event.findByParameters(Some(brandId), future, public, archived)
        }
        result flatMap { events =>
          repos.cm.event.applyFacilitators(events)
          repos.cm.event.withInvoices(events) flatMap { views =>
            ok(Json.toJson(views))
          }
        }
      } { (brand, brands) =>
        repos.cm.event.findByFacilitator(user.person.identifier, Some(brandId), future, public, archived) flatMap { events =>
          repos.cm.event.applyFacilitators(events)
          repos.cm.event.withInvoices(events) flatMap { views =>
            ok(Json.toJson(views))
          }
        }
      } {
        ok(Json.toJson(List[EventView]()))
      }
  }

  /**
    * Renders event public page
    *
    * @param hashedId Hashed event identifier
    */
  def public(hashedId: String) = Action.async { implicit request =>
    repos.cm.event.find(hashedId) flatMap {
      case None => notFound(views.html.notFoundPage(request.path))
      case Some(event) =>
        val query = for {
          b <- repos.cm.brand.get(event.brandId)
          f <- repos.cm.event.facilitators(event.identifier)
          d <- repos.cm.facilitator.find(event.brandId, f.map(_.identifier))
        } yield (b, f.sortBy(_.id), d.sortBy(_.personId))
        query flatMap { case (brand, facilitators, stats) =>
          val facilitatorsWithStat = facilitators.zip(stats).map(v => (v._1, v._2.publicRating, ""))
          ok(views.html.v2.event.public(event, brand, facilitatorsWithStat))
        }
    }
  }

  /**
    * Renders form with cancellation reason
    *
    * @param id Event identifier
   */
  def reason(id: Long) = RestrictedAction(Role.Viewer) { implicit request =>
    implicit handler => implicit user =>
      ok(views.html.v2.event.reason(id))
  }

  /**
   * Edit form submits to this action.
    *
    * @param id Event ID
   */
  def update(id: Long) = EventAction(List(Role.Facilitator, Role.Coordinator), id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒ implicit event =>

      val form = EventForms.event(repos).bindFromRequest
      form.fold(
        errors ⇒ formError(user, errors, Some(id)),
        received ⇒ {
          validateEvent(received.event, user.account) flatMap {
            case Some(errors) ⇒
              formError(user, form.withError("facilitatorIds", Messages("error.event.invalidLicense")), Some(id))
            case None =>
              repos.cm.event.findWithInvoice(id) flatMap {
                case None => notFound("Event not found")
                case Some(view) =>
                  val updated = received.copy(event = received.event.copy(id = Some(id)),
                    invoice = received.invoice.copy(id = view.invoice.id))
                  updated.event.facilitatorIds_=(received.event.facilitatorIds(repos))

                  // it's important to compare before updating as with lazy
                  // initialization invoice and facilitators data
                  // for an old event will be destroyed
                  val changes = (new Comparator(repos)).compare(view, updated)
                  repos.cm.event.update(updated)

                  val log = activity(updated.event, user.person).updated.insert(repos)
                  sendEmailNotification(updated.event, changes, log)

                  redirect(routes.Events.details(id), "success" -> "Event was updated")
              }
          }
        })
  }

  /**
    * Returns invoice view object ready for the representation
    *
    * @param invoice Invoice
    * @param orgs List of organisations related to the invoice
    */
  protected def invoice(invoice: EventInvoice, orgs: List[Organisation]): InvoiceView = {
    val invoiceTo = orgs.filter(_.identifier == invoice.invoiceTo).map(_.name).head
    val invoiceBy = orgs.filter(_.id == invoice.invoiceBy).map(_.name).headOption
    InvoiceView(invoice, invoiceTo, invoiceBy)
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
    repos.cm.license.licensees(event.brandId) map { licenses =>
      if (event.facilitatorIds(repos).forall(id ⇒ licenses.exists(_.id.get == id))) {
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
    repos.cm.rep.brand.eventType.find(event.eventTypeId) map {
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
    repos.cm.brand.findByUser(user.account) flatMap { brands =>
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
      b <- repos.cm.brand.findWithCoordinators(event.brandId) if b.isDefined
      e <- repos.cm.rep.brand.eventType.get(event.eventTypeId)
      f <- repos.cm.event.facilitators(event.identifier)
    } yield (b.get, e, f)) map { case (x, eventType, facilitators) =>
      val recipients = x.coordinators.filter(_._2.notification.event).map(_._1) ::: facilitators
      if (recipients.nonEmpty) {
        val subject = s"${activity.description} event"
        email.send(recipients.toSet, None, None, subject,
          mail.templates.event.html.details(event, eventType.name, x.brand, changes).toString, richMessage = true)
      }
    }
  }
}
