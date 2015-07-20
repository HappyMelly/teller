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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing
 * Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models.service

import models.database.PortableJodaSupport._
import models._
import models.database.{ Evaluations, EventFacilitators, EventInvoices, Events, Participants }
import org.joda.time.LocalDate
import play.api.Play
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import services.integrations.Integrations

import scala.language.postfixOps
import scala.slick.lifted.Query

class EventService extends Integrations with Services {

  private val events = TableQuery[Events]

  /**
   * Confirms the given event
   *
   * @param id Event identifier
   */
  def confirm(id: Long): Unit = DB.withSession { implicit session ⇒
    events.filter(_.id === id).map(_.confirmed).update(true)
  }

  /**
   * Deletes the given event and all related data from database
   *
   * @param id Event identifier
   */
  def delete(id: Long): Unit = DB.withTransaction {
    implicit session ⇒
      TableQuery[EventFacilitators].filter(_.eventId === id).delete
      TableQuery[EventInvoices].filter(_.eventId === id).delete
      events.filter(_.id === id).delete

  }

  /**
   * Returns event if it exists, otherwise - None
   *
   * @param id Event identifier
   */
  def find(id: Long): Option[Event] = DB.withSession { implicit session ⇒
    events.filter(_.id === id).firstOption
  }

  /**
   * Returns event with related invoice if it exists
   *
   * @param id Event identifier
   */
  def findWithInvoice(id: Long): Option[EventView] = DB.withSession {
    implicit session ⇒
      val query = for {
        event ← events if event.id === id
        invoice ← TableQuery[EventInvoices] if invoice.eventId === id
      } yield (event, invoice)
      query.firstOption.map(x ⇒ EventView(x._1, x._2))
  }

  /**
   * Return event if it exists related to the given evaluation
   * @param evaluationId Evaluation id
   */
  def findByEvaluation(evaluationId: Long): Option[Event] = DB.withSession {
    implicit session ⇒
      val query = for {
        x ← TableQuery[Evaluations] if x.id === evaluationId
        y ← events if y.id === x.eventId
      } yield y
      query.firstOption
  }

  /**
   * Returns a list of events based on several parameters
   *
   * @param brandId Only events of this brand
   * @param future Only future and current events
   * @param public Only public events
   * @param archived Only archived events
   * @param confirmed Only confirmed events
   * @param country Only events in this country
   * @param eventType Only events of this type
   */
  def findByParameters(
    brandId: Option[Long],
    future: Option[Boolean] = None,
    public: Option[Boolean] = None,
    archived: Option[Boolean] = None,
    confirmed: Option[Boolean] = None,
    country: Option[String] = None,
    eventType: Option[Long] = None): List[Event] = DB.withSession {
    implicit session ⇒
      val brandQuery = brandId map {
        v ⇒ events filter (_.brandId === v)
      } getOrElse events

      val timeQuery = applyTimeFilter(future, brandQuery)
      val publicityQuery = applyPublicityFilter(public, timeQuery)
      val archivedQuery = applyArchivedFilter(archived, publicityQuery)

      val confirmedQuery = confirmed map { value ⇒
        archivedQuery filter (_.confirmed === value)
      } getOrElse archivedQuery

      val countryQuery = country map { value ⇒
        confirmedQuery filter (_.countryCode === value)
      } getOrElse confirmedQuery

      val typeQuery = eventType map { value ⇒
        countryQuery filter (_.eventTypeId === value)
      } getOrElse countryQuery

      typeQuery sortBy (_.start) list
  }

  /**
   * Return a list of events for a given facilitator
   *
   * @param facilitatorId Only events facilitated by this facilitator
   * @param brandId Only events of this brand
   * @param future Only future and current events
   * @param public Only public events
   * @param archived Only archived events
   */
  def findByFacilitator(
    facilitatorId: Long,
    brandId: Option[Long] = None,
    future: Option[Boolean] = None,
    public: Option[Boolean] = None,
    archived: Option[Boolean] = None): List[Event] = DB.withSession {
    implicit session ⇒
      val facilitators = TableQuery[EventFacilitators]
      val baseQuery = brandId map { value ⇒
        for {
          entry ← facilitators if entry.facilitatorId === facilitatorId
          event ← events if event.id === entry.eventId && event.brandId === value
        } yield event
      } getOrElse {
        for {
          entry ← facilitators if entry.facilitatorId === facilitatorId
          event ← events if event.id === entry.eventId
        } yield event
      }

      val timeQuery = applyTimeFilter(future, baseQuery)
      val publicityQuery = applyPublicityFilter(public, timeQuery)
      val archivedQuery = applyArchivedFilter(archived, publicityQuery)

      archivedQuery sortBy (_.start) list
  }

  /** Returns list with active events */
  def findActive: List[Event] = DB.withSession { implicit session ⇒
    findByParameters(brandId = None, archived = Some(false)).
      sortBy(_.title.toLowerCase)
  }

  /** Returns list with all events */
  def findAll: List[Event] = DB.withSession { implicit session ⇒
    findByParameters(brandId = None)
  }

  /**
   * Returns list of events ran by the given facilitator where the given person
   *  participated
   * @TEST
   * @param personId Person identifier
   * @param facilitatorId Facilitator identifier
   */
  def findByParticipation(personId: Long, facilitatorId: Long): List[Event] =
    DB.withSession { implicit session ⇒
      val query = for {
        facilitation ← TableQuery[EventFacilitators] if facilitation.facilitatorId === facilitatorId
        event ← TableQuery[Events] if facilitation.eventId === event.id
        participation ← TableQuery[Participants] if participation.eventId === event.id
      } yield (event, facilitation, participation)

      query
        .filter(_._3.personId === personId)
        .mapResult(_._1)
        .list
    }

