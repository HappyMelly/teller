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

import com.github.tototoshi.slick.JodaSupport._
import models.database.{ EventInvoices, EventFacilitators, Participants, Events }
import models.event.{ EventService, Comparator }
import models.event.Comparator.FieldChange
import org.joda.time.{ LocalDate, DateTime }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.i18n.Messages
import play.api.Play.current
import scala.language.postfixOps
import scala.slick.lifted.Query
import services.EmailService
import views.Languages

/**
 * Contains schedule-related data
 *
 *   - when an event starts/ends
 *   - how many hours per day it takes
 *   - how many total hours it takes
 */
case class Schedule(start: LocalDate, end: LocalDate, hoursPerDay: Int, totalHours: Int)

/**
 * Contains optional descriptive data
 */
case class Details(
  description: Option[String],
  specialAttention: Option[String],
  webSite: Option[String],
  registrationPage: Option[String])

/**
 * Contains location-related data
 */
case class Location(city: String, countryCode: String)

/**
 * Contains language-related data
 */
case class Language(spoken: String, secondSpoken: Option[String], materials: Option[String])

/** An event such as a Management 3.0 course or a DARE Festival */
case class Event(
  id: Option[Long],
  eventTypeId: Long,
  brandCode: String,
  title: String,
  language: Language,
  location: Location,
  details: Details,
  schedule: Schedule,
  notPublic: Boolean = false,
  archived: Boolean = false,
  confirmed: Boolean = false,
  created: DateTime = DateTime.now(),
  createdBy: String,
  updated: DateTime,
  updatedBy: String) {

  private var _facilitators: Option[List[Person]] = None
  private var _invoice: Option[EventInvoice] = None
  private var _facilitatorIds: Option[List[Long]] = None

  /** Returns (and retrieves from db if needed) a list of facilitators */
  def facilitators: List[Person] = if (_facilitators.isEmpty) {
    val data = DB.withSession { implicit session: Session ⇒
      val query = for {
        facilitation ← EventFacilitators if facilitation.eventId === this.id
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

  /** Returns (and retrieves from db if needed) an invoice data */
  def invoice: EventInvoice = if (_invoice.isEmpty) {
    invoice_=(EventInvoice findByEvent id.get)
    _invoice.get
  } else {
    _invoice.get
  }

  def invoice_=(invoice: EventInvoice): Unit = {
    _invoice = Some(invoice)
  }

  def facilitatorIds: List[Long] = if (_facilitatorIds.isEmpty) {
    val ids = DB.withSession { implicit session: Session ⇒
      (for {
        e ← EventFacilitators if e.eventId === id.getOrElse(0L)
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

  val materialsLanguage = Languages.all.get(language.materials.getOrElse(""))

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

  lazy val participants: List[Person] = DB.withSession { implicit session: Session ⇒
    val query = for {
      participation ← Participants if participation.eventId === this.id
      person ← participation.participant
    } yield person
    query.sortBy(_.lastName.toLowerCase).list
  }

  lazy val deletable: Boolean = participants.isEmpty

  /**
   * Returns true if a person is a facilitator of this event
   *
   * @param personId A person's unique identifier
   */
  def canFacilitate(personId: Long): Boolean =
    facilitatorIds.contains(personId) || isBrandManager(personId)

  /**
   * Returns true if a person is a brand manager of this event
   *
   * @param personId A person unique identifier
   */
  def isBrandManager(personId: Long): Boolean = DB.withSession {
    implicit session: Session ⇒
      Brand.find(brandCode).exists(_.coordinator.id.get == personId)
  }

  def insert: Event = DB.withSession { implicit session: Session ⇒
    val insertTuple = (eventTypeId, brandCode, title, language.spoken,
      language.secondSpoken, language.materials, location.city,
      location.countryCode, details.description, details.specialAttention,
      details.webSite, details.registrationPage, schedule.start, schedule.end,
      schedule.hoursPerDay, schedule.totalHours, notPublic, archived, confirmed,
      created, createdBy)
    val id = Events.forInsert.insert(insertTuple)
    this.facilitatorIds.distinct.foreach(facilitatorId ⇒
      EventFacilitators.insert((id, facilitatorId)))
    EventInvoice.insert(this.invoice.copy(eventId = Some(id)))
    this.copy(id = Some(id))
  }

  /** Deletes this event and its related data from database */
  def delete(): Unit = DB.withSession { implicit session: Session ⇒
    EventFacilitators.where(_.eventId === this.id.get).mutate(_.delete())
    EventInvoice.delete(this.id.get)
    Query(Events).filter(_.id === this.id.get).delete
  }

  def update: Event = DB.withSession { implicit session: Session ⇒
    val updateTuple = (eventTypeId, brandCode, title, language.spoken,
      language.secondSpoken, language.materials, location.city,
      location.countryCode, details.description, details.specialAttention,
      details.webSite, details.registrationPage, schedule.start, schedule.end,
      schedule.hoursPerDay, schedule.totalHours, notPublic, archived, confirmed,
      updated, updatedBy)
    val updateQuery = Events.filter(_.id === this.id).map(_.forUpdate)
    updateQuery.update(updateTuple)

    EventFacilitators.where(_.eventId === this.id).mutate(_.delete())
    this.facilitatorIds.distinct.foreach(facilitatorId ⇒
      EventFacilitators.insert((this.id.get, facilitatorId)))
    EventInvoice.update(this.invoice)

    this
  }
}

object Event {

  /**
   * Returns true if and only if a user is allowed to manage this event.
   * @deprecated
   */
  def canManage(eventId: Long, user: UserAccount): Boolean = DB.withSession { implicit session: Session ⇒
    if (EventService.find(eventId).isEmpty)
      false
    else
      findByUser(user).exists(_.id.get == eventId)
  }

  /**
   * Returns a list of all events for a specified user which he could manage
   * @deprecated
   */
  def findByUser(user: UserAccount): List[Event] = DB.withSession { implicit session: Session ⇒
    if (user.editor)
      Query(Events).filter(_.archived === false).sortBy(_.start).list
    else {
      val brands = Brand.findByUser(user)
      if (brands.length > 0) {
        val brandCodes = brands.map(_.code)
        val events = Query(Events).filter(_.archived === false).sortBy(_.start).list
        events.filter(e ⇒ brandCodes.contains(e.brandCode))
      } else {
        List[Event]()
      }
    }
  }

  /**
   * Return a list of events for a given facilitator
   *
   * @param facilitatorId Only events facilitated by this facilitator
   * @param brand Only events of this brand
   * @param future Only future and current events
   * @param public Only public events
   * @param archived Only archived events
   */
  def findByFacilitator(
    facilitatorId: Long,
    brand: Option[String],
    future: Option[Boolean] = None,
    public: Option[Boolean] = None,
    archived: Option[Boolean] = None): List[Event] = DB.withSession {
    implicit session: Session ⇒

      val baseQuery = brand map { value ⇒
        for {
          entry ← EventFacilitators if entry.facilitatorId === facilitatorId
          event ← Events if event.id === entry.eventId && event.brandCode === value
        } yield event
      } getOrElse {
        for {
          entry ← EventFacilitators if entry.facilitatorId === facilitatorId
          event ← Events if event.id === entry.eventId
        } yield event
      }

      val timeQuery = applyTimeFilter(future, baseQuery)
      val publicityQuery = applyPublicityFilter(public, timeQuery)
      val archivedQuery = applyArchivedFilter(archived, publicityQuery)

      archivedQuery sortBy (_.start) list
  }

  /** Returns list with active events */
  def findActive: List[Event] = DB.withSession { implicit session: Session ⇒
    EventService.findByParameters(brandCode = None, archived = Some(false)).
      sortBy(_.title.toLowerCase)
  }

  /** Returns list with all events */
  def findAll: List[Event] = DB.withSession { implicit session: Session ⇒
    EventService.findByParameters(brandCode = None)
  }

  def sendConfirmationAlert() = Brand.findAll.foreach { brand ⇒
    EventService.findByParameters(
      brandCode = Some(brand.code),
      future = Some(false),
      confirmed = Some(false)).foreach { event ⇒
        val subject = "Сonfirm your event " + event.title
        EmailService.send(
          event.facilitators.toSet,
          None,
          None,
          subject,
          mail.txt.confirm(event).toString())
      }
  }

  /**
   * Applies time filter on query
   *
   * @param future Defines if time filter should be applied
   * @param parentQuery Query to apply this filter to
   * @return returns updated query if 'future' flag is defined
   */
  private def applyTimeFilter(
    future: Option[Boolean],
    parentQuery: Query[Events.type, Event]) = {
    future map { value ⇒
      val now = LocalDate.now
      val today = new LocalDate(
        now.getValue(0),
        now.getValue(1),
        now.getValue(2))
      if (value)
        parentQuery.filter(_.end >= today)
      else
        parentQuery.filter(_.end <= today)
    } getOrElse parentQuery
  }

  /**
   * Applies publicity filter on query
   *
   * @param public Defines if publicity filter should be applied
   * @param parentQuery Query to apply this filter to
   * @return returns updated query if 'public' flag is defined
   */
  private def applyPublicityFilter(
    public: Option[Boolean],
    parentQuery: Query[Events.type, Event]) = {
    public map { value ⇒
      parentQuery.filter(_.notPublic === !value)
    } getOrElse parentQuery
  }

  /**
   * Applies archived filter on query
   *
   * @param archived Defines if archived filter should be applied
   * @param parentQuery Query to apply this filter to
   * @return returns updated query if 'archived' flag is defined
   */
  private def applyArchivedFilter(
    archived: Option[Boolean],
    parentQuery: Query[Events.type, Event]) = {
    archived map { value ⇒
      parentQuery.filter(_.archived === value)
    } getOrElse parentQuery
  }
}

