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
import java.text.Collator
import java.util.Locale

import controllers.brand.Badges
import models._
import models.brand.Badge
import org.joda.time.LocalDate
import play.api.libs.json._
import views.Languages

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Facilitators API
 */
trait FacilitatorsApi extends ApiAuthentication {

  implicit val facilitatorWrites = new Writes[(Person, Float)] {
    def writes(value: (Person, Float)): JsValue = {
      Json.obj(
        "id" -> value._1.id.get,
        "first_name" -> value._1.firstName,
        "last_name" -> value._1.lastName,
        "unique_name" -> value._1.uniqueName,
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
  def facilitators(code: String) = TokenSecuredAction(readWrite = false) { implicit request ⇒ implicit token ⇒
    brandService.find(code) flatMap {
      case None => jsonNotFound("Brand not found")
      case Some(brand) =>
        (for {
          facilitators <- licenseService.licensees(brand.identifier, LocalDate.now())
          data <- facilitatorService.findByBrand(brand.identifier)
        } yield (facilitators, data)) flatMap { case (facilitators, facilitationData) =>
          val collator = Collator.getInstance(Locale.ENGLISH)
          val ord = new Ordering[String] {
            def compare(x: String, y: String) = collator.compare(x, y)
          }
          val sorted = facilitators.sortBy(_.fullName.toLowerCase)(ord)
          personService.collection.addresses(sorted)
          personService.collection.countries(sorted)
          personService.collection.languages(sorted)
          val data = sorted.map(x ⇒ (x, facilitationData.find(_.personId == x.id.get).get.publicRating))
          jsonOk(Json.toJson(data))
        }
    }
  }

  import OrganisationsApi.organisationWrites
  import PeopleApi.addressWrites
  import PeopleApi.licenseSummaryWrites

  implicit val endorsementWrites = new Writes[Endorsement] {
    def writes(endorsement: Endorsement) = {
      Json.obj(
        "content" -> endorsement.content,
        "name" -> endorsement.name,
        "company" -> endorsement.company,
        "rating" -> endorsement.rating)
    }
  }

  implicit val materialWrites = new Writes[Material] {
    def writes(material: Material) = {
      Json.obj(
        "type" -> material.linkType,
        "link" -> material.link)
    }
  }

  implicit val badgeUrlWrites = new Writes[BadgeUrl] {
    def writes(badge: BadgeUrl) = {
      Json.obj(
        "name" -> badge.name,
        "url" -> badge.url)
    }
  }

  case class BrandStatistics(eventsNumber: Int, yearsOfExperience: Int, rating: Float)
  case class BadgeUrl(name: String, url: Option[String])

  case class FacilitatorView(person: Person,
    endorsements: List[Endorsement],
    materials: List[Material],
    licenses: List[LicenseView],
    facilitator: Facilitator,
    badges: List[BadgeUrl])

  implicit val facilitatorDetailsWrites = new Writes[FacilitatorView] {
    def writes(view: FacilitatorView) = {
      Json.obj(
        "id" -> view.person.id.get,
        "unique_name" -> view.person.uniqueName,
        "first_name" -> view.person.firstName,
        "last_name" -> view.person.lastName,
        "email_address" -> view.person.email,
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
        "licenses" -> view.licenses,
        "endorsements" -> view.endorsements,
        "materials" -> view.materials,
        "years_of_experience" -> view.facilitator.yearsOfExperience,
        "number_of_events" -> view.facilitator.numberOfEvents,
        "rating" -> view.facilitator.publicRating,
        "statistics" -> Json.obj(
          "public_rating" -> view.facilitator.publicRating,
          "private_rating" -> view.facilitator.privateRating,
          "public_median" -> view.facilitator.publicMedian,
          "private_median" -> view.facilitator.privateMedian,
          "public_nps" -> view.facilitator.publicNps,
          "private_nps" -> view.facilitator.privateNps,
          "number_of_public_evaluations" -> view.facilitator.numberOfPublicEvaluations,
          "number_of_private_evaluations" -> view.facilitator.numberOfPrivateEvaluations
        ),
        "badges" -> view.badges)
    }
  }

  /**
   * Returns the facilitator's data
    *
    * @param identifier Person identifier
   */
  def facilitator(identifier: String, code: Option[String] = None) = TokenSecuredAction(readWrite = false) {
    implicit request ⇒ implicit token ⇒
      val mayBePerson = try {
        val id = identifier.toLong
        personService.find(id)
      } catch {
        case e: NumberFormatException ⇒ personService.find(URLDecoder.decode(identifier, "ASCII"))
      }
      val view = (for {
        person <- mayBePerson
        brand <- code.map(value => brandService.find(value)).getOrElse(Future.successful(None))
      } yield (person, brand)) flatMap {
        case (Some(person), None) => withoutBrandData(person)
        case (Some(person), Some(brand)) => withBrandData(person, brand)
      }
      view flatMap { case (person, endorsements, materials, licenses, facilitator, urls) =>
        jsonOk(Json.toJson(FacilitatorView(person, endorsements, materials, licenses, facilitator, urls)))
      }
  }

  /**
    * Returns facilitator-related data when no brand is specified
    *
    * @param person Person object
    */
  protected def withoutBrandData(person: Person) = {
    (for {
      e <- personService.endorsements(person.identifier)
      m <- personService.materials(person.identifier)
      l <- licenseService.activeLicenses(person.identifier)
    } yield (e, m, l)) map { case (endorsements, materials, licenses) =>
      val filteredEndorsements = endorsements.filter(_.brandId == 0)
      val filteredMaterials = materials.filter(_.brandId == 0)
      (person, filteredEndorsements, filteredMaterials, licenses, Facilitator(None, 0, 0), List())
    }
  }

  /**
    * Returns facilitator-related data when brand is specified
    *
    * @param person Person
    * @param brand Brand
    */
  protected def withBrandData(person: Person, brand: Brand) = {
    (for {
      e <- personService.endorsements(person.identifier)
      m <- personService.materials(person.identifier)
      f <- retrieveFacilitatorStat(brand, person.identifier)
      b <- brandBadgeService.findByBrand(brand.identifier)
      l <- licenseService.activeLicenses(person.identifier)
    } yield (e, m, f, b, l)) map { case (endorsements, materials, facilitator, badges, licenses) =>
      val urls = badgeUrls(badges, facilitator.badges)
      val filteredMaterials = materials.filter(_.brandId == 0) ::: materials.filter(_.brandId == brand.identifier)
      (person, withBrandEndorsements(endorsements, brand.identifier), filteredMaterials, licenses, facilitator, urls)
    }
  }

  /**
    * Returns name of badges and urls to their images
 *
    * @param badges All badges
    * @param facilitatorBadges Badges for the given facilitator
    */
  protected def badgeUrls(badges: List[Badge], facilitatorBadges: List[Long]) =
    badges.
      filter(badge => facilitatorBadges.contains(badge.id.get)).
      map(badge => BadgeUrl(badge.name, Badges.pictureUrl(badge)))

  protected def withBrandEndorsements(endorsements: List[Endorsement], brandId: Long) =
    endorsements.filter(_.brandId == 0) ::: endorsements.filter(_.brandId == brandId)

  /**
   * Returns brand statistics for the given person
    *
    * @param brand Brand of interest
   * @param personId Person identifier
   */
  protected def retrieveFacilitatorStat(brand: Brand, personId: Long): Future[Facilitator] = {
    facilitatorService.find(brand.identifier, personId) map {
      case None => Facilitator(None, personId, brand.identifier)
      case Some(facilitator) => facilitator
    }
  }
}

object FacilitatorsApi extends FacilitatorsApi with ApiAuthentication
