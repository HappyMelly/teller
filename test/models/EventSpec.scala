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
package models

import helpers.{ BrandHelper, EventHelper }
import integration.{ WithTestApp, PlayAppSpec }
import org.joda.time.LocalDate
import org.specs2.execute._
import org.specs2.specification._
import org.specs2.matcher.DataTables
import play.api.test.FakeApplication
import play.api.test.Helpers._
import play.api.Play

class EventSpec extends PlayAppSpec with DataTables with WithTestApp {

  def setupDb() {
    BrandHelper.defaultBrand.insert
    addEvents(BrandHelper.defaultBrand.code)
    addEvents("MGT30")
  }

  def cleanupDb() {
    Brand.find(BrandHelper.defaultBrand.code).map(_.brand.delete())
    Event.findAll.map(_.delete())
  }

  lazy val event = EventHelper.makeEvent(
    title = Some("Daily Workshop"),
    city = Some("spb"),
    startDate = Some(LocalDate.parse("2014-05-12")))
  val tooLongTitle = "This title is just too long and should be truncated by the system to 70 characters"

  "Long title of an event" should {
    "contain a title, a city and a start date" in {
      event.longTitle mustEqual "Daily Workshop / spb / 2014-05-12"
    }
  }
  "If a title is > 70 characters long it" should {
    "be truncated and has a length = 70" in {
      event.copy(title = tooLongTitle).longTitle.length mustEqual
        (70 + " / spb / 2014-05-12".length)
    }
    "not be contained fully in a long title" in {
      event.copy(title = tooLongTitle).longTitle mustNotEqual
        tooLongTitle + " / spb / 2014-05-12"
    }
  }

  val langEvent = EventHelper.makeEvent(
    spokenLanguage = Option("DE"),
    secondSpokenLanguage = Option("EN"),
    materialsLanguage = Option("PT"))

  "Event" should {
    "have two spoken languages divided by slash" in {
      langEvent.spokenLanguage mustEqual "German / English"
    }
    "have English as a materials language" in {
      langEvent.materialsLanguage mustEqual Some("Portuguese")
    }
    "have two spoken languages returned as a list" in {
      langEvent.spokenLanguages mustEqual List("German", "English")
    }
  }
  val noMaterialsEvent = EventHelper.makeEvent(spokenLanguage = Option("EN"))
  "Event" should {
    "have one spoken language" in {
      noMaterialsEvent.spokenLanguage mustEqual "English"
    }
    "have an empty materials language" in {
      noMaterialsEvent.materialsLanguage mustEqual None
    }
  }

  event.facilitatorIds_=(List(2L, 3L, 4L, 6L))

  "A brand manager (id = 1)" should {
    "be detected as a brand manager" in {
      event.isBrandManager(1) must beTrue
    }
    "be able to facilitate an event" in {
      event.canFacilitate(1) must beTrue
    }
  }
  "A random person (id = 5)" should {
    val id = 5
    "not be detected as a brand manager" in {
      event.isBrandManager(id) must beFalse
    }
    "not be able to facilitate the event" in {
      event.canFacilitate(id) must beFalse
    }
  }

  "A facilitator" should {
    "be able to facilitate events" in {
      Result.unit {
        List(2, 4, 6) foreach { i ⇒ event.canFacilitate(i) must beTrue }
      }
    }
  }

  "Method findByParameters" should {
    "return 6 events for default brand" in {
      Event.findByParameters(Some(BrandHelper.defaultBrand.code)).length mustEqual 6
    }
    "return 4 public events" in {
      val events = Event.findByParameters(brandCode = None, public = Some(true))
      (events.length mustEqual 8) and
        (events.exists(_.title == "one") must beTrue) and
        (events.exists(_.title == "two") must beTrue) and
        (events.exists(_.title == "four") must beFalse) and
        (events.exists(_.title == "five") must beTrue) and
        (events.exists(_.title == "six") must beTrue)
    }
    "return 1 archived event" in {
      val events = Event.findByParameters(brandCode = None, archived = Some(true))
      (events.length mustEqual 2) and
        (events.exists(_.title == "four") must beTrue)
    }
    "return 3 confirmed events" in {
      val events = Event.findByParameters(brandCode = None, confirmed = Some(true))
      (events.length mustEqual 6) and
        (events.exists(_.title == "one") must beTrue) and
        (events.exists(_.title == "four") must beTrue) and
        (events.exists(_.title == "five") must beTrue) and
        (events.exists(_.title == "six") must beFalse)
    }
    "return 1 event in DE" in {
      val events = Event.findByParameters(brandCode = None, country = Some("DE"))
      (events.length mustEqual 2) and
        (events.exists(_.title == "five") must beTrue)
    }
    "return 3 events with type = 2" in {
      val events = Event.findByParameters(brandCode = None, eventType = Some(2))
      (events.length mustEqual 6) and
        (events.exists(_.title == "three") must beTrue) and
        (events.exists(_.title == "four") must beTrue) and
        (events.exists(_.title == "six") must beTrue) and
        (events.exists(_.title == "five") must beFalse)
    }
    "return 3 future events" in {
      val events = Event.findByParameters(
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
      val events = Event.findByFacilitator(
        1,
        Some(BrandHelper.defaultBrand.code))
      events.length mustEqual 4
    }
    "return 4 events facilitated by facilitator = 2" in {
      val events = Event.findByFacilitator(2, None)
      events.length mustEqual 4
    }
    "return 0 events facilitated by facilitator = 3" in {
      Event.findByFacilitator(3, None).length mustEqual 0
    }
  }

  def addEvents(brand: String) = {
    Seq(
      ("one", "2013-01-01", "2013-01-03", true, false, true, "RU", 1, List(1L, 2L)),
      ("two", "2013-01-01", "2013-01-03", true, false, false, "RU", 1, List(1L, 4L)),
      ("three", "2013-01-01", "2013-01-03", false, false, false, "RU", 2, List(1L, 4L)),
      ("four", "2023-01-01", "2023-01-03", false, true, true, "RU", 2, List(2L, 4L)),
      ("five", "2023-01-01", "2023-01-03", true, false, true, "DE", 1, List(4L, 5L)),
      ("six", "2023-01-01", "2023-01-03", true, false, false, "ES", 2, List(1L, 4L))).foreach {
        case (title, start, end, public, archived, confirmed, code, eventType,
          facilitators) ⇒ {
          val event = EventHelper.makeEvent(
            title = Some(title),
            startDate = Some(LocalDate.parse(start)),
            endDate = Some(LocalDate.parse(end)),
            brandCode = Some(brand),
            notPublic = Some(!public),
            archived = Some(archived),
            confirmed = Some(confirmed),
            country = Some(code),
            eventTypeId = Some(eventType),
            facilitatorIds = Some(facilitators))
          event.insert
        }
      }
  }

}
