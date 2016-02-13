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

import javax.inject.Inject

import controllers.apiv2.json.{PersonConverter, ProductConverter}
import controllers.{Products, Brands}
import models.brand.{BrandLink, BrandTestimonial}
import models._
import models.repository.Repositories
import play.api.i18n.{MessagesApi, Messages}
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Brands API
 */
class BrandsApi @Inject() (val services: Repositories,
                           override val messagesApi: MessagesApi) extends ApiAuthentication(services, messagesApi) {

  implicit val brandWrites = new Writes[(Brand, Int)] {
    def writes(view: (Brand, Int)): JsValue = {
      Json.obj(
        "code" -> view._1.code,
        "unique_name" -> view._1.uniqueName,
        "name" -> view._1.name,
        "image" -> Brands.pictureUrl(view._1),
        "tagline" -> view._1.tagLine,
        "products" -> view._2)
    }
  }

  implicit val personWrites = (new PersonConverter).personWrites
  implicit val productWrites = (new ProductConverter).productWrites

  implicit val brandLinkWrites = new Writes[BrandLink] {
    def writes(link: BrandLink): JsValue = {
      Json.obj(
        "type" -> link.linkType,
        "url" -> link.link)
    }
  }

  implicit val brandTestimonialWrites = new Writes[BrandTestimonial] {
    def writes(testimonial: BrandTestimonial): JsValue = {
      Json.obj(
        "content" -> testimonial.content,
        "name" -> testimonial.name,
        "company" -> testimonial.company)
    }
  }

  implicit val eventWrites = new Writes[Event] {
    def writes(event: Event): JsValue = {
      Json.obj(
        "title" -> event.title,
        "description" -> event.details.description,
        "spokenLanguages" -> event.spokenLanguages,
        "start" -> event.schedule.start,
        "end" -> event.schedule.end,
        "hoursPerDay" -> event.schedule.hoursPerDay,
        "totalHours" -> event.schedule.totalHours,
        "city" -> event.location.city,
        "country" -> event.location.countryCode,
        "website" -> event.organizer.webSite,
        "registrationPage" -> event.organizer.registrationPage,
        "free" -> event.free)
    }
  }

  case class BrandFullView(brand: Brand,
                           profile: SocialProfile,
                          coordinator: Person,
                          links: List[BrandLink],
                          testimonials: List[BrandTestimonial],
                          events: List[Event],
                           products: List[Product])

  val detailsWrites = new Writes[BrandFullView] {
    def writes(view: BrandFullView): JsValue = {
      Json.obj(
        "code" -> view.brand.code,
        "unique_name" -> view.brand.uniqueName,
        "name" -> view.brand.name,
        "tagline" -> view.brand.tagLine,
        "description" -> view.brand.description,
        "image" -> Brands.pictureUrl(view.brand),
        "coordinator" -> view.coordinator,
        "contact_info" -> Json.obj(
          "email" -> view.brand.contactEmail,
          "skype" -> view.profile.skype,
          "phone" -> view.profile.phone,
          "form" -> view.profile.contactForm),
        "social_profile" -> Json.obj(
          "facebook" -> view.profile.facebookUrl,
          "twitter" -> view.profile.twitterHandle,
          "google_plus" -> view.profile.googlePlusUrl,
          "linkedin" -> view.profile.linkedInUrl),
        "products" -> view.products,
        "links" -> view.links,
        "testimonials" -> view.testimonials,
        "events" -> view.events)
    }
  }

  /**
   * Returns brand in JSON format if the brand exists, otherwise - Not Found
    *
    * @param code Brand code
   */
  def brand(code: String) = TokenSecuredAction(readWrite = false) { implicit request ⇒ implicit token ⇒
    val view = services.brand.find(code) flatMap {
      case Some(brand) => fullView(brand)
      case None =>
        services.brand.findByName(code) flatMap {
          case None => Future.successful(None)
          case Some(brand) => fullView(brand)
        }
    }
    view flatMap {
      case None => jsonNotFound("Brand not found")
      case Some(brandView) => jsonOk(Json.toJson(brandView)(detailsWrites))
    }
  }

  /**
   * Returns a list of brands in JSON format
   */
  def brands = TokenSecuredAction(readWrite = false) { implicit request ⇒ implicit token ⇒
    (for {
      brands <- services.brand.findAll
      products <- services.product.findNumberPerBrand
    } yield (brands, products)) flatMap { case (brands, products) =>
      val brandsWithProducts = brands.filter(_.active).map(brand => (brand, products.getOrElse(brand.identifier, 0)))
      jsonOk(Json.toJson(brandsWithProducts))
    }
  }

  /**
   * Returns brand data with links, testimonials, products and related events
   *
   * @param brand Brand of interest
   */
  protected def fullView(brand: Brand): Future[Option[BrandFullView]] = {
    val id = brand.identifier
    (for {
      owner <- services.person.findComplete(brand.ownerId)
      member <- services.member.findByObject(brand.ownerId, person = true)
      events <- services.event.findByParameters(Some(id), future = Some(true), public = Some(true), archived = Some(false))
      links <- services.brand.links(id)
      testimonials <- services.brand.testimonials(id)
      products <- services.product.findByBrand(id)
      profile <- services.socialProfile.find(id, ProfileType.Brand)
    } yield (owner, member, events, links, testimonials, products, profile)) map {
      case (None, _, _, _, _, _, _) => None
      case (Some(owner), None, events, links, testimonials, products, profile) =>
        Some(BrandFullView(brand, profile, owner.copy(id = None), links, testimonials, events.take(3), products))
      case (Some(owner), Some(member), events, links, testimonials, products, profile) =>
        Some(BrandFullView(brand, profile, owner.copy(id = member.id), links, testimonials, events.take(3), products))
    }
  }
}