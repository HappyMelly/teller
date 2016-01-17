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

import models.database.event.EventCancellationTable
import models.event.EventCancellation
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class EventCancellationService extends HasDatabaseConfig[JdbcProfile]
  with EventCancellationTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._

  private val cancellations = TableQuery[EventCancellations]

  /**
   * Returs list of event cancellation belonged the given brands
   *
   * @param brands List of brand identifiers
   */
  def findByBrands(brands: List[Long]): Future[List[EventCancellation]] =
    db.run(cancellations.filter(_.brandId inSet brands).result).map(_.toList)

  /**
   * Inserts event cancellation into database
   *
   * @param cancellation EventCancellation object
   * @return Updated object object with id
   */
  def insert(cancellation: EventCancellation): Future[EventCancellation] = {
    val query = cancellations returning cancellations.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += cancellation)
  }

}

object EventCancellationService {
  private val instance = new EventCancellationService()

  def get: EventCancellationService = instance

}
