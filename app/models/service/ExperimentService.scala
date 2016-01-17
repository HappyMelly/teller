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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing
 * Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models.service

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.Experiment
import models.database.ExperimentTable
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ExperimentService extends HasDatabaseConfig[JdbcProfile]
  with ExperimentTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._
  private val experiments = TableQuery[Experiments]

  /**
   * Deletes an experiment from database
   *
   * Member identifier is for security reasons. If a user passes security
   * check for the memberId, the user cannot delete an experiment which are
   * belonged to another memberId.
   *
   * @param memberId Member identifier
   * @param id Experiment identifier
   */
  def delete(memberId: Long, id: Long): Future[Int] =
    db.run(experiments.filter(_.id === id).filter(_.memberId === memberId).delete)

  /**
   * Returns the requested experiments if it exists
   *
   * @param id Experiment id
   */
  def find(id: Long): Future[Option[Experiment]] =
    db.run(experiments.filter(_.id === id).result).map(_.headOption)

  /**
   * Returns all experiments
   */
  def findAll(): Future[List[Experiment]] = db.run(experiments.result).map(_.toList)

  /**
   * Returns list of experiments for the given member
   *
   * @param memberId Member identifier
   */
  def findByMember(memberId: Long): Future[List[Experiment]] =
    db.run(experiments.filter(_.memberId === memberId).result).map(_.toList)

  /**
   * Inserts the given experiment to database
   *
   * @param experiment Experiment
   */
  def insert(experiment: Experiment): Future[Experiment] = {
    val query = experiments returning experiments.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += experiment)
  }

  /**
   * Updates the given experiment in database
   *
   * @param experiment Experiment
   */
  def update(experiment: Experiment): Unit = {
    val fields = (experiment.name, experiment.description,
      experiment.picture, experiment.url, experiment.recordInfo.updated,
      experiment.recordInfo.updatedBy)
    db.run(experiments.filter(_.id === experiment.id.get).map(_.forUpdate).update(fields))
  }
}

object ExperimentService {
  private val _instance = new ExperimentService

  def get: ExperimentService = _instance
}