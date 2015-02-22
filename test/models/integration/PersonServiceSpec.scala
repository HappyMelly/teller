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

import helpers.{ MemberHelper, PersonHelper }
import integration.PlayAppSpec
import models.Member
import models.service.PersonService
import org.joda.money.CurrencyUnit._
import org.joda.money.Money
import org.joda.time.{ DateTime, LocalDate }
import org.specs2.matcher.DataTables

class PersonServiceSpec extends PlayAppSpec with DataTables {
  def setupDb(): Unit = {
    addPeople()
    add()
  }
  def cleanupDb() {}

  "Method findNonMembers" should {
    "return 4 non members" in {
      val people = PersonService.get.findNonMembers
      people.length must_== 4
      people.exists(_.id == Some(3L)) must_== true
      people.exists(_.id == Some(4L)) must_== true
      people.exists(_.id == Some(5L)) must_== true
      people.exists(_.id == Some(6L)) must_== true
    }

    "return 4 non members" in {
      MemberHelper.make(None, 3L, person = false, funder = true).insert
      val people = PersonService.get.findNonMembers

      people.length must_== 4
      people.exists(_.id == Some(3L)) must_== true
      people.exists(_.id == Some(4L)) must_== true
      people.exists(_.id == Some(5L)) must_== true
      people.exists(_.id == Some(6L)) must_== true
    }
  }

  "Method member" should {
    "return None if person is not a member" in {
      val r = PersonService.get.member(3L)
      r.nonEmpty must_== false
    }
    "return member data if person is a member" in {
      PersonService.get.member(1L) map { m ⇒
        m.person must_== true
        m.funder must_== false
        m.createdBy must_== 1L
      } getOrElse ko
    }
  }

  private def addPeople() = {
    Seq(
      (Some(1L), "First", "Tester"),
      (Some(2L), "Second", "Tester"),
      (Some(3L), "Third", "Tester"),
      (Some(4L), "Fourth", "Tester"),
      (Some(5L), "Firth", "Tester"),
      (Some(6L), "Sixth", "Tester")).foreach {
        case (id, firstName, lastName) ⇒
          val person = PersonHelper.make(id = id, firstName = firstName,
            lastName = lastName)
          person.insert
      }
  }
  private def add() = {
    Seq(
      (1L, false, false, Money.of(EUR, 100), LocalDate.now(), 1L),
      (2L, false, true, Money.of(EUR, 200), LocalDate.now(), 1L),
      (1L, true, false, Money.of(EUR, 50), LocalDate.now(), 1L),
      (2L, true, true, Money.of(EUR, 1000), LocalDate.now(), 1L)).foreach {
        case (objectId, person, funder, fee, since, createdBy) ⇒
          val member = new Member(None, objectId, person, funder, fee,
            subscription = false, since, since.plusYears(1),
            existingObject = false, DateTime.now(), createdBy, DateTime.now(),
            createdBy)
          member.insert
      }
  }
}
