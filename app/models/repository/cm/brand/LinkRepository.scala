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

import models.cm.brand.BrandLink
import models.database.brand.BrandLinkTable
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Contains methods for managing brand links in database
  */
class LinkRepository(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with BrandLinkTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val links = TableQuery[BrandLinks]

  /**
    * Deletes the given link from database
    *
    * Brand identifier is for security reasons. If a user passes security
    * check for the brand, the user cannot delete links which aren't belonged to
    * another brand.
    *
    * @param brandId Brand identifier
    * @param id Link identifier
    */
  def delete(brandId: Long, id: Long): Future[Int] = {
    val action = links.filter(_.id === id).filter(_.brandId === brandId).delete
    db.run(action)
  }

  /**
    * Return list of links for the given brand
    *
    * @param brandId Brand identifier
    */
  def find(brandId: Long): Future[List[BrandLink]] =
    db.run(links.filter(_.brandId === brandId).result).map(_.toList)

  /**
    * Inserts the given link to database
    *
    * @param link Brand link
    */
  def insert(link: BrandLink): Future[BrandLink] = {
    val query = links returning links.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += link)
  }

}
