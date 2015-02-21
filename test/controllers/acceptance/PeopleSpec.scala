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
package controllers.acceptance

import controllers.{ People, Security }
import helpers.{ MemberHelper, OrganisationHelper, PersonHelper }
import integration.PlayAppSpec
import models._
import org.joda.money.Money
import org.joda.time.{ LocalDate, DateTime }
import org.scalamock.specs2.MockContext
import play.api.mvc.SimpleResult
import play.api.test.FakeRequest
import stubs._

import scala.concurrent.Future

class TestPeople() extends People with Security with FakeServices

class PeopleSpec extends PlayAppSpec {
  def setupDb() {}
  def cleanupDb() {}

  override def is = s2"""

  Page with person's data should

    not be visible to unauthorized user                                  $e1
    and be visible to authorized user                                    $e2
    not contain accounting details if user is not Editor                 $e3
    contain a supporter badge if the person is a supporter               $e5
    contain a funder badge if the person is a funder                     $e6
    contain 'Make a Facilitator' button if user is Editor and a person has none $e7
    contain a list of payments if the person is a member                 $e8

  Editor should
    not see links to remote payments                                     $e9
  """

  def e1 = {
    val controller = new TestPeople()
    val result: Future[SimpleResult] = controller.details(1).apply(FakeRequest())

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome.which(_.contains("login"))
  }

  def e2 = {
    new MockContext {
      val controller = new TestPeople()
      val mockService = mock[FakePersonService]
      // if this method is called it means we have passed a security check
      (mockService.find(_: Long)) expects 1L returning None
      controller.personService_=(mockService)
      val identity = StubUserIdentity.viewer
      val request = prepareSecuredGetRequest(identity, "/person/1")
      controller.details(1).apply(request)
    }
  }

  def e3 = {
    new MockContext {
      val person = PersonHelper.one()
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val controller = new TestPeople()
      val mockService = mock[FakePersonService]
      (mockService.find(_: Long)) expects 1L returning Some(person)
      controller.personService_=(mockService)
      val identity = StubUserIdentity.viewer
      val request = prepareSecuredGetRequest(identity, "/person/1")
      val result: Future[SimpleResult] = controller.details(person.id.get).apply(request)

      status(result) must equalTo(OK)
      contentAsString(result) must not contain "Financial account"
      contentAsString(result) must not contain "Account history"
    }
  }

  def e5 = {
    truncateTables()
    new MockContext {
      val person = PersonHelper.one().insert
      val id = 1L
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val member = MemberHelper.make(Some(1L), id, person = true, funder = false)
      person.member_=(member)

      val controller = new TestPeople()
      val personService = mock[FakePersonService]
      (personService.find(_: Long)) expects id returning Some(person)
      controller.personService_=(personService)
      val orgService = mock[FakeOrganisationService]
      orgService.findActive _ expects () returning List()
      controller.orgService_=(orgService)
      val accountService = mock[FakeUserAccountService]
      (accountService.findRole _).expects(id).returning(None).never()
      (accountService.findDuplicateIdentity _).expects(person).returning(None).never()
      controller.userAccountService_=(accountService)
      val contributionService = mock[FakeContributionService]
      (contributionService.contributions(_, _)) expects (id, true) returning List()
      controller.contributionService_=(contributionService)
      val paymentService = mock[FakePaymentRecordService]
      (paymentService.findByPerson _) expects id returning List()
      controller.paymentRecordService_=(paymentService)

      val identity = StubUserIdentity.viewer
      val request = prepareSecuredGetRequest(identity, "/person/1")
      val result: Future[SimpleResult] = controller.details(person.id.get).apply(request)

      status(result) must equalTo(OK)
      contentAsString(result) must contain("Supporter")
      contentAsString(result) must not contain "/member/1/edit"
    }
  }

  def e6 = {
    truncateTables()
    new MockContext {
      // we insert a person object here to prevent crashing on account retrieval
      // when @person.deletable is called
      val person = PersonHelper.one().insert
      val id = 1L
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
      person.member_=(member)

      val controller = new TestPeople()
      val personService = mock[FakePersonService]
      (personService.find(_: Long)) expects id returning Some(person)
      controller.personService_=(personService)
      val orgService = mock[FakeOrganisationService]
      orgService.findActive _ expects () returning List()
      controller.orgService_=(orgService)
      val accountService = mock[FakeUserAccountService]
      (accountService.findRole _) expects id returning None
      (accountService.findDuplicateIdentity _) expects person returning None
      controller.userAccountService_=(accountService)
      val contributionService = mock[FakeContributionService]
      (contributionService.contributions(_, _)) expects (id, true) returning List()
      controller.contributionService_=(contributionService)
      val paymentService = mock[FakePaymentRecordService]
      (paymentService.findByPerson _) expects id returning List()
      controller.paymentRecordService_=(paymentService)

      val identity = StubUserIdentity.editor
      val request = prepareSecuredGetRequest(identity, "/person/1")
      val result: Future[SimpleResult] = controller.details(person.id.get).apply(request)

      status(result) must equalTo(OK)
      contentAsString(result) must contain("Funder")
      contentAsString(result) must contain("/member/1/edit")
    }
  }

