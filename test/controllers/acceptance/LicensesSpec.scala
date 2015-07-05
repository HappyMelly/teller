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
import controllers.{ Licenses, Security }
import helpers._
import models.{ LicenseLicenseeBrandView, Brand, License, Person }
import models.service.LicenseService
import org.joda.time.LocalDate
import org.joda.money.Money
import org.joda.money.CurrencyUnit._
import org.scalamock.specs2.IsolatedMockFactory
import play.api.libs.json._
import play.api.test.FakeRequest
import stubs._

class LicensesSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  When license with the given id doesn't exist
    the system should return an error on update                     $e1

  When license with the given id exists
    the system should replace license id and licensee id on update  $e2
  """

  class TestLicenses() extends Licenses(FakeRuntimeEnvironment)
    with FakeSecurity with FakeServices

  val controller = new TestLicenses()
  val licenseService = mock[LicenseService]
  controller.licenseService_=(licenseService)

  def e1 = {
    (licenseService.findWithBrandAndLicensee _) expects 1L returning None
    val result = controller.update(1L).apply(fakePostRequest())
    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/people")
  }

  def e2 = {
    val brand = BrandHelper.one
    val person = PersonHelper.one
    val now = LocalDate.now()
    val license = new License(Some(1L), person.id.get, brand.id.get,
      "1", now.minusYears(1), now.minusDays(1), now.plusDays(2),
      true, Money.of(EUR, 201), Some(Money.of(EUR, 200)))
    val view = LicenseLicenseeBrandView(license, brand, person)

    (licenseService.findWithBrandAndLicensee _) expects 1L returning Some(view)
    (licenseService.update _) expects license.copy(version = "v1")

    val req = fakePostRequest().
      withFormUrlEncodedBody("id" -> "3", "licenseeId" -> "4", "brandId" -> "1",
        "version" -> "v1", "signed" -> license.signed.toString,
        "start" -> license.start.toString, "end" -> license.end.toString,
        "confirmed" -> "true", "fee.currency" -> "EUR", "fee.amount" -> "201",
        "feePaid.currency" -> "EUR", "feePaid.amount" -> "200")

    val result = controller.update(1L).apply(req)
    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/person/1#facilitation")
  }
}