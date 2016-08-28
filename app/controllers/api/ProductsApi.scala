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

import controllers.api.json.ProductConverter
import models.ProductView
import models.repository.Repositories
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Products API.
 */
class ProductsApi @Inject() (val services: Repositories,
                             override val messagesApi: MessagesApi) extends ApiAuthentication(services, messagesApi)
  with I18nSupport {

  private val converter = new ProductConverter
  implicit val productWrites = converter.productWithBrandsWrites


  /**
   * Returns product in JSON format if exists
    *
    * @param id Product id
   */
  def product(id: Long) = TokenSecuredAction(readWrite = false) { implicit request ⇒
    (for {
      p <- services.product.find(id)
      b <- services.product.brands(id)
      c <- services.contribution.contributors(id)
    } yield (p, b, c)) flatMap {
      case (None, _, _) => jsonNotFound("Product not found")
      case (Some(product), brands, contributors) =>
        val withBrands = ProductView(product, brands, contributors)
        jsonOk(Json.toJson(withBrands)(converter.productDetailsWrites))
    }
  }

  /**
   * Returns list of products in JSON format
   */
  def products = TokenSecuredAction(readWrite = false) { implicit request ⇒
    (for {
      products <- services.product.findAll
      withBrands <- services.product.collection.brands(products)
    } yield withBrands) flatMap { products =>
      jsonOk(Json.toJson(products))
    }
  }
}
