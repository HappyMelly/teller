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

import org.joda.time.{ LocalDate, DateTime }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import scala.slick.lifted.Query
import models.database.{ EventFacilitators, Participants, Events }
import play.api.i18n.Messages
import com.github.tototoshi.slick.JodaSupport._
import services.EmailService

case class Schedule(start: LocalDate, end: LocalDate, hoursPerDay: Int, totalHours: Int)
case class Details(description: Option[String], specialAttention: Option[String],
  webSite: Option[String], registrationPage: Option[String])
case class Location(city: String, countryCode: String)

/**
 * An event such as a Management 3.0 course or a DARE Festival.
 */
case class Event(
  id: Option[Long],
  eventTypeId: Long,
  brandCode: String,
  title: String,
  spokenLanguage: String,
  materialsLanguage: Option[String],
  location: Location,
  details: Details,
  schedule: Schedule,
  notPublic: Boolean = false,
  archived: Boolean = false,
  confirmed: Boolean = false,
  invoice: EventInvoice,
  created: DateTime = DateTime.now(),
  createdBy: String,
  updated: DateTime,
  updatedBy: String,
  facilitatorIds: List[Long]) {

  val longTitle: String = title + " / " + location.city + " / " + schedule.start.toString

  lazy val facilitators: List[Person] = DB.withSession { implicit session: Session ⇒
    val query = for {
      facilitation ← EventFacilitators if facilitation.eventId === this.id
      person ← facilitation.facilitator
    } yield person
    query.sortBy(_.lastName.toLowerCase).list
  }

  lazy val participants: List[Person] = DB.withSession { implicit session: Session ⇒
    val query = for {
      participation ← Participants if participation.eventId === this.id
      person ← participation.participant
    } yield person
    query.sortBy(_.lastName.toLowerCase).list
  }

  def canEdit(account: UserAccount): Boolean = DB.withSession { implicit session: Session ⇒
    facilitatorIds.exists(_ == account.personId) || canAdministrate(account)
  }

  def canAdministrate(account: UserAccount): Boolean = DB.withSession { implicit session: Session ⇒
    account.editor || Brand.find(brandCode).get.coordinator.id.get == account.personId
  }

  def insert: Event = DB.withSession { implicit session: Session ⇒
    val insertTuple = (eventTypeId, brandCode, title, spokenLanguage, materialsLanguage, location.city, location.countryCode,
      details.description, details.specialAttention, details.webSite, details.registrationPage,
      schedule.start, schedule.end, schedule.hoursPerDay, schedule.totalHours,
      notPublic, archived, confirmed, created, createdBy)
    val id = Events.forInsert.insert(insertTuple)
    this.facilitatorIds.foreach(facilitatorId ⇒ EventFacilitators.insert((id, facilitatorId)))
    EventInvoice.insert(this.invoice.copy(eventId = Some(id)))
    this.copy(id = Some(id))
  }

  def delete(): Unit = Event.delete(this.id.get)

  def update: Event = DB.withSession { implicit session: Session ⇒
    val updateTuple = (eventTypeId, brandCode, title, spokenLanguage, materialsLanguage, location.city, location.countryCode,
      details.description, details.specialAttention, details.webSite, details.registrationPage,
      schedule.start, schedule.end, schedule.hoursPerDay, schedule.totalHours,
      notPublic, archived, confirmed, updated, updatedBy)
    val updateQuery = Events.filter(_.id === this.id).map(_.forUpdate)
    updateQuery.update(updateTuple)

    EventFacilitators.where(_.eventId === this.id).mutate(_.delete())
    this.facilitatorIds.foreach(facilitatorId ⇒ EventFacilitators.insert((this.id.get, facilitatorId)))

    EventInvoice.update(this.invoice)

    this
  }
}

object Event {

  abstract class FieldChange(label: String, oldValue: Any, newValue: Any) {
    def changed() = oldValue != newValue
  }

  class SimpleFieldChange(label: String, oldValue: String, newValue: String) extends FieldChange(label, oldValue, newValue) {
    override def toString = s"$label: $newValue (was: $oldValue)"
  }

  class BrandChange(label: String, oldValue: String, newValue: String) extends FieldChange(label, oldValue, newValue) {
    override def toString = {
      val oldBrand = Brand.find(oldValue).get.brand.name
      val newBrand = Brand.find(newValue).get.brand.name
      s"$label: $newBrand (was: $oldBrand)"
    }
  }

  class EventTypeChange(label: String, oldValue: Long, newValue: Long) extends FieldChange(label, oldValue, newValue) {
    override def toString = {
      val oldEventType = EventType.find(oldValue).get.name
      val newEventType = EventType.find(newValue).get.name
      s"$label: $newEventType (was: $oldEventType)"
    }
  }

