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

import models.database.EventInvoices
import models.service.OrganisationService
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

case class EventInvoice(id: Option[Long],
  eventId: Option[Long],
  invoiceTo: Long,
  invoiceBy: Option[Long],
  number: Option[String]) {

  lazy val invoiceToOrg: Option[Organisation] = OrganisationService.get.find(invoiceTo)
  lazy val invoiceByOrg: Option[Organisation] = invoiceBy.map{ OrganisationService.get.find(_) }.getOrElse(None)
}

object EventInvoice {

  def findByEvent(id: Long): EventInvoice = DB.withSession { implicit session: Session ⇒
    Query(EventInvoices).filter(_.eventId === id).first
  }

  def update(invoice: EventInvoice): Unit = DB.withSession {
    implicit session: Session ⇒
      _update(invoice)
  }

  def _update(invoice: EventInvoice)(implicit session: Session): Unit =
    EventInvoices.filter(_.id === invoice.id)
      .map(_.forUpdate)
      .update((invoice.invoiceTo, invoice.invoiceBy, invoice.number))
}

