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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models.service

import models.ProfileStrength
import models.database.ProfileStrengthTable
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProfileStrengthService extends HasDatabaseConfig[JdbcProfile]
  with ProfileStrengthTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._
  private val profiles = TableQuery[ProfileStrengths]


  /**
   * Returns a profile strength object for the given person/org if exists
   *
   * @param objectId Person or organisation identifier
   * @param org If true, objectId is an organisation identifier
   */
  def find(objectId: Long, org: Boolean = false): Future[Option[ProfileStrength]] = {
    val query = profiles.filter(_.objectId === objectId).filter(_.org === org)
    db.run(query.result).map(_.headOption)
  }

  /**
   * Returns list of profile strength objects for the given persons/orgs
   *
   * @param objectIds List of person or organisation identifiers
   * @param org If true, objectIds are organisation identifiers
   */
  def find(objectIds: List[Long], org: Boolean): Future[List[ProfileStrength]] = {
    val query = profiles.filter(_.org === org).filter(_.objectId inSet objectIds)
    db.run(query.result).map(_.toList)
  }

  def update(strength: ProfileStrength): Future[ProfileStrength] = {
    import ProfileStrengths.jsArrayMapper

    val query = profiles.
      filter(_.objectId === strength.objectId).
      filter(_.org === strength.org).
      map(_.steps).
      update(strength.stepsInJson)
    db.run(query).map(_ => strength)
  }

  def insert(strength: ProfileStrength): Future[ProfileStrength] = {
    val query = profiles returning profiles.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += strength)
  }
}

object ProfileStrengthService {
  private val _instance = new ProfileStrengthService

  def get: ProfileStrengthService = _instance
}