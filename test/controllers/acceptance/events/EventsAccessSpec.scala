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

package controllers.acceptance.events

import _root_.integration.PlayAppSpec
import controllers.cm.Events
import models.UserRole.Role
import stubs.{AccessCheckSecurity, FakeRuntimeEnvironment}

class EventsAccessSpec extends PlayAppSpec {
  class TestEvents() extends Events(FakeRuntimeEnvironment) with AccessCheckSecurity

  val controller = new TestEvents()

  "Method 'add'" should {
    "have Facilitator, Coordinator access rights" in {
      controller.add.apply(fakeGetRequest())
      controller.checkedRoles.diff(List(Role.Facilitator, Role.Coordinator)).isEmpty must_== true
    }
  }

  "Method 'cancel'" should {
    "have Facilitator access rights for 'event' object" in {
      controller.cancel(1L).apply(fakePostRequest())
      controller.checkedRoles.diff(List(Role.Facilitator, Role.Coordinator)).isEmpty must_== true
      controller.checkedObjectId must_== Some(1L)
    }
  }

  "Method 'confirm'" should {
    "have Facilitator access rights for 'event' object" in {
      controller.confirm(1L).apply(fakeGetRequest())
      controller.checkedRoles.diff(List(Role.Facilitator, Role.Coordinator)).isEmpty must_== true
      controller.checkedObjectId must_== Some(1L)
    }
  }

  "Method 'create'" should {
    "have Facilitator, Coordinator access rights" in {
      controller.create.apply(fakePostRequest())
      controller.checkedRoles.diff(List(Role.Facilitator, Role.Coordinator)).isEmpty must_== true
    }
  }

  "Method 'details'" should {
    "have Facilitator, Coordinator access rights" in {
      controller.details(1L).apply(fakePostRequest())
      controller.checkedRoles.diff(List(Role.Facilitator, Role.Coordinator)).isEmpty must_== true
    }
  }

  "Method 'duplicate'" should {
    "have Facilitator access rights for 'event' object" in {
      controller.duplicate(1L).apply(fakeGetRequest())
      controller.checkedRoles.diff(List(Role.Facilitator, Role.Coordinator)).isEmpty must_== true
      controller.checkedObjectId must_== Some(1L)
    }
  }

  "Method 'edit'" should {
    "have Facilitator access rights for 'event' object" in {
      controller.edit(1L).apply(fakeGetRequest())
      controller.checkedRoles.diff(List(Role.Facilitator, Role.Coordinator)).isEmpty must_== true
      controller.checkedObjectId must_== Some(1L)
    }
  }

  "Method 'index'" should {
    "have Viewer access rights" in {
      controller.index(1L).apply(fakePostRequest())
      controller.checkedRoles.diff(List(Role.Facilitator, Role.Coordinator)).isEmpty must_== true
    }
  }

  "Method 'invoice'" should {
    "have Coordinator access rights for 'event' object" in {
      controller.invoice(1L).apply(fakeGetRequest())
      controller.checkedRoles must_== List(Role.Coordinator)
      controller.checkedObjectId must_== Some(1L)
    }
  }

  "Method 'sendRequest'" should {
    "have Facilitator access rights for 'event' object" in {
      controller.sendRequest(1L).apply(fakePostRequest())
      controller.checkedRoles.diff(List(Role.Facilitator, Role.Coordinator)).isEmpty must_== true
      controller.checkedObjectId must_== Some(1L)
    }
  }

  "Method 'update'" should {
    "have Facilitator access rights for 'event' object" in {
      controller.update(1L).apply(fakePostRequest())
      controller.checkedRoles.diff(List(Role.Facilitator, Role.Coordinator)).isEmpty must_== true
      controller.checkedObjectId must_== Some(1L)
    }
  }
}
