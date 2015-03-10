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

package models.unit.controllers

import controllers.People
import models.SocialProfile
import org.specs2.mutable._
import play.api.data.FormError

class PeopleSpec extends Specification {

  class TestPeople extends People {

    def callCompareSocialProfiles(left: SocialProfile,
      right: SocialProfile): List[FormError] = {
      compareSocialProfiles(left, right)
    }
  }

  val controller = new TestPeople
  val left = SocialProfile(email = "left@profile.com")
  val right = SocialProfile(email = "right@profile.com")

  "Method 'compareSocialProfiles'" should {
    "return an error if twitterHandles are equal" in {
      val errors = controller.callCompareSocialProfiles(
        left.copy(twitterHandle = Some("ok")),
        right.copy(twitterHandle = Some("ok")))
      errors.exists(_.key == "profile.twitterHandle") must_== true
    }
    "return an error if facebookUrls are equal" in {
      val errors = controller.callCompareSocialProfiles(
        left.copy(facebookUrl = Some("ok")),
        right.copy(facebookUrl = Some("ok")))
      errors.exists(_.key == "profile.facebookUrl") must_== true
    }
    "return an error if linkedInUrls are equal" in {
      val errors = controller.callCompareSocialProfiles(
        left.copy(linkedInUrl = Some("ok")),
        right.copy(linkedInUrl = Some("ok")))
      errors.exists(_.key == "profile.linkedInUrl") must_== true
    }
    "return an error if googlePlusUrls are equal" in {
      val errors = controller.callCompareSocialProfiles(
        left.copy(googlePlusUrl = Some("ok")),
        right.copy(googlePlusUrl = Some("ok")))
      errors.exists(_.key == "profile.googlePlusUrl") must_== true
    }
    "return no errors if all social profiles are empty" in {
      val errors = controller.callCompareSocialProfiles(left, right)
      errors.length must_== 0
    }
  }
}
