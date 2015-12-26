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

import models.service.Services
import models.{ Product, ProductView }
import models.service.ProductsCollection
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc.Controller

/**
 * Products API.
 */
trait ProductsApi extends Controller with ApiAuthentication with Services {

  import BrandsApi.brandWrites

  implicit val productWithBrandsWrites = new Writes[ProductView] {
    def writes(obj: ProductView): JsValue = {
      Json.obj(
        "id" -> obj.product.id,
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
        "id" -> obj.id,
        "title" -> obj.title,
        "subtitle" -> obj.subtitle,
        "image" -> obj.picture.map(picture ⇒ controllers.routes.Products.picture(obj.id.get).url),
        "category" -> obj.category.map(name ⇒ Messages(s"models.ProductCategory.$name")).orNull)
    }
  }

  import ContributionsApi.contributorWrites

  val productDetailsWrites = new Writes[ProductView] {
    def writes(obj: ProductView): JsValue = {
      Json.obj(
        "id" -> obj.product.id,
        "title" -> obj.product.title,
        "subtitle" -> obj.product.subtitle,
        "url" -> obj.product.url,
        "description" -> obj.product.description,
        "cta_url" -> obj.product.callToActionUrl,
        "cta_text" -> obj.product.callToActionText,
        "image" -> obj.product.picture.map(picture ⇒ controllers.routes.Products.picture(obj.product.id.get).url),
        "brands" -> obj.brands,
        "category" -> obj.product.category.map(name ⇒ Messages(s"models.ProductCategory.$name")).orNull,
        "parent" -> obj.product.parentId,
        "contributors" -> obj.product.contributors)
    }
  }

  /**
   * Returns product in JSON format if exists
   * @param id Product id
   */
  def product(id: Long) = TokenSecuredAction(readWrite = false) {
    implicit request ⇒
      implicit token ⇒
        productService.find(id) map { product ⇒
          val withBrands = ProductView(product, productService.brands(id))
          jsonOk(Json.toJson(withBrands)(productDetailsWrites))
        } getOrElse jsonNotFound("Unknown product")
  }

  /**
   * Returns list of products in JSON format
   */
  def products = TokenSecuredAction(readWrite = false) { implicit request ⇒
    implicit token ⇒
      val products = ProductsCollection.brands(productService.findActive)
      jsonOk(Json.toJson(products))
  }
}

object ProductsApi extends ProductsApi with ApiAuthentication with Services
