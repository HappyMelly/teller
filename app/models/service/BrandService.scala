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

import models.brand.{ BrandNotifications, BrandCoordinator }
import models.database.brand.BrandCoordinators
import models.database.{ Brands, People, SocialProfiles }
import models.{ Brand, Person, ProfileType }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import scala.slick.lifted.Query

case class BrandWithCoordinators(brand: Brand,
  coordinators: List[(Person, BrandCoordinator)])

class BrandService extends Services {

  /**
   * Returns list of coordinators for the given brand
   * @param brandId Brand identifier
   */
  def coordinators(brandId: Long): List[(Person, BrandCoordinator)] = DB.withSession {
    implicit session ⇒
      val query = for {
        t ← BrandCoordinators if t.brandId === brandId
        p ← People if p.id === t.personId
      } yield (p, t)
      query.list()
  }

  /**
   * Deletes brand and all related brand data (which are allowed to be deleted
   *  automatically) from database
   * @param brand Brand to delete
   */
  def delete(brand: Brand): Unit = DB.withTransaction {
    implicit session: Session ⇒
      SocialProfileService.delete(brand.id.get, ProfileType.Brand)
      Query(Brands).filter(_.id === brand.id.get).mutate(_.delete())
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

  /**
   * Adds brand and all related records to database
   * @param brand Brand object
   * @return Updated brand object with ID
   */
  def insert(brand: Brand): Brand = DB.withTransaction {
    implicit session ⇒
      val id = Brands.forInsert.insert(brand)
      SocialProfileService._insert(brand.socialProfile.copy(objectId = id))
      val owner = BrandCoordinator(None, id, brand.ownerId,
        BrandNotifications(true, true, true))
      brandCoordinatorService._insert(owner)
      brand.copy(id = Some(id))
  }

  /**
   * Update brand
   * @param old Brand data before update
   * @param updated Brand data including updated fields from the from
   * @param picture New brand picture
   * @return Updated brand object
   */
  def update(old: Brand,
    updated: Brand,
    picture: Option[String]): Brand = DB.withTransaction {
    implicit session: Session ⇒
      import models.database.SocialProfiles._

      val u = updated.copy(id = old.id).copy(picture = picture)
      u.socialProfile_=(updated.socialProfile)

      val socialQuery = for {
        p ← SocialProfiles if p.objectId === u.id.get && p.objectType === u.socialProfile.objectType
      } yield p
      socialQuery
        .update(u.socialProfile.copy(objectId = u.id.get))

      val updateTuple = (u.code, u.uniqueName, u.name, u.ownerId,
        u.description, u.picture, u.tagLine, u.webSite, u.blog,
        u.evaluationHookUrl, u.updated, u.updatedBy)
      val updateQuery = Brands.filter(_.id === u.id).map(_.forUpdate)
      updateQuery.update(updateTuple)

      if (old.ownerId != updated.ownerId &&
        !brandService.isCoordinator(old.id.get, updated.ownerId)) {
        val owner = BrandCoordinator(None, updated.id.get, updated.ownerId,
          BrandNotifications(true, true, true))
        brandCoordinatorService.insert(owner)
      }
      u
  }
}

object BrandService {
  private val instance = new BrandService

  def get: BrandService = instance
}