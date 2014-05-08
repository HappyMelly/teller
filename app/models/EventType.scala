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

import models.database.EventTypes
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

case class EventType(id: Option[Long], brandId: Long, name: String, defaultTitle: Option[String]) {

  def brand: Brand = DB.withSession { implicit session: Session ⇒
    Brand.find(this.brandId).get
  }

  def insert: EventType = DB.withSession { implicit session: Session ⇒
    val id = EventTypes.forInsert.insert(this)
    this.copy(id = Some(id))
  }

}

object EventType {

  /**
   * Checks if an event type with id exists
   */
  def exists(id: Long): Boolean = DB.withSession { implicit session: Session ⇒
    Query(Query(EventTypes).filter(_.id === id).exists).first
  }

  /**
   * Returns a list of all event types for the given brand.
   */
  def findByBrand(brandId: Long): List[EventType] = DB.withSession { implicit session: Session ⇒
    Query(EventTypes).filter(_.brandId === brandId).list
  }

  /**
   * Finds a event type by ID.
   */
  def find(id: Long): Option[EventType] = DB.withSession { implicit session: Session ⇒
    Query(EventTypes).filter(_.id === id).firstOption
  }

  def delete(id: Long): Unit = DB.withSession { implicit session: Session ⇒
    EventTypes.filter(_.id === id).mutate(_.delete)
  }

}
