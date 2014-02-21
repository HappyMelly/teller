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

import models.database.{ ProductBrandAssociations, Products }
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

/**
 * Category classifications that a product has zero or one of.
 */
object ProductCategory extends Enumeration {
  val Book = Value("book")
  val Game = Value("game")
  val Software = Value("software")
}

/**
 * A thing such as a book, a game or a piece of software
 */
case class Product(
  id: Option[Long],
  title: String,
  subtitle: Option[String],
  url: Option[String],
  category: Option[ProductCategory.Value],
  parentId: Option[Long],
  created: DateTime,
  createdBy: String,
  updated: DateTime,
  updatedBy: String) {

  /**
   * Get all brands associated with this product
   */
  def brands: List[Brand] = DB.withSession { implicit session: Session ⇒
    val query = for {
      relation ← ProductBrandAssociations if relation.productId === this.id
      brand ← relation.brand
    } yield brand
    query.sortBy(_.name.toLowerCase).list
  }

  def contributors: List[ContributorView] = Contribution.contributors(this.id.get)

  /**
   * Assign this product to a brand
   */
  def addBrand(brandId: Long): Unit = DB.withSession { implicit session: Session ⇒
    ProductBrandAssociations.forInsert.insert(this.id.get, brandId)
  }

  /**
   * Unassign this product to a brand
   */
  def deleteBrand(brandId: Long): Unit = DB.withSession { implicit session: Session ⇒
    ProductBrandAssociations.filter(relation ⇒ relation.productId === id && relation.brandId === brandId).mutate(_.delete)
  }

  def insert: Product = DB.withSession { implicit session: Session ⇒
    val id = Products.forInsert.insert(this)
    this.copy(id = Some(id))
  }

  def delete(): Unit = Product.delete(this.id.get)

  def update: Product = DB.withSession { implicit session: Session ⇒
    val updateTuple = (title, subtitle, url, category, parentId, updated, updatedBy)
    val updateQuery = Products.filter(_.id === this.id).map(_.forUpdate)
    updateQuery.update(updateTuple)
    this
  }
}

object Product {

  def exists(title: String): Boolean = DB.withSession { implicit session: Session ⇒
    Query(Query(Products).filter(_.title === title).exists).first
  }
  /**
   * Returns true if and only if there is a product with the given title.
   */
  def exists(title: String, id: Long): Boolean = DB.withSession { implicit session: Session ⇒
    Query(Query(Products).filter(_.title === title).filter(_.id =!= id).exists).first
  }

  /** Finds a product by ID **/
  def find(id: Long) = DB.withSession { implicit session: Session ⇒
    Query(Products).filter(_.id === id).firstOption
  }

  def findDerivatives(parentId: Long): List[Product] = DB.withSession { implicit session: Session ⇒
    Query(Products).filter(_.parentId === parentId).list
  }

  def findAll: List[Product] = DB.withSession { implicit session: Session ⇒
    Query(Products).sortBy(_.title.toLowerCase).list
  }

  def delete(id: Long): Unit = DB.withSession { implicit session: Session ⇒
    Products.where(_.id === id).mutate(_.delete())
  }

}
