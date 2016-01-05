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

import models.{ Facilitator, FacilitatorLanguage }
import models.database.{ FacilitatorLanguages, Facilitators }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

/**
 * Contains all database-related operations
 */
class FacilitatorService {

  private val facilitators = TableQuery[Facilitators]

  /**
   * Inserts the given facilitator to database
   * @param facilitator Facilitator
   * @return Returns the updated facilitator with a valid id
   */
  def insert(facilitator: Facilitator): Facilitator = DB.withSession { implicit session ⇒
    val id = (facilitators returning facilitators.map(_.id)) += facilitator
    facilitator.copy(id = id)
  }

  /**
   * Returns facilitator if it exists, otherwise - None
   * @param brandId Brand id
   * @param personId Person id
   */
  def find(brandId: Long, personId: Long): Option[Facilitator] = DB.withSession {
    implicit session ⇒
      facilitators.filter(_.personId === personId).filter(_.brandId === brandId).firstOption
  }

  /**
   * Returns list of all facilitators
   */
  def findAll: List[Facilitator] = DB.withSession { implicit session =>
    facilitators.list
  }

  /**
   * Returns list of facilitator records for the given person
   * @param personId Person id
   */
  def findByPerson(personId: Long): List[Facilitator] = DB.withSession {
    implicit session ⇒
      facilitators.filter(_.personId === personId).list
  }

  /**
   * Returns list of facilitator records for the given brand
   * @param brandId Brand id
   * @return
   */
  def findByBrand(brandId: Long): List[Facilitator] = DB.withSession {
    implicit session ⇒
      facilitators.filter(_.brandId === brandId).list
  }

  /**
   * Updates the given facilitator in database
   * @param facilitator Facilitator
   * @return Retunrs the given facilitator
   */
  def update(facilitator: Facilitator): Facilitator = DB.withSession {
    implicit session ⇒
      facilitators.
        filter(_.personId === facilitator.personId).
        filter(_.brandId === facilitator.brandId).
        map(_.forUpdate).
        update((facilitator.publicRating, facilitator.privateRating,
          facilitator.publicMedian, facilitator.privateMedian,
          facilitator.publicNps, facilitator.privateNps,
          facilitator.numberOfPublicEvaluations,
          facilitator.numberOfPrivateEvaluations,
          if (facilitator.badges.isEmpty) None else Option[String](facilitator.badges.mkString(","))))
      facilitator
  }

  /**
   * Updates the experience of the given facilitator in database
   * @param facilitator Facilitator
   */
  def updateExperience(facilitator: Facilitator): Int = DB.withSession {
    implicit session =>
      facilitators.
        filter(_.personId === facilitator.personId).
        filter(_.brandId === facilitator.brandId).
        map(record => (record.numberOfEvents, record.yearsOfExperience)).
        update((facilitator.numberOfEvents, facilitator.yearsOfExperience))
  }

  /**
   * Returns list of languages the given facilitator talks
   *
   * @param personId Person identifier
   */
  def languages(personId: Long): List[FacilitatorLanguage] = DB.withSession {
    implicit session ⇒
      TableQuery[FacilitatorLanguages].filter(_.personId === personId).list
  }
}

object FacilitatorService {
  private val instance = new FacilitatorService

  def get: FacilitatorService = instance
}
