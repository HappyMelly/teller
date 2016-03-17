/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models.repository.cm.brand

import models.cm.brand.EventType
import models.database.brand.EventTypeTable
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EventTypeRepository(app: Application)  extends HasDatabaseConfig[JdbcProfile]
  with EventTypeTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val types = TableQuery[EventTypes]

  /**
   * Deletes event type object form database
   *
   * @param id Event type id
   */
  def delete(id: Long): Unit = db.run(types.filter(_.id === id).delete)

  /**
   * Returns if an event type with the given id exists
   *
   * @param id Event type id
   */
  def exists(id: Long): Future[Boolean] = db.run(types.filter(_.id === id).exists.result)

  /**
   * Returns an event type if it exists, otherwise - None
 *
   * @param id Event type id
   * @return
   */
  def find(id: Long): Future[Option[EventType]] = db.run(types.filter(_.id === id).result).map(_.headOption)

  /**
   * Returns a list of event types for the given brand
   *
   * @param brandId Brand identifier
   */
  def findByBrand(brandId: Long): Future[List[EventType]] =
    db.run(types.filter(_.brandId === brandId).result).map(_.toList)

  /**
    * Returns the requested event type
 *
    * @param id Event type id
    */
  def get(id: Long): Future[EventType] = db.run(types.filter(_.id === id).result).map(_.head)

  /**
   * Inserts event type data into database
   *
   * @param value Event type object
   * @return Updated object with id
   */
  def insert(value: EventType): Future[EventType] = {
    val query = types returning types.map(_.id) into ((eventType, id) => eventType.copy(id = Some(id)))
    db.run(query += value)
  }

  /**
   * Updates the given event type in database
 *
   * @param value Event type object
   * @return Returns the updated object
   */
  def update(value: EventType): Future[EventType] = {
    val tuple = (value.name, value.defaultTitle, value.maxHours, value.free)
    db.run(types.filter(_.id === value.id).map(_.forUpdate).update(tuple)).map(_ => value)
  }
}

