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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models.event

import laika.api._
import laika.parse.markdown.Markdown
import laika.render.HTML
import models.service.brand.EventTypeService
import models.service.{ OrganisationService, PersonService }
import models.{ Brand, Event }
import play.api.i18n.Messages
import views.Languages

import scala.language.postfixOps

object Comparator {

  abstract class FieldChange(label: String, oldValue: Any, newValue: Any) {
    def changed() = oldValue != newValue
    def printable(): (String, String, String) = (label, newValue.toString, oldValue.toString)
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

    override def printable(): (String, String, String) = {
      val oldBrand = Brand.find(oldValue).get.brand.name
      val newBrand = Brand.find(newValue).get.brand.name
      (label, newBrand, oldBrand)
    }
  }

  class EventTypeChange(label: String, oldValue: Long, newValue: Long) extends FieldChange(label, oldValue, newValue) {
    override def toString = {
      val label, newEventType, oldEventType = printable()
      s"$label: $newEventType (was: $oldEventType)"
    }

    override def printable(): (String, String, String) = {
      val oldEventType = EventTypeService.get.find(oldValue).get.name
      val newEventType = EventTypeService.get.find(newValue).get.name
      (label, newEventType, oldEventType)
    }
  }

  class InvoiceChange(label: String, oldValue: Long, newValue: Long) extends FieldChange(label, oldValue, newValue) {
    override def toString = {
      val oldInvoiceToOrg = OrganisationService.get.find(oldValue).get.name
      val newInvoiceToOrg = OrganisationService.get.find(newValue).get.name
      s"$label: $newInvoiceToOrg (was: $oldInvoiceToOrg)"
    }

    override def printable(): (String, String, String) = {
      val oldInvoiceToOrg = OrganisationService.get.find(oldValue).get.name
      val newInvoiceToOrg = OrganisationService.get.find(newValue).get.name
      (label, newInvoiceToOrg, oldInvoiceToOrg)
    }

  }

  class FacilitatorChange(label: String, oldValue: List[Long], newValue: List[Long]) extends FieldChange(label, oldValue, newValue) {
    override def toString = {
      val newFacilitators = newValue.diff(oldValue).map(PersonService.get.find(_).get.fullName).mkString(", ")
      val removedFacilitators = oldValue.diff(newValue).map(PersonService.get.find(_).get.fullName).mkString(", ")
      s"Removed $label: $removedFacilitators / Added $label: $newFacilitators"
    }

    override def printable(): (String, String, String) = {
      val newFacilitators = newValue.diff(oldValue).map(PersonService.get.find(_).get.fullName).mkString(", ")
      val removedFacilitators = oldValue.diff(newValue).map(PersonService.get.find(_).get.fullName).mkString(", ")
      (label, newFacilitators, removedFacilitators)
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
      new SimpleFieldChange("Spoken Language", Languages.all.getOrElse(was.language.spoken, ""),
        Languages.all.getOrElse(now.language.spoken, "")),
      new SimpleFieldChange("Second Spoken Language", Languages.all.getOrElse(was.language.secondSpoken.getOrElse(""), ""),
        Languages.all.getOrElse(now.language.secondSpoken.getOrElse(""), "")),
      new SimpleFieldChange("Materials Language", Languages.all.getOrElse(was.language.materials.getOrElse(""), ""),
        Languages.all.getOrElse(now.language.materials.getOrElse(""), "")),
      new SimpleFieldChange("City", was.location.city, now.location.city),
      new SimpleFieldChange("Country", Messages("country." + was.location.countryCode), Messages("country." + now.location.countryCode)),
      new SimpleFieldChange("Description",
        Transform from Markdown to HTML fromString was.details.description.getOrElse("") toString,
        Transform from Markdown to HTML fromString now.details.description.getOrElse("") toString),
      new SimpleFieldChange("Special Attention",
        Transform from Markdown to HTML fromString was.details.specialAttention.getOrElse("") toString,
        Transform from Markdown to HTML fromString now.details.specialAttention.getOrElse("") toString),
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

}
