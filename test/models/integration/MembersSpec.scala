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

import controllers.Members
import helpers.{ OrganisationHelper, PersonHelper, MemberHelper }
import integration.PlayAppSpec
import models.service.{ OrganisationService, PersonService }
import models._
import org.joda.money.{ CurrencyUnit, Money }
import org.joda.time.{ DateTime, LocalDate }
import org.scalamock.specs2.{ MockContext, IsolatedMockFactory }
import play.api.Play.current
import play.api.cache.Cache
import services.integrations.{ Slack, Email }
import stubs._
import stubs.services.{ FakeIntegrations, FakeSlack, FakeEmail }

class TestMembers() extends Members
  with FakeSecurity
  with FakeServices {

  val slackInstance = new FakeSlack
  val emailInstance = new FakeEmail
  var counter: Int = 0

  override def slack: Slack = {
    counter += 1
    slackInstance
  }

  override def email: Email = emailInstance

  override protected def subscribe(person: Person, member: Member) = true
}

class MembersSpec extends PlayAppSpec {

  val controller = new TestMembers()
  val person = PersonHelper.one
  val org = OrganisationHelper.one
  val profile = SocialProfile(0, ProfileType.Organisation, "")

  trait WithStubs extends MockContext {
    val personService = mock[PersonService]
    val memberService = mock[FakeMemberService]
    val orgService = mock[OrganisationService]
    controller.orgService_=(orgService)
    controller.personService_=(personService)
    controller.memberService_=(memberService)
  }

  "While creating membership fee, a system" should {
    "reset objectId and id to 0/None to prevent cheating" in {
      val m = member()
      val fakeId = 400
      val req = fakePostRequest().
        withFormUrlEncodedBody(("id", fakeId.toString),
          ("objectId", "3"),
          ("person", "1"), ("funder", "0"),
          ("fee.currency", m.fee.getCurrencyUnit.toString),
          ("fee.amount", m.fee.getAmountMajorLong.toString),
          ("since", m.since.toString), ("existingObject", "0"))
      controller.create().apply(req)

      val inserted = Cache.getAs[Member](Members.cacheId(1L))
      inserted.nonEmpty must_== true
      inserted.get.id must_!= fakeId
      inserted.get.objectId must_== 0
    }
    "pass all fields from form to object" in {
      val fakeId = 400
      val req = fakePostRequest().
        withFormUrlEncodedBody(("id", fakeId.toString),
          ("objectId", "3"),
          ("person", "1"), ("funder", "1"),
          ("fee.currency", "EUR"),
          ("fee.amount", "100"),
          ("since", "2015-01-15"), ("existingObject", "1"))
      controller.create().apply(req)

      Cache.getAs[Member](Members.cacheId(1L)) map { m ⇒
        m.id must_== None
        m.objectId must_== 0
        m.existingObject must_== true
        m.person must_== true
        m.funder must_== true
        m.fee.getCurrencyUnit.getCode must_== "EUR"
        m.since.toString must_== "2015-01-15"
      } getOrElse ko
    }
  }

  "Incomplete member object" should {
    "be destroyed after successful creation of a person" in new WithStubs {
      val request = fakePostRequest().
        withFormUrlEncodedBody(("emailAddress", "ttt@test.ru"),
          ("address.country", "GB"), ("firstName", "Test"),
          ("lastName", "Test"), ("signature", "false"),
          ("role", "0"))
      (personService.insert _) expects * returning PersonHelper.one
      val m = member().copy(objectId = 1L)
      (memberService.insert _) expects m returning m.copy(id = Some(1L))
      Cache.set(Members.cacheId(1L), m, 1800)
      controller.createNewPerson().apply(request)

      Cache.getAs[Member](Members.cacheId(1L)).isEmpty must_== true
    }

    "be destroyed after successful creation of an organisation" in new WithStubs {
      val m = member()
      val req = fakePostRequest().
        withFormUrlEncodedBody(("name", "Test"), ("address.country", "RU"),
          ("profile.email", "test@test.ru"))

      (orgService.insert _) expects * returning OrgView(org, profile)
      val updated = m.copy(objectId = 1, person = false)
      (memberService.insert _) expects * returning updated
      Cache.set(Members.cacheId(1L), m, 1800)

      controller.createNewOrganisation().apply(req)
      // test check
      Cache.getAs[Member](Members.cacheId(1L)).isEmpty must_== true
    }

  }

  """Incomplete member object can be created with one type of related object
    (ex: person) and be connected to another type (ex: org).

    To keep system coherency attribute 'person' in a
    member object should be updated""".stripMargin >> {

    "after the creation of related new organisation" in new WithStubs {
      val m = member(person = true)
      val req = fakePostRequest().
        withFormUrlEncodedBody(("name", "Test"), ("address.country", "RU"),
          ("profile.email", "test@test.ru"))
      Cache.set(Members.cacheId(1L), m, 1800)
      (orgService.insert _) expects * returning OrgView(org, profile)
      val updated = m.copy(objectId = 1, person = false)
      //test line
      (memberService.insert _) expects updated returning updated

      controller.createNewOrganisation().apply(req)
      ok
    }
    "after the creation of related new person" in new WithStubs {
      val m = member(person = false, existingObject = true)

      val req = prepareSecuredPostRequest(FakeUserIdentity.editor, "/").
        withFormUrlEncodedBody(("emailAddress", "ttt@ttt.ru"),
          ("address.country", "RU"), ("firstName", "Test"),
          ("lastName", "Test"), ("signature", "false"),
          ("role", "0"))
      Cache.set(Members.cacheId(1L), m, 1800)
      (personService.insert _) expects * returning person
      val updated = m.copy(person = true, objectId = 1)
      //test line
      (memberService.insert _) expects updated returning updated.copy(id = Some(1L))
      controller.createNewPerson().apply(req)
      ok
    }
  }

