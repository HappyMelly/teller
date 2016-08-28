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
package controllers.api

import javax.inject.Inject

import controllers.Brands
import controllers.api.json.{PersonConverter, ProductConverter}
import models._
import models.cm.brand.{BrandLink, BrandTestimonial}
import models.repository.Repositories
import play.api.i18n.MessagesApi
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Brands API
 */
class BrandsApi @Inject() (val repos: Repositories,
                           override val messagesApi: MessagesApi) extends ApiAuthentication(repos, messagesApi) {

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

  case class BrandFullView(brand: Brand,
                           profile: SocialProfile,
                          coordinator: Person,
                          links: List[BrandLink],
                          testimonials: List[BrandTestimonial],
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
        "testimonials" -> view.testimonials)
    }
  }

  /**
   * Returns brand in JSON format if the brand exists, otherwise - Not Found
    *
    * @param code Brand code
   */
  def brand(code: String) = TokenSecuredAction(readWrite = false) { implicit request ⇒
    val view = repos.cm.brand.find(code) flatMap {
      case Some(brand) => fullView(brand)
      case None =>
        repos.cm.brand.findByName(code) flatMap {
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
  def brands = TokenSecuredAction(readWrite = false) { implicit request ⇒
    (for {
      brands <- repos.cm.brand.findAll
      products <- repos.product.findNumberPerBrand
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
      owner <- repos.person.findComplete(brand.ownerId)
      member <- repos.member.findByObject(brand.ownerId, person = true)
      links <- repos.cm.rep.brand.link.find(id)
      testimonials <- repos.cm.rep.brand.testimonial.findByBrand(id)
      products <- repos.product.findByBrand(id)
      profile <- repos.socialProfile.find(id, ProfileType.Brand)
    } yield (owner, member, links, testimonials, products, profile)) map {
      case (None, _, _, _, _, _) => None
      case (Some(owner), None, links, testimonials, products, profile) =>
        Some(BrandFullView(brand, profile, owner.copy(id = None), links, testimonials, products))
      case (Some(owner), Some(member), links, testimonials, products, profile) =>
        Some(BrandFullView(brand, profile, owner.copy(id = member.id), links, testimonials, products))
    }
  }
}