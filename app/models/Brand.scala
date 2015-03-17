/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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

package models

import java.text.Collator
import java.util.Locale
import models.brand.CertificateTemplate
import models.database._
import models.service.SocialProfileService
import org.joda.time.{ LocalDate, DateTime }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.libs.Crypto
import scala.util.Random

/**
 * A person, such as the owner or employee of an organisation.
 */
case class Brand(id: Option[Long],
  code: String,
  uniqueName: String,
  name: String,
  coordinatorId: Long,
  description: Option[String],
  picture: Option[String],
  generateCert: Boolean = false,
  tagLine: Option[String],
  webSite: Option[String],
  blog: Option[String],
  created: DateTime,
  createdBy: String,
  updated: DateTime,
  updatedBy: String) extends ActivityRecorder {

  private var _socialProfile: Option[SocialProfile] = None

  /**
   * Returns identifier of the object
   */
  def identifier: Long = id.getOrElse(0)

  /**
   * Returns string identifier which can be understood by human
   *
   * For example, for object 'Person' human identifier is "[FirstName] [LastName]"
   */
  def humanIdentifier: String = name

  /**
   * Returns type of this object
   */
  def objectType: String = Activity.Type.Brand

  def socialProfile: SocialProfile = if (_socialProfile.isEmpty) {
    DB.withSession { implicit session: Session ⇒
      SocialProfileService.find(id.getOrElse(0), ProfileType.Brand)
    }
  } else {
    _socialProfile.get
  }

  def socialProfile_=(socialProfile: SocialProfile): Unit = {
    _socialProfile = Some(socialProfile)
  }

  /**
   * Returns true if this brand may be deleted.
   */
  lazy val deletable: Boolean = DB.withSession { implicit session: Session ⇒
    val hasLicences = id.exists { brandId ⇒
      val query = Query(Licenses).filter(l ⇒ l.brandId === brandId)
      Query(query.exists).first
    }
    val hasBookings = id.exists { brandId ⇒
      val query = Query(BookingEntries).filter(e ⇒ e.brandId === brandId)
      Query(query.exists).first
    }
    !hasLicences && !hasBookings && products.isEmpty
  }

  lazy val products: List[Product] = DB.withSession { implicit session: Session ⇒
    val query = for {
      relation ← ProductBrandAssociations if relation.brandId === this.id
      product ← relation.product
    } yield product
    query.sortBy(_.title.toLowerCase).list
  }

  lazy val certificates: List[CertificateTemplate] = CertificateTemplate.findByBrand(code)

  def insert: Brand = DB.withSession { implicit session: Session ⇒
    val id = Brands.forInsert.insert(this)
    SocialProfileService.insert(socialProfile.copy(objectId = id))
    this.copy(id = Some(id))
  }

  def delete(): Unit = {
    SocialProfileService.delete(this.id.get, ProfileType.Brand)
    Brand.delete(this.id.get)
  }
}

case class BrandView(brand: Brand, coordinator: Person, licenses: Seq[Long])

object Brand {

  def cacheId(code: String): String = "brands." + code

  def generateImageName(filename: String): String = "brands/" + Crypto.sign("%s-%s".format(filename, Random.nextInt())) + ".png"

  /**
   * Returns true if and only if there is a brand with the given code.
   */
  def exists(code: String, id: Option[Long] = None): Boolean = DB.withSession { implicit session: Session ⇒
    id.map { value ⇒
      Query(Query(Brands).filter(_.code === code).filter(_.id =!= value).exists).first
    }.getOrElse {
      Query(Query(Brands).filter(_.code === code).exists).first
    }
  }

  /**
   * Returns true if and only if there is a brand with the given unique name.
   *
   * @param uniqueName An unique name of the brand
   * @param id An unique number identifier of the brand
   * @return
   */
  def nameExists(uniqueName: String, id: Option[Long] = None): Boolean = DB.withSession { implicit session: Session ⇒
    id.map { value ⇒
      Query(Query(Brands).filter(_.uniqueName === uniqueName).filter(_.id =!= value).exists).first
    }.getOrElse {
      Query(Query(Brands).filter(_.uniqueName === uniqueName).exists).first
    }
  }

  /**
   * Returns true if and only if a user is allowed to manage this brand.
   * Notice: there's a difference between MANAGED BRAND and FACILITATED BRAND. A brand can be managed by
   *  any person with an Editor role, and a brand can be facilitated ONLY by its coordinator or active content
   *  license holders.
   */
  def canManage(code: String, user: UserAccount): Boolean = DB.withSession { implicit session: Session ⇒
    if (!exists(code))
      false
    else
      findByUser(user).exists(_.code == code)
  }

