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
package models.service.event

import models.database.event.EventCancellations
import models.event.EventCancellation
import models.service.Services
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import scala.language.postfixOps

class EventCancellationService extends Services {

  private val cancellations = TableQuery[EventCancellations]

  /**
   * Returs list of event cancellation belonged the given brands
   *
   * @param brands List of brand identifiers
   */
  def findByBrands(brands: List[Long]): List[EventCancellation] =
    DB.withSession { implicit session ⇒
      cancellations.filter(_.brandId inSet brands).list
    }

  /**
   * Inserts event cancellation into database
   *
   * @param cancellation EventCancellation object
   * @return Updated object object with id
   */
  def insert(cancellation: EventCancellation): EventCancellation = DB.withSession {
    implicit session ⇒
      val id = (cancellations returning cancellations.map(_.id)) += cancellation
      cancellation.copy(id = Some(id))
  }

}

object EventCancellationService {
  private val instance = new EventCancellationService()

  def get: EventCancellationService = instance

}
