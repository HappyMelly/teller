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

import controllers.{ Registration, User }
import integration.PlayAppSpec
import play.api.Play.current
import play.api.cache.Cache
import play.api.mvc.SimpleResult
import play.api.test.FakeRequest
import securesocial.core.IdentityId
import stubs.FakeUserIdentity

import scala.concurrent.Future

class RegistrationSpec extends PlayAppSpec {

  class TestRegistration() extends Registration {

    def callPersonCacheId(id: IdentityId): String = personCacheId(id)
  }

  def setupDb() {}

  def cleanupDb() {}

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
    save person data to cache                         $e11
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

  def e11 = {
    val identity = FakeUserIdentity.unregistered
    val req = prepareSecuredPostRequest(identity, "").
      withFormUrlEncodedBody(("firstName", "First"),
        ("lastName", "Tester"), ("email", "tt@ttt.ru"), ("country", "RU"))
    val result: Future[SimpleResult] = controller.savePerson().apply(req)
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").get must contain("/step3")
    val cacheId = controller.callPersonCacheId(identity)
    Cache.getAs[User](cacheId) map { userData ⇒
      userData.firstName must_== "First"
      userData.lastName must_== "Tester"
      userData.email must_== "tt@ttt.ru"
      userData.country must_== "RU"
    } getOrElse ko
  }
}
