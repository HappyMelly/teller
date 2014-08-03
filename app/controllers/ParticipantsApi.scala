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

import models._
import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.mvc._
import play.api.data.Form
import play.api.i18n.Messages
import play.api.libs.json._
import views.Countries

/**
 * Participants API
 */
object ParticipantsApi extends ApiAuthentication {

  def participantForm(account: UserAccount, userName: String) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "event_id" -> longNumber.verifying(
        "error.event.invalid",
        (eventId: Long) ⇒ Event.canManage(eventId, account)),
      "first_name" -> nonEmptyText,
      "last_name" -> nonEmptyText,
      "birth_date" -> optional(jodaLocalDate),
      "email" -> email,
      "city" -> nonEmptyText,
      "country" -> nonEmptyText.verifying(
        "error.unknown_country",
        (country: String) ⇒ Countries.all.exists(_._1 == country))) ({
        (id, event_id, first_name, last_name, birth_date, email, city, country) ⇒
          ParticipantData(id, event_id, first_name, last_name, birth_date, email, city, country,
            DateTime.now(), userName, DateTime.now(), userName)
      }) ({
        (p: ParticipantData) ⇒
          Some((p.id, p.eventId, p.firstName, p.lastName, p.birthDate, p.emailAddress,
            p.city, p.country))
      }))
  }

  /**
   * Create a participant through API call
   */
  def create = TokenSecuredActionWithIdentity { (request: Request[AnyContent], identity: LoginIdentity) ⇒
    val person = identity.person
    val form: Form[ParticipantData] = participantForm(identity.userAccount, person.fullName).bindFromRequest()(request)

    form.fold(
      formWithErrors ⇒ {
        val json = Json.toJson(APIError.formValidationError(formWithErrors.errors))
        BadRequest(Json.prettyPrint(json))
      },
      data ⇒ {
        val participant = Participant.create(data)
        val activityObject = Messages("activity.participant.create", data.firstName + " " + data.lastName, data.event.get.title)
        Activity.insert(person.fullName, Activity.Predicate.Created, activityObject)
        Ok(Json.prettyPrint(Json.obj("participant_id" -> participant.participantId)))
      })
  }
}
