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
import helpers.EventHelper
import integration.PlayAppSpec
import org.scalamock.specs2.MockContext
import play.api.mvc.SimpleResult
import play.api.test.Helpers._
import stubs.{ StubEventService, FakeServices, StubLoginIdentity }

import scala.concurrent.Future

class TestDashboard() extends Dashboard with Security with FakeServices

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

    On facilitator's dashboard should be
      three nearest future events               $e7
      10 latest evaluations                     $e8
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

  def e7 = {
    new MockContext {
      //@TODO use FakeSecurity here
      val identity = StubLoginIdentity.viewer
      val request = prepareSecuredRequest(identity, "/")

      val events = List(EventHelper.future(1, 1), EventHelper.future(2, 2),
        EventHelper.future(3, 3), EventHelper.future(4, 4),
        EventHelper.past(5, 3), EventHelper.past(6, 2))
      val service = stub[StubEventService]
      (service.findByFacilitator _).when(1L, None, *, *, *).returns(events)

      val controller = new TestDashboard()
      controller.eventService_=(service)
      val result: Future[SimpleResult] = controller.index().apply(request)
      status(result) must equalTo(OK)
      contentAsString(result) must contain("Upcoming events")
      contentAsString(result) must contain("/event/1")
      contentAsString(result) must contain("/event/2")
      contentAsString(result) must contain("/event/3")
      contentAsString(result) must not contain "/event/4"
      contentAsString(result) must not contain "/event/5"
      contentAsString(result) must not contain "/event/6"
    }
  }

  def e8 = {
    new MockContext {
      //@TODO use FakeSecurity here
      val identity = StubLoginIdentity.viewer
      val request = prepareSecuredRequest(identity, "/")

      val events = List(EventHelper.past(1, 1), EventHelper.past(2, 2),
        EventHelper.past(3, 3), EventHelper.past(4, 4),
        EventHelper.past(5, 3), EventHelper.past(6, 2))
      val service = stub[StubEventService]
      (service.findByFacilitator _).when(1L, None, *, *, *).returns(events)

      val controller = new TestDashboard()
      controller.eventService_=(service)
      val result: Future[SimpleResult] = controller.index().apply(request)
      status(result) must equalTo(OK)
      contentAsString(result) must contain("Latest evaluations")
    }
  }
}
