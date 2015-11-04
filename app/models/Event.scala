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

package models

import akka.actor.{ Actor, Props }
import models.database.{ EventFacilitators, Events, Participants }
import models.event.EventCancellation
import models.service.{ EventService, Services }
import org.joda.money.Money
import org.joda.time.{ Days, LocalDate }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.libs.concurrent.Akka
import views.Languages

import scala.language.postfixOps

/**
 * Contains schedule-related data
 *
 *   - when an event starts/ends
 *   - how many hours per day it takes
 *   - how many total hours it takes
 */
case class Schedule(start: LocalDate,
    end: LocalDate,
    hoursPerDay: Int,
    totalHours: Int) {

  /**
   * Returns true if number of total hours is inside a threshold for an allowed
   * number of total hours
   */
  def validateTotalHours: Boolean = {
    val days = math.abs(Days.daysBetween(start, end).getDays) + 1
    val idealHours = days * hoursPerDay
    val difference = (idealHours - totalHours) / idealHours.toFloat
    if (difference > 0.2)
      false
    else
      true
  }
}

case class Organizer(
  id: Long,
  webSite: Option[String],
  registrationPage: Option[String])

/**
 * Contains optional descriptive data
 */
case class Details(
  description: Option[String],
  specialAttention: Option[String])

/**
 * Contains location-related data
 */
case class Location(city: String, countryCode: String) {

  lazy val online: Boolean = countryCode == "00"
}

/**
 * Contains language-related data
 */
case class Language(spoken: String, secondSpoken: Option[String], materials: Option[String])

case class EventView(event: Event, invoice: EventInvoice)

/** An event such as a Management 3.0 course or a DARE Festival */
case class Event(
    id: Option[Long],
    eventTypeId: Long,
    brandId: Long,
    title: String,
    language: Language,
    location: Location,
    details: Details,
    organizer: Organizer,
    schedule: Schedule,
    notPublic: Boolean = false,
    archived: Boolean = false,
    confirmed: Boolean = false,
    free: Boolean = false,
    followUp: Boolean = true,
    rating: Float = 0.0f,
    fee: Option[Money] = None) extends ActivityRecorder with Services {

  private var _facilitators: Option[List[Person]] = None
  private var _facilitatorIds: Option[List[Long]] = None

  /**
   * Returns identifier of the object
   */
  def identifier: Long = id.getOrElse(0)

  /**
   * Returns string identifier which can be understood by human
   *
   * For example, for object 'Person' human identifier is "[FirstName] [LastName]"
   */
  def humanIdentifier: String = title

  /**
   * Returns type of this object
   */
  def objectType: String = Activity.Type.Event

  /** Returns (and retrieves from db if needed) a list of facilitators */
  def facilitators: List[Person] = if (_facilitators.isEmpty) {
    val data = DB.withSession { implicit session ⇒
      val query = for {
        facilitation ← TableQuery[EventFacilitators] if facilitation.eventId === this.id
        person ← facilitation.facilitator
      } yield person
      query.sortBy(_.lastName.toLowerCase).list
    }
    facilitators_=(data)
    data
  } else {
    _facilitators.get
  }

  def facilitators_=(facilitators: List[Person]): Unit = {
    _facilitators = Some(facilitators)
  }

  def facilitatorIds: List[Long] = if (_facilitatorIds.isEmpty) {
    val ids = DB.withSession { implicit session ⇒
      (for {
        e ← TableQuery[EventFacilitators] if e.eventId === id.getOrElse(0L)
      } yield (e)).list.map(_._2)
    }
    facilitatorIds_=(ids)
    _facilitatorIds.get
  } else {
    _facilitatorIds.get
  }

  def facilitatorIds_=(facilitatorIds: List[Long]): Unit = {
    _facilitatorIds = Some(facilitatorIds)
  }

  val longTitle: String = {
    val printableTitle = if (title.length <= 70)
      title
    else
      title.substring(0, 70)
    printableTitle + " / " + location.city + " / " + schedule.start.toString
  }

  val materialsLanguage = Languages.all.get(language.materials.getOrElse("English"))

  lazy val spokenLanguage: String = if (language.secondSpoken.isEmpty)
    Languages.all.getOrElse(language.spoken, "")
  else
    Languages.all.getOrElse(language.spoken, "") + " / " +
      Languages.all.getOrElse(language.secondSpoken.get, "")

  lazy val spokenLanguages: List[String] = if (language.secondSpoken.isEmpty)
    List(Languages.all.getOrElse(language.spoken, ""))
  else
    List(Languages.all.getOrElse(language.spoken, ""),
      Languages.all.getOrElse(language.secondSpoken.get, ""))

  lazy val participants: List[Person] = DB.withSession { implicit session ⇒
    val query = for {
      participation ← TableQuery[Participants] if participation.eventId === this.id
      person ← participation.participant
    } yield person
    query.sortBy(_.lastName.toLowerCase).list
  }

  lazy val deletable: Boolean = participants.isEmpty

  def isFacilitator(personId: Long): Boolean = facilitatorIds.contains(personId)

  /**
   * Cancels the event
   *
   * @param facilitatorId Identifier of the facilitator who requested the action
   * @param reason Reason why the event is cancelled
   * @param participants Number of participants already registered to the event
   * @param details Details (emails, names) of registered participants
   */
  def cancel(facilitatorId: Long,
    reason: Option[String],
    participants: Option[Int],
    details: Option[String]): Unit = {

    val eventType = eventTypeService.find(this.eventTypeId).map(_.name).getOrElse("")
    val cancellation = EventCancellation(None, this.brandId, facilitatorId,
      this.title, eventType, this.location.city, this.location.countryCode,
      this.schedule.start, this.schedule.end, this.free, reason, participants, details)
    eventCancellationService.insert(cancellation)
    eventService.delete(this.id.get)
  }
}

