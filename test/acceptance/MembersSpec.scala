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
import integration.PlayAppSpec
import org.joda.time.LocalDate
import org.specs2.matcher._
import org.specs2.mutable.After
import play.api.cache.Cache
import play.api.mvc.{ AnyContentAsEmpty, SimpleResult }
import play.api.Play.current
import play.api.test.FakeRequest
import stubs.{ StubLoginIdentity, FakeServices }

import scala.concurrent.Future

class MembersSpec extends PlayAppSpec with DataTables {
  class TestMembers() extends Members with Security with FakeServices

  def setupDb() {}
  def cleanupDb() {}

  override def is = s2"""

  Page with a list of members should
    show all members sorted by names                     $e3

  Editor should
    not be able to add new member with wrong parameters            $e6
    get a correct error message if membership date is too early    $e7
    get a correct error message if membership date is too late     $e8
    be redirected to 'New Organisation' form if he chose 'Org'     $e9
    be redirected to 'New Person' form if he chose 'Person'        $e10
    be redirected to 'Existing Org' form if he chose 'Org'         $e11
    be redirected to 'Existing Person' form if he chose 'Person'   $e12

  If an editor tries to create an organisation without creating membership fee first then
    she should get an error message                                $e14


  If an editor tries to create a person without creating membership fee first then
    she should get an error message                                       $e19
  """

  val controller = new TestMembers()

  def e3 = {
    //@TODO use FakeSecurity here
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
      "0" !! "1" ! "false" ! "" ! "100" ! "2015-01-01" ! "true" |
      // unknown currency
      "0" !! "1" ! "false" ! "TERES" ! "100" ! "2015-01-01" ! "true" |
      // negative amount
      "0" !! "1" ! "false" ! "EUR" ! "-100" ! "2015-01-01" ! "true" |
      // zero amount
      "0" !! "1" ! "false" ! "EUR" ! "0.00" ! "2015-01-01" ! "false" |
      // empty since
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! "" ! "false" |
      // wrong since
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! "31-312-321" ! "true" |
      // since earlier than 2015-01-01
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! "2014-12-31" ! "false" |
      // since later than today
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! LocalDate.now().plusDays(1).toString ! "false" |
      // empty 'funder'
      "0" !! "1" ! "" ! "EUR" ! "105.05" ! "2015-01-01" ! "true" |
      // non-boolean 'funder'
      "0" !! "1" ! "1.00" ! "EUR" ! "105.05" ! "2015-01-01" ! "false" |
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
    val uReq = addMemberData(req, person = "0", existingObject = "true")
    val result: Future[SimpleResult] = controller.create().apply(uReq)

    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").nonEmpty must_== true
    headers(result).get("Location").get must_== "/member/existing/organisation"
  }

  def e12 = new cleanup {
    val req = prepareSecuredPostRequest(StubLoginIdentity.editor, "/")
    val uReq = addMemberData(req, person = "1", existingObject = "true")
    val result: Future[SimpleResult] = controller.create().apply(uReq)

    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").nonEmpty must_== true
    headers(result).get("Location").get must_== "/member/existing/person"
  }

  def e14 = {
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredPostRequest(identity, "/member/organisation").
      withFormUrlEncodedBody(("name", "Test"), ("country", "RU"))
    val result = controller.createNewOrganisation().apply(request)

    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("You are trying to complete step 2 while adding new member without completing step 1")
  }

  def e19 = {
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredPostRequest(identity, "/member/person").
      withFormUrlEncodedBody(("emailAddress", "ttt@ttt.ru"), ("address.country", "RU"),
        ("firstName", "Test"), ("lastName", "Test"), ("signature", "false"),
        ("role", "0"))
    val result = controller.createNewPerson().apply(request)

    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("You are trying to complete step 2 while adding new member without completing step 1")
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
    existingObject: String = "false") = {
    request.withFormUrlEncodedBody(
      ("objectId", "0"), ("person", person),
      ("funder", "false"), ("fee.currency", "EUR"),
      ("fee.amount", "100"), ("since", since),
      ("existingObject", existingObject))
  }
}

trait cleanup extends After {
  def after = Cache.remove(Members.cacheId(1L))
}
