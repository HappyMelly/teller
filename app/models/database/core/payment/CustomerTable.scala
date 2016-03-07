/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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
package models.database.core.payment

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.DateStamp
import models.core.payment.{Customer, CustomerType}
import org.joda.time.DateTime
import slick.driver.JdbcProfile

trait CustomerTable {

  protected val driver: JdbcProfile
  import driver.api._

  class Customers(tag: Tag) extends Table[Customer](tag, "CUSTOMER") {

    implicit val customerTypeMapper = MappedColumnType.base[CustomerType.Value, Int](
      { objectType ⇒ objectType.id }, { id ⇒ CustomerType(id) })

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def remoteId = column[String]("REMOTE_ID")
    def objectId = column[Long]("OBJECT_ID")
    def objectType = column[CustomerType.Value]("OBJECT_TYPE")
    def created = column[DateTime]("CREATED")
    def createdBy = column[String]("CREATED_BY")
    def updated = column[DateTime]("UPDATED")
    def updatedBy = column[String]("UPDATED_BY")

    type CustomerFields = (Option[Long], String, Long, CustomerType.Value, DateTime, String, DateTime, String)

    def * = (id.?, remoteId, objectId, objectType, created, createdBy, updated, updatedBy) <> (
      (c: CustomerFields) => Customer(c._1, c._2, c._3, c._4, DateStamp(c._5, c._6, c._7, c._8)),
      (c: Customer) => Some((c.id, c.remoteId, c.objectId, c.objectType,
        c.recordInfo.created, c.recordInfo.createdBy, c.recordInfo.updated, c.recordInfo.updatedBy))
      )
  }

  object Customers {
    implicit val customerTypeMapper = MappedColumnType.base[CustomerType.Value, Int](
      { objectType ⇒ objectType.id }, { id ⇒ CustomerType(id) })
  }
}