object Event {
  val ratingActor = Akka.system.actorOf(Props[RatingCalculatorActor])

  /**
   * Returns new event with a fee calculated the given one and a number of hours
   * @param event Source event
   * @param fee Country Fee for 16-hours event
   * @param maxHours Maximum number of chargeable hours
   */
  def withFee(event: Event, fee: Money, maxHours: Int): Event = {
    val hours = scala.math.min(maxHours, event.schedule.totalHours)
    val slotNumber = hours / 4 + (hours % 4).min(1)
    val eventFee = fee.multipliedBy(slotNumber).dividedBy(4L, java.math.RoundingMode.UNNECESSARY)
    event.copy(fee = Some(eventFee))
  }

  /**
   * Returns true if and only if a user is allowed to manage this event.
   * @deprecated
   */
  def canManage(eventId: Long, user: UserAccount): Boolean = DB.withSession { implicit session: Session ⇒
    if (EventService.get.find(eventId).isEmpty)
      false
    else
      findByUser(user).exists(_.id.get == eventId)
  }

  /**
   * Returns a list of all events for a specified user which he could manage
   * @deprecated
   */
  def findByUser(user: UserAccount): List[Event] = DB.withSession { implicit session: Session ⇒
    val brands = Brand.findByUser(user)
    if (brands.nonEmpty) {
      val events = TableQuery[Events].filter(_.archived === false).sortBy(_.start).list
      events.filter(e ⇒ brands.exists(_.id == Some(e.brandId)))
    } else {
      List[Event]()
    }
  }

  /**
   * Updates event rating
   */
  class RatingCalculatorActor extends Actor with Services {
    def receive = {
      case eventId: Long ⇒
        val evaluations = evaluationService.findByEvent(eventId).filter(_.approved)
        val rating = evaluations.foldLeft(0.0f)(_ + _.facilitatorImpression.toFloat / evaluations.length)
        eventService.updateRating(eventId, rating)
    }
  }
}

