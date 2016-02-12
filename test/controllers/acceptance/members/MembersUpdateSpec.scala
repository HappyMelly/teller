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
package controllers.acceptance.members

import _root_.integration.PlayAppSpec
import controllers._
import helpers._
import models.ProfileStrength
import models.service.{ MemberService, PersonService, ProfileStrengthService }
import org.scalamock.specs2.IsolatedMockFactory
import play.api.libs.json._
import stubs._

class MembersUpdateSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  Given a person is not a member, when the reason for this person is updated,
    then the system should return error                                    $e1

  Given a person is a member, when the reason for thi person is updated,
    then the system should update the reason                               $e2
    then the system should complete 'Reason' step                          $e3
  """
  class TestMembers extends Members(FakeRuntimeEnvironment)
    with FakeServices
    with FakeSecurity

  val controller = new TestMembers
  val personService = mock[PersonService]
  val memberService = mock[MemberService]
  val profileStrengthService = mock[ProfileStrengthService]
  controller.personService_=(personService)
  controller.memberService_=(memberService)
  controller.profileStrengthService_=(profileStrengthService)
  val member = MemberHelper.make(None, 1L, person = true, funder = false)
  val reason = "testblabla"

  def e1 = {
    (services.personService.member _) expects 1L returning None
    val res = controller.updateReason(1L).apply(fakePostRequest())
    status(res) must equalTo(NOT_FOUND)
  }

  def e2 = {
    (services.personService.member _) expects 1L returning Some(member)
    (services.memberService.update _) expects member.copy(reason = Some(reason))
    (services.profileStrengthService.find(_: Long, _: Boolean)) expects (1L, false) returning None
    val req = fakePostRequest().withFormUrlEncodedBody("reason" -> reason)
    val res = controller.updateReason(1L).apply(req)
    status(res) must equalTo(OK)
    val data = contentAsJson(res).as[JsObject]
    (data \ "message").as[String] must contain(reason)
  }

  def e3 = {
    val strength = ProfileStrength.forMember(ProfileStrength.empty(1L, false))
    (services.personService.member _) expects 1L returning Some(member)
    (services.memberService.update _) expects member.copy(reason = Some(reason))
    (services.profileStrengthService.find(_: Long, _: Boolean)) expects (1L, false) returning Some(strength)
    (services.profileStrengthService.update _) expects strength.markComplete("reason")
    val req = fakePostRequest().withFormUrlEncodedBody("reason" -> reason)
    val res = controller.updateReason(1L).apply(req)
    status(res) must equalTo(OK)
  }

}