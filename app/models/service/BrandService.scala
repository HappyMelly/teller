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

import models.database.brand.BrandCoordinators
import models.{ Person, Brand }
import models.database.{ People, Brands }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import scala.slick.lifted.Query

case class BrandWithCoordinators(brand: Brand, coordinators: List[Person])

class BrandService {

  /**
   * Returns list of team members for the given brand
   * @param brandId Brand identifier
   */
  def coordinators(brandId: Long): List[Person] = DB.withSession { implicit session ⇒
    val query = for {
      t ← BrandCoordinators if t.brandId === brandId
      p ← People if p.id === t.personId
    } yield p
    query.list()
  }

  /**
   * Returns brand if it exists, otherwise - None
   * @param id Brand identifier
   */
  def find(id: Long) = DB.withSession { implicit session: Session ⇒
    Query(Brands).filter(_.id === id).firstOption
  }

  /**
   * Returns brand if it exists, otherwise - None
   * @param code Brand code
   */
  def find(code: String): Option[Brand] = DB.withSession { implicit session ⇒
    Query(Brands).filter(_.code === code).firstOption
  }

  /**
   * Returns a list of all brands
   */
  def findAll: List[Brand] = DB.withSession { implicit session: Session ⇒
    Query(Brands).sortBy(_.name.toLowerCase).list
  }

  /**
   * Returns list of brands belonging to one coordinator
   * @param coordinatorId Coordinator identifier
   */
  def findByCoordinator(coordinatorId: Long): List[Brand] = DB.withSession {
    implicit session: Session ⇒
      val query = for {
        x ← BrandCoordinators if x.personId === coordinatorId
        y ← Brands if y.id === x.brandId
      } yield y
      query.list
  }

  /**
   * Returns brand with its coordinators if exists; otherwise - None
   * @param id Brand id
   */
  def findWithCoordinators(id: Long): Option[BrandWithCoordinators] =
    find(id) flatMap { x ⇒ Some(BrandWithCoordinators(x, coordinators(id))) }

  /**
   * Returns true if the given person is a coordinator of the given brand
   * @param brandId Brand id
   * @param personId Person id
   */
  def isCoordinator(brandId: Long, personId: Long): Boolean = DB.withSession {
    implicit session ⇒
      Query(Query(BrandCoordinators)
        .filter(_.brandId === brandId)
        .filter(_.personId === personId)
        .exists).first()
  }
}

object BrandService {
  private val instance = new BrandService

  def get: BrandService = instance
}