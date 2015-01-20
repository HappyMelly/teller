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

import models.service.EventService
import play.api.mvc._
import play.api.libs.json._
import models.{ Brand, Event }

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
        "website" -> event.details.webSite,
        "registrationPage" -> event.details.registrationPage)
    }
  }

  val eventDetailsWrites = new Writes[Event] {
    def writes(event: Event): JsValue = {
      Json.obj(
        "brand" -> event.brandCode,
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
        "public" -> !event.notPublic,
        "archived" -> event.archived)
    }
  }

  implicit val countryInfoWrites = new Writes[(String, Int)] {
    def writes(countryInfo: (String, Int)): JsValue = {
      Json.obj(
        "country" -> countryInfo._1,
        "eventsNumber" -> countryInfo._2)
    }
  }

  /**
   * Returns a list of countries with a number of events
   *
   * @param code Brand identifier
   */
  def countries(code: String) = TokenSecuredAction { implicit request ⇒
    Brand.find(code).map { brandView ⇒
      val events = eventService.findByParameters(Some(code), future = Some(true))
      val data = events.groupBy(_.location.countryCode).map(v ⇒ (v._1, v._2.length))
      Ok(Json.prettyPrint(Json.toJson(data.toList.sortBy(_._1))))
    }.getOrElse(NotFound("Unknown brand"))
  }

  /**
   * Event details API.
   */
  def event(id: Long) = TokenSecuredAction { implicit request ⇒
    eventService find id map { event ⇒
      Ok(Json.prettyPrint(Json.toJson(event)(eventDetailsWrites)))
    } getOrElse NotFound("Unknown event")
  }

  /**
   * Events list
   */
  def events(code: String,
    future: Option[Boolean],
    public: Option[Boolean],
    archived: Option[Boolean],
    facilitatorId: Option[Long],
    countryCode: Option[String],
    eventType: Option[Long]) = TokenSecuredAction { implicit request ⇒

    val events: List[Event] = facilitatorId map { value ⇒
      eventService.findByFacilitator(
        value,
        Some(code),
        future,
        public,
        archived = Some(false))
    } getOrElse {
      eventService.findByParameters(
        Some(code),
        future,
        public,
        archived,
        None,
        countryCode,
        eventType)
    }
    eventService.applyFacilitators(events)
    Ok(Json.prettyPrint(Json.toJson(events)))
  }
}

object EventsApi extends EventsApi with ApiAuthentication
