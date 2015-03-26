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
import helpers.{ BrandHelper, EventHelper, PersonHelper }
import models._
import models.brand.EventType
import models.service.{ BrandService, EventService }
import org.joda.time.LocalDate
import org.scalamock.specs2.MockContext
import services.notifiers.Email
import stubs.FakeServices

class EventServiceSpec extends PlayAppSpec {

  override def setupDb() {
    PersonHelper.one().insert
    PersonHelper.two().insert
    PersonHelper.make(Some(4L), "Four", "Tester").insert
    PersonHelper.make(Some(5L), "Four", "Tester").insert
    BrandHelper.one.insert
    (new EventType(None, 1L, "Type 1", None)).insert
    (new EventType(None, 1L, "Type 2", None)).insert
    EventHelper.addEvents(BrandHelper.one.code)
    EventHelper.addEvents("MGT30")
  }

  lazy val event = EventHelper.make(
    title = Some("Daily Workshop"),
    city = Some("spb"),
    startDate = Some(LocalDate.parse("2014-05-12")))

  "A brand manager (id = 1)" >> {
    "with id = 1 be detected as a brand manager" in {
      event.isBrandManager(1) must beTrue
    }
    "with id = 5 not be detected as a brand manager" in {
      (event isBrandManager 5) must beFalse
    }
  }
  val service = new EventService

  "Method findByParameters" should {
    "return 6 events for default brand" in {
      service.findByParameters(Some(BrandHelper.one.code)).length mustEqual 6
    }
    "return 4 public events" in {
      val events = EventService.get.findByParameters(brandCode = None, public = Some(true))
      (events.length mustEqual 8) and
        (events.exists(_.title == "one") must beTrue) and
        (events.exists(_.title == "two") must beTrue) and
        (events.exists(_.title == "four") must beFalse) and
        (events.exists(_.title == "five") must beTrue) and
        (events.exists(_.title == "six") must beTrue)
    }
    "return 1 archived event" in {
      val events = service.findByParameters(brandCode = None, archived = Some(true))
      (events.length mustEqual 2) and
        (events.exists(_.title == "four") must beTrue)
    }
    "return 3 confirmed events" in {
      val events = service.findByParameters(brandCode = None, confirmed = Some(true))
      (events.length mustEqual 6) and
        (events.exists(_.title == "one") must beTrue) and
        (events.exists(_.title == "four") must beTrue) and
        (events.exists(_.title == "five") must beTrue) and
        (events.exists(_.title == "six") must beFalse)
    }
    "return 1 unconfirmed past events for brand TEST" in {
      val events = service.findByParameters(brandCode = Some("TEST"),
        future = Some(false),
        confirmed = Some(false))
      (events.length mustEqual 1) and
        (events.exists(_.title == "two") must beTrue) and
        (events.exists(_.title == "three") must beFalse)
    }
    "return 1 event in DE" in {
      val events = service.findByParameters(brandCode = None, country = Some("DE"))
      (events.length mustEqual 2) and
        (events.exists(_.title == "five") must beTrue)
    }
    "return 3 events with type = 2" in {
      val events = service.findByParameters(brandCode = None, eventType = Some(2))
      (events.length mustEqual 6) and
        (events.exists(_.title == "three") must beTrue) and
        (events.exists(_.title == "four") must beTrue) and
        (events.exists(_.title == "six") must beTrue) and
        (events.exists(_.title == "five") must beFalse)
    }
    "return 3 future events" in {
      val events = service.findByParameters(
        brandCode = Some("MGT30"),
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
        Some(BrandHelper.one.code))
      events.length mustEqual 4
    }
    "return 4 events facilitated by facilitator = 2" in {
      val events = service.findByFacilitator(2, None)
      events.length mustEqual 4
    }
    "return 0 events facilitated by facilitator = 3" in {
      service.findByFacilitator(3, None).length mustEqual 0
    }
  }

  "Method sendConfirmationAlert" should {
    "send 1 email and record an activity" in new MockContext {
      class StubBrandService extends BrandService {
        override def findAll: List[Brand] = List(BrandHelper.one)
      }
      class FakeEmail extends Email {
        override def send(to: Set[Person],
          cc: Option[Set[Person]] = None,
          bcc: Option[Set[Person]] = None,
          subject: String,
          body: String,
          richMessage: Boolean = false,
          attachment: Option[(String, String)] = None) = {
          val people: Set[Person] = Set(PersonHelper.one(), PersonHelper.two())
          to.size must_== people.size
          to.exists(_.fullName == "First Tester")
          to.exists(_.fullName == "Second Tester")
          cc must_== None
          bcc must_== None
          subject must_== "Confirm your event One"
        }
      }
      class TestEventService extends EventService with FakeServices {
        override def findByParameters(
          brandCode: Option[String],
          future: Option[Boolean] = None,
          public: Option[Boolean] = None,
          archived: Option[Boolean] = None,
          confirmed: Option[Boolean] = None,
          country: Option[String] = None,
          eventType: Option[Long] = None): List[Event] = List(EventHelper.one)

        override def email = new FakeEmail
      }
      val service = new TestEventService
      val brand = new StubBrandService
      service.brandService_=(brand)

      service.sendConfirmationAlert()

      val activities = Activity.findAll
      activities.length must_== 1
      activities.head.subject must_== "Teller"
      activities.head.predicate must_== Activity.Predicate.Sent.toString
      val msg = "confirmation email for event One (id = 1)"
      activities.head.activityObject must_== Some(msg)
    }
  }
}
