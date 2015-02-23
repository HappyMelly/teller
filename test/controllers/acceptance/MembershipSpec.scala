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
import play.api.mvc.SimpleResult
import play.api.test.FakeRequest
import scala.concurrent.Future
import stubs.{ StubUserIdentity, FakeServices }

class MembershipSpec extends PlayAppSpec {
  class TestMembership() extends Membership with Security with FakeServices

  def setupDb() {}
  def cleanupDb() {}

  override def is = s2"""

  Welcome page for person should
    not be visible to unauthorized user                  $e1
    be visible to authorized user                        $e2

  On welcome page button 'Become a Supporter' as individual should
    be active for a non-member                             $e3
    not exist for a supporter                              $e4
    not exist for a funder                                 $e5

  A user should
    not be charged if she is already a member              $e6
    see miminum and suggested fees on the payment form     $e7

  On welcome page the block 'My org wants to be a Supporter' should
    contain a list of non-member orgs where the user works            $e8
    contain a notice if zero non-member orgs exist                    $e9
    have active 'Make .. a Supporter' button if at least one non-member org exists $e10
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
    contentAsString(result) must contain("You are already a member")
  }

  def e7 = {
    truncateTables()
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.payment().apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("minimum fee is <b>EUR 20</b>")
    contentAsString(result) must contain("suggested fee is <b>EUR 40</b>")
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
}
