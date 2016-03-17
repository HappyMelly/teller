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
import controllers.BrandFees
import helpers._
import models.cm.brand.BrandFee
import models.repository.cm.BrandRepository
import models.repository.cm.brand.BrandFeeRepository
import org.joda.money.Money
import org.scalamock.specs2.IsolatedMockFactory
import stubs._

class BrandFeesSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  Given a brand doesn't exists
    when its fees are requested
      then an error should be returned                                       $e1

  Given a brand exists
    when its fees are requested
      then a list of fees should be returned                                 $e2
  """

  class TestBrandFees
      extends BrandFees(FakeRuntimeEnvironment) with FakeRepositories with FakeSecurity {
  }

  val controller = new TestBrandFees
  val brandService = mock[BrandRepository]
  val feeService = mock[BrandFeeRepository]
  controller.feeService_=(feeService)
  controller.brandService_=(brandService)

  def e1 = {
    (services.brandService.find(_: Long)) expects 1L returning None
    val result = controller.index(1L).apply(fakeGetRequest())
    status(result) must equalTo(NOT_FOUND)
  }

  def e2 = {
    (services.brandService.find(_: Long)) expects 1L returning Some(BrandHelper.one)
    val fees = List(
      BrandFee(None, 1L, "DE", Money.parse("EUR 100")),
      BrandFee(None, 1L, "RU", Money.parse("EUR 50")))
    (services.feeService.findByBrand _) expects 1L returning fees
    val result = controller.index(1L).apply(fakeGetRequest())
    contentAsString(result) must contain("Germany")
    contentAsString(result) must contain("Russia")
  }
}