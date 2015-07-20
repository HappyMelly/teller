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
package controllers.api

import models.Event
import models.service.Services
import play.api.libs.json._
import play.api.mvc._

/**
 * Events API
 */
trait EventsApi extends Controller with ApiAuthentication with Services {

  import PeopleApi.personWrites

  implicit val eventWrites = new Writes[Event] {
    def writes(event: Event): JsValue = {
      Json.obj(
        "id" -> event.id.get,
        "title" -> event.title,
        "type" -> event.eventTypeId,
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
        "website" -> event.organizer.webSite,
        "registrationPage" -> event.organizer.registrationPage)
    }
  }

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
        "website" -> event.organizer.webSite,
        "registrationPage" -> event.organizer.registrationPage,
        "public" -> !event.notPublic,
        "archived" -> event.archived)
    }
  }

  /**
   * Returns event in JSON format
   *
   * @param id Event identifier
   */
  def event(id: Long) = TokenSecuredAction { implicit request ⇒
    eventService find id map { event ⇒
      Ok(Json.prettyPrint(Json.toJson(event)(eventDetailsWrites)))
    } getOrElse NotFound("Unknown event")
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
    eventType: Option[Long]) = TokenSecuredAction { implicit request ⇒

    brandService.find(code) map { x ⇒
      val events: List[Event] = facilitatorId map { value ⇒
        eventService.findByFacilitator(
          value,
          x.id,
          future,
          public,
          archived = Some(false))
      } getOrElse {
        eventService.findByParameters(
          x.id,
          future,
          public,
          archived,
          None,
          countryCode,
          eventType)
      }
      eventService.applyFacilitators(events)
      Ok(Json.prettyPrint(Json.toJson(events)))
    } getOrElse NotFound("Unknown brand")
  }
}

object EventsApi extends EventsApi with ApiAuthentication
