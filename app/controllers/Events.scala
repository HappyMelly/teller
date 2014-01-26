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
import models.{ Schedule, Activity, Event }
import play.api.mvc._
import securesocial.core.{ SecuredRequest, SecureSocial }
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import org.joda.time.DateTime
import models.UserRole.Role._
import securesocial.core.SecuredRequest
import scala.Some
import play.api.data.format.Formatter

object Events extends Controller with Security {

  /**
   * HTML form mapping for creating and editing.
   */
  def eventForm(implicit request: SecuredRequest[_]) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "brandId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString),
    "title" -> nonEmptyText,
    "spokenLanguage" -> nonEmptyText,
    "materialsLanguage" -> optional(text),
    "city" -> nonEmptyText,
    "country" -> nonEmptyText,
    "description" -> optional(text),
    "specialAttention" -> optional(text),
    "schedule" -> mapping(
      "start" -> jodaLocalDate,
      "end" -> jodaLocalDate,
      "hoursPerDay" -> number(1, 24, true))(Schedule.apply)(Schedule.unapply).verifying(
        "error.date.range", (schedule: Schedule) ⇒ !schedule.start.isAfter(schedule.end)),
    "webSite" -> optional(webUrl),
    "registrationPage" -> optional(webUrl),
    "isPrivate" -> default(boolean, false),
    "isArchived" -> default(boolean, false),
    "created" -> ignored(DateTime.now()),
    "createdBy" -> ignored(request.user.fullName),
    "updated" -> ignored(DateTime.now()),
    "updatedBy" -> ignored(request.user.fullName))(Event.apply)(Event.unapply))

  /**
   * Create page.
   */
  def add = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Ok(views.html.event.form(request.user, None, eventForm))
  }

  /**
   * Create form submits to this action.
   */
  def create = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      eventForm.bindFromRequest.fold(
        formWithErrors ⇒
          BadRequest(views.html.event.form(request.user, None, formWithErrors)),
        event ⇒ {
          val eventObj = event.insert
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, eventObj.title)
          Redirect(routes.Events.index()).flashing("success" -> activity.toString)
        })
  }

  //  /**
  //   * Deletes an organisation.
  //   * @param id Organisation ID
  //   */
  //  def delete(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
  //    implicit handler ⇒
  //
  //      Event.find(id).map {
  //        event ⇒
  //          Event.delete(id)
  //          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, event.name)
  //          Redirect(routes.Events.index).flashing("success" -> activity.toString)
  //      }.getOrElse(NotFound)
  //  }
  //
  //  /**
  //   * Details page.
  //   * @param id Organisation ID
  //   */
  //  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
  //    implicit handler ⇒
  //
  //      Event.find(id).map {
  //        event ⇒
  //          val members = event.members
  //          val otherPeople = Person.findActive.filterNot(person ⇒ members.contains(person))
  //          val contributions = Contribution.contributions(id, false)
  //          val products = Product.findAll
  //
  //          Ok(views.html.organisation.details(request.user, organisation,
  //            members, otherPeople,
  //            contributions, products))
  //      } getOrElse {
  //        //TODO return 404
  //        Redirect(routes.Organisations.index).flashing("error" -> Messages("error.notFound", Messages("models.Organisation")))
  //      }
  //  }
  //
  //  /**
  //   * Edit page.
  //   * @param id Organisation ID
  //   */
  //  def edit(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
  //    implicit handler ⇒
  //
  //      Organisation.find(id).map {
  //        organisation ⇒
  //          Ok(views.html.organisation.form(request.user, Some(id), organisationForm.fill(organisation)))
  //      }.getOrElse(NotFound)
  //  }

  /**
   * List page.
   */
  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒

      val events = Event.findAll
      Ok(views.html.event.index(request.user, events))
  }

  /**
   * Edit form submits to this action.
   * @param id Event ID
   */
  def update(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      eventForm.bindFromRequest.fold(
        formWithErrors ⇒
          BadRequest(views.html.event.form(request.user, Some(id), formWithErrors)),
        event ⇒ {
          event.copy(id = Some(id)).update
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, event.title)
          Redirect(routes.Events.index).flashing("success" -> activity.toString)
        })
  }

}
