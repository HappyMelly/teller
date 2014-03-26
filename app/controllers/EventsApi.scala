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

import play.mvc.Controller
import play.api.libs.json._
import models.Event
import play.api.i18n.Messages

/**
 * Events API
 */
object EventsApi extends Controller with ApiAuthentication {

  import PeopleApi.personWrites

  implicit val eventWrites = new Writes[Event] {
    def writes(event: Event): JsValue = {
      Json.obj(
        //        "href" -> event.id.map(id ⇒ routes.EventsApi.event(id).url),
        "title" -> event.title,
        "description" -> event.details.description,
        "start" -> event.schedule.start,
        "end" -> event.schedule.end,
        "totalHours" -> event.schedule.totalHours,
        "facilitators" -> event.facilitators,
        "city" -> event.location.city,
        "country" -> Json.obj(
          "code" -> event.location.countryCode,
          "name" -> Messages("country." + event.location.countryCode)),
        "website" -> event.details.webSite,
        "registrationPage" -> event.details.registrationPage)
    }
  }

  /**
   * Event details API.
   */
  //  def Event(id: Long) = TokenSecuredAction { implicit request ⇒
  //    Event.find(id).map { Event ⇒
  //      Ok(Json.toJson(Event)(EventDetailsWrites))
  //    }.getOrElse(NotFound("Unknown organization"))
  //  }

  /**
   * Event list API.
   */
  def events(brand: String, future: Option[Boolean], public: Option[Boolean], countryCode: Option[String]) = TokenSecuredAction { implicit request ⇒
    Ok(Json.toJson(Event.findByBrandTimeAndPublicity(brand, future, public, countryCode)))
  }
}
