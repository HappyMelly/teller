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

import models.service.Services
import play.api.mvc.Controller
import play.api.libs.json._
import models.{ Address, Organisation }

/**
 * Organisations API.
 */
object OrganisationsApi extends Controller with ApiAuthentication with Services {

  implicit val organisationWrites = new Writes[Organisation] {
    def writes(organisation: Organisation): JsValue = {
      Json.obj(
        "href" -> organisation.id.map(organisationId ⇒ routes.OrganisationsApi.organisation(organisationId).url),
        "name" -> organisation.name,
        "city" -> organisation.city,
        "country" -> organisation.countryCode,
        "website" -> organisation.webSite)
    }
  }

  import PeopleApi.personWrites
  import ContributionsApi.contributionWrites

  val organisationDetailsWrites = new Writes[Organisation] {
    def writes(organisation: Organisation): JsValue = {
      val address = Address(None, organisation.street1, organisation.street2, organisation.city, organisation.province,
        organisation.postCode, organisation.countryCode)
      import PeopleApi.addressWrites

      Json.obj(
        "name" -> organisation.name,
        "address" -> Json.toJson(address),
        "vat_number" -> organisation.vatNumber,
        "registration_number" -> organisation.registrationNumber,
        "website" -> organisation.webSite,
        "members" -> organisation.members,
        "contributions" -> organisation.contributions)
    }
  }

  /**
   * Organisation details API.
   */
  def organisation(id: Long) = TokenSecuredAction { implicit request ⇒
    orgService.find(id).map { organisation ⇒
      Ok(Json.toJson(organisation)(organisationDetailsWrites))
    }.getOrElse(NotFound("Unknown organization"))
  }

  /** Returns a list of all organisations in JSON format */
  def organisations = TokenSecuredAction { implicit request ⇒
    Ok(Json.toJson(Organisation.findAll))
  }
}
