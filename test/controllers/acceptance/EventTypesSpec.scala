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

package controllers.acceptance

import controllers.EventTypes
import helpers.BrandHelper
import integration.PlayAppSpec
import models.brand.EventType
import org.scalamock.specs2.MockContext
import play.api.libs.json.JsObject
import play.api.mvc.SimpleResult
import stubs.{ FakeBrandService, FakeEventTypeService, FakeUserIdentity, FakeServices }

import scala.concurrent.Future

class EventTypesSpec extends PlayAppSpec {
  class TestEventTypes extends EventTypes with FakeServices

  override def is = s2"""

  'Update' action should
    be accessible to Editors                               $e1
    not be accessible to Viewers                           $e2
    return JSON with error if event type is not found    $e3
    return JSON with error if event type with such name already exists $e4
    return JSON if event type is successfully updated                  $e5
  """

  val controller = new TestEventTypes()

  def e1 = {
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/")
    val result: Future[SimpleResult] = controller.update(1L).apply(req)

    status(result) must equalTo(NOT_FOUND)
  }

  def e2 = {
    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/update/2")
    val result: Future[SimpleResult] = controller.update(2L).apply(req)

    status(result) must equalTo(SEE_OTHER)
  }

  def e3 = new MockContext {
    val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/")
    val service = mock[FakeEventTypeService]
    (service.find _).expects(1L).returning(None)
    controller.eventTypeService_=(service)
    val res = controller.update(1L).apply(req)
    status(res) must equalTo(NOT_FOUND)
    contentAsString(res) must contain("is not found")
    contentAsJson(res) must beAnInstanceOf[JsObject]
  }

  def e4 = new MockContext {
    val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
      withFormUrlEncodedBody(("id", "1"), ("brandId", "1"), ("name", "Test"))
    val eventType = EventType(Some(1L), 1L, "Test", Some("test type"))
    val eventTypeService = mock[FakeEventTypeService]
    (eventTypeService.find _).expects(1L).returning(Some(eventType))
    val types = List(eventType.copy(id = Some(2L)))
    (eventTypeService.findByBrand _).expects(1L).returning(types)
    val brandService = mock[FakeBrandService]
    val brand = BrandHelper.one
    (brandService.find _).expects(1L).returning(Some(brand))
    controller.brandService_=(brandService)
    controller.eventTypeService_=(eventTypeService)
    val res = controller.update(1L).apply(req)
    status(res) must equalTo(BAD_REQUEST)
    contentAsString(res) must contain("already exists")
    contentAsJson(res) must beAnInstanceOf[JsObject]
  }

  def e5 = new MockContext {
    val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
      withFormUrlEncodedBody(("id", "1"), ("brandId", "1"), ("name", "Test"))
    val eventType = EventType(Some(1L), 1L, "Test", Some("test type"))
    val eventTypeService = mock[FakeEventTypeService]
    (eventTypeService.find _).expects(1L).returning(Some(eventType))
    (eventTypeService.findByBrand _).expects(1L).returning(List())
    (eventTypeService.update _).expects(*)
    val brandService = mock[FakeBrandService]
    val brand = BrandHelper.one
    (brandService.find _).expects(1L).returning(Some(brand))
    controller.brandService_=(brandService)
    controller.eventTypeService_=(eventTypeService)
    val res = controller.update(1L).apply(req)
    status(res) must equalTo(OK)
    contentAsString(res) must contain("success")
    contentAsJson(res) must beAnInstanceOf[JsObject]
  }
}
