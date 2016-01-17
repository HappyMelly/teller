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

package models.database

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.{Product, ProductCategory}
import org.joda.time.DateTime
import slick.driver.JdbcProfile

private[models] trait ProductTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * `Product` database table mapping.
    */
  class Products(tag: Tag) extends Table[Product](tag, "PRODUCT") {

    import Products.productCategoryTypeMapper

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def title = column[String]("TITLE")
    def subtitle = column[Option[String]]("SUBTITLE")
    def url = column[Option[String]]("URL")
    def description = column[Option[String]]("DESCRIPTION")
    def callToActionUrl = column[Option[String]]("CALL_TO_ACTION_URL")
    def callToActionText = column[Option[String]]("CALL_TO_ACTION_TEXT")
    def picture = column[Option[String]]("PICTURE")
    def category = column[Option[ProductCategory.Value]]("CATEGORY")
    def parentId = column[Option[Long]]("PARENT_ID")
    def active = column[Boolean]("ACTIVE")
    def created = column[DateTime]("CREATED")
    def createdBy = column[String]("CREATED_BY")
    def updated = column[DateTime]("UPDATED")
    def updatedBy = column[String]("UPDATED_BY")

    def * = (id.?, title, subtitle, url, description, callToActionUrl,
      callToActionText, picture, category, parentId, active, created,
      createdBy, updated, updatedBy) <>((Product.apply _).tupled, Product.unapply)

    def forUpdate = (title, subtitle, url, description, callToActionUrl,
      callToActionText, picture, category, parentId, updated, updatedBy)
  }

  object Products {

    implicit val productCategoryTypeMapper = MappedColumnType.base[ProductCategory.Value, String](
      { category ⇒ category.toString }, { category ⇒ ProductCategory.withName(category) })

  }
}