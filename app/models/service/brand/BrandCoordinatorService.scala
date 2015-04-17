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

import models.brand.BrandCoordinator
import models.database.brand.BrandCoordinators
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import scala.slick.session.Session

/**
 * Contains a set of functions for managing team members in database
 */
class BrandCoordinatorService {

  /**
   * Adds new team member to the given brand
   * @param coordinator Brand object
   */
  def insert(coordinator: BrandCoordinator) = DB.withSession {
    implicit session: Session ⇒
      _insert(coordinator)
  }

  /**
   * Removes the given person from the given brand
   * @param brandId Brand identifier
   * @param personId Person identifier
   */
  def delete(brandId: Long, personId: Long) = DB.withSession {
    implicit session: Session ⇒
      Query(BrandCoordinators).
        filter(_.brandId === brandId).
        filter(_.personId === personId).mutate(_.delete())
  }

  /**
   * Adds new coordinator to the given brand
   *
   * Requires session object so it can be used inside withTransaction
   * @param coordinator Brand object
   * @param session Session object
   */
  def _insert(coordinator: BrandCoordinator)(implicit session: Session) =
    BrandCoordinators.forInsert.insert(coordinator)
}

object BrandCoordinatorService {
  private val instance = new BrandCoordinatorService

  def get: BrandCoordinatorService = instance
}