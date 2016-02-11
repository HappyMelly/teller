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

package models

import models.service.{ContributionService, ProductService}
import org.joda.time.DateTime
import play.api.libs.Crypto

import scala.concurrent.Await
import scala.util.Random
import scala.concurrent.duration._

/**
 * Category classifications that a product has zero or one of.
 */
object ProductCategory extends Enumeration {
  val Book = Value("book")
  val Game = Value("game")
  val Apps = Value("apps")
  val Conference = Value("conference")
  val Course = Value("course")
}

case class ProductView(product: Product, brands: List[Brand], contributors: List[ContributorView])

/**
 * A thing such as a book, a game or a piece of software
 */
case class Product(
    id: Option[Long],
    title: String,
    subtitle: Option[String],
    url: Option[String],
    description: Option[String],
    callToActionUrl: Option[String],
    callToActionText: Option[String],
    picture: Option[String],
    category: Option[ProductCategory.Value],
    parentId: Option[Long],
    active: Boolean = true,
    created: DateTime,
    createdBy: String,
    updated: DateTime,
    updatedBy: String) {

}

object Product {

  def cacheId(id: Long): String = "products." + id.toString

  def generateImageName(filename: String): String = "products/" + Crypto.sign("%s-%s".format(filename, Random.nextInt())) + ".png"
}
