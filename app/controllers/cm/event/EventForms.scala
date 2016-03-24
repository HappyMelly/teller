/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers.cm.event

import controllers.Forms._
import models.cm._
import models.repository.Repositories
import org.joda.time.LocalDate
import play.api.data._
import play.api.data.FormError
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.i18n.Messages

/**
  * Event form related objects
  */
object EventForms {

  val dateRangeFormatter = new Formatter[LocalDate] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {
      // "data" lets you access all form data values
      try {
        val start = LocalDate.parse(data.get("schedule.start").get)
        try {
          val end = LocalDate.parse(data.get("schedule.end").get)
          if (start.isAfter(end)) {
            Left(List(FormError("schedule.start", "error.date.range"), FormError("schedule.end", "error.date.range")))
          } else {
            Right(end)
          }
        } catch {
          case e: IllegalArgumentException ⇒ Left(List(FormError("schedule.end", "Invalid date")))
        }
      } catch {
        // The list is empty because we've already handled a date parse error inside the form (jodaLocalDate formatter)
        case e: IllegalArgumentException ⇒ Left(List())
      }
    }

    override def unbind(key: String, value: LocalDate): Map[String, String] = {
      Map(key -> value.toString)
    }
  }

  /**
    * HTML form mapping for an event’s invoice.
    */
  val invoice = Form(tuple(
    "invoiceBy" -> longNumber,
    "number" -> optional(nonEmptyText)))

  /**
    * HTML form mapping for creating and editing.
    */
  def event(repos: Repositories)(implicit messages: Messages) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "eventTypeId" -> longNumber.verifying("Wrong event type", _ > 0),
    "brandId" -> longNumber.verifying("Wrong brand", _ > 0),
    "title" -> text.verifying("Empty title", _.nonEmpty),
    "language" -> mapping(
      "spoken" -> language,
      "secondSpoken" -> optional(language),
      "materials" -> optional(language))(Language.apply)(Language.unapply),
    "location" -> mapping(
      "city" -> text.verifying("Empty city name", _.nonEmpty),
      "country" -> text.verifying("Unknown country", _.nonEmpty))(Location.apply)(Location.unapply),
    "details" -> mapping(
      "description" -> optional(text),
      "specialAttention" -> optional(text))(Details.apply)(Details.unapply),
    "organizer" -> mapping(
      "id" -> longNumber.verifying("Unknown organizer", _ > 0),
      "webSite" -> optional(webUrl),
      "registrationPage" -> optional(text))(Organizer.apply)(Organizer.unapply),
    "schedule" -> mapping(
      "start" -> jodaLocalDate,
      "end" -> of(dateRangeFormatter),
      "hoursPerDay" -> number(1, 24),
      "totalHours" -> number(1))(Schedule.apply)(Schedule.unapply),
    "notPublic" -> default(boolean, false),
    "archived" -> default(boolean, false),
    "confirmed" -> default(boolean, false),
    "free" -> default(boolean, false),
    "followUp" -> boolean,
    "pageType" -> boolean,
    "invoice" -> longNumber.verifying("No organization to invoice", _ > 0),
    "facilitatorIds" -> list(longNumber).verifying(
      Messages("error.event.nofacilitators"), (ids: List[Long]) ⇒ ids.nonEmpty))(
    { (id, eventTypeId, brandId, title, language, location, details, organizer,
       schedule, notPublic, archived, confirmed, free, followUp, pageType, invoiceTo,
       facilitatorIds) ⇒
    {
      val event = Event(id, eventTypeId, brandId, title, language, location,
        details, organizer, schedule, notPublic, archived, confirmed, free,
        followUp, 0.0f, publicPage = pageType)
      val invoice = EventInvoice.empty.copy(eventId = id, invoiceTo = invoiceTo)
      event.facilitatorIds_=(facilitatorIds)
      EventView(event, invoice)
    }
    })({ (view: EventView) ⇒
    Some((view.event.id, view.event.eventTypeId, view.event.brandId,
      view.event.title, view.event.language, view.event.location,
      view.event.details, view.event.organizer, view.event.schedule,
      view.event.notPublic, view.event.archived, view.event.confirmed,
      view.event.free, view.event.followUp, view.event.publicPage,
      view.invoice.invoiceTo, view.event.facilitatorIds(repos)))

  }))
}
