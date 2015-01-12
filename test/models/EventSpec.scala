package models

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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

import helpers.{ BrandHelper, EventHelper }
import integration.{ WithDb, WithTestApp }
import org.joda.time.LocalDate
import org.specs2.mutable._

class EventSpec extends Specification with WithTestApp {

  val event = EventHelper.makeEvent(title = Some("Daily Workshop"), city = Some("spb"), startDate = Some(LocalDate.parse("2014-05-12")))
  val tooLongTitle = "This title is just too long and should be truncated by the system to 70 characters"

  "Long title of an event" should {
    "contain a title, a city and a start date" in {
      event.longTitle mustEqual "Daily Workshop / spb / 2014-05-12"
    }
  }
  "If a title is > 70 characters long it" should {
    "be truncated and has a length = 70" in {
      event.copy(title = tooLongTitle).longTitle.length mustEqual (70 + " / spb / 2014-05-12".length)
    }
    "not be contained fully in a long title" in {
      event.copy(title = tooLongTitle).longTitle mustNotEqual tooLongTitle + " / spb / 2014-05-12"
    }
  }

  "A facilitator" should {
    val facilitatedEvent = event
    facilitatedEvent.facilitatorIds_=(List(2L, 3L, 4L, 6L))
    "be able to facilitate events" in new WithTestApp {
      List(2, 4, 6) foreach { i ⇒ facilitatedEvent.canFacilitate(i) must beTrue }
    }

    "not be able to facilitate an event if the facilitator is not in the list of facilitators" in new WithTestApp {
      List(1, 5) foreach { i ⇒ facilitatedEvent.canFacilitate(i) must beFalse }
    }
    "be able to facilitate an event if she is an Editor" in new WithDb {
      BrandHelper.defaultBrand.insert
      facilitatedEvent.canFacilitate(1) must beTrue
    }
  }

}

