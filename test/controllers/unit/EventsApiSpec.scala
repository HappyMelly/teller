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

import controllers.api.{ ApiAuthentication, EventsApi }
import helpers.{ BrandHelper, EventHelper, PersonHelper }
import models.Event
import models.repository.cm.brand.EventTypeRepository
import models.repository.cm.{EventRepository, BrandRepository}
import org.scalamock.specs2.IsolatedMockFactory
import org.specs2.mutable._
import play.api.libs.json._
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import stubs.{ FakeApiAuthentication, FakeRepositories }

import scala.concurrent.Future

class EventsApiSpec extends Specification with IsolatedMockFactory {

  /** Test controller without api authentication and with stubbed services */
  class TestEventsApi() extends EventsApi
    with FakeApiAuthentication
    with FakeRepositories

  /** Test controller with api authentication and with stubbed services */
  class AnotherTestEventsApi() extends EventsApi
    with ApiAuthentication
    with FakeRepositories

  val brandService = mock[BrandRepository]
  val eventService = mock[EventRepository]
  val eventTypeService = mock[EventTypeRepository]
  val controller = new TestEventsApi()
  controller.eventService_=(eventService)
  controller.brandService_=(brandService)
  controller.eventTypeService_=(eventTypeService)

  "Event details API call" should {
    "return event details in JSON format" in {
      (services.eventService.find _) expects 1L returning Some(EventHelper.one)
      val result: Future[Result] = controller.event(1).apply(FakeRequest())
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      charset(result) must beSome("utf-8")
      contentAsJson(result) mustEqual Json.obj(
        "brand" -> 1,
        "type" -> 1,
        "title" -> "One",
        "description" -> None.asInstanceOf[Option[String]],
        "spokenLanguages" -> Json.arr("German"),
        "materialsLanguage" -> None.asInstanceOf[Option[String]],
        "specialAttention" -> None.asInstanceOf[Option[String]],
        "start" -> "2015-01-01",
        "end" -> "2015-01-02",
        "hoursPerDay" -> 1,
        "totalHours" -> 1,
        "facilitators" -> Json.arr(
          PersonHelper.oneAsJson(),
          PersonHelper.twoAsJson()),
        "city" -> "spb",
        "country" -> "RU",
        "website" -> None.asInstanceOf[Option[String]],
        "registrationPage" -> None.asInstanceOf[Option[String]],
        "rating" -> 0.0f,
        "public" -> true,
        "archived" -> false,
        "confirmed" -> false,
        "free" -> false,
        "online" -> false)
    }
    "return 404 error with error message when an event doesn't exist" in {
      (services.eventService.find _) expects 101L returning None

      val result: Future[Result] = controller.event(101).apply(FakeRequest())
      status(result) must equalTo(NOT_FOUND)
      contentType(result) must beSome("application/json")
      contentAsString(result) must contain("Unknown event")
    }
  }

  "Events API call" should {
    "pass all parameters to findByFacilitator in a right order" in {
      inSequence {
        (services.brandService.find(_: String)) expects "TEST" returning Some(BrandHelper.one)
        (services.cm.rep.brand.eventTypeService.findByBrand _) expects 1L returning List()
        (services.eventService.findByFacilitator _)
          .expects(1, Some(1L), None, Some(true), Some(false))
          .returning(List[Event]())
        (services.eventService.applyFacilitators _).expects(*)
      }
      controller.events(
        "TEST",
        future = None,
        public = Some(true),
        archived = Some(false),
        facilitatorId = Some(1),
        countryCode = Some("UK"),
        eventType = Some(1)).apply(FakeRequest())
      // we check passed parameters, so we don't care about result
      ok
    }

    "pass all parameters to findByParameters in a right order" in {
      inSequence {
        (services.brandService.find(_: String)) expects "TEST" returning Some(BrandHelper.one)
        (services.cm.rep.brand.eventTypeService.findByBrand _) expects 1L returning List()
        (services.eventService.findByParameters _)
          .expects(Some(1L), None, Some(true), Some(false), None, Some("UK"), Some(1L))
          .returning(List[Event]())
        (services.eventService.applyFacilitators _).expects(*)
      }
      controller.events(
        "TEST",
        future = None,
        public = Some(true),
        archived = Some(false),
        facilitatorId = None,
        countryCode = Some("UK"),
        eventType = Some(1)).apply(FakeRequest())
      // we check passed parameters, so we don't care about result
      ok
    }

    "return events in JSON format" in {
      inSequence {
        (services.brandService.find(_: String)) expects "TEST" returning Some(BrandHelper.one)
        (services.cm.rep.brand.eventTypeService.findByBrand _) expects 1L returning List()
        (services.eventService.findByFacilitator _)
          .expects(*, *, *, *, *)
          .returning(List[Event](EventHelper.one, EventHelper.two))
        (services.eventService.applyFacilitators _) expects *
      }
      val result: Future[Result] = controller.events(
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
  }

}
