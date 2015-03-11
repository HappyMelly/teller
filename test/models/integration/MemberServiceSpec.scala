/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing Happy Melly One, Handelsplein 37, Rotterdam,
 * The Netherlands, 3071 PR
 */
package models.integration

import helpers.{ MemberHelper, OrganisationHelper, PersonHelper }
import integration.PlayAppSpec
import models.Member
import models.service.MemberService
import org.joda.money.CurrencyUnit._
import org.joda.money.Money
import org.joda.time.{ DateTime, LocalDate }
import org.specs2.matcher.DataTables

class MemberServiceSpec extends PlayAppSpec with DataTables {
  def setupDb() {}
  def cleanupDb() {}

  "Method findAll" should {
    "return 6 members" in {
      addOrgsAndPeople()
      Seq(
        (1L, false, false, Money.of(EUR, 100), LocalDate.now(), 1L),
        (2L, false, true, Money.of(EUR, 200), LocalDate.now(), 1L),
        (3L, false, false, Money.of(EUR, 50), LocalDate.now(), 1L),
        (1L, true, false, Money.of(EUR, 50), LocalDate.now(), 1L),
        (2L, true, true, Money.of(EUR, 1000), LocalDate.now(), 1L),
        (5L, true, true, Money.of(EUR, 1000), LocalDate.now(), 1L)).foreach {
          case (objectId, person, funder, fee, since, createdBy) ⇒ {
            val member = new Member(None, objectId, person, funder, fee,
              renewal = false, since, since.plusYears(1),
              existingObject = false, DateTime.now(), createdBy, DateTime.now(),
              createdBy)
            member.insert
          }
        }
      val members = MemberService.get.findAll
      members.length must_== 6
      members.exists(_.name == "First org") must_== true
      members.exists(_.name == "Second org") must_== true
      members.exists(_.name == "Third org") must_== true
      members.exists(_.name == "First Tester") must_== true
      members.exists(_.name == "Second Tester") must_== true
      members.exists(_.name == "Fifth Tester") must_== true
    }
  }

  "Method find" should {
    "return empty data" in {
      truncateTables()
      val data = MemberService.get.find(2L)
      data must_== None
    }
    "return membership data with person object" in {
      truncateTables()
      val m = MemberHelper.make(None, 1L, person = true, funder = false).insert
      PersonHelper.one().insert
      val data = MemberService.get.find(1L)
      data map { v ⇒
        v.objectId must_== m.objectId
        v.person must_== m.person
        v.funder must_== m.funder
        v.name must_== "First Tester"
      } getOrElse ko
    }
    "return membership data with org object" in {
      truncateTables()
      val m = MemberHelper.make(None, 1L, person = false, funder = false).insert
      OrganisationHelper.one.insert
      val data = MemberService.get.find(1L)
      data map { v ⇒
        v.objectId must_== m.objectId
        v.person must_== m.person
        v.funder must_== m.funder
        v.name must_== "One"
      } getOrElse ko
    }
  }
  "Method `delete`" should {
    "delete membership data" in {
      truncateTables()
      val m = MemberHelper.make(None, 1L, person = false, funder = false).insert
      MemberService.get.delete(m.objectId, person = false)
      MemberService.get.find(m.id.get) must_== None
    }
    "not delete membership data" in {
      truncateTables()
      OrganisationHelper.one.insert
      val m = MemberHelper.make(None, 1L, person = false, funder = false).insert
      MemberService.get.delete(m.objectId, person = true)
      MemberService.get.find(m.id.get) must_!= None
    }
  }

  private def addOrgsAndPeople() = {
    Seq(
      (Some(1L), "First org", "DE"),
      (Some(2L), "Second org", "DE"),
      (Some(3L), "Third org", "DE"),
      (Some(4L), "Fourth org", "DE"),
      (Some(5L), "Fifth org", "DE"),
      (Some(6L), "Sixth org", "DE")).foreach {
        case (id, name, country) ⇒
          val org = OrganisationHelper.make(
            id = id,
            name = name,
            countryCode = country)
          org.insert
      }
    Seq(
      (Some(1L), "First", "Tester"),
      (Some(2L), "Second", "Tester"),
      (Some(3L), "Third", "Tester"),
      (Some(4L), "Fourth", "Tester"),
      (Some(5L), "Fifth", "Tester"),
      (Some(6L), "Sixth", "Tester")).foreach {
        case (id, firstName, lastName) ⇒
          val person = PersonHelper.make(id = id, firstName = firstName,
            lastName = lastName)
          person.insert
      }
  }
}
