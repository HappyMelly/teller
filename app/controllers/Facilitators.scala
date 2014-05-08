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
import org.joda.time.LocalDate
import play.api.libs.json._
import play.api.mvc.Controller
import models.UserRole.Role._

/**
 * Facilitators pages
 */
object Facilitators extends Controller with Security {

  implicit val organizationWrites = new Writes[Organisation] {
    def writes(data: Organisation): JsValue = {
      Json.obj(
        "id" -> data.id.get,
        "name" -> data.name)
    }
  }

  implicit val personWrites = new Writes[(Person, Boolean)] {
    def writes(data: (Person, Boolean)): JsValue = {
      Json.obj(
        "first_name" -> data._1.firstName,
        "last_name" -> data._1.lastName,
        "coordinator" -> data._2,
        "id" -> data._1.id.get,
        "memberships" -> data._1.memberships)
    }
  }

  /**
   * Returns a list of facilitators for the given brand on today, including the coordinator of the brand
   */
  def index(brandCode: String) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒
      Brand.find(brandCode).map { brand ⇒
        val facilitators = Brand.findFacilitators(brandCode, brand.coordinator)
        Ok(Json.toJson(facilitators.map(person ⇒ (person, person.id == brand.coordinator.id))))
      }.getOrElse(NotFound("Unknown brand"))
  }

}
