/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

/**
 * A person, such as the owner or employee of an organisation.
 */
case class Brand(id: Option[Long], code: String, name: String, coordinatorId: Long,
  created: DateTime, createdBy: String, updated: DateTime, updatedBy: String) {

  /**
   * Returns true if this brand may be deleted.
   */
  lazy val deletable: Boolean = DB.withSession { implicit session: Session ⇒
    val hasLicences = id.map { brandId ⇒
      val query = Query(Licenses).filter(l ⇒ l.brandId === brandId)
      Query(query.exists).first
    }.getOrElse(false)
    val hasBookings = id.map { brandId ⇒
      val query = Query(BookingEntries).filter(e ⇒ e.brandId === brandId)
      Query(query.exists).first
    }.getOrElse(false)
    !hasLicences && !hasBookings && products.isEmpty
  }

  lazy val products: List[Product] = DB.withSession { implicit session: Session ⇒
    val query = for {
      relation ← ProductBrandAssociations if relation.brandId === this.id
      product ← relation.product
    } yield product
    query.sortBy(_.title.toLowerCase).list
  }

  def insert: Brand = DB.withSession { implicit session: Session ⇒
    val id = Brands.forInsert.insert(this)
    this.copy(id = Some(id))
  }

  def delete(): Unit = Brand.delete(this.id.get)

  def update = DB.withSession { implicit session: Session ⇒
    val updateTuple = (code, name, coordinatorId, updated, updatedBy)
    val updateQuery = Brands.filter(_.id === this.id).map(_.forUpdate)
    updateQuery.update(updateTuple)
    this
  }
}

case class BrandView(brand: Brand, coordinator: Person, licenses: Seq[Long])

object Brand {

  /**
   * Returns true if and only if there is a brand with the given code.
   */
  def exists(code: String): Boolean = DB.withSession { implicit session: Session ⇒
    Query(Query(Brands).filter(_.code === code).exists).first
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

  /** Finds a brand by ID **/
  def find(id: Long) = DB.withSession { implicit session: Session ⇒
    Query(Brands).filter(_.id === id).firstOption
  }

  /** Finds all brands belonging to one coordinator **/
  def findByCoordinator(coordinatorId: Long): List[Brand] = DB.withSession { implicit session: Session ⇒
    Query(Brands).filter(_.coordinatorId == coordinatorId).list
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

