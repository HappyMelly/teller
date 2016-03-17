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

import controllers.api._
import org.specs2.mutable.Specification
import play.api.test.FakeRequest
import stubs.FakeNoCallApiAuthentication

class ApiAccessSpec extends Specification {
  override def is = s2"""

  In BrandFees API method
    'fees' should be read-only   $e1

  In Brands API method
    'brand' should be read-only  $e2
    'brands' should be read-only $e3

  In Evaluations API method
    'created' should be read-write $e4
    'confirm' should be read-write $e15

  In Events API method
    'event' should be read-only  $e5
    'events' should be read-only $e6

  In EventTypes API method
    'types' should be read-only  $e7

  In Facilitators API method
    'facilitators' should be read-only $e8

  In Members API method
    'members' should be read-only $e9
    'member' should be read-only  $e10

  In Products API method
    'product' should be read-only $e13
    'products' should be read-only $e14
  """

  class TestBrandFeesApi() extends BrandFeesApi
    with FakeNoCallApiAuthentication

  def e1 = {
    val controller = new TestBrandFeesApi()
    val result = controller.fees("TEST").apply(FakeRequest())
    controller.readWrite must_== false
  }

  class TestBrandsApi() extends BrandsApi
    with FakeNoCallApiAuthentication

  def e2 = {
    val controller = new TestBrandsApi()
    val result = controller.brand("TEST").apply(FakeRequest())
    controller.readWrite must_== false
  }

  def e3 = {
    val controller = new TestBrandsApi()
    val result = controller.brands.apply(FakeRequest())
    controller.readWrite must_== false
  }

  class TestEvaluationsApi() extends EvaluationsApi with FakeNoCallApiAuthentication

  def e4 = {
    val controller = new TestEvaluationsApi
    controller.create().apply(FakeRequest("POST", ""))
    controller.readWrite must_== true
  }

  class TestEventsApi() extends EventsApi with FakeNoCallApiAuthentication

  def e5 = {
    val controller = new TestEventsApi
    controller.event(1L).apply(FakeRequest())
    controller.readWrite must_== false
  }

  def e6 = {
    val controller = new TestEventsApi
    controller.events("Test", None, None, None, None, None, None).apply(FakeRequest())
    controller.readWrite must_== false
  }

  class TestEventTypesApi() extends EventTypesApi with FakeNoCallApiAuthentication

  def e7 = {
    val controller = new TestEventTypesApi
    controller.types("TEST").apply(FakeRequest())
    controller.readWrite must_== false
  }

  class TestFacilitatorsApi() extends FacilitatorsApi with FakeNoCallApiAuthentication

  def e8 = {
    val controller = new TestFacilitatorsApi
    controller.facilitators("TEST").apply(FakeRequest())
    controller.readWrite must_== false
  }

  class TestMembersApi() extends MembersApi with FakeNoCallApiAuthentication

  def e9 = {
    val controller = new TestMembersApi
    controller.members().apply(FakeRequest())
    controller.readWrite must_== false
  }

  def e10 = {
    val controller = new TestMembersApi
    controller.member("1", true).apply(FakeRequest())
    controller.readWrite must_== false
  }

  class TestProductsApi() extends ProductsApi with FakeNoCallApiAuthentication

  def e13 = {
    val controller = new TestProductsApi
    controller.product(1L).apply(FakeRequest())
    controller.readWrite must_== false
  }

  def e14 = {
    val controller = new TestProductsApi
    controller.products().apply(FakeRequest())
    controller.readWrite must_== false
  }

  def e15 = {
    val controller = new TestEvaluationsApi
    controller.confirm("test").apply(FakeRequest())
    controller.readWrite must_== true
  }
}