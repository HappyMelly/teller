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

package models.service.brand

import models.brand.Badge
import models.database.brand.BadgeTable
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.Future

class BadgeService extends HasDatabaseConfig[JdbcProfile]
  with BadgeTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._
  private val badges = TableQuery[Badges]

  /**
    * Deletes the given badge
    * @param brandId Brand identifier
    * @param id Badge identifier
    */
  def delete(brandId: Long, id: Long): Unit =
    db.run(badges.filter(_.id === id).filter(_.brandId === brandId).delete)

  /**
    * Returns badge for the given id if exists
    * @param id Identifier
    */
  def find(id: Long): Future[Option[Badge]] = db.run(badges.filter(_.id === id).result).map(_.headOption)

  /**
   * Returns a list of fees belonged to the given brand
   * @param brandId Brand id
   */
  def findByBrand(brandId: Long): Future[List[Badge]] =
    db.run(badges.filter(_.brandId === brandId).result).map(_.toList)

  /**
   * Inserts the given badge into database and returns the updated fee with ID
   * @param badge Fee
   */
  def insert(badge: Badge): Future[Badge] = {
    val query = badges returning badges.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += badge)
  }

  /**
    * Updates the given badge in the database
    * @param badge Badge
    */
  def update(badge: Badge): Future[Badge] = {
    val query = badges.filter(_.id === badge.id).map(_.forUpdate)
    db.run(query.update((badge.name, badge.file, badge.recordInfo.updatedBy))).map(_ => badge)
  }

}

object BadgeService {
  private val _instance = new BadgeService

  def get: BadgeService = _instance
}


