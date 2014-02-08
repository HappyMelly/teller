/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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
import securesocial.core.{ SecuredRequest, SecureSocial }
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import org.joda.time.{ LocalDate, DateTime }
import models.UserRole.Role._
import securesocial.core.SecuredRequest
import scala.Some
import play.api.data.format.Formatter
import play.Logger
import models.Location
import securesocial.core.SecuredRequest
import models.Schedule
import scala.Some

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
        case e: IllegalArgumentException ⇒ return Left(List())
      }
    }

    override def unbind(key: String, value: LocalDate): Map[String, String] = {
      Map(key -> value.toString)
    }
  }

  /**
   * HTML form mapping for creating and editing.
   */
  def eventForm(implicit request: SecuredRequest[_]) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "brandCode" -> nonEmptyText.verifying(
      "error.brand.invalid", (brandCode: String) ⇒ Brand.canManage(brandCode, request.user.asInstanceOf[LoginIdentity].userAccount)),
    "title" -> nonEmptyText,
    "spokenLanguage" -> nonEmptyText,
    "materialsLanguage" -> optional(text),
    "location" -> mapping(
      "city" -> nonEmptyText,
      "country" -> nonEmptyText) (Location.apply)(Location.unapply),
    "schedule" -> mapping(
      "start" -> jodaLocalDate,
      "end" -> jodaLocalDate, /* of(dateRangeFormatter), */
      "hoursPerDay" -> number(1, 24, true),
      "totalHours" -> number(1))(Schedule.apply)(Schedule.unapply),
    "details" -> mapping(
      "description" -> optional(text),
      "specialAttention" -> optional(text),
      "webSite" -> optional(webUrl),
      "registrationPage" -> optional(webUrl))(Details.apply)(Details.unapply),
    "notPublic" -> default(boolean, false),
    "archived" -> default(boolean, false),
    "created" -> ignored(DateTime.now()),
    "createdBy" -> ignored(request.user.fullName),
    "updated" -> ignored(DateTime.now()),
    "updatedBy" -> ignored(request.user.fullName),
    "facilitatorIds" -> list(longNumber).verifying(
      "An event should have at least one facilitator", (ids: List[Long]) ⇒ !ids.isEmpty))(Event.apply)(Event.unapply))

  /**
   * Create page.
   */
  def add = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒

      val defaultDetails = Details(Some(""), Some(""), Some(""), Some(""))
      val defaultSchedule = Schedule(LocalDate.now(), LocalDate.now().plusDays(1), 8, 0)
      val default = Event(None, "", "", "", Some("English"), Location("", ""), defaultSchedule, defaultDetails, false, false, DateTime.now(), "", DateTime.now(), "", List[Long]())
      val account = request.user.asInstanceOf[LoginIdentity].userAccount
      val brands = Brand.findForUser(account)
      Ok(views.html.event.form(request.user, None, brands, account.personId, eventForm.fill(default)))
  }

  /**
   * Duplicate page.
   */
  def duplicate(id: Long) = SecuredDynamicAction("event", "edit") { implicit request ⇒
    implicit handler ⇒

      Event.find(id).map {
        event ⇒
          val account = request.user.asInstanceOf[LoginIdentity].userAccount
          val brands = Brand.findForUser(account)
          Ok(views.html.event.form(request.user, None, brands, account.personId, eventForm.fill(event)))
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
          val brands = Brand.findForUser(account)
          BadRequest(views.html.event.form(request.user, None, brands, account.personId, formWithErrors))
        },
        event ⇒ {
          val validLicensees = License.licensees(event.brandCode)
          val coordinator = Brand.find(event.brandCode).get.coordinator
          if (event.facilitatorIds.forall(id ⇒ { validLicensees.exists(_.id.get == id) || coordinator.id.get == id })) {
            val eventObj = event.insert

            val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, eventObj.title)
            Redirect(routes.Events.index()).flashing("success" -> activity.toString)
          } else {
            val account = request.user.asInstanceOf[LoginIdentity].userAccount
            val brands = Brand.findForUser(account)
            BadRequest(views.html.event.form(request.user, None, brands, account.personId,
              form.withError("facilitatorIds", "Some facilitators do not have valid licenses")))
          }
        })
  }

  /**
   * Deletes an event.
   * @param id Event ID
   */
  def delete(id: Long) = SecuredDynamicAction("event", "edit") { implicit request ⇒
    implicit handler ⇒

      Event.find(id).map {
        event ⇒
          Event.delete(id)
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, event.title)
          Redirect(routes.Events.index).flashing("success" -> activity.toString)
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
          Ok(views.html.event.details(request.user, event))
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
          val brands = Brand.findForUser(account)
          Ok(views.html.event.form(request.user, Some(id), brands, account.personId, eventForm.fill(event)))
      }.getOrElse(NotFound)
  }

  /**
   * List page.
   */
  def index = SecuredDynamicAction("event", "view") { implicit request ⇒
    implicit handler ⇒

      val events = Event.findAll.sortBy(_.schedule.start.toDate).reverse
      Ok(views.html.event.index(request.user, events))
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
          val brands = Brand.findForUser(account)
          BadRequest(views.html.event.form(request.user, Some(id), brands, account.personId, formWithErrors))
        },
        event ⇒ {
          val validLicensees = License.licensees(event.brandCode)
          val coordinator = Brand.find(event.brandCode).get.coordinator
          if (event.facilitatorIds.forall(id ⇒ { validLicensees.exists(_.id.get == id) || coordinator.id.get == id })) {
            event.copy(id = Some(id)).update

            val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, event.title)
            Redirect(routes.Events.index()).flashing("success" -> activity.toString)
          } else {
            val account = request.user.asInstanceOf[LoginIdentity].userAccount
            val brands = Brand.findForUser(account)
            BadRequest(views.html.event.form(request.user, Some(id), brands, account.personId,
              form.withError("facilitatorIds", "Some facilitators do not have valid licenses")))
          }
        })
  }

}
