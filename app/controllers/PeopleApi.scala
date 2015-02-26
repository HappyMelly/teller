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

import java.net.URLDecoder
import models._
import models.service.Services
import play.mvc.Controller
import play.api.libs.json._

trait PeopleApi extends Controller with ApiAuthentication with Services {

  implicit val personWrites = new Writes[Person] {
    def writes(person: Person): JsValue = {
      Json.obj(
        "id" -> person.id.get,
        "unique_name" -> person.uniqueName,
        "href" -> routes.PeopleApi.person(person.id.get.toString).url,
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
        "id" -> person.id.get,
        "unique_name" -> person.uniqueName,
        "first_name" -> person.firstName,
        "last_name" -> person.lastName,
        "email_address" -> person.socialProfile.email,
        "photo" -> person.photo.url,
        "address" -> person.address,
        "stakeholder" -> (person.role == PersonRole.Stakeholder),
        "board_member" -> (person.role == PersonRole.BoardMember),
        "bio" -> person.bio,
        "interests" -> person.interests,
        "twitter_handle" -> person.socialProfile.twitterHandle,
        "facebook_url" -> person.socialProfile.facebookUrl,
        "linkedin_url" -> person.socialProfile.linkedInUrl,
        "google_plus_url" -> person.socialProfile.googlePlusUrl,
        "website" -> person.webSite,
        "blog" -> person.blog,
        "active" -> person.active,
        "organizations" -> person.organisations,
        "licenses" -> person.licenses,
        "contributions" -> person.contributions)
    }
  }

  /**
   * Get a list of people
   * @param stakeholdersOnly If true only stakeholders are retrieved
   * @param boardmembersOnly If true only board members are retrieved
   * @param active If true only active members are retrieved
   * @param query Retrieve only people whose name meets the pattern
   * @return
   */
  def people(stakeholdersOnly: Option[Boolean],
    boardmembersOnly: Option[Boolean],
    active: Option[Boolean],
    query: Option[String]) = TokenSecuredAction { implicit request ⇒

    val people: List[Person] = Person.findByParameters(stakeholdersOnly.getOrElse(false),
      boardmembersOnly.getOrElse(false), active, query)
    PeopleCollection.addresses(people)
    Ok(Json.prettyPrint(Json.toJson(people)))
  }

  /**
   * Get a person
   * @param identifier Person identifier
   * @return
   */
  def person(identifier: String) = TokenSecuredAction { implicit request ⇒
    val person = try {
      val id = identifier.toLong
      personService.find(id)
    } catch {
      case e: NumberFormatException ⇒ personService.find(URLDecoder.decode(identifier, "ASCII"))
    }
    person.map(person ⇒ Ok(Json.prettyPrint(Json.toJson(person)(personDetailsWrites)))).getOrElse(NotFound)
  }
}

object PeopleApi extends PeopleApi with ApiAuthentication with Services
