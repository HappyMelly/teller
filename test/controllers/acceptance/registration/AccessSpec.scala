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

package controllers.acceptance.registration

import _root_.integration.PlayAppSpec
import controllers.Registration
import models.UserRole.{ DynamicRole, Role }
import stubs.{ AccessCheckSecurity, FakeRuntimeEnvironment }

class AccessSpec extends PlayAppSpec {
  class TestRegistration() extends Registration(FakeRuntimeEnvironment)
    with AccessCheckSecurity

  val controller = new TestRegistration

  "Method 'congratulations'" should {
    "have no access rights" in {
      controller.congratulations().apply(fakePostRequest())
      controller.checkedRole must_== None
      controller.checkedDynamicObject must_== None
      controller.checkedDynamicLevel must_== None
    }
  }

  "Method 'welcome'" should {
    "have no access rights" in {
      controller.welcome().apply(fakePostRequest())
      controller.checkedRole must_== None
      controller.checkedDynamicObject must_== None
      controller.checkedDynamicLevel must_== None
    }
  }

  "Method 'step1'" should {
    "have no access rights" in {
      controller.step1().apply(fakePostRequest())
      controller.checkedRole must_== None
      controller.checkedDynamicObject must_== None
      controller.checkedDynamicLevel must_== None
    }
  }

  "Method 'step2'" should {
    "have Unregistered access rights" in {
      controller.step2().apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Unregistered)
    }
  }

  "Method 'step3'" should {
    "have Unregistered access rights" in {
      controller.step3().apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Unregistered)
    }
  }

  "Method 'savePerson'" should {
    "have Unregistered access rights" in {
      controller.savePerson().apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Unregistered)
    }
  }

  "Method 'saveOrg'" should {
    "have Unregistered access rights" in {
      controller.saveOrg().apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Unregistered)
    }
  }

  "Method 'payment'" should {
    "have Unregistered access rights" in {
      controller.payment().apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Unregistered)
    }
  }

  "Method 'charge'" should {
    "have Unregistered access rights" in {
      controller.charge().apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Unregistered)
    }
  }
}