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

import models.ActiveUser
import models.UserRole.DynamicRole
import models.UserRole.Role._
import models.brand.EventType
import models.service.Services
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json.{ JsValue, Writes, Json }
import securesocial.core.RuntimeEnvironment

class EventTypes(environment: RuntimeEnvironment[ActiveUser])
    extends JsonController
    with Security
    with Services
    with Activities {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  /** HTML form mapping for creating and editing. */
  def eventTypeForm = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "brandId" -> longNumber(min = 1),
    "name" -> nonEmptyText(maxLength = 254),
    "title" -> optional(text(maxLength = 254)),
    "maxHours" -> number(min = 1),
    "free" -> boolean)(EventType.apply)(EventType.unapply))

  implicit val eventTypeWrites = new Writes[EventType] {
    def writes(data: EventType): JsValue = {
      Json.obj(
        "name" -> data.name,
        "title" -> data.defaultTitle,
        "maxhours" -> data.maxHours,
        "free" -> data.free,
        "id" -> data.id.get)
    }
  }

  /**
   * Renders add form for event type for the given brand
   *
   * @param brandId Brand identifier
   */
  def add(brandId: Long) = SecuredBrandAction(brandId) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        brandService.find(brandId) map { brand ⇒
          Ok(views.html.eventtype.form(user, brand, eventTypeForm))
        } getOrElse Redirect(routes.Brands.index()).
          flashing("error" -> Messages("error.brand.notFound"))
  }

  /**
   * Creates a new event type for the given brand
   *
   * @param brandId Brand identifier
   */
  def create(brandId: Long) = SecuredBrandAction(brandId) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        val form = eventTypeForm.bindFromRequest
        brandService.find(brandId) map { brand ⇒
          form.fold(
            withErrors ⇒
              BadRequest(views.html.eventtype.form(user, brand, withErrors)),
            received ⇒ validateEventType(brandId, received) map { x ⇒
              val withErrors = form.withError(x._1, x._2)
              BadRequest(views.html.eventtype.form(user, brand, withErrors))
            } getOrElse {
              println(received)
              val inserted = eventTypeService.insert(received.copy(brandId = brandId))
              val log = activity(inserted, user.person, Some(brand)).connected.insert()
              val route = routes.Brands.details(brandId).url + "#types"
              Redirect(route).flashing("success" -> log.toString)
            })
        } getOrElse Redirect(routes.Brands.index()).
          flashing("error" -> Messages("error.brand.notFound"))
  }

  /**
   * Deletes an event type
   *
   * @param id Type identifier
   */
  def delete(brandId: Long, id: Long) = SecuredBrandAction(brandId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      eventTypeService.find(id).map { eventType ⇒
        val brand = eventType.brand
        val route = routes.Brands.details(brand.id.get).url + "#types"
        val events = eventService.findByParameters(
          brandId = None,
          eventType = Some(eventType.id.get))
        if (events.length > 0) {
          Redirect(route).flashing("error" -> Messages("error.eventType.tooManyEvents"))
        } else {
          eventTypeService.delete(id)
          val log = activity(eventType, user.person, Some(brand)).disconnected.insert()
          Redirect(route).flashing("success" -> log.toString)
        }
      }.getOrElse(NotFound)
  }

  /**
   * Returns a list of event types for the given brand in JSON format
   *
   * @param brandId Brand id
   */
  def index(brandId: Long) = SecuredRestrictedAction(BrandViewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(Json.toJson(eventTypeService.findByBrand(brandId)))
  }

  /**
   * Updates the given event type
   *
   * @param id Event type identifier
   */
  def update(brandId: Long, id: Long) = SecuredBrandAction(brandId) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        eventTypeForm.bindFromRequest.fold(
          hasErrors ⇒ jsonBadRequest(Messages("error.eventType.wrongParameters")),
          updated ⇒ brandService.find(updated.brandId) map { brand ⇒
            validateUpdatedEventType(id, updated) map { x ⇒
              jsonRequest(x._1, Messages(x._2))
            } getOrElse {
              eventTypeService.update(updated.copy(id = Some(id), brandId = updated.brandId))
              jsonSuccess("success")
            }
          } getOrElse jsonBadRequest(Messages("error.brand.notFound")))
  }

  /**
   * Validates updated event type and
   *
   * @param id Updated event type identifier
   * @param value Event type object
   * @return returns a validation error if it's invalid
   */
  protected def validateUpdatedEventType(id: Long,
    value: EventType): Option[(Int, String)] = {
    eventTypeService.find(id) map { x ⇒
      val types = eventTypeService.findByBrand(value.brandId)
      if (!types.exists(_.id == Some(id))) {
        Some((BAD_REQUEST, "error.eventType.wrongBrand"))
      } else {
        if (types.exists(y ⇒ y.name == value.name && y.id != Some(id)))
          Some((CONFLICT, "error.eventType.nameExists"))
        else
          None
      }
    } getOrElse Some((BAD_REQUEST, "error.eventType.notFound"))
  }

  /**
   * Validates event type for the given brand
   *
   * @param brandId Brand identifier
   * @param value Event type object
   * @return returns a validation error if invalid
   */
  protected def validateEventType(brandId: Long,
    value: EventType): Option[(String, String)] = {
    val types = eventTypeService.findByBrand(brandId)
    if (types.exists(y ⇒ y.name == value.name))
      Some(("name", "error.eventType.nameExists"))
    else
      None
  }
}
