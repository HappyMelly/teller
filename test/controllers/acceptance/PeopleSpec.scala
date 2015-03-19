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
import _root_.integration.PlayAppSpec
import models._
import models.payment.Record
import org.joda.money.Money
import org.joda.time.LocalDate
import org.scalamock.specs2.{ IsolatedMockFactory, MockContext }
import play.api.mvc.SimpleResult
import play.api.test.FakeRequest
import stubs._

import scala.concurrent.Future

class TestPeople() extends People with Security with FakeServices

class PeopleSpec extends PlayAppSpec with IsolatedMockFactory {

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

  When subscription exists, 'Stop automatic...' button should be visible,
      'No automatic renewal' badge should not be visible to
    an Editor                                                            $e11
    the owner of the profile                                             $e12

  When subscription does not exist, 'Stop automatic...' button should not be
  visible, 'No automatic renewal' badge should be visible to
    an Editor                                                            $e13
    a Viewer                                                             $e14

  Viewer should not see subscription-related buttons
    if subscription exists                                               $e15

  On automatic renewal cancellation the system should
    return 'Not found' if a person does not exists                       $e16
    return an error if a person is not a member                          $e17
    return an error if there is not subscription                         $e18

  'Edit Membership' and 'Delete Membership' buttons should
    not be visible to Viewers                                            $e19
    be visible to Editors                                                $e20
  """

  val personService = mock[FakePersonService]
  val orgService = mock[FakeOrganisationService]
  val accountService = mock[FakeUserAccountService]
  val contributionService = mock[FakeContributionService]
  val paymentService = mock[FakePaymentRecordService]
  val licenseService = mock[FakeLicenseService]
  val id = 1L
  val person = PersonHelper.one()
  person.socialProfile_=(new SocialProfile(email = "test@test.com"))

  trait DefaultMockContext extends MockContext {
    truncateTables()
    (personService.find(_: Long)) expects id returning Some(person)
    orgService.findActive _ expects () returning List()
  }

  trait ExtendedMockContext extends DefaultMockContext {
    licenseService.licenses _ expects (id) returning List()
    (contributionService.contributions(_, _)) expects (id, true) returning List()
    (paymentService.findByPerson _) expects id returning List()
  }

  trait EditorMockContext extends ExtendedMockContext {
    (accountService.findRole _).expects(id).returning(None)
    (accountService.findDuplicateIdentity _).expects(person).returning(None)
  }

  trait ViewerMockContext extends ExtendedMockContext {
    (accountService.findRole _).expects(id).returning(None).never()
    (accountService.findDuplicateIdentity _).expects(person).returning(None).never()
  }

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
      val identity = FakeUserIdentity.viewer
      val request = prepareSecuredGetRequest(identity, "/person/1")
      controller.details(id).apply(request)
    }
  }

  def e3 = new EditorMockContext {
    person.insert
    val member = MemberHelper.make(Some(1L), id, person = true, funder = false)
    person.member_=(member)

    val controller = fakedController()
    val req = prepareSecuredGetRequest(FakeUserIdentity.admin, "/person/1")
    val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must not contain "Financial account"
    contentAsString(result) must not contain "Account history"
  }

  def e4 = new ViewerMockContext {
    person.insert
    val member = MemberHelper.make(Some(1L), id, person = true, funder = false)
    person.member_=(member)

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/person/1")
    val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Supporter")
    contentAsString(result) must not contain "/member/1/edit"
  }

  def e5 = new EditorMockContext {
    // we insert a person object here to prevent crashing on account retrieval
    // when @person.deletable is called
    person.insert
    val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
    person.member_=(member)

    val controller = fakedController()

    val request = prepareSecuredGetRequest(FakeUserIdentity.editor, "/person/1")
    val result: Future[SimpleResult] = controller.details(person.id.get).apply(request)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Funder")
    contentAsString(result) must contain("/member/1/edit")
  }

  def e6 = new DefaultMockContext {
    // we insert a person object here to prevent crashing on account retrieval
    // when @person.deletable is called
    person.insert
    val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
    person.member_=(member)

    licenseService.licenses _ expects (id) returning List()
    (accountService.findRole _) expects id returning None
    (accountService.findDuplicateIdentity _) expects person returning None
    (contributionService.contributions(_, _)) expects (id, true) returning List()
    val payments = List(
      Record("remote1", 1L, 1L, person = true, "One Year Membership Fee", Money.parse("EUR 100")),
      Record("remote2", 1L, 1L, person = true, "One Year Membership Fee 2", Money.parse("EUR 200")))
    (paymentService.findByPerson _) expects id returning payments
    val controller = fakedController()

    val identity = FakeUserIdentity.admin
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

  def e7 = new EditorMockContext {
    person.insert
    val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
    person.member_=(member)

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/person/1")
    val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("/person/1/licenses/new")
    contentAsString(result) must contain("Make a Facilitator")
  }

  def e8 = new DefaultMockContext {
    person.insert
    val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
    person.member_=(member)

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

    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/person/1")
    val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("/person/1/licenses/new")
    contentAsString(result) must not contain "Make a Facilitator"
  }

  def e9 = new ViewerMockContext {
    person.insert
    val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
    person.member_=(member)

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/person/1")
    val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must not contain "/person/1/licenses/new"
    contentAsString(result) must not contain "Make a Facilitator"
  }

  def e10 = new DefaultMockContext {
    // we insert a person object here to prevent crashing on account retrieval
    // when @person.deletable is called
    person.insert
    val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
    person.member_=(member)

    licenseService.licenses _ expects id returning List()
    accountService.findRole _ expects id returning None
    accountService.findDuplicateIdentity _ expects person returning None
    (contributionService.contributions(_, _)) expects (id, true) returning List()
    val payments = List(
      Record("remote1", 1L, 1L, person = true, "One Year Membership Fee", Money.parse("EUR 100")),
      Record("remote2", 1L, 1L, person = true, "One Year Membership Fee 2", Money.parse("EUR 200")))
    (paymentService.findByPerson _) expects id returning payments
    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/person/1")
    val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

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

  def e11 = new EditorMockContext {
    // we insert a person object here to prevent crashing on account retrieval
    // when @person.deletable is called
    person.insert
    val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
    person.member_=(member)

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/person/1")
    val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Stop automatic renewal")
    contentAsString(result) must contain("person/1/cancel")
    contentAsString(result) must not contain "Renew subscription"
    contentAsString(result) must not contain "Automatic renewal is stopped"
  }

  def e12 = new ViewerMockContext {
    // we insert a person object here to prevent crashing on account retrieval
    // when @person.deletable is called
    person.insert
    val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
    person.member_=(member)

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/person/1")
    val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Stop automatic renewal")
    contentAsString(result) must contain("person/1/cancel")
    contentAsString(result) must not contain "Renew subscription"
    contentAsString(result) must not contain "Automatic renewal is stopped"
  }

  def e13 = new EditorMockContext {
    // we insert a person object here to prevent crashing on account retrieval
    // when @person.deletable is called
    person.insert
    val member = MemberHelper.make(Some(1L), id, person = true, funder = true,
      renewal = false)
    person.member_=(member)

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/person/1")
    val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must not contain "Stop automatic renewal"
    contentAsString(result) must not contain "person/1/renew"
    contentAsString(result) must not contain "Renew subscription"
    contentAsString(result) must contain("Automatic renewal is stopped")
  }

  def e14 = new MockContext {
    truncateTables()

    // we insert a person object here to prevent crashing on account retrieval
    // when @person.deletable is called
    val person = PersonHelper.two().insert
    val id = 2L
    person.socialProfile_=(new SocialProfile(email = "test@test.com"))
    val member = MemberHelper.make(Some(1L), id, person = true, funder = true,
      renewal = false)
    person.member_=(member)

    (personService.find(_: Long)) expects id returning Some(person)
    orgService.findActive _ expects () returning List()
    licenseService.licenses _ expects id returning List()
    (accountService.findRole _).expects(id).returning(None).never()
    (accountService.findDuplicateIdentity _).expects(person).returning(None).never()
    (contributionService.contributions(_, _)) expects (id, true) returning List()
    (paymentService.findByPerson _) expects id returning List()
    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/person/2")
    val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must not contain "Stop automatic renewal"
    contentAsString(result) must contain("Automatic renewal is stopped")
  }

  def e15 = new MockContext {
    truncateTables()

    // we insert a person object here to prevent crashing on account retrieval
    // when @person.deletable is called
    val person = PersonHelper.two().insert
    val id = 2L
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

    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/person/2")
    val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must not contain "Stop automatic renewal"
    contentAsString(result) must not contain "person/1/cancel"
    contentAsString(result) must not contain "Automatic renewal is stopped"
  }

  def e16 = new MockContext {
    truncateTables()
    (personService.find(_: Long)) expects id returning None
    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/person/1/cancel")
    val result: Future[SimpleResult] = controller.cancel(person.id.get).apply(req)

    status(result) must equalTo(NOT_FOUND)
  }

  def e17 = new MockContext {
    truncateTables()
    val person = PersonHelper.one()
    person.socialProfile_=(new SocialProfile(email = "test@test.com"))
    (personService.find(_: Long)) expects id returning Some(person)
    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/person/1/cancel")
    val result: Future[SimpleResult] = controller.cancel(person.id.get).apply(req)

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome.which(_.contains("/person/1"))
  }

  def e18 = new MockContext {
    truncateTables()
    val person = PersonHelper.one()
    person.socialProfile_=(new SocialProfile(email = "test@test.com"))
    val member = MemberHelper.make(Some(1L), id, person = true, funder = true,
      renewal = false)
    person.member_=(member)

    (personService.find(_: Long)) expects id returning Some(person)
    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/person/1/cancel")
    val result: Future[SimpleResult] = controller.cancel(person.id.get).apply(req)

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome.which(_.contains("/person/1"))
  }

  def e19 = new ViewerMockContext {
    // we insert a person object here to prevent crashing on account retrieval
    // when @person.deletable is called
    person.insert
    val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
    person.member_=(member)

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/person/1")
    val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must not contain "Edit Membership"
    contentAsString(result) must not contain "member/1/edit"
    contentAsString(result) must not contain "Delete Membership"
    contentAsString(result) must not contain "member/1/delete"
  }

  def e20 = new EditorMockContext {
    // we insert a person object here to prevent crashing on account retrieval
    // when @person.deletable is called
    person.insert
    val member = MemberHelper.make(Some(1L), id, person = true, funder = true)
    person.member_=(member)

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/person/1")
    val result: Future[SimpleResult] = controller.details(person.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Edit Membership")
    contentAsString(result) must contain("member/1/edit")
    contentAsString(result) must contain("Delete Membership")
    contentAsString(result) must contain("member/1/delete")
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
