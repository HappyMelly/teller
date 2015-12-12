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

import models.brand._
import models.database.brand._
import models.database.{ Brands, People, SocialProfiles }
import models.{ Brand, Person, ProfileType }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

case class BrandWithCoordinators(brand: Brand,
  coordinators: List[(Person, BrandCoordinator)])

class BrandService extends Services {

  private val brands = TableQuery[Brands]

  /**
   * Activates the given brand
   *
   * @param id Brand id
   */
  def activate(id: Long): Unit = switchState(id, true)

  /**
   * Returns list of coordinators for the given brand
   * @param brandId Brand identifier
   */
  def coordinators(brandId: Long): List[(Person, BrandCoordinator)] = DB.withSession {
    implicit session ⇒
      val query = for {
        t ← TableQuery[BrandCoordinators] if t.brandId === brandId
        p ← TableQuery[People] if p.id === t.personId
      } yield (p, t)
      query.list
  }

  /**
   * Deactivates the given brand
   *
   * @param id Brand id
   */
  def deactivate(id: Long): Unit = switchState(id, false)

  /**
   * Deletes brand and all related brand data (which are allowed to be deleted
   *  automatically) from database
   * @param brand Brand to delete
   */
  def delete(brand: Brand): Unit = DB.withTransaction {
    implicit session: Session ⇒
      socialProfileService.delete(brand.id.get, ProfileType.Brand)
      brands.filter(_.id === brand.id.get).delete
  }

  /**
   * Deletes brand link from database
   *
   * Brand identifier is for security reasons. If a user passes security
   * check for the brand, the user cannot delete links which aren't belonged to
   * another brand.
   *
   * @param brandId Brand identifier
   * @param id Link identifier
   */
  def deleteLink(brandId: Long, id: Long): Unit = DB.withSession {
    implicit session ⇒
      TableQuery[BrandLinks].
        filter(_.id === id).
        filter(_.brandId === brandId).delete
  }

  /**
   * Deletes brand testimonial from database
   *
   * Brand identifier is for security reasons. If a user passes security
   * check for the brand, the user cannot delete testimonials which aren't
   * belonged to another brand.
   *
   * @param brandId Brand identifier
   * @param id Testimonial identifier
   */
  def deleteTestimonial(brandId: Long, id: Long): Unit = DB.withSession {
    implicit session ⇒
      TableQuery[BrandTestimonials].
        filter(_.id === id).
        filter(_.brandId === brandId).delete
  }

  /**
   * Returns brand if it exists, otherwise - None
   * @param id Brand identifier
   */
  def find(id: Long) = DB.withSession { implicit session ⇒
    brands.filter(_.id === id).firstOption
  }

  /**
   * Returns brand if it exists, otherwise - None
   * @param code Brand code
   */
  def find(code: String): Option[Brand] = DB.withSession { implicit session ⇒
    brands.filter(_.code === code).firstOption
  }

  /**
   * Returns a list of all brands
   */
  def findAll: List[Brand] = DB.withSession { implicit session ⇒
    brands.sortBy(_.name.toLowerCase).list
  }

  /**
   * Returns list of brands belonging to one coordinator
   * @param coordinatorId Coordinator identifier
   */
  def findByCoordinator(coordinatorId: Long): List[Brand] = DB.withSession {
    implicit session ⇒
      val query = for {
        x ← TableQuery[BrandCoordinators] if x.personId === coordinatorId
        y ← brands if y.id === x.brandId
      } yield y
      query.list
  }

  /**
   * Returns testimonial if it exists
   *
   * @param testimonialId Testimonial identification
   */
  def findTestimonial(testimonialId: Long): Option[BrandTestimonial] = DB.withSession {
    implicit session ⇒
      TableQuery[BrandTestimonials].filter(_.id === testimonialId).firstOption
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
      TableQuery[BrandCoordinators]
        .filter(_.brandId === brandId)
        .filter(_.personId === personId)
        .exists.run
  }

  /**
   * Adds brand and all related records to database
   * @param brand Brand object
   * @return Updated brand object with ID
   */
  def insert(brand: Brand): Brand = DB.withTransaction {
    implicit session ⇒
      val id = (brands returning brands.map(_.id)) += brand
      socialProfileService._insert(brand.socialProfile.copy(objectId = id))
      val owner = BrandCoordinator(None, id, brand.ownerId,
        BrandNotifications(true, true, true))
      brandCoordinatorService._insert(owner)
      brand.copy(id = Some(id))
  }

  /**
   * Inserts the given link brand to database
   *
   * @param link Brand link
   */
  def insertLink(link: BrandLink): BrandLink = DB.withSession {
    implicit session ⇒
      val links = TableQuery[BrandLinks]
      val id = (links returning links.map(_.id)) += link
      link.copy(id = Some(id))
  }

  /**
   * Inserts the given testimonial brand to database
   *
   * @param testimonial Brand testimonial
   */
  def insertTestimonial(testimonial: BrandTestimonial): BrandTestimonial =
    DB.withSession {
      implicit session ⇒
        val testimonials = TableQuery[BrandTestimonials]
        val id = (testimonials returning testimonials.map(_.id)) += testimonial
        testimonial.copy(id = Some(id))
    }

  /**
   * Return list of links for the given brand
   *
   * @param brandId Brand identifier
   */
  def links(brandId: Long): List[BrandLink] = DB.withSession {
    implicit session ⇒
      TableQuery[BrandLinks].filter(_.brandId === brandId).list
  }

  /**
   * Return list of testimonials for the given brand
   *
   * @param brandId Brand identifier
   */
  def testimonials(brandId: Long): List[BrandTestimonial] = DB.withSession {
    implicit session ⇒
      TableQuery[BrandTestimonials].filter(_.brandId === brandId).list
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
      import models.database.SocialProfilesStatic._

      val u = updated.copy(id = old.id).copy(picture = picture)
      u.socialProfile_=(updated.socialProfile)

      val socialQuery = for {
        p ← TableQuery[SocialProfiles] if p.objectId === u.id.get && p.objectType === u.socialProfile.objectType
      } yield p
      socialQuery
        .update(u.socialProfile.copy(objectId = u.id.get))

      val updateTuple = (u.code, u.uniqueName, u.name, u.ownerId,
        u.description, u.picture, u.tagLine, u.webSite, u.blog, u.contactEmail,
        u.evaluationUrl, u.evaluationHookUrl, u.recordInfo.updated, u.recordInfo.updatedBy)
      brands.filter(_.id === u.id).map(_.forUpdate).update(updateTuple)

      if (old.ownerId != updated.ownerId &&
        !brandService.isCoordinator(old.id.get, updated.ownerId)) {
        val owner = BrandCoordinator(None, updated.id.get, updated.ownerId,
          BrandNotifications(true, true, true))
        brandCoordinatorService.insert(owner)
      }
      u
  }

  /**
   * Updates brand testimonial in database
   *
   * @param testimonial Testimonital to update
   */
  def updateTestimonial(testimonial: BrandTestimonial): Unit = DB.withSession {
    implicit session ⇒
      TableQuery[BrandTestimonials].
        filter(_.id === testimonial.id.get).
        filter(_.brandId === testimonial.brand).
        update(testimonial)
  }

  /**
   * Deactivates/actives the given brand
   *
   * @param id Brand id
   * @param active If true, the brand is activated
   */
  private def switchState(id: Long, active: Boolean): Unit = DB.withSession {
    implicit session ⇒
      brands.filter(_.id === id).map(_.active).update(active)
  }

}

object BrandService {
  private val instance = new BrandService

  def get: BrandService = instance
}