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

import _root_.integration.PlayAppSpec
import controllers.Membership
import helpers.{ OrganisationHelper, PersonHelper, MemberHelper }
import models.service.{ PersonService, OrganisationService }
import org.scalamock.specs2.IsolatedMockFactory
import stubs._

class MembershipSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  On welcome page button 'Become a Supporter' as individual should
    be active for a non-member                             $e3
    not exist for a supporter                              $e4
    not exist for a funder                                 $e5

  A payer should
    not be charged if she is already a member                   $e6
    see minimum, regular and elite fees on the payment form     $e7

  On welcome page the block 'My org wants to be a Supporter' should
    contain a list of non-member orgs where the user works            $e8
    contain a notice if zero non-member orgs exist                    $e9
    have active 'Make .. a Supporter' button if at least one non-member org exists $e10

  A payer should be redirected to welcome page if
    the org does not exist                                            $e11
    he is not a member of the org                                     $e12

  A payer on behalf of the org should should
    see 'Make My Organisation a Supporter'                            $e13
    see minimum, regular and elite fees for the org                   $e14
  """

  class TestMembership() extends Membership(FakeRuntimeEnvironment)
    with FakeSecurity with FakeServices

  val controller = new TestMembership()
  val orgService = mock[OrganisationService]
  val personService = mock[PersonService]
  controller.orgService_=(orgService)
  controller.personService_=(personService)
  val person = PersonHelper.one

  def e3 = {
    (services.personService.memberships _) expects 1L returning List()
    val result = controller.welcome().apply(fakeGetRequest())

    contentAsString(result) must contain("Become a Supporter")
    contentAsString(result) must contain("href=\"/membership/payment\"")
  }

  def e4 = {
    (services.personService.memberships _) expects 1L returning List()
    person.member_=(MemberHelper.make(objectId = 1L, person = true, funder = false))
    controller.activeUser_=(person)
    val result = controller.welcome().apply(fakeGetRequest())

    contentAsString(result) must contain("You're already a Supporter")
    contentAsString(result) must not contain "Become a Supporter"
  }

  def e5 = {
    (services.personService.memberships _) expects 1L returning List()
    person.member_=(MemberHelper.make(objectId = 1L, person = true, funder = true))
    controller.activeUser_=(person)
    val result = controller.welcome().apply(fakeGetRequest())

    contentAsString(result) must contain("You're a Funder")
    contentAsString(result) must not contain "Become a Supporter"
  }

  def e6 = {
    person.member_=(MemberHelper.make(objectId = 1L, person = true, funder = true))
    controller.activeUser_=(person)
    val req = fakePostRequest().
      withFormUrlEncodedBody(("token", "stub"), ("fee", "20"))
    val result = controller.charge().apply(req)

    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("This person is already a member")
  }

  def e7 = {
    val result = controller.payment(None).apply(fakeGetRequest())

    contentAsString(result) must contain("the minimum fee is <b>EUR 20</b>")
    contentAsString(result) must contain("the regular fee is <b>EUR 40</b>")
    contentAsString(result) must contain("the elite fee is <b>EUR 80</b>")
    contentAsString(result) must contain("You are from United Kingdom")
  }

  def e8 = {
    val org1 = OrganisationHelper.one
    val org2 = OrganisationHelper.two
    val org3 = OrganisationHelper.make(id = Some(3L), name = "Three")
    org3.member_=(MemberHelper.make(objectId = 3L, person = false, funder = false))
    (services.personService.memberships _) expects 1L returning List(org1, org2, org3)
    person.member_=(MemberHelper.make(objectId = 1L, person = true, funder = true))
    controller.activeUser_=(person)
    val result = controller.welcome().apply(fakeGetRequest())

    contentAsString(result) must contain(">Select organisation<")
    contentAsString(result) must contain(">One<")
    contentAsString(result) must contain(">Two<")
    contentAsString(result) must not contain ">Three<"
  }

  def e9 = {
    person.member_=(MemberHelper.make(objectId = 1L, person = true, funder = true))
    controller.activeUser_=(person)
    val org1 = OrganisationHelper.one
    org1.member_=(MemberHelper.make(objectId = 1L, person = false, funder = false))
    val org2 = OrganisationHelper.two
    org2.member_=(MemberHelper.make(objectId = 2L, person = false, funder = false))
    (services.personService.memberships _) expects 1L returning List(org1, org2)

    val result = controller.welcome().apply(fakeGetRequest())
    contentAsString(result) must not contain ">Select organisation<"
    contentAsString(result) must contain("All your organisations are members")
  }

  def e10 = {
    val org = OrganisationHelper.make(Some(4L), "One")
    (services.personService.memberships _) expects 1L returning List(org)
    val result = controller.welcome().apply(fakeGetRequest())

    contentAsString(result) must contain(">Select organisation<")
    contentAsString(result) must contain(">One<")
    contentAsString(result) must contain("Make My Organisation a Supporter")
  }

  def e11 = {
    (services.orgService.find(_: Long)).expects(1L).returning(None)
    val result = controller.payment(Some(1L)).apply(fakeGetRequest())

    header("Location", result) must beSome.which(_.contains("membership/welcome"))
  }

  def e12 = {
    val org = OrganisationHelper.one
    (services.orgService.find(_: Long)).expects(1L).returning(Some(org))
    (services.personService.memberships _) expects 1L returning List()

    val result = controller.payment(Some(1L)).apply(fakeGetRequest())

    header("Location", result) must beSome.which(_.contains("membership/welcome"))
  }

  def e13 = {
    val org = OrganisationHelper.one
    (services.orgService.find(_: Long)).expects(1L).returning(Some(org))
    (services.personService.memberships _) expects 1L returning List(org)

    val result = controller.payment(Some(1L)).apply(fakeGetRequest())

    contentAsString(result) must contain("Make My Organisation a Supporter")
  }

  def e14 = {
    val org = OrganisationHelper.one.copy(countryCode = "NL")
    (services.orgService.find(_: Long)).expects(1L).returning(Some(org))
    // default person is from United Kingdom
    (services.personService.memberships _) expects 1L returning List(org)

    val result = controller.payment(Some(1L)).apply(fakeGetRequest())

    contentAsString(result) must contain("One is from Netherlands")
    contentAsString(result) must not contain "You are from United Kingdom"
    contentAsString(result) must contain("the minimum fee is <b>EUR 25</b>")
    contentAsString(result) must contain("the regular fee is <b>EUR 50</b>")
    contentAsString(result) must contain("the elite fee is <b>EUR 100</b>")
    contentAsString(result) must contain("<input type=\"hidden\" name=\"orgId\" value=\"1\"/>")
  }
}
