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

import models.{ Brand, BrandView, Person, Event }
import models.brand.{ BrandLink, BrandTestimonial }
import play.api.libs.json._
import play.mvc.Controller

/**
 * Brands API
 */
trait BrandsApi extends Controller with ApiAuthentication {

  implicit val brandWrites = new Writes[Brand] {
    def writes(brand: Brand): JsValue = {
      Json.obj(
        "code" -> brand.code,
        "unique_name" -> brand.uniqueName,
        "name" -> brand.name)
    }
  }

  implicit val brandViewWrites = new Writes[BrandView] {
    def writes(brandView: BrandView): JsValue = {
      Json.obj(
        "unique_name" -> brandView.brand.uniqueName,
        "name" -> brandView.brand.name,
        "image" -> brandView.brand.picture.map(picture ⇒
          controllers.routes.Brands.picture(brandView.brand.code).url),
        "tagline" -> brandView.brand.tagLine,
        "products" -> brandView.brand.products.length)
    }
  }
  import PeopleApi.personWrites
  import ProductsApi.productWrites

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
    coordinator: Person,
    links: List[BrandLink],
    testimonials: List[BrandTestimonial],
    events: List[Event])

  val detailsWrites = new Writes[BrandFullView] {
    def writes(view: BrandFullView): JsValue = {
      Json.obj(
        "code" -> view.brand.code,
        "unique_name" -> view.brand.uniqueName,
        "name" -> view.brand.name,
        "tagline" -> view.brand.tagLine,
        "description" -> view.brand.description,
        "image" -> view.brand.picture.map(picture ⇒
          controllers.routes.Brands.picture(view.brand.code).url),
        "coordinator" -> view.coordinator,
        "contact_info" -> Json.obj(
          "email" -> view.brand.contactEmail,
          "skype" -> view.brand.socialProfile.skype,
          "phone" -> view.brand.socialProfile.phone,
          "form" -> view.brand.socialProfile.contactForm),
        "social_profile" -> Json.obj(
          "facebook" -> view.brand.socialProfile.facebookUrl,
          "twitter" -> view.brand.socialProfile.twitterHandle,
          "google_plus" -> view.brand.socialProfile.googlePlusUrl,
          "linkedin" -> view.brand.socialProfile.linkedInUrl),
        "products" -> view.brand.products,
        "links" -> view.links,
        "testimonials" -> view.testimonials,
        "events" -> view.events)
    }
  }

  /**
   * Returns brand in JSON format if the brand exists, otherwise - Not Found
   * @param code Brand code
   */
  def brand(code: String) = TokenSecuredAction(readWrite = false) {
    implicit request ⇒
      implicit token ⇒
        Brand.find(code) map { view ⇒
          jsonOk(Json.toJson(fullView(view))(detailsWrites))
        } getOrElse {
          Brand.findByName(code) map { view ⇒
            jsonOk(Json.toJson(fullView(view))(detailsWrites))
          } getOrElse jsonNotFound("Unknown brand")
        }
  }

  /**
   * Returns a list of brands in JSON format
   */
  def brands = TokenSecuredAction(readWrite = false) { implicit request ⇒
    implicit token ⇒
      val views = Brand.findAllWithCoordinator.filter(_.brand.active)
      jsonOk(Json.toJson(views))
  }

  /**
   * Returns brand data with links, testimonials and related events
   *
   * @param view Brand of interest
   */
  protected def fullView(view: BrandView): BrandFullView = {
    val id = view.brand.id.get
    val events = eventService.findByParameters(Some(id),
      future = Some(true), public = Some(true), archived = Some(false)).take(3)
    val person = personService.member(view.coordinator.id.get) map { x ⇒
      view.coordinator.copy(id = x.id)
    } getOrElse view.coordinator.copy(id = None)
    BrandFullView(view.brand, person,
      brandService.links(id), brandService.testimonials(id), events)
  }
}

object BrandsApi extends BrandsApi