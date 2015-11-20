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

package controllers.acceptance

import _root_.integration.PlayAppSpec
import controllers.ProfilePhotos
import stubs.{AccessCheckSecurity, FakeRuntimeEnvironment}

class ProfilePhotosAccessSpec extends PlayAppSpec {
  class TestProfilePhotos() extends ProfilePhotos(FakeRuntimeEnvironment)
    with AccessCheckSecurity

  val controller = new TestProfilePhotos()

  "Method 'choose'" should {
    "have profile access rights" in {
      controller.choose(2L).apply(fakeGetRequest())
      controller.checkedDynamicRole must_== None
      controller.checkedObjectId must_== Some(2L)
    }
  }

  "Method 'delete'" should {
    "have profile access rights" in {
      controller.delete(3L).apply(fakeGetRequest())
      controller.checkedDynamicRole must_== None
      controller.checkedObjectId must_== Some(3L)
    }
  }

  "Method 'update'" should {
    "have profile access rights" in {
      controller.update(2L).apply(fakePostRequest())
      controller.checkedDynamicRole must_== None
      controller.checkedObjectId must_== Some(2L)
    }
  }

  "Method 'upload'" should {
    "have profile access rights" in {
      controller.upload(2L).apply(fakePostRequest())
      controller.checkedDynamicRole must_== None
      controller.checkedObjectId must_== Some(2L)
    }
  }
}
