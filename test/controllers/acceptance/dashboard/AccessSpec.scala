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

package controllers.acceptance.dashboard

import _root_.integration.PlayAppSpec
import controllers.Dashboard
import models.UserRole.Role
import stubs.{ AccessCheckSecurity, FakeRuntimeEnvironment }

class AccessSpec extends PlayAppSpec {
  class TestDashboard() extends Dashboard(FakeRuntimeEnvironment)
    with AccessCheckSecurity

  val controller = new TestDashboard

  "Method 'about'" should {
    "have Editor access rights" in {
      controller.about.apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Editor)
    }
  }

  "Method 'api'" should {
    "have Editor access rights" in {
      controller.api.apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Editor)
    }
  }

  "Method 'apiv2'" should {
    "have Editor access rights" in {
      controller.apiv2.apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Viewer)
    }
  }

  "Method 'index'" should {
    "have Unregistered access rights" in {
      controller.index.apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Unregistered)
    }
  }

  "Method 'profile'" should {
    "have Viewer access rights" in {
      controller.profile.apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Viewer)
    }
  }
}