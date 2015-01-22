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

import controllers.{ Dashboard, Security }
import integration.PlayAppSpec
import play.api.mvc.SimpleResult
import play.api.test.Helpers._
import stubs.StubLoginIdentity

import scala.concurrent.Future

class TestDashboard() extends Dashboard with Security

class DashboardSpec extends PlayAppSpec {
  def setupDb() {}
  def cleanupDb() {}

  override def is = s2"""

  About page should
    not be visible to Viewer                  $e1
    and be visible to Editor                  $e2

  API page should
    not be visible to Viewer                  $e3
    and be visible to Editor                  $e4

  Activity stream on the dashboard should
    not be visible to Viewer                  $e5
    and be visible to Editor                  $e6
  """

  def e1 = {
    val controller = new TestDashboard()
    val identity = StubLoginIdentity.viewer
    val request = prepareSecuredRequest(identity, "/about")
    val result: Future[SimpleResult] = controller.about().apply(request)
    status(result) must equalTo(SEE_OTHER)
  }

  def e2 = {
    val controller = new TestDashboard()
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredRequest(identity, "/about")
    val result: Future[SimpleResult] = controller.about().apply(request)
    status(result) must equalTo(OK)
  }

  def e3 = {
    val controller = new TestDashboard()
    val identity = StubLoginIdentity.viewer
    val request = prepareSecuredRequest(identity, "/api")
    val result: Future[SimpleResult] = controller.api().apply(request)
    status(result) must equalTo(SEE_OTHER)
  }

  def e4 = {
    val controller = new TestDashboard()
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredRequest(identity, "/api")
    val result: Future[SimpleResult] = controller.api().apply(request)
    status(result) must equalTo(OK)
  }

  def e5 = {
    val controller = new TestDashboard()
    val identity = StubLoginIdentity.viewer
    val request = prepareSecuredRequest(identity, "/")
    val result: Future[SimpleResult] = controller.index().apply(request)
    status(result) must equalTo(OK)
    contentAsString(result) must not contain "Latest activity"
  }

  def e6 = {
    val controller = new TestDashboard()
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredRequest(identity, "/")
    val result: Future[SimpleResult] = controller.index().apply(request)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Latest activity")
  }
}
