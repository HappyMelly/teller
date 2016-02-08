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

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.database.{ProductBrandAssociationTable, ProductTable}
import models.{Brand, Product, ProductView}
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProductService(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with ProductTable
  with ProductBrandAssociationTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._

  private val products = TableQuery[Products]
  private val associations = TableQuery[ProductBrandAssociations]

  /**
   * Activates the given product
   *
   * @param id Product id
   */
  def activate(id: Long): Unit = switchState(id, true)

  /**
   * Assign this product to a brand
   */
  def addBrand(productId: Long, brandId: Long): Unit =
    db.run(TableQuery[ProductBrandAssociations] += (None, productId, brandId))

  /**
   * Returns list of related brand for the given product, sorted by name
   *
   * @param id Product id
   */
  def brands(id: Long): Future[List[Brand]] = {
    val query = for {
      relation ← associations if relation.productId === id
      brand ← relation.brand
    } yield brand
    db.run(query.sortBy(_.name.toLowerCase).result).map(_.toList)
  }

  /**
   * Deactivates the given product
   *
   * @param id Product id
   */
  def deactivate(id: Long): Unit = switchState(id, false)

  /**
   * @param productId
   * @param brandId
   */
  def deleteBrand(productId: Long, brandId: Long): Unit =
    db.run(associations.filter(x ⇒ x.productId === productId && x.brandId === brandId).delete)

  /**
   * Deletes a product with the given id
   *
   * @param id Product id
   */
  def delete(id: Long): Unit = db.run(products.filter(_.id === id).delete)

  /**
   * Returns a product with the given id if exists
   *
   * @param id Product id
   */
  def find(id: Long) = db.run(products.filter(_.id === id).result).map(_.headOption)

  /** Returns list with active products */
  def findActive: Future[List[Product]] =
    db.run(products.filter(_.active === true).sortBy(_.title.toLowerCase).result).map(_.toList)

  /** Returns list with all products */
  def findAll: Future[List[Product]] =
    db.run(products.sortBy(_.title.toLowerCase).result).map(_.toList)

  /**
   * Returns sorted list of products for the given brand
   *
   * @param brandId Brand identifier
   */
  def findByBrand(brandId: Long): Future[List[Product]] = {
    val query = for {
      relation ← associations if relation.brandId === brandId
      product ← relation.product
    } yield product
    db.run(query.sortBy(_.title.toLowerCase).result).map(_.toList)
  }

  /**
   * Returns list of derivative products for the given product
   *
   * @param parentId Product id
   */
  def findDerivatives(parentId: Long): Future[List[Product]] =
    db.run(products.filter(_.parentId === parentId).result).map(_.toList)

  /**
    * Return number of product per brand
    */
  def findNumberPerBrand: Future[Map[Long, Int]] =
    db.run(associations.result).map(_.toList.groupBy(_._3).map(value => value._1 -> value._2.length))

  /**
   * Inserts the given product to database
   *
   * @param product Product to insert
   * @return The given product with updated id
   */
  def insert(product: Product): Future[Product] = {
    val query = products returning products.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += product)
  }

  /**
   * Returns true if the given title is taken by other product except the given one
   * @param title The title of interest
   * @param id Product id
   */
  def isTitleTaken(title: String, id: Long): Future[Boolean] =
    db.run(products.filter(_.title === title).filter(_.id =!= id).exists.result)

  /**
   * Returns true if a product with the given title exists
   * @param title Product title
   */
  def titleExists(title: String): Future[Boolean] =
    db.run(products.filter(_.title === title).exists.result)

  /**
   * Updates the given product to database
   *
   * @param product Product to update
   * @return The given product
   */
  def update(product: Product): Future[Product] = {
    import Products.productCategoryTypeMapper

    val updateTuple = (product.title, product.subtitle, product.url,
      product.description, product.callToActionUrl, product.callToActionText,
      product.picture, product.category, product.parentId,
      product.updated, product.updatedBy)
    db.run(products.filter(_.id === product.id).map(_.forUpdate).update(updateTuple)).map(_ => product)
  }

  object collection {

    /**
      * Fill products with brands (using only one query to database)
      * @param products List of products
      */
    def brands(products: List[Product]): Future[List[ProductView]] = {
      val ids = products.map(_.id.get).distinct

      val query = for {
        relation ← associations if relation.productId inSet ids
        brand ← relation.brand
      } yield (relation.productId, brand)
      val result = db.run(query.result).map(_.toList.groupBy(_._1).map(f ⇒ (f._1, f._2.map(_._2))))
      result map { brands =>
        products map (p ⇒ ProductView(p, brands.getOrElse(p.id.get, List()), List()))
      }
    }
  }

  /**
   * Deactivates/actives the given product
   *
   * @param id Product id
   * @param active If true, the product is activated
   */
  private def switchState(id: Long, active: Boolean): Unit =
    db.run(products.filter(_.id === id).map(_.active).update(active))
}