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
import play.api.mvc.SimpleResult
import play.api.test.FakeRequest
import stubs.{ StubLoginIdentity, FakeServices }

import scala.concurrent.Future

class TestMembers() extends Members with Security with FakeServices

class MembersSpec extends PlayAppSpec {
  def setupDb() {}
  def cleanupDb() {}

  override def is = s2"""

  Page with a list of members should
    not be visible to unauthorized user                  $e1
    and be visible to authorized user                    $e2
  """

  def e1 = {
    val controller = new TestMembers()
    val result: Future[SimpleResult] = controller.index().apply(FakeRequest())

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome.which(_.contains("login"))
  }

  def e2 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.viewer
    val request = prepareSecuredRequest(identity, "/members/")
    val result: Future[SimpleResult] = controller.index().apply(request)
    status(result) must equalTo(OK)
  }
}
