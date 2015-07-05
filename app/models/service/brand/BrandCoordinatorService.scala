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

/**
 * Contains a set of functions for managing team members in database
 */
class BrandCoordinatorService {

  private val coordinators = TableQuery[BrandCoordinators]

  /**
   * Removes the given person from the given brand
   * @param brandId Brand identifier
   * @param personId Person identifier
   */
  def delete(brandId: Long, personId: Long) = DB.withSession { implicit session ⇒
    coordinators.
      filter(_.brandId === brandId).
      filter(_.personId === personId).delete
  }

  /**
   * Adds new team member to the given brand
   * @param coordinator Brand object
   */
  def insert(coordinator: BrandCoordinator) = DB.withSession {
    implicit session ⇒ _insert(coordinator)
  }

  def update(brandId: Long, personId: Long, notification: String, value: Boolean) =
    DB.withSession { implicit session ⇒
      val query = coordinators.
        filter(_.brandId === brandId).
        filter(_.personId === personId)
      notification match {
        case "event" ⇒ query.map(_.event).update(value)
        case "evaluation" ⇒ query.map(_.evaluation).update(value)
        case _ ⇒ query.map(_.certificate).update(value)
      }
    }

  /**
   * Adds new coordinator to the given brand
   *
   * Requires session object so it can be used inside withTransaction
   * @param coordinator Brand object
   * @param session Session object
   */
  def _insert(coordinator: BrandCoordinator)(implicit session: Session) =
    coordinators += coordinator
}

object BrandCoordinatorService {
  private val instance = new BrandCoordinatorService

  def get: BrandCoordinatorService = instance
}