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

import com.github.tototoshi.slick.JodaSupport._
import models.{ Photo, Address, Person, SocialProfile, DateStamp }
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

/**
 * `Organisation` database table mapping.
 */
private[models] object People extends Table[Person]("PERSON") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def firstName = column[String]("FIRST_NAME")
  def lastName = column[String]("LAST_NAME")
  def emailAddress = column[String]("EMAIL_ADDRESS")
  def photo = column[Option[String]]("PHOTO")
  def signature = column[Boolean]("SIGNATURE")

  def addressId = column[Long]("ADDRESS_ID")

  def bio = column[Option[String]]("BIO", O.DBType("TEXT"))
  def interests = column[Option[String]]("INTERESTS", O.DBType("TEXT"))

  def boardMember = column[Boolean]("BOARD_MEMBER")
  def stakeholder = column[Boolean]("STAKEHOLDER")

  def webSite = column[Option[String]]("WEB_SITE")
  def blog = column[Option[String]]("BLOG")

  def virtual = column[Boolean]("VIRTUAL")
  def active = column[Boolean]("ACTIVE")
  def created = column[DateTime]("CREATED")
  def createdBy = column[String]("CREATED_BY")
  def updated = column[DateTime]("UPDATED")
  def updatedBy = column[String]("UPDATED_BY")

  def address = foreignKey("ADDRESS_FK", addressId, Addresses)(_.id)
  def socialProfile = foreignKey("SOCIAL_PROFILE_FK", id, SocialProfiles)(_.personId)

  // Note that this projection does not include the address and social profile, which must be joined in queries.
  def * = id.? ~ firstName ~ lastName ~ emailAddress ~ photo ~ signature ~ addressId ~ bio ~ interests ~
    boardMember ~ stakeholder ~ webSite ~ blog ~
    virtual ~ active ~ created ~ createdBy ~ updated ~ updatedBy <> (
      { p ⇒
        Person(p._1, p._2, p._3, p._4, Photo.parse(p._5), p._6, Address.find(p._7), p._8, p._9,
          SocialProfile.find(p._1.getOrElse(0)), p._10, p._11, p._12, p._13, p._14, p._15,
          DateStamp(p._16, p._17, p._18, p._19))
      },
      { (p: Person) ⇒
        Some((p.id, p.firstName, p.lastName, p.emailAddress, p.photo.url, p.signature, p.address.id.get, p.bio,
          p.interests, p.boardMember, p.stakeholder, p.webSite, p.blog, p.virtual, p.active,
          p.dateStamp.created, p.dateStamp.createdBy, p.dateStamp.updated, p.dateStamp.updatedBy))
      })

  def forInsert = * returning id

  def forUpdate = firstName ~ lastName ~ emailAddress ~ photo ~ signature ~ bio ~ interests ~
    boardMember ~ stakeholder ~ webSite ~ blog ~ virtual ~ updated ~ updatedBy
}
