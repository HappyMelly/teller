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

package controllers.acceptance.people

import _root_.integration.PlayAppSpec
import controllers.People
import models.UserRole.Role
import stubs.{ AccessCheckSecurity, FakeRuntimeEnvironment }

class PeopleAccessSpec extends PlayAppSpec {
  class TestPeople() extends People(FakeRuntimeEnvironment)
    with AccessCheckSecurity

  val controller = new TestPeople()

  "Method 'cancel'" should {
    "have 'edit' access rights for 'person' object" in {
      controller.cancel(1L).apply(fakeGetRequest())
      controller.checkedDynamicObject must_== Some("person")
      controller.checkedDynamicLevel must_== Some("edit")
    }
  }

  "Method 'details'" should {
    "have Viewer access rights" in {
      controller.details(1L).apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Viewer)
    }
  }

  "Method 'renderTabs'" should {
    "have Viewer access rights" in {
      controller.renderTabs(1L, "testlab").apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Viewer)
    }
  }
}
