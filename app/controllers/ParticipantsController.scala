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
import play.api.data.Form
import play.api.data.Forms._
import play.mvc.Controller
import views.Countries

trait ParticipantsController extends Controller {

  def newPersonForm(account: UserAccount, userName: String) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "brandId" -> nonEmptyText,
      "eventId" -> longNumber.verifying(
        "error.event.invalid",
        (eventId: Long) ⇒ Event.canManage(eventId, account)),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "birthDate" -> optional(jodaLocalDate),
      "emailAddress" -> email,
      "city" -> nonEmptyText,
      "country" -> nonEmptyText.verifying(
        "Unknown country",
        (country: String) ⇒ Countries.all.exists(_._1 == country)))({
        (id, brandId, eventId, firstName, lastName, birthDate, emailAddress, city, country) ⇒
          ParticipantData(id, eventId, firstName, lastName, birthDate, emailAddress, city, country,
            DateTime.now, userName, DateTime.now, userName)
      })({
        (p: ParticipantData) ⇒
          Some(p.id, p.event.get.brandCode, p.eventId, p.firstName, p.lastName, p.birthDate, p.emailAddress,
            p.city, p.country)
      }))
  }

}
