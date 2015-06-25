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
package models.unit

import helpers.{ MemberHelper, PersonHelper }
import models._
import models.service.{ ProfileStrengthService, MemberService }
import org.joda.money.Money
import org.joda.time.{ DateTime, LocalDate }
import org.scalamock.specs2.MockContext
import org.specs2.mutable._
import stubs.FakeServices

class PersonMemberSpec extends Specification {

  class TestPerson(id: Option[Long],
    firstName: String,
    lastName: String) extends Person(id, firstName, lastName, None,
    Photo.empty, false, 1L, None, None, None, None, None, false, true,
    DateStamp(DateTime.now(), "", DateTime.now(), ""))
    with FakeServices {

    def callMembership(funder: Boolean, fee: Money): Member =
      membership(funder, fee)
  }

  "Given a person becomes a member" >> {
    val person = new TestPerson(Some(1L), "test", "tester")
    val fee = Money.parse("EUR 200")

    "the membership should be one year long" in {
      val member = person.callMembership(true, fee)
      member.until must_== LocalDate.now().plusYears(1)
      member.since must_== LocalDate.now()
    }
    "a member should be a person and a funder" in {
      val member = person.callMembership(true, fee)
      member.funder must_== true
      member.person must_== true
    }
    "the membership should be automatically renewed" in {
      val member = person.callMembership(true, fee)
      member.renewal must_== true
    }
    "a member should be a supporter and a membership fee equals 200 EUR" in {
      val member = person.callMembership(false, fee)
      member.fee must_== fee
    }
  }

  class AnotherTestPerson(id: Option[Long],
    firstName: String,
    lastName: String) extends Person(id, firstName, lastName, None,
    Photo.empty, false, 1L, None, None, None, None, None, false, true,
    DateStamp(DateTime.now(), "", DateTime.now(), ""))
    with FakeServices {

    /**
     * We need to override 'membership' function to remove ambiguity of
     * DateTime.now() returning values
     */
    override def membership(funder: Boolean, fee: Money): Member =
      new Member(None, id.get, person = true,
        funder = funder, fee = fee,
        renewal = true,
        since = LocalDate.now(),
        until = LocalDate.now().plusYears(1),
        existingObject = true, None,
        created = DateTime.parse("2015-01-01"), id.get,
        DateTime.parse("2015-01-01"), id.get)
  }

  "Given a person becomes a member" >> {
    val person = new AnotherTestPerson(Some(1L), "test", "tester")
    val fee = Money.parse("EUR 200")
    val member = person.membership(true, fee)
    "the membership data should be saved to database" in new MockContext {
      val memberService = mock[MemberService]
      val profileStrengthService = mock[ProfileStrengthService]
      (profileStrengthService.find _) expects (1L, false) returning None
      //the line of interest
      (memberService.insert _) expects member returning member
      person.memberService_=(memberService)
      person.profileStrengthService_=(profileStrengthService)
      person.becomeMember(true, fee)
      ok
    }
    "additional profile strength steps should be added" in new MockContext {
      val profileStength = ProfileStrength.empty(1L, false)
      val profileStrengthService = mock[ProfileStrengthService]
      val memberService = mock[MemberService]
      (memberService.insert _) expects member returning member
      (profileStrengthService.find _) expects (1L, false) returning Some(profileStength)
      //the line of interest
      (profileStrengthService.update _) expects ProfileStrength.forMember(profileStength)
      person.memberService_=(memberService)
      person.profileStrengthService_=(profileStrengthService)
      person.becomeMember(true, fee)
      ok
    }
  }
}
