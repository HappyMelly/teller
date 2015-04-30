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
package helpers

import models._
import org.joda.time.{ DateTime, LocalDate }
import models.service.EventService
import play.api.libs.json.{ Json, JsValue }

object EventHelper {

  def make(id: Option[Long] = None,
    eventTypeId: Option[Long] = None,
    brandId: Option[Long] = None,
    title: Option[String] = None,
    spokenLanguage: Option[String] = None,
    secondSpokenLanguage: Option[String] = None,
    materialsLanguage: Option[String] = None,
    city: Option[String] = None,
    country: Option[String] = None,
    startDate: Option[LocalDate] = None,
    endDate: Option[LocalDate] = None,
    notPublic: Option[Boolean] = None,
    archived: Option[Boolean] = None,
    confirmed: Option[Boolean] = None,
    invoice: Option[EventInvoice] = None,
    facilitatorIds: Option[List[Long]] = None): Event = {

    val invoice = new EventInvoice(None, None, 1, None, None)
    val language = new Language(
      spokenLanguage.getOrElse("DE"),
      secondSpokenLanguage,
      materialsLanguage)
    var event = new Event(id, eventTypeId.getOrElse(1), brandId.getOrElse(1L), title.getOrElse("Test event"),
      language, new Location(city.getOrElse("spb"), country.getOrElse("RU")),
      new Details(None, None, None, None),
      new Schedule(startDate.getOrElse(new LocalDate(DateTime.now())),
        endDate.getOrElse(new LocalDate(DateTime.now())), 1, 1),
      notPublic.getOrElse(false), archived.getOrElse(false), confirmed.getOrElse(false),
      false, 0.0f, None)
    event.facilitatorIds_=(facilitatorIds.getOrElse(1 :: Nil))
    event.invoice_=(invoice)

    event
  }

  def addEvents(brandId: Long) = {
    Seq(
      ("one", "2013-01-01", "2013-01-03", true, false, true, "RU", 1, List(1L, 2L)),
      ("two", "2013-01-01", "2013-01-03", true, false, false, "RU", 1, List(1L, 4L)),
      ("three", "2013-01-01", "2023-01-03", false, false, false, "RU", 2, List(1L, 4L)), //current
      ("four", "2023-01-01", "2023-01-03", false, true, true, "RU", 2, List(2L, 4L)),
      ("five", "2023-01-01", "2023-01-03", true, false, true, "DE", 1, List(4L, 5L)),
      ("six", "2023-01-01", "2023-01-03", true, false, false, "ES", 2, List(1L, 4L))).foreach {
        case (title, start, end, public, archived, confirmed, code, eventType,
          facilitators) ⇒ {
          val event = EventHelper.make(
            title = Some(title),
            startDate = Some(LocalDate.parse(start)),
            endDate = Some(LocalDate.parse(end)),
            brandId = Some(brandId),
            notPublic = Some(!public),
            archived = Some(archived),
            confirmed = Some(confirmed),
            country = Some(code),
            eventTypeId = Some(eventType),
            facilitatorIds = Some(facilitators))
          EventService.get.insert(event)
        }
      }
  }

  def one: Event = {
    val event = make(id = Some(1),
      title = Some("One"),
      startDate = Some(LocalDate.parse("2015-01-01")),
      endDate = Some(LocalDate.parse("2015-01-02")))
    event.facilitators_=(List[Person](PersonHelper.one(), PersonHelper.two()))
    event
  }

  def two: Event = {
    val event = make(id = Some(2),
      title = Some("Two"),
      eventTypeId = Some(2),
      startDate = Some(LocalDate.parse("2015-01-01")),
      endDate = Some(LocalDate.parse("2015-01-02")))
    event.facilitators_=(List[Person](PersonHelper.one(), PersonHelper.two()))
    event
  }

  def future(id: Long, plusDays: Int): Event = {
    val event = make(id = Some(id),
      title = Some(id.toString),
      startDate = Some(LocalDate.now().plusDays(plusDays)),
      endDate = Some(LocalDate.now().plusDays(plusDays + 2)))
    event.facilitators_=(List[Person](PersonHelper.one()))
    event
  }

  def past(id: Long, minusDays: Int): Event = {
    val event = make(id = Some(id),
      title = Some(id.toString),
      startDate = Some(LocalDate.now().minusDays(minusDays - 2)),
      endDate = Some(LocalDate.now().minusDays(minusDays)))
    event.facilitators_=(List[Person](PersonHelper.one()))
    event
  }

  def oneAsJson: JsValue = Json.obj(
    "id" -> 1,
    "title" -> "One",
    "type" -> 1,
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
    "rating" -> 0.0,
    "confirmed" -> false,
    "free" -> false)

  def twoAsJson: JsValue = Json.obj(
    "id" -> 2,
    "title" -> "Two",
    "type" -> 2,
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
    "rating" -> 0.0,
    "confirmed" -> false,
    "free" -> false)

}
