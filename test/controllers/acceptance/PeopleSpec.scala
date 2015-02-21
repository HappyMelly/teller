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
import helpers._
import integration.PlayAppSpec
import models._
import org.joda.money.Money
import org.joda.time.LocalDate
import org.scalamock.specs2.{ IsolatedMockFactory, MockContext }
import play.api.mvc.SimpleResult
import play.api.test.FakeRequest
import stubs._

import scala.concurrent.Future

class TestPeople() extends People with Security with FakeServices

class PeopleSpec extends PlayAppSpec with IsolatedMockFactory {
  def setupDb() {}
  def cleanupDb() {}

  override def is = s2"""

  Page with person's data should

    not be visible to unauthorized user                                  $e1
    and be visible to authorized user                                    $e2
    not contain accounting details                                       $e3
    contain a supporter badge if the person is a supporter               $e4
    contain a funder badge if the person is a funder                     $e5
    contain a list of payments if the person is a member                 $e6

  'Make a Facilitator' button should
    be visible to an Editor if a person has no licenses                  $e7
    not be visible to an Editor if a person has licenses                 $e8
    not be visible to a Viewer if a person has no licenses               $e9

  Editor should
    not see links to remote payments                                     $e10


  """

  val personService = mock[FakePersonService]
  val orgService = mock[FakeOrganisationService]
  val accountService = mock[FakeUserAccountService]
  val contributionService = mock[FakeContributionService]
  val paymentService = mock[FakePaymentRecordService]
  val licenseService = mock[FakeLicenseService]
  val id = 1L

  def e1 = {
    val controller = new TestPeople()
    val result: Future[SimpleResult] = controller.details(id).apply(FakeRequest())

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome.which(_.contains("login"))
  }

  def e2 = {
    new MockContext {
      val controller = new TestPeople()
      val mockService = mock[FakePersonService]
      // if this method is called it means we have passed a security check
      (mockService.find(_: Long)) expects id returning None
      controller.personService_=(mockService)
      val identity = StubUserIdentity.viewer
      val request = prepareSecuredGetRequest(identity, "/person/1")
      controller.details(id).apply(request)
    }
  }

  def e3 = {
    new MockContext {
      val person = PersonHelper.one().insert
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val controller = new TestPeople()
      val mockService = mock[FakePersonService]
      (mockService.find(_: Long)) expects id returning Some(person)
      controller.personService_=(mockService)
      val identity = StubUserIdentity.admin
      val request = prepareSecuredGetRequest(identity, "/person/1")
      val result: Future[SimpleResult] = controller.details(person.id.get).apply(request)

      status(result) must equalTo(OK)
      contentAsString(result) must not contain "Financial account"
      contentAsString(result) must not contain "Account history"
    }
  }

  def e4 = {
    truncateTables()
    new MockContext {
      val person = PersonHelper.one().insert
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val member = MemberHelper.make(Some(1L), id, person = true, funder = false)
      person.member_=(member)

      (personService.find(_: Long)) expects id returning Some(person)
      orgService.findActive _ expects () returning List()
      licenseService.licenses _ expects (id) returning List()
      (accountService.findRole _).expects(id).returning(None).never()
      (accountService.findDuplicateIdentity _).expects(person).returning(None).never()
      (contributionService.contributions(_, _)) expects (id, true) returning List()
      (paymentService.findByPerson _) expects id returning List()
      val controller = fakedController()

      val identity = StubUserIdentity.viewer
      val request = prepareSecuredGetRequest(identity, "/person/1")
      val result: Future[SimpleResult] = controller.details(person.id.get).apply(request)

      status(result) must equalTo(OK)
      contentAsString(result) must contain("Supporter")
      contentAsString(result) must not contain "/member/1/edit"
    }
  }

  def e5 = {
    truncateTables()
    new MockContext {
      // we insert a person object here to prevent crashing on account retrieval
      // when @person.deletable is called
      val person = PersonHelper.one().insert
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
      person.member_=(member)

      (personService.find(_: Long)) expects id returning Some(person)
      orgService.findActive _ expects () returning List()
      licenseService.licenses _ expects (id) returning List()
      (accountService.findRole _) expects id returning None
      (accountService.findDuplicateIdentity _) expects person returning None
      (contributionService.contributions(_, _)) expects (id, true) returning List()
      (paymentService.findByPerson _) expects id returning List()
      val controller = fakedController()

      val identity = StubUserIdentity.editor
      val request = prepareSecuredGetRequest(identity, "/person/1")
      val result: Future[SimpleResult] = controller.details(person.id.get).apply(request)

      status(result) must equalTo(OK)
      contentAsString(result) must contain("Funder")
      contentAsString(result) must contain("/member/1/edit")
    }
  }

