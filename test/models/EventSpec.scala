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

import org.joda.time.{ DateTime, LocalDate }
import org.specs2.mutable.Specification

class EventSpec extends Specification with WithTestApp {

  val event = makeEvent(title = Some("Daily Workshop"), city = Some("spb"), startDate = Some(LocalDate.parse("2014-05-12")))
  val tooLongTitle = "This title is just too long and should be truncated by the system to 70 characters"

  "Long title of an event" should {
    "contain a title, a city and a start date" in {
      event.longTitle mustEqual "Daily Workshop / spb / 2014-05-12"
      event.longTitle mustNotEqual "Daily Workshop / msk / 2014-05-12"
    }
    "truncate a title if the title > 70 characters" in {
      event.copy(title = tooLongTitle).longTitle.length mustEqual (70 + " / spb / 2014-05-12".length)
      event.copy(title = tooLongTitle).longTitle mustNotEqual tooLongTitle + " / spb / 2014-05-12"
    }
  }

  "A facilitator" should {
    val facilitatedEvent = event.copy(facilitatorIds = List(1L, 2L, 3L, 4L))
    "facilitate events" in new WithTestApp {
      facilitatedEvent.canFacilitate(2) must beTrue
      facilitatedEvent.canFacilitate(1) must beTrue
      facilitatedEvent.canFacilitate(4) must beTrue
    }
    "not facilitate an event" in new WithTestApp {
      facilitatedEvent.canFacilitate(5) must beFalse
    }
  }

  def makeEvent(id: Option[Long] = None, eventTypeId: Option[Long] = None, brandCode: Option[String] = None,
    title: Option[String] = None, spokenLanguage: Option[String] = None, materialsLanguage: Option[String] = None,
    city: Option[String] = None, country: Option[String] = None, startDate: Option[LocalDate] = None,
    endDate: Option[LocalDate] = None, notPublic: Option[Boolean] = None, archived: Option[Boolean] = None,
    confirmed: Option[Boolean] = None, invoice: Option[EventInvoice] = None,
    facilitatorIds: Option[List[Long]] = None): Event = {
    new Event(id, eventTypeId.getOrElse(1), brandCode.getOrElse("MGT30"), title.getOrElse("Test event"),
      spokenLanguage.getOrElse("EN"), materialsLanguage, new Location(city.getOrElse("spb"), country.getOrElse("RU")),
      new Details(None, None, None, None),
      new Schedule(startDate.getOrElse(new LocalDate(DateTime.now())), endDate.getOrElse(new LocalDate(DateTime.now())), 1, 1),
      notPublic.getOrElse(false), archived.getOrElse(false), confirmed.getOrElse(false),
      invoice.getOrElse(new EventInvoice(None, None, 1, None, None)), DateTime.now(), "Sergey Kotlov",
      DateTime.now(), "Sergey Kotlov", facilitatorIds.getOrElse(1 :: Nil))
  }

}

