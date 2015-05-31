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

import models.database.ProfileCompletions
import models.ProfileCompletion
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

class ProfileCompletionService {

  /**
   * Returns a profile completion object for the given person/org if exists
   *
   * @param objectId Person or organisation identifier
   * @param org If true, objectId is an organisation identifier
   */
  def find(objectId: Long, org: Boolean = false): Option[ProfileCompletion] =
    DB.withSession { implicit session: Session ⇒
      Query(ProfileCompletions).
        filter(_.objectId === objectId).
        filter(_.org === org).firstOption
    }

  def update(completion: ProfileCompletion): ProfileCompletion = {
    DB.withSession { implicit session: Session ⇒
      Query(ProfileCompletions).
        filter(_.objectId === completion.objectId).
        filter(_.org === completion.org).
        map(_.forUpdate).
        update(completion.stepsInJson)

      completion
    }
  }

  def insert(completion: ProfileCompletion): ProfileCompletion = {
    DB.withSession { implicit session: Session ⇒
      val id = ProfileCompletions.forInsert.insert(completion)
      completion.copy(id = Some(id))
    }
  }
}

object ProfileCompletionService {
  private val _instance = new ProfileCompletionService

  def get: ProfileCompletionService = _instance
}