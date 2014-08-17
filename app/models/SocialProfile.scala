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

import models.database.SocialProfiles
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

/**
 * A type of social profile
 */
object ProfileType extends Enumeration {
  type ProfileType = Value
  val Person = Value("0")
  val Brand = Value("1")
  val Organisation = Value("2")

  implicit val profileTypeMapper = MappedTypeMapper.base[ProfileType.Value, Int](
    { objectType ⇒ objectType.id }, { id ⇒ ProfileType(id) })
}

case class SocialProfile(
  objectId: Long = 0,
  objectType: ProfileType.Value = ProfileType.Person,
  email: String,
  twitterHandle: Option[String] = None,
  facebookUrl: Option[String] = None,
  linkedInUrl: Option[String] = None,
  googlePlusUrl: Option[String] = None,
  skype: Option[String] = None,
  phone: Option[String] = None) {

  def defined: Boolean = twitterHandle.isDefined || facebookUrl.isDefined ||
    googlePlusUrl.isDefined || linkedInUrl.isDefined
}

object SocialProfile {

  def find(objectId: Long, objectType: ProfileType.Value): SocialProfile = DB.withSession { implicit session: Session ⇒
    Query(SocialProfiles).filter(_.objectId === objectId).filter(_.objectType === objectType).first
  }

  def insert(socialProfile: SocialProfile): SocialProfile = DB.withSession { implicit session: Session ⇒
    SocialProfiles.insert(socialProfile)
    socialProfile
  }

  def update(socialProfile: SocialProfile, objectType: ProfileType.Value): Unit = DB.withSession { implicit session: Session ⇒
    SocialProfiles.filter(_.objectId === socialProfile.objectId).filter(_.objectType === objectType).update(socialProfile)
  }
}

