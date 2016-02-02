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

import javax.inject.Inject

import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import be.objectify.deadbolt.scala.cache.HandlerCache
import models.UserRole.Role._
import models.brand.EventType
import models.service.Services
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{MessagesApi, I18nSupport, Messages}
import play.api.libs.json.{JsValue, Json, Writes}
import services.TellerRuntimeEnvironment
import scala.concurrent.Future

class EventTypes @Inject() (override implicit val env: TellerRuntimeEnvironment,
                            override val messagesApi: MessagesApi,
                            deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder)(messagesApi, env)
  with Activities
  with I18nSupport {

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
  def add(brandId: Long) = AsyncSecuredBrandAction(brandId) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    brandService.find(brandId) flatMap {
      case None => redirect(routes.Brands.index(), "error" -> Messages("error.brand.notFound"))
      case Some(brand) =>
        ok(views.html.v2.eventtype.form(user, brand, eventTypeForm))
    }
  }

  /**
   * Creates a new event type for the given brand
   *
   * @param brandId Brand identifier
   */
  def create(brandId: Long) = AsyncSecuredBrandAction(brandId) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val form = eventTypeForm.bindFromRequest
    brandService.find(brandId) flatMap {
      case None => redirect(routes.Brands.index(), "error" -> Messages("error.brand.notFound"))
      case Some(brand) =>
        form.fold(
          withErrors ⇒ badRequest(views.html.v2.eventtype.form(user, brand, withErrors)),
          received ⇒ validateEventType(brandId, received) flatMap {
            case None =>
              eventTypeService.insert(received.copy(brandId = brandId)) flatMap { eventType =>
                val route: String = routes.Brands.details(brandId).url + "#types"
                redirect(route, "success" -> "Event type was added")
              }
            case Some(result) ⇒
              val withErrors = form.withError(result._1, result._2)
              badRequest(views.html.v2.eventtype.form(user, brand, withErrors))
          })
    }
  }

  /**
   * Deletes an event type
   *
   * @param id Type identifier
   */
  def delete(brandId: Long, id: Long) = AsyncSecuredBrandAction(brandId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      (for {
        eventType <- eventTypeService.find(id)
        events <- eventService.findByParameters(brandId = None, eventType = Some(id))
      } yield (eventType, events)) flatMap {
        case (None, _) => notFound("")
        case (Some(eventType), events) =>
          val brand = eventType.brand
          val route: String = routes.Brands.details(brand.id.get).url + "#types"
          if (events.nonEmpty) {
            redirect(route, "error" -> Messages("error.eventType.tooManyEvents"))
          } else {
            eventTypeService.delete(id)
            redirect(route, "success" -> "Event type was deleted")
          }
      }
  }

  /**
   * Returns a list of event types for the given brand in JSON format
   *
   * @param brandId Brand id
   */
  def index(brandId: Long) = AsyncSecuredRestrictedAction(List(Facilitator, Coordinator)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      eventTypeService.findByBrand(brandId) flatMap { eventType =>
        ok(Json.toJson(eventType))
      }
  }

  /**
   * Updates the given event type
   *
   * @param id Event type identifier
   */
  def update(brandId: Long, id: Long) = AsyncSecuredBrandAction(brandId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      eventTypeForm.bindFromRequest.fold(
        hasErrors ⇒ jsonBadRequest(Messages("error.eventType.wrongParameters")),
        updated ⇒ brandService.find(updated.brandId) flatMap {
          case None => jsonBadRequest(Messages("error.brand.notFound"))
          case Some(brand) =>
            validateUpdatedEventType(id, updated) flatMap {
              case None =>
                eventTypeService.update(updated.copy(id = Some(id), brandId = updated.brandId))
                jsonSuccess("success")
              case Some(result) =>
                jsonRequest(result._1, Messages(result._2))
            }
        })
  }

  /**
   * Validates updated event type and
   *
   * @param id Updated event type identifier
   * @param value Event type object
   * @return returns a validation error if it's invalid
   */
  protected def validateUpdatedEventType(id: Long, value: EventType): Future[Option[(Int, String)]] = {
    (for {
      eventType <- eventTypeService.find(id)
      types <- eventTypeService.findByBrand(value.brandId)
    } yield (eventType, types)) map {
      case (None, _) => Some((BAD_REQUEST, "error.eventType.notFound"))
      case (Some(eventType), types) =>
        if (!types.exists(_.id.contains(id))) {
          Some((BAD_REQUEST, "error.eventType.wrongBrand"))
        } else {
          if (types.exists(y ⇒ y.name == value.name && !y.id.contains(id)))
            Some((CONFLICT, "error.eventType.nameExists"))
          else
            None
        }
    }
  }

  /**
   * Validates event type for the given brand
   *
   * @param brandId Brand identifier
   * @param value Event type object
   * @return returns a validation error if invalid
   */
  protected def validateEventType(brandId: Long, value: EventType): Future[Option[(String, String)]] = {
    eventTypeService.findByBrand(brandId) map { types =>
      if (types.exists(y ⇒ y.name == value.name))
        Some(("name", "error.eventType.nameExists"))
      else
        None
    }
  }
}
