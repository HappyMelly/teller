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

package controllers.acceptance.eventtypes

import _root_.integration.PlayAppSpec
import controllers.EventTypes
import models.UserRole.Role
import stubs.{AccessCheckSecurity, FakeRuntimeEnvironment}

class AccessSpec extends PlayAppSpec {
  class TestEventTypes() extends EventTypes(FakeRuntimeEnvironment)
    with AccessCheckSecurity

  val controller = new TestEventTypes

  "Method 'add'" should {
    "have brand access rights" in {
      controller.add(2L).apply(fakeGetRequest())
      controller.checkedMethod must_== Some("brand")
      controller.checkedObjectId must_== Some(2L)
    }
  }

  "Method 'create'" should {
    "have coordinator access rights for brand object" in {
      controller.create(2L).apply(fakePostRequest())
      controller.checkedMethod must_== Some("brand")
      controller.checkedObjectId must_== Some(2L)
    }
  }

  "Method 'delete'" should {
    "have coordinator access rights for brand object" in {
      controller.delete(2L, 1L).apply(fakePostRequest())
      controller.checkedMethod must_== Some("brand")
      controller.checkedObjectId must_== Some(2L)
    }
  }

  "Method 'index'" should {
    "have Facilitator, Coordinator access rights" in {
      controller.index(1L).apply(fakePostRequest())
      controller.checkedRoles.diff(List(Role.Facilitator, Role.Coordinator)).isEmpty must_== true
    }
  }

  "Method 'update'" should {
    "have coordinator access rights for brand object" in {
      controller.update(2L, 1L).apply(fakePostRequest())
      controller.checkedMethod must_== Some("brand")
      controller.checkedObjectId must_== Some(2L)
    }
  }
}