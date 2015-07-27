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
import models.{ UserAccount, ProfileType, Member, Experience, Endorsement }
import models.payment.Record
import models.service.{ SocialProfileService, PaymentRecordService, PersonService, UserAccountService }
import org.joda.money.CurrencyUnit._
import org.joda.money.Money
import org.joda.time.{ DateTime, LocalDate }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

class PersonServiceSpec extends PlayAppSpec {

  override def setupDb(): Unit = {
    addPeople()
    add()

    val links = List(Experience(None, 1L, "video", "http://test.com"),
      Experience(None, 1L, "blog", "http://test1.com"))
    links.foreach(PersonService.get.insertExperience(_))
    val endorsements = List(Endorsement(None, 1L, "", "nikolai"),
      Endorsement(None, 1L, "", "viktor"))
    endorsements.foreach(PersonService.get.insertEndorsement(_))
  }

  val service = PersonService.get

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
  "Method endorsements" should {
    "return 2 endorsements for person with id = 1" in {
      val endorsements = service.endorsements(1L)
      endorsements.length must_== 2
      endorsements.exists(_.name == "nikolai") must_== true
      endorsements.exists(_.name == "viktor") must_== true
    }
    "return 0 endorsements for person with id = 2" in {
      val endorsements = service.endorsements(2L)
      endorsements.length must_== 0
    }
  }
  "Method deleteEndorsement" should {
    "delete endorsement with id = 1 from database" in {
      service.deleteEndorsement(1L, 1L)
      val endorsements = service.endorsements(1L)
      endorsements.length must_== 1
      endorsements.exists(_.name == "viktor") must_== true
    }
  }

  "Method experiences" should {
    "return 2 experiences for person with id = 1" in {
      val experiences = service.experiences(1L)
      experiences.length must_== 2
      experiences.exists(_.link == "http://test.com") must_== true
      experiences.exists(_.link == "http://test1.com") must_== true
    }
    "return 0 experiences for person with id = 2" in {
      val experiences = service.experiences(2L)
      experiences.length must_== 0
    }
  }
  "Method deleteExperience" should {
    "delete link with id = 1 from database" in {
      service.deleteExperience(1L, 1L)
      val experiences = service.experiences(1L)
      experiences.length must_== 1
      experiences.exists(_.link == "http://test1.com") must_== true
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

  "Method `delete`" should {
    "delete account, membership, social profile, address, evaluation data" in {
      //setup
      truncateTables()
      val id = 1L
      val person = PersonHelper.one().insert
      val member = MemberHelper.make(objectId = id,
        person = true,
        funder = false).insert
      Record("123", id, id, person = true, "desc", Money.parse("EUR 100")).insert
      Record("234", id, id, person = true, "desc", Money.parse("EUR 200")).insert
      UserAccountService.get.insert(UserAccount(None, id, "viewer", Some("test"), None, None, None))
      //test
      val addressId = person.address.id.get
      service.delete(id)

      //check
      DB.withSession { implicit session ⇒
        import models.database._
        TableQuery[Accounts].filter(_.personId === id).firstOption must_== None
        TableQuery[Addresses].filter(_.id === addressId).firstOption must_== None
        TableQuery[Members].filter(_.objectId === id).filter(_.person === true).firstOption must_== None
        TableQuery[PaymentRecords].filter(_.objectId === id).filter(_.person === true).list must_== List()
        // we can skip a type check here as there's only one record in database
        TableQuery[SocialProfiles].filter(_.objectId === id).firstOption must_== None
        TableQuery[UserAccounts].filter(_.personId === id).firstOption must_== None
      }
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
            renewal = false, since, since.plusYears(1),
            existingObject = false, None, DateTime.now(), createdBy,
            DateTime.now(), createdBy)
          member.insert
      }
  }
}
