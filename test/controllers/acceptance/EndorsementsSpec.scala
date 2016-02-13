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
import controllers.Endorsements
import helpers._
import models.Person
import models.Endorsement
import models.repository.PersonRepository
import org.scalamock.specs2.IsolatedMockFactory
import play.api.libs.json._
import play.api.test.FakeRequest
import stubs._

class EndorsementsSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  When the given person is not found
    during endorsement creation an error should be returned        $e1

  Given a endorsement is empty when the endorsement is being added
    then an error should be returned                                     $e2

  Given a endorsement has empty name when the endorsement is being added
    then an error should be returned                                     $e3

  Given a endorsement form data is valid when the endorsement is being added
    then the system should just add it                                   $e4

  When the endorsement is being deleted
    successful response should be always returned                        $e5

  Given a endorsement is empty when the endorsement is being updated
    then an error should be returned                                     $e6

  Given a endorsement has empty name when the endorsement is being updated
    then an error should be returned                                     $e7

  Given a endorsement form data is valid when the endorsement is being updated
    then the system should just update it                                $e8
  """

  class TestEndorsements extends Endorsements(FakeRuntimeEnvironment)
    with FakeRepositories
    with FakeSecurity

  val controller = new TestEndorsements
  val personService = mock[PersonRepository]
  controller.personService_=(personService)
  val person = PersonHelper.one

  def e1 = {
    (services.personService.find(_: Long)) expects 1L returning None
    val res = controller.create(1L).apply(fakePostRequest())
    status(res) must equalTo(NOT_FOUND)
  }

  def e2 = {
    (services.personService.find(_: Long)) expects 1L returning Some(person)
    val req = fakePostRequest().
      withFormUrlEncodedBody("content" -> "", "name" -> "katja")
    val res = controller.create(1L).apply(req)
    status(res) must equalTo(BAD_REQUEST)
    contentAsString(res) must contain("Required value missing")
  }

  def e3 = {
    (services.personService.find(_: Long)) expects 1L returning Some(person)
    val req = fakePostRequest().
      withFormUrlEncodedBody("content" -> "test", "name" -> "")
    val res = controller.create(1L).apply(req)
    status(res) must equalTo(BAD_REQUEST)
    contentAsString(res) must contain("Required value missing")
  }

  def e4 = {
    (services.personService.find(_: Long)) expects 1L returning Some(person)
    val endorsement = Endorsement(None, 1L, 0L, "blabla", "katja", Some("test"))
    (services.personService.insertEndorsement _) expects endorsement.copy(position = 1)
    (services.personService.endorsements _) expects 1L returning List()
    val req = fakePostRequest().
      withFormUrlEncodedBody("brandId" -> "0", "content" -> "blabla", "name" -> "katja", "company" -> "test")
    val res = controller.create(1L).apply(req)
    status(res) must equalTo(SEE_OTHER)
  }

  def e5 = {
    (services.personService.deleteEndorsement _) expects (2L, 1L)
    val res = controller.remove(2L, 1L).apply(fakeDeleteRequest())
    status(res) must equalTo(OK)
  }

  def e6 = {
    val req = fakePostRequest().
      withFormUrlEncodedBody("content" -> "", "name" -> "katja")
    val res = controller.update(2L, 1L).apply(req)
    status(res) must equalTo(BAD_REQUEST)
    contentAsString(res) must contain("Required value missing")
  }

  def e7 = {
    val req = fakePostRequest().
      withFormUrlEncodedBody("content" -> "test", "name" -> "")
    val res = controller.update(2L, 1L).apply(req)
    status(res) must equalTo(BAD_REQUEST)
    contentAsString(res) must contain("Required value missing")
  }

  def e8 = {
    val endorsement = Endorsement(Some(2L), 1L, 0L, "blabla", "katja", Some("test"))
    (services.personService.updateEndorsement _) expects endorsement
    val req = fakePostRequest().
      withFormUrlEncodedBody("brandId" -> "0", "content" -> "blabla", "name" -> "katja", "company" -> "test")
    val res = controller.update(1L, 2L).apply(req)
    status(res) must equalTo(SEE_OTHER)
  }

}