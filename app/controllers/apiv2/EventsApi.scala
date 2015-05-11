/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers.apiv2

import models.Event
import models.service.Services
import play.api.libs.json._
import play.api.mvc._

/**
 * Events API
 */
trait EventsApi extends Controller with ApiAuthentication with Services {

  import PeopleApi.personWrites

  val eventDetailsWrites = new Writes[Event] {
    def writes(event: Event): JsValue = {
      Json.obj(
        "brand" -> event.brandId,
        "type" -> event.eventTypeId,
        "title" -> event.title,
        "description" -> event.details.description,
        "spokenLanguages" -> event.spokenLanguages,
        "materialsLanguage" -> event.materialsLanguage,
        "specialAttention" -> event.details.specialAttention,
        "start" -> event.schedule.start,
        "end" -> event.schedule.end,
        "hoursPerDay" -> event.schedule.hoursPerDay,
        "totalHours" -> event.schedule.totalHours,
        "facilitators" -> event.facilitators,
        "city" -> event.location.city,
        "country" -> event.location.countryCode,
        "website" -> event.details.webSite,
        "registrationPage" -> event.details.registrationPage,
        "rating" -> event.rating,
        "public" -> !event.notPublic,
        "archived" -> event.archived,
        "confirmed" -> event.confirmed,
        "free" -> event.free)
    }
  }

  /**
   * Returns event in JSON format
   *
   * @param id Event identifier
   */
  def event(id: Long) = TokenSecuredAction(readWrite = false) {
    implicit request ⇒
      implicit token ⇒
        eventService find id map { event ⇒
          jsonOk(Json.toJson(event)(eventDetailsWrites))
        } getOrElse jsonNotFound("Unknown event")
  }

  /**
   * Returns a list of events based on several parameters in JSON format
   *
   * @param code Only events of this brand
   * @param future Only future and current events
   * @param public Only public events
   * @param archived Only archived events
   * @param facilitatorId Only events by this facilitator
   * @param countryCode Only events in this country
   * @param eventType Only events of this type
   */
  def events(code: String,
    future: Option[Boolean],
    public: Option[Boolean],
    archived: Option[Boolean],
    facilitatorId: Option[Long],
    countryCode: Option[String],
    eventType: Option[Long]) = TokenSecuredAction(readWrite = false) {
    implicit request ⇒
      implicit token ⇒
        brandService.find(code) map { x ⇒
          val types = eventTypeService.
            findByBrand(x.id.get).
            map(y ⇒ y.id.get -> y.name).toMap
          val events = facilitatorId map { value ⇒
            eventsByFacilitator(value, x.id, future, public)
          } getOrElse {
            eventsByBrand(x.id, future, public, archived, countryCode, eventType)
          }
          eventService.applyFacilitators(events)
          jsonOk(eventsToJson(events, types))
        } getOrElse jsonNotFound("Unknown brand")
  }

  /**
   * Returns event list in JSON format
   *
   * @param events List of events
   * @param types List of event types belonged to the given brand
   */
  protected def eventsToJson(events: List[Event], types: Map[Long, String]): JsValue = {
    implicit val eventWrites = new Writes[Event] {
      def writes(event: Event): JsValue = {
        val typeName: String = types.getOrElse(event.eventTypeId, "")
        Json.obj(
          "id" -> event.id.get,
          "title" -> event.title,
          "type" -> event.eventTypeId,
          "typeName" -> typeName,
          "description" -> event.details.description,
          "spokenLanguages" -> event.spokenLanguages,
          "materialsLanguage" -> event.materialsLanguage,
          "specialAttention" -> event.details.specialAttention,
          "start" -> event.schedule.start,
          "end" -> event.schedule.end,
          "hoursPerDay" -> event.schedule.hoursPerDay,
          "totalHours" -> event.schedule.totalHours,
          "facilitators" -> event.facilitators,
          "city" -> event.location.city,
          "country" -> event.location.countryCode,
          "website" -> event.details.webSite,
          "registrationPage" -> event.details.registrationPage,
          "rating" -> event.rating,
          "confirmed" -> event.confirmed,
          "free" -> event.free)
      }
    }
    Json.toJson(events)
  }

  /**
   * Returns list of events for the given facilitator*
   *
   * @param facilitatorId Only events by this facilitator
   * @param brandId Brand id
   * @param future Only future and current events
   * @param public Only public events
   */
  protected def eventsByFacilitator(facilitatorId: Long,
    brandId: Option[Long],
    future: Option[Boolean],
    public: Option[Boolean]): List[Event] = {
    eventService.findByFacilitator(facilitatorId, brandId, future, public, archived = Some(false))
  }

  /**
   * Returns a list of events for the given brand
   *
   * @param brandId Brand identifier
   * @param future Only future and current events
   * @param public Only public events
   * @param archived Only archived events
   * @param countryCode Only events in this country
   * @param eventType Only events of this type
   */
  protected def eventsByBrand(brandId: Option[Long],
    future: Option[Boolean],
    public: Option[Boolean],
    archived: Option[Boolean],
    countryCode: Option[String],
    eventType: Option[Long]): List[Event] = {
    eventService.findByParameters(brandId, future, public, archived, None, countryCode, eventType)
  }
}

object EventsApi extends EventsApi with ApiAuthentication
