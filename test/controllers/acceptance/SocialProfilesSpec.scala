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
import controllers.{ SocialProfiles, Security }
import helpers._
import models.SocialProfile
import models.service.SocialProfileService
import org.scalamock.specs2.IsolatedMockFactory
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import stubs._

class SocialProfilesSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  When a user requests a facebook url of a profile which is already taken
    the system should return an error                                      $e1

  When a user request a facebook url of a free profile
    the system should return an url with the profile photo                 $e2
  """

  class TestSocialProfiles() extends SocialProfiles with Security with FakeServices

  val controller = new TestSocialProfiles
  val service = mock[SocialProfileService]
  controller.socialProfileService_=(service)

  def e1 = {
    val profile = SocialProfile(objectId = 1,
      email = "dummy",
      facebookUrl = Some("https://www.facebook.com/test"))
    (service.findDuplicate _) expects profile returning Some(profile.copy(objectId = 2))
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "")
    val result = controller.facebookUrl(1L, "test").apply(req)
    status(result) must equalTo(CONFLICT)
    val data = contentAsJson(result).as[JsObject]
    (data \ "message").as[String] must contain("already taken")
  }

  def e2 = {
    val profile = SocialProfile(objectId = 1,
      email = "dummy",
      facebookUrl = Some("https://www.facebook.com/skotlov"))
    (service.findDuplicate _) expects profile returning None
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "")
    val result = controller.facebookUrl(1L, "skotlov").apply(req)
    status(result) must equalTo(OK)
    val data = contentAsJson(result).as[JsObject]
    (data \ "message").as[String] must contain("graph.facebook.com")
  }
}