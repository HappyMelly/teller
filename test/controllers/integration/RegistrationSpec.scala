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

import controllers.{ OrgData, PaymentData, Registration, UserData }
import helpers.{ OrganisationHelper, MemberHelper, PersonHelper }
import integration.{ TruncateBefore, PlayAppSpec }
import models.service.{ OrganisationService, PersonService }
import models.{ UserIdentity, Member, Organisation, Person }
import org.joda.money.Money
import play.api.Play.current
import play.api.cache.Cache
import play.api.mvc.{ Cookie, SimpleResult }
import play.api.test.FakeRequest
import securesocial.core.IdentityId
import stubs.FakeUserIdentity
import stubs.services.FakeNotifiers

import scala.concurrent.Future

class RegistrationSpec extends PlayAppSpec {

  class TestRegistration extends Registration with FakeNotifiers {
    var notifyData: Option[(Person, Option[Organisation], Money, Member)] = None

    def callPersonCacheId(id: IdentityId): String = personCacheId(id)

    override def subscribe(person: Person,
      org: Option[Organisation],
      data: PaymentData): String = "customerId"

    override def notify(person: Person,
      org: Option[Organisation],
      fee: Money,
      member: Member): Unit = {
      notifyData = Some((person, org, fee, member))
    }
  }

  class AnotherTestRegistration extends Registration with FakeNotifiers {

    def callNotify(person: Person,
      org: Option[Organisation],
      fee: Money,
      member: Member) =
      notify(person, org, fee, member)
  }

  val controller = new TestRegistration

  "On step1 the system" should {
    "set cookie 'registration'=org if org parameter is true" in {
      val res = controller.step1(org = true).apply(FakeRequest())
      cookies(res).get("registration") map { _.value must_== "org" } getOrElse ko
    }
    "discard cookie 'registration' if org parameter is false" in {
      val cookie = Cookie(Registration.REGISTRATION_COOKIE, "org")
      val res = controller.step1(org = false).apply(FakeRequest().withCookies(cookie))
      cookies(res).get("registration") map { _.maxAge.get must beLessThan(-1) } getOrElse ko
    }
  }
  "While saving a person the system" should {
    "put the person's data to cache" in {
      val identity = FakeUserIdentity.unregistered
      val req = prepareSecuredPostRequest(identity, "").
        withFormUrlEncodedBody(("firstName", "First"),
          ("lastName", "Tester"), ("email", "tt@ttt.ru"), ("country", "RU"))
      val result: Future[SimpleResult] = controller.savePerson().apply(req)
      status(result) must equalTo(SEE_OTHER)
      headers(result).get("Location").get must contain("/registration/payment")
      val cacheId = controller.callPersonCacheId(identity)
      Cache.getAs[UserData](cacheId) map { userData ⇒
        userData.firstName must_== "First"
        userData.lastName must_== "Tester"
        userData.email must_== "tt@ttt.ru"
        userData.country must_== "RU"
      } getOrElse ko
    }
  }

