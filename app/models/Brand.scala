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

import models.database.{ BookingEntries, Licenses, Brands, ProductBrandAssociations }
import org.joda.time.{ LocalDate, DateTime }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.libs.Crypto
import scala.util.Random

/**
 * Brand classifications that a brand has one of.
 */
object BrandStatus extends Enumeration {
  val Accepted = Value("accepted")
  val HuddleGathering = Value("huddlegathering")
  val GatheringHuddle = Value("gatheringhuddle")
  val ProvisionallyAccepted = Value("provisionallyaccepted")
  val Experimental = Value("experimental")
}

/**
 * A person, such as the owner or employee of an organisation.
 */
case class Brand(id: Option[Long],
  code: String,
  name: String,
  coordinatorId: Long,
  description: Option[String],
  status: BrandStatus.Value,
  picture: Option[String],
  generateCert: Boolean = false,
  created: DateTime,
  createdBy: String,
  updated: DateTime,
  updatedBy: String) {

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
    this.copy(id = Some(id))
  }

  def delete(): Unit = Brand.delete(this.id.get)

  def update = DB.withSession { implicit session: Session ⇒
    val updateTuple = (code, name, coordinatorId, description, status, picture, updated, updatedBy)
    val updateQuery = Brands.filter(_.id === this.id).map(_.forUpdate)
    updateQuery.update(updateTuple)
    this
  }
}

case class BrandView(brand: Brand, coordinator: Person, licenses: Seq[Long])

object Brand {

  def cacheId(code: String): String = "brands." + code

  def generateImageName(filename: String): String = "brands/" + Crypto.sign("%s-%s".format(filename, Random.nextInt())) + ".png"

  /**
   * Returns true if and only if there is a brand with the given code.
   */
  def exists(code: String): Boolean = DB.withSession { implicit session: Session ⇒
    Query(Query(Brands).filter(_.code === code).exists).first
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
   * Return a list of facilitators for a given brand
   */
  def findFacilitators(code: String, coordinator: Person): List[Person] = DB.withSession { implicit session: Session ⇒
    val licensees = License.licensees(code, LocalDate.now())
    if (licensees.exists(_.id == coordinator.id))
      licensees
    else
      coordinator :: licensees
  }

  /** Finds a brand by ID **/
  def find(id: Long) = DB.withSession { implicit session: Session ⇒
    Query(Brands).filter(_.id === id).firstOption
  }

  /** Finds all brands belonging to one coordinator **/
  def findByCoordinator(coordinatorId: Long): List[Brand] = DB.withSession { implicit session: Session ⇒
    Query(Brands).filter(_.coordinatorId === coordinatorId).list
  }

  def findAll: List[BrandView] = DB.withSession { implicit session: Session ⇒
    val query = for {
      (brand, license) ← Brands leftJoin Licenses on (_.id === _.brandId)
      coordinator ← brand.coordinator
    } yield (brand, coordinator, license.id.?)

    // Transform results to BrandView
    // TODO Preserve query order, currently lost by the groupBy
    query.sortBy(_._1.name).list.groupBy {
      case (brand, coordinator, _) ⇒ brand -> coordinator
    }.mapValues(_.flatMap(_._3)).map {
      case ((brand, coordinator), licenseIDs) ⇒
        BrandView(brand, coordinator, licenseIDs)
    }.toList
  }

  def delete(id: Long): Unit = DB.withSession { implicit session: Session ⇒
    Brands.where(_.id === id).mutate(_.delete())
  }

}