  def e6 = {
    truncateTables()
    new MockContext {
      // we insert a person object here to prevent crashing on account retrieval
      // when @person.deletable is called
      val person = PersonHelper.one().insert
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
      person.member_=(member)

      (personService.find(_: Long)) expects id returning Some(person)
      orgService.findActive _ expects () returning List()
      licenseService.licenses _ expects (id) returning List()
      (accountService.findRole _) expects id returning None
      (accountService.findDuplicateIdentity _) expects person returning None
      (contributionService.contributions(_, _)) expects (id, true) returning List()
      val payments = List(
        PaymentRecord("remote1", 1L, 1L, person = true, "One Year Membership Fee", Money.parse("EUR 100")),
        PaymentRecord("remote2", 1L, 1L, person = true, "One Year Membership Fee 2", Money.parse("EUR 200")))
      (paymentService.findByPerson _) expects id returning payments
      val controller = fakedController()

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

  def e7 = {
    truncateTables()
    new MockContext {
      val person = PersonHelper.one().insert
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
      person.member_=(member)

      (personService.find(_: Long)) expects id returning Some(person)
      orgService.findActive _ expects () returning List()
      licenseService.licenses _ expects id returning List()
      (accountService.findRole _) expects id returning None
      (accountService.findDuplicateIdentity _) expects person returning None
      (contributionService.contributions(_, _)) expects (id, true) returning List()
      (paymentService.findByPerson _) expects id returning List()
      val controller = fakedController()

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
      val person = PersonHelper.one().insert
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
      person.member_=(member)

      (personService.find(_: Long)) expects id returning Some(person)
      orgService.findActive _ expects () returning List()

      val license = new License(Some(1L), id, 1L, "1",
        LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), true,
        Money.parse("EUR 10"), None)
      val licenses = List(LicenseView(BrandHelper.one, license))

      licenseService.licenses _ expects id returning licenses
      accountService.findRole _ expects id returning None
      accountService.findDuplicateIdentity _ expects person returning None
      (contributionService.contributions(_, _)) expects (id, true) returning List()
      paymentService.findByPerson _ expects id returning List()
      val controller = fakedController()

      val req = prepareSecuredGetRequest(StubUserIdentity.editor, "/person/1")
      val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

      status(result) must equalTo(OK)
      contentAsString(result) must contain("/person/1/licenses/new")
      contentAsString(result) must not contain "Make a Facilitator"
    }
  }

  def e9 = {
    truncateTables()
    new MockContext {
      val person = PersonHelper.one().insert
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
      person.member_=(member)

      (personService.find(_: Long)) expects id returning Some(person)
      orgService.findActive _ expects () returning List()
      licenseService.licenses _ expects id returning List()
      (accountService.findRole _).expects(id).returning(None).never()
      (accountService.findDuplicateIdentity _).expects(person).returning(None).never()
      (contributionService.contributions(_, _)) expects (id, true) returning List()
      (paymentService.findByPerson _) expects id returning List()
      val controller = fakedController()

      val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/person/1")
      val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

      status(result) must equalTo(OK)
      contentAsString(result) must not contain "/person/1/licenses/new"
      contentAsString(result) must not contain "Make a Facilitator"
    }
  }

  def e10 = {
    truncateTables()
    // we insert a person object here to prevent crashing on account retrieval
    // when @person.deletable is called
    new MockContext {
      val person = PersonHelper.one().insert
      person.socialProfile_=(new SocialProfile(email = "test@test.com"))
      val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
      person.member_=(member)

      (personService.find(_: Long)) expects id returning Some(person)
      orgService.findActive _ expects () returning List()
      licenseService.licenses _ expects id returning List()
      accountService.findRole _ expects id returning None
      accountService.findDuplicateIdentity _ expects person returning None
      (contributionService.contributions(_, _)) expects (id, true) returning List()
      val payments = List(
        PaymentRecord("remote1", 1L, 1L, person = true, "One Year Membership Fee", Money.parse("EUR 100")),
        PaymentRecord("remote2", 1L, 1L, person = true, "One Year Membership Fee 2", Money.parse("EUR 200")))
      (paymentService.findByPerson _) expects id returning payments
      val controller = fakedController()

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

  private def fakedController() = {
    val controller = new TestPeople()
    controller.personService_=(personService)
    controller.orgService_=(orgService)
    controller.userAccountService_=(accountService)
    controller.contributionService_=(contributionService)
    controller.paymentRecordService_=(paymentService)
    controller.licenseService_=(licenseService)
    controller
  }
}
