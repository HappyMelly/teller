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

package models.service

import models.Facilitator
import models.database.Facilitators
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

/**
 * Contains all database-related operations
 */
class FacilitatorService {

  /**
   * Inserts the given facilitator to database
   * @param facilitator Facilitator
   * @return Returns the updated facilitator with a valid id
   */
  def insert(facilitator: Facilitator): Facilitator = DB.withSession {
    implicit session ⇒
      val id = Facilitators.forInsert.insert(facilitator)
      facilitator.copy(id = Some(id))
  }

  /**
   * Returns facilitator if it exists, otherwise - None
   * @param brandId Brand id
   * @param personId Person id
   */
  def find(brandId: Long, personId: Long): Option[Facilitator] = DB.withSession {
    implicit session ⇒
      Query(Facilitators).
        filter(_.personId === personId).
        filter(_.brandId === brandId).firstOption
  }
}

object FacilitatorService {
  private val instance = new FacilitatorService

  def get: FacilitatorService = instance
}
