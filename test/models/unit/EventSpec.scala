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

import _root_.integration.PlayAppSpec
import helpers.{ BrandHelper, EventHelper, PersonHelper }
import models._
import models.cm.brand.EventType
import models.cm.event.EventCancellation
import models.repository._
import models.repository.cm.EventRepository
import models.repository.cm.brand.EventTypeRepository
import models.repository.cm.event.EventCancellationRepository
import org.joda.money.Money
import org.joda.time.LocalDate
import org.scalamock.specs2.MockContext
import org.specs2.execute._
import _root_.stubs.FakeRepositories

class EventSpec extends PlayAppSpec {

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
      val maxHours = 16
      // total hours = 1
      Event.withFee(e1, fee, maxHours).fee map { f: Money ⇒
        f.getAmount.longValue must_== 40L
      } getOrElse ko
      val e2 = e1.copy(schedule = e1.schedule.copy(totalHours = 5))
      Event.withFee(e2, fee, maxHours).fee map { f: Money ⇒
        f.getAmount.longValue must_== 80L
      } getOrElse ko
      val e3 = e1.copy(schedule = e1.schedule.copy(totalHours = 11))
      Event.withFee(e3, fee, maxHours).fee map { f: Money ⇒
        f.getAmount.longValue must_== 120L
      } getOrElse ko
      val e4 = e1.copy(schedule = e1.schedule.copy(totalHours = 16))
      Event.withFee(e4, fee, maxHours).fee map { f: Money ⇒
        f.getAmount.longValue must_== 160L
      } getOrElse ko
      val e5 = e1.copy(schedule = e1.schedule.copy(totalHours = 21))
      Event.withFee(e5, fee, maxHours).fee map { f: Money ⇒
        f.getAmount.longValue must_== 160L
      } getOrElse ko
    }
  }

  "Total hours should be invalid if they are less than hoursPerDay * numOfDays * 0.8" >> {
    "when an event is two-days long" in {
      val schedule = Schedule(LocalDate.now(), LocalDate.now().plusDays(1), 8, 12)
      schedule.validateTotalHours must_== false
      schedule.copy(totalHours = 1).validateTotalHours must_== false
      schedule.copy(totalHours = 13).validateTotalHours must_== true
    }
    "when an event is one-day long" in {
      val schedule = Schedule(LocalDate.now(), LocalDate.now(), 8, 6)
      schedule.validateTotalHours must_== false
      schedule.copy(totalHours = 3).validateTotalHours must_== false
      schedule.copy(totalHours = 7).validateTotalHours must_== true
    }
  }

  "When an event is cancelled the system" should {
    "deletes event data and adds event cancellation record" in new MockContext {
      class EventTest(id: Option[Long],
        eventTypeId: Long,
        brandId: Long,
        title: String,
        language: Language,
        location: Location,
        details: models.Details,
        organizer: Organizer,
        schedule: Schedule) extends Event(id, eventTypeId, brandId, title,
        language, location, details, organizer, schedule) with FakeRepositories {}

      val now = LocalDate.now()
      val schedule = Schedule(now, now.plusDays(1), 8, 8)
      val lang = Language("EN", None, None)
      val details = Details(None, None)
      val organizer = Organizer(0, None, None)
      val location = Location("Berlin", "DE")
      val one = new EventTest(Some(1L), 1L, 1L, "Cancel", lang, location,
        details, organizer, schedule)
      val eventService = mock[EventRepository]
      (services.eventService.delete _) expects 1L
      val eventCancellationService = mock[EventCancellationRepository]
      val cancellation = EventCancellation(None, 1L, 3L, "Cancel", "Regular event",
        "Berlin", "DE", now, now.plusDays(1), false,
        Some("Small number of participants"),
        Some(2), Some("some data"))
      (services.cm.rep.event.cancellationService.insert _) expects cancellation returning cancellation
      val eventTypeService = mock[EventTypeRepository]
      val eventType = EventType(Some(1L), 1L, "Regular event", None, 8, false)
      (services.cm.rep.brand.eventTypeService.find _) expects 1L returning Some(eventType)
      one.eventService_=(eventService)
      one.eventCancellationService_=(eventCancellationService)
      one.eventTypeService_=(eventTypeService)

      one.cancel(3L, Some("Small number of participants"), Some(2), Some("some data"))
    }
  }
}
