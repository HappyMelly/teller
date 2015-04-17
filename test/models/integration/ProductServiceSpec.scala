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

package models.integration

import helpers.{ PersonHelper, ProductHelper, BrandHelper }
import integration.PlayAppSpec
import models.service.ProductService

/**
 * Contains a set of function for managing products in database
 */
class ProductServiceSpec extends PlayAppSpec {

  override def setupDb(): Unit = {
    val person = PersonHelper.one().insert
    val one = BrandHelper.one.insert()
    val two = BrandHelper.make("TWO").insert()
    Seq(
      (ProductHelper.make("one"), one.id.get),
      (ProductHelper.make("two"), one.id.get),
      (ProductHelper.make("three"), one.id.get),
      (ProductHelper.make("four"), two.id.get),
      (ProductHelper.make("five"), two.id.get)).foreach {
        case (p, id) ⇒
          val product = p.insert
          product.addBrand(id)
      }
  }

  val service = new ProductService
  "The service" should {
    "return 3 products available in database for brand id = 1" in {
      val products = service.findByBrand(1L)
      products.length must_== 3
      products.exists(_.title == "one") must_== true
      products.exists(_.title == "two") must_== true
      products.exists(_.title == "three") must_== true
    }
    "return 2 products available in database for brand with id = 2" in {
      val products = service.findByBrand(2L)
      products.length must_== 2
      products.exists(_.title == "four") must_== true
      products.exists(_.title == "five") must_== true
    }
    "return products sorted by title" in {
      val names = service.findByBrand(1L).map(_.title)
      names must_== Seq("one", "three", "two")
    }
  }
}
