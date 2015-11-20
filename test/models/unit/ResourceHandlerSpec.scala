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

package models.unit

import helpers.{MemberHelper, PersonHelper}
import models._
import models.service.{MemberService, OrganisationService}
import org.scalamock.specs2.{IsolatedMockFactory, MockContext}
import org.specs2.mutable.Specification
import stubs.{FakeServices, FakeUserIdentity}

class ResourceHandlerSpec extends Specification with IsolatedMockFactory {

  class TestResourceHandler(user: ActiveUser)
    extends ResourceHandler(user) with FakeServices {


    def callCheckMemberPermission(user: ActiveUser, objectId: Long): Boolean =
      checkMemberPermission(user, objectId)

  }

  val viewer = UserAccount(None, 1L, None, None, None, None)
  val admin = viewer.copy(admin = true)
  val person = PersonHelper.one()
  person.member_=(MemberHelper.make(Some(1L), 1L, person = true, funder = false))
  val identity = new FakeUserIdentity(Some(123213L), FakeUserIdentity.viewer,
    "Sergey", "Kotlov", "Sergey Kotlov", None)
  val activeUser = ActiveUser(identity, viewer, person)

  val handler = new TestResourceHandler(activeUser)
  val memberService = mock[MemberService]
  handler.memberService_=(memberService)

  "When brand permissions are checked for coordinator" >> {
    "isBrandCoordinator function should be called" in new MockContext {
      val handler = new TestResourceHandler(activeUser)
      //      (checker.isBrandCoordinator _) expects 1L returning true
      //      handler.callCheckBrandPermission(admin, 1L) must_== true
    }
  }

  "When member permissions are checked" >> {
    "and the user is an admin then permission should be granted" in {
      val user = ActiveUser(identity, admin, person)
      handler.callCheckMemberPermission(user, 2L) must_== true
    }
    "and the user is a member and checks his own profile then permission should be granted" in {
      handler.callCheckMemberPermission(activeUser, 1L) must_== true
    }
    "and the user is not a member then permission should not be granted" in {
      (memberService.find(_: Long)) expects 3L returning None
      handler.callCheckMemberPermission(activeUser, 3L) must_== false
    }
    "and the user is a member but she checks the profile of other member then permission should not be granted" in {
      val member = MemberHelper.make(Some(1L), 3L, person = true, funder = true)
      (memberService.find(_: Long)) expects 3L returning Some(member)
      handler.callCheckMemberPermission(activeUser, 3L) must_== false
    }
    "and the user is an employee of an organisation which is a member and checks the profile of this organisation then permission should be granted" in {
      val member = MemberHelper.make(Some(1L), 3L, person = false, funder = true)
      (memberService.find(_: Long)) expects 3L returning Some(member)
      val orgService = mock[OrganisationService]
      handler.orgService_=(orgService)
      (orgService.people _) expects 3L returning List(person)
      handler.callCheckMemberPermission(activeUser, 3L) must_== true
    }
  }
}