  /**
   * Adds event and related objects to database
   *
   * @param view Event object
   * @return Updated event object with id
   */
  def insert(view: EventView): EventView = DB.withTransaction { implicit session ⇒
    val insertTuple = (view.event.eventTypeId, view.event.brandId,
      view.event.title, view.event.language.spoken, view.event.language.secondSpoken,
      view.event.language.materials, view.event.location.city,
      view.event.location.countryCode, view.event.details.description,
      view.event.details.specialAttention, view.event.organizer.id,
      view.event.organizer.webSite,
      view.event.organizer.registrationPage, view.event.schedule.start,
      view.event.schedule.end, view.event.schedule.hoursPerDay,
      view.event.schedule.totalHours, view.event.notPublic,
      view.event.archived, view.event.confirmed, view.event.free)
    val id = (events.map(_.forInsert) returning events.map(_.id)) += insertTuple
    view.event.facilitatorIds.distinct.foreach(facilitatorId ⇒
      TableQuery[EventFacilitators] += (id, facilitatorId))
    TableQuery[EventInvoices] += view.invoice.copy(eventId = Some(id))
    view.copy(event = view.event.copy(id = Some(id)))
  }

  /**
   * Fill events with facilitators (using only one query to database)
   * TODO: Cover with tests
   * @param events List of events
   * @return
   */
  def applyFacilitators(events: List[Event]): Unit = DB.withSession { implicit session ⇒
    val ids = events.map(_.id.get).distinct
    val query = for {
      facilitation ← TableQuery[EventFacilitators] if facilitation.eventId inSet ids
      person ← facilitation.facilitator
    } yield (facilitation.eventId, person)
    val facilitationData = query.list
    val facilitators = facilitationData.map(_._2).distinct
    PeopleCollection.addresses(facilitators)
    facilitationData.foreach(f ⇒ f._2.address_=(facilitators.find(_.id == f._2.id).get.address))
    val groupedFacilitators = facilitationData.groupBy(_._1).map(f ⇒ (f._1, f._2.map(_._2)))
    events.foreach(e ⇒ e.facilitators_=(groupedFacilitators.getOrElse(e.id.get, List())))
  }

  /**
   * Sends email notifications to facilitators asking to confirm or delete
   *  past events which are unconfirmed
   */
  def sendConfirmationAlert() = brandService.findAll.foreach { brand ⇒
    findByParameters(
      brandId = brand.id,
      future = Some(false),
      confirmed = Some(false)).foreach { event ⇒
        val subject = "Confirm your event " + event.title
        val url = Play.configuration.getString("application.baseUrl").getOrElse("")
        val body = mail.html.confirm(event, brand, url).toString()
        email.send(
          event.facilitators.toSet,
          None,
          None,
          subject,
          body,
          richMessage = true)
        val msg = "confirmation email for event %s (id = %s)".format(
          event.title,
          event.id.get.toString)
        Activity.insert("Teller", Activity.Predicate.Sent, msg)
      }
  }

  /**
   * Fill events with invoices (using only one query to database)
   * @todo test
   * @todo comment
   * @param events List of events
   */
  def withInvoices(events: List[Event]): List[EventView] = DB.withSession {
    implicit session ⇒
      val ids = events.map(_.id.get).distinct
      val query = for {
        invoice ← TableQuery[EventInvoices] if invoice.eventId inSet ids
      } yield invoice
      val invoices = query.list
      events.map { e ⇒
        EventView(e, invoices.find(_.eventId == e.id).getOrElse(EventInvoice.empty))
      }
  }

  /**
   * Updates event and related objects in database
   *
   * @param view Event
   * @return Updated event object with id
   */
  def update(view: EventView): EventView = DB.withSession { implicit session ⇒
    val updateTuple = (view.event.eventTypeId, view.event.brandId,
      view.event.title, view.event.language.spoken,
      view.event.language.secondSpoken, view.event.language.materials,
      view.event.location.city, view.event.location.countryCode,
      view.event.details.description, view.event.details.specialAttention,
      view.event.organizer.id,
      view.event.organizer.webSite, view.event.organizer.registrationPage,
      view.event.schedule.start, view.event.schedule.end,
      view.event.schedule.hoursPerDay, view.event.schedule.totalHours,
      view.event.notPublic, view.event.archived, view.event.confirmed,
      view.event.free)

    events.filter(_.id === view.event.id).map(_.forUpdate).update(updateTuple)

    val facilitators = TableQuery[EventFacilitators]
    facilitators.filter(_.eventId === view.event.id).delete
    view.event.facilitatorIds.distinct.foreach(facilitatorId ⇒
      facilitators += (view.event.id.get, facilitatorId))
    EventInvoice._update(view.invoice)

    view
  }

  /**
   * Updates rating for the given event
   * @param eventId Event id
   * @param rating New rating
   */
  def updateRating(eventId: Long, rating: Float): Unit = DB.withSession {
    implicit session ⇒
      events.filter(_.id === eventId).map(_.rating).update(rating)
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
    parentQuery: Query[Events, Event, Seq]): Query[Events, Event, Seq] = {
    future map { value ⇒
      val now = LocalDate.now
      val today = new LocalDate(
        now.getValue(0),
        now.getValue(1),
        now.getValue(2))
      if (value)
        parentQuery.filter(_.start > today)
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
    parentQuery: Query[Events, Event, Seq]) = {
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
    parentQuery: Query[Events, Event, Seq]) = {
    archived map { value ⇒
      parentQuery.filter(_.archived === value)
    } getOrElse parentQuery
  }
}

object EventService {
  private val instance = new EventService()

  def get: EventService = instance

}
