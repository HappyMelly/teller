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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models.database

import com.github.tototoshi.slick.MySQLJodaSupport._
import models._
import org.joda.time.{DateTime, LocalDate}
import slick.driver.JdbcProfile

private[models] trait PersonTable extends AddressTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * `Organisation` database table mapping.
    */
  class People(tag: Tag) extends Table[Person](tag, "PERSON") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("FIRST_NAME")
    def lastName = column[String]("LAST_NAME")
    def email = column[String]("EMAIL")
    def birthday = column[Option[LocalDate]]("BIRTHDAY")
    def photo = column[Option[String]]("PHOTO")
    def signature = column[Boolean]("SIGNATURE")
    def addressId = column[Long]("ADDRESS_ID")
    def bio = column[Option[String]]("BIO")
    def interests = column[Option[String]]("INTERESTS")
    def webSite = column[Option[String]]("WEB_SITE")
    def blog = column[Option[String]]("BLOG")
    def customerId = column[Option[String]]("CUSTOMER_ID")
    def virtual = column[Boolean]("VIRTUAL")
    def active = column[Boolean]("ACTIVE")
    def created = column[DateTime]("CREATED")
    def createdBy = column[String]("CREATED_BY")
    def updated = column[DateTime]("UPDATED")
    def updatedBy = column[String]("UPDATED_BY")
    def address = foreignKey("ADDRESS_FK", addressId, TableQuery[Addresses])(_.id)

    type PeopleFields = (Option[Long], String, String, String,
      Option[LocalDate], Option[String], Boolean, Long, Option[String], Option[String], Option[String],
      Option[String], Option[String], Boolean, Boolean,
      DateTime, String, DateTime, String)

    def * = (id.?, firstName, lastName, email, birthday, photo, signature, addressId,
      bio, interests, webSite, blog, customerId, virtual, active,
      created, createdBy, updated, updatedBy) <>(
      (p: PeopleFields) ⇒
        Person(p._1, p._2, p._3, p._4, p._5, Photo.parse(p._6), p._7, p._8,
          p._9, p._10, p._11, p._12, p._13, p._14, p._15,
          DateStamp(p._16, p._17, p._18, p._19)),
      (p: Person) ⇒
        Some((p.id, p.firstName, p.lastName, p.email, p.birthday, p.photo.url,
          p.signature, p.addressId, p.bio, p.interests, p.webSite, p.blog,
          p.customerId, p.virtual, p.active, p.dateStamp.created,
          p.dateStamp.createdBy, p.dateStamp.updated, p.dateStamp.updatedBy)))

    def forUpdate = (firstName, lastName, email, birthday, photo, signature, bio, interests,
      webSite, blog, customerId, virtual, active, updated, updatedBy)
  }

}