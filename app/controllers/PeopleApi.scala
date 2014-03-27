/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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

import play.mvc.Controller
import models._
import play.api.libs.json._
import models.LicenseView

object PeopleApi extends Controller with ApiAuthentication {

  implicit val personWrites = new Writes[Person] {
    def writes(person: Person): JsValue = {
      Json.obj(
        "href" -> routes.PeopleApi.person(person.id.get).url,
        "first_name" -> person.firstName,
        "last_name" -> person.lastName,
        "photo" -> person.photo.url,
        "country" -> person.address.countryCode)
    }
  }

  import OrganisationsApi.organisationWrites

  implicit val licenseSummaryWrites = new Writes[LicenseView] {
    def writes(license: LicenseView) = {
      Json.obj(
        "brand" -> license.brand.code,
        "start" -> license.license.start,
        "end" -> license.license.end)
    }
  }

  implicit val addressWrites = new Writes[Address] {
    def writes(address: Address) = Json.obj(
      "street1" -> address.street1,
      "street2" -> address.street2,
      "city" -> address.city,
      "post_code" -> address.postCode,
      "province" -> address.province,
      "country" -> address.countryCode)
  }

  import ContributionsApi.contributionWrites

  val personDetailsWrites = new Writes[Person] {
    def writes(person: Person) = {
      Json.obj(
        "first_name" -> person.firstName,
        "last_name" -> person.lastName,
        "email_address" -> person.emailAddress,
        "photo" -> person.photo.url,
        "address" -> person.address,
        "stakeholder" -> person.stakeholder,
        "board_member" -> person.boardMember,
        "bio" -> person.bio,
        "interests" -> person.interests,
        "twitter_handle" -> person.twitterHandle,
        "facebook_url" -> person.facebookUrl,
        "linkedin_url" -> person.linkedInUrl,
        "google_plus_url" -> person.googlePlusUrl,
        "active" -> person.active,
        "created" -> person.created.toString(),
        "createdBy" -> person.createdBy,
        "updated" -> person.updated.toString(),
        "updatedBy" -> person.updatedBy,
        "organizations" -> person.memberships,
        "licenses" -> person.licenses,
        "contributions" -> person.contributions)
    }
  }

  def people(stakeholdersOnly: Option[Boolean], boardmembersOnly: Option[Boolean]) = TokenSecuredAction { implicit request ⇒
    val people: List[Person] = Person.findActive(stakeholdersOnly.getOrElse(false), boardmembersOnly.getOrElse(false))
    Ok(Json.toJson(people))
  }

  def person(id: Long) = TokenSecuredAction { implicit request ⇒
    val person = Person.find(id)
    person.map(person ⇒ Ok(Json.toJson(person)(personDetailsWrites))).getOrElse(NotFound)
  }

}
