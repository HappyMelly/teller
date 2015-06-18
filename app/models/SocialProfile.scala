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

import scala.language.implicitConversions

/**
 * A type of social profile
 */
object ProfileType extends Enumeration {
  type ProfileType = Value
  val Person = Value("0")
  val Brand = Value("1")
  val Organisation = Value("2")
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
  phone: Option[String] = None,
  contactForm: Option[String] = None) {

  def defined: Boolean = twitterHandle.isDefined || facebookUrl.isDefined ||
    googlePlusUrl.isDefined || linkedInUrl.isDefined

  /**
   * Returns true if at least 2 social networks are not empy
   */
  def complete: Boolean = {
    implicit def bool2int(b: Boolean) = if (b) 1 else 0

    val counter = twitterHandle.isDefined + facebookUrl.isDefined +
      googlePlusUrl.isDefined + linkedInUrl.isDefined
    (counter >= 2)
  }

  def forBrand: SocialProfile = this.copy(objectType = ProfileType.Brand)
  def forOrg: SocialProfile = this.copy(objectType = ProfileType.Organisation)
  def forPerson: SocialProfile = this.copy(objectType = ProfileType.Person)
}

