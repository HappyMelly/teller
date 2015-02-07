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

import models.database.{ People, UserAccounts, LoginIdentities }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.libs.Crypto
import play.api.Play.current
import play.api.cache.Cache
import scala.slick.lifted.Query
import scala.util.Random
import securesocial.core._
import scala.Predef._
import securesocial.core.OAuth2Info
import securesocial.core.OAuth1Info
import securesocial.core.IdentityId
import securesocial.core.PasswordInfo
import securesocial.core.providers.{ FacebookProvider, GoogleProvider, LinkedInProvider, TwitterProvider }

/**
 * Contains profile and authentication info for a SecureSocial Identity.
 */
case class LoginIdentity(uid: Option[Long],
  identityId: IdentityId,
  firstName: String,
  lastName: String,
  fullName: String,
  email: Option[String],
  avatarUrl: Option[String],
  authMethod: AuthenticationMethod,
  oAuth1Info: Option[OAuth1Info],
  oAuth2Info: Option[OAuth2Info],
  passwordInfo: Option[PasswordInfo] = None,
  apiToken: String,
  twitterHandle: Option[String],
  facebookUrl: Option[String],
  googlePlusUrl: Option[String],
  linkedInUrl: Option[String]) extends Identity {

  private var _account: Option[UserAccount] = None
  private var _person: Option[Person] = None

  /**
   * Returns the database query that will fetch this identity’s `UserAccount`, for the appropriate provider.
   */
  private def accountQuery: Query[UserAccounts.type, UserAccount] = identityId.providerId match {
    case TwitterProvider.Twitter ⇒ {
      Query(UserAccounts).filter(_.twitterHandle === twitterHandle)
    }
    case FacebookProvider.Facebook ⇒ {
      Query(UserAccounts).filter(_.facebookUrl like "https?".r.replaceFirstIn(facebookUrl.getOrElse(""), "%"))
    }
    case GoogleProvider.Google ⇒ {
      Query(UserAccounts).filter(_.googlePlusUrl === googlePlusUrl)
    }
    case LinkedInProvider.LinkedIn ⇒ {
      Query(UserAccounts).filter(_.linkedInUrl like "https?".r.replaceFirstIn(linkedInUrl.getOrElse(""), "%"))
    }
  }

  def name = identityId.providerId match {
    case TwitterProvider.Twitter ⇒ twitterHandle
    case FacebookProvider.Facebook ⇒ facebookUrl
    case GoogleProvider.Google ⇒ googlePlusUrl
    case LinkedInProvider.LinkedIn ⇒ linkedInUrl
  }

  /**
   * Returns the `Person` associated with this identity.
   */
  def person: Person = _person map { p ⇒ p } getOrElse {
    println("======> PERSON REQUEST")
    val person = DB.withSession { implicit session: Session ⇒
      (for {
        account ← accountQuery
        person ← People if person.id === account.personId
      } yield person).first
    }
    _person = Some(person)
    _person.get
  }

  /**
   * Returns user account associated with this identity
   */
  def account: UserAccount = _account map { v ⇒ v } getOrElse {
    println("ACCOUNT REQUEST")
    val account = DB.withSession { implicit session: Session ⇒
      accountQuery.first
    }
    val roles = UserRole.forName(account.role)
    account.roles_=(roles.list)
    _account = Some(account)
    _account.get
  }

  /**
   * Sets account attribute
   * @param account Account object
   */
  def account_=(account: Option[UserAccount]) = _account = account

}

object LoginIdentity {

  /**
   * Factory method to return a Twitter login identity.
   */
  def forTwitterHandle(i: Identity, twitterHandle: String): LoginIdentity = LoginIdentity(None, i.identityId,
    i.firstName, i.lastName, i.fullName, i.email, i.avatarUrl, i.authMethod, i.oAuth1Info, i.oAuth2Info, i.passwordInfo,
    generateApiToken(i), Some(twitterHandle), None, None, None)

  /**
   * Factory method to return a Facebook login identity.
   */
  def forFacebookUrl(i: Identity, facebookUrl: String): LoginIdentity = LoginIdentity(None, i.identityId,
    i.firstName, i.lastName, i.fullName, i.email, i.avatarUrl, i.authMethod, i.oAuth1Info, i.oAuth2Info, i.passwordInfo,
    generateApiToken(i), None, Some(facebookUrl), None, None)

  /**
   * Factory method to return a Facebook login identity.
   */
  def forGooglePlusUrl(i: Identity, googlePlusUrl: String): LoginIdentity = LoginIdentity(None, i.identityId,
    i.firstName, i.lastName, i.fullName, i.email, i.avatarUrl, i.authMethod, i.oAuth1Info, i.oAuth2Info, i.passwordInfo,
    generateApiToken(i), None, None, Some(googlePlusUrl), None)

  /**
   * Factory method to return a LinkedIn login identity.
   */
  def forLinkedInUrl(i: Identity, linkedInUrl: String): LoginIdentity = LoginIdentity(None, i.identityId,
    i.firstName, i.lastName, i.fullName, i.email, i.avatarUrl, i.authMethod, i.oAuth1Info, i.oAuth2Info, i.passwordInfo,
    generateApiToken(i), None, None, None, Some(linkedInUrl))

  private def generateApiToken(i: Identity) = {
    Crypto.sign("%s-%s".format(i.identityId.userId, Random.nextInt()))
  }

  def findBytoken(token: String): Option[LoginIdentity] = DB.withSession { implicit session: Session ⇒
    Query(LoginIdentities).filter(_.apiToken === token).list.headOption
  }

  def findByUid(uid: Long) = DB.withSession { implicit session: Session ⇒
    val q = for {
      user ← LoginIdentities
      if user.uid is uid
    } yield user

    q.firstOption
  }

  def findByUserId(identityId: IdentityId): Option[LoginIdentity] = DB.withSession { implicit session: Session ⇒
    val q = for {
      identity ← LoginIdentities
      if (identity.userId is identityId.userId) && (identity.providerId is identityId.providerId)
    } yield identity

    q.firstOption
  }

  def save(user: LoginIdentity) = DB.withSession { implicit session: Session ⇒
    findByUserId(user.identityId) match {
      case None ⇒ {
        Activity.insert(user.fullName, Activity.Predicate.SignedUp)
        val uid = LoginIdentities.forInsert.insert(user)
        val updatedUser = user.copy(uid = Some(uid))
        Cache.set("identity." + updatedUser.apiToken, updatedUser)
        updatedUser
      }
      case Some(existingUser) ⇒ {
        val userRow = for {
          u ← LoginIdentities
          if u.uid is existingUser.uid
        } yield u

        val updatedUser = user.copy(uid = existingUser.uid, apiToken = existingUser.apiToken)
        userRow.update(updatedUser)
        val cacheKey = "identity." + updatedUser.apiToken
        Cache.remove(cacheKey)
        Cache.set(cacheKey, updatedUser)
        updatedUser
      }
    }
  }
}

