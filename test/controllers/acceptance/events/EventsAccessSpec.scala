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
import controllers.Events
import models.UserRole.{ DynamicRole, Role }
import stubs.{ FakeRuntimeEnvironment, AccessCheckSecurity }

class EventsAccessSpec extends PlayAppSpec {
  class TestEvents() extends Events(FakeRuntimeEnvironment) with AccessCheckSecurity

  val controller = new TestEvents()

  "Method 'add'" should {
    "have 'add' access rights for 'event' object" in {
      controller.add.apply(fakeGetRequest())
      controller.checkedDynamicObject must_== Some("event")
      controller.checkedDynamicLevel must_== Some("add")
    }
  }

  "Method 'cancel'" should {
    "have Facilitator access rights for 'event' object" in {
      controller.cancel(1L).apply(fakePostRequest())
      controller.checkedDynamicObject must_== Some("event")
      controller.checkedDynamicLevel must_== Some(DynamicRole.Facilitator)
    }
  }

  "Method 'confirm'" should {
    "have Facilitator access rights for 'event' object" in {
      controller.confirm(1L).apply(fakeGetRequest())
      controller.checkedDynamicObject must_== Some("event")
      controller.checkedDynamicLevel must_== Some(DynamicRole.Facilitator)
    }
  }

  "Method 'create'" should {
    "have 'add' access rights for 'event' object" in {
      controller.create.apply(fakePostRequest())
      controller.checkedDynamicObject must_== Some("event")
      controller.checkedDynamicLevel must_== Some("add")
    }
  }

  "Method 'details'" should {
    "have Viewer access rights" in {
      controller.details(1L).apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Viewer)
    }
  }

  "Method 'duplicate'" should {
    "have Facilitator access rights for 'event' object" in {
      controller.duplicate(1L).apply(fakeGetRequest())
      controller.checkedDynamicObject must_== Some("event")
      controller.checkedDynamicLevel must_== Some(DynamicRole.Facilitator)
    }
  }

  "Method 'edit'" should {
    "have Facilitator access rights for 'event' object" in {
      controller.edit(1L).apply(fakeGetRequest())
      controller.checkedDynamicObject must_== Some("event")
      controller.checkedDynamicLevel must_== Some(DynamicRole.Facilitator)
    }
  }

  "Method 'index'" should {
    "have Viewer access rights" in {
      controller.index(1L).apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Viewer)
    }
  }

  "Method 'invoice'" should {
    "have Coordinator access rights for 'event' object" in {
      controller.invoice(1L).apply(fakeGetRequest())
      controller.checkedDynamicObject must_== Some("event")
      controller.checkedDynamicLevel must_== Some(DynamicRole.Coordinator)
    }
  }

  "Method 'sendRequest'" should {
    "have Facilitator access rights for 'event' object" in {
      controller.sendRequest(1L).apply(fakePostRequest())
      controller.checkedDynamicObject must_== Some("event")
      controller.checkedDynamicLevel must_== Some(DynamicRole.Facilitator)
    }
  }

  "Method 'update'" should {
    "have Facilitator access rights for 'event' object" in {
      controller.update(1L).apply(fakePostRequest())
      controller.checkedDynamicObject must_== Some("event")
      controller.checkedDynamicLevel must_== Some(DynamicRole.Facilitator)
    }
  }
}
