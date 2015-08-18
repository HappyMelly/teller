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

import java.net.URLDecoder
import models.{ Brand, PeopleCollection, Person, Endorsement, Material }
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
        "countries" -> value._1.countries.map(_.country).distinct.toList,
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

  import PeopleApi.addressWrites
  import OrganisationsApi.organisationWrites
  import PeopleApi.licenseSummaryWrites

  implicit val endorsementWrites = new Writes[Endorsement] {
    def writes(endorsement: Endorsement) = {
      Json.obj(
        "content" -> endorsement.content,
        "name" -> endorsement.name,
        "company" -> endorsement.company)
    }
  }

  implicit val materialWrites = new Writes[Material] {
    def writes(material: Material) = {
      Json.obj(
        "type" -> material.linkType,
        "link" -> material.link)
    }
  }

  case class FacilitatorView(person: Person,
    endorsements: List[Endorsement],
    materials: List[Material])

  implicit val facilitatorDetailsWrites = new Writes[FacilitatorView] {
    def writes(view: FacilitatorView) = {
      Json.obj(
        "id" -> view.person.id.get,
        "unique_name" -> view.person.uniqueName,
        "first_name" -> view.person.firstName,
        "last_name" -> view.person.lastName,
        "email_address" -> view.person.socialProfile.email,
        "image" -> view.person.photo.url,
        "address" -> view.person.address,
        "bio" -> view.person.bio,
        "interests" -> view.person.interests,
        "twitter_handle" -> view.person.socialProfile.twitterHandle,
        "facebook_url" -> view.person.socialProfile.facebookUrl,
        "linkedin_url" -> view.person.socialProfile.linkedInUrl,
        "google_plus_url" -> view.person.socialProfile.googlePlusUrl,
        "website" -> view.person.webSite,
        "blog" -> view.person.blog,
        "active" -> view.person.active,
        "organizations" -> view.person.organisations,
        "licenses" -> view.person.licenses,
        "endorsements" -> view.endorsements,
        "materials" -> view.materials)
    }
  }

  /**
   * Returns the facilitator's data
   * @param identifier Person identifier
   */
  def facilitator(identifier: String) = TokenSecuredAction(readWrite = false) {
    implicit request ⇒
      implicit token ⇒
        val person = try {
          val id = identifier.toLong
          personService.find(id)
        } catch {
          case e: NumberFormatException ⇒ personService.find(URLDecoder.decode(identifier, "ASCII"))
        }
        person map { person =>
          val endorsements = personService.endorsements(person.id.get)
          val materials = personService.materials(person.id.get)
          jsonOk(Json.toJson(FacilitatorView(person, endorsements, materials)))
        } getOrElse NotFound
  }
}

object FacilitatorsApi extends FacilitatorsApi with ApiAuthentication
