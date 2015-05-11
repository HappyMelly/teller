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

import models.{ Brand, PeopleCollection, Person }
import play.api.libs.json._
import play.mvc.Controller
import views.Languages

/**
 * Facilitators API
 */
trait FacilitatorsApi extends Controller with ApiAuthentication {

  implicit val facilitatorWrites = new Writes[(Person, Float)] {
    def writes(value: (Person, Float)): JsValue = {
      Json.obj(
        "id" -> value._1.id.get,
        "first_name" -> value._1.firstName,
        "last_name" -> value._1.lastName,
        "photo" -> value._1.photo.url,
        "country" -> value._1.address.countryCode,
        "languages" -> value._1.languages.map(r ⇒ Languages.all.getOrElse(r.language, "")).toList,
        "countries" -> value._1.countries.map(_.country).toList,
        "rating" -> value._2)
    }
  }

  /**
   * Returns a list of facilitators for the given brand in JSON format
   *
   * @param code Brand code
   */
  def facilitators(code: String) = TokenSecuredAction(readWrite = false) {
    implicit request ⇒
      implicit token ⇒
        brandService.find(code) map { brand ⇒
          val facilitators = Brand.findFacilitators(brand.id.get)
          PeopleCollection.addresses(facilitators)
          PeopleCollection.countries(facilitators)
          PeopleCollection.languages(facilitators)
          val facilitationData = facilitatorService.findByBrand(brand.id.get)
          val data = facilitators.
            map(x ⇒ (x, facilitationData.find(_.personId == x.id.get).get.rating))
          jsonOk(Json.toJson(data))
        } getOrElse jsonNotFound("Unknown brand")
  }
}

object FacilitatorsApi extends FacilitatorsApi with ApiAuthentication
