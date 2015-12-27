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

package controllers.unit

import controllers.People
import helpers._
import models.{ SocialProfile, Person }
import org.specs2.mutable._
import play.api.data.FormError
import stubs.FakeRuntimeEnvironment

class PeopleSpec extends Specification {

  class TestPeople extends People(FakeRuntimeEnvironment) {

    def callCompareSocialProfiles(left: SocialProfile,
      right: SocialProfile): List[FormError] = {
      compareSocialProfiles(left, right)
    }

    def callComposeSocialNotification(existing: SocialProfile,
      updated: SocialProfile): Option[String] = {
      connectMeMessage(existing, updated)
    }

    def callComposeNotification(msgType: String, old: Option[String],
      updated: Option[String]): Option[String] =
      fieldMessage(msgType, old, updated)
  }

  val controller = new TestPeople
  val left = SocialProfile()
  val right = SocialProfile()
  val person = PersonHelper.one()
  val updatedPerson = PersonHelper.one()

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

  "Method 'composeSocialNotification'" should {
    "produce no notification if no social account has changed" in {
      controller.callComposeSocialNotification(left, right) must_== None
    }
    "add Twitter info to notification if Twitter has been added" in {
      val updated = left.copy(twitterHandle = Some("skotlov"))
      val msg = controller.callComposeSocialNotification(left, updated)
      msg map { x ⇒
        x must contain("Let's show them some love by linking on  <http://twitter.com/skotlov|Twitter>")
      } getOrElse ko
    }
    "add Facebook info to notification if Facebook has been added" in {
      val updated = left.copy(facebookUrl = Some("https://facebook.com/skotlov"))
      val msg = controller.callComposeSocialNotification(left, updated)
      msg map { x ⇒
        x must contain("Let's show them some love by linking on  <https://facebook.com/skotlov|Facebook>")
      } getOrElse ko
    }
    "add Google info to notification if Google has been added" in {
      val updated = left.copy(googlePlusUrl = Some("https://plus.google.com/+SergeyKotlov"))
      val msg = controller.callComposeSocialNotification(left, updated)
      msg map { x ⇒
        x must contain("Let's show them some love by linking on  <https://plus.google.com/+SergeyKotlov|G+>")
      } getOrElse ko
    }
    "add LinkedIn info to notification if LinkedIn has been added" in {
      val updated = left.copy(linkedInUrl = Some("https://www.linkedin.com/in/skotlov"))
      val msg = controller.callComposeSocialNotification(left, updated)
      msg map { x ⇒
        x must contain("Let's show them some love by linking on  <https://www.linkedin.com/in/skotlov|LinkedIn>")
      } getOrElse ko
    }

    "add Twitter and Facebook info to notification if both have been added" in {
      val updated = left.copy(twitterHandle = Some("skotlov"), facebookUrl = Some("https://facebook.com/skotlov"))
      val msg = controller.callComposeSocialNotification(left, updated)
      msg map { x ⇒
        x must contain("Let's show them some love by linking on  <http://twitter.com/skotlov|Twitter>")
        x must contain(", <https://facebook.com/skotlov|Facebook>")
      } getOrElse ko
    }
  }
  "Method 'composeNotification'" should {
    "return no notification if updated value is empty" in {
      controller.callComposeNotification("twitter", Some("test"), None) must_== None
    }
    "return no notification if updated value is equal to old value" in {
      controller.callComposeNotification("twitter", Some("test"), Some("test")) must_== None
    }
    "return a notification msg if updated value is not equal to old value and not empty" in {
      controller.callComposeNotification("twitter", None, Some("test")).isEmpty must_== false
    }
  }
}
