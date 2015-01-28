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
package integration

import java.math.RoundingMode

import controllers.{ Security, Members }
import models.{ Person, Organisation, Member }
import org.joda.money.{ CurrencyUnit, Money }
import org.joda.time.{ DateTime, LocalDate }
import org.specs2.mutable.After
import play.api.cache.Cache
import play.api.db.slick._
import play.api.mvc.SimpleResult
import play.api.Play.current
import stubs.{ StubLoginIdentity, FakeServices }

import scala.concurrent.Future
import scala.slick.jdbc.{ StaticQuery ⇒ Q, GetResult }
import scala.slick.session.Session

class TestMembers() extends Members with Security with FakeServices

class MembersSpec extends PlayAppSpec {
  def setupDb() {}
  def cleanupDb() {}

  "While creating membership fee, objectId and id are unknown and a system" should {
    "reset them to None to prevent cheating" in new cleanDb {
      val controller = new TestMembers()
      val identity = StubLoginIdentity.editor
      // here we have resetted values
      val m = new Member(None, None, person = true, funder = false,
        Money.parse("EUR 100"), LocalDate.now(), existingObject = false,
        DateTime.now(), 1L, DateTime.now(), 1L)
      val fakeId = 400
      val request = prepareSecuredPostRequest(identity, "/member/new").
        withFormUrlEncodedBody(("id", fakeId.toString),
          ("objectId", "3"),
          ("person", "1"), ("funder", m.funder.toString),
          ("fee.currency", m.fee.getCurrencyUnit.toString),
          ("fee.amount", m.fee.getAmountMajorLong.toString),
          ("since", m.since.toString))
      val result: Future[SimpleResult] = controller.create().apply(request)
      status(result) must equalTo(SEE_OTHER)
      //@TODO redirection check
      val insertedM = Cache.getAs[Member](controller.cacheId(1L))
      insertedM.nonEmpty must_== true
      insertedM.get.id must_!= fakeId
    }
  }

  "Incomplete member object" should {
    "be destroyed after successful creation of a person" in new cleanDb {
      val m = new Member(None, None, person = true, funder = false,
        Money.parse("EUR 100"), LocalDate.now(), existingObject = false,
        DateTime.now(), 1L, DateTime.now(), 1L)
      val controller = new TestMembers()
      val oldList = Person.findAll
      val identity = StubLoginIdentity.editor
      val request = prepareSecuredPostRequest(identity, "/member/member").
        withFormUrlEncodedBody(("emailAddress", "ttt@ttt.ru"),
          ("address.country", "RU"), ("firstName", "Test"),
          ("lastName", "Test"), ("signature", "false"),
          ("role", "0"))

      Cache.set(controller.cacheId(1L), m, 1800)
      controller.createNewPerson().apply(request)
      // test check
      Cache.getAs[Member](controller.cacheId(1L)).isEmpty must_== true

      //this is a little bit ugly but there's no other way for now to track
      // new person
      val person = Person.findAll.diff(oldList).headOption
      person map { o ⇒ Person.delete(o.id); success } getOrElse failure
    }

    "be destroyed after successful creation of an organisation" in new cleanDb {
      val m = new Member(None, None, person = false, funder = false,
        Money.parse("EUR 100"), LocalDate.now(), existingObject = false,
        DateTime.now(), 1L, DateTime.now(), 1L)
      val controller = new TestMembers()
      val oldList = Organisation.findAll
      val identity = StubLoginIdentity.editor
      val request = prepareSecuredPostRequest(identity, "/member/organisation").
        withFormUrlEncodedBody(("name", "Test"), ("country", "RU"))

      Cache.set(controller.cacheId(1L), m, 1800)
      controller.createNewOrganisation().apply(request)
      // test check
      Cache.getAs[Member](controller.cacheId(1L)).isEmpty must_== true

      //this is a little bit ugly but there's no other way for now to track
      // new organisations
      val org = Organisation.findAll.diff(oldList).headOption
      org map { o ⇒ Organisation.delete(o.id.get); success } getOrElse failure
    }

  }

