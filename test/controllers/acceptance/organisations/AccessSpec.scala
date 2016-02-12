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

package controllers.acceptance.organisations

import _root_.integration.PlayAppSpec
import controllers.Organisations
import models.UserRole.{DynamicRole, Role}
import stubs.{AccessCheckSecurity, FakeRuntimeEnvironment}

class AccessSpec extends PlayAppSpec {
  class TestOrganisations() extends Organisations(FakeRuntimeEnvironment)
    with AccessCheckSecurity

  val controller = new TestOrganisations

  "Method 'activate'" should {
    "have Admin access rights" in {
      controller.activation(1L).apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }

  "Method 'add'" should {
    "have Admin access rights" in {
      controller.add.apply(fakeGetRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }

  "Method 'cancel'" should {
    "have 'edit' access rights for 'organisation' object" in {
      controller.cancel(1L).apply(fakePostRequest())
      controller.checkedDynamicRole must_== Some(OrgMember)
      controller.checkedObjectId must_== Some(1L)
    }
  }

  "Method 'create'" should {
    "have Admin access rights" in {
      controller.create.apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }

  "Method 'createOrganizer'" should {
    "have Facilitator, Coordinator access rights for events" in {
      controller.createOrganizer.apply(fakePostRequest())
      controller.checkedRoles.diff(List(Role.Facilitator, Role.Coordinator)).isEmpty must_== true
    }
  }

  "Method 'delete'" should {
    "have Admin access rights" in {
      controller.delete(1L).apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }

  "Method 'deleteLogo'" should {
    "have 'edit' access rights for 'organisation' object" in {
      controller.deleteLogo(1L).apply(fakePostRequest())
      controller.checkedDynamicRole must_== Some(OrgMember)
      controller.checkedObjectId must_== Some(1L)
    }
  }

  "Method 'details'" should {
    "have Viewer access rights" in {
      controller.details(1L).apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Viewer)
    }
  }

  "Method 'edit'" should {
    "have 'edit' access rights for 'organisation' object" in {
      controller.edit(1L).apply(fakePostRequest())
      controller.checkedDynamicRole must_== Some(OrgMember)
      controller.checkedObjectId must_== Some(1L)
    }
  }

  "Method 'index'" should {
    "have Admin access rights" in {
      controller.index.apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Admin)
    }
  }

  "Method 'name'" should {
    "have Viewer access rights" in {
      controller.name(1L).apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Viewer)
    }
  }

  "Method 'search'" should {
    "have Viewer access rights" in {
      controller.search(Some("test")).apply(fakePostRequest())
      controller.checkedRole must_== Some(Role.Viewer)
    }
  }

  "Method 'update'" should {
    "have 'edit' access rights for 'organisation' object" in {
      controller.update(1L).apply(fakePostRequest())
      controller.checkedDynamicRole must_== Some(OrgMember)
      controller.checkedObjectId must_== Some(1L)
    }
  }

  "Method 'uploadLogo'" should {
    "have 'edit' access rights for 'organisation' object" in {
      controller.uploadLogo(1L).apply(fakePostRequest())
      controller.checkedDynamicRole must_== Some(OrgMember)
      controller.checkedObjectId must_== Some(1L)
    }
  }
}