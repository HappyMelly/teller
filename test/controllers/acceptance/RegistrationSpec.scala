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
import controllers.{ OrgData, User, Registration }
import play.api.mvc.{ Cookie, SimpleResult }
import play.api.test.FakeRequest
import securesocial.core.IdentityId
import stubs.FakeUserIdentity
import play.api.cache.Cache
import play.api.Play.current

import scala.concurrent.Future

class RegistrationSpec extends PlayAppSpec {

  class TestRegistration extends Registration {
    def callPersonCacheId(id: IdentityId): String = personCacheId(id)
  }

  override def is = s2"""

  Step 1 should
    be visible to anyone                           $e1
    contain all social login buttons               $e2

  Step 2 should
    be visible to an unregistered user             $e3
    redirect Viewers to Main page                  $e4
    not be visible to unauthorized user            $e5
    fill in fields 'First name', 'Last name' and 'Email' if they are available $e6

  'Save person' should
    not be accessible to an unauthorized user         $e7
    redirect Viewers to Main page                     $e8
    generate errors if not all fields are filled      $e10
    redirect to Step3 if a new member is an organisation $e12

  Step 3 should
    be visible to an unregistered user             $e13
    redirect Viewers to Main page                  $e14
    not be visible to unauthorized user            $e15

  'Save org' should
    not be accessible to an unauthorized user         $e16
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

  def e1 = {
    val result: Future[SimpleResult] = controller.step1().apply(FakeRequest())
    status(result) must equalTo(OK)
  }

  def e2 = {
    val result: Future[SimpleResult] = controller.step1().apply(FakeRequest())
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Log in with Twitter")
    contentAsString(result) must contain("Log in with Facebook")
    contentAsString(result) must contain("Log in with Google")
    contentAsString(result) must contain("Log in with Linkedin")
  }

  def e3 = {
    val identity = FakeUserIdentity.unregistered
    val req = prepareSecuredGetRequest(identity, "/registration/step2")
    val result: Future[SimpleResult] = controller.step2().apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Step 2")
  }

  def e4 = {
    val identity = FakeUserIdentity.viewer
    val req = prepareSecuredGetRequest(identity, "/registration/step2")
    val result: Future[SimpleResult] = controller.step2().apply(req)
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must_== "/"
  }

  def e5 = {
    val result: Future[SimpleResult] = controller.step2().apply(FakeRequest())
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must contain("/login")
  }

  def e6 = {
    val identity = FakeUserIdentity.unregistered
    val req = prepareSecuredGetRequest(identity, "/registration/step2")
    val result: Future[SimpleResult] = controller.step2().apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Sergey")
    contentAsString(result) must contain("Kotlov")
    contentAsString(result) must contain("Invalid e-mail address")
  }

  def e7 = {
    val result: Future[SimpleResult] = controller.savePerson().apply(FakeRequest())
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must contain("/login")
  }

  def e8 = {
    val identity = FakeUserIdentity.viewer
    val req = prepareSecuredPostRequest(identity, "")
    val result: Future[SimpleResult] = controller.savePerson().apply(req)
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must_== "/"
  }

  def e10 = {
    val identity = FakeUserIdentity.unregistered
    val req = prepareSecuredPostRequest(identity, "").
      withFormUrlEncodedBody(("firstName", ""),
        ("lastName", ""), ("email", "ttt.ru"), ("country", "WWW"))
    val result: Future[SimpleResult] = controller.savePerson().apply(req)
    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("Required value missing")
    contentAsString(result) must contain("Invalid e-mail address")
    contentAsString(result) must contain("Please choose a country")
  }

  def e12 = {
    val identity = FakeUserIdentity.unregistered
    val cookie = Cookie(Registration.REGISTRATION_COOKIE, "org")

    val req = prepareSecuredPostRequest(identity, "").
      withFormUrlEncodedBody(("firstName", "First"),
        ("lastName", "Tester"), ("email", "tt@ttt.ru"), ("country", "RU")).
        withCookies(cookie)
    val result: Future[SimpleResult] = controller.savePerson().apply(req)
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must contain("/registration/step3")
  }

  def e13 = {
    val identity = FakeUserIdentity.unregistered
    val req = prepareSecuredGetRequest(identity, "/registration/step3")
    val result: Future[SimpleResult] = controller.step3().apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Step 3")
  }

  def e14 = {
    val identity = FakeUserIdentity.viewer
    val req = prepareSecuredGetRequest(identity, "/registration/step3")
    val result: Future[SimpleResult] = controller.step3().apply(req)
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must_== "/"
  }

  def e15 = {
    val result: Future[SimpleResult] = controller.step3().apply(FakeRequest())
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must contain("/login")
  }

  def e16 = {
    val result: Future[SimpleResult] = controller.saveOrg().apply(FakeRequest())
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must contain("/login")
  }

  def e17 = {
    val identity = FakeUserIdentity.viewer
    val req = prepareSecuredPostRequest(identity, "")
    val result: Future[SimpleResult] = controller.saveOrg().apply(req)
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must_== "/"
  }

  def e18 = {
    val identity = FakeUserIdentity.unregistered
    val req = prepareSecuredPostRequest(identity, "").
      withFormUrlEncodedBody(("name", ""), ("country", "WWW"))
    val result: Future[SimpleResult] = controller.saveOrg().apply(req)
    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("Required value missing")
    contentAsString(result) must contain("Please choose a country")
  }

  def e19 = {
    val identity = FakeUserIdentity.viewer
    val req = prepareSecuredGetRequest(identity, "/")
    val result: Future[SimpleResult] = controller.payment().apply(req)
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must_== "/"
  }

  def e20 = {
    val identity = FakeUserIdentity.unregistered
    val req = prepareSecuredGetRequest(identity, "/")
    val cacheId = controller.callPersonCacheId(identity)
    Cache.remove(cacheId)
    val result: Future[SimpleResult] = controller.payment().apply(req)
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must_== "/registration/step2"
  }

  def e21 = {
    val identity = FakeUserIdentity.unregistered
    val req = prepareSecuredGetRequest(identity, "/")
    val cacheId = controller.callPersonCacheId(identity)
    val userData = User("First", "Tester", "t@ttt.ru", "RU")
    Cache.set(cacheId, userData, 900)
    val result: Future[SimpleResult] = controller.payment().apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("You are from Russia")
  }

  def e22 = {
    val identity = FakeUserIdentity.unregistered
    val req = prepareSecuredGetRequest(identity, "/")
    val cacheId = controller.callPersonCacheId(identity)
    val userData = User("First", "Tester", "t@ttt.ru", "RU", true, OrgData("One", "DE"))
    Cache.set(cacheId, userData, 900)
    val result: Future[SimpleResult] = controller.payment().apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("One is from Germany")
  }

  def e23 = {
    val identity = FakeUserIdentity.viewer
    val req = prepareSecuredPostRequest(identity, "/")
    val result: Future[SimpleResult] = controller.charge().apply(req)
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must_== "/"
  }

  def e24 = {
    val identity = FakeUserIdentity.unregistered
    val req = prepareSecuredPostRequest(identity, "/")
    val cacheId = controller.callPersonCacheId(identity)
    Cache.remove(cacheId)
    val result: Future[SimpleResult] = controller.charge().apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("/registration/step2")
  }
}
