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
import controllers.Products
import helpers._
import models.Product
import models.repository.ProductRepository
import org.scalamock.specs2.IsolatedMockFactory
import play.api.libs.json._
import play.api.test.FakeRequest
import stubs._

class ProductsSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  When the given product is not found
    during product activation an error should be returned                 $e1

  When the given product is found and form data are valid
    an Editor should be able to activate the product                      $e2
    an Editor should be able to deactivate the product                    $e3
  """

  class TestProducts extends Products(FakeRuntimeEnvironment)
    with FakeRepositories
    with FakeSecurity

  val controller = new TestProducts
  val productService = mock[ProductRepository]
  controller.productService_=(productService)

  def e1 = {
    (services.productService.find _) expects 1L returning None
    val res = controller.activation(1L).apply(fakePostRequest())
    status(res) must equalTo(NOT_FOUND)
    val data = contentAsJson(res).as[JsObject]
    (data \ "message").as[String] must contain("not found")
  }

  def e2 = {
    val product = ProductHelper.make("Test", Some(1L))
    (services.productService.find _) expects 1L returning Some(product)
    (services.productService.activate _) expects 1L
    val req = fakePostRequest().withFormUrlEncodedBody("active" -> "true")
    val res = controller.activation(1L).apply(req)
    status(res) must equalTo(OK)
  }

  def e3 = {
    val product = ProductHelper.make("Test", Some(1L))
    (services.productService.find _) expects 1L returning Some(product)
    (services.productService.deactivate _) expects 1L
    val req = fakePostRequest().withFormUrlEncodedBody("active" -> "false")
    val res = controller.activation(1L).apply(req)
    status(res) must equalTo(OK)
  }
}