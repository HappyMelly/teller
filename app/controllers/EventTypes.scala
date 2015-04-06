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

import models.brand.EventType
import models.service.{ Services, EventService }
import models.{ Event, Brand, Activity }
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.UserRole.Role._
import securesocial.core.SecuredRequest
import play.api.i18n.Messages
import play.api.libs.json.{ JsValue, Writes, Json }

trait EventTypes extends Controller with Security with Services {

  /** HTML form mapping for creating and editing. */
  def eventTypeForm = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "brandId" -> nonEmptyText.transform(_.toLong,
      (l: Long) ⇒ l.toString).verifying((brandId: Long) ⇒ brandService.find(brandId).isDefined),
    "name" -> nonEmptyText(maxLength = 254),
    "title" -> optional(text(maxLength = 254)),
    "maxhours" -> number(min = 1))(EventType.apply)(EventType.unapply))

  implicit val eventTypeWrites = new Writes[EventType] {
    def writes(data: EventType): JsValue = {
      Json.obj(
        "name" -> data.name,
        "title" -> data.defaultTitle,
        "maxhours" -> data.maxHours,
        "id" -> data.id.get)
    }
  }

  /**
   * Returns a list of event types for the given brand in JSON format
   *
   * @param brandCode Brand code
   */
  def index(brandCode: String) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      brandService.find(brandCode) map { brand ⇒
        Ok(Json.toJson(eventTypeService.findByBrand(brand.id.get)))
      } getOrElse NotFound("Unknown brand")
  }

  /**
   * Creates a new event type
   */
  def create = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val boundForm: Form[EventType] = eventTypeForm.bindFromRequest
      val brand = brandService.find(boundForm.data("brandId").toLong).get
      val route = routes.Brands.details(brand.code).url + "#types"
      boundForm.bindFromRequest.fold(
        formWithErrors ⇒ Redirect(route).flashing("error" -> Messages.apply("error.eventType.nameWrongLength")),
        eventType ⇒ {
          val et = eventType.insert
          val activity = et.activity(user.person,
            Activity.Predicate.Connected,
            Some(brand)).insert
          Redirect(route).flashing("success" -> activity.toString)
        })
  }

  /**
   * Updates the given event type
   * @param id Event type identifier
   */
  def update(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      eventTypeService.find(id) map { x ⇒
        eventTypeForm.bindFromRequest.fold(
          hasErrors ⇒ {
            BadRequest(Json.toJson(Json.obj(
              "message" -> Messages("event.eventType.notFound"))))
          },
          updated ⇒ {
            val types = eventTypeService.findByBrand(updated.brandId)
            if (types.exists(y ⇒ y.name == updated.name && y.id.get != id)) {
              BadRequest(Json.toJson(Json.obj(
                "message" -> Messages("event.eventType.nameExists"))))
            } else {
              eventTypeService.update(updated.copy(id = Some(id)))
              Ok(Json.obj("message" -> "success"))
            }
          })
      } getOrElse
        NotFound(Json.toJson(Json.obj(
          "message" -> Messages("event.eventType.notFound"))))
  }

  /**
   * Deletes an event type
   *
   * @param id Type identifier
   */
  def delete(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      eventTypeService.find(id).map { eventType ⇒
        val brand = eventType.brand
        val route = routes.Brands.details(brand.code).url + "#types"
        val events = eventService.findByParameters(
          brandCode = None,
          eventType = Some(eventType.id.get))
        if (events.length > 0) {
          Redirect(route).flashing("error" -> Messages.apply("error.eventType.tooManyEvents"))
        } else {
          EventType.delete(id)
          val activity = eventType.activity(user.person,
            Activity.Predicate.Disconnected,
            Some(brand)).insert
          Redirect(route).flashing("success" -> activity.toString)
        }
      }.getOrElse(NotFound)
  }

}

object EventTypes extends EventTypes