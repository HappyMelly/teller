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

package controllers.unit

import controllers.apiv2.BrandFeesApi
import helpers.BrandHelper
import models.brand.BrandFee
import models.repository.BrandRepository
import models.repository.brand.BrandFeeRepository
import org.joda.money.Money
import org.scalamock.specs2.MockContext
import org.specs2.mutable._
import play.api.libs.json.{ Json, JsArray }
import play.api.test.FakeRequest
import play.api.test.Helpers._
import stubs.{ FakeRepositories, FakeApiAuthentication }

class BrandFeesApiSpec extends Specification {

  class TestBrandFeesApi extends BrandFeesApi with FakeApiAuthentication with FakeRepositories

  "Method 'fees'" should {
    "return 2 fees in the correct JSON format" in new MockContext {
      val fees = List(
        BrandFee(Some(1), 1L, "RU", Money.parse("RUB 100")),
        BrandFee(Some(2), 1L, "DE", Money.parse("EUR 50")))
      val brandFeeService = mock[BrandFeeRepository]
      (brandFeeService.findByBrand _).expects(1L).returning(fees)
      val brandService = mock[BrandRepository]
      (services.brandService.find(_: String)).expects("TEST").returning(Some(BrandHelper.one))
      val controller = new TestBrandFeesApi
      controller.feeService_=(brandFeeService)
      controller.brandService_=(brandService)

      val res = controller.fees("TEST").apply(FakeRequest())
      status(res) must beEqualTo(OK)
      val data = contentAsJson(res).as[JsArray]
      data.value.length must_== 2
      data.value(0) must_== Json.obj(
        "id" -> 1,
        "country" -> "RU",
        "fee" -> "RUB 100.00")
      data.value(1) must_== Json.obj(
        "id" -> 2,
        "country" -> "DE",
        "fee" -> "EUR 50.00")
    }
  }
}
