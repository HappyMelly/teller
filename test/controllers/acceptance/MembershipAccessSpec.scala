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
import controllers.{ Membership, Security }
import helpers.{ OrganisationHelper, PersonHelper, MemberHelper }
import models.service.PersonService
import org.scalamock.specs2.IsolatedMockFactory
import play.api.test.FakeRequest
import stubs._

class MembershipAccessSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  Welcome page for person should
    not be visible to unauthorized user                  $e1
    be visible to authorized user                        $e2
  """

  class TestMembership() extends Membership with Security with FakeServices

  val controller = new TestMembership()
  val personService = mock[PersonService]
  controller.personService_=(personService)

  def e1 = {
    val result = controller.welcome().apply(FakeRequest())

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome.which(_.contains("login"))
  }

  def e2 = {
    (personService.memberships _) expects 1L returning List()
    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/")
    val result = controller.welcome().apply(req)

    contentAsString(result) must contain("Join Happy Melly network")
  }
}
