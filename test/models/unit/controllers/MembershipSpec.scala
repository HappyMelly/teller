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

package models.unit.controllers

import controllers.{ Membership, PaymentData }
import helpers.{ OrganisationHelper, PersonHelper }
import models.{ Organisation, Person }
import org.specs2.mutable._
import stubs.FakeServices

class MembershipSpec extends Specification {

  class TestMembership() extends Membership with FakeServices {

    def call(data: PaymentData, person: Person, org: Option[Organisation]) = {
      validatePaymentData(data, person, org)
    }
  }

  "validatePaymentData" should {
    val controller = new TestMembership

    "throw an exception if an org doesn't exist" in {
      val data = new PaymentData("", 20, Some(1L))
      val person = PersonHelper.one()
      controller.call(data, person, None) must throwA[Membership.ValidationException]("error.organisation.notExist")
    }
    "throw an exception if the person is not a member of org" in {
      val data = new PaymentData("", 20, Some(1L))
      val person = PersonHelper.one()
      person.memberships_=(List())
      val org = OrganisationHelper.one

      controller.call(data, person, Some(org)) must throwA[Membership.ValidationException]("error.person.notOrgMember")
    }

    "throw an exception if fee is less than minimum for the org" in {
      val data = new PaymentData("", 20, Some(1L))
      val person = PersonHelper.one()
      val org = OrganisationHelper.one.copy(countryCode = "NL")
      person.memberships_=(List(org))

      controller.call(data, person, Some(org)) must throwA[Membership.ValidationException]("error.payment.minimum_fee")
    }

    "throw an exception if fee is less than minimum for the person" in {
      val data = new PaymentData("", 5, None)
      val person = PersonHelper.one()

      controller.call(data, person, None) must throwA[Membership.ValidationException]("error.payment.minimum_fee")
    }
  }
}