  class InvoiceChange(label: String, oldValue: Long, newValue: Long) extends FieldChange(label, oldValue, newValue) {
    override def toString = {
      val oldInvoiceToOrg = Organisation.find(oldValue).get.name
      val newInvoiceToOrg = Organisation.find(newValue).get.name
      s"$label: $newInvoiceToOrg (was: $oldInvoiceToOrg)"
    }
  }

  class FacilitatorChange(label: String, oldValue: List[Long], newValue: List[Long]) extends FieldChange(label, oldValue, newValue) {
    override def toString = {
      val newFacilitators = newValue.diff(oldValue).map(Person.find(_).get.fullName).mkString(", ")
      val removedFacilitators = oldValue.diff(newValue).map(Person.find(_).get.fullName).mkString(", ")
      s"Removed $label: $removedFacilitators / Added $label: $newFacilitators"
    }
  }

  /**
   * Compares two events and returns a list of changes.
   * @param was The event with ‘old’ values.
   * @param now The event with ‘new’ values.
   */
  def compare(was: Event, now: Event): List[FieldChange] = {
    val changes = List(
      new BrandChange("Brand", was.brandCode, now.brandCode),
      new EventTypeChange("Event Type", was.eventTypeId, now.eventTypeId),
      new SimpleFieldChange("Title", was.title, now.title),
      new SimpleFieldChange("Spoken Language", was.spokenLanguage, now.spokenLanguage),
      new SimpleFieldChange("Materials Language", was.materialsLanguage.getOrElse(""), now.materialsLanguage.getOrElse("")),
      new SimpleFieldChange("City", was.location.city, now.location.city),
      new SimpleFieldChange("Country", Messages("country." + was.location.countryCode), Messages("country." + now.location.countryCode)),
      new SimpleFieldChange("Description", was.details.description.getOrElse(""), now.details.description.getOrElse("")),
      new SimpleFieldChange("Special Attention", was.details.specialAttention.getOrElse(""), now.details.specialAttention.getOrElse("")),
      new SimpleFieldChange("Start Date", was.schedule.start.toString, now.schedule.start.toString),
      new SimpleFieldChange("End Date", was.schedule.end.toString, now.schedule.end.toString),
      new SimpleFieldChange("Hours Per Day", was.schedule.hoursPerDay.toString, now.schedule.hoursPerDay.toString),
      new SimpleFieldChange("Total Hours", was.schedule.totalHours.toString, now.schedule.totalHours.toString),
      new SimpleFieldChange("Organizer Website", was.details.webSite.getOrElse(""), now.details.webSite.getOrElse("")),
      new SimpleFieldChange("Registration Page", was.details.registrationPage.getOrElse(""), now.details.registrationPage.getOrElse("")),
      new SimpleFieldChange("Private Event", was.notPublic.toString, now.notPublic.toString),
      new SimpleFieldChange("Achived Event", was.archived.toString, now.archived.toString),
      new FacilitatorChange("Facilitators", was.facilitatorIds, now.facilitatorIds),
      new InvoiceChange("Invoice To", was.invoice.invoiceTo, now.invoice.invoiceTo))

    changes.filter(change ⇒ change.changed())
  }

  def getFacilitatorIds(id: Long): List[Long] = DB.withSession { implicit session: Session ⇒
    (for {
      e ← EventFacilitators if e.eventId === id
    } yield (e)).list.map(_._2)
  }

  /**
   * Return a number of events with a specified event type
   * @param eventTypeId Event type id
   * @return Int
   */
  def getNumberByEventType(eventTypeId: Long): Int = DB.withSession { implicit session: Session ⇒
    Query(Events).filter(_.eventTypeId === eventTypeId).list.length
  }

  /**
   * Delete an event.
   */
  def delete(id: Long): Unit = DB.withSession { implicit session: Session ⇒
    EventFacilitators.where(_.eventId === id).mutate(_.delete())
    EventInvoice.delete(id)
    Query(Events).filter(_.id === id).delete
  }

  /**
   * Returns true if and only if a user is allowed to manage this event.
   */
  def canManage(eventId: Long, user: UserAccount): Boolean = DB.withSession { implicit session: Session ⇒
    if (find(eventId).isEmpty)
      false
    else
      findByUser(user).exists(_.id.get == eventId)
  }

  def find(id: Long): Option[Event] = DB.withSession { implicit session: Session ⇒
    Query(Events).filter(_.id === id).list.headOption
  }

