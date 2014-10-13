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

import models.{ Event, Brand, EventType, Activity }
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.UserRole.Role._
import securesocial.core.SecuredRequest
import play.api.i18n.Messages
import play.api.libs.json.{ JsValue, Writes, Json }

object EventTypes extends Controller with Security {

  /** HTML form mapping for creating and editing. */
  def eventTypeForm(implicit request: SecuredRequest[_]) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "brandId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString).verifying((brandId: Long) ⇒ Brand.find(brandId).isDefined),
    "name" -> nonEmptyText(maxLength = 254),
    "defaultTitle" -> optional(text(maxLength = 254)))(EventType.apply)(EventType.unapply))

  implicit val eventTypeWrites = new Writes[EventType] {
    def writes(data: EventType): JsValue = {
      Json.obj(
        "name" -> data.name,
        "defaultTitle" -> data.defaultTitle,
        "id" -> data.id.get)
    }
  }

  /**
   * Returns a list of event types for the given brand
   *
   * @param brandCode Brand code
   */
  def index(brandCode: String) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒
      Brand.find(brandCode).map { brand ⇒
        Ok(Json.toJson(EventType.findByBrand(brand.brand.id.get)))
      }.getOrElse(NotFound("Unknown brand"))
  }

  /**
   * Creates a new event type
   */
  def create = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      val boundForm: Form[EventType] = eventTypeForm.bindFromRequest
      val brand = Brand.find(boundForm.data("brandId").toLong).get
      val route = routes.Brands.details(brand.code).url + "#eventTypes"
      boundForm.bindFromRequest.fold(
        formWithErrors ⇒ Redirect(route).flashing("error" -> Messages.apply("error.eventType.nameWrongLength")),
        eventType ⇒ {
          eventType.insert
          val activityObject = Messages("activity.eventType.create", brand.name, eventType.name)
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, activityObject)
          Redirect(route).flashing("success" -> activity.toString)
        })
  }

  /**
   * Deletes an event type
   *
   * @param id Type identifier
   */
  def delete(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      EventType.find(id).map { eventType ⇒
        val brand = eventType.brand
        val route = routes.Brands.details(brand.code).url + "#eventTypes"
        if (Event.getNumberByEventType(eventType.id.get) > 0) {
          Redirect(route).flashing("error" -> Messages.apply("error.eventType.tooManyEvents"))
        } else {
          EventType.delete(id)
          val activityObject = Messages("activity.eventType.delete", brand.name, eventType.name)
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, activityObject)
          Redirect(route).flashing("success" -> activity.toString)
        }
      }.getOrElse(NotFound)
  }

}
