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
package controllers.unit

import controllers.{ ProfileStrengths, Security }
import helpers._
import models._
import models.service.{ LicenseService, FacilitatorService }
import org.joda.money.Money
import org.joda.time.LocalDate
import org.scalamock.specs2.IsolatedMockFactory
import org.specs2.mutable._
import stubs._

class ProfileStrengthsSpec extends Specification with IsolatedMockFactory {

  class TestProfileStrengths extends ProfileStrengths with FakeServices {

    def callInitializeProfileStrength(person: Person): ProfileStrength =
      initializeProfileStrength(person)
  }
  val controller = new TestProfileStrengths
  val licenseService = mock[LicenseService]
  val facilitatorService = mock[FacilitatorService]
  controller.facilitatorService_=(facilitatorService)
  controller.licenseService_=(licenseService)

  "When a person is not a member and not a facilitator and has a bio" >> {
    "then an default profile strength with 3 incomplete steps should be returned" in {
      (licenseService.activeLicenses _) expects 1L returning List()
      val person = PersonHelper.one().copy(bio = Some("test"))
      val steps = controller.callInitializeProfileStrength(person).incompleteSteps
      steps.exists(_.name == "about") must_== false
    }
  }
  "When a person is a member and not a facilitator" >> {
    "and reason is not filled, then a profile strength with member steps should be returned" in {
      (licenseService.activeLicenses _) expects 1L returning List()
      val person = PersonHelper.one()
      person.member_=(MemberHelper.make(objectId = 1L, person = true, funder = true))
      val steps = controller.callInitializeProfileStrength(person).incompleteSteps
      steps.exists(_.name == "reason") must_== true
      steps.exists(_.name == "member") must_== false
    }
    "and reason is filled, then a profile strength with member steps should be returned" in {
      (licenseService.activeLicenses _) expects 1L returning List()
      val person = PersonHelper.one()
      person.member_=(MemberHelper.make(objectId = 1L, person = true, funder = true))
      val steps = controller.callInitializeProfileStrength(person).incompleteSteps
      //@TODO add reason
      // steps.exists(_.name == "reason") must_== false
      steps.exists(_.name == "member") must_== false
    }
  }
  "When a person is not a member and a facilitator" >> {
    val brand = BrandHelper.one
    val license = License(None, 1L, 1L, "1", LocalDate.now(), LocalDate.now(),
      LocalDate.now(), true, Money.parse("EUR 100"), None)
    val licenseView = LicenseView(brand, license)

    "and has no signature and languages, then a profile strength with facilitator steps should be returned" in {
      (licenseService.activeLicenses _) expects 1L returning List(licenseView)
      (facilitatorService.languages _) expects 1L returning List()
      val person = PersonHelper.one()
      val steps = controller.callInitializeProfileStrength(person).incompleteSteps
      steps.exists(_.name == "signature") must_== true
      steps.exists(_.name == "language") must_== true
    }
    "and has a signature and 1 language, then a profile strength with facilitator steps should be returned" in {
      (licenseService.activeLicenses _) expects 1L returning List(licenseView)
      val language = FacilitatorLanguage(1L, "EN")
      (facilitatorService.languages _) expects 1L returning List(language)
      val person = PersonHelper.one().copy(signature = true)
      val steps = controller.callInitializeProfileStrength(person).incompleteSteps
      steps.exists(_.name == "signature") must_== false
      steps.exists(_.name == "language") must_== false
    }
  }
}