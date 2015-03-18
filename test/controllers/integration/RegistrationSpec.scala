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

package controllers.integration

import controllers.Registration
import integration.PlayAppSpec
import play.api.mvc.Cookie
import play.api.test.FakeRequest

class RegistrationSpec extends PlayAppSpec {

  class TestRegistration extends Registration

  "On step1 the system" should {
    "set cookie 'registration'=org if org parameter is true" in {
      val controller = new TestRegistration
      val res = controller.step1(org = true).apply(FakeRequest())
      cookies(res).get("registration") map { _.value must_== "org" } getOrElse ko
    }
    "discard cookie 'registration' if org parameter is false" in {
      val controller = new TestRegistration
      val cookie = Cookie(Registration.REGISTRATION_COOKIE, "org")
      val res = controller.step1(org = false).apply(FakeRequest().withCookies(cookie))
      cookies(res).get("registration") must_== None
    }
  }
}
