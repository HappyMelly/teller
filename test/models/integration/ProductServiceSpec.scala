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
import models.repository.ProductRepository

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
      (ProductHelper.make("three", parentId = Some(1L)), one.id.get),
      (ProductHelper.make("four", parentId = Some(1L)), two.id.get),
      (ProductHelper.make("five", parentId = Some(2L)), two.id.get)).foreach {
        case (p, id) ⇒
          val product = p.insert
          product.addBrand(id)
      }
  }

  val service = new ProductRepository
  "When findByBrand method is called, the service" should {
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
  "Given a product with title 'one' exists, when this title is checked for existence" >> {
    "then the result should be positive" in {
      service.titleExists("one") must_== true
    }
  }
  "Given a product with title 'test' doesn't exist, when this title is checked for existence" >> {
    "then the result should be negative" in {
      service.titleExists("test") must_== false
    }
  }
  "Given a product with title 'one' and id = 1 exists, when we check if title is taken by any product except with id = 1" >> {
    "then the result should be negative" in {
      service.isTitleTaken("one", 1L) must_== false
    }
  }
  "Given a product with title 'two' and id = 2 exists, when we check if title is taken by any product except with id = 1" >> {
    "then the result should be positive" in {
      service.isTitleTaken("two", 1L) must_== true
    }
  }
  "When findDerivatives method is called, the service" should {
    "return 2 products for a product with id = 1" in {
      val products = service.findDerivatives(1L)
      products.length must_== 2
      products.exists(_.title == "three") must_== true
      products.exists(_.title == "four") must_== true
    }
    "return 1 product for a product with id = 2" in {
      val products = service.findDerivatives(2L)
      products.length must_== 1
      products.exists(_.title == "five") must_== true
    }
    "return 0 products for a product with id = 3" in {
      val products = service.findDerivatives(3L)
      products.length must_== 0
    }
  }
  "When brands mehtod is called, the service" should {
    "return 1 brand for a product with id = 1" in {
      val brands = service.brands(1L)
      brands.length must_== 1
      brands.exists(_.name == "Test Brand") must_== true
    }
    "return 0 brands for a product with id = 10" in {
      val brands = service.brands(10L)
      brands.length must_== 0
    }
  }
  "When a product is deactivated, it" should {
    "change its state from active to inactive" in {
      service.find(1L) map { x ⇒
        x.active must_== true
      } getOrElse ko
      service.deactivate(1L)
      service.find(1L) map { x ⇒
        x.active must_== false
      } getOrElse ko
    }
  }
  "When a product is activated, it" should {
    "change its state from inactive to active" in {
      service.find(1L) map { x ⇒
        x.active must_== false
      } getOrElse ko
      service.activate(1L)
      service.find(1L) map { x ⇒
        x.active must_== true
      } getOrElse ko
    }
  }
}
