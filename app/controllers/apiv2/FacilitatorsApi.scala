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
import javax.inject.Inject

import controllers.apiv2.json.PersonConverter
import controllers.brand.Badges
import models._
import models.brand.Badge
import models.repository.Repositories
import org.joda.time.LocalDate
import play.api.i18n.MessagesApi
import play.api.libs.json._
import views.{Countries, Languages}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Facilitators API
 */
class FacilitatorsApi @Inject() (val services: Repositories,
                                 override val messagesApi: MessagesApi)
  extends ApiAuthentication(services, messagesApi) {

  case class FacilitatorSummary(person: Person,
                                address: Address,
                                languages: List[FacilitatorLanguage],
                                countries: List[FacilitatorCountry],
                                rating: Float,
                                badges: Seq[BadgeUrl])

  implicit val badgeUrlWrites = new Writes[BadgeUrl] {
    def writes(badge: BadgeUrl) = {
      Json.obj(
        "name" -> badge.name,
        "url" -> badge.url)
    }
  }


  implicit val facilitatorWrites = new Writes[FacilitatorSummary] {
    def writes(value: FacilitatorSummary): JsValue = {
      Json.obj(
        "id" -> value.person.id.get,
        "first_name" -> value.person.firstName,
        "last_name" -> value.person.lastName,
        "unique_name" -> value.person.uniqueName,
        "photo" -> value.person.photo.url,
        "country" -> value.address.countryCode,
        "languages" -> value.languages.map(r ⇒ Languages.all.getOrElse(r.language, "")).toList,
        "countries" -> value.countries.map(_.country).distinct.toList,
        "rating" -> value.rating,
        "badges" -> value.badges)
    }
  }

  /**
   * Returns a list of facilitators for the given brand in JSON format
   *
   * @param code Brand code
   */
  def facilitators(code: String) = TokenSecuredAction(readWrite = false) { implicit request ⇒ implicit token ⇒
    services.brand.find(code) flatMap {
      case None => jsonNotFound("Brand not found")
      case Some(brand) =>
        (for {
          f <- services.license.licensees(brand.identifier, LocalDate.now())
          data <- services.facilitator.findByBrand(brand.identifier)
          c <- services.facilitator.countries(f.map(_.identifier))
          l <- services.facilitator.languages(f.map(_.identifier))
          a <- services.address.find(f.map(_.addressId))
          b <- services.brandBadge.findByBrand(brand.identifier)
        } yield facilitatorsSummary(f, c, l, a, data, b)) flatMap { summaries =>
          val collator = Collator.getInstance(Locale.ENGLISH)
          val ord = new Ordering[String] {
            def compare(x: String, y: String) = collator.compare(x, y)
          }
          val sorted = summaries.sortBy(_.person.fullName.toLowerCase)(ord)
          jsonOk(Json.toJson(sorted))
        }
    }
  }

  import OrganisationsApi.organisationWrites
  private val personConverter = new PersonConverter
  implicit val personWrites = personConverter.personWrites
  implicit val licenseWrites = personConverter.licenseSummaryWrites
  implicit val addressWrites = personConverter.addressWrites

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

  case class BrandStatistics(eventsNumber: Int, yearsOfExperience: Int, rating: Float)
  case class BadgeUrl(name: String, url: String)

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
        "twitter_handle" -> view.person.profile.twitterHandle,
        "facebook_url" -> view.person.profile.facebookUrl,
        "linkedin_url" -> view.person.profile.linkedInUrl,
        "google_plus_url" -> view.person.profile.googlePlusUrl,
        "website" -> view.person.webSite,
        "blog" -> view.person.blog,
        "active" -> view.person.active,
        "organizations" -> view.person.organisations(services),
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
        services.person.findComplete(id)
      } catch {
        case e: NumberFormatException ⇒ services.person.find(URLDecoder.decode(identifier, "ASCII"))
      }
      val view = (for {
        person <- mayBePerson
        brand <- code.map(value => services.brand.find(value)).getOrElse(Future.successful(None))
      } yield (person, brand)) flatMap {
        case (None, _) => Future.successful(None)
        case (Some(person), None) => withoutBrandData(person).map(value => Some(value))
        case (Some(person), Some(brand)) => withBrandData(person, brand).map(value => Some(value))
      }
      view flatMap {
        case None => jsonNotFound("Person not found")
        case Some((person, endorsements, materials, licenses, facilitator, urls)) =>
          jsonOk(Json.toJson(FacilitatorView(person, endorsements, materials, licenses, facilitator, urls)))
      }
  }

  protected def facilitatorsSummary(facilitators: List[Person],
                                    countries: List[FacilitatorCountry],
                                    languages: List[FacilitatorLanguage],
                                    addresses: List[Address],
                                    stats: List[Facilitator],
                                    badges: List[Badge]): List[FacilitatorSummary] = {
    val withAddresses = facilitators.sortBy(_.addressId).zip(addresses.sortBy(_.id))
    val withRating = withAddresses.sortBy(_._1.identifier).zip(stats.sortBy(_.personId))
    val groupedLanguages = languages.groupBy(_.personId)
    val groupedCountries = countries.groupBy(_.personId)
    withRating.map { case ((person, address), stat) =>
      facilitatorSummary(person, address, stat, groupedCountries, groupedLanguages, badges)
    }
  }

  protected def facilitatorSummary(person: Person, address: Address, data: Facilitator,
                                   countries: Map[Long, List[FacilitatorCountry]],
                                   languages: Map[Long, List[FacilitatorLanguage]],
                                   badges: List[Badge]): FacilitatorSummary = {
    val language = languages.getOrElse(person.identifier, List())
    val countryOfResidence = List(FacilitatorCountry(person.identifier, address.countryCode))
    val country = countries.get(person.identifier).map(_ ::: countryOfResidence).getOrElse(countryOfResidence).distinct
    FacilitatorSummary(person, address, language, country, data.publicRating, badgeUrls(badges, data.badges))
  }

  /**
    * Returns facilitator-related data when no brand is specified
    *
    * @param person Person object
    */
  protected def withoutBrandData(person: Person) = {
    (for {
      e <- services.person.endorsements(person.identifier)
      m <- services.person.materials(person.identifier)
      l <- services.license.activeLicenses(person.identifier)
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
      e <- services.person.endorsements(person.identifier)
      m <- services.person.materials(person.identifier)
      f <- retrieveFacilitatorStat(brand, person.identifier)
      b <- services.brandBadge.findByBrand(brand.identifier)
      l <- services.license.activeLicenses(person.identifier)
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
    services.facilitator.find(brand.identifier, personId) map {
      case None => Facilitator(None, personId, brand.identifier)
      case Some(facilitator) => facilitator
    }
  }
}
