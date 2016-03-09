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

import models.core.payment.{CreditCard, Charge}
import models.database.core.payment.{CreditCardTable, ChargeTable}
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/** Provides operations with database related to charges */
class CreditCardRepository(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with CreditCardTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._

  private val cards = TableQuery[CreditCards]

  def delete(ids: Seq[Long]): Future[Int] = db.run(cards.filter(_.id inSet ids).delete)

  def findByCustomer(customerId: Long): Future[Seq[CreditCard]] =
    db.run(cards.filter(_.customerId === customerId).result)

  /**
   * Inserts the given record to database
   *
   * @param card Object to insert
   * @return Returns member object with updated id
   */
  def insert(card: CreditCard): Future[CreditCard] = {
    val query = cards returning cards.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += card)
  }
}