  def e7 = {
    truncateTables()
    new MockContext {
      val person = PersonHelper.one().insert
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val controller = new TestPeople()
      val mockService = mock[FakePersonService]
      (mockService.find(_: Long)) expects 1L returning Some(person)
      controller.personService_=(mockService)
      val req = prepareSecuredGetRequest(StubUserIdentity.editor, "/person/1")
      val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

      status(result) must equalTo(OK)
      contentAsString(result) must contain("/person/1/licenses/new")
      contentAsString(result) must contain("Make a Facilitator")
    }
  }

  def e8 = {
    truncateTables()
    new MockContext {
      // we insert a person object here to prevent crashing on account retrieval
      // when @person.deletable is called
      val person = PersonHelper.one().insert
      val id = 1L
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
      person.member_=(member)

      val controller = new TestPeople()
      val personService = mock[FakePersonService]
      (personService.find(_: Long)) expects id returning Some(person)
      controller.personService_=(personService)
      val orgService = mock[FakeOrganisationService]
      orgService.findActive _ expects () returning List()
      controller.orgService_=(orgService)
      val accountService = mock[FakeUserAccountService]
      (accountService.findRole _) expects id returning None
      (accountService.findDuplicateIdentity _) expects person returning None
      controller.userAccountService_=(accountService)
      val contributionService = mock[FakeContributionService]
      (contributionService.contributions(_, _)) expects (id, true) returning List()
      controller.contributionService_=(contributionService)
      val paymentService = mock[FakePaymentRecordService]
      val payments = List(
        PaymentRecord("remote1", 1L, 1L, person = true, "One Year Membership Fee", Money.parse("EUR 100")),
        PaymentRecord("remote2", 1L, 1L, person = true, "One Year Membership Fee 2", Money.parse("EUR 200")))
      (paymentService.findByPerson _) expects id returning payments
      controller.paymentRecordService_=(paymentService)

      val identity = StubUserIdentity.admin
      val request = prepareSecuredGetRequest(identity, "/person/1")
      val result: Future[SimpleResult] = controller.details(person.id.get).apply(request)

      status(result) must equalTo(OK)
      contentAsString(result) must contain("One Year Membership Fee")
      contentAsString(result) must contain("EUR 100")
      contentAsString(result) must contain(">remote1<")
      contentAsString(result) must contain("https://dashboard.stripe.com/live/payments/remote1")
      contentAsString(result) must contain("One Year Membership Fee 2")
      contentAsString(result) must contain("EUR 200")
      contentAsString(result) must contain(">remote2<")
      contentAsString(result) must contain("https://dashboard.stripe.com/live/payments/remote2")
    }
  }

  def e9 = {
    truncateTables()
    new MockContext {
      // we insert a person object here to prevent crashing on account retrieval
      // when @person.deletable is called
      val person = PersonHelper.one().insert
      val id = 1L
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
      person.member_=(member)

      val controller = new TestPeople()
      val personService = mock[FakePersonService]
      (personService.find(_: Long)) expects id returning Some(person)
      controller.personService_=(personService)
      val orgService = mock[FakeOrganisationService]
      orgService.findActive _ expects () returning List()
      controller.orgService_=(orgService)
      val accountService = mock[FakeUserAccountService]
      (accountService.findRole _) expects id returning None
      (accountService.findDuplicateIdentity _) expects person returning None
      controller.userAccountService_=(accountService)
      val contributionService = mock[FakeContributionService]
      (contributionService.contributions(_, _)) expects (id, true) returning List()
      controller.contributionService_=(contributionService)
      val paymentService = mock[FakePaymentRecordService]
      val payments = List(
        PaymentRecord("remote1", 1L, 1L, person = true, "One Year Membership Fee", Money.parse("EUR 100")),
        PaymentRecord("remote2", 1L, 1L, person = true, "One Year Membership Fee 2", Money.parse("EUR 200")))
      (paymentService.findByPerson _) expects id returning payments
      controller.paymentRecordService_=(paymentService)

      val identity = StubUserIdentity.editor
      val request = prepareSecuredGetRequest(identity, "/person/1")
      val result: Future[SimpleResult] = controller.details(person.id.get).apply(request)

      status(result) must equalTo(OK)
      contentAsString(result) must contain("One Year Membership Fee")
      contentAsString(result) must contain("EUR 100")
      contentAsString(result) must not contain ">remote1<"
      contentAsString(result) must not contain "https://dashboard.stripe.com/live/payments/remote1"
      contentAsString(result) must contain("One Year Membership Fee 2")
      contentAsString(result) must contain("EUR 200")
      contentAsString(result) must not contain ">remote2<"
      contentAsString(result) must not contain "https://dashboard.stripe.com/live/payments/remote2"
    }
  }
}
