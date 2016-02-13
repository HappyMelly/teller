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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package services

import models.{ SocialIdentity }
import models.repository.IdentityRepository
import org.scalamock.specs2.IsolatedMockFactory
import org.specs2.mutable.Specification
import securesocial.core._
import securesocial.core.providers._
import securesocial.core.services._
import stubs.FakeRepositories

class LoginIdentityServiceSpec extends Specification with IsolatedMockFactory {

  class TestLoginIdentityService extends LoginIdentityService with FakeRepositories {
    def callUserNames(identity: SocialIdentity) = userNames(identity)
  }
  val service = new TestLoginIdentityService
  val userIdentityService = mock[IdentityRepository]
  service.identityService_=(userIdentityService)

  "Method 'userNames'" should {
    "return first and last names if they are set in identity object" in {
      val identity = ("123", "twitter")
      val user = new SocialIdentity(None, BasicProfile(identity._1, identity._2,
        Some("First"), Some("Tester"), Some(""),
        email = None, avatarUrl = None, AuthenticationMethod.OAuth2,
        oAuth1Info = None, oAuth2Info = None, passwordInfo = None),
        apiToken = "test", profileUrl = None)

      val names = service.callUserNames(user)
      names._1 must_== "First"
      names._2 must_== "Tester"
    }
    "return first and last names if full name set in identity object" in {
      val identity = ("123", "twitter")
      val user = new SocialIdentity(None, BasicProfile(identity._1, identity._2,
        Some(""), Some(""), Some("First Tester Andre"),
        email = None, avatarUrl = None, AuthenticationMethod.OAuth2,
        oAuth1Info = None, oAuth2Info = None, passwordInfo = None),
        apiToken = "test", profileUrl = None)
      service.callUserNames(user) must_== ("First", "Tester Andre")
      service.callUserNames(
        user.copy(profile = user.profile.copy(fullName = Some("FirstTester")))) must_== ("FirstTester", "")
      service.callUserNames(
        user.copy(profile = user.profile.copy(fullName = Some("")))) must_== ("", "")
    }
  }
}