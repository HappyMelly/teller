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
import controllers.{ ProfileCompletions, Security }
import helpers._
import models.ProfileCompletion
import models.service.ProfileCompletionService
import org.scalamock.specs2.IsolatedMockFactory
import play.api.libs.json.{ Json, JsArray }
import play.api.test.FakeRequest
import stubs._

class ProfileCompletionsSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  When a photo step is not complete
    the profile completion widget should contain 'Add photo' step         $e1

  When a description step is not complete
    the profile completion widget should contain 'Add description' step   $e2

  When a social profile step is not complete
    the profile completion widget should contain 'Add 2 social networks' step $e3

  When a reason step is not complete
    the profile completion widget should contain 'Share why you joined the network' step $e4
  """

  class TestProfileCompletions() extends ProfileCompletions
    with Security
    with FakeServices

  val controller = new TestProfileCompletions()
  val service = mock[ProfileCompletionService]
  controller.profileCompletionService_=(service)

  def e1 = {
    val steps = Json.arr(
      Json.obj(
        "name" -> "photo",
        "weight" -> 10,
        "done" -> false))
    val completion = ProfileCompletion(None, 1L, false, steps);

    (service.find _) expects (1L, false) returning Some(completion)
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "")
    val result = controller.personProfile(1L).apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add photo")
  }

  def e2 = {
    val steps = Json.arr(
      Json.obj(
        "name" -> "about",
        "weight" -> 10,
        "done" -> false))
    val completion = ProfileCompletion(None, 1L, false, steps);

    (service.find _) expects (1L, false) returning Some(completion)
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "")
    val result = controller.personProfile(1L).apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add description")
  }

  def e3 = {
    val steps = Json.arr(
      Json.obj(
        "name" -> "social",
        "weight" -> 10,
        "done" -> false))
    val completion = ProfileCompletion(None, 1L, false, steps);

    (service.find _) expects (1L, false) returning Some(completion)
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "")
    val result = controller.personProfile(1L).apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add 2 social networks")
  }

  def e4 = {
    val steps = Json.arr(
      Json.obj(
        "name" -> "reason",
        "weight" -> 10,
        "done" -> false))
    val completion = ProfileCompletion(None, 1L, false, steps);

    (service.find _) expects (1L, false) returning Some(completion)
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "")
    val result = controller.personProfile(1L).apply(req)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Share why you joined the network")
  }
}
