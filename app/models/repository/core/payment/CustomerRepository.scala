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

import models.database.core.payment.CustomerTable
import models.core.payment.{CustomerType, Customer}
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Manages customer records in database
  */
class CustomerRepository (app: Application) extends HasDatabaseConfig[JdbcProfile]
  with CustomerTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val customers = TableQuery[Customers]

  /**
    * Deletes the given customer
    *
    * @param objectId Customer identifier
    * @param objectType Type
    */
  def delete(objectId: Long, objectType: CustomerType.Value): Future[Int] = {
    import Customers.customerTypeMapper
    db.run(customers.filter(_.objectId === objectId).filter(_.objectType === objectType).delete)
  }

  /**
    * Returns customer for the given id
    *
    * @param id Customer identifier
    */
  def find(id: Long): Future[Option[Customer]] =
    db.run(customers.filter(_.id === id).result).map(_.headOption)

  /**
    * Returns customer for the given id
    *
    * @param remoteId Customer identifier
    */
  def find(remoteId: String): Future[Option[Customer]] =
    db.run(customers.filter(_.remoteId === remoteId).result).map(_.headOption)

  /**
    * Returns customer for the given object
    *
    * @param objectId Customer identifier
    * @param objectType Type
    */
  def find(objectId: Long, objectType: CustomerType.Value): Future[Option[Customer]] = {
    import Customers.customerTypeMapper
    db.run(customers.filter(_.objectId === objectId).filter(_.objectType === objectType).result).map(_.headOption)
  }

  /**
    * Inserts new customer record to the databae
    *
    * @param customer Customer
    */
  def insert(customer: Customer): Future[Customer] = {
    val query = customers returning customers.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += customer)
  }
}
