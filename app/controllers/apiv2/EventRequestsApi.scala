/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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

import javax.inject.Inject

import models.event.EventRequest
import models.repository.Repositories
import models.{APIError, DateStamp}
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.libs.json._
import views.Countries

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * API for adding event requests
 */
class EventRequestsApi @Inject() (val services: Repositories,
                                  override val messagesApi: MessagesApi)
  extends ApiAuthentication(services, messagesApi) {

  def form(brandId: Long, appName: String) = Form(mapping(
    "country" -> nonEmptyText.verifying(
      "error.unknown_country",
      (country: String) ⇒ Countries.all.exists(_._1 == country)),
    "city" -> optional(nonEmptyText),
    "language" -> nonEmptyText,
    "start_date" -> optional(jodaLocalDate),
    "end_date" -> optional(jodaLocalDate),
    "number_of_participants" -> number,
    "comment" -> optional(text),
    "name" -> nonEmptyText,
    "email" -> email)({
    (country, city, language, start, end, participantsNumber, comment, name,
      email) => EventRequest(None, brandId, country, city, language, start,
      end, participantsNumber, comment, name, email,
      recordInfo = DateStamp(DateTime.now(), appName, DateTime.now(), appName))
  })({
    (r: EventRequest) => Some((r.countryCode, r.city, r.language, r.start,
      r.end, r.participantsNumber, r.comment, r.name, r.email))
  }))

  /**
   * Create an event request through API call
    *
    * @param brandCode Brand string identifier
   */
  def create(brandCode: String) = TokenSecuredAction(readWrite = true) { implicit request ⇒ implicit token ⇒
    val name = token.appName

    services.brand.find(brandCode) flatMap {
      case None => jsonNotFound(s"Brand $brandCode not found")
      case Some(brand) =>
        val requestData = form(brand.identifier, name).bindFromRequest()
        requestData.fold(
          erroneousData => {
            val json = Json.toJson(APIError.formValidationError(erroneousData.errors))
            badRequest(Json.prettyPrint(json))
          },
          eventRequest =>
            services.eventRequest.insert(eventRequest) flatMap { value =>
              jsonOk(Json.obj("request_id" -> value.id))
            }
        )
    }
  }
}

