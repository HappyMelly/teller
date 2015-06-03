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

import models.{ Product, Brand }
import models.database.{ ProductBrandAssociations, Products }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

class ProductService {

  /**
   * Activates the given product
   *
   * @param id Product id
   */
  def activate(id: Long): Unit = switchState(id, true)

  /**
   * Returns list of related brand for the given product, sorted by name
   *
   * @param id Product id
   */
  def brands(id: Long): List[Brand] = DB.withSession { implicit session ⇒
    val query = for {
      relation ← ProductBrandAssociations if relation.productId === id
      brand ← relation.brand
    } yield brand
    query.sortBy(_.name.toLowerCase).list
  }

  /**
   * Deactivates the given product
   *
   * @param id Product id
   */
  def deactivate(id: Long): Unit = switchState(id, false)

  /**
   * Deletes a product with the given id
   *
   * @param id Product id
   */
  def delete(id: Long): Unit = DB.withSession { implicit session: Session ⇒
    Products.where(_.id === id).mutate(_.delete())
  }

  /**
   * Returns a product with the given id if exists
   *
   * @param id Product id
   */
  def find(id: Long) = DB.withSession { implicit session: Session ⇒
    Query(Products).filter(_.id === id).firstOption
  }

  /** Returns list with active products */
  def findActive: List[Product] = DB.withSession { implicit session: Session ⇒
    Query(Products).filter(_.active === true).sortBy(_.title.toLowerCase).list
  }

  /** Returns list with all products */
  def findAll: List[Product] = DB.withSession { implicit session: Session ⇒
    Query(Products).sortBy(_.title.toLowerCase).list
  }

  /**
   * Returns sorted list of products for the given brand
   *
   * @param brandId Brand identifier
   */
  def findByBrand(brandId: Long): List[Product] = DB.withSession {
    implicit session: Session ⇒
      val query = for {
        relation ← ProductBrandAssociations if relation.brandId === brandId
        product ← relation.product
      } yield product
      query.sortBy(_.title.toLowerCase).list
  }

  /**
   * Returns list of derivative products for the given product
   *
   * @param parentId Product id
   */
  def findDerivatives(parentId: Long): List[Product] = DB.withSession {
    implicit session: Session ⇒
      Query(Products).filter(_.parentId === parentId).list
  }

  /**
   * Returns true if the given title is taken by other product except the given one
   *
   * @param title The title of interest
   * @param id Product id
   */
  def isTitleTaken(title: String, id: Long): Boolean = DB.withSession {
    implicit session: Session ⇒
      Query(Query(Products).filter(_.title === title).filter(_.id =!= id).exists).first
  }

  /**
   * Returns true if a product with the given title exists
   *
   * @param title Product title
   */
  def titleExists(title: String): Boolean = DB.withSession {
    implicit session: Session ⇒
      Query(Query(Products).filter(_.title === title).exists).first
  }

  /**
   * Deactivates/actives the given product
   *
   * @param id Product id
   * @param active If true, the product is activated
   */
  private def switchState(id: Long, active: Boolean): Unit = DB.withSession {
    implicit session: Session ⇒
      val query = for {
        product ← Products if product.id === id
      } yield product.active
      query.update(active)
  }
}

object ProductService {
  private val instance = new ProductService

  def get: ProductService = instance
}