  "The organisation created on step 2" should {
    "be connected with member data" in new cleanDb {
      val m = new Member(None, None, person = false, funder = false,
        Money.parse("EUR 100"), LocalDate.now(), existingObject = false,
        DateTime.now(), 1L, DateTime.now(), 1L)
      val controller = new TestMembers()
      val oldList = Organisation.findAll
      val identity = StubLoginIdentity.editor
      val request = prepareSecuredPostRequest(identity, "/member/organisation").
        withFormUrlEncodedBody(("name", "Test"), ("country", "RU"))

      Cache.set(controller.cacheId(1L), m, 1800)
      val result = controller.createNewOrganisation().apply(request)
      status(result) must equalTo(SEE_OTHER)
      //@TODO this should be moved to acceptance module
      headers(result).get("Location").nonEmpty must_== true
      headers(result).get("Location").get must_== "/members"

      //this is a little bit ugly but there's no other way for now to track
      // new organisations
      val newList = Organisation.findAll.diff(oldList)
      newList.length must_== 1
      val org = newList.head

      DB.withSession { implicit session: Session ⇒
        implicit val getMemberResult = GetResult(r ⇒
          Member(r.<<, r.<<, r.<<, r.<<,
            Money.of(CurrencyUnit.of(r.nextString()), r.nextBigDecimal().bigDecimal, RoundingMode.DOWN),
            LocalDate.parse(r.nextString()), false,
            DateTime.parse(r.nextString().replace(' ', 'T')), r.<<,
            DateTime.parse(r.nextString().replace(' ', 'T')), r.<<))
        val q = Q.queryNA[Member]("SELECT * FROM member WHERE OBJECT_ID = " + org.id.get.toString)
        val updatedM = q.firstOption
        updatedM.nonEmpty must_== true
        updatedM.get.objectId.nonEmpty must_== true
        updatedM.get.objectId must_== org.id
      }

      // clean up. We don't need this organisation anymore
      Organisation.delete(org.id.get)
      ok
    }
  }

  "The person created on step 2" should {
    "be connected with member data" in new cleanDb {
      val m = new Member(None, None, person = true, funder = true,
        Money.parse("EUR 100"), LocalDate.now(), existingObject = false,
        DateTime.now(), 1L, DateTime.now(), 1L)
      val oldList = Person.findAll
      val identity = StubLoginIdentity.editor
      val controller = new TestMembers()
      val request = prepareSecuredPostRequest(identity, "/member/member").
        withFormUrlEncodedBody(("emailAddress", "ttt@ttt.ru"),
          ("address.country", "RU"), ("firstName", "Test"),
          ("lastName", "Test"), ("signature", "false"),
          ("role", "0"))

      Cache.set(controller.cacheId(1L), m, 1800)
      val result = controller.createNewPerson().apply(request)
      status(result) must equalTo(SEE_OTHER)
      //@TODO this should be moved to acceptance module
      headers(result).get("Location").nonEmpty must_== true
      headers(result).get("Location").get must_== "/members"

      //this is a little bit ugly but there's no other way for now to track
      // new person
      val newList = Person.findAll.diff(oldList)
      newList.length must_== 1
      val person = newList.head

      DB.withSession { implicit session: Session ⇒
        implicit val getMemberResult = GetResult(r ⇒
          Member(r.<<, r.<<, r.<<, r.<<,
            Money.of(CurrencyUnit.of(r.nextString()), r.nextBigDecimal().bigDecimal, RoundingMode.DOWN),
            LocalDate.parse(r.nextString()), false,
            DateTime.parse(r.nextString().replace(' ', 'T')), r.<<,
            DateTime.parse(r.nextString().replace(' ', 'T')), r.<<))
        val q = Q.queryNA[Member]("SELECT * FROM member WHERE OBJECT_ID = " + person.id.toString)
        val updatedM = q.firstOption
        updatedM.nonEmpty must_== true
        updatedM.get.objectId.nonEmpty must_== true
        updatedM.get.objectId.get must_== person.id
      }

      // clean up. We don't need this person anymore
      Person.delete(person.id)
      ok
    }
  }
}

trait cleanDb extends After {
  def after = DB.withSession { implicit session: Session ⇒
    Q.updateNA("TRUNCATE `MEMBER`").execute
  }
}
