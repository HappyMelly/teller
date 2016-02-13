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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing
 * Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models.repository.event

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.database.event.EventRequestTable
import models.event.EventRequest
import org.joda.time.LocalDate
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Contains a set of methods for retrieving/updating event requests in database
 */
class EventRequestRepository(app: Application)  extends HasDatabaseConfig[JdbcProfile]
  with EventRequestTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val requests = TableQuery[EventRequests]

  /**
    * Deletes event requests which end date is less than the given date
    * @param expiration Expiration date
    */
  def deleteExpired(expiration: LocalDate) =
    db.run(requests.filter(_.end <= expiration).delete)

  /**
    * Returns event request if exists
    *
    * @param requestId Request id
    */
  def find(requestId: Long): Future[Option[EventRequest]] =
    db.run(requests.filter(_.id === requestId).result).map(_.headOption)

  /**
    * Returns event request if exists
    *
    * @param hashedId Request id
    */
  def find(hashedId: String): Future[Option[EventRequest]] =
    db.run(requests.filter(_.hashedId === hashedId).result).map(_.headOption)

  /**
   * Returns list of event requests belonged the given brand
   *
   * @param brandId Brand identifier
   */
  def findByBrand(brandId: Long): Future[List[EventRequest]] =
    db.run(requests.filter(_.brandId === brandId).result).map(_.toList)

  /**
    * Returns all event requests with one participant valid for upcoming event notifications
    */
  def findWithOneParticipant: Future[List[EventRequest]] =
    db.run(requests.filter(_.participantsNumber === 1).filter(_.unsubscribed === false).result).map(_.toList)

  /**
   * Inserts event request into database
   *
   * @param request EventRequest object
   * @return Updated object with id
   */
  def insert(request: EventRequest): Future[EventRequest] = {
    val query = requests returning requests.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += request)
  }

  /**
    * Update the given event request in dabase
    *
    * @param request Event request
    */
  def update(request: EventRequest): Future[EventRequest] =
    db.run(requests.filter(_.id === request.id).update(request)).map(_ => request)
}