  /**
   * Return a list of events based on several parameters
   */
  def findByParameters(brandCode: String,
    future: Option[Boolean] = None,
    public: Option[Boolean] = None,
    confirmed: Option[Boolean] = None,
    countryCode: Option[String] = None,
    eventType: Option[Long] = None): List[Event] = DB.withSession { implicit session: Session ⇒
    val baseQuery = Query(Events).filter(_.brandCode === brandCode)

    val timeQuery = future.map { value ⇒
      val now = LocalDate.now()
      val today = new LocalDate(now.getValue(0), now.getValue(1), now.getValue(2))
      if (value) baseQuery.filter(_.end >= today)
      else baseQuery.filter(_.end <= today)
    }.getOrElse(baseQuery)

    val publicityQuery = public.map { value ⇒
      timeQuery.filter(_.notPublic === !value)
    }.getOrElse(timeQuery)

    val confirmedQuery = confirmed.map { value ⇒
      publicityQuery.filter(_.confirmed === value)
    }.getOrElse(publicityQuery)

    val countryQuery = countryCode.map { value ⇒
      confirmedQuery.filter(_.countryCode === value)
    }.getOrElse(confirmedQuery)

    val typeQuery = eventType.map { value ⇒
      countryQuery.filter(_.eventTypeId === value)
    }.getOrElse(countryQuery)

    typeQuery.sortBy(_.start).list
  }

  /**
   * Returns a list of all events for a specified user
   */
  def findByUser(user: UserAccount): List[Event] = DB.withSession { implicit session: Session ⇒
    if (user.editor)
      Query(Events).filter(_.archived === false).sortBy(_.start).list
    else {
      val brands = Brand.findForUser(user)
      if (brands.length > 0) {
        val brandCodes = brands.map(_.code)
        val events = Query(Events).filter(_.archived === false).sortBy(_.start).list
        events.filter(e ⇒ brandCodes.exists(_ == e.brandCode))
      } else {
        Query(Events).filter(_.archived === false).sortBy(_.start).list
      }
    }
  }

  /**
   * Returns a list of all events for a specified coordinator
   */
  def findByCoordinator(coordinatorId: Long): List[Event] = DB.withSession { implicit session: Session ⇒
    val brands = Brand.findByCoordinator(coordinatorId)
    if (brands.length > 0) {
      val brandCodes = brands.map(_.code)
      val events = Query(Events).filter(_.archived === false).sortBy(_.start).list
      events.filter(e ⇒ brandCodes.exists(_ == e.brandCode))
    } else {
      List[Event]()
    }
  }

  /**
   * Return a list of events for a given facilitator
   */
  def findByFacilitator(facilitatorId: Long, brandCode: String,
    future: Option[Boolean] = None,
    public: Option[Boolean] = None): List[Event] = DB.withSession { implicit session: Session ⇒

    val baseQuery = for {
      entry ← EventFacilitators if entry.facilitatorId === facilitatorId
      event ← Events if event.id === entry.eventId && event.brandCode === brandCode
    } yield event

    val timeQuery = future.map { value ⇒
      val now = LocalDate.now()
      val today = new LocalDate(now.getValue(0), now.getValue(1), now.getValue(2))
      if (value) baseQuery.filter(_.end >= today)
      else baseQuery.filter(_.end <= today)
    }.getOrElse(baseQuery)

    val publicityQuery = public.map { value ⇒
      timeQuery.filter(_.notPublic === !value)
    }.getOrElse(timeQuery)

    publicityQuery.sortBy(_.start).list
  }

  def findByBrandGroupByCountry(brandCode: String): List[(String, Int)] = DB.withSession { implicit session: Session ⇒
    val now = LocalDate.now()
    val today = new LocalDate(now.getValue(0), now.getValue(1), now.getValue(2))
    val query = Query(Events).filter(_.brandCode === brandCode).filter(_.end >= today).groupBy(_.countryCode)
    query.map { case (code, events) ⇒ (code, events.length) }.list
  }

  def findActive: List[Event] = DB.withSession { implicit session: Session ⇒
    Query(Events).filter(_.archived === false).sortBy(_.title.toLowerCase).list
  }

  def findAll: List[Event] = DB.withSession { implicit session: Session ⇒
    Query(Events).sortBy(_.title.toLowerCase).list
  }

  def sendConfirmationAlert = {
    Brand.findAll.foreach { brand ⇒
      Event.findByParameters(brand.brand.code, Some(false), None, Some(false), None, None).foreach { event ⇒
        val subject = "Сonfirm your event " + event.title
        EmailService.send(event.facilitators.toSet, None, None, subject, mail.txt.confirm(event).toString)
      }
    }
  }

}

