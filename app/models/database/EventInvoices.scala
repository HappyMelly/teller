/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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

package models.database

import models.EventInvoice
import play.api.db.slick.Config.driver.simple._

/**
 * `EventInvoice` table mapping
 */
private[models] object EventInvoices extends Table[EventInvoice]("EVENT_INVOICE") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def eventId = column[Option[Long]]("EVENT_ID")
  def invoiceTo = column[Long]("INVOICE_TO")
  def invoiceBy = column[Option[Long]]("INVOICE_BY")
  def number = column[Option[String]]("NUMBER")

  def event = foreignKey("EVENT_INVOICE_FK", eventId, Events)(_.id)
  def invoicedOrg = foreignKey("INVOICE_TO_FK", invoiceTo, Organisations)(_.id)

  def * = id.? ~ eventId ~ invoiceTo ~ invoiceBy ~ number <> (EventInvoice.apply _, EventInvoice.unapply _)

  def forInsert = * returning id

  def forUpdate = invoiceTo ~ invoiceBy ~ number
}