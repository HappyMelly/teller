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
package acceptance

import controllers._
import helpers.{ PersonHelper, OrganisationHelper }
import integration.PlayAppSpec
import models.Member
import org.joda.money.CurrencyUnit._
import org.joda.money.Money
import org.joda.time.{ DateTime, LocalDate }
import org.scalamock.specs2.MockContext
import org.specs2.matcher._
import org.specs2.mutable.After
import play.api.cache.Cache
import play.api.db.slick._
import play.api.mvc.{ AnyContentAsEmpty, SimpleResult }
import play.api.Play.current
import play.api.test.FakeRequest
import stubs.{ FakePersonService, FakeOrganisationService, StubLoginIdentity, FakeServices }

import scala.concurrent.Future
import scala.slick.jdbc.{ StaticQuery ⇒ Q }
import scala.slick.session.Session

class MembersSpec extends PlayAppSpec with DataTables {
  class TestMembers() extends Members with Security with FakeServices

  def setupDb() {}
  def cleanupDb() {}

  override def is = s2"""

  Page with a list of members should
    show all members sorted by names                     $e1

  Editor should
    not be able to add new member with wrong parameters            $e6
    get a correct error message if membership date is too early    $e7
    get a correct error message if membership date is too late     $e8
    be redirected to 'New Organisation' form if he chose 'Org'     $e9
    be redirected to 'New Person' form if he chose 'Person'        $e10
    be redirected to 'Existing Org' form if he chose 'Org'         $e11
    be redirected to 'Existing Person' form if he chose 'Person'   $e12

  If an editor tries to complete step 2 without completing step 1,
    she should get an error message
      while creating new organisation                              $e13
      while creating new person                                    $e14
      while updating exiting organisation                          $e15
      while updating existing person                               $e16

  Add existing org form should contain
    a set of predefined elements                                   $e17
    only organisations which are not members in the selector       $e18

  While updating existing org Editor should
    get a correct error message if org does not exist              $e19
    get a correct error message if org is already a member         $e20

  Add existing person form should contain
    a set of predefined elements                                   $e21
    only people which are not members in the selector              $e22

  While updating existing person Editor should
    get a correct error message if person does not exist           $e23
    get a correct error message if person is already a member      $e24
  """

  val controller = new TestMembers()
  val MSG = "You are trying to complete step 2 while adding new member without completing step 1"

  def e1 = {
    val identity = StubLoginIdentity.viewer
    val request = prepareSecuredGetRequest(identity, "/members")

    val result: Future[SimpleResult] = controller.index().apply(request)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Members")
    contentAsString(result) must contain("/member/1")
    contentAsString(result) must contain("/member/2")
    contentAsString(result) must contain("/member/3")
    contentAsString(result) must contain("/member/4")
    //@TODO finish multiple checks
  }

  def e6 = {
    val identity = StubLoginIdentity.editor

    "objectId" || "person" | "funder" | "currency" | "amount" | "since" | "existingObject" |
      // empty currency
      "0" !! "1" ! "false" ! "" ! "100" ! "2015-01-01" ! "1" |
      // unknown currency
      "0" !! "1" ! "false" ! "TERES" ! "100" ! "2015-01-01" ! "1" |
      // negative amount
      "0" !! "1" ! "false" ! "EUR" ! "-100" ! "2015-01-01" ! "1" |
      // zero amount
      "0" !! "1" ! "false" ! "EUR" ! "0.00" ! "2015-01-01" ! "0" |
      // empty since
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! "" ! "0" |
      // wrong since
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! "31-312-321" ! "1" |
      // since earlier than 2015-01-01
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! "2014-12-31" ! "0" |
      // since later than today
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! LocalDate.now().plusDays(1).toString ! "0" |
      // empty 'funder'
      "0" !! "1" ! "" ! "EUR" ! "105.05" ! "2015-01-01" ! "1" |
      // non-boolean 'funder'
      "0" !! "1" ! "1.00" ! "EUR" ! "105.05" ! "2015-01-01" ! "0" |
      // empty 'existingObject'
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! "2015-01-01" ! "" |
      // non-boolean 'existingObject'
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! "2015-01-01" ! "1.00" |> {
        (objectId, person, funder, currency, amount, since, existingObject) ⇒
          {
            val req = prepareSecuredPostRequest(identity, "/member/new").
              withFormUrlEncodedBody(("objectId", objectId),
                ("person", person),
                ("funder", funder),
                ("fee.currency", currency),
                ("fee.amount", amount),
                ("since", since),
                ("existingObject", existingObject))
            val result: Future[SimpleResult] = controller.create().apply(req)

            status(result) must equalTo(BAD_REQUEST)
          }
      }
  }

  def e7 = {
    val req = prepareSecuredPostRequest(StubLoginIdentity.editor, "/")
    val uReq = addMemberData(req, since = "2014-01-01")
    val result: Future[SimpleResult] = controller.create().apply(uReq)

    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("Membership date cannot be earlier than 2015-01-01")
  }

  def e8 = {
    val req = prepareSecuredPostRequest(StubLoginIdentity.editor, "/")
    val uReq = addMemberData(req, since = LocalDate.now().plusDays(3).toString)
    val result: Future[SimpleResult] = controller.create().apply(uReq)

    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("Membership date cannot be later than today")
  }

