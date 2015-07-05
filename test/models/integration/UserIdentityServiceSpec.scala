/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing Happy Melly One, Handelsplein 37, Rotterdam,
 * The Netherlands, 3071 PR
 */
package models.integration

import _root_.integration.PlayAppSpec
import helpers.PersonHelper
import models.{ UserIdentity, UserAccount }
import models.service.{ UserAccountService, UserIdentityService }
import securesocial.core.{ BasicProfile, AuthenticationMethod }

class UserIdentityServiceSpec extends PlayAppSpec {

  val userId = "1"
  val providerId = "twitter"
  val service = new UserIdentityService
  val accountService = new UserAccountService

  "Method findActiveUser" should {
    "return None if account data are not available" in {
      truncateTables()
      service.insert(user(userId, providerId, twitter = Some("tester")))

      val result = UserIdentityService.get.findActiveUser(userId, providerId)
      result must_== None
    }
    "return None if person data are not available" in {
      truncateTables()
      val account = new UserAccount(None, 1L, "viewer", Some("tester"),
        None, None, None)
      service.insert(user(userId, providerId, twitter = Some("tester")))
      accountService.insert(account)

      val result = UserIdentityService.get.findActiveUser(userId, providerId)
      result must_== None
    }
    "return identity with Twitter if all data are available" in {
      truncateTables()
      val account = new UserAccount(None, 1L, "viewer", Some("tester"),
        None, None, None)
      service.insert(user(userId, providerId, twitter = Some("tester")))
      accountService.insert(account)
      PersonHelper.one().insert

      val result = UserIdentityService.get.findActiveUser(userId, providerId)
      result map { i ⇒
        i.identity.profile.firstName must_== Some("First")
        i.identity.profile.lastName must_== Some("Tester")
        i.identity.profile.email must_!= None
        i.identity.profile.email.get must_== "t@t.com"
        i.account.twitterHandle must_== Some("tester")
        i.person.fullName must_== "First Tester"
      } getOrElse ko
    }

    "return identity with Facebook if all data are available" in {
      truncateTables()
      val providerId = "facebook"
      val account = new UserAccount(None, 1L, "viewer", None,
        Some("tester"), None, None)
      service.insert(user(userId, providerId, facebook = Some("tester")))
      accountService.insert(account)
      PersonHelper.one().insert

      val result = UserIdentityService.get.findActiveUser(userId, providerId)
      result map { i ⇒
        i.identity.profile.firstName must_== Some("First")
        i.identity.profile.lastName must_== Some("Tester")
        i.identity.profile.email must_!= None
        i.identity.profile.email.get must_== "t@t.com"
        i.account.facebookUrl must_== Some("tester")
        i.person.fullName must_== "First Tester"
      } getOrElse ko
    }

    "return identity with Google if all data are available" in {
      truncateTables()
      val providerId = "google"
      val url = "https://plus.google.com/tester"
      val account = new UserAccount(None, 1L, "viewer", None,
        None, None, Some(url))
      service.insert(user(userId, providerId, google = Some(url)))
      accountService.insert(account)
      PersonHelper.one().insert

      val result = UserIdentityService.get.findActiveUser(userId, providerId)
      result map { i ⇒
        i.identity.profile.firstName must_== Some("First")
        i.identity.profile.lastName must_== Some("Tester")
        i.identity.profile.email must_!= None
        i.identity.profile.email.get must_== "t@t.com"
        i.account.googlePlusUrl must_== Some(url)
        i.person.fullName must_== "First Tester"
      } getOrElse ko
    }

    "return identity with LinkedIn if all data are available" in {
      truncateTables()
      val providerId = "linkedin"
      val account = new UserAccount(None, 1L, "viewer", None,
        None, Some("tester"), None)
      service.insert(user(userId, providerId, linkedin = Some("tester")))
      accountService.insert(account)
      PersonHelper.one().insert

      val result = UserIdentityService.get.findActiveUser(userId, providerId)
      result map { i ⇒
        i.identity.profile.firstName must_== Some("First")
        i.identity.profile.lastName must_== Some("Tester")
        i.identity.profile.email must_!= None
        i.identity.profile.email.get must_== "t@t.com"
        i.account.linkedInUrl must_== Some("tester")
        i.person.fullName must_== "First Tester"
      } getOrElse ko
    }
  }

  private def user(userId: String,
    providerId: String,
    twitter: Option[String] = None,
    facebook: Option[String] = None,
    google: Option[String] = None,
    linkedin: Option[String] = None): UserIdentity = {
    new UserIdentity(None, BasicProfile(providerId, userId, Some("First"),
      Some("Tester"), Some("First Tester"), Some("t@t.com"), None,
      AuthenticationMethod.OAuth2, None, None, None), "token123",
      twitter, facebook, google, linkedin)
  }
}
