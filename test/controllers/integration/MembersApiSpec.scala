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

package controllers.integration

import controllers.apiv2.MembersApi
import helpers.{ MemberHelper, OrganisationHelper, PersonHelper }
import integration.PlayAppSpec
import models._
import models.service.{ OrganisationService, MemberService }
import org.scalamock.specs2.MockContext
import play.api.libs.json._
import play.api.test.FakeRequest
import stubs.{ FakeApiAuthentication, FakeServices }

class MembersApiSpec extends PlayAppSpec {

  class TestMembersApi() extends MembersApi
    with FakeApiAuthentication
    with FakeServices

  val memberOne = MemberHelper.make(Some(1L), 1L, person = true, funder = false)
  val memberTwo = MemberHelper.make(Some(2L), 2L, person = false, funder = true)
  val controller = new TestMembersApi

  "Method 'member'" should {
    "return well-formed JSON if a member is a person" in new MockContext {
      memberOne.memberObj_=(PersonHelper.one)

      val service = mock[MemberService]
      (service.find _) expects 1L returning Some(memberOne)
      controller.memberService_=(service)
      val res = controller.member(1L).apply(FakeRequest())
      val data = contentAsJson(res).asInstanceOf[JsObject]

      (data \ "id").as[JsNumber].value must_== BigDecimal(1L)
      (data \ "funder").as[JsBoolean].value must_== false
      (data \ "type").as[JsString].value must_== "person"
      data \ "org" must haveClass[JsUndefined]
      val keys = (data \ "person").as[JsObject].keys
      keys.contains("id") must_== true
      keys.contains("first_name") must_== true
      keys.contains("address") must_== true
      keys.contains("organizations") must_== true
    }
    "return well-formed JSON if a member is an org" in new MockContext {
      val memberService = mock[MemberService]
      controller.memberService_=(memberService)
      val orgService = mock[OrganisationService]
      controller.orgService_=(orgService)
      (memberService.find _) expects 2L returning Some(memberTwo)
      val profile = SocialProfile(0, ProfileType.Organisation, "")
      val view = OrgView(OrganisationHelper.one, profile)
      (orgService.findWithProfile _) expects 2L returning Some(view)
      val res = controller.member(2L).apply(FakeRequest())
      val data = contentAsJson(res).asInstanceOf[JsObject]

      (data \ "id").as[JsNumber].value must_== BigDecimal(2L)
      (data \ "funder").as[JsBoolean].value must_== true
      (data \ "type").as[JsString].value must_== "org"
      data \ "person" must haveClass[JsUndefined]
      val keys = (data \ "org").as[JsObject].keys
      keys.contains("name") must_== true
      keys.contains("about") must_== true
      keys.contains("twitter_handle") must_== true
      keys.contains("facebook_url") must_== true
      keys.contains("linkedin_url") must_== true
      keys.contains("google_plus_url") must_== true
      keys.contains("members") must_== true
      keys.contains("contributions") must_== true
    }
  }
}
