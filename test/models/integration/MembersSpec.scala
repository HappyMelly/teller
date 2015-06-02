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
package models.integration

import java.math.RoundingMode

import controllers.{ Members, Security }
import helpers.{ OrganisationHelper, PersonHelper, MemberHelper }
import integration.PlayAppSpec
import models.service.{ OrganisationService, PersonService }
import models.{ Member, Organisation, Person }
import org.joda.money.{ CurrencyUnit, Money }
import org.joda.time.{ DateTime, LocalDate }
import org.scalamock.specs2.MockContext
import org.specs2.mutable.After
import play.api.Play.current
import play.api.cache.Cache
import play.api.db.slick._
import play.api.mvc.SimpleResult
import services.integrations.Slack
import stubs.{ FakeServices, FakeUserIdentity, FakeMemberService }
import stubs.services.{ FakeIntegrations, FakeSlack }

import scala.concurrent.Future
import scala.slick.jdbc.{ GetResult, StaticQuery ⇒ Q }
import scala.slick.session.Session

class TestMembers() extends Members
  with Security
  with FakeServices {

  val slackInstance = new FakeSlack
  var counter: Int = 0

  override def slack: Slack = {
    counter += 1
    slackInstance
  }
}

class MembersSpec extends PlayAppSpec {

  implicit val getMemberResult = GetResult(r ⇒
    Member(r.<<, r.<<, r.<<, r.<<,
      Money.of(CurrencyUnit.of(r.nextString()), r.nextBigDecimal().bigDecimal, RoundingMode.DOWN),
      r.<<, LocalDate.parse(r.nextString()), LocalDate.parse(r.nextString()),
      existingObject = false, Some(r.nextString()),
      DateTime.parse(r.nextString().replace(' ', 'T')), r.<<,
      DateTime.parse(r.nextString().replace(' ', 'T')), r.<<))

  trait cleanDb extends After {
    def after = DB.withSession { implicit session: Session ⇒
      truncateTables()
    }
  }

  val controller = new TestMembers()

  "While creating membership fee, a system" should {
    "reset objectId and id to 0/None to prevent cheating" in new cleanDb {
      val m = member()
      val fakeId = 400
      val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(("id", fakeId.toString),
          ("objectId", "3"),
          ("person", "1"), ("funder", "0"),
          ("fee.currency", m.fee.getCurrencyUnit.toString),
          ("fee.amount", m.fee.getAmountMajorLong.toString),
          ("since", m.since.toString), ("existingObject", "0"))
      val result: Future[SimpleResult] = controller.create().apply(req)

      status(result) must equalTo(SEE_OTHER)
      val insertedM = Cache.getAs[Member](Members.cacheId(1L))
      insertedM.nonEmpty must_== true
      insertedM.get.id must_!= fakeId
      insertedM.get.objectId must_== 0
    }
    "pass all fields from form to object" in new cleanDb {
      val fakeId = 400
      val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(("id", fakeId.toString),
          ("objectId", "3"),
          ("person", "1"), ("funder", "1"),
          ("fee.currency", "EUR"),
          ("fee.amount", "100"),
          ("since", "2015-01-15"), ("existingObject", "1"))
      val result: Future[SimpleResult] = controller.create().apply(req)

      status(result) must equalTo(SEE_OTHER)
      Cache.getAs[Member](Members.cacheId(1L)) map { m ⇒
        m.id must_== None
        m.objectId must_== 0
        m.existingObject must_== true
        m.person must_== true
        m.funder must_== true
        m.fee.getCurrencyUnit.getCode must_== "EUR"
        m.since.toString must_== "2015-01-15"
      } getOrElse failure
    }
  }

  "Incomplete member object" should {
    "be destroyed after successful creation of a person" in new cleanDb {
      val m = member()
      val oldList = Person.findAll
      val request = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(("emailAddress", "ttt@ttt.ru"),
          ("address.country", "RU"), ("firstName", "Test"),
          ("lastName", "Test"), ("signature", "false"),
          ("role", "0"))

      Cache.set(Members.cacheId(1L), m, 1800)
      controller.createNewPerson().apply(request)
      // test check
      Cache.getAs[Member](Members.cacheId(1L)).isEmpty must_== true
    }

    "be destroyed after successful creation of an organisation" in new cleanDb {
      val m = member()
      val oldList = Organisation.findAll
      val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(("name", "Test"), ("country", "RU"))

      Cache.set(Members.cacheId(1L), m, 1800)
      controller.createNewOrganisation().apply(req)
      // test check
      Cache.getAs[Member](Members.cacheId(1L)).isEmpty must_== true

      Organisation.findAll.diff(oldList).headOption map { o ⇒
        Organisation.delete(o.id.get); success
      } getOrElse failure
    }

  }

