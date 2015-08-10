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

package models.database.event

import models.database.PortableJodaSupport._
import models.database.Brands
import models.event.EventCancellation
import org.joda.time.LocalDate
import play.api.db.slick.Config.driver.simple._

/**
 * `EventCancellation` database table mapping.
 */
private[models] class EventCancellations(tag: Tag)
    extends Table[EventCancellation](tag, "EVENT_CANCELLATION") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def brandId = column[Long]("BRAND_ID")
  def facilitatorId = column[Long]("FACILITATOR_ID")
  def event = column[String]("EVENT")
  def eventType = column[String]("EVENT_TYPE")
  def city = column[String]("CITY")
  def countryCode = column[String]("COUNTRY_CODE")
  def start = column[LocalDate]("START_DATE")
  def end = column[LocalDate]("END_DATE")
  def free = column[Boolean]("FREE")
  def reason = column[Option[String]]("REASON", O.DBType("TEXT"))
  def participantsNumber = column[Option[Int]]("PARTICIPANTS_NUMBER")
  def participantsInfo = column[Option[String]]("PARTICIPANTS_INFO", O.DBType("TEXT"))

  def brand = foreignKey("BRAND_FK", brandId, TableQuery[Brands])(_.id)

  def * = (id.?, brandId, facilitatorId, event, eventType, city,
    countryCode, start, end, free, reason, participantsNumber,
    participantsInfo) <> (EventCancellation.tupled, EventCancellation.unapply)

}
