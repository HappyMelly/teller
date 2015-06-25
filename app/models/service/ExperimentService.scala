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

import models.Experiment
import models.database.Experiments
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

class ExperimentService extends Services {

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
  def delete(memberId: Long, id: Long): Unit = DB.withSession {
    implicit session: Session ⇒
      Query(Experiments).
        filter(_.id === id).
        filter(_.memberId === memberId).
        mutate(_.delete())
  }

  /**
   * Returns the requested experiments if it exists
   *
   * @param id Experiment id
   */
  def find(id: Long): Option[Experiment] = DB.withSession {
    implicit session ⇒
      Query(Experiments).filter(_.id === id).firstOption
  }

  /**
   * Returns list of experiments for the given member
   *
   * @param memberId Member identifier
   */
  def findByMember(memberId: Long): List[Experiment] = DB.withSession {
    implicit session ⇒
      Query(Experiments).filter(_.memberId === memberId).list
  }

  /**
   * Inserts the given experiment to database
   *
   * @param experiment Experiment
   */
  def insert(experiment: Experiment): Experiment = DB.withSession {
    implicit session ⇒
      val id = Experiments.forInsert.insert(experiment)
      experiment.copy(id = Some(id))
  }

  /**
   * Updates the given experiment in database
   *
   * @param experiment Experiment
   */
  def update(experiment: Experiment): Unit = DB.withSession {
    implicit session: Session ⇒
      Query(Experiments).
        filter(_.id === experiment.id.get).
        update(experiment)
  }
}

object ExperimentService {
  private val _instance = new ExperimentService

  def get: ExperimentService = _instance
}