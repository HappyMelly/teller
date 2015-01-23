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
package integration

import helpers.{ BrandHelper, EventHelper }
import models.Brand
import models.service.EventService
import org.joda.time.LocalDate
import org.specs2.matcher.DataTables

class EventServiceSpec extends PlayAppSpec with DataTables {

  def setupDb() {
    BrandHelper.defaultBrand.insert
    EventHelper.addEvents(BrandHelper.defaultBrand.code)
    EventHelper.addEvents("MGT30")
  }

  def cleanupDb() {
    Brand.find(BrandHelper.defaultBrand.code).map(_.brand.delete())
    EventService.findAll.map(_.delete())
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

  "Method findByParameters" should {
    "return 6 events for default brand" in {
      EventService.findByParameters(Some(BrandHelper.defaultBrand.code)).length mustEqual 6
    }
    "return 4 public events" in {
      val events = EventService.findByParameters(brandCode = None, public = Some(true))
      (events.length mustEqual 8) and
        (events.exists(_.title == "one") must beTrue) and
        (events.exists(_.title == "two") must beTrue) and
        (events.exists(_.title == "four") must beFalse) and
        (events.exists(_.title == "five") must beTrue) and
        (events.exists(_.title == "six") must beTrue)
    }
    "return 1 archived event" in {
      val events = EventService.findByParameters(brandCode = None, archived = Some(true))
      (events.length mustEqual 2) and
        (events.exists(_.title == "four") must beTrue)
    }
    "return 3 confirmed events" in {
      val events = EventService.findByParameters(brandCode = None, confirmed = Some(true))
      (events.length mustEqual 6) and
        (events.exists(_.title == "one") must beTrue) and
        (events.exists(_.title == "four") must beTrue) and
        (events.exists(_.title == "five") must beTrue) and
        (events.exists(_.title == "six") must beFalse)
    }
    "return 1 event in DE" in {
      val events = EventService.findByParameters(brandCode = None, country = Some("DE"))
      (events.length mustEqual 2) and
        (events.exists(_.title == "five") must beTrue)
    }
    "return 3 events with type = 2" in {
      val events = EventService.findByParameters(brandCode = None, eventType = Some(2))
      (events.length mustEqual 6) and
        (events.exists(_.title == "three") must beTrue) and
        (events.exists(_.title == "four") must beTrue) and
        (events.exists(_.title == "six") must beTrue) and
        (events.exists(_.title == "five") must beFalse)
    }
    "return 3 future events" in {
      val events = EventService.findByParameters(
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
      val events = EventService.findByFacilitator(
        1,
        Some(BrandHelper.defaultBrand.code))
      events.length mustEqual 4
    }
    "return 4 events facilitated by facilitator = 2" in {
      val events = EventService.findByFacilitator(2, None)
      events.length mustEqual 4
    }
    "return 0 events facilitated by facilitator = 3" in {
      EventService.findByFacilitator(3, None).length mustEqual 0
    }
  }

}
