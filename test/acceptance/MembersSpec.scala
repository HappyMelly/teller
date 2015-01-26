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
import org.scalamock.specs2.MockContext
import play.api.mvc.SimpleResult
import play.api.test.FakeRequest
import stubs.{ FakeMemberService, StubLoginIdentity, FakeServices }

import scala.concurrent.Future

class TestMembers() extends Members with Security with FakeServices

class MembersSpec extends PlayAppSpec {
  def setupDb() {}
  def cleanupDb() {}

  override def is = s2"""

  Page with a list of members should
    not be visible to unauthorized user                  $e1
    and be visible to authorized user                    $e2
    show all members sorted by names                     $e3

  Add form should
    not be accessible to Viewers                         $e4
    be accessible to Editors                             $e5
  """

  def e1 = {
    val controller = new TestMembers()
    val result: Future[SimpleResult] = controller.index().apply(FakeRequest())

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome.which(_.contains("login"))
  }

  def e2 = {
    new MockContext {
      val controller = new TestMembers()
      val identity = StubLoginIdentity.viewer
      val request = prepareSecuredRequest(identity, "/members/")

      val service = mock[FakeMemberService]
      (service.findAll _).expects().returning(List()).once()
      controller.memberService_=(service)

      val result: Future[SimpleResult] = controller.index().apply(request)
      status(result) must equalTo(OK)
    }
  }

  def e3 = {
    //@TODO use FakeSecurity here
    val identity = StubLoginIdentity.viewer
    val request = prepareSecuredRequest(identity, "/members")

    val controller = new TestMembers()
    val result: Future[SimpleResult] = controller.index().apply(request)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Members")
    contentAsString(result) must contain("/member/1")
    contentAsString(result) must contain("/member/2")
    contentAsString(result) must contain("/member/3")
    contentAsString(result) must contain("/member/4")
    //@TODO finish multiple checks
  }

  def e4 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.viewer
    val request = prepareSecuredRequest(identity, "/member/")

    val result: Future[SimpleResult] = controller.add().apply(request)
    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/")
  }

  def e5 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredRequest(identity, "/member/")

    val result: Future[SimpleResult] = controller.add().apply(request)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add member")
  }
}
