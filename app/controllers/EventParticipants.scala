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

import models.{ EventParticipant, Person, Event, LoginIdentity }
import org.joda.time.LocalDate
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.UserRole.Role._
import securesocial.core.SecuredRequest
import play.api.i18n.Messages

case class Participant(id: Option[Long], eventId: Long, firstName: String, lastName: String,
  birthDate: Option[LocalDate], emailAddress: String, city: String, country: String)

object EventParticipants extends Controller with Security {

  def newPersonForm(request: SecuredRequest[_]) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "eventId" -> longNumber.verifying(
        "error.event.invalid", (eventId: Long) ⇒ Event.canManage(eventId, request.user.asInstanceOf[LoginIdentity].userAccount)),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "birthDate" -> optional(jodaLocalDate),
      "emailAddress" -> email,
      "city" -> nonEmptyText,
      "country" -> nonEmptyText)(Participant.apply)(Participant.unapply))
  }

  def existingPersonForm(request: SecuredRequest[_]) = {
    Form(tuple(
      "id" -> ignored(Option.empty[Long]),
      "eventId" -> longNumber.verifying(
        "error.event.invalid", (eventId: Long) ⇒ Event.canManage(eventId, request.user.asInstanceOf[LoginIdentity].userAccount)),
      "personId" -> longNumber.verifying(
        "error.person.invalid", (personId: Long) ⇒ Person.find(personId).isEmpty)))
  }

  /**
   * Returns a list of event types for the given brand
   */
  // def index(brandCode: String) = SecuredRestrictedAction(Viewer) { implicit request ⇒
  //   implicit handler ⇒
  //     Brand.find(brandCode).map { brand ⇒
  //       Ok(Json.toJson(EventType.findByBrand(brand.brand.id.get)))
  //     }.getOrElse(NotFound("Unknown brand"))
  // }

  /**
   * Create page.
   */
  def add = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒

      val account = request.user.asInstanceOf[LoginIdentity].userAccount
      val events = Event.findByUser(account)
      var people = Person.findActive
      Ok(views.html.participant.form(request.user, None, events, people, newPersonForm(request), existingPersonForm(request)))
  }

  def create = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒
      Redirect(routes.EventParticipants.add).flashing("success" -> "Yay!")
  }

  def createParticipantAndPerson = SecuredDynamicAction("event", "add") { implicit request ⇒
    implicit handler ⇒
      Redirect(routes.EventParticipants.add).flashing("success" -> "Yay!")
  }

  // /** Creates an event type **/
  // def create = SecuredDynamicAction("event", "add") { implicit request ⇒
  //   implicit handler ⇒

  //     val boundForm: Form[EventType] = eventTypeForm.bindFromRequest
  //     val brand = Brand.find(boundForm.data("brandId").toLong).get
  //     boundForm.bindFromRequest.fold(
  //       formWithErrors ⇒ Redirect(routes.Brands.details(brand.code)).flashing("error" -> "A name of an event type cannot be empty"),
  //       eventType ⇒ {
  //         eventType.insert
  //         val activityObject = Messages("activity.eventType.create", brand.name, eventType.name)
  //         val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, activityObject)
  //         Redirect(routes.Brands.details(brand.code)).flashing("success" -> activity.toString)
  //       })
  // }

  // /** Deletes an event type **/
  // def delete(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
  //   implicit handler ⇒

  //     EventType.find(id).map { eventType ⇒
  //       val brand = eventType.brand
  //       if (Event.getNumberByEventType(eventType.id.get) > 0) {
  //         Redirect(routes.Brands.details(brand.code)).flashing("error" -> Messages.apply("error.eventType.tooManyEvents"))
  //       } else {
  //         EventType.delete(id)
  //         val activityObject = Messages("activity.eventType.delete", brand.name, eventType.name)
  //         val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, activityObject)
  //         Redirect(routes.Brands.details(brand.code)).flashing("success" -> activity.toString)
  //       }
  //     }.getOrElse(NotFound)
  // }

}
