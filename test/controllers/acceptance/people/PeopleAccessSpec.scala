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
import models.UserRole.{DynamicRole, Role}
import stubs.{AccessCheckSecurity, FakeRuntimeEnvironment}

class PeopleAccessSpec extends PlayAppSpec {
  class TestPeople() extends People(FakeRuntimeEnvironment)
    with AccessCheckSecurity

  val controller = new TestPeople()

  "Method 'activation'" should {
    "have Admin access rights" in {
      controller.activation(1L).apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }
  "Method 'add'" should {
    "have Admin access rights" in {
      controller.add().apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }
  "Method 'addRelationship'" should {
    "have Admin access rights" in {
      controller.addRelationship().apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }
  "Method 'create'" should {
    "have Admin access rights" in {
      controller.create().apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }
  "Method 'delete'" should {
    "have Admin access rights" in {
      controller.delete(1L).apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }
  "Method 'deleteRelationship'" should {
    "have profile access rights" in {
      controller.deleteRelationship("test", 3L, 1L).apply(fakeGetRequest())
      controller.checkedRole must_== None
      controller.checkedObjectId must_== Some(3L)
    }
  }
  "Method 'details'" should {
    "have Viewer access rights" in {
      controller.details(1L).apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Viewer)
    }
  }
  "Method 'edit'" should {
    "have ProfileEditor access rights" in {
      controller.edit(3L).apply(fakeGetRequest())
      controller.checkedRole must_== None
      controller.checkedDynamicRole must_== Some(DynamicRole.ProfileEditor)
      controller.checkedObjectId must_== Some(3L)
    }
  }
  "Method 'index'" should {
    "have Admin access rights" in {
      controller.index().apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }
  "Method 'update'" should {
    "have ProfileEditor access rights" in {
      controller.update(3L).apply(fakeGetRequest())
      controller.checkedRole must_== None
      controller.checkedDynamicRole must_== Some(DynamicRole.ProfileEditor)
      controller.checkedObjectId must_== Some(3L)
    }
  }

  "Method 'cancel'" should {
    "have profile access rights" in {
      controller.cancel(5L).apply(fakeGetRequest())
      controller.checkedDynamicRole must_== None
      controller.checkedRole must_== None
      controller.checkedObjectId must_== Some(5L)
    }
  }

  "Method 'renderTabs'" should {
    "have Viewer access rights" in {
      controller.renderTabs(1L, "testlab").apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Viewer)
    }
  }
}
