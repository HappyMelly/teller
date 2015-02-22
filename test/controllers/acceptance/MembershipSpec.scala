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
import helpers.MemberHelper
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

  On welcome page for person button 'Become Supporter' should
    be active for a non-member                             $e3
    not exist for a supporter                              $e4
    not exist for a funder                                 $e5

  A user should
    not be charged if she is already a member              $e6
    see miminum and suggested fees on the payment form     $e7
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
    contentAsString(result) must contain("I want to become")
  }

  def e3 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.welcome().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Become Supporter")
    contentAsString(result) must contain("href=\"/membership/payment\"")
  }

  def e4 = {
    MemberHelper.make(objectId = 1L, person = true, funder = false).insert
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.welcome().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("You're already a Supporter")
    contentAsString(result) must not contain "Become Supporter"
  }

  def e5 = {
    truncateTables()
    MemberHelper.make(objectId = 1L, person = true, funder = true).insert
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.welcome().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("You're a Funder")
    contentAsString(result) must not contain "Become Supporter"
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
    contentAsString(result) must contain("Minimum fee: EUR 20")
    contentAsString(result) must contain("Suggested fee: EUR 40")
  }
}
