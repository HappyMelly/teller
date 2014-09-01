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

import play.mvc.Controller
import play.api.libs.json._
import models.{ Brand, Event }
import views.Languages

/**
 * Events API
 */
object EventsApi extends Controller with ApiAuthentication {

  import PeopleApi.personWrites

  implicit val eventWrites = new Writes[Event] {
    def writes(event: Event): JsValue = {
      Json.obj(
        "id" -> event.id.get,
        "title" -> event.title,
        "type" -> event.eventTypeId,
        "description" -> event.details.description,
        "spokenLanguage" -> Languages.all.get(event.spokenLanguage),
        "materialsLanguage" -> Languages.all.get(event.materialsLanguage.getOrElse("")),
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

  val noFacilitatorsEventsWrites = new Writes[List[Event]] {
    def writes(events: List[Event]): JsValue = {
      val serializedEvents = events.map { event ⇒
        Json.obj(
          "id" -> event.id.get,
          "title" -> event.title,
          "type" -> event.eventTypeId,
          "description" -> event.details.description,
          "spokenLanguage" -> Languages.all.get(event.spokenLanguage),
          "materialsLanguage" -> Languages.all.get(event.materialsLanguage.getOrElse("")),
          "specialAttention" -> event.details.specialAttention,
          "start" -> event.schedule.start,
          "end" -> event.schedule.end,
          "hoursPerDay" -> event.schedule.hoursPerDay,
          "totalHours" -> event.schedule.totalHours,
          "city" -> event.location.city,
          "country" -> event.location.countryCode,
          "website" -> event.details.webSite,
          "registrationPage" -> event.details.registrationPage)
      }
      JsArray(serializedEvents)
    }
  }

  val eventDetailsWrites = new Writes[Event] {
    def writes(event: Event): JsValue = {
      Json.obj(
        "brand" -> event.brandCode,
        "type" -> event.eventTypeId,
        "title" -> event.title,
        "description" -> event.details.description,
        "spokenLanguage" -> Languages.all.get(event.spokenLanguage),
        "materialsLanguage" -> Languages.all.get(event.materialsLanguage.getOrElse("")),
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
   * A list of countries with a number of events for a given brand
   */
  def countries(brandCode: String) = TokenSecuredAction { implicit request ⇒
    Brand.find(brandCode).map { brandView ⇒
      Ok(Json.toJson(Event.findByBrandGroupByCountry(brandCode)))
    }.getOrElse(NotFound("Unknown brand"))
  }

  /**
   * Event details API.
   */
  def event(id: Long) = TokenSecuredAction { implicit request ⇒
    Event.find(id).map { event ⇒
      Ok(Json.toJson(event)(eventDetailsWrites))
    }.getOrElse(NotFound("Unknown event"))
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
    val events: List[Event] = facilitatorId.map { value ⇒
      Event.findByFacilitator(value, code, future, public)
    }.getOrElse {
      Event.findByParameters(code, future, public, archived, None, countryCode, eventType)
    }
    Ok(Json.toJson(events))
  }
}
