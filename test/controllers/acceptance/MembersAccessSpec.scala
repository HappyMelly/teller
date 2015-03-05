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

import controllers._
import integration.PlayAppSpec
import org.scalamock.specs2.MockContext
import org.specs2.matcher._
import play.api.mvc.SimpleResult
import play.api.test.FakeRequest
import stubs.{ FakeMemberService, StubUserIdentity, FakeServices }

import scala.concurrent.Future

/** Contains only access tests */
class MembersAccessSpec extends PlayAppSpec with DataTables {
  class TestMembers() extends Members with Security with FakeServices

  def setupDb() {}
  def cleanupDb() {}

  override def is = s2"""

  Page with a list of members should
    not be visible to unauthorized user                  $e1
    and be visible to authorized user                    $e2

  Add Fee form should
    not be accessible to Viewers                         $e4
    be accessible to Editors                             $e5

  Edit Fee form should
    not be accessible to Viewers                         $e6
    be accessible to Editors                             $e7

  Update membershipt action should
    not be accessible to Viewers                         $e8
    be accessible to Editors                             $e9

  Add new organisation form should
    not be accessible to Viewers                         $e15
    be accessible to Editors                             $e16

  Add new person form should
    not be accessible to Viewers                         $e17
    be accessible to Editors                             $e18

  Add existing organisation form should
    not be accessible to Viewers                         $e20
    be accessible to Editors                             $e21

  Add existing person form should
    not be accessible to Viewers                         $e22
    be accessible to Editors                             $e23

  Delete action should
    not be accessible to Viewers                         $e24
    be accessible to Editors                             $e25

  """

  val controller = new TestMembers()

  def e1 = {
    val result: Future[SimpleResult] = controller.index().apply(FakeRequest())

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome.which(_.contains("login"))
  }

  def e2 = {
    new MockContext {
      val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
      val service = mock[FakeMemberService]
      (service.findAll _).expects().returning(List()).once()
      controller.memberService_=(service)
      val result: Future[SimpleResult] = controller.index().apply(req)

      status(result) must equalTo(OK)
    }
  }

  def e4 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.add().apply(req)

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/")
  }

  def e5 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.editor, "/")
    val result: Future[SimpleResult] = controller.add().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add member")
  }

  def e6 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.edit(1L).apply(req)

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/")
  }

  def e7 = new MockContext {
    val req = prepareSecuredGetRequest(StubUserIdentity.editor, "/")
    val service = mock[FakeMemberService]
    (service.find _).expects(1L, true).returning(None)
    controller.memberService_=(service)
    val result: Future[SimpleResult] = controller.edit(1L).apply(req)

    status(result) must equalTo(NOT_FOUND)
  }
  def e8 = {
    val req = prepareSecuredPostRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.update(1L).apply(req)

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/")
  }

  def e9 = new MockContext {
    val req = prepareSecuredPostRequest(StubUserIdentity.editor, "/")
    val service = mock[FakeMemberService]
    (service.find _).expects(1L, true).returning(None)
    controller.memberService_=(service)
    val result: Future[SimpleResult] = controller.update(1L).apply(req)

    status(result) must equalTo(NOT_FOUND)
  }

  def e15 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.addOrganisation().apply(req)

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/")
  }

  def e16 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.editor, "/")
    val result: Future[SimpleResult] = controller.addOrganisation().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add member")
    contentAsString(result) must contain("Step 2: New organisation")
  }

  def e17 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.addOrganisation().apply(req)

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/")
  }

  def e18 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.editor, "/")
    val result: Future[SimpleResult] = controller.addPerson().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add member")
    contentAsString(result) must contain("Step 2: New person")
  }

  def e20 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.addExistingOrganisation().apply(req)

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/")
  }

  def e21 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.editor, "/")
    val result: Future[SimpleResult] = controller.addExistingOrganisation().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add member")
    contentAsString(result) must contain("Step 2: Existing organisation")
  }

  def e22 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.addExistingPerson().apply(req)

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/")
  }

  def e23 = {
    val req = prepareSecuredGetRequest(StubUserIdentity.editor, "/")
    val result: Future[SimpleResult] = controller.addExistingPerson().apply(req)

    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add member")
    contentAsString(result) must contain("Step 2: Existing person")
  }

  def e24 = {
    val req = prepareSecuredPostRequest(StubUserIdentity.viewer, "/")
    val result: Future[SimpleResult] = controller.delete(1L).apply(req)

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/")
  }

  def e25 = new MockContext {
    val memberService = mock[FakeMemberService]
    // the existance of call itself proves that the authentication passed
    (memberService.find(_, _)).expects(1L, true).returning(None)
    controller.memberService_=(memberService)
    val req = prepareSecuredPostRequest(StubUserIdentity.editor, "/")
    val result: Future[SimpleResult] = controller.delete(1L).apply(req)

    status(result) must equalTo(NOT_FOUND)
  }
}