  """Incomplete member object can be created with one type of related object
    |(ex: person) and be connected to another type (ex: org).
    |
    |To keep system coherency attribute 'person' in a
    |member object should be updated""".stripMargin >> {

    "after the creation of related new organisation" in new cleanDb {
      val m = member(person = true)
      val oldList = Organisation.findAll
      val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(("name", "Test"), ("country", "RU"))
      Cache.set(Members.cacheId(1L), m, 1800)
      val result = controller.createNewOrganisation().apply(req)

      status(result) must equalTo(SEE_OTHER)

      Organisation.findAll.diff(oldList).headOption map { org ⇒
        retrieveMember(org.id.get.toString) map { upd ⇒
          upd.person must_== false
        } getOrElse failure

        // clean up. We don't need this organisation anymore
        Organisation.delete(org.id.get)
        success
      } getOrElse failure
    }
    "after the creation of related new person" in new cleanDb {
      val m = member(person = false, existingObject = true)
      val oldList = Person.findAll
      val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(("emailAddress", "ttt@ttt.ru"),
          ("address.country", "RU"), ("firstName", "Test"),
          ("lastName", "Test"), ("signature", "false"),
          ("role", "0"))
      Cache.set(Members.cacheId(1L), m, 1800)
      val result = controller.createNewPerson().apply(req)

      status(result) must equalTo(SEE_OTHER)

      Person.findAll.diff(oldList).headOption map { person ⇒
        retrieveMember(person.id.toString) map { upd ⇒
          upd.person must_== true
        } getOrElse failure
      } getOrElse failure
    }
  }

  "The organisation created on step 2" should {
    "be connected with member data" in new cleanDb {
      val m = member()
      val oldList = Organisation.findAll
      val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(("name", "Test"), ("country", "RU"))

      Cache.set(Members.cacheId(1L), m, 1800)
      val result = controller.createNewOrganisation().apply(req)
      status(result) must equalTo(SEE_OTHER)
      headers(result).get("Location").nonEmpty must_== true
      headers(result).get("Location").get must contain("/organization")

      Organisation.findAll.diff(oldList).headOption map { org ⇒
        val updatedM = retrieveMember(org.id.get.toString)
        updatedM.nonEmpty must_== true
        updatedM.get.objectId must_== org.id.get

        // clean up. We don't need this organisation anymore
        Organisation.delete(org.id.get)
        success
      } getOrElse failure

    }
  }

  "The person created on step 2" should {
    "be connected with member data" in new cleanDb {
      val m = member()
      val oldList = Person.findAll
      val request = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(("emailAddress", "ttt@ttt.ru"),
          ("address.country", "RU"), ("firstName", "Test"),
          ("lastName", "Test"), ("signature", "false"),
          ("role", "0"))

      Cache.set(Members.cacheId(1L), m, 1800)
      val result = controller.createNewPerson().apply(request)
      status(result) must equalTo(SEE_OTHER)
      headers(result).get("Location").nonEmpty must_== true
      headers(result).get("Location").get must contain("/person")

      Person.findAll.diff(oldList).headOption map { person ⇒
        val updatedM = retrieveMember(person.id.toString)
        updatedM.nonEmpty must_== true
        updatedM.get.objectId must_== person.id
      } getOrElse failure
    }
  }

  "On step 2 an existing organisation " should {
    "be linked to a member object" in new cleanDb {
      val m = member(person = false)
      val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(("id", "1"))
      val org = OrganisationHelper.one.copy(id = Some(1L)).insert
      Cache.set(Members.cacheId(1L), m, 1800)
      OrganisationService.get.find(1L) map { o ⇒
        o.member must_== None
      } getOrElse failure
      val result = controller.updateExistingOrg().apply(req)
      status(result) must equalTo(SEE_OTHER)

      OrganisationService.get.find(1L) map { o ⇒
        o.member.nonEmpty must_== true
      } getOrElse failure
    }
  }

  "On step 2 an existing person" should {
    "be linked to a member object" in new cleanDb {
      truncateTables()
      val m = member(person = true)
      val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(("id", "1"))
      val person = PersonHelper.one().insert
      Cache.set(Members.cacheId(1L), m, 1800)
      PersonService.get.find(1L) map { p ⇒
        p.member must_== None
      } getOrElse failure
      val result = controller.updateExistingPerson().apply(req)
      status(result) must equalTo(SEE_OTHER)

      PersonService.get.find(1L) map { p ⇒
        p.member.nonEmpty must_== true
      } getOrElse failure
    }
  }

