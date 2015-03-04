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
package controllers.unit

import controllers.Members
import integration.PlayAppSpec
import helpers.{ PersonHelper, MemberHelper }
import models.Member
import org.joda.money.Money
import org.scalamock.specs2.MockContext
import org.specs2.mutable._
import services.notifiers.Slack
import stubs._
import stubs.services.FakeSlack

class TestMembers extends Members with FakeServices {
  val slackInstance = new FakeSlack
  var counter: Int = 0

  override def slack: Slack = {
    counter += 1
    slackInstance
  }

  def callSlackMessage(member: Member, name: String, url: String): String = {
    slackMessage(member, name, url)
  }

  def callProfileUrl(member: Member): String = profileUrl(member)
}

class MembersSpec extends PlayAppSpec {

  def cleanupDb() {}
  def setupDb() {}

  val controller = new TestMembers

  "Slack messages for new members" should {
    "be nicely formed" in {
      val m1 = MemberHelper.make(Some(1L), 1L,
        person = true,
        funder = true,
        money = Some(Money.parse("EUR 100")))
      val msg1 = controller.callSlackMessage(m1, "One", "/person/1")
      msg1 must_== "Hey @channel, we have *new Funder*. One, EUR 100.00. <http://localhost:9000/person/1|View profile>"
      val m2 = MemberHelper.make(Some(2L), 2L,
        person = false,
        funder = false,
        money = Some(Money.parse("EUR 200")))
      val msg2 = controller.callSlackMessage(m2, "Two", "/organisation/2")
      msg2 must_== "Hey @channel, we have *new Supporter*. Two, EUR 200.00. <http://localhost:9000/organisation/2|View profile>"
    }
  }
  "Profile url" should {
    "direct to Organisation details page" in {
      val m = MemberHelper.make(Some(1L), 3L, person = false, funder = true)
      val url = controller.callProfileUrl(m)
      url must_== "/organization/3"
    }
    "direct to Person details page" in {
      val m = MemberHelper.make(Some(1L), 2L, person = true, funder = true)
      val url = controller.callProfileUrl(m)
      url must_== "/person/2"
    }
  }

  "Delete action" should {
    "send Slack notification" in new MockContext {
      val memberService = mock[FakeMemberService]
      val person = PersonHelper.one()
      val member = MemberHelper.make(Some(2L), 1L, person = true, funder = false)
      member.memberObj_=(person)
      (memberService.find(_, _)).expects(2L, true).returning(Some(member))
      (memberService.delete(_, _)).expects(1L, true)
      controller.memberService_=(memberService)
      controller.counter = 0
      val req = prepareSecuredPostRequest(StubUserIdentity.editor, "/")
      val result = controller.delete(2L).apply(req)
      status(result) must equalTo(SEE_OTHER)
      controller.counter must_== 1
      val msg = "Hey @channel, First Tester is not a member anymore. <http://localhost:9000/person/1|View profile>"
      controller.slackInstance.message must_== msg
    }
  }
  // "In Update action Slack notification should be sent" >> {
  //   "when since date is changed" in new MockContext {
  //     val memberService = mock[FakeMemberService]
  //     val person = PersonHelper.one()
  //     val member = MemberHelper.make(Some(2L),
  //       1L,
  //       person = true,
  //       funder = false).insert
  //     member.memberObj_=(person)
  //     (memberService.find(_, _)).expects(2L, true).returning(Some(member))
  //     (memberService.delete(_, _)).expects(1L, true)
  //     val controller = new TestMembers
  //     controller.memberService_=(memberService)
  //     controller.counter = 0
  //     val req = prepareSecuredPostRequest(StubUserIdentity.editor, "/")
  //     val result = controller.delete(2L).apply(req)
  //     status(result) must equalTo(SEE_OTHER)
  //     controller.counter must_== 1
  //     val msg = "Hey @channel, First Tester is not a member anymore. <http://localhost:9000/person/1|View profile>"
  //     controller.slackInstance.message must_== msg
  //   }
  // }
}