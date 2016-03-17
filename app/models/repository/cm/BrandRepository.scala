/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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
package models.repository.cm

import com.github.tototoshi.slick.MySQLJodaSupport._
import controllers.BrandProfileView
import models._
import models.cm.brand._
import models.database._
import models.database.brand._
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class BrandWithCoordinators(brand: Brand,
  coordinators: List[(Person, BrandCoordinator)])

class BrandRepository(app: Application, repos: models.repository.Repositories) extends HasDatabaseConfig[JdbcProfile]
  with BrandTable
  with BrandCoordinatorTable
  with BrandLinkTable
  with BrandSettingsTable
  with BrandTestimonialTable
  with LicenseTable
  with PersonTable
  with ProductBrandAssociationTable
  with SocialProfileTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._

  private val brands = TableQuery[Brands]
  private val settings = TableQuery[BrandSettings]

  /**
   * Activates the given brand
   *
   * @param id Brand id
   */
  def activate(id: Long): Unit = switchState(id, true)

  /**
   * Returns list of coordinators for the given brand
    *
    * @param brandId Brand identifier
   */
  def coordinators(brandId: Long): Future[List[(Person, BrandCoordinator)]] = {
    val query = for {
      t ← TableQuery[BrandCoordinators] if t.brandId === brandId
      p ← TableQuery[People] if p.id === t.personId
    } yield (p, t)
    db.run(query.result).map(_.toList)
  }

  /**
   * Deactivates the given brand
   *
   * @param id Brand id
   */
  def deactivate(id: Long): Unit = switchState(id, false)

  def deletable(id: Long): Future[Boolean] = {
    val actions = for {
      l <- TableQuery[Licenses].filter(_.brandId === id).exists.result
      p <- TableQuery[ProductBrandAssociations].filter(_.brandId === id).exists.result
    } yield (l, p)
    db.run(actions) map { case (hasLicenses, hasProducts) =>
      !hasLicenses && !hasProducts
    }
  }

  /**
   * Deletes brand and all related brand data (which are allowed to be deleted
   *  automatically) from database
    *
    * @param brand Brand to delete
   */
  def delete(brand: Brand): Unit = {
    repos.socialProfile.delete(brand.id.get, ProfileType.Brand)
    db.run(brands.filter(_.id === brand.id.get).delete)
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
  def deleteLink(brandId: Long, id: Long): Future[Int] = {
    val action = TableQuery[BrandLinks].filter(_.id === id).filter(_.brandId === brandId).delete
    db.run(action)
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
  def deleteTestimonial(brandId: Long, id: Long): Future[Int] = {
    val action = TableQuery[BrandTestimonials].filter(_.id === id).filter(_.brandId === brandId).delete
    db.run(action)
  }

  /**
    * Returns true if and only if there is a brand with the given code.
    */
  def exists(code: String, id: Option[Long] = None): Future[Boolean] = id map { value ⇒
    db.run(brands.filter(_.code === code).filter(_.id =!= value).exists.result)
  } getOrElse {
    db.run(brands.filter(_.code === code).exists.result)
  }

  /**
    * Returns true if and only if there is a brand with the given unique name.
    *
    * @param uniqueName An unique name of the brand
    * @param id An unique number identifier of the brand
    * @return
    */
  def nameExists(uniqueName: String, id: Option[Long] = None): Future[Boolean] = id map { value ⇒
    db.run(brands.filter(_.uniqueName === uniqueName).filter(_.id =!= value).exists.result)
  } getOrElse {
    db.run(brands.filter(_.uniqueName === uniqueName).exists.result)
  }

  /**
   * Returns brand if it exists, otherwise - None
    *
    * @param id Brand identifier
   */
  def find(id: Long): Future[Option[Brand]] = db.run(brands.filter(_.id === id).result).map(_.headOption)

  /**
   * Returns brand if it exists, otherwise - None
    *
    * @param code Brand code
   */
  def find(code: String): Future[Option[Brand]] = db.run(brands.filter(_.code === code).result).map(_.headOption)

  /**
    * @param uniqueName Unique identifier of the brand
    */
  def findByName(uniqueName: String): Future[Option[Brand]] =
    db.run(brands.filter(_.uniqueName === uniqueName).result).map(_.headOption)


  /**
    * Returns list of brands with settings for the given identifiers
    *
    * @param ids Brand identifiers
    */
  def find(ids: List[Long]): Future[List[BrandWithSettings]] = {
    val query = for {
      brand <- brands if brand.id inSet ids
      settings  <- settings if settings.brandId === brand.id
    } yield (brand, settings)
    db.run(query.result).map(_.toList.map(view => BrandWithSettings(view._1, view._2)))
  }

  /**
   * Returns a list of all brands
   */
  def findAll: Future[List[Brand]] = db.run(brands.sortBy(_.name.toLowerCase).result).map(_.toList)


  def findAllWithCoordinator: Future[List[BrandView]] = {
    val query = for {
      (brand, license) ← brands joinLeft TableQuery[Licenses] on (_.id === _.brandId)
      coordinator ← brand.coordinator
    } yield (brand, coordinator, license)

    db.run(query.result).map { value =>
      value.toList.groupBy {
        case (brand, coordinator, _) ⇒ brand -> coordinator
      }.mapValues(_.flatMap(_._3.toList.map(_.identifier))).map {
        case ((brand, coordinator), licenseIDs) ⇒
          BrandView(brand, coordinator, licenseIDs)
      }.toList.sortBy(_.brand.name)
    }
  }

  /**
    * Returns list of all brands with settings
    */
  def findAllWithSettings: Future[List[BrandWithSettings]] = {
    val query = for {
      brand <- brands
      settings <- settings if settings.brandId === brand.id
    } yield (brand, settings)
    db.run(query.result)map(_.toList.map(view => BrandWithSettings(view._1, view._2)))
  }

  /**
   * Returns list of brands belonging to one coordinator
    *
    * @param coordinatorId Coordinator identifier
   */
  def findByCoordinator(coordinatorId: Long): Future[List[BrandWithSettings]] = {
    val query = for {
      coordinator ← TableQuery[BrandCoordinators] if coordinator.personId === coordinatorId
      brand ← brands if brand.id === coordinator.brandId
      settings <- settings if settings.brandId === brand.id
    } yield (brand, settings)
    db.run(query.result).map(_.toList.map(view => BrandWithSettings(view._1, view._2)))
  }

  /**
    * Returns list of brands with settings belonging to the given license holder
    *
    * @param licenseeId License holder identifier
    */
  def findByLicense(licenseeId: Long): Future[List[BrandWithSettings]] = {
    val query = for {
      license <- TableQuery[Licenses] if license.licenseeId === licenseeId
      brand <- brands if brand.id === license.brandId
      settings <- settings if settings.brandId === brand.id
    } yield (brand, settings)
    db.run(query.result).map(_.toList.map(view => BrandWithSettings(view._1, view._2)))
  }

  /**
    * Returns a list of all brands for a specified user which he could facilitate
    * Notice: there's a difference between MANAGED BRAND and FACILITATED BRAND. A brand can be managed by
    *  any person with an Editor role, and a brand can be facilitated ONLY by its coordinator or active content
    *  license holders.
    *
    *  @deprecated
    */
  def findByUser(user: UserAccount): Future[List[Brand]] = {
    (for {
      l <- repos.cm.license.activeLicenses(user.personId)
      b <- repos.cm.brand.findByCoordinator(user.personId)
    } yield (l, b)) map { case (licenses, brands) =>
      licenses.map(_.brand).union(brands.map(_.brand)).distinct.sortBy(_.name)
    }
  }

  /**
   * Returns testimonial if it exists
   *
   * @param testimonialId Testimonial identification
   */
  def findTestimonial(testimonialId: Long): Future[Option[BrandTestimonial]] =
    db.run(TableQuery[BrandTestimonials].filter(_.id === testimonialId).result).map(_.headOption)

  /**
   * Returns brand with its coordinators if exists; otherwise - None
    *
    * @param id Brand id
   */
  def findWithCoordinators(id: Long): Future[Option[BrandWithCoordinators]] =
    (for {
      brand <- find(id)
      coordinators <- coordinators(id)
    } yield (brand, coordinators)) map {
      case (None, _) => None
      case (Some(brand), coordinators) => Some(BrandWithCoordinators(brand, coordinators))
    }

  /**
    * Returns brand with settings if exists
    *
    * @param id Brand identifier
    */
  def findWithSettings(id: Long): Future[Option[BrandWithSettings]] = {
    val query = for {
      brand <- brands if brand.id === id
      settings <- settings if settings.brandId === id
    } yield (brand, settings)
    db.run(query.result).map(_.headOption.map(view => BrandWithSettings(view._1, view._2)))
  }

  /**
    * Returns the given brand
    *
    * @param id Brand identifier
    */
  def get(id: Long): Future[Brand] = db.run(brands.filter(_.id === id).result).map(_.head)

  /**
   * Returns true if the given person is a coordinator of the given brand
    *
    * @param brandId Brand id
   * @param personId Person id
   */
  def isCoordinator(brandId: Long, personId: Long): Future[Boolean] = {
    val query = TableQuery[BrandCoordinators].filter(_.brandId === brandId).filter(_.personId === personId).exists
    db.run(query.result)
  }

  /**
   * Adds brand and all related records to database
    *
    * @param view Brand object
   * @return Updated brand object with ID
   */
  def insert(view: BrandProfileView): Future[Brand] = {
    val query = brands returning brands.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += view.brand).map { b =>
      repos.socialProfile.insert(view.profile.copy(objectId = b.identifier))
      val owner = BrandCoordinator(None, b.identifier, view.brand.ownerId, BrandNotifications(true, true, true))
      repos.cm.rep.brand.coordinator.save(owner)
      db.run(settings += Settings(b.identifier))
      b
    }
  }

  /**
   * Inserts the given link brand to database
   *
   * @param link Brand link
   */
  def insertLink(link: BrandLink): Future[BrandLink] = {
    val links = TableQuery[BrandLinks]
    val query = links returning links.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += link)
  }

  /**
   * Inserts the given testimonial brand to database
   *
   * @param testimonial Brand testimonial
   */
  def insertTestimonial(testimonial: BrandTestimonial): Future[BrandTestimonial] = {
    val testimonials = TableQuery[BrandTestimonials]
    val query = testimonials returning testimonials.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += testimonial)
  }

  /**
   * Return list of links for the given brand
   *
   * @param brandId Brand identifier
   */
  def links(brandId: Long): Future[List[BrandLink]] =
    db.run(TableQuery[BrandLinks].filter(_.brandId === brandId).result).map(_.toList)

  /**
   * Return list of testimonials for the given brand
   *
   * @param brandId Brand identifier
   */
  def testimonials(brandId: Long): Future[List[BrandTestimonial]] =
    db.run(TableQuery[BrandTestimonials].filter(_.brandId === brandId).result).map(_.toList)

  /**
   * Update brand
    *
    * @param old Brand data before update
   * @param view Brand data including updated fields from the from
   * @param picture New brand picture
   * @return Updated brand object
   */
  def update(old: Brand, view: BrandProfileView, picture: Option[String]): Brand = {

    val u = view.brand.copy(id = old.id, picture = picture)

    repos.socialProfile.update(view.profile.copy(objectId = old.identifier), ProfileType.Brand)
    val updateTuple = (u.code, u.uniqueName, u.name, u.ownerId,
      u.description, u.picture, u.tagLine, u.webSite, u.blog, u.contactEmail,
      u.evaluationUrl, u.evaluationHookUrl, u.recordInfo.updated, u.recordInfo.updatedBy)
    db.run(brands.filter(_.id === u.id).map(_.forUpdate).update(updateTuple))

    updateOwnerRecord(old.identifier, view.brand.ownerId, old.ownerId)
    u
  }

  /**
    * Updates the Picture field for the given brand
    *
    * @param brandId Brand identifier
    * @param picture Picture url
    * @return
    */
  def updatePicture(brandId: Long, picture: Option[String]): Future[Int] =
    db.run(brands.filter(_.id === brandId).map(_.picture).update(picture))

  /**
    * Update brand settings in database
    *
    * @param value Brand settings
    */
  def updateSettings(value: Settings): Future[Int] =
    db.run(settings.filter(_.brandId === value.brandId).update(value))

  /**
   * Updates brand testimonial in database
   *
   * @param testimonial Testimonital to update
   */
  def updateTestimonial(testimonial: BrandTestimonial): Future[Int] = {
    val action = TableQuery[BrandTestimonials].
        filter(_.id === testimonial.id.get).
        filter(_.brandId === testimonial.brand).
        update(testimonial)
    db.run(action)
  }

  /**
    * Adds new brand coordinator record for a new owner
 *
    * @param brandId Brand identifier
    * @param newOwner New owner identifier
    * @param oldOwner Old owner identifier
    */
  protected def updateOwnerRecord(brandId: Long, newOwner: Long, oldOwner: Long): Future[Unit] = {
    (for {
      value <- repos.cm.brand.isCoordinator(brandId, newOwner) if value
    } yield ()) map { _ =>
      if (oldOwner != newOwner) {
        val owner = BrandCoordinator(None, brandId, newOwner, BrandNotifications(true, true, true))
        repos.cm.rep.brand.coordinator.save(owner)
      }
    }
  }

  /**
   * Deactivates/actives the given brand
   *
   * @param id Brand id
   * @param active If true, the brand is activated
   */
  private def switchState(id: Long, active: Boolean): Unit =
    db.run(brands.filter(_.id === id).map(_.active).update(active))

}