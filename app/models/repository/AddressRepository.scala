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
package models.repository

import models.Address
import models.database._
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by sery0ga on 25/01/16.
  */
class AddressRepository(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with AddressTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val addresses = TableQuery[Addresses]

  /**
    * Return the requested address
    * @param id Address identifier
    */
  def get(id: Long): Future[Address] = db.run(addresses.filter(_.id === id).result).map(_.head)

  /**
    * Returns list of the requested addresses
    * @param ids Ids of the addresses
    */
  def find(ids: List[Long]): Future[List[Address]] = if (ids.isEmpty)
    Future.successful(List())
  else
    db.run(addresses.filter(_.id inSet ids).result).map(_.toList)

  def insert(address: Address): Future[Address] = {
    val query = addresses returning addresses.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += address)
  }

  def update(address: Address): Future[Int] =
    db.run(addresses.filter(_.id === address.id).update(address))
}
