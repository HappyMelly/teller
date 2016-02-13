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
package controllers.acceptance

import _root_.integration.PlayAppSpec
import controllers.ProfilePhotos
import helpers._
import models.{ SocialProfile, Photo }
import models.repository.PersonRepository
import org.scalamock.specs2.{ IsolatedMockFactory, MockContext }
import play.api.libs.json._
import play.api.test.FakeRequest
import stubs._

class ProfilePhotosSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  Gravatar and Custom options should
    be always available to a person to choose from                      $e3

  When a person provides incomplete data about new photo the system
    should return an error                                              $e4

  When a person provides valid data the system
    should update a person profile                                      $e5
  """

  class TestProfilePhotos() extends ProfilePhotos(FakeRuntimeEnvironment)
    with FakeSecurity with FakeRepositories

  val controller = new TestProfilePhotos()
  val personService = mock[PersonRepository]
  controller.personService_=(personService)

  val person = PersonHelper.one()

  trait DefaultPerson extends MockContext {
    (services.personService.find(_: Long)) expects 1L returning Some(person)
  }

  def e3 = new DefaultPerson {
    val result = controller.choose(1L).apply(fakeGetRequest())
    contentAsString(result) must contain("https://secure.gravatar.com")
    contentAsString(result) must contain("happymelly-face-white.png")
  }

  def e4 = {
    val result = controller.update(1L).apply(fakePostRequest())
    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("No option is provided")
  }

  def e5 = new DefaultPerson {
    (services.personService.update _) expects personWithGravatar returning personWithGravatar

    val result = controller.update(1L).apply(gravatarRequest)
    status(result) must equalTo(OK)
  }

  private def gravatarRequest = fakePostRequest().
    withFormUrlEncodedBody(("type" -> "gravatar"), ("name" -> ""))

  private def personWithGravatar = {
    val photo = Photo(Some("gravatar"), Some("https://secure.gravatar.com/avatar/cbc4c5829ca103f23a20b31dbf953d05?s=300"))
    val updatedPerson = person.copy(photo = photo)
    updatedPerson
  }
}