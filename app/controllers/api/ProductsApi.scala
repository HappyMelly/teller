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
import models.{ Product, ProductsCollection }
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc.Controller

/**
 * Products API.
 */
trait ProductsApi extends Controller with ApiAuthentication with Services {

  import controllers.api.BrandsApi.brandWrites

  implicit val productWrites = new Writes[Product] {
    def writes(product: Product): JsValue = {
      Json.obj(
        "href" -> product.id.map(productId ⇒ routes.ProductsApi.product(productId).url),
        "title" -> product.title,
        "subtitle" -> product.subtitle,
        "image" -> product.picture.map(picture ⇒ controllers.routes.Products.picture(product.id.get).url),
        "brands" -> product.brands,
        "category" -> product.category.map(name ⇒ Messages(s"models.ProductCategory.$name")).orNull)
    }
  }

  import controllers.api.ContributionsApi.contributorWrites

  val productDetailsWrites = new Writes[Product] {
    def writes(product: Product): JsValue = {
      Json.obj(
        "title" -> product.title,
        "subtitle" -> product.subtitle,
        "url" -> product.url,
        "description" -> product.description,
        "cta_url" -> product.callToActionUrl,
        "cta_text" -> product.callToActionText,
        "image" -> product.picture.map(picture ⇒ controllers.routes.Products.picture(product.id.get).url),
        "brands" -> product.brands,
        "category" -> product.category.map(name ⇒ Messages(s"models.ProductCategory.$name")).orNull,
        "parent" -> product.parentId.map(parentId ⇒ routes.ProductsApi.product(parentId).url),
        "contributors" -> product.contributors)
    }
  }

  /**
   * Product details API.
   */
  def product(id: Long) = TokenSecuredAction { implicit request ⇒
    Product.find(id).map { product ⇒
      Ok(Json.prettyPrint(Json.toJson(product)(productDetailsWrites)))
    }.getOrElse(NotFound("Unknown product"))
  }

  /**
   * Product list API.
   */
  def products = TokenSecuredAction { implicit request ⇒
    val products = productService.findAll
    ProductsCollection.brands(products)
    Ok(Json.prettyPrint(Json.toJson(products)))
  }
}

object ProductsApi extends ProductsApi with ApiAuthentication with Services
