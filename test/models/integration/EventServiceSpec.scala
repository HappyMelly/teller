/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
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
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing Happy Melly One, Handelsplein 37, Rotterdam,
 * The Netherlands, 3071 PR
 */
package models.integration

import _root_.integration.PlayAppSpec
import helpers.{ EvaluationHelper, BrandHelper, EventHelper, PersonHelper }
import models._
import models.brand.EventType
import models.repository.{ ActivityRepository, EvaluationRepository, BrandRepository, EventRepository }
import models.repository.brand.EventTypeRepository
import org.joda.time.{ DateTime, LocalDate }
import org.scalamock.specs2.MockContext
import services.integrations.Email
import stubs.FakeRepositories

class EventServiceSpec extends PlayAppSpec {

  override def setupDb() {
    PersonHelper.one().insert
    PersonHelper.two().insert
    PersonHelper.make(Some(3L), "Three", "Tester").insert
    PersonHelper.make(Some(4L), "Four", "Tester").insert
    BrandHelper.one.insert
    val service = new EventTypeRepository
    service.insert(new EventType(None, 1L, "Type 1", None, 16, false))
    service.insert(new EventType(None, 1L, "Type 2", None, 16, false))
    EventHelper.addEvents(1L)
    EventHelper.addEvents(2L)
  }

  lazy val event = EventHelper.make(
    title = Some("Daily Workshop"),
    city = Some("spb"),
    startDate = Some(LocalDate.parse("2014-05-12")))

  val service = new EventRepository

  "Method findByParameters" should {
    "return 6 events for default brand" in {
      service.findByParameters(Some(1L)).length mustEqual 6
    }
    "return 4 public events" in {
      val events = EventService.get.findByParameters(brandId = None, public = Some(true))
      (events.length mustEqual 8) and
        (events.exists(_.title == "one") must beTrue) and
        (events.exists(_.title == "two") must beTrue) and
        (events.exists(_.title == "four") must beFalse) and
        (events.exists(_.title == "five") must beTrue) and
        (events.exists(_.title == "six") must beTrue)
    }
    "return 1 archived event" in {
      val events = service.findByParameters(brandId = None, archived = Some(true))
      (events.length mustEqual 2) and
        (events.exists(_.title == "four") must beTrue)
    }
    "return 3 confirmed events" in {
      val events = service.findByParameters(brandId = None, confirmed = Some(true))
      (events.length mustEqual 6) and
        (events.exists(_.title == "one") must beTrue) and
        (events.exists(_.title == "four") must beTrue) and
        (events.exists(_.title == "five") must beTrue) and
        (events.exists(_.title == "six") must beFalse)
    }
    "return 1 unconfirmed past events for brand TEST" in {
      val events = service.findByParameters(brandId = Some(1L),
        future = Some(false),
        confirmed = Some(false))
      (events.length mustEqual 1) and
        (events.exists(_.title == "two") must beTrue) and
        (events.exists(_.title == "three") must beFalse)
    }
    "return 1 event in DE" in {
      val events = service.findByParameters(brandId = None, country = Some("DE"))
      (events.length mustEqual 2) and
        (events.exists(_.title == "five") must beTrue)
    }
    "return 3 events with type = 2" in {
      val events = service.findByParameters(brandId = None, eventType = Some(2))
      (events.length mustEqual 6) and
        (events.exists(_.title == "three") must beTrue) and
        (events.exists(_.title == "four") must beTrue) and
        (events.exists(_.title == "six") must beTrue) and
        (events.exists(_.title == "five") must beFalse)
    }
    "return 3 future events" in {
      val events = service.findByParameters(
        brandId = Some(2L),
        future = Some(true))
      (events.length mustEqual 3) and
        (events.exists(_.title == "three") must beFalse) and
        (events.exists(_.title == "four") must beTrue) and
        (events.exists(_.title == "six") must beTrue) and
        (events.exists(_.title == "five") must beTrue)
    }
  }

  "Method findByFacilitator" should {
    "return 4 events for default brand and facilitator = 1" in {
      val events = service.findByFacilitator(
        1,
        Some(1L))
      events.length mustEqual 4
    }
    "return 4 events facilitated by facilitator = 2" in {
      val events = service.findByFacilitator(2, None)
      events.length mustEqual 4
    }
    "return 0 events facilitated by facilitator = 5" in {
      service.findByFacilitator(5, None).length mustEqual 0
    }
  }

  "Method findByEvaluation" should {
    "return an event connected to an evaluation 1" in {
      val eval = EvaluationHelper.make(Some(1L), 1L, 1L, EvaluationStatus.Approved, 10, DateTime.now())
      EvaluationService.get.add(eval)
      val event = service.findByEvaluation(1L)
      event map { x ⇒
        x.id must_== Some(1L)
        x.title must_== "one"
      } getOrElse ko
    }
    "return no event connected to an evaluation 4" in {
      service.findByEvaluation(4L) must_== None
    }
  }

  "Method updateRating" should {
    "set new rating to 6.5" in {
      val view = EventView(EventHelper.one, EventInvoice.empty.copy(invoiceTo = 1))
      val event = EventService.get.insert(view).event
      event.rating must_== 0.0f
      EventService.get.updateRating(event.id.get, 6.5f)
      EventService.get.find(event.id.get) map { x ⇒
        x.rating must_== 6.5f
      } getOrElse ko
    }
    "throw no exception if the event doesn't exist" in {
      EventService.get.updateRating(567, 7.0f)
      ok
    }
  }

  "Method 'confirm'" should {
    "update event confirmed state in database" in {
      val id = 2L
      service.find(id) map { x ⇒
        x.confirmed must_== false
        service.confirm(id)
        service.find(id) map { y ⇒
          y.confirmed must_== true
        } getOrElse ko
      } getOrElse ko
    }
  }
}
