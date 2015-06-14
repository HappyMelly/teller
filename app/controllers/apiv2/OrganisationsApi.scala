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

import models.service.Services
import models.{ Address, Organisation }
import play.api.libs.json._
import play.api.mvc.Controller

/**
 * Organisations API.
 */
trait OrganisationsApi extends Controller with ApiAuthentication with Services {

  implicit val organisationWrites = new Writes[Organisation] {
    def writes(organisation: Organisation): JsValue = {
      Json.obj(
        "name" -> organisation.name,
        "city" -> organisation.city,
        "country" -> organisation.countryCode,
        "website" -> organisation.webSite)
    }
  }

  import PeopleApi.{ personWrites, addressWrites }
  import ContributionsApi.contributionWrites

  val organisationDetailsWrites = new Writes[Organisation] {
    def writes(org: Organisation): JsValue = {
      val address = Address(None, org.street1, org.street2, org.city,
        org.province, org.postCode, org.countryCode)

      Json.obj(
        "name" -> org.name,
        "address" -> Json.toJson(address),
        "vat_number" -> org.vatNumber,
        "registration_number" -> org.registrationNumber,
        "website" -> org.webSite,
        "members" -> org.people,
        "contributions" -> org.contributions)
    }
  }

  /**
   * Returns organisation in JSON format if exists
   * @param id Organisation id
   */
  def organisation(id: Long) = TokenSecuredAction(readWrite = false) {
    implicit request ⇒
      implicit token ⇒
        orgService.find(id) map { organisation ⇒
          jsonOk(Json.toJson(organisation)(organisationDetailsWrites))
        } getOrElse jsonNotFound("Unknown organization")
  }

  /**
   * Returns list of organisations in JSON format
   */
  def organisations = TokenSecuredAction(readWrite = false) { implicit request ⇒
    implicit token ⇒
      jsonOk(Json.toJson(orgService.findAll))
  }
}

object OrganisationsApi extends OrganisationsApi