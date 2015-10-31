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
import org.joda.time.LocalDate
import services.integrations.Slack
import stubs._
import stubs.services.FakeSlack

class TestMembers extends Members(FakeRuntimeEnvironment) with FakeServices {
  val slackInstance = new FakeSlack
  var counter: Int = 0

  override def slack: Slack = {
    counter += 1
    slackInstance
  }

  def callNewMemberMsg(member: Member, name: String, url: String): String = {
    newMemberMsg(member, name, url)
  }

  def callProfileUrl(member: Member): String = profileUrl(member)

  def callUpdatedMemberMsg(before: Member, after: Member): Option[String] = {
    updatedMemberMsg(before, after, "")
  }
}

class MembersSpec extends PlayAppSpec {

  val controller = new TestMembers

  "Slack messages for new members" should {
    "be nicely formed" in {
      val m1 = MemberHelper.make(Some(1L), 1L,
        person = true,
        funder = true,
        money = Some(Money.parse("EUR 100")))
      val msg1 = controller.callNewMemberMsg(m1, "One", "/person/1")
      msg1 must_== "Hooray!! We have *new Funder*, One. <http://localhost:9000/person/1|View profile>"
      val m2 = MemberHelper.make(Some(2L), 2L,
        person = false,
        funder = false,
        money = Some(Money.parse("EUR 200")))
      val msg2 = controller.callNewMemberMsg(m2, "Two", "/organisation/2")
      msg2 must_== "Hooray!! We have *new Supporter*, Two. <http://localhost:9000/organisation/2|View profile>"
    }
  }

  "Slack messages for updated members" should {
    "contain info about changed date if Since field changed" in {
      val person = PersonHelper.one()
      val before = MemberHelper.make(
        Some(1L),
        1L,
        person = true,
        funder = true,
        since = Some(LocalDate.parse("2015-01-15")))
      before.memberObj_=(person)
      val after = before.copy(since = LocalDate.parse("2015-01-01"))
      val msg = controller.callUpdatedMemberMsg(before, after)
      msg map { m ⇒
        m must_== "Hey @channel, member First Tester was updated. Field *Since* has changed from '2015-01-15' to '2015-01-01'. <http://localhost:9000|View profile>"
      } getOrElse ko
    }
    "contain info about changed membership type if Funder field changed" in {
      val person = PersonHelper.one()
      val before = MemberHelper.make(Some(1L), 1L, person = true, funder = true)
      before.memberObj_=(person)
      val after = before.copy(funder = false)
      val msg = controller.callUpdatedMemberMsg(before, after)
      msg map { m ⇒
        m must_== "Hey @channel, member First Tester was updated. Field *Funder* has changed from 'funder' to 'supporter'. <http://localhost:9000|View profile>"
      } getOrElse ko
    }
    "contain info about changed fee if Fee field changed" in {
      val person = PersonHelper.one()
      val before = MemberHelper.make(Some(1L),
        1L,
        person = true,
        funder = true,
        money = Some(Money.parse("EUR 100")))
      before.memberObj_=(person)
      val after = before.copy(fee = Money.parse("EUR 200"))
      val msg = controller.callUpdatedMemberMsg(before, after)
      msg map { m ⇒
        m must_== "Hey @channel, member First Tester was updated. Field *Fee* has changed from 'EUR 100.00' to 'EUR 200.00'. <http://localhost:9000|View profile>"
      } getOrElse ko
    }
    "contain info about changed fee and date if Fee and Since fields changed" in {
      val person = PersonHelper.one()
      val before = MemberHelper.make(Some(1L),
        1L,
        person = true,
        funder = true,
        money = Some(Money.parse("EUR 100")),
        since = Some(LocalDate.parse("2015-01-15")))
      before.memberObj_=(person)
      val after = before.
        copy(fee = Money.parse("EUR 200")).
        copy(since = LocalDate.parse("2015-01-01"))
      val msg = controller.callUpdatedMemberMsg(before, after)
      msg map { m ⇒
        m must contain("Field *Fee* has changed from 'EUR 100.00' to 'EUR 200.00'")
        m must contain("Field *Since* has changed from '2015-01-15' to '2015-01-01")
      } getOrElse ko
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
}