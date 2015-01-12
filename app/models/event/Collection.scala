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

import models.{ Event, PeopleCollection, EventInvoice }
import models.database.{ EventFacilitators, EventInvoices }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

object Collection {

  /**
   * Fill events with facilitators (using only one query to database)
   * @param events List of events
   * @return
   */
  def facilitators(events: List[Event]): Unit = DB.withSession { implicit session: Session ⇒
    val ids = events.map(_.id.get).distinct.toList
    val query = for {
      facilitation ← EventFacilitators if facilitation.eventId inSet ids
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
   * Fill events with invoices (using only one query to database)
   * @param events List of events
   * @return
   */
  def invoices(events: List[Event]): Unit = DB.withSession { implicit session: Session ⇒
    val ids = events.map(_.id.get).distinct.toList
    val query = for {
      invoice ← EventInvoices if invoice.eventId inSet ids
    } yield invoice
    val invoices = query.list
    events.foreach(e ⇒ e.invoice_=(invoices.find(_.eventId == e.id).getOrElse(EventInvoice(None, None, 0, None, None))))
  }
}
