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
package controllers.api

import javax.inject.Inject

import models.brand.EventType
import models.repository.Repositories
import play.api.i18n.MessagesApi
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Event types API
 */
class EventTypesApi @Inject() (val services: Repositories,
                               override val messagesApi: MessagesApi) extends ApiAuthentication(services, messagesApi) {

  implicit val eventTypeWrites = new Writes[(EventType, String)] {
    def writes(view: (EventType, String)): JsValue = {
      Json.obj(
        "id" -> view._1.id,
        "brand" -> view._2,
        "name" -> view._1.name,
        "title" -> view._1.defaultTitle)
    }
  }

  /**
   * Returns a list of event types for the given brand in JSON format
 *
   * @param code Brand code
   */
  def types(code: String) = TokenSecuredAction(readWrite = false) { implicit request ⇒ implicit token ⇒
    services.brand.find(code) flatMap {
      case None => jsonNotFound("Brand not found")
      case Some(brand) =>
        services.eventType.findByBrand(brand.identifier) flatMap { eventTypes =>
          jsonOk(Json.toJson(eventTypes.map(x => (x, code))))
        }
    }
  }
}
