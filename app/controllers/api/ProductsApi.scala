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

package controllers.api

import models.service.Services
import models.{ Product, ProductView, ProductsCollection }
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc.Controller

/**
 * Products API.
 */
trait ProductsApi extends Controller with ApiAuthentication with Services {

  import controllers.api.BrandsApi.brandWrites

  implicit val productWithBrandsWrites = new Writes[ProductView] {
    def writes(obj: ProductView): JsValue = {
      Json.obj(
        "href" -> obj.product.id.map(id ⇒ routes.ProductsApi.product(id).url),
        "title" -> obj.product.title,
        "subtitle" -> obj.product.subtitle,
        "image" -> obj.product.picture.map(picture ⇒ controllers.routes.Products.picture(obj.product.id.get).url),
        "brands" -> obj.brands,
        "category" -> obj.product.category.map(name ⇒ Messages(s"models.ProductCategory.$name")).orNull)
    }
  }

  implicit val productWrites = new Writes[Product] {
    def writes(obj: Product): JsValue = {
      Json.obj(
        "title" -> obj.title,
        "subtitle" -> obj.subtitle,
        "image" -> obj.picture.map(picture ⇒ controllers.routes.Products.picture(obj.id.get).url),
        "category" -> obj.category.map(name ⇒ Messages(s"models.ProductCategory.$name")).orNull)
    }
  }

  import controllers.api.ContributionsApi.contributorWrites

  val productDetailsWrites = new Writes[ProductView] {
    def writes(obj: ProductView): JsValue = {
      Json.obj(
        "title" -> obj.product.title,
        "subtitle" -> obj.product.subtitle,
        "url" -> obj.product.url,
        "description" -> obj.product.description,
        "cta_url" -> obj.product.callToActionUrl,
        "cta_text" -> obj.product.callToActionText,
        "image" -> obj.product.picture.map(picture ⇒ controllers.routes.Products.picture(obj.product.id.get).url),
        "brands" -> obj.brands,
        "category" -> obj.product.category.map(name ⇒ Messages(s"models.ProductCategory.$name")).orNull,
        "parent" -> obj.product.parentId.map(parentId ⇒ routes.ProductsApi.product(parentId).url),
        "contributors" -> obj.product.contributors)
    }
  }

  /**
   * Product details API.
   */
  def product(id: Long) = TokenSecuredAction { implicit request ⇒
    productService.find(id) map { product ⇒
      val withBrands = ProductView(product, productService.brands(id))
      Ok(Json.prettyPrint(Json.toJson(withBrands)(productDetailsWrites)))
    } getOrElse NotFound("Unknown product")
  }

  /**
   * Product list API.
   */
  def products = TokenSecuredAction { implicit request ⇒
    val products = ProductsCollection.brands(productService.findActive)
    Ok(Json.prettyPrint(Json.toJson(products)))
  }
}

object ProductsApi extends ProductsApi with ApiAuthentication with Services
