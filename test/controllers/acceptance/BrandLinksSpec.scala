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
import controllers.BrandLinks
import helpers._
import models.Brand
import models.brand.BrandLink
import models.service.BrandService
import org.scalamock.specs2.IsolatedMockFactory
import play.api.libs.json._
import play.api.test.FakeRequest
import stubs._

class BrandLinksSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  When the given brand is not found
    during brand link creation an error should be returned               $e1

  Given a brand link has an empty link when the brand link is being added
    then an error should be returned                                     $e2

  Given a brand link has unknown type when the brand link is being added
    then it should get 'Other' type                                      $e3

  When the brand link is being deleted
    successful response should be always returned                        $e4
  """

  class TestBrandLinks extends BrandLinks(FakeRuntimeEnvironment)
    with FakeServices
    with FakeSecurity

  val controller = new TestBrandLinks
  val brandService = mock[BrandService]
  controller.brandService_=(brandService)

  def e1 = {
    (services.brandService.find(_: Long)) expects 1L returning None
    val res = controller.create(1L).apply(fakePostRequest())
    status(res) must equalTo(NOT_FOUND)
    val data = contentAsJson(res).as[JsObject]
    (data \ "message").as[String] must_== "Brand is not found"
  }

  def e2 = {
    val brand = BrandHelper.one
    (services.brandService.find(_: Long)) expects 1L returning Some(brand)
    val req = fakePostRequest().
      withFormUrlEncodedBody("type" -> "video", "url" -> "")
    val res = controller.create(1L).apply(req)
    status(res) must equalTo(BAD_REQUEST)
    val data = contentAsJson(res).as[JsObject]
    (data \ "message").as[String] must_== "Link cannot be empty"
  }

  def e3 = {
    val brand = BrandHelper.one
    (services.brandService.find(_: Long)) expects 1L returning Some(brand)
    val brandLink = BrandLink(None, 1L, "other", "http://test.com")
    (services.brandService.insertLink _) expects brandLink returning brandLink.copy(id = Some(1L))
    val req = fakePostRequest().
      withFormUrlEncodedBody("type" -> "blabla", "url" -> "http://test.com")
    val res = controller.create(1L).apply(req)
    status(res) must equalTo(OK)
  }

  def e4 = {
    (services.brandService.deleteLink _) expects (2L, 1L)
    val res = controller.remove(2L, 1L).apply(fakeDeleteRequest())
    status(res) must equalTo(OK)
  }

}