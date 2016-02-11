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
import models.service.{UserAccountService, IdentityService}
import models.{UserAccount, SocialIdentity}
import securesocial.core.{AuthenticationMethod, BasicProfile}

class UserIdentityServiceSpec extends PlayAppSpec {

  val userId = "1"
  val providerId = "twitter"
  val service = new IdentityService
  val accountService = new UserAccountService

  "Method findActiveUser" should {
    "return None if account data are not available" in {
      truncateTables()
      service.insert(user(userId, providerId, "tester"))

      val result = IdentityService.get.findActiveUser(userId, providerId)
      result must_== None
    }
    "return None if person data are not available" in {
      truncateTables()
      val account = new UserAccount(None, 1L, false, Some("tester"), None, None, None)
      service.insert(user(userId, providerId, "tester"))
      services.accountService.insert(account)

      val result = IdentityService.get.findActiveUser(userId, providerId)
      result must_== None
    }
    "return identity with Twitter if all data are available" in {
      truncateTables()
      val account = new UserAccount(None, 1L, false, Some("tester"), None, None, None)
      service.insert(user(userId, providerId, "tester"))
      services.accountService.insert(account)
      PersonHelper.one().insert

      val result = IdentityService.get.findActiveUser(userId, providerId)
      result map { i ⇒
        i.account.twitterHandle must_== Some("tester")
        i.person.fullName must_== "First Tester"
      } getOrElse ko
    }

    "return identity with Facebook if all data are available" in {
      truncateTables()
      val providerId = "facebook"
      val account = new UserAccount(None, 1L, false, None, Some("tester"), None, None)
      service.insert(user(userId, providerId, "tester"))
      services.accountService.insert(account)
      PersonHelper.one().insert

      val result = IdentityService.get.findActiveUser(userId, providerId)
      result map { i ⇒
        i.account.facebookUrl must_== Some("tester")
        i.person.fullName must_== "First Tester"
      } getOrElse ko
    }

    "return identity with Google if all data are available" in {
      truncateTables()
      val providerId = "google"
      val url = "https://plus.google.com/tester"
      val account = new UserAccount(None, 1L, false, None, None, None, Some(url))
      service.insert(user(userId, providerId, url))
      services.accountService.insert(account)
      PersonHelper.one().insert

      val result = IdentityService.get.findActiveUser(userId, providerId)
      result map { i ⇒
        i.account.googlePlusUrl must_== Some(url)
        i.person.fullName must_== "First Tester"
      } getOrElse ko
    }

    "return identity with LinkedIn if all data are available" in {
      truncateTables()
      val providerId = "linkedin"
      val account = new UserAccount(None, 1L, false, None, None, Some("tester"), None)
      service.insert(user(userId, providerId, "tester"))
      services.accountService.insert(account)
      PersonHelper.one().insert

      val result = IdentityService.get.findActiveUser(userId, providerId)
      result map { i ⇒
        i.account.linkedInUrl must_== Some("tester")
        i.person.fullName must_== "First Tester"
      } getOrElse ko
    }
  }

  private def user(userId: String,
    providerId: String,
    profileUrl: String): SocialIdentity = {
    new SocialIdentity(None, BasicProfile(providerId, userId, Some("First"),
      Some("Tester"), Some("First Tester"), Some("t@t.com"), None,
      AuthenticationMethod.OAuth2, None, None, None), "token123", Some(profileUrl))
  }
}