  "Slack notification should be sent" >> {
    "when a new organisation becomes a member" in {
      truncateTables()
      val m = member(person = false)
      val oldList = Organisation.findAll
      val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(("name", "Test"), ("country", "RU"))
      Cache.set(Members.cacheId(1L), m, 1800)

      controller.counter = 0
      val result = controller.createNewOrganisation().apply(req)
      status(result) must equalTo(SEE_OTHER)
      controller.counter must_== 1
      controller.slackInstance.message must contain("Test")
    }
    "when a new person becomes a member" in {
      val m = member().copy(funder = true)
      val oldList = Person.findAll
      val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(("emailAddress", "ttt@ttt.ru"),
          ("address.country", "RU"), ("firstName", "Test"),
          ("lastName", "Buddy"), ("signature", "false"),
          ("role", "0"))
      Cache.set(Members.cacheId(1L), m, 1800)
      controller.counter = 0
      val result = controller.createNewPerson().apply(req)
      status(result) must equalTo(SEE_OTHER)
      controller.counter must_== 1
      controller.slackInstance.message must contain("Test Buddy")
    }
    "when an existing organisation becomes a member" in {
      truncateTables()
      val m = member(person = false)
      val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(("id", "1"))
      val org = OrganisationHelper.one.copy(id = Some(1L)).insert
      Cache.set(Members.cacheId(1L), m, 1800)
      OrganisationService.get.find(1L) map { o ⇒
        o.member must_== None
      } getOrElse failure
      controller.counter = 0
      val result = controller.updateExistingOrg().apply(req)
      status(result) must equalTo(SEE_OTHER)
      headers(result).get("Location").get must contain("/organization/1")
      controller.counter must_== 1
      controller.slackInstance.message must contain("One")
    }
    "when an existing person becomes a member" in {
      truncateTables()
      val m = member(person = true)
      val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(("id", "1"))
      val person = PersonHelper.one().insert
      Cache.set(Members.cacheId(1L), m, 1800)
      PersonService.get.find(1L) map { p ⇒
        p.member must_== None
      } getOrElse failure
      controller.counter = 0
      val result = controller.updateExistingPerson().apply(req)
      status(result) must equalTo(SEE_OTHER)
      headers(result).get("Location").get must contain("/person/1")
      controller.counter must_== 1
      controller.slackInstance.message must contain("First Tester")
    }
    "membership is revoked" in new MockContext {
      val memberService = mock[FakeMemberService]
      val person = PersonHelper.one()
      val member = MemberHelper.make(Some(2L), 1L, person = true, funder = false)
      member.memberObj_=(person)
      (memberService.find _).expects(2L).returning(Some(member))
      (memberService.delete(_, _)).expects(1L, true)
      controller.memberService_=(memberService)
      controller.counter = 0
      val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/")
      val result = controller.delete(2L).apply(req)
      status(result) must equalTo(SEE_OTHER)
      controller.counter must_== 1
      controller.slackInstance.message must contain("First Tester")
    }
    "when membership is changed" in new MockContext {
      val memberService = mock[FakeMemberService]
      val person = PersonHelper.one()
      val member = MemberHelper.make(Some(2L), 1L, person = true, funder = false).insert
      member.memberObj_=(person)
      (memberService.find _).expects(2L).returning(Some(member))
      val controller = new TestMembers
      controller.memberService_=(memberService)
      controller.counter = 0
      val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(
          ("objectId", member.objectId.toString), ("person", "1"),
          ("funder", "1"), ("fee.currency", "EUR"),
          ("fee.amount", "200"), ("subscription", member.renewal.toString),
          ("since", "2015-01-01"), ("end", member.until.toString),
          ("existingObject", "1"))
      val result = controller.update(2L).apply(req)
      status(result) must equalTo(SEE_OTHER)
      controller.counter must_== 1
      controller.slackInstance.message must contain("First Tester")
    }
  }

  /**
   * Retrieves member from database if it exists
   * @param id Id of related object
   * @return Member or None
   */
  private def retrieveMember(id: String) = DB.withSession {
    implicit session: Session ⇒
      val q = Q.queryNA[Member]("SELECT * FROM MEMBER WHERE OBJECT_ID = " + id)
      q.firstOption
  }

  private def member(person: Boolean = true, existingObject: Boolean = false): Member = {
    new Member(None, 0, person = person, funder = false,
      Money.parse("EUR 100"), renewal = false, LocalDate.now(), LocalDate.now(),
      existingObject = existingObject, None, DateTime.now(), 1L, DateTime.now(), 1L)
  }
}
