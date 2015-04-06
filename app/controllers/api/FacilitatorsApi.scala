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

import models.{ Brand, PeopleCollection, Person }
import play.api.libs.json._
import play.mvc.Controller
import views.Languages

/**
 * Facilitators API
 */
object FacilitatorsApi extends Controller with ApiAuthentication {

  implicit val facilitatorWrites = new Writes[Person] {
    def writes(person: Person): JsValue = {
      Json.obj(
        "id" -> person.id.get,
        "first_name" -> person.firstName,
        "last_name" -> person.lastName,
        "photo" -> person.photo.url,
        "country" -> person.address.countryCode,
        "languages" -> person.languages.map(r ⇒ Languages.all.getOrElse(r.language, "")).toList,
        "countries" -> person.countries.map(_.country).toList)
    }
  }

  /**
   * Facilitators list for a given brand
   *
   * @param code Brand code
   */
  def facilitators(code: String) = TokenSecuredAction { implicit request ⇒
    val facilitators = Brand.findFacilitators(code)
    if (facilitators.length > 0) {
      PeopleCollection.countries(facilitators)
      PeopleCollection.languages(facilitators)
      PeopleCollection.addresses(facilitators)
    }
    Ok(Json.prettyPrint(Json.toJson(facilitators)))
  }

}
