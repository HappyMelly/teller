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

package controllers.acceptance

import controllers.Organisations
import helpers.{ MemberHelper, PersonHelper, OrganisationHelper }
import _root_.integration.PlayAppSpec
import models.payment.Record
import models.{ Organisation, Person, SocialProfile, ProfileType, OrgView }
import models.service._
import org.joda.money.Money
import org.scalamock.specs2.{ MockContext, IsolatedMockFactory }
import play.api.mvc.SimpleResult
import stubs._

import scala.concurrent.Future

class TestOrganisations() extends Organisations with FakeServices

class OrganisationsSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  When an organisation is not a member, 'Become a Member' button should
    be visible to members of this organisation                              $e1
    be invisible to Editors                                                 $e2
    be invisible to Viewers                                                 $e3

  Org Details page should
    contain a supporter badge if the org is a supporter                     $e4
    contain a funder badge if the org is a funder                           $e5
    contain a list of payments if the org is a member                       $e6

  Editor should
    not see links to remote payments                                        $e7

  When subscription exists, 'Stop automatic...' button should be visible,
  'No automatic renewal' badge should not be visible to
    an Editor                                                               $e8
    a member of the org                                                     $e9

  When subscription does not exist, 'Stop automatic...' button should not be
  visible, 'No automatic renewal' badge should be visible to
    an Editor                                                               $e10
    a Viewer                                                                $e11

  Non-member Viewer should not see subscription-related buttons
    if subscription exists                                                  $e12

  On subscription cancellation the system should
    return 'Not found' if an org does not exists                            $e13
    return an error if an org is not a member                               $e14
    return an error if there is not subscription                            $e15

  'Edit Membership' and 'Delete Membership' buttons should
    not be visible to Viewers                                               $e16
    be visible to Editors                                                   $e17
  """

  val personService = mock[PersonService]
  val orgService = mock[OrganisationService]
  val productService = mock[ProductService]
  val contributionService = mock[ContributionService]
  val paymentService = mock[PaymentRecordService]
  val org = OrganisationHelper.one
  val id = 1L
  val profile = SocialProfile(0, ProfileType.Organisation, "")

  trait DefaultMockContext extends MockContext {
    truncateTables()
    (contributionService.contributions(_, _)).expects(id, false).returning(List())
    (orgService.findWithProfile _) expects id returning Some(OrgView(org, profile))
    (productService.findAll _).expects().returning(List())
    (personService.findActive _).expects().returning(List())
  }

  trait ExtendedMemberMockContext extends DefaultMockContext {
    (paymentService.findByOrganisation _) expects id returning List()
  }

  trait ExtendedNonMemberMockContext extends DefaultMockContext {
    (paymentService.findByOrganisation _).expects(id).returning(List()).never()
  }

  def e1 = new ExtendedNonMemberMockContext {
    // we insert an org object here to prevent crashing on account retrieval
    // when @org.deletable is called
    OrganisationService.get.insert(OrgView(org, profile))
    org.people_=(List(PersonHelper.one()))

    val controller = fakedController()
    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(id).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Become a Member")
    contentAsString(result) must contain("/membership/welcome")
  }

  def e2 = new ExtendedNonMemberMockContext {
    // we insert an org object here to prevent crashing on account retrieval
    // when @org.deletable is called
    OrganisationService.get.insert(OrgView(org, profile))
    org.people_=(List())
    val controller = fakedController()
    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(id).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must not contain "Become a Member"
  }

  def e3 = new ExtendedNonMemberMockContext {
    // we insert an org object here to prevent crashing on account retrieval
    // when @org.deletable is called
    OrganisationService.get.insert(OrgView(org, profile))
    org.people_=(List())
    val controller = fakedController()
    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(id).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must not contain "Become a Member"
  }

  def e4 = new ExtendedMemberMockContext {
    // we insert an org object here to prevent crashing on account retrieval
    // when @org.deletable is called
    OrganisationService.get.insert(OrgView(org, profile))
    val member = MemberHelper.make(Some(1L), id, person = false, funder = false)
    org.member_=(member)
    org.people_=(List())

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(org.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Supporter")
    contentAsString(result) must not contain "/member/1/edit"
  }

  def e5 = new ExtendedMemberMockContext {
    // we insert a person object here to prevent crashing on account retrieval
    // when @person.deletable is called
    OrganisationService.get.insert(OrgView(org, profile))
    val member = MemberHelper.make(Some(1L), id, person = false, funder = true)
    org.member_=(member)
    org.people_=(List())

    val controller = fakedController()

    val request = prepareSecuredGetRequest(FakeUserIdentity.editor, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(org.id.get).apply(request)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("funder.jpg")
    contentAsString(result) must contain("/member/1/edit")
  }

  def e6 = new DefaultMockContext {
    // we insert a person object here to prevent crashing on account retrieval
    // when @person.deletable is called
    OrganisationService.get.insert(OrgView(org, profile))
    val member = MemberHelper.make(Some(1L), id, person = false, funder = true)
    org.member_=(member)
    org.people_=(List())

    val payments = List(
      Record("remote1", 1L, 1L, person = false, "One Year Membership Fee", Money.parse("EUR 100")),
      Record("remote2", 1L, 1L, person = false, "One Year Membership Fee 2", Money.parse("EUR 200")))
    (paymentService.findByOrganisation _) expects id returning payments
    val controller = fakedController()

    val identity = FakeUserIdentity.admin
    val req = prepareSecuredGetRequest(identity, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(org.id.get).apply(req)

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

  def e7 = new DefaultMockContext {
    // we insert a person object here to prevent crashing on account retrieval
    // when @person.deletable is called
    OrganisationService.get.insert(OrgView(org, profile))
    val member = MemberHelper.make(Some(1L), id, person = false, funder = true)
    org.member_=(member)
    org.people_=(List())

    val payments = List(
      Record("remote1", 1L, 1L, person = false, "One Year Membership Fee", Money.parse("EUR 100")),
      Record("remote2", 1L, 1L, person = false, "One Year Membership Fee 2", Money.parse("EUR 200")))
    (paymentService.findByOrganisation _) expects id returning payments
    val controller = fakedController()

    val identity = FakeUserIdentity.editor
    val req = prepareSecuredGetRequest(identity, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(org.id.get).apply(req)

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

  def e8 = new ExtendedMemberMockContext {
    // we insert an org object here to prevent crashing on account retrieval
    // when @org.deletable is called
    OrganisationService.get.insert(OrgView(org, profile))
    val member = MemberHelper.make(Some(1L), id, person = false, funder = true)
    org.member_=(member)
    org.people_=(List())

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(org.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Stop automatic renewal")
    contentAsString(result) must contain("organization/1/cancel")
    contentAsString(result) must not contain "Renew subscription"
    contentAsString(result) must not contain "Automatic renewal is stopped"
  }

  def e9 = new ExtendedMemberMockContext {
    // we insert an org object here to prevent crashing on account retrieval
    // when @org.deletable is called
    OrganisationService.get.insert(OrgView(org, profile))
    val member = MemberHelper.make(Some(1L), id, person = false, funder = true)
    org.member_=(member)
    val person = PersonHelper.one().insert
    person.addRelation(id)
    org.people_=(List(person))

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(org.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Stop automatic renewal")
    contentAsString(result) must contain("organization/1/cancel")
    contentAsString(result) must not contain "Renew subscription"
    contentAsString(result) must not contain "Automatic renewal is stopped"
  }

  def e10 = new ExtendedMemberMockContext {
    // we insert a org object here to prevent crashing on account retrieval
    // when @person.deletable is called
    OrganisationService.get.insert(OrgView(org, profile))
    val member = MemberHelper.make(Some(1L), id, person = false, funder = true,
      renewal = false)
    org.member_=(member)
    org.people_=(List())

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(org.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must not contain "Stop automatic renewal"
    contentAsString(result) must not contain "organization/1/renew"
    contentAsString(result) must not contain "Renew subscription"
    contentAsString(result) must contain("Automatic renewal is stopped")
  }

  def e11 = new ExtendedMemberMockContext {
    // we insert a org object here to prevent crashing on account retrieval
    // when @person.deletable is called
    OrganisationService.get.insert(OrgView(org, profile))
    val member = MemberHelper.make(Some(1L), id, person = false, funder = true,
      renewal = false)
    org.member_=(member)
    org.people_=(List())

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(org.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must not contain "Stop automatic renewal"
    contentAsString(result) must not contain "organisation/1/renew"
    contentAsString(result) must not contain "Renew subscription"
    contentAsString(result) must contain("Automatic renewal is stopped")
  }

  def e12 = new ExtendedMemberMockContext {
    // we insert an org object here to prevent crashing on account retrieval
    // when @org.deletable is called
    OrganisationService.get.insert(OrgView(org, profile))
    val member = MemberHelper.make(Some(1L), id, person = false, funder = true)
    org.member_=(member)
    org.people_=(List())

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(org.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must not contain "Stop automatic renewal"
    contentAsString(result) must not contain "organization/1/renew"
    contentAsString(result) must not contain "Renew subscription"
    contentAsString(result) must not contain "Automatic renewal is stopped"
  }

  def e13 = new MockContext {
    truncateTables()
    (orgService.find(_: Long)) expects id returning None
    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/organization/1/cancel")
    val result: Future[SimpleResult] = controller.cancel(org.id.get).apply(req)

    status(result) must equalTo(NOT_FOUND)
  }

  def e14 = new MockContext {
    truncateTables()
    val org = OrganisationHelper.one
    (orgService.find(_: Long)) expects id returning Some(org)
    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/organization/1/cancel")
    val result: Future[SimpleResult] = controller.cancel(org.id.get).apply(req)

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome.which(_.contains("/organization/1"))
  }

  def e15 = new MockContext {
    truncateTables()
    val org = OrganisationHelper.one
    val member = MemberHelper.make(Some(1L), id, person = false, funder = true,
      renewal = false)
    org.member_=(member)
    (orgService.find(_: Long)) expects id returning Some(org)
    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/organization/1/cancel")
    val result: Future[SimpleResult] = controller.cancel(org.id.get).apply(req)

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome.which(_.contains("/organization/1"))
  }

  def e16 = new ExtendedMemberMockContext {
    // we insert a org object here to prevent crashing on account retrieval
    // when @person.deletable is called
    OrganisationService.get.insert(OrgView(org, profile))
    val member = MemberHelper.make(Some(1L), id, person = false, funder = true)
    org.member_=(member)
    org.people_=(List())

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.viewer, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(org.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must not contain "Edit Membership"
    contentAsString(result) must not contain "member/1/edit"
    contentAsString(result) must not contain "Delete Membership"
    contentAsString(result) must not contain "member/1/delete"
  }

  def e17 = new ExtendedMemberMockContext {
    // we insert a org object here to prevent crashing on account retrieval
    // when @person.deletable is called
    OrganisationService.get.insert(OrgView(org, profile))
    val member = MemberHelper.make(Some(1L), id, person = false, funder = true)
    org.member_=(member)
    org.people_=(List())

    val controller = fakedController()

    val req = prepareSecuredGetRequest(FakeUserIdentity.editor, "/organisation/1")
    val result: Future[SimpleResult] = controller.details(org.id.get).apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Edit Membership")
    contentAsString(result) must contain("member/1/edit")
    contentAsString(result) must contain("Delete Membership")
    contentAsString(result) must contain("member/1/delete")
  }

  private def fakedController(): TestOrganisations = {
    val controller = new TestOrganisations()
    controller.contributionService_=(contributionService)
    controller.orgService_=(orgService)
    controller.personService_=(personService)
    controller.productService_=(productService)
    controller.paymentRecordService_=(paymentService)
    controller
  }
}
