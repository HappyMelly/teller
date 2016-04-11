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

import play.api.libs.Crypto
import securesocial.core.providers._
import securesocial.core.{PasswordInfo, _}

import scala.Predef._
import scala.util.Random

case class ActiveUser(id: String,
                      providerId: String,
                      account: UserAccount,
                      person: Person,
                      member: Option[Member] = None) {

  val name: String = person.fullName
}

/**
 * Contains profile and authentication info when a user authenticates using a social network
 */
case class SocialIdentity(uid: Option[Long],
                          profile: BasicProfile,
                          apiToken: String) {

  def name: String = profile.fullName getOrElse {
    profile.firstName.getOrElse("") + " " + profile.lastName.getOrElse("")
  }
}

/**
  * Contains profile and authentication info when a user authenticates using email
  */
case class PasswordIdentity(userId: Option[Long],
  email: String,
  password: String,
  firstName: Option[String],
  lastName: Option[String],
  hasher: String) {

  def profile: BasicProfile = BasicProfile(UsernamePasswordProvider.UsernamePassword, email,
    firstName, lastName, Some(fullName), Some(email),
    None, AuthenticationMethod.UserPassword, None, None, Some(PasswordInfo(hasher, password)))

  def fullName: String = if (firstName.nonEmpty && lastName.nonEmpty) {
    firstName.get + " " + lastName.get
  } else ""
}

object PasswordIdentity {

  /**
    * Returns new identity based on the given profile
    * @param profile Profile
    */
  def fromProfile(profile: BasicProfile): PasswordIdentity = profile.passwordInfo.map { info =>
    PasswordIdentity(None, profile.userId, info.password, profile.firstName, profile.lastName, info.hasher)
  }.getOrElse {
    PasswordIdentity(None, profile.userId, "", profile.firstName, profile.lastName, "")
  }
}

object SocialIdentity {

  def apply(i: GenericProfile): SocialIdentity = SocialIdentity(None,
    BasicProfile(i.providerId, i.userId, i.firstName, i.lastName, i.fullName, i.email, i.avatarUrl,
      i.authMethod, i.oAuth1Info, i.oAuth2Info, i.passwordInfo, i.extraInfo),
    generateApiToken(i.userId))

  private def generateApiToken(userId: String) = {
    Crypto.sign("%s-%s".format(userId, Random.nextInt()))
  }

}

