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

import models._
import models.service.Services
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import play.api.mvc._
import views.Countries

/**
 * Participants API
 */
trait ParticipantsApi extends ApiAuthentication with Services {

  def participantForm(account: UserAccount, userName: String) = {

    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "event_id" -> longNumber.verifying(
        "error.event.invalid",
        (eventId: Long) ⇒ Event.canManage(eventId, account)),
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
         street_1, street_2, postcode, province, organisation, comment,
          role) ⇒
          ParticipantData(id, event_id, first_name, last_name, birthday, email,
            Address(None, street_1, street_2, Some(city), province, postcode, country),
            organisation, comment, role,
            DateTime.now(), userName, DateTime.now(), userName)
      })({
        (p: ParticipantData) ⇒
          Some((p.id, p.eventId, p.firstName, p.lastName, p.birthday, p.emailAddress,
            p.address.city.getOrElse(""), p.address.countryCode,
            p.address.street1, p.address.street2, p.address.postCode,
            p.address.province, p.organisation, p.comment, p.role))
      }))
  }

  def existingPersonForm(account: UserAccount) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "event_id" -> longNumber.verifying(
        "error.event.invalid",
        (eventId: Long) ⇒ Event.canManage(eventId, account)),
      "person_id" -> longNumber.verifying(
        "error.person.notExist",
        (personId: Long) ⇒ personService.find(personId).nonEmpty),
      "role" -> optional(nonEmptyText))({
        (id, event_id, person_id, role) ⇒
          Participant(id = None, event_id, person_id, certificate = None,
            issued = None, evaluationId = None, organisation = None,
            comment = None, role = role)
      })({
        (p: Participant) ⇒ Some(p.id, p.eventId, p.personId, p.role)
      }))
  }

  /**
   * Create a participant through API call
   */
  def create = TokenSecuredActionWithIdentity { (request: Request[AnyContent], identity: UserIdentity) ⇒
    //@TODO this should be changed as soon as API is refactored
    val name = "Teller API"
    val account = userAccountService.findByIdentity(identity)
    val testFrom = existingPersonForm(account).bindFromRequest()(request)
    if (testFrom.data.contains("person_id")) {
      val form: Form[Participant] = existingPersonForm(account).bindFromRequest()(request)
      form.fold(
        formWithErrors ⇒ {
          val json = Json.toJson(APIError.formValidationError(formWithErrors.errors))
          BadRequest(Json.prettyPrint(json))
        },
        participant ⇒ {
          val createdParticipant = participantService.
            find(participant.personId, participant.eventId).
            map { p ⇒ p } getOrElse { Participant.insert(participant) }
          Ok(Json.prettyPrint(Json.obj("participant_id" -> createdParticipant.personId)))
        })
    } else {
      val form: Form[ParticipantData] = participantForm(account, name).bindFromRequest()(request)
      form.fold(
        formWithErrors ⇒ {
          val json = Json.toJson(APIError.formValidationError(formWithErrors.errors))
          BadRequest(Json.prettyPrint(json))
        },
        data ⇒ {
          val participant = Participant.create(data)
          Ok(Json.prettyPrint(Json.obj("participant_id" -> participant.personId)))
        })
    }
  }

  import PeopleApi.personWrites

  /**
   * Participants list for a given event
   *
   * @param eventId Event identifier
   */
  def participants(eventId: Long) = TokenSecuredAction { implicit request ⇒
    Ok(Json.prettyPrint(Json.toJson(Participant.findByEvent(eventId).map(_.person))))
  }

}

object ParticipantsApi extends ParticipantsApi with Services
