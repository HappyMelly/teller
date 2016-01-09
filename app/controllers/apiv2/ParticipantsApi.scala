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

import models._
import models.event.Attendee
import models.service.Services
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import views.Countries

/**
 * Participants API
 */
trait ParticipantsApi extends ApiAuthentication with Services {

  def attendeeForm(appName: String) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "event_id" -> longNumber(min = 1),
    "first_name" -> nonEmptyText,
    "last_name" -> nonEmptyText,
    "birthday" -> optional(jodaLocalDate),
    "email" -> email,
    "city" -> nonEmptyText,
    "country" -> nonEmptyText.verifying(
      "error.unknown_country",
      (country: String) ⇒ Countries.all.exists(_._1 == country)),
    "street_1" -> optional(nonEmptyText),
    "street_2" -> optional(nonEmptyText),
    "postcode" -> optional(nonEmptyText),
    "province" -> optional(nonEmptyText),
    "organisation" -> optional(nonEmptyText),
    "comment" -> optional(nonEmptyText),
    "role" -> optional(nonEmptyText))({
      (id, event_id, first_name, last_name, birthday, email, city, country,
       street_1, street_2, postcode, province, organisation, comment, role) ⇒
        Attendee(id, event_id, None, first_name, last_name, email, birthday, Some(country), Some(city),
          street_1, street_2, province, postcode, None, None, None, organisation, comment, role,
          DateStamp(DateTime.now(), appName, DateTime.now(), appName))
    })({
      (a: Attendee) ⇒
        Some((a.id, a.eventId, a.firstName, a.lastName, a.dateOfBirth, a.email, a.countryCode.getOrElse(""),
          a.city.getOrElse(""), a.street_1, a.street_2, a.province, a.postcode, a.organisation, a.comment, a.role))
    }))

  /**
   * Create an attendee through API call
   */
  def create = TokenSecuredAction(readWrite = true) { implicit request ⇒ implicit token ⇒

    val form: Form[Attendee] = attendeeForm(token.appName).bindFromRequest()(request)
    form.fold(
      formWithErrors ⇒ {
        val json = Json.toJson(APIError.formValidationError(formWithErrors.errors))
        BadRequest(Json.prettyPrint(json))
      },
      data ⇒ {
        val attendee = attendeeService.insert(data)
        jsonOk(Json.obj("participant_id" -> attendee.identifier))
      })
  }

  implicit val attendeeWrites = new Writes[Attendee] {
    def writes(attendee: Attendee): JsValue = {
      Json.obj(
        "id" -> attendee.identifier,
        "first_name" -> attendee.firstName,
        "last_name" -> attendee.lastName,
        "photo" -> None.asInstanceOf[Option[String]],
        "country" -> attendee.countryCode)
    }
  }

  /**
   * Returns list of attendees for the given event
   *
   * @param eventId Event identifier
   */
  def attendees(eventId: Long) = TokenSecuredAction(readWrite = false) { implicit request ⇒ implicit token ⇒
    val attendees = attendeeService.findByEvents(List(eventId)).map(_._2)
    jsonOk(Json.toJson(attendees))
  }

}

object ParticipantsApi extends ParticipantsApi with Services
