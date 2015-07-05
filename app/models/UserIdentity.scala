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

import models.service.{ PersonService, UserAccountService, UserIdentityService }
import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.Crypto
import securesocial.core.providers.{ FacebookProvider, GoogleProvider, LinkedInProvider, TwitterProvider }
import securesocial.core.{ OAuth1Info, OAuth2Info, PasswordInfo, _ }

import scala.Predef._
import scala.util.Random

case class ActiveUser(identity: UserIdentity,
    account: UserAccount,
    person: Person) {

  val name: String = person.fullName
}

/**
 * Contains profile and authentication info for a SecureSocial Identity
 */
case class UserIdentity(uid: Option[Long],
    profile: BasicProfile,
    apiToken: String,
    twitterHandle: Option[String],
    facebookUrl: Option[String],
    googlePlusUrl: Option[String],
    linkedInUrl: Option[String]) {

  def name: String = profile.fullName getOrElse {
    profile.firstName.getOrElse("") + " " + profile.lastName.getOrElse("")
  }
}

object UserIdentity {

  /**
   * Factory method to return a Twitter login identity.
   */
  def forTwitterHandle(i: GenericProfile, twitterHandle: String): UserIdentity = UserIdentity(None,
    BasicProfile(i.providerId, i.userId, i.firstName, i.lastName, i.fullName, i.email, i.avatarUrl,
      i.authMethod, i.oAuth1Info, i.oAuth2Info, i.passwordInfo),
    generateApiToken(i.userId), Some(twitterHandle), None, None, None)

  /**
   * Factory method to return a Facebook login identity.
   */
  def forFacebookUrl(i: GenericProfile, facebookUrl: String): UserIdentity = UserIdentity(None,
    BasicProfile(i.providerId, i.userId, i.firstName, i.lastName, i.fullName, i.email, i.avatarUrl,
      i.authMethod, i.oAuth1Info, i.oAuth2Info, i.passwordInfo),
    generateApiToken(i.userId), None, Some(facebookUrl), None, None)

  /**
   * Factory method to return a Facebook login identity.
   */
  def forGooglePlusUrl(i: GenericProfile, googlePlusUrl: String): UserIdentity = UserIdentity(None,
    BasicProfile(i.providerId, i.userId, i.firstName, i.lastName, i.fullName, i.email, i.avatarUrl,
      i.authMethod, i.oAuth1Info, i.oAuth2Info, i.passwordInfo),
    generateApiToken(i.userId), None, None, Some(googlePlusUrl), None)

  /**
   * Factory method to return a LinkedIn login identity.
   */
  def forLinkedInUrl(i: GenericProfile, linkedInUrl: String): UserIdentity = UserIdentity(None,
    BasicProfile(i.providerId, i.userId, i.firstName, i.lastName, i.fullName, i.email, i.avatarUrl,
      i.authMethod, i.oAuth1Info, i.oAuth2Info, i.passwordInfo),
    generateApiToken(i.userId), None, None, None, Some(linkedInUrl))

  private def generateApiToken(userId: String) = {
    Crypto.sign("%s-%s".format(userId, Random.nextInt()))
  }

}