  "On step 2 an existing organisation " should {
    "be linked to a member object" in new WithStubs {
      (orgService.find _) expects 1L returning Some(org)
      val m = member(person = false).copy(objectId = 1L)
      //test line
      (memberService.insert _) expects * returning m.copy(id = Some(1L))
      val req = fakePostRequest().withFormUrlEncodedBody(("id", "1"))
      Cache.set(Members.cacheId(1L), m, 1800)

      controller.updateExistingOrg().apply(req)
      ok
    }
  }

  "On step 2 an existing person" should {
    "be linked to a member object" in new WithStubs {
      (personService.find(_: Long)) expects 1L returning Some(person)
      val m = member(person = true).copy(objectId = 1L)
      Cache.set(Members.cacheId(1L), m, 1800)
      //test line
      (memberService.insert _) expects m returning m.copy(id = Some(1L))

      val req = fakePostRequest().withFormUrlEncodedBody(("id", "1"))
      controller.updateExistingPerson().apply(req)
      ok
    }
  }

  "Slack notification should be sent" >> {
    "when a new organisation becomes a member" in new WithStubs {
      val org = OrganisationHelper.make(id = Some(1L), name = "Test")
      (orgService.insert _) expects * returning OrgView(org, profile)
      val m = member(person = false)
      (memberService.insert _) expects * returning m.copy(id = Some(1L))
      val req = fakePostRequest().
        withFormUrlEncodedBody(("name", "Test"), ("address.country", "RU"),
          ("profile.email", "test@test.com"))
      Cache.set(Members.cacheId(1L), m, 1800)
      controller.counter = 0
      controller.createNewOrganisation().apply(req)

      controller.counter must_== 1
      controller.slackInstance.message must contain("Test")
    }
    "when a new person becomes a member" in new WithStubs {
      (personService.insert _) expects * returning person
      val m = member().copy(funder = true)
      (memberService.insert _) expects * returning m.copy(id = Some(1L))
      Cache.set(Members.cacheId(1L), m, 1800)
      controller.counter = 0
      val req = fakePostRequest().
        withFormUrlEncodedBody(("emailAddress", "ttt@ttt.ru"),
          ("address.country", "RU"), ("firstName", "First"),
          ("lastName", "Tester"), ("signature", "false"),
          ("role", "0"))
      controller.createNewPerson().apply(req)

      controller.counter must_== 1
      controller.slackInstance.message must contain("First Tester")
    }
    "when an existing organisation becomes a member" in new WithStubs {
      (orgService.find _) expects 1L returning Some(org)
      val m = member(person = false).copy(id = Some(1L))
      (memberService.insert _) expects * returning m
      val req = fakePostRequest().withFormUrlEncodedBody(("id", "1"))
      Cache.set(Members.cacheId(1L), m, 1800)
      controller.counter = 0

      controller.updateExistingOrg().apply(req)

      // headers(result).get("Location").get must contain("/organization/1")
      controller.counter must_== 1
      controller.slackInstance.message must contain("One")
    }
    "when an existing person becomes a member" in new WithStubs {
      (personService.find(_: Long)) expects 1L returning Some(person)
      val m = member(person = true)
      (memberService.insert _) expects * returning m.copy(id = Some(1L))
      Cache.set(Members.cacheId(1L), m, 1800)
      controller.counter = 0

      val request = fakePostRequest().withFormUrlEncodedBody(("id", "1"))
      val result = controller.updateExistingPerson().apply(request)

      controller.counter must_== 1
      controller.slackInstance.message must contain("First Tester")
    }
    "membership is revoked" in new WithStubs {
      val member = MemberHelper.make(Some(2L), 1L, person = true, funder = false)
      member.memberObj_=(person)
      (memberService.find _) expects 2L returning Some(member)
      (memberService.delete _) expects (1L, true)
      controller.counter = 0
      controller.delete(2L).apply(fakePostRequest())

      controller.counter must_== 1
      controller.slackInstance.message must contain("First Tester")
    }
    "when membership is changed" in new WithStubs {
      val member = MemberHelper.make(Some(2L), 1L, person = true, funder = false)
      member.memberObj_=(person)
      (memberService.find _) expects 2L returning Some(member)
      (memberService.update _) expects *
      // (memberService.update _) expects (where {
      //   (m: Member) ⇒ m.funder == true && m.since.toString == "2015-01-01"
      // })
      controller.counter = 0
      val req = fakePostRequest().
        withFormUrlEncodedBody(
          ("objectId", member.objectId.toString), ("person", "1"),
          ("funder", "1"), ("fee.currency", "EUR"),
          ("fee.amount", "100"), ("subscription", member.renewal.toString),
          ("since", "2015-01-01"), ("end", member.until.toString),
          ("existingObject", "1"))
      controller.update(2L).apply(req)

      controller.counter must_== 1
      controller.slackInstance.message must contain("First Tester")
    }
  }

  private def member(person: Boolean = true, existingObject: Boolean = false): Member = {
    new Member(None, 0, person = person, funder = false,
      Money.parse("EUR 100"), renewal = false, LocalDate.now(), LocalDate.now(),
      existingObject = existingObject, None, DateTime.now(), 1L, DateTime.now(), 1L)
  }
}
