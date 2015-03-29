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
package models.unit

import models.Activity
import helpers.{ BrandHelper, EventHelper, PersonHelper }
import integration.PlayAppSpec
import models.Event
import models.brand.EventType
import org.joda.money.Money
import org.joda.time.LocalDate
import org.scalamock.specs2.MockContext
import org.specs2.execute._

class EventSpec extends PlayAppSpec {

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

  val langEvent = EventHelper.make(
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
  val noMaterialsEvent = EventHelper.make(spokenLanguage = Option("EN"))
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
    "be able to facilitate an event" in new MockContext {
      event.canFacilitate(1) must beTrue
    }
  }
  "A random person (id = 5)" should {
    val id = 5
    "not be able to facilitate the event" in {
      (event canFacilitate id) must beFalse
    }
  }

  "A facilitator" should {
    "be able to facilitate events" in {
      Result.unit {
        List(2, 4, 6) foreach { i ⇒ event.canFacilitate(i) must beTrue }
      }
    }
  }

  "Event" should {
    "have well-formed activity attributes" in {
      val eventType = EventHelper.one
      eventType.objectType must_== Activity.Type.Event
      eventType.identifier must_== 1
      eventType.humanIdentifier must_== "One"
      val eventType2 = EventHelper.two
      eventType2.objectType must_== Activity.Type.Event
      eventType2.identifier must_== 2
      eventType2.humanIdentifier must_== "Two"
    }
  }

  "Event fee " should {
    "be rounded to a multiple of half a day" in {
      val e1 = EventHelper.one
      val fee = Money.parse("EUR 160")
      // total hours = 1
      Event.withFee(e1, fee).fee map { f: Money ⇒
        f.getAmount.longValue must_== 40L
      } getOrElse ko
      val e2 = e1.copy(schedule = e1.schedule.copy(totalHours = 5))
      Event.withFee(e2, fee).fee map { f: Money ⇒
        f.getAmount.longValue must_== 80L
      } getOrElse ko
      val e3 = e1.copy(schedule = e1.schedule.copy(totalHours = 11))
      Event.withFee(e3, fee).fee map { f: Money ⇒
        f.getAmount.longValue must_== 120L
      } getOrElse ko
    }

  }
}
