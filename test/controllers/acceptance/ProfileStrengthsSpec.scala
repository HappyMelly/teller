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
import controllers.ProfileStrengths
import helpers._
import models.ProfileStrength
import models.service.ProfileStrengthService
import org.scalamock.specs2.IsolatedMockFactory
import play.api.libs.json.{ Json, JsArray }
import play.api.test.FakeRequest
import stubs._

class ProfileStrengthsSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  When a photo step is not complete
    the profile strength widget should contain 'Add photo' step         $e1

  When a description step is not complete
    the profile strength widget should contain 'Add description' step   $e2

  When a social profile step is not complete
    the profile strength widget should contain 'Add 2 social networks' step $e3

  When a reason step is not complete
    the profile strength widget should contain 'Share why you joined the network' step $e4

  When a member step is not complete
    the profile strength widget should contain 'Become a member' step     $e5

  When a signature step is not complete
    the profile strength widget should contain 'Upload your signature'    $e6

  When a language step is not complete
    the profile strength widget should contain 'Add at least 1 language'  $e7

  When profile strength is not initialized
    and person id doesn't belong to current user, the error should be returned $e8
  """

  class TestProfileStrengths() extends ProfileStrengths(FakeRuntimeEnvironment)
    with FakeSecurity
    with FakeServices

  val controller = new TestProfileStrengths()
  val service = mock[ProfileStrengthService]
  controller.profileStrengthService_=(service)

  def e1 = {
    val steps = Json.arr(
      Json.obj(
        "name" -> "photo",
        "weight" -> 10,
        "done" -> false))
    val strength = ProfileStrength(None, 1L, false, steps);

    (service.find _) expects (1L, false) returning Some(strength)
    val result = controller.personWidget(1L, true).apply(fakeGetRequest())
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add photo")
  }

  def e2 = {
    val steps = Json.arr(
      Json.obj(
        "name" -> "about",
        "weight" -> 10,
        "done" -> false))
    val strength = ProfileStrength(None, 1L, false, steps);

    (service.find _) expects (1L, false) returning Some(strength)
    val result = controller.personWidget(1L, true).apply(fakeGetRequest())
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add description")
  }

  def e3 = {
    val steps = Json.arr(
      Json.obj(
        "name" -> "social",
        "weight" -> 10,
        "done" -> false))
    val strength = ProfileStrength(None, 1L, false, steps);

    (service.find _) expects (1L, false) returning Some(strength)
    val result = controller.personWidget(1L, true).apply(fakeGetRequest())
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add 2 social networks")
  }

  def e4 = {
    val steps = Json.arr(
      Json.obj(
        "name" -> "reason",
        "weight" -> 10,
        "done" -> false))
    val strength = ProfileStrength(None, 1L, false, steps);

    (service.find _) expects (1L, false) returning Some(strength)
    val result = controller.personWidget(1L, true).apply(fakeGetRequest())
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Share why you joined the network")
  }

  def e5 = {
    val steps = Json.arr(
      Json.obj(
        "name" -> "member",
        "weight" -> 10,
        "done" -> false))
    val strength = ProfileStrength(None, 1L, false, steps);

    (service.find _) expects (1L, false) returning Some(strength)
    val result = controller.personWidget(1L, true).apply(fakeGetRequest())
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Become a member")
  }

  def e6 = {
    val steps = Json.arr(
      Json.obj(
        "name" -> "signature",
        "weight" -> 10,
        "done" -> false))
    val strength = ProfileStrength(None, 1L, false, steps);

    (service.find _) expects (1L, false) returning Some(strength)
    val result = controller.personWidget(1L, true).apply(fakeGetRequest())
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Upload your signature")
  }

  def e7 = {
    val steps = Json.arr(
      Json.obj(
        "name" -> "language",
        "weight" -> 10,
        "done" -> false))
    val strength = ProfileStrength(None, 1L, false, steps);

    (service.find _) expects (1L, false) returning Some(strength)
    val result = controller.personWidget(1L, true).apply(fakeGetRequest())
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add at least 1 language")
  }

  def e8 = {
    //current user id = 1
    (service.find _) expects (2L, false) returning None
    val result = controller.personWidget(2L, true).apply(fakeGetRequest())
    status(result) must equalTo(BAD_REQUEST)
  }
}
