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

import models.{ SocialIdentity$ }
import models.service.IdentityService$
import org.scalamock.specs2.IsolatedMockFactory
import org.specs2.mutable.Specification
import securesocial.core._
import securesocial.core.providers._
import securesocial.core.services._
import stubs.FakeServices

class LoginIdentityServiceSpec extends Specification with IsolatedMockFactory {

  override def is = s2"""

  Given a user is logged in
    when 'save' method is called
      his identity should be retrieved                                      e1

  Given a user is logged in
    and his identity couldn't be found
      when 'save' method is called
        exception should be thrown                                          e2
  """

  class TestLoginIdentityService extends LoginIdentityService with FakeServices
  val service = new TestLoginIdentityService
  val userIdentityService = mock[IdentityService]
  service.identityService_=(userIdentityService)

  //  def e1 = {
  //    val fb = FacebookProvider.Facebook
  //    val profile = BasicProfile(fb, "123", Some("Tester"),
  //      Some("First"), Some(""), Some("test@test.ru"), None,
  //      AuthenticationMethod.OAuth2)
  //    val identity = UserIdentity(Some(1L), profile, "api", None, Some("fb"), None, None)
  //    (userIdentityService.findByUserId(_, _)) expects (fb, "123") returning Some(identity)
  //    val result = service.save(profile, SaveMode.LoggedIn)
  //    result.map(_.profile.email.exists(_ == "test@test.ru")) must beTrue.await
  //    result.map(_.facebookUrl.exists(_ == "fb")) must beTrue.await
  //  }
  //
  //  def e2 = {
  //    val fb = FacebookProvider.Facebook
  //    (userIdentityService.findByUserId(_, _)) expects (fb, "123") returning None
  //    val profile = BasicProfile(fb, "123", Some("Tester"),
  //      Some("First"), Some(""), Some("test@test.ru"), None,
  //      AuthenticationMethod.OAuth2)
  //    service.save(profile, SaveMode.LoggedIn) must throwA[AuthenticationException]
  //  }
}