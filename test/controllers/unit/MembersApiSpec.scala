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

package controllers.unit

import controllers.api.MembersApi
import helpers.{ MemberHelper, OrganisationHelper, PersonHelper }
import models.Photo
import org.joda.time.LocalDate
import org.scalamock.specs2.MockContext
import org.specs2.mutable._
import play.api.libs.json.{ JsArray, Json }
import play.api.test.FakeRequest
import play.api.test.Helpers._
import stubs.{ FakeApiAuthentication, FakeMemberService, FakeServices }

class MembersApiSpec extends Specification {

  class TestMembersApi() extends MembersApi
    with FakeApiAuthentication
    with FakeServices

  val memberOne = MemberHelper.make(Some(1L), 1L, person = true, funder = false)
  val memberTwo = MemberHelper.make(Some(2L), 2L, person = false, funder = true)
  val controller = new TestMembersApi

  "Method 'members'" should {
    "return a list of members with some details in JSON format" in new MockContext {
      val photo = Photo(Some("gravatar"), Some("link"))
      memberOne.memberObj_=(PersonHelper.one().copy(photo = photo))
      memberTwo.memberObj_=(OrganisationHelper.two)

      val service = mock[FakeMemberService]
      (service.findAll _).expects().returning(List(memberOne, memberTwo))
      controller.memberService_=(service)
      val res = controller.members().apply(FakeRequest())

      status(res) must beEqualTo(OK)
      val data = contentAsJson(res).as[JsArray]
      data.value.length must_== 2
      data.value(0) must_== Json.obj(
        "id" -> 1,
        "name" -> "First Tester",
        "type" -> "person",
        "funder" -> false,
        "image" -> "link")
      data.value(1) must_== Json.obj(
        "id" -> 2,
        "name" -> "Two",
        "type" -> "org",
        "funder" -> true,
        "image" -> None.asInstanceOf[Option[String]])
    }
    "return a list of active members only" in new MockContext {
      val inactiveOne = MemberHelper.make(Some(3L), 2L,
        person = true, funder = false,
        until = Some(LocalDate.now().minusMonths(2)))
      val inactiveTwo = MemberHelper.make(Some(4L), 1L,
        person = false, funder = false,
        since = Some(LocalDate.now().plusDays(2)))
      memberOne.memberObj_=(PersonHelper.one())
      memberTwo.memberObj_=(OrganisationHelper.two)
      inactiveOne.memberObj_=(PersonHelper.two())
      inactiveTwo.memberObj_=(OrganisationHelper.one)

      val service = mock[FakeMemberService]
      (service.findAll _).expects().returning(
        List(memberOne, memberTwo, inactiveOne, inactiveTwo))
      controller.memberService_=(service)
      val res = controller.members().apply(FakeRequest())

      status(res) must beEqualTo(OK)
      val data = contentAsJson(res).as[JsArray]
      data.value.length must_== 2
    }
    "return funders only if funder = true" in new MockContext {
      memberOne.memberObj_=(PersonHelper.one())
      memberTwo.memberObj_=(OrganisationHelper.two)

      val service = mock[FakeMemberService]
      (service.findAll _).expects().returning(List(memberOne, memberTwo))
      controller.memberService_=(service)
      val res = controller.members(Some(true)).apply(FakeRequest())

      val data = contentAsJson(res).as[JsArray]
      data.value.length must_== 1
      data.value(0) must_== Json.obj(
        "id" -> 2,
        "name" -> "Two",
        "type" -> "org",
        "funder" -> true,
        "image" -> None.asInstanceOf[Option[String]])
    }
    "return supporters only if supporter = true" in new MockContext {
      memberOne.memberObj_=(PersonHelper.one())
      memberTwo.memberObj_=(OrganisationHelper.two)

      val service = mock[FakeMemberService]
      (service.findAll _).expects().returning(List(memberOne, memberTwo))
      controller.memberService_=(service)
      val res = controller.members(Some(false)).apply(FakeRequest())

      val data = contentAsJson(res).as[JsArray]
      data.value.length must_== 1
      data.value(0) must_== Json.obj(
        "id" -> 1,
        "name" -> "First Tester",
        "type" -> "person",
        "funder" -> false,
        "image" -> None.asInstanceOf[Option[String]])
    }
  }

}
