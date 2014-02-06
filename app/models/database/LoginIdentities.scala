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

import models.LoginIdentity
import play.api.db.slick.Config.driver.simple._
import securesocial.core.{ IdentityId, OAuth2Info, OAuth1Info, AuthenticationMethod }

/**
 * `LoginIdentity` database table mapping.
 */
private[models] object LoginIdentities extends Table[LoginIdentity]("LOGIN_IDENTITY") {

  implicit def string2AuthenticationMethod: TypeMapper[AuthenticationMethod] = MappedTypeMapper.base[AuthenticationMethod, String](
    authenticationMethod ⇒ authenticationMethod.method,
    string ⇒ AuthenticationMethod(string))

  implicit def tuple2OAuth1Info(tuple: (Option[String], Option[String])) = tuple match {
    case (Some(token), Some(secret)) ⇒ Some(OAuth1Info(token, secret))
    case _ ⇒ None
  }

  implicit def tuple2OAuth2Info(tuple: (Option[String], Option[String], Option[Int], Option[String])) = tuple match {
    case (Some(token), tokenType, expiresIn, refreshToken) ⇒ Some(OAuth2Info(token, tokenType, expiresIn, refreshToken))
    case _ ⇒ None
  }

  implicit def tuple2UserId(tuple: (String, String)) = tuple match {
    case (userId, providerId) ⇒ IdentityId(userId, providerId)
  }

  def uid = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def userId = column[String]("USER_ID")
  def providerId = column[String]("PROVIDER_ID")
  def email = column[Option[String]]("EMAIL")
  def firstName = column[String]("FIRST_NAME")
  def lastName = column[String]("LAST_NAME")
  def fullName = column[String]("FULL_NAME")
  def authMethod = column[AuthenticationMethod]("AUTH_METHOD")
  def avatarUrl = column[Option[String]]("AVATAR_URL")
  def secret = column[Option[String]]("SECRET")
  def tokenType = column[Option[String]]("TOKEN_TYPE")
  def expiresIn = column[Option[Int]]("EXPIRES_IN")
  def refreshToken = column[Option[String]]("REFRESH_TOKEN")
  def apiToken = column[String]("API_TOKEN")
  def twitterHandle = column[Option[String]]("TWITTER_HANDLE")
  def facebookUrl = column[Option[String]]("FACEBOOK_URL")
  def googlePlusUrl = column[Option[String]]("GOOGLE_PLUS_URL")
  def linkedInUrl = column[Option[String]]("LINKEDIN_URL")

  // oAuth 1
  def token = column[Option[String]]("TOKEN")

  // oAuth 2
  def accessToken = column[Option[String]]("ACCESS_TOKEN")

  def * = uid.? ~ userId ~ providerId ~ firstName ~ lastName ~ fullName ~ email ~ avatarUrl ~ authMethod ~ token ~
    secret ~ accessToken ~ tokenType ~ expiresIn ~ refreshToken ~ apiToken ~ twitterHandle ~ facebookUrl ~
    googlePlusUrl ~ linkedInUrl <> (
      u ⇒ LoginIdentity(u._1, (u._2, u._3), u._4, u._5, u._6, u._7, u._8, u._9, (u._10, u._11), (u._12, u._13, u._14, u._15), None, u._16, u._17, u._18, u._19, u._20),
      (u: LoginIdentity) ⇒ {
        Some((u.uid, u.identityId.userId, u.identityId.providerId, u.firstName, u.lastName, u.fullName, u.email,
          u.avatarUrl, u.authMethod, u.oAuth1Info.map(_.token), u.oAuth1Info.map(_.secret),
          u.oAuth2Info.map(_.accessToken), u.oAuth2Info.flatMap(_.tokenType), u.oAuth2Info.flatMap(_.expiresIn),
          u.oAuth2Info.flatMap(_.refreshToken), u.apiToken, u.twitterHandle, u.facebookUrl, u.googlePlusUrl, u.linkedInUrl))
      })

  def forInsert = * returning uid

}