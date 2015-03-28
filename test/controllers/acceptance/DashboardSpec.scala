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

import _root_.integration.PlayAppSpec
import controllers.{ Dashboard, Security }
import helpers.{ BrandHelper, EvaluationHelper, EventHelper, PersonHelper }
import models._
import models.service.{ LicenseService, EvaluationService }
import org.joda.money.CurrencyUnit._
import org.joda.money.Money
import org.joda.time.{ DateTime, LocalDate }
import org.scalamock.specs2.MockContext
import play.api.mvc.SimpleResult
import stubs._

import scala.collection.mutable
import scala.concurrent.Future

class TestDashboard() extends Dashboard with Security with FakeServices

class DashboardSpec extends PlayAppSpec {

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

    On facilitator's dashboard there should be
      three nearest future events               $e7
      10 latest evaluations                     $e8

    On admin's dashboard there should be
      a list of facilitators with expiring licenses $e9

    Expiring licenses should
      not be visible to Viewer                  $e10
      not be visible to Editor                  $e11
  """

  def e1 = {
    val controller = new TestDashboard()
    val identity = FakeUserIdentity.viewer
    val request = prepareSecuredGetRequest(identity, "/about")
    val result: Future[SimpleResult] = controller.about().apply(request)
    status(result) must equalTo(SEE_OTHER)
  }

  def e2 = {
    val controller = new TestDashboard()
    val identity = FakeUserIdentity.editor
    val request = prepareSecuredGetRequest(identity, "/about")
    val result: Future[SimpleResult] = controller.about().apply(request)
    status(result) must equalTo(OK)
  }

  def e3 = {
    val controller = new TestDashboard()
    val identity = FakeUserIdentity.viewer
    val request = prepareSecuredGetRequest(identity, "/api")
    val result: Future[SimpleResult] = controller.api().apply(request)
    status(result) must equalTo(SEE_OTHER)
  }

  def e4 = {
    val controller = new TestDashboard()
    val identity = FakeUserIdentity.editor
    val request = prepareSecuredGetRequest(identity, "/api")
    val result: Future[SimpleResult] = controller.api().apply(request)
    status(result) must equalTo(OK)
  }

  def e5 = {
    val controller = new TestDashboard()
    val identity = FakeUserIdentity.viewer
    val request = prepareSecuredGetRequest(identity, "/")
    val result: Future[SimpleResult] = controller.index().apply(request)
    status(result) must equalTo(OK)
    contentAsString(result) must not contain "Latest activity"
  }

  def e6 = {
    val controller = new TestDashboard()
    val identity = FakeUserIdentity.editor
    val request = prepareSecuredGetRequest(identity, "/")
    val result: Future[SimpleResult] = controller.index().apply(request)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Latest activity")
  }

  def e7 = {
    new MockContext {
      //@TODO use FakeSecurity here
      val identity = FakeUserIdentity.viewer
      val request = prepareSecuredGetRequest(identity, "/")

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
      val identity = FakeUserIdentity.viewer
      val request = prepareSecuredGetRequest(identity, "/")

      val evaluationService = stub[EvaluationService]
      val evalStatus = EvaluationStatus.Pending
      val evaluations: List[(Event, Person, Evaluation)] = List(
        (EventHelper.one, PersonHelper.one(),
          EvaluationHelper.make(Some(13L), 1L, 1L, evalStatus, 8, DateTime.now().minusHours(13))),
        (EventHelper.one, PersonHelper.two(),
          EvaluationHelper.make(Some(2L), 1L, 2L, evalStatus, 8, DateTime.now().minusHours(12))),
        (EventHelper.one, PersonHelper.fast(3, "Three", "Lad"),
          EvaluationHelper.make(Some(3L), 1L, 3L, evalStatus, 8, DateTime.now().minusHours(11))),
        (EventHelper.one, PersonHelper.fast(4, "Four", "Girl"),
          EvaluationHelper.make(Some(4L), 1L, 4L, evalStatus, 8, DateTime.now().minusHours(10))),
        (EventHelper.one, PersonHelper.fast(5, "Five", "Lad"),
          EvaluationHelper.make(Some(5L), 1L, 5L, evalStatus, 8, DateTime.now().minusHours(9))),
        (EventHelper.one, PersonHelper.fast(6, "Six", "Girl"),
          EvaluationHelper.make(Some(6L), 1L, 6L, evalStatus, 8, DateTime.now().minusHours(8))),
        (EventHelper.one, PersonHelper.fast(7, "Seven", "Lad"),
          EvaluationHelper.make(Some(7L), 1L, 7L, evalStatus, 8, DateTime.now().minusHours(7))),
        (EventHelper.one, PersonHelper.fast(8, "Eight", "Girl"),
          EvaluationHelper.make(Some(8L), 1L, 8L, evalStatus, 8, DateTime.now().minusHours(6))),
        (EventHelper.one, PersonHelper.fast(9, "Nine", "Lad"),
          EvaluationHelper.make(Some(9L), 1L, 9L, evalStatus, 8, DateTime.now().minusHours(5))),
        (EventHelper.one, PersonHelper.fast(10, "Ten", "Girl"),
          EvaluationHelper.make(Some(10L), 1L, 10L, evalStatus, 8, DateTime.now().minusHours(4))),
        (EventHelper.one, PersonHelper.fast(11, "Eleven", "Lad"),
          EvaluationHelper.make(Some(11L), 1L, 11L, evalStatus, 8, DateTime.now().minusHours(3))))
      (evaluationService.findByEvents _).when(toMockParameter(List())).returns(evaluations)

      val eventService = stub[StubEventService]
      (eventService.findByFacilitator _).when(1L, None, *, *, *).returns(List())

      val controller = new TestDashboard()
      controller.eventService_=(eventService)
      controller.evaluationService_=(evaluationService)
      val result: Future[SimpleResult] = controller.index().apply(request)
      status(result) must equalTo(OK)
      contentAsString(result) must contain("Latest evaluations")
      contentAsString(result) must not contain "/evaluation/13"
      contentAsString(result) must contain("/evaluation/2")
      contentAsString(result) must contain("/evaluation/3")
      contentAsString(result) must contain("/evaluation/4")
      contentAsString(result) must contain("/evaluation/5")
      contentAsString(result) must contain("/evaluation/6")
      contentAsString(result) must contain("/evaluation/7")
      contentAsString(result) must contain("/evaluation/8")
      contentAsString(result) must contain("/evaluation/9")
      contentAsString(result) must contain("/evaluation/10")
      contentAsString(result) must contain("/evaluation/11")
    }
  }

  def e9 = {
    new MockContext {
      val identity = FakeUserIdentity.admin
      val request = prepareSecuredGetRequest(identity, "/")

      val facilitators = Map(1L -> PersonHelper.one(),
        2L -> PersonHelper.two())
      val now = LocalDate.now()
      val licenses = mutable.MutableList[LicenseLicenseeView]()
      Seq(
        (1L, now.withDayOfMonth(1), now.dayOfMonth().withMinimumValue()),
        (2L, now.minusYears(1), now.dayOfMonth().withMaximumValue())).foreach {
          case (licenseeId, start, end) ⇒ {
            val license = new License(None, licenseeId, 1L,
              "1", LocalDate.now().minusYears(1),
              start, end, true, Money.of(EUR, 100), Some(Money.of(EUR, 100)))
            licenses += LicenseLicenseeView(license, facilitators.get(licenseeId).get)
          }
        }

      val service = mock[FakeLicenseService]
      (service.expiring _).expects().returning(licenses.toList)
      val controller = new TestDashboard()
      controller.licenseService_=(service)
      val result: Future[SimpleResult] = controller.index().apply(request)
      status(result) must equalTo(OK)
      val title = "Expiring licenses in " + month(LocalDate.now().getMonthOfYear)
      contentAsString(result) must contain(title)
      contentAsString(result) must contain("/person/1")
      contentAsString(result) must contain("/person/2")
      contentAsString(result) must contain("EUR 100")
      contentAsString(result) must contain("First Tester")
      contentAsString(result) must contain("Second Tester")
      contentAsString(result) must contain(now.dayOfMonth().withMinimumValue().toString)
      contentAsString(result) must contain(now.dayOfMonth().withMaximumValue().toString)
      contentAsString(result) must not contain "/person/4"
      contentAsString(result) must not contain "/person/5"
    }
  }

  def e10 = {
    truncateTables()
    addLicenseData()
    val identity = FakeUserIdentity.viewer
    val request = prepareSecuredGetRequest(identity, "/")

    val controller = new TestDashboard()
    val result: Future[SimpleResult] = controller.index().apply(request)
    status(result) must equalTo(OK)
    val title = "Expiring licenses in " + month(LocalDate.now().getMonthOfYear)
    contentAsString(result) must not contain title
  }

  def e11 = {
    truncateTables()
    addLicenseData()
    val identity = FakeUserIdentity.editor
    val request = prepareSecuredGetRequest(identity, "/")

    val controller = new TestDashboard()
    val result: Future[SimpleResult] = controller.index().apply(request)
    status(result) must equalTo(OK)
    val title = "Expiring licenses in " + month(LocalDate.now().getMonthOfYear)
    contentAsString(result) must not contain title
  }

  /**
   * Returns name of month by its index
   * @param index Index of month
   */
  private def month(index: Int): String = {
    val months = Map(1 -> "January", 2 -> "February", 3 -> "March", 4 -> "April",
      5 -> "May", 6 -> "June", 7 -> "July", 8 -> "August", 9 -> "September",
      10 -> "October", 11 -> "November", 12 -> "December")
    months.getOrElse(index, "")
  }

  /**
   * Adds test data to database
   */
  private def addLicenseData() = {
    val facilitators = Map(1L -> PersonHelper.one(),
      2L -> PersonHelper.two(),
      3L -> PersonHelper.make(Some(3L), "Third", "Tester"),
      4L -> PersonHelper.make(Some(4L), "Fourth", "Tester"),
      5L -> PersonHelper.make(Some(5L), "Fifth", "Tester"))
    facilitators.foreach(v ⇒ v._2.insert)
    BrandHelper.one.insert
    val now = LocalDate.now()
    Seq(
      (1L, now.withDayOfMonth(1), now.dayOfMonth().withMinimumValue()),
      (2L, now.minusYears(1), now.dayOfMonth().withMaximumValue()),
      (4L, now.minusMonths(6), now.plusMonths(4)),
      (5L, now.minusMonths(4), now.plusMonths(1))).foreach {
        case (licenseeId, start, end) ⇒ {
          val license = new License(None, licenseeId, 1L,
            "1", LocalDate.now().minusYears(1),
            start, end, true, Money.of(EUR, 100), Some(Money.of(EUR, 100)))
          LicenseService.get.add(license)
        }
      }
  }
}
