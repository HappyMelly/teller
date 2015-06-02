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
import models.service._
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
  ownerId: Long,
  description: Option[String],
  picture: Option[String],
  generateCert: Boolean = false,
  tagLine: Option[String],
  webSite: Option[String],
  blog: Option[String],
  evaluationHookUrl: Option[String] = None,
  created: DateTime,
  createdBy: String,
  updated: DateTime,
  updatedBy: String) extends ActivityRecorder with Services {

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
      SocialProfileService.get.find(id.getOrElse(0), ProfileType.Brand)
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
    !hasLicences && !hasBookings && ProductService.get.findByBrand(this.id.get).isEmpty
  }

  lazy val products: List[Product] = DB.withSession { implicit session: Session ⇒
    val query = for {
      relation ← ProductBrandAssociations if relation.brandId === this.id
      product ← relation.product
    } yield product
    query.sortBy(_.title.toLowerCase).list
  }

  /**
   * Adds this brand to database and returns an updated object with ID
   */
  def insert(): Brand = brandService.insert(this)

  def delete(): Unit = brandService.delete(this)
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
  def canManage(brandId: Long, user: UserAccount): Boolean = DB.withSession {
    implicit session: Session ⇒
      findByUser(user).exists(_.id == Some(brandId))
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
      val facilitatedBrands = LicenseService.get.activeLicenses(user.personId).map(_.brand)
      BrandService.get.findByCoordinator(user.personId).union(facilitatedBrands).distinct.sortBy(_.name)
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
   * Returns list of facilitators for the given brand
   *
   * @param brandId Brand id
   */
  def findFacilitators(brandId: Long): List[Person] = DB.withSession {
    implicit session: Session ⇒
      val collator = Collator.getInstance(Locale.ENGLISH)
      val ord = new Ordering[String] {
        def compare(x: String, y: String) = collator.compare(x, y)
      }
      LicenseService.get.licensees(brandId, LocalDate.now()).sortBy(_.fullName.toLowerCase)(ord)
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

}

