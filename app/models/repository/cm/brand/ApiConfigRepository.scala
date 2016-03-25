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

import models.cm.brand.ApiConfig
import models.database.brand.ApiConfigTable
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApiConfigRepository(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with ApiConfigTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val configs = TableQuery[ApiConfigs]

  /**
    * Activated API for the given brand
    * @param brandId Brand identifier
    * @param active Activate/deactivate flag
    */
  def activate(brandId: Long, active: Boolean): Future[Int] =
    db.run(configs.filter(_.brandId === brandId).map(_.active).update(active))

  /**
   * Returns api config if exists, None otherwise
   * @param token Token
   */
  def find(token: String): Future[Option[ApiConfig]] =
    db.run(configs.filter(_.token === token).result).map(_.headOption)

  /**
    * Returns api config if exists, None otherwise
    * @param brandId Token identifier
    */
  def findByBrand(brandId: Long): Future[Option[ApiConfig]] =
    db.run(configs.filter(_.brandId === brandId).result).map(_.headOption)

  /**
    * Inserts new config to the database
    * @param config Config entity
    */
  def insert(config: ApiConfig): Future[ApiConfig] = {
    val query = configs returning configs.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += config)
  }

  /**
    * Update an existing API config
    * @param config Config entity
    */
  def update(config: ApiConfig): Future[ApiConfig] = {
    val updateTuple = (config.event, config.facilitator, config.generalEvaluation, config.specificEventEvaluation)
    val query = configs.filter(_.id === config.id).map(_.forUpdate).update(updateTuple)
    db.run(query).map(_ => config)
  }
}
