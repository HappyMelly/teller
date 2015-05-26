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
import controllers.{ People, Security }
import helpers._
import models.{ SocialProfile, Photo }
import org.scalamock.specs2.IsolatedMockFactory
import play.api.test.FakeRequest
import stubs._

class PeoplePhotoSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  When a person has a Facebook profile
    a photo from Facebook should be shown as an option                  $e1

  When a peson does not have a Facebook profile
    a field requesting Facebook name should be shown as an option       $e2

  Gravatar and No photo options should
    be always available to a person to choose from                      $e3

  When a person provides incomplete data about new photo the system
    should return an error                                              $e4

  When a person provides valid data the system
    should update a person profile if new photo is Gravatar             $e5
    should update a person profile if there is no photo                 $e6
    should update a person profile if new photo is Facebook             $e7
  """

  class TestPeople() extends People with Security with FakeServices

  val controller = new TestPeople()
  val personService = mock[FakePersonService]
  controller.personService_=(personService)

  def e1 = {
    val person = PersonHelper.one()
    person.socialProfile_=(new SocialProfile(email = "test@test.com",
      facebookUrl = Some("https://www.facebook.com/skotlov")))
    (personService.find(_: Long)) expects 1L returning Some(person)
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/person/1/photo")
    val result = controller.choosePhoto(1L).apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("<img class=\"facebook")
    contentAsString(result) must contain("http://graph.facebook.com")
  }

  def e2 = {
    val person = PersonHelper.one()
    person.socialProfile_=(new SocialProfile(email = "test@test.com"))
    (personService.find(_: Long)) expects 1L returning Some(person)
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/person/1/photo")
    val result = controller.choosePhoto(1L).apply(req)
    contentAsString(result) must not contain "http://graph.facebook.com"
    contentAsString(result) must contain("Enter Facebook name")
  }

  def e3 = {
    val person = PersonHelper.one()
    person.socialProfile_=(new SocialProfile(email = "test@test.com"))
    (personService.find(_: Long)) expects 1L returning Some(person)
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/person/1/photo")
    val result = controller.choosePhoto(1L).apply(req)
    contentAsString(result) must contain("https://secure.gravatar.com")
    contentAsString(result) must contain("happymelly-face-white.png")
  }

  def e4 = {
    val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/person/1/photo")
    val result = controller.updatePhoto(1L).apply(req)
    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("No option is provided")
  }

  def e5 = {
    val profile = new SocialProfile(email = "test@test.com")
    val person = PersonHelper.one()
    person.socialProfile_=(profile)
    (personService.find(_: Long)) expects 1L returning Some(person)
    val photo = Photo(Some("gravatar"), Some("https://secure.gravatar.com/avatar/b642b4217b34b1e8d3bd915fc65c4452?s=300"))
    val updatedPerson = person.copy(photo = photo)
    (personService.update _) expects updatedPerson returning updatedPerson
    val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/person/1/photo").
      withFormUrlEncodedBody(("type" -> "gravatar"))
    val result = controller.updatePhoto(1L).apply(req)
    status(result) must equalTo(OK)
  }

  def e6 = {
    val profile = new SocialProfile(email = "test@test.com")
    val person = PersonHelper.one()
    person.socialProfile_=(profile)
    (personService.find(_: Long)) expects 1L returning Some(person)
    val updatedPerson = person.copy(photo = Photo.empty)
    (personService.update _) expects updatedPerson returning updatedPerson
    val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/person/1/photo").
      withFormUrlEncodedBody(("type" -> "nophoto"))
    val result = controller.updatePhoto(1L).apply(req)
    status(result) must equalTo(OK)
  }

  def e7 = {
    val profile = new SocialProfile(email = "test@test.com",
      facebookUrl = Some("https://www.facebook.com/skotlov"))
    val person = PersonHelper.one()
    person.socialProfile_=(profile)
    (personService.find(_: Long)) expects 1L returning Some(person)
    val photo = Photo(Some("facebook"), Some("http://graph.facebook.com/skotlov/picture?type=large"))
    val updatedPerson = person.copy(photo = photo)
    (personService.update _) expects updatedPerson returning updatedPerson
    val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/person/1/photo").
      withFormUrlEncodedBody(("type" -> "facebook"))
    val result = controller.updatePhoto(1L).apply(req)
    status(result) must equalTo(OK)
  }
}