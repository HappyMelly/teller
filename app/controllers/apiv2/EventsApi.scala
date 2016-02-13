/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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
package controllers.apiv2

import javax.inject.Inject

import controllers.apiv2.json.PersonConverter
import models.{Person, Event}
import models.repository.Repositories
import play.api.i18n.MessagesApi
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Events API
 */
class EventsApi @Inject() (val services: Repositories,
                           override val messagesApi: MessagesApi) extends ApiAuthentication(services, messagesApi) {

  implicit val personWrites = (new PersonConverter).personWrites

  val eventDetailsWrites = new Writes[(Event, List[Person])] {
    def writes(view: (Event, List[Person])): JsValue = {
      Json.obj(
        "brand" -> view._1.brandId,
        "type" -> view._1.eventTypeId,
        "title" -> view._1.title,
        "description" -> view._1.details.description,
        "spokenLanguages" -> view._1.spokenLanguages,
        "materialsLanguage" -> view._1.materialsLanguage,
        "specialAttention" -> view._1.details.specialAttention,
        "start" -> view._1.schedule.start,
        "end" -> view._1.schedule.end,
        "hoursPerDay" -> view._1.schedule.hoursPerDay,
        "totalHours" -> view._1.schedule.totalHours,
        "facilitators" -> view._2,
        "city" -> view._1.location.city,
        "country" -> view._1.location.countryCode,
        "website" -> view._1.pageUrl(controllers.routes.Events.public(view._1.hashedId).url),
        "registrationPage" -> view._1.organizer.registrationPage,
        "rating" -> view._1.rating,
        "public" -> !view._1.notPublic,
        "archived" -> view._1.archived,
        "confirmed" -> view._1.confirmed,
        "free" -> view._1.free,
        "online" -> view._1.location.online)
    }
  }

  /**
   * Returns event in JSON format
   *
   * @param id Event identifier
   */
  def event(id: Long) = TokenSecuredAction(readWrite = false) { implicit request ⇒ implicit token ⇒
    services.event.find(id) flatMap {
      case None => jsonNotFound("Event not found")
      case Some(event) =>
        val futureFacilitators = for {
          f <- services.event.facilitators(id)
          _ <- services.person.collection.addresses(f)
        } yield f
        futureFacilitators flatMap { facilitators =>
          jsonOk(Json.toJson((event, facilitators))(eventDetailsWrites))
        }
    }
  }

  /**
   * Returns a list of events based on several parameters in JSON format
   *
   * @param code Only events of this brand
   * @param future Only future and current events
   * @param public Only public events
   * @param archived Only archived events
   * @param facilitatorId Only events by this facilitator
   * @param countryCode Only events in this country
   * @param eventType Only events of this type
   */
  def events(code: String,
    future: Option[Boolean],
    public: Option[Boolean],
    archived: Option[Boolean],
    facilitatorId: Option[Long],
    countryCode: Option[String],
    eventType: Option[Long]) = TokenSecuredAction(readWrite = false) { implicit request ⇒ implicit token ⇒
      services.brand.find(code) flatMap {
        case None => jsonNotFound("Brand not found")
        case Some(brand) =>
          (for {
            types <- services.eventType.findByBrand(brand.identifier)
            events <- facilitatorId map { value ⇒
              eventsByFacilitator(value, brand.identifier, future, public)
            } getOrElse {
              eventsByBrand(brand.identifier, future, public, archived, countryCode, eventType)
            }
          } yield (types, events)) flatMap { case (types, events) =>
            val typeNames = types.map(eventType => eventType.identifier -> eventType.name).toMap
            services.event.applyFacilitators(events) flatMap { _ =>
              jsonOk(eventsToJson(events, typeNames))
            }
          }
      }
  }

  /**
   * Returns event list in JSON format
   *
   * @param events List of events
   * @param types List of event types belonged to the given brand
   */
  protected def eventsToJson(events: List[Event], types: Map[Long, String]): JsValue = {
    implicit val eventWrites = new Writes[Event] {
      def writes(event: Event): JsValue = {
        val typeName: String = types.getOrElse(event.eventTypeId, "")
        Json.obj(
          "id" -> event.id.get,
          "title" -> event.title,
          "type" -> event.eventTypeId,
          "typeName" -> typeName,
          "description" -> event.details.description,
          "spokenLanguages" -> event.spokenLanguages,
          "materialsLanguage" -> event.materialsLanguage,
          "specialAttention" -> event.details.specialAttention,
          "start" -> event.schedule.start,
          "end" -> event.schedule.end,
          "hoursPerDay" -> event.schedule.hoursPerDay,
          "totalHours" -> event.schedule.totalHours,
          "facilitators" -> event.facilitators(services),
          "city" -> event.location.city,
          "country" -> event.location.countryCode,
          "website" -> event.organizer.webSite,
          "registrationPage" -> event.organizer.registrationPage,
          "rating" -> event.rating,
          "confirmed" -> event.confirmed,
          "free" -> event.free,
          "online" -> event.location.online)
      }
    }
    Json.toJson(events)
  }

  /**
   * Returns list of events for the given facilitator*
   *
   * @param facilitatorId Only events by this facilitator
   * @param brandId Brand id
   * @param future Only future and current events
   * @param public Only public events
   */
  protected def eventsByFacilitator(facilitatorId: Long,
    brandId: Long,
    future: Option[Boolean],
    public: Option[Boolean]): Future[List[Event]] = {
    services.event.findByFacilitator(facilitatorId, Some(brandId), future, public, archived = Some(false))
  }

  /**
   * Returns a list of events for the given brand
   *
   * @param brandId Brand identifier
   * @param future Only future and current events
   * @param public Only public events
   * @param archived Only archived events
   * @param countryCode Only events in this country
   * @param eventType Only events of this type
   */
  protected def eventsByBrand(brandId: Long,
    future: Option[Boolean],
    public: Option[Boolean],
    archived: Option[Boolean],
    countryCode: Option[String],
    eventType: Option[Long]): Future[List[Event]] = {
    services.event.findByParameters(Some(brandId), future, public, archived, None, countryCode, eventType)
  }
}

