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

package models.database

import com.github.tototoshi.slick.JodaSupport._
import models.{ Address, Person }
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

  def addressId = column[Long]("ADDRESS_ID")

  def bio = column[Option[String]]("BIO", O.DBType("TEXT"))
  def interests = column[Option[String]]("INTERESTS", O.DBType("TEXT"))

  def twitterHandle = column[Option[String]]("TWITTER_HANDLE")
  def facebookUrl = column[Option[String]]("FACEBOOK_URL")
  def linkedInUrl = column[Option[String]]("LINKEDIN_URL")
  def googlePlusUrl = column[Option[String]]("GOOGLE_PLUS_URL")
  def boardMember = column[Boolean]("BOARD_MEMBER")
  def stakeholder = column[Boolean]("STAKEHOLDER")

  def webSite = column[Option[String]]("WEB_SITE")
  def blog = column[Option[String]]("BLOG")

  def active = column[Boolean]("ACTIVE")
  def created = column[DateTime]("CREATED")
  def createdBy = column[String]("CREATED_BY")
  def updated = column[DateTime]("UPDATED")
  def updatedBy = column[String]("UPDATED_BY")

  def address = foreignKey("ADDRESS_FK", addressId, Addresses)(_.id)

  // Note that this projection does not include the address, which must be joined in queries.
  def * = id.? ~ firstName ~ lastName ~ emailAddress ~ addressId ~ bio ~ interests ~ twitterHandle ~ facebookUrl ~
    linkedInUrl ~ googlePlusUrl ~ boardMember ~ stakeholder ~ webSite ~ blog ~ active ~ created ~ createdBy ~ updated ~ updatedBy <> (
      { p ⇒ Person(p._1, p._2, p._3, p._4, Address.find(p._5), p._6, p._7, p._8, p._9, p._10, p._11, p._12, p._13, p._14, p._15, p._16, p._17, p._18, p._19, p._20) },
      { (p: Person) ⇒
        Some((p.id, p.firstName, p.lastName, p.emailAddress, p.address.id.get, p.bio, p.interests, p.twitterHandle, p.facebookUrl,
          p.linkedInUrl, p.googlePlusUrl, p.boardMember, p.stakeholder, p.webSite, p.blog, p.active, p.created, p.createdBy, p.updated, p.updatedBy))
      })

  def forInsert = * returning id

  def forUpdate = firstName ~ lastName ~ emailAddress ~ bio ~ interests ~ twitterHandle ~ facebookUrl ~ linkedInUrl ~
    googlePlusUrl ~ boardMember ~ stakeholder ~ webSite ~ blog ~ updated ~ updatedBy
}