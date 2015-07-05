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

import helpers.{ MemberHelper, PersonHelper }
import models.UserRole.DynamicRole
import models.{ DynamicResourceChecker, ResourceHandler, UserAccount, ActiveUser, UserRole }
import org.scalamock.specs2.MockContext
import org.specs2.mutable.Specification
import stubs.{ FakeServices, FakeUserIdentity }

class ResourceHandlerSpec extends Specification {

  class TestTellerResourceHandler(user: ActiveUser,
    checker: DynamicResourceChecker)
      extends ResourceHandler(user) with FakeServices {

    def callCheckBrandPermission(account: UserAccount, meta: String, url: String): Boolean =
      checkBrandPermission(user.account, meta, url)

    def callCheckEventPermission(account: UserAccount, meta: String, url: String): Boolean =
      checkEventPermission(user.account, meta, url)

    def callCheckEvaluationPermission(account: UserAccount, meta: String, url: String): Boolean =
      checkEvaluationPermission(user.account, meta, url)

    def callCheckMemberPermission(user: ActiveUser, url: String): Boolean =
      checkMemberPermission(user, url)

    def callCheckPersonPermission(account: UserAccount, meta: String, url: String): Boolean =
      checkPersonPermission(account, meta, url)

    override def checker(account: UserAccount): DynamicResourceChecker = checker
  }

  val editor = UserAccount(None, 1L, "editor", None, None, None, None)
  editor.roles_=(List(UserRole.forName("editor")))
  val viewer = editor.copy(role = "viewer")
  viewer.roles_=(List(UserRole.forName("viewer")))
  val person = PersonHelper.one
  person.member_=(MemberHelper.make(Some(1L), 1L, person = true, funder = false))
  val identity = new FakeUserIdentity(Some(123213L), FakeUserIdentity.viewer,
    "Sergey", "Kotlov", "Sergey Kotlov", None)
  val activeUser = ActiveUser(identity, viewer, person)

  val checker = new DynamicResourceChecker(editor)
  val handler = new TestTellerResourceHandler(activeUser, checker)

  "When brand permissions are checked for coordinator" >> {
    "isBrandCoordinator function should be called" in new MockContext {
      class MockedChecker extends DynamicResourceChecker(editor)
      val checker = mock[MockedChecker]
      val handler = new TestTellerResourceHandler(activeUser, checker)
      (checker.isBrandCoordinator _) expects 1L returning true
      handler.callCheckBrandPermission(editor, DynamicRole.Coordinator, "/1") must_== true
    }
    "and url doesn't containt brand id then permission should not be granted" in {
      handler.callCheckBrandPermission(editor, DynamicRole.Coordinator, "/") must_== false
    }
  }
  "When brand permissions are checked not for coordinator" >> {
    "then permission should not be granted" in {
      handler.callCheckBrandPermission(editor, "anything", "/1") must_== false
    }
  }

  "When event permissions are checked for coordinator" >> {
    "isEventCoordinator function should be called" in new MockContext {
      class MockedChecker extends DynamicResourceChecker(editor)
      val checker = mock[MockedChecker]
      val handler = new TestTellerResourceHandler(activeUser, checker)
      (checker.isEventCoordinator _) expects 1L returning true
      handler.callCheckEventPermission(viewer, DynamicRole.Coordinator, "/1") must_== true
    }
    "and url doesn't contain event id then permission should not be granted" in {
      handler.callCheckEventPermission(editor, DynamicRole.Coordinator, "/") must_== false
    }
  }
  "When event permissions are checked for facilitator" >> {
    "isEventFacilitator function should be called" in new MockContext {
      class MockedChecker extends DynamicResourceChecker(editor)
      val checker = mock[MockedChecker]
      val handler = new TestTellerResourceHandler(activeUser, checker)
      (checker.isEventFacilitator _) expects 1L returning true
      handler.callCheckEventPermission(editor, DynamicRole.Facilitator, "/1") must_== true
    }
    "and url doesn't contain event id then permission should not be granted" in {
      handler.callCheckEventPermission(editor, DynamicRole.Facilitator, "/") must_== false
    }
  }
  "When event permissions are checked for random role" >> {
    "then permission should not be granted" in {
      handler.callCheckEventPermission(editor, "anything", "/1") must_== false
    }
  }

  "When evaluation permissions are checked for coordinator" >> {
    "isEvaluationCoordinator function should be called" in new MockContext {
      class MockedChecker extends DynamicResourceChecker(editor)
      val checker = mock[MockedChecker]
      val handler = new TestTellerResourceHandler(activeUser, checker)
      (checker.isEvaluationCoordinator _) expects 1L returning true
      handler.callCheckEvaluationPermission(editor, DynamicRole.Coordinator, "/1") must_== true
    }

    "and url doesn't contain event id then permission should not be granted" in {
      handler.callCheckEvaluationPermission(editor, DynamicRole.Coordinator, "/") must_== false
    }
  }
  "When evaluation permissions are checked for facilitator" >> {
    "isEvaluationFacilitator function should be called" in new MockContext {
      class MockedChecker extends DynamicResourceChecker(editor)
      val checker = mock[MockedChecker]
      val handler = new TestTellerResourceHandler(activeUser, checker)
      (checker.isEvaluationFacilitator _) expects 1L returning true
      handler.callCheckEvaluationPermission(editor, DynamicRole.Facilitator, "/1") must_== true
    }
    "and url doesn't contain event id then permission should not be granted" in {
      handler.callCheckEvaluationPermission(editor, DynamicRole.Facilitator, "/") must_== false
    }
  }
  "When evaluation permissions are checked for random role" >> {
    "then permission should not be granted" in {
      handler.callCheckEvaluationPermission(editor, "anything", "/1") must_== false
    }
  }

  "When member permissions are checked" >> {
    "and the user is a member and checks his own profile then permission should be granted" in {
      handler.callCheckMemberPermission(activeUser, "/1") must_== true
    }
    "and url doesn't contain member id then permission should not be granted" in {
      handler.callCheckMemberPermission(activeUser, "/") must_== false
    }
  }

  "When person permissions are checked for editing" >> {
    "canEditPerson function should be called" in new MockContext {
      class MockedChecker extends DynamicResourceChecker(editor)
      val checker = mock[MockedChecker]
      val handler = new TestTellerResourceHandler(activeUser, checker)
      (checker.canEditPerson _) expects 1L returning true
      handler.callCheckPersonPermission(editor, "edit", "/1") must_== true
    }

    "and url doesn't contain person id then permission should not be granted" in {
      handler.callCheckPersonPermission(editor, "edit", "/") must_== false
    }
  }
  "When person permissions are checked for deletion" >> {
    "canDeletePerson function should be called" in new MockContext {
      class MockedChecker extends DynamicResourceChecker(editor)
      val checker = mock[MockedChecker]
      val handler = new TestTellerResourceHandler(activeUser, checker)
      (checker.canDeletePerson _) expects 1L returning true
      handler.callCheckPersonPermission(editor, "delete", "/1") must_== true
    }
    "and url doesn't contain person id then permission should not be granted" in {
      handler.callCheckPersonPermission(editor, "delete", "/") must_== false
    }
  }
  "When person permissions are checked for random role" >> {
    "then permission should not be granted" in {
      handler.callCheckPersonPermission(editor, "anything", "/1") must_== false
    }
  }
}
