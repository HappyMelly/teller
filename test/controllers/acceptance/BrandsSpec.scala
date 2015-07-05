/*
* Happy Melly Teller
* Copyright (C) 2013 - 2015, Happy Melly http -> //www.happymelly.com
*
* This file is part of the Happy Melly Teller.
*
* Happy Melly Teller is free software ->  you can redistribute it and/or modify
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
* along with Happy Melly Teller.  If not, see <http -> //www.gnu.org/licenses/>.
*
* If you have questions concerning this license or the applicable additional
* terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
* or in writing Happy Melly One, Handelsplein 37, Rotterdam,
* The Netherlands, 3071 PR
*/
package controllers.acceptance

import _root_.integration.PlayAppSpec
import controllers.Brands
import helpers._
import models.service.{ BrandService, ProductService }
import org.scalamock.specs2.IsolatedMockFactory
import play.api.libs.json._
import stubs._

class BrandsSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  When the given brand is not found
    during brand activation an error should be returned                 $e1

  When the given brand is found and form data are valid
    an Editor should be able to activate the brand                      $e2
    an Editor should be able to deactivate the brand                    $e3

  When the given brand is deactivated
    related products should be also deactivated                         $e4
  """

  class TestBrands extends Brands(FakeRuntimeEnvironment)
    with FakeServices
    with FakeSecurity

  val controller = new TestBrands
  val brandService = mock[BrandService]
  val productService = mock[ProductService]
  controller.productService_=(productService)
  controller.brandService_=(brandService)

  def e1 = {
    (brandService.find(_: Long)) expects 1L returning None
    // val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "")
    val res = controller.activation(1L).apply(fakePostRequest())
    status(res) must equalTo(NOT_FOUND)
    val data = contentAsJson(res).as[JsObject]
    (data \ "message").as[String] must_== "Brand is not found"
  }

  def e2 = {
    val brand = BrandHelper.one
    (brandService.find(_: Long)) expects 1L returning Some(brand)
    (brandService.activate _) expects 1L
    val req = fakePostRequest().withFormUrlEncodedBody("active" -> "true")
    val res = controller.activation(1L).apply(req)
    status(res) must equalTo(OK)
  }

  def e3 = {
    val brand = BrandHelper.one
    (brandService.find(_: Long)) expects 1L returning Some(brand)
    (brandService.deactivate _) expects 1L
    (productService.findByBrand _) expects 1L returning List()
    val req = fakePostRequest().withFormUrlEncodedBody("active" -> "false")
    val res = controller.activation(1L).apply(req)
    status(res) must equalTo(OK)
  }

  def e4 = {
    val brand = BrandHelper.one
    (brandService.find(_: Long)) expects 1L returning Some(brand)
    (brandService.deactivate _) expects 1L
    val products = List(ProductHelper.make("One", Some(1L)),
      ProductHelper.make("Two", Some(2L)))
    (productService.findByBrand _) expects 1L returning products
    (productService.deactivate _) expects 1L
    (productService.deactivate _) expects 2L
    val req = fakePostRequest().withFormUrlEncodedBody("active" -> "false")
    val res = controller.activation(1L).apply(req)
    status(res) must equalTo(OK)
  }

}