  "While saving an org the system" should {
    "redirect to Step 2 if a user data are not in the cache" in {
      val identity = FakeUserIdentity.unregistered
      val req = prepareSecuredPostRequest(identity, "").
        withFormUrlEncodedBody(("name", "One"), ("country", "RU"))
      val cacheId = controller.callPersonCacheId(identity)
      Cache.remove(cacheId)
      val result: Future[SimpleResult] = controller.saveOrg().apply(req)
      status(result) must equalTo(SEE_OTHER)
      headers(result).get("Location").get must contain("/registration/step2")
    }
    "put the org's data to cache" in {
      val identity = FakeUserIdentity.unregistered
      val req = prepareSecuredPostRequest(identity, "").
        withFormUrlEncodedBody(("name", "One"), ("country", "RU"))
      val cacheId = controller.callPersonCacheId(identity)
      val userData = UserData("First", "Tester", "t@ttt.ru", "RU")
      Cache.set(cacheId, userData, 900)
      val result: Future[SimpleResult] = controller.saveOrg().apply(req)
      status(result) must equalTo(SEE_OTHER)
      headers(result).get("Location").get must contain("/registration/payment")
      Cache.getAs[UserData](cacheId) map { userData ⇒
        userData.org must_== true
        userData.orgData.name must_== "One"
        userData.orgData.country must_== "RU"
      } getOrElse ko
    }
  }
  "On charging the system" should {
    val identity = FakeUserIdentity.unregistered
    val cacheId = controller.callPersonCacheId(identity)
    val userData = UserData("First", "Member", "t@ttt.ru", "RU")
    "create a new person and make her a member" in new TruncateBefore {
      Cache.set(cacheId, userData, 900)
      val req = prepareSecuredPostRequest(identity, "").
        withFormUrlEncodedBody(("token", "test"), ("fee", "50"))
      val res = controller.charge().apply(req)

      // here I assume that database was clean
      val person = PersonService.get.find(1L)
      person map { x ⇒
        x.lastName must_== "Member"
        x.customerId must_== Some("customerId")
        x.active must_== true
        val member = PersonService.get.member(x.id.get)
        member map { m ⇒
          m.funder must_== false
          m.fee.getAmountMajorInt must_== 50
        } getOrElse ko
      } getOrElse ko
    }
    "create a new org and make it a member" in new TruncateBefore {
      val data = userData.copy(org = true, orgData = OrgData("OneMember", "DE"))
      Cache.set(cacheId, data, 900)
      val req = prepareSecuredPostRequest(identity, "").
        withFormUrlEncodedBody(("token", "test"), ("fee", "50"))
      val res = controller.charge().apply(req)

      // here I assume that database was clean
      val org = OrganisationService.get.find(1L)
      org map { x ⇒
        x.name must_== "OneMember"
        x.customerId must_== Some("customerId")
        x.active must_== true
        val member = OrganisationService.get.member(x.id.get)
        member map { m ⇒
          m.funder must_== false
          m.fee.getAmountMajorInt must_== 50
        } getOrElse ko
      } getOrElse ko
    }
    "make a new person a member of new org" in new TruncateBefore {
      val data = userData.copy(org = true, orgData = OrgData("OneMember", "DE"))
      Cache.set(cacheId, data, 900)
      val req = prepareSecuredPostRequest(identity, "").
        withFormUrlEncodedBody(("token", "test"), ("fee", "50"))
      val res = controller.charge().apply(req)

      // here I assume that database was clean
      val org = OrganisationService.get.find(1L)
      org map { x ⇒
        x.people.length must_== 1
        x.people.exists(_.lastName == "Member") must_== true
      } getOrElse ko
    }

    "send notifications when a new person becomes a member" in new TruncateBefore {
      Cache.set(cacheId, userData, 900)
      val req = prepareSecuredPostRequest(identity, "").
        withFormUrlEncodedBody(("token", "test"), ("fee", "50"))
      val res = controller.charge().apply(req)

      controller.notifyData map { data ⇒
        data._1.fullName must_== "First Member"
        data._2 must_== None
        data._3.getAmountMajorInt must_== 50
        data._4.funder must_== false
      } getOrElse ko
    }
    "send notifications when a new org becomes a member" in new TruncateBefore {
      val data = userData.copy(org = true, orgData = OrgData("OneMember", "DE"))
      Cache.set(cacheId, data, 900)
      val req = prepareSecuredPostRequest(identity, "").
        withFormUrlEncodedBody(("token", "test"), ("fee", "50"))
      val res = controller.charge().apply(req)

      controller.notifyData map { data ⇒
        data._1.fullName must_== "First Member"
        data._2 map { x ⇒
          x.name must_== "OneMember"
        } getOrElse ko
        data._3.getAmountMajorInt must_== 50
        data._4.funder must_== false
      } getOrElse ko
    }
  }

  "Method 'notify'" should {
    "send Slack and Email notifications for a new person member" in {
      val person = PersonHelper.one()
      val fee = Money.parse("EUR 100")
      val member = MemberHelper.make(Some(1L), 1L, funder = false, person = true)
      val controller = new AnotherTestRegistration
      controller.callNotify(person, None, fee, member)

      controller.slack.message must contain("Hey @channel, we have *new Supporter*")
      controller.slack.message must contain("First Tester")
      controller.slack.message must contain("/person/1")
      controller.email.to.exists(_.lastName == "Tester") must_== true
      controller.email.cc must_== None
      controller.email.bcc must_== None
      controller.email.subject must_== "Welcome to Happy Melly network"
      controller.email.body must contain("Hi First,")
      controller.email.body must contain("Join Slack discussions")
      controller.email.body must contain(member.profileUrl)
    }
    "send Slack and Email notifications for a new organisation member" in {
      val person = PersonHelper.one()
      val fee = Money.parse("EUR 100")
      val org = OrganisationHelper.two
      val member = MemberHelper.make(Some(1L), 2L, funder = false, person = false)
      val controller = new AnotherTestRegistration
      controller.callNotify(person, Some(org), fee, member)

      controller.slack.message must contain("Hey @channel, we have *new Supporter*")
      controller.slack.message must contain("Two")
      controller.slack.message must contain("/organization/2")
      controller.email.to.exists(_.lastName == "Tester") must_== true
      controller.email.cc must_== None
      controller.email.bcc must_== None
      controller.email.subject must_== "Welcome to Happy Melly network"
      controller.email.body must contain("Hi Two,")
      controller.email.body must contain("Join Slack discussions")
      controller.email.body must contain(member.profileUrl)
    }
  }
}
