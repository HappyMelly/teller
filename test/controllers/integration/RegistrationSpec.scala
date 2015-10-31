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
import integration.{ PlayAppSpec, TruncateBefore }
import models.service.{ OrganisationService, PersonService }
import models.{ Member, Organisation, Person }
import org.joda.money.Money
import play.api.Play.current
import play.api.cache.Cache
import play.api.mvc.{ Cookie, Result }
import play.api.test.FakeRequest
import stubs.{ FakeRuntimeEnvironment, FakeUserIdentity, FakeSecurity }
import stubs.services.FakeIntegrations

import scala.concurrent.Future

class RegistrationSpec extends PlayAppSpec {

  class TestRegistration extends Registration(FakeRuntimeEnvironment)
      with FakeIntegrations
      with FakeSecurity {
    var notifyData: Option[(Person, Option[Organisation], Member)] = None

    def callPersonCacheId(userId: String): String = personCacheId(userId)

    override def subscribe(person: Person,
      org: Option[Organisation],
      data: PaymentData): String = "customerId"

    override def notify(person: Person,
      org: Option[Organisation],
      member: Member): Unit = {
      notifyData = Some((person, org, member))
    }
  }

  class AnotherTestRegistration extends Registration(FakeRuntimeEnvironment)
      with FakeIntegrations
      with FakeSecurity {

    def callNotify(person: Person,
      org: Option[Organisation],
      member: Member) =
      notify(person, org, member)
  }

  val controller = new TestRegistration

  "On step1 the system" should {
    "set cookie 'registration'=org if org parameter is true" in {
      val res = controller.step1(org = true).apply(fakeGetRequest())
      cookies(res).get("registration") map { _.value must_== "org" } getOrElse ko
    }
    "discard cookie 'registration' if org parameter is false" in {
      val cookie = Cookie(controller.REGISTRATION_COOKIE, "org")
      val res = controller.step1(org = false).apply(FakeRequest().withCookies(cookie))
      cookies(res).get("registration") map { _.maxAge.get must beLessThan(-1) } getOrElse ko
    }
  }
  "While saving a person the system" should {
    "put the person's data to cache" in {
      val identity = FakeUserIdentity.unregistered
      controller.identity_=(identity)
      val req = fakePostRequest().
        withFormUrlEncodedBody(("firstName", "First"),
          ("lastName", "Tester"), ("email", "tt@ttt.ru"), ("country", "RU"))
      val result = controller.savePerson().apply(req)

      headers(result).get("Location").get must contain("/registration/payment")
      val cacheId = controller.callPersonCacheId(identity._1)
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
      controller.identity_=(identity)
      val req = fakePostRequest().
        withFormUrlEncodedBody(("name", "One"), ("country", "RU"))
      val cacheId = controller.callPersonCacheId(identity._1)
      Cache.remove(cacheId)
      val result = controller.saveOrg().apply(req)
      headers(result).get("Location").get must contain("/registration/step2")
    }
    "put the org's data to cache" in {
      val identity = FakeUserIdentity.unregistered
      controller.identity_=(identity)
      val req = fakePostRequest().
        withFormUrlEncodedBody(("name", "One"), ("country", "RU"))
      val cacheId = controller.callPersonCacheId(identity._1)
      val userData = UserData("First", "Tester", "t@ttt.ru", "RU")
      Cache.set(cacheId, userData, 900)
      val result = controller.saveOrg().apply(req)

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
    controller.identity_=(identity)
    val cacheId = controller.callPersonCacheId(identity._1)
    val userData = UserData("First", "Member", "t@ttt.ru", "RU")
    "create a new person and make her a member" in new TruncateBefore {
      Cache.set(cacheId, userData, 900)
      val req = fakePostRequest().
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
      val req = fakePostRequest().
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
      val req = fakePostRequest().
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
      val req = fakePostRequest().
        withFormUrlEncodedBody(("token", "test"), ("fee", "50"))
      val res = controller.charge().apply(req)

      controller.notifyData map { data ⇒
        data._1.fullName must_== "First Member"
        data._2 must_== None
        data._3.funder must_== false
      } getOrElse ko
    }
    "send notifications when a new org becomes a member" in new TruncateBefore {
      val data = userData.copy(org = true, orgData = OrgData("OneMember", "DE"))
      Cache.set(cacheId, data, 900)
      val req = fakePostRequest().
        withFormUrlEncodedBody(("token", "test"), ("fee", "50"))
      val res = controller.charge().apply(req)

      controller.notifyData map { data ⇒
        data._1.fullName must_== "First Member"
        data._2 map { x ⇒
          x.name must_== "OneMember"
        } getOrElse ko
        data._3.funder must_== false
      } getOrElse ko
    }
  }
}
