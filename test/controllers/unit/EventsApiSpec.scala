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
package controllers.unit

import controllers.apiv2.{ ApiAuthentication, EventsApi }
import helpers.{ EventHelper, PersonHelper }
import models.Event
import org.scalamock.specs2.MockContext
import org.specs2.mutable._
import play.api.libs.json._
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import stubs.{ FakeApiAuthentication, FakeServices, StubEventService }

import scala.concurrent.Future

class EventsApiSpec extends Specification {

  /** Test controller without api authentication and with stubbed services */
  class TestEventsApi() extends EventsApi
    with FakeApiAuthentication
    with FakeServices

  /** Test controller with api authentication and with stubbed services */
  class AnotherTestEventsApi() extends EventsApi
    with ApiAuthentication
    with FakeServices

  "Event details API call" should {
    "return event details in JSON format" in {
      val controller = new TestEventsApi()
      val result: Future[SimpleResult] = controller.event(1).apply(FakeRequest())
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      charset(result) must beSome("utf-8")
      contentAsJson(result) mustEqual Json.obj(
        "brand" -> "TEST",
        "type" -> 1,
        "title" -> "Test event",
        "description" -> None.asInstanceOf[Option[String]],
        "spokenLanguages" -> Json.arr("German"),
        "materialsLanguage" -> None.asInstanceOf[Option[String]],
        "specialAttention" -> None.asInstanceOf[Option[String]],
        "start" -> "2015-01-20",
        "end" -> "2015-01-20",
        "hoursPerDay" -> 1,
        "totalHours" -> 1,
        "facilitators" -> Json.arr(
          PersonHelper.oneAsJson(),
          PersonHelper.twoAsJson()),
        "city" -> "spb",
        "country" -> "RU",
        "website" -> None.asInstanceOf[Option[String]],
        "registrationPage" -> None.asInstanceOf[Option[String]],
        "public" -> true,
        "archived" -> false)
    }
    "return 404 error with error message when an event doesn't exist" in {
      val controller = new TestEventsApi()
      val result: Future[SimpleResult] = controller.event(101).apply(FakeRequest())
      status(result) must equalTo(NOT_FOUND)
      contentType(result) must beSome("text/plain")
      contentAsString(result) mustEqual "Unknown event"
    }
    "return 401 error if api_token is not provided" in {
      val controller = new AnotherTestEventsApi()
      val result: Future[SimpleResult] = controller.event(1).apply(FakeRequest())
      status(result) must equalTo(UNAUTHORIZED)
      contentAsString(result) mustEqual "Unauthorized"
    }
  }

  "Events API call" should {
    "pass all parameters to findByFacilitator in a right order" in new MockContext {
      val service = mock[StubEventService]
      inSequence {
        (service.findByFacilitator _)
          .expects(1, Some("TEST"), None, Some(true), Some(false))
          .returning(List[Event]())
        (service.applyFacilitators _).expects(*)
      }
      val controller = new TestEventsApi()
      controller.eventService_=(service)
      controller.events(
        "TEST",
        future = None,
        public = Some(true),
        archived = Some(false),
        facilitatorId = Some(1),
        countryCode = Some("UK"),
        eventType = Some(1)).apply(FakeRequest())
    }

    "pass all parameters to findByParameters in a right order" in new MockContext {
      val service = mock[StubEventService]
      inSequence {
        (service.findByParameters _)
          .expects(Some("TEST"), None, Some(true), Some(false), None, Some("UK"), Some(1L))
          .returning(List[Event]())
        (service.applyFacilitators _).expects(*)
      }
      val controller = new TestEventsApi()
      controller.eventService_=(service)
      controller.events(
        "TEST",
        future = None,
        public = Some(true),
        archived = Some(false),
        facilitatorId = None,
        countryCode = Some("UK"),
        eventType = Some(1)).apply(FakeRequest())
    }

    "return events in JSON format" in new MockContext {
      val service = mock[StubEventService]
      inSequence {
        (service.findByFacilitator _)
          .expects(*, *, *, *, *)
          .returning(List[Event](EventHelper.one, EventHelper.two))
        (service.applyFacilitators _).expects(*)
      }
      val controller = new TestEventsApi()
      controller.eventService_=(service)
      val result: Future[SimpleResult] = controller.events(
        "TEST",
        future = None,
        public = Some(true),
        archived = Some(false),
        facilitatorId = Some(1),
        countryCode = Some("UK"),
        eventType = Some(1)).apply(FakeRequest())
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      charset(result) must beSome("utf-8")
      contentAsJson(result) mustEqual Json.arr(
        EventHelper.oneAsJson,
        EventHelper.twoAsJson)
    }
    "return 401 error if api_token is not provided" in {
      val controller = new AnotherTestEventsApi()
      val result: Future[SimpleResult] = controller.events(
        "TEST",
        future = None,
        public = Some(true),
        archived = Some(false),
        facilitatorId = Some(1),
        countryCode = Some("UK"),
        eventType = Some(1)).apply(FakeRequest())
      status(result) must equalTo(UNAUTHORIZED)
      contentAsString(result) mustEqual "Unauthorized"
    }
  }

}