  def e9 = new cleanup {
    val req = prepareSecuredPostRequest(StubLoginIdentity.editor, "/")
    val uReq = addMemberData(req, person = "0")
    val result: Future[SimpleResult] = controller.create().apply(uReq)

    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").nonEmpty must_== true
    headers(result).get("Location").get must_== "/member/new/organisation"
  }

  def e10 = new cleanup {
    val req = prepareSecuredPostRequest(StubLoginIdentity.editor, "/")
    val uReq = addMemberData(req, person = "1")
    val result: Future[SimpleResult] = controller.create().apply(uReq)

    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").nonEmpty must_== true
    headers(result).get("Location").get must_== "/member/new/person"
  }

  def e11 = new cleanup {
    val req = prepareSecuredPostRequest(StubLoginIdentity.editor, "/")
    val uReq = addMemberData(req, person = "0", existingObject = "1")
    val result: Future[SimpleResult] = controller.create().apply(uReq)

    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").nonEmpty must_== true
    headers(result).get("Location").get must_== "/member/existing/organisation"
  }

  def e12 = new cleanup {
    val req = prepareSecuredPostRequest(StubLoginIdentity.editor, "/")
    val uReq = addMemberData(req, person = "1", existingObject = "1")
    val result: Future[SimpleResult] = controller.create().apply(uReq)

    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").nonEmpty must_== true
    headers(result).get("Location").get must_== "/member/existing/person"
  }

  def e13 = {
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredPostRequest(identity, "/member/organisation").
      withFormUrlEncodedBody(("name", "Test"), ("country", "RU"))
    val result = controller.createNewOrganisation().apply(request)

    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain(MSG)
  }

  def e14 = {
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredPostRequest(identity, "/member/person").
      withFormUrlEncodedBody(("emailAddress", "ttt@ttt.ru"), ("address.country", "RU"),
        ("firstName", "Test"), ("lastName", "Test"), ("signature", "false"),
        ("role", "0"))
    val result = controller.createNewPerson().apply(request)

    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain(MSG)
  }

  def e15 = {
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredPostRequest(identity, "/member/existing/organisation").
      withFormUrlEncodedBody(("id", "1"))
    val result = controller.updateExistingOrg().apply(request)

    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain(MSG)
  }

  def e16 = {
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredPostRequest(identity, "/member/existing/person").
      withFormUrlEncodedBody(("id", "1"))
    val result = controller.updateExistingPerson().apply(request)

    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain(MSG)
  }

  def e17 = {
    val req = prepareSecuredGetRequest(
      StubLoginIdentity.editor,
      "/member/existing/organisation")
    val result: Future[SimpleResult] = controller.addExistingOrganisation().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add member")
    contentAsString(result) must contain("Step 2: Existing organisation")
    contentAsString(result) must contain(routes.Members.updateExistingOrg.url)
    contentAsString(result) must contain("<select")
  }

  def e18 = {
    Seq(
      (Some(1L), "First org", "DE"),
      (Some(2L), "Second org", "DE"),
      (Some(3L), "Third org", "DE"),
      (Some(4L), "Fourth org", "DE"),
      (Some(5L), "Firth org", "DE"),
      (Some(6L), "Sixth org", "DE")).foreach {
        case (id, name, country) ⇒
          val org = OrganisationHelper.make(
            id = id,
            name = name,
            countryCode = country)
          org.insert
      }
    Seq(
      (2L, false, false, Money.of(EUR, 100), LocalDate.now(), 1L),
      (5L, false, true, Money.of(EUR, 200), LocalDate.now(), 1L)).foreach {
        case (objectId, person, funder, fee, since, createdBy) ⇒
          val member = new Member(None, objectId, person, funder, fee, since,
            existingObject = false,
            DateTime.now(), createdBy, DateTime.now(), createdBy)
          member.insert
      }

    val req = prepareSecuredGetRequest(
      StubLoginIdentity.editor,
      "/member/existing/organisation")
    val result: Future[SimpleResult] = controller.addExistingOrganisation().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("<select")
    contentAsString(result) must contain("Select organisation")
    contentAsString(result) must contain("value=\"1\"")
    contentAsString(result) must contain("value=\"3\"")
    contentAsString(result) must contain("value=\"4\"")
    contentAsString(result) must contain("value=\"6\"")
    contentAsString(result) must contain("First org")
    contentAsString(result) must contain("Third org")
    contentAsString(result) must contain("Fourth org")
    contentAsString(result) must contain("Sixth org")
  }

  def e19 = new MockContext {
    val controller = new TestMembers()
    val m = new Member(None, 0, person = false, funder = false,
      Money.parse("EUR 100"), LocalDate.now(), existingObject = true,
      DateTime.now(), 1L, DateTime.now(), 1L)
    Cache.set(Members.cacheId(1L), m, 1800)
    val service = mock[FakeOrganisationService]
    (service.find _).expects(*).returning(None)
    (service.findNonMembers _).expects().returning(List())
    controller.organisationService_=(service)
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredPostRequest(identity, "/member/existing/organisation").
      withFormUrlEncodedBody(("id", "1"))
    val result = controller.updateExistingOrg().apply(request)

    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("This organisation does not exist")
  }

