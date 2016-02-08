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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models.service

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.database.PaymentRecordTable
import models.payment.Record
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** Provides operations with database related to payment records */
class PaymentRecordService(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with PaymentRecordTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._

  private val records = TableQuery[PaymentRecords]

  /**
   * Deletes a record from database
   * @param objectId Object id
   * @param person If true, object is a person, otherwise - org
   */
  def delete(objectId: Long, person: Boolean): Unit =
    db.run(records.filter(_.objectId === objectId).filter(_.person === person).delete)

  /**
   * Inserts the given record to database
   *
   * @param r Object to insert
   * @return Returns member object with updated id
   */
  def insert(r: Record): Future[Record] = {
    val query = records returning records.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += r)
  }

  /**
   * Returns list of payment records made by the given person
   * @param personId Person identifier
   * @return
   */
  def findByPerson(personId: Long): Future[List[Record]] =
    db.run(records.filter(_.objectId === personId).filter(_.person === true).sortBy(_.created).result).map(_.toList)

  /**
   * Returns list of payment records made by the given organisation
   * @param orgId Organisation identifier
   * @return
   */
  def findByOrganisation(orgId: Long): Future[List[Record]] =
    db.run(records.filter(_.objectId === orgId).filter(_.person === false).sortBy(_.created).result).map(_.toList)
}
