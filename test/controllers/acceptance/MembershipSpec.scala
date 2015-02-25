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

import controllers.{ Membership, Security }
import helpers.{ OrganisationHelper, PersonHelper, MemberHelper }
import integration.PlayAppSpec
import org.scalamock.specs2.MockContext
import play.api.mvc.SimpleResult
import play.api.test.FakeRequest
import scala.concurrent.Future
import stubs.{ FakeOrganisationService, StubUserIdentity, FakeServices }

class MembershipSpec extends PlayAppSpec {
  class TestMembership() extends Membership with Security with FakeServices

  def setupDb() {}
  def cleanupDb() {}

  override def is = s2"""

  Welcome page should
    not be visible to unauthorized user                  $e1
    be visible to authorized user                        $e2

  On Welcome page button 'Become a Supporter' as individual should
    be active for a non-member                             $e3
    not exist for a supporter                              $e4
    not exist for a funder                                 $e5

  A payer should
    not be charged if she is already a member              $e6
    see minimum and suggested fees on the payment form     $e7

  On welcome page the block 'My org wants to be a Supporter' should
    contain a list of non-member orgs where the user works            $e8
    contain a notice if zero non-member orgs exist                    $e9
    have active 'Make .. a Supporter' button if at least one non-member org exists $e10

  A payer should be redirected to welcome page if
    the org does not exist                                            $e11
    he is not a member of the org                                     $e12

  A payer on behalf of the org should should
    see 'Make My Organisation a Supporter'                            $e13
    see minimum and suggested fees for the org                        $e14

  'Welcome New User' page should
    be visible to unauthorized user                                   $e15
    have three active buttons                                         $e16
  """

  val controller = new TestMembership()

  def e1 = {
    val result: Future[SimpleResult] = controller.welcome().apply(FakeRequest())

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome.which(_.contains("login"))
  }

  def e2 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.welcome().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Join Happy Melly network")
  }

  def e3 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.welcome().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Become a Supporter")
    contentAsString(result) must contain("href=\"/membership/payment\"")
  }

  def e4 = {
    MemberHelper.make(objectId = 1L, person = true, funder = false).insert
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.welcome().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("You're already a Supporter")
    contentAsString(result) must not contain "Become a Supporter"
  }

  def e5 = {
    truncateTables()
    MemberHelper.make(objectId = 1L, person = true, funder = true).insert
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.welcome().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("You're a Funder")
    contentAsString(result) must not contain "Become a Supporter"
  }

  def e6 = {
    truncateTables()
    MemberHelper.make(objectId = 1L, person = true, funder = true).insert
    val req = prepareSecuredPostRequest(StubUserIdentity.viewer, "/").
      withFormUrlEncodedBody(("token", "stub"), ("fee", "20"))
    val result: Future[SimpleResult] = controller.charge().apply(req)

    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("This person is already a member")
  }

  def e7 = {
    truncateTables()
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.payment(None).apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("minimum fee is <b>EUR 20</b>")
    contentAsString(result) must contain("suggested fee is <b>EUR 40</b>")
    contentAsString(result) must contain("You are from United Kingdom")
  }

  def e8 = {
    truncateTables()
    val person = PersonHelper.one().insert
    val org1 = OrganisationHelper.one.insert
    val org2 = OrganisationHelper.two.insert
    val org3 = OrganisationHelper.make(id = Some(3L), name = "Three").insert
    person.addMembership(1L)
    person.addMembership(2L)
    person.addMembership(3L)
    //the person and org3 are members
    MemberHelper.make(objectId = 1L, person = true, funder = true).insert
    MemberHelper.make(objectId = 3L, person = false, funder = false).insert

    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.welcome().apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must contain(">Select organisation<")
    contentAsString(result) must contain(">One<")
    contentAsString(result) must contain(">Two<")
    contentAsString(result) must not contain ">Three<"
  }

  def e9 = {
    truncateTables()
    val person = PersonHelper.one().insert
    val org1 = OrganisationHelper.one.insert
    val org2 = OrganisationHelper.two.insert
    person.addMembership(1L)
    person.addMembership(2L)
    //the person and all orgs are members
    MemberHelper.make(objectId = 1L, person = true, funder = true).insert
    MemberHelper.make(objectId = 1L, person = false, funder = false).insert
    MemberHelper.make(objectId = 2L, person = false, funder = false).insert

    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.welcome().apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must not contain ">Select organisation<"
    contentAsString(result) must contain("All your organisations are members")
  }

  def e10 = {
    truncateTables()
    val person = PersonHelper.one().insert
    val org1 = OrganisationHelper.one.insert
    person.addMembership(1L)

    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.welcome().apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must contain(">Select organisation<")
    contentAsString(result) must contain(">One<")
    contentAsString(result) must contain("Make My Organisation a Supporter")
  }

  def e11 = new MockContext {
    val orgService = mock[FakeOrganisationService]
    (orgService.find _).expects(1L).returning(None)
    controller.orgService_=(orgService)

    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "")
    val result: Future[SimpleResult] = controller.payment(Some(1L)).apply(req)

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome.which(_.contains("membership/welcome"))
  }

  def e12 = new MockContext {
    truncateTables()

    val org = OrganisationHelper.one
    val orgService = mock[FakeOrganisationService]
    (orgService.find _).expects(1L).returning(Some(org))
    controller.orgService_=(orgService)

    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "")
    val result: Future[SimpleResult] = controller.payment(Some(1L)).apply(req)

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome.which(_.contains("membership/welcome"))
  }

  def e13 = new MockContext {
    truncateTables()

    val org = OrganisationHelper.one.insert
    val orgService = mock[FakeOrganisationService]
    (orgService.find _).expects(1L).returning(Some(org))
    controller.orgService_=(orgService)
    val person = PersonHelper.one().insert
    person.addMembership(1L)

    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "")
    val result: Future[SimpleResult] = controller.payment(Some(1L)).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Make My Organisation a Supporter")
  }

  def e14 = new MockContext {
    truncateTables()

    val org = OrganisationHelper.one.copy(countryCode = "NL").insert
    val orgService = mock[FakeOrganisationService]
    (orgService.find _).expects(1L).returning(Some(org))
    controller.orgService_=(orgService)
    // this person is from United Kingdom
    val person = PersonHelper.one().insert
    person.addMembership(1L)

    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "")
    val result: Future[SimpleResult] = controller.payment(Some(1L)).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("One is from Netherlands")
    contentAsString(result) must not contain "You are from United Kingdom"
    contentAsString(result) must contain("minimum fee is <b>EUR 25</b>")
    contentAsString(result) must contain("suggested fee is <b>EUR 50</b>")
    contentAsString(result) must contain("<input type=\"hidden\" name=\"orgId\" value=\"1\"/>")
  }

  def e15 = {
    val result: Future[SimpleResult] = controller.welcomeNewUser().apply(FakeRequest())

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Join Happy Melly network")
  }

  def e16 = {
    val result: Future[SimpleResult] = controller.welcomeNewUser().apply(FakeRequest())

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Become a Supporter as Individual")
    contentAsString(result) must contain("Become a Supporter as Organisation")
    contentAsString(result) must contain("Become a Funder")
    contentAsString(result) must contain("Join Happy Melly network")
  }
}