  /**
   * Returns a list of all brands for a specified user which he could facilitate
   * Notice: there's a difference between MANAGED BRAND and FACILITATED BRAND. A brand can be managed by
   *  any person with an Editor role, and a brand can be facilitated ONLY by its coordinator or active content
   *  license holders.
   */
  def findByUser(user: UserAccount): List[Brand] = DB.withSession { implicit session: Session ⇒
    if (user.editor)
      Query(Brands).list.sortBy(_.name)
    else {
      val facilitatedBrands = License.activeLicenses(user.personId).map(_.brand)
      findByCoordinator(user.personId).union(facilitatedBrands).distinct.sortBy(_.name)
    }
  }

  def find(code: String): Option[BrandView] = DB.withSession { implicit session: Session ⇒
    val query = for {
      (brand, license) ← Brands leftJoin Licenses on (_.id === _.brandId) if brand.code === code
      coordinator ← brand.coordinator
    } yield (brand, coordinator, license.id.?)

    query.list.groupBy { case (brand, coordinator, _) ⇒ brand -> coordinator }
      .mapValues(_.flatMap(_._3)).map {
        case ((brand, coordinator), licenses) ⇒
          BrandView(brand, coordinator, licenses)
      }.toList.headOption
  }

  /**
   * @param uniqueName Unique identifier of the brand
   * @return
   */
  def findByName(uniqueName: String): Option[BrandView] = DB.withSession { implicit session: Session ⇒
    val query = for {
      (brand, license) ← Brands leftJoin Licenses on (_.id === _.brandId) if brand.uniqueName === uniqueName
      coordinator ← brand.coordinator
    } yield (brand, coordinator, license.id.?)

    query.list.groupBy { case (brand, coordinator, _) ⇒ brand -> coordinator }
      .mapValues(_.flatMap(_._3)).map {
        case ((brand, coordinator), licenses) ⇒
          BrandView(brand, coordinator, licenses)
      }.toList.headOption

  }

  /**
   * Get a list of facilitators for a given brand
   *
   * @param code Brand string identifier
   * @param coordinator Brand coordinator
   * @return
   */
  def findFacilitators(code: String, coordinator: Person): List[Person] = DB.withSession { implicit session: Session ⇒
    val collator = Collator.getInstance(Locale.ENGLISH)
    val ord = new Ordering[String] { def compare(x: String, y: String) = collator.compare(x, y) }
    (coordinator :: License.licensees(code, LocalDate.now())).distinct.sortBy(_.fullName.toLowerCase)(ord)
  }

  /** Finds a brand by ID **/
  def find(id: Long) = DB.withSession { implicit session: Session ⇒
    Query(Brands).filter(_.id === id).firstOption
  }

  /** Finds all brands belonging to one coordinator **/
  def findByCoordinator(coordinatorId: Long): List[Brand] = DB.withSession { implicit session: Session ⇒
    Query(Brands).filter(_.coordinatorId === coordinatorId).list
  }

  def findAllWithCoordinator: List[BrandView] = DB.withSession { implicit session: Session ⇒
    val query = for {
      (brand, license) ← Brands leftJoin Licenses on (_.id === _.brandId)
      coordinator ← brand.coordinator
    } yield (brand, coordinator, license.id.?)

    // Transform results to BrandView
    query.list.groupBy {
      case (brand, coordinator, _) ⇒ brand -> coordinator
    }.mapValues(_.flatMap(_._3)).map {
      case ((brand, coordinator), licenseIDs) ⇒
        BrandView(brand, coordinator, licenseIDs)
    }.toList.sortBy(_.brand.name)
  }

  def delete(id: Long): Unit = DB.withSession { implicit session: Session ⇒
    //TODO delete social profile
    Brands.where(_.id === id).mutate(_.delete())
  }

  /**
   * Update brand
   * @param existingData Brand data before update
   * @param updatedData Brand data including updated fields from the from
   * @param picture New brand picture
   * @return
   */
  def update(existingData: Brand, updatedData: Brand, picture: Option[String]): Brand = DB.withSession { implicit session: Session ⇒
    session.withTransaction {
      import models.database.SocialProfiles._

      val u = updatedData.copy(id = existingData.id).copy(picture = picture)
      u.socialProfile_=(updatedData.socialProfile)

      val socialQuery = for {
        p ← SocialProfiles if p.objectId === u.id.get && p.objectType === u.socialProfile.objectType
      } yield p
      socialQuery
        .update(u.socialProfile.copy(objectId = u.id.get))

      if (existingData.code != u.code) {
        val eventQuery = for {
          event ← Events if event.brandCode === existingData.code
        } yield event.brandCode
        eventQuery.update(u.code)
      }

      val updateTuple = (u.code, u.uniqueName, u.name, u.coordinatorId, u.description, u.picture, u.tagLine,
        u.webSite, u.blog, u.updated, u.updatedBy)
      val updateQuery = Brands.filter(_.id === u.id).map(_.forUpdate)
      updateQuery.update(updateTuple)
      u
    }
  }
}