  def e20 = new cleanup {
    new MockContext {
      val controller = new TestMembers()
      val m = new Member(None, 1L, person = false, funder = false,
        Money.parse("EUR 100"), LocalDate.now(), existingObject = true,
        DateTime.now(), 1L, DateTime.now(), 1L).insert
      val org = OrganisationHelper.one.copy(id = Some(1L))
      Cache.set(Members.cacheId(1L), m, 1800)
      val service = mock[FakeOrganisationService]
      (service.find _).expects(*).returning(Some(org))
      (service.findNonMembers _).expects().returning(List())
      controller.organisationService_=(service)
      val identity = StubLoginIdentity.editor
      val request = prepareSecuredPostRequest(identity, "/member/existing/organisation").
        withFormUrlEncodedBody(("id", "1"))
      val result = controller.updateExistingOrg().apply(request)

      status(result) must equalTo(BAD_REQUEST)
      contentAsString(result) must contain("This organisation is already a member")
    }
  }

  def e21 = {
    val req = prepareSecuredGetRequest(
      StubLoginIdentity.editor,
      "/member/existing/person")
    val result: Future[SimpleResult] = controller.addExistingPerson().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add member")
    contentAsString(result) must contain("Step 2: Existing person")
    contentAsString(result) must contain(routes.Members.updateExistingPerson.url)
    contentAsString(result) must contain("<select")
  }

  def e22 = {
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
    Seq(
      (2L, true, false, Money.of(EUR, 100), LocalDate.now(), 1L),
      (5L, true, true, Money.of(EUR, 200), LocalDate.now(), 1L)).foreach {
        case (objectId, person, funder, fee, since, createdBy) ⇒
          val member = new Member(None, objectId, person, funder, fee, since,
            existingObject = false,
            DateTime.now(), createdBy, DateTime.now(), createdBy)
          member.insert
      }

    val req = prepareSecuredGetRequest(
      StubLoginIdentity.editor,
      "/member/existing/person")
    val result: Future[SimpleResult] = controller.addExistingPerson().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("<select")
    contentAsString(result) must contain("Select person")
    contentAsString(result) must contain("value=\"1\"")
    contentAsString(result) must contain("value=\"3\"")
    contentAsString(result) must contain("value=\"4\"")
    contentAsString(result) must contain("value=\"6\"")
    contentAsString(result) must contain("First Tester")
    contentAsString(result) must contain("Third Tester")
    contentAsString(result) must contain("Fourth Tester")
    contentAsString(result) must contain("Sixth Tester")
  }

  def e23 = new MockContext {
    val controller = new TestMembers()
    val m = new Member(None, 0, person = true, funder = false,
      Money.parse("EUR 100"), LocalDate.now(), existingObject = true,
      DateTime.now(), 1L, DateTime.now(), 1L)
    Cache.set(Members.cacheId(1L), m, 1800)
    val service = mock[FakePersonService]
    (service.find(_: Long)).expects(*).returning(None)
    (service.findNonMembers _).expects().returning(List())
    controller.personService_=(service)
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredPostRequest(identity, "/member/existing/person").
      withFormUrlEncodedBody(("id", "1"))
    val result = controller.updateExistingPerson().apply(request)

    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("This person does not exist")
  }

  def e24 = new cleanup {
    new MockContext {
      val controller = new TestMembers()
      val m = new Member(None, 1L, person = true, funder = false,
        Money.parse("EUR 100"), LocalDate.now(), existingObject = true,
        DateTime.now(), 1L, DateTime.now(), 1L).insert
      val person = PersonHelper.one.copy(id = Some(1L))
      Cache.set(Members.cacheId(1L), m, 1800)
      val service = mock[FakePersonService]
      (service.find(_: Long)).expects(*).returning(Some(person))
      (service.findNonMembers _).expects().returning(List())
      controller.personService_=(service)
      val identity = StubLoginIdentity.editor
      val request = prepareSecuredPostRequest(identity, "/member/existing/person").
        withFormUrlEncodedBody(("id", "1"))
      val result = controller.updateExistingPerson().apply(request)

      status(result) must equalTo(BAD_REQUEST)
      contentAsString(result) must contain("This person is already a member")
    }
  }

  /**
   * Adds member data to post request and returns updated request
   * @param request Request
   * @return
   */
  private def addMemberData(
    request: FakeRequest[AnyContentAsEmpty.type],
    since: String = "2015-01-03",
    person: String = "1",
    existingObject: String = "0") = {
    request.withFormUrlEncodedBody(
      ("objectId", "0"), ("person", person),
      ("funder", "false"), ("fee.currency", "EUR"),
      ("fee.amount", "100"), ("since", since),
      ("existingObject", existingObject))
  }
}

trait cleanup extends After {
  def after = DB.withSession { implicit session: Session ⇒
    Q.updateNA("TRUNCATE `MEMBER`").execute
    Cache.remove(Members.cacheId(1L))
  }
}
