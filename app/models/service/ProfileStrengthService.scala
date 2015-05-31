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

import models.database.ProfileStrengths
import models.ProfileStrength
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

class ProfileStrengthService {

  /**
   * Returns a profile strength object for the given person/org if exists
   *
   * @param objectId Person or organisation identifier
   * @param org If true, objectId is an organisation identifier
   */
  def find(objectId: Long, org: Boolean = false): Option[ProfileStrength] =
    DB.withSession { implicit session: Session ⇒
      Query(ProfileStrengths).
        filter(_.objectId === objectId).
        filter(_.org === org).firstOption
    }

  def update(strength: ProfileStrength): ProfileStrength = {
    DB.withSession { implicit session: Session ⇒
      Query(ProfileStrengths).
        filter(_.objectId === strength.objectId).
        filter(_.org === strength.org).
        map(_.forUpdate).
        update(strength.stepsInJson)

      strength
    }
  }

  def insert(strength: ProfileStrength): ProfileStrength = {
    DB.withSession { implicit session: Session ⇒
      val id = ProfileStrengths.forInsert.insert(strength)
      strength.copy(id = Some(id))
    }
  }
}

object ProfileStrengthService {
  private val _instance = new ProfileStrengthService

  def get: ProfileStrengthService = _instance
}