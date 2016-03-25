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

import models._
import models.cm.event.Attendee
import models.repository.Repositories
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.libs.json._
import views.Countries

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Participants API
 */
class ParticipantsApi @Inject() (val repos: Repositories,
                                 override val messagesApi: MessagesApi)
  extends ApiAuthentication(repos, messagesApi) {

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
    "role" -> optional(nonEmptyText),
    "opt_out" -> optional(boolean))({
      (id, event_id, first_name, last_name, birthday, email, city, country,
       street_1, street_2, postcode, province, organisation, comment, role, optOut) ⇒
        Attendee(id, event_id, None, first_name, last_name, email, birthday,
          Some(country), Some(city), street_1, street_2, province, postcode,
          None, None, None, organisation, comment, role, optOut.getOrElse(false),
          DateStamp(DateTime.now(), appName, DateTime.now(), appName))
    })({
      (a: Attendee) ⇒
        Some((a.id, a.eventId, a.firstName, a.lastName, a.dateOfBirth, a.email,
          a.countryCode.getOrElse(""), a.city.getOrElse(""), a.street_1, a.street_2, a.province, a.postcode,
          a.organisation, a.comment, a.role, Some(a.optOut)))
    }))

  /**
   * Create an attendee through API call
   */
  def create = TokenSecuredAction(readWrite = true) { implicit request ⇒ implicit token ⇒

    val form: Form[Attendee] = attendeeForm(token.humanIdentifier).bindFromRequest()(request)
    form.fold(
      formWithErrors ⇒ {
        val json = Json.toJson(APIError.formValidationError(formWithErrors.errors))
        badRequest(Json.prettyPrint(json))
      },
      data ⇒ {
        repos.cm.rep.event.attendee.insert(data) flatMap { attendee =>
          jsonOk(Json.obj("participant_id" -> attendee.identifier))
        }
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
    repos.cm.rep.event.attendee.findByEvents(List(eventId)) flatMap { attendees =>
      jsonOk(Json.toJson(attendees.map(_._2)))
    }
  }

}
