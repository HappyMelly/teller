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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models.database.brand

import models.DateStamp
import models.brand.Badge
import org.joda.time.DateTime
import slick.driver.JdbcProfile

private[models] trait BadgeTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * Connects Badge object with its database representation
    */
  class Badges(tag: Tag) extends Table[Badge](tag, "BRAND_BADGE") {

    def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
    def brandId = column[Long]("BRAND_ID")
    def name = column[String]("NAME")
    def file = column[String]("FILE")
    def created = column[DateTime]("CREATED")
    def createdBy = column[String]("CREATED_BY")
    def updated = column[DateTime]("UPDATED")
    def updatedBy = column[String]("UPDATED_BY")

    type BadgeFields = (Option[Long], Long, String, String, DateTime, String, DateTime, String)

    def * = (id, brandId, name, file, created, createdBy, updated, updatedBy) <>(
      (b: BadgeFields) => Badge(b._1, b._2, b._3, b._4, DateStamp(b._5, b._6, b._7, b._8)),
      (b: Badge) => Some((b.id, b.brandId, b.name, b.file, b.recordInfo.created, b.recordInfo.createdBy,
        b.recordInfo.updated, b.recordInfo.updatedBy)))

    def forUpdate = (name, file, updatedBy)
  }

}