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

import play.mvc.Controller
import play.api.libs.json._
import models.{ Brand, BrandView }

/**
 * Brands API
 */
object BrandsApi extends Controller with ApiAuthentication {

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
        "href" -> routes.BrandsApi.brand(brandView.brand.code).url,
        "unique_name" -> brandView.brand.uniqueName,
        "name" -> brandView.brand.name,
        "image" -> brandView.brand.picture.map(picture ⇒ routes.Brands.picture(brandView.brand.code).url),
        "description" -> brandView.brand.description)
    }
  }

  import PeopleApi.personWrites
  import ProductsApi.productWrites

  val brandViewDetailsWrites = new Writes[BrandView] {
    def writes(brandView: BrandView): JsValue = {
      Json.obj(
        "code" -> brandView.brand.code,
        "unique_name" -> brandView.brand.uniqueName,
        "name" -> brandView.brand.name,
        "tagline" -> brandView.brand.tagLine,
        "description" -> brandView.brand.description,
        "image" -> brandView.brand.picture.map(picture ⇒ routes.Brands.picture(brandView.brand.code).url),
        "coordinator" -> brandView.coordinator,
        "contact_info" -> Json.obj(
          "email" -> brandView.brand.socialProfile.email,
          "skype" -> brandView.brand.socialProfile.skype,
          "phone" -> brandView.brand.socialProfile.phone),
        "social_profile" -> Json.obj(
          "facebook" -> brandView.brand.socialProfile.facebookUrl,
          "twitter" -> brandView.brand.socialProfile.twitterHandle,
          "google_plus" -> brandView.brand.socialProfile.googlePlusUrl,
          "linkedin" -> brandView.brand.socialProfile.linkedInUrl),
        "website" -> brandView.brand.webSite,
        "blog" -> brandView.brand.blog,
        "products" -> brandView.brand.products)
    }
  }

  /**
   * Brand details API.
   */
  def brand(code: String) = TokenSecuredAction { implicit request ⇒
    Brand.find(code).map { brandView ⇒
      Ok(Json.prettyPrint(Json.toJson(brandView)(brandViewDetailsWrites)))
    }.getOrElse {
      Brand.findByName(code).map { brandView ⇒
        Ok(Json.prettyPrint(Json.toJson(brandView)(brandViewDetailsWrites)))
      }.getOrElse(NotFound("Unknown brand"))
    }
  }

  /**
   * Brand list API.
   */
  def brands = TokenSecuredAction { implicit request ⇒
    Ok(Json.prettyPrint(Json.toJson(Brand.findAll)))
  }

}
