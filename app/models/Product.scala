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

import models.database.{ ProductBrandAssociations, Products }
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import play.api.libs.Crypto
import scala.util.Random

/**
 * Category classifications that a product has zero or one of.
 */
object ProductCategory extends Enumeration {
  val Book = Value("book")
  val Game = Value("game")
  val Apps = Value("apps")
  val Conference = Value("conference")
  val Course = Value("course")
}

/**
 * A thing such as a book, a game or a piece of software
 */
case class Product(
  id: Option[Long],
  title: String,
  subtitle: Option[String],
  url: Option[String],
  description: Option[String],
  callToActionUrl: Option[String],
  callToActionText: Option[String],
  picture: Option[String],
  category: Option[ProductCategory.Value],
  parentId: Option[Long],
  created: DateTime,
  createdBy: String,
  updated: DateTime,
  updatedBy: String) {

  private var _brands: Option[List[Brand]] = None

  def brands: List[Brand] = if (_brands.isEmpty) {
    DB.withSession { implicit session: Session ⇒
      val query = for {
        relation ← ProductBrandAssociations if relation.productId === this.id
        brand ← relation.brand
      } yield brand
      brands_=(query.sortBy(_.name.toLowerCase).list)
      _brands.get
    }
  } else {
    _brands.get
  }

  def brands_=(brands: List[Brand]): Unit = {
    _brands = Some(brands)
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
    val updateTuple = (title, subtitle, url, description, callToActionUrl, callToActionText, picture, category, parentId, updated, updatedBy)
    val updateQuery = Products.filter(_.id === this.id).map(_.forUpdate)
    updateQuery.update(updateTuple)
    this
  }
}

object Product {

  def cacheId(id: Long): String = "products." + id.toString

  def generateImageName(filename: String): String = "products/" + Crypto.sign("%s-%s".format(filename, Random.nextInt())) + ".png"

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

object ProductsCollection {

  /**
   * Fill products with brands (using only one query to database)
   * @param products List of products
   * @return
   */
  def brands(products: List[Product]): Unit = DB.withSession { implicit session: Session ⇒
    val ids = products.map(_.id.get).distinct.toList
    val query = for {
      relation ← ProductBrandAssociations if relation.productId inSet ids
      brand ← relation.brand
    } yield (relation.productId, brand)
    val brands = query.list.groupBy(_._1).map(f ⇒ (f._1, f._2.map(_._2)))
    products.foreach(e ⇒ e.brands_=(brands.getOrElse(e.id.get, List())))
  }
}
