/*
* Happy Melly Teller
* Copyright (C) 2013 - 2015, Happy Melly http -> //www.happymelly.com
*
* This file is part of the Happy Melly Teller.
*
* Happy Melly Teller is free software ->  you can redistribute it and/or modify
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
* along with Happy Melly Teller.  If not, see <http -> //www.gnu.org/licenses/>.
*
* If you have questions concerning this license or the applicable additional
* terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
* or in writing Happy Melly One, Handelsplein 37, Rotterdam,
* The Netherlands, 3071 PR
*/
package acceptance

import controllers.{ People, Security }
import helpers.PersonHelper
import integration.PlayAppSpec
import models.SocialProfile
import org.scalamock.specs2.MockContext
import play.api.mvc.SimpleResult
import play.api.test.FakeRequest
import play.api.test.Helpers._
import stubs.{ StubLoginIdentity, StubPersonService, FakeServices }

import scala.concurrent.Future

class TestPeople() extends People with Security with FakeServices

class PeopleSpec extends PlayAppSpec {
  def setupDb() {}
  def cleanupDb() {}

  override def is = s2"""

  Page with person's data should
    not be visible to unauthorized user                  $e1
    and be visible to authorized user                    $e2
    not contain accounting details if user is not Editor $e3
    contain accounting details if user is Editor         $e4
  """
  def e1 = {
    val controller = new TestPeople()
    val result: Future[SimpleResult] = controller.details(1).apply(FakeRequest())

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome.which(_.contains("login"))
  }

  def e2 = {
    new MockContext {
      val controller = new TestPeople()
      val mockService = mock[StubPersonService]
      // if this method is called it means we have passed a security check
      (mockService.find(_: Long)) expects 1L returning None
      controller.personService_=(mockService)
      val identity = StubLoginIdentity.viewer
      val request = prepareSecuredGetRequest(identity, "/person/1")
      controller.details(1).apply(request)
    }
  }

  def e3 = {
    new MockContext {
      val person = PersonHelper.one()
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val controller = new TestPeople()
      val mockService = mock[StubPersonService]
      (mockService.find(_: Long)) expects 1L returning Some(person)
      controller.personService_=(mockService)
      val identity = StubLoginIdentity.viewer
      val request = prepareSecuredGetRequest(identity, "/person/1")
      val result: Future[SimpleResult] = controller.details(person.id.get).apply(request)

      status(result) must equalTo(OK)
      contentAsString(result) must not contain "Financial account"
      contentAsString(result) must not contain "Account history"
    }
  }

  def e4 = {
    new MockContext {
      val person = PersonHelper.one().insert
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val controller = new TestPeople()
      val mockService = mock[StubPersonService]
      (mockService.find(_: Long)) expects 1L returning Some(person)
      controller.personService_=(mockService)
      val req = prepareSecuredGetRequest(StubLoginIdentity.editor, "/person/1")
      val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

      status(result) must equalTo(OK)
      contentAsString(result) must contain("Financial account")
      contentAsString(result) must contain("Account history")
    }
  }
}
