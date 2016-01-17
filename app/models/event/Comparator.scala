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
import models.EventView
import models.service.Services
import play.api.i18n.Messages
import views.Languages

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.concurrent.duration._

object Comparator extends Services {

  abstract class FieldChange(label: String, oldValue: Any, newValue: Any) {
    def changed() = oldValue != newValue
    def printable(): (String, String, String) = (label, newValue.toString, oldValue.toString)
  }

  class SimpleFieldChange(label: String, oldValue: String, newValue: String) extends FieldChange(label, oldValue, newValue) {
    def toStr = s"$label: $newValue (was: $oldValue)"
  }

  class BrandChange(label: String, oldValue: Long, newValue: Long) extends FieldChange(label, oldValue, newValue) {
    def toStr = names map { case (oldName, newName) =>
      s"$label: $newName (was: $oldName)"
    }

    override def printable(): (String, String, String) = {
      val (oldName, newName) = Await.result(names, 3.seconds)
      (label, newName, oldName)
    }

    protected def names: Future[(String, String)] = {
      (for {
        o <- brandService.get(oldValue)
        n <- brandService.get(newValue)
      } yield (o, n)) map { case (oldBrand, newBrand) =>
        (oldBrand.name, newBrand.name)
      }
    }
  }

  class EventTypeChange(label: String, oldValue: Long, newValue: Long) extends FieldChange(label, oldValue, newValue) {
    def toStr = names map { case (oldName, newName) =>
      s"$label: $newName (was: $oldName)"
    }

    override def printable(): (String, String, String) = {
      val (oldName, newName) = Await.result(names, 3.seconds)
      (label, newName, oldName)
    }

    protected def names: Future[(String, String)] = {
      (for {
        o <- eventTypeService.get(oldValue)
        n <- eventTypeService.get(newValue)
      } yield (o, n)) map { case (oldEventType, newEventType) =>
        (oldEventType.name, newEventType.name)
      }
    }
  }

  class InvoiceChange(label: String, oldValue: Long, newValue: Long) extends FieldChange(label, oldValue, newValue) {
    def toStr = names map { case (oldName, newName) =>
      s"$label: $newName (was: $oldName)"
    }

    override def printable(): (String, String, String) = {
      val (oldName, newName) = Await.result(names, 3.seconds)
      (label, newName, oldName)
    }

    protected def names: Future[(String, String)] = {
      (for {
        o <- orgService.get(oldValue)
        n <- orgService.get(newValue)
      } yield (o, n)) map { case (oldEventType, newEventType) =>
        (oldEventType.name, newEventType.name)
      }
    }
  }

  class FacilitatorChange(label: String, oldValue: List[Long], newValue: List[Long]) extends FieldChange(label, oldValue, newValue) {
    def toStr = facilitators map { case (added, removed) =>
      s"Removed $label: $removed / Added $label: $added"
    }

    override def printable(): (String, String, String) = {
      val (added, removed) = Await.result(facilitators, 3.seconds)
      (label, added, removed)
    }

    protected def facilitators: Future[(String, String)] = {
      (for {
        o <- personService.find(oldValue.diff(newValue))
        n <- personService.find(newValue.diff(oldValue))
      } yield (o, n)) map { case (removed, added) =>
        (added.map(_.fullName).mkString(", "), removed.map(_.fullName).mkString(", "))
      }
    }
  }

  /**
   * Compares two events and returns a list of changes.
    *
    * @param was The event with ‘old’ values.
   * @param now The event with ‘new’ values.
   */
  def compare(was: EventView, now: EventView)(implicit messages: Messages): List[FieldChange] = {
    val changes = List(
      new BrandChange("Brand", was.event.brandId, now.event.brandId),
      new EventTypeChange("Event Type", was.event.eventTypeId, now.event.eventTypeId),
      new SimpleFieldChange("Title", was.event.title, now.event.title),
      new SimpleFieldChange("Spoken Language", Languages.all.getOrElse(was.event.language.spoken, ""),
        Languages.all.getOrElse(now.event.language.spoken, "")),
      new SimpleFieldChange("Second Spoken Language", Languages.all.getOrElse(was.event.language.secondSpoken.getOrElse(""), ""),
        Languages.all.getOrElse(now.event.language.secondSpoken.getOrElse(""), "")),
      new SimpleFieldChange("Materials Language", Languages.all.getOrElse(was.event.language.materials.getOrElse(""), ""),
        Languages.all.getOrElse(now.event.language.materials.getOrElse(""), "")),
      new SimpleFieldChange("City", was.event.location.city, now.event.location.city),
      new SimpleFieldChange("Country", Messages("country." + was.event.location.countryCode), Messages("country." + now.event.location.countryCode)),
      new SimpleFieldChange("Description",
        Transform from Markdown to HTML fromString was.event.details.description.getOrElse("") toString,
        Transform from Markdown to HTML fromString now.event.details.description.getOrElse("") toString),
      new SimpleFieldChange("Special Attention",
        Transform from Markdown to HTML fromString was.event.details.specialAttention.getOrElse("") toString,
        Transform from Markdown to HTML fromString now.event.details.specialAttention.getOrElse("") toString),
      new SimpleFieldChange("Start Date", was.event.schedule.start.toString, now.event.schedule.start.toString),
      new SimpleFieldChange("End Date", was.event.schedule.end.toString, now.event.schedule.end.toString),
      new SimpleFieldChange("Hours Per Day", was.event.schedule.hoursPerDay.toString, now.event.schedule.hoursPerDay.toString),
      new SimpleFieldChange("Total Hours", was.event.schedule.totalHours.toString, now.event.schedule.totalHours.toString),
      new SimpleFieldChange("Organizer Website", was.event.organizer.webSite.getOrElse(""), now.event.organizer.webSite.getOrElse("")),
      new SimpleFieldChange("Registration Page", was.event.organizer.registrationPage.getOrElse(""), now.event.organizer.registrationPage.getOrElse("")),
      new SimpleFieldChange("Private Event", was.event.notPublic.toString, now.event.notPublic.toString),
      new SimpleFieldChange("Achived Event", was.event.archived.toString, now.event.archived.toString),
      new SimpleFieldChange("Follow Up Emails", was.event.followUp.toString, now.event.followUp.toString),
      new FacilitatorChange("Facilitators", was.event.facilitatorIds, now.event.facilitatorIds),
      new InvoiceChange("Invoice To", was.invoice.invoiceTo, now.invoice.invoiceTo))

    changes.filter(change ⇒ change.changed())
  }

}
