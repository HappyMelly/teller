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
package models.repository.core.payment

import models.core.payment.Charge
import models.database.core.payment.ChargeTable
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** Provides operations with database related to charges */
class ChargeRepository(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with ChargeTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._

  private val charges = TableQuery[Charges]

  /**
    * Checks if the record with the remote id exists
    * @param chargeId Remote charge id
    */
  def exists(chargeId: String): Future[Boolean] = db.run(charges.filter(_.remoteId === chargeId).exists.result)

  def findByCustomer(customerId: Long): Future[Seq[Charge]] =
    db.run(charges.filter(_.customerId === customerId).result)

  /**
   * Inserts the given record to database
   *
   * @param charge Object to insert
   * @return Returns member object with updated id
   */
  def insert(charge: Charge): Future[Charge] = {
    val query = charges returning charges.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += charge)
  }
}
