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
import controllers.{ OrgData, UserData, Registration }
import play.api.mvc.{ Cookie, Result }
import play.api.test.FakeRequest
import stubs.{ FakeRuntimeEnvironment, FakeSocialIdentity, FakeSecurity }
import play.api.cache.Cache
import play.api.Play.current

import scala.concurrent.Future

class RegistrationSpec extends PlayAppSpec {

  class TestRegistration extends Registration(FakeRuntimeEnvironment)
      with FakeSecurity {
    def callPersonCacheId(userId: String): String = personCacheId(userId)
  }

  override def is = s2"""

  Step 1 should
    contain all social login buttons               $e2

  Step 2 should
    redirect Viewers to Main page                  $e4
    fill in fields 'First name', 'Last name' and 'Email' if they are available $e6

  'Save person' should
    redirect Viewers to Main page                     $e8
    generate errors if not all fields are filled      $e10
    redirect to Step3 if a new member is an organisation $e12

  Step 3 should
    redirect Viewers to Main page                  $e14

  'Save org' should
    redirect Viewers to Main page                     $e17
    generate errors if not all fields are filled      $e18

  'Payment' should
    redirect Viewers to Main page                      $e19
    redirect users to Step 2 if user data is not found $e20
    render payment form for a person if a new member is a person $e21
    render payment form for an org if a new member is an organization $e22

  'Charge' should
    redirect Viewers to Main page                      $e23
    redirect users to Step 2 if user data is not found $e24
  """

  val controller = new TestRegistration()

  def e2 = {
    val result = controller.step1().apply(FakeRequest())
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Log in with Twitter")
    contentAsString(result) must contain("Log in with Facebook")
    contentAsString(result) must contain("Log in with Google")
    contentAsString(result) must contain("Log in with Linkedin")
  }

  def e4 = {
    controller.identity_=(FakeSocialIdentity.viewer)
    val result = controller.step2().apply(fakeGetRequest())
    headers(result).get("Location").get must_== "/"
  }

  def e6 = {
    controller.identity_=(FakeSocialIdentity.unregistered)
    val result = controller.step2().apply(fakeGetRequest())
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Sergey")
    contentAsString(result) must contain("Kotlov")
    contentAsString(result) must contain("Invalid e-mail address")
  }

  def e8 = {
    controller.identity_=(FakeSocialIdentity.viewer)
    val result = controller.savePerson().apply(fakePostRequest())
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must_== "/"
  }

  def e10 = {
    controller.identity_=(FakeSocialIdentity.unregistered)
    val req = fakePostRequest().
      withFormUrlEncodedBody(("firstName", ""),
        ("lastName", ""), ("email", "ttt.ru"), ("country", "WWW"))
    val result = controller.savePerson().apply(req)
    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("Required value missing")
    contentAsString(result) must contain("Invalid e-mail address")
    contentAsString(result) must contain("Please choose a country")
  }

  def e12 = {
    val identity = FakeSocialIdentity.unregistered
    val cookie = Cookie(controller.REGISTRATION_COOKIE, "org")

    val req = fakePostRequest().
      withFormUrlEncodedBody(("firstName", "First"),
        ("lastName", "Tester"), ("email", "tt@ttt.ru"), ("country", "RU")).
        withCookies(cookie)
    val result = controller.savePerson().apply(req)
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must contain("/registration/step3")
  }

  def e14 = {
    controller.identity_=(FakeSocialIdentity.viewer)
    val result = controller.step3().apply(fakeGetRequest())
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must_== "/"
  }

  def e17 = {
    controller.identity_=(FakeSocialIdentity.viewer)
    val result = controller.saveOrg().apply(fakePostRequest())
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must_== "/"
  }

  def e18 = {
    controller.identity_=(FakeSocialIdentity.unregistered)
    val req = fakePostRequest().
      withFormUrlEncodedBody(("name", ""), ("country", "WWW"))
    val result = controller.saveOrg().apply(req)
    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("Required value missing")
    contentAsString(result) must contain("Please choose a country")
  }

  def e19 = {
    controller.identity_=(FakeSocialIdentity.viewer)
    val result = controller.payment().apply(fakeGetRequest())
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must_== "/"
  }

  def e20 = {
    val identity = FakeSocialIdentity.unregistered
    controller.identity_=(identity)
    val cacheId = controller.callPersonCacheId(identity._1)
    Cache.remove(cacheId)
    val result = controller.payment().apply(fakeGetRequest())
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must_== "/registration/step2"
  }

  def e21 = {
    val identity = FakeSocialIdentity.unregistered
    controller.identity_=(identity)
    val cacheId = controller.callPersonCacheId(identity._1)
    val userData = UserData("First", "Tester", "t@ttt.ru", "RU")
    Cache.set(cacheId, userData, 900)
    val result = controller.payment().apply(fakeGetRequest())
    status(result) must equalTo(OK)
    contentAsString(result) must contain("You are from Russia")
  }

  def e22 = {
    val identity = FakeSocialIdentity.unregistered
    controller.identity_=(identity)
    val cacheId = controller.callPersonCacheId(identity._1)
    val userData = UserData("First", "Tester", "t@ttt.ru", "RU", true, OrgData("One", "DE"))
    Cache.set(cacheId, userData, 900)
    val result = controller.payment().apply(fakeGetRequest())
    status(result) must equalTo(OK)
    contentAsString(result) must contain("One is from Germany")
  }

  def e23 = {
    controller.identity_=(FakeSocialIdentity.viewer)
    val result = controller.charge().apply(fakePostRequest())
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must_== "/"
  }

  def e24 = {
    val identity = FakeSocialIdentity.unregistered
    controller.identity_=(identity)
    val cacheId = controller.callPersonCacheId(identity._1)
    Cache.remove(cacheId)
    val result = controller.charge().apply(fakePostRequest())
    status(result) must equalTo(OK)
    contentAsString(result) must contain("/registration/step2")
  }
}
