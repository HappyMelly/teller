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

package models

import models.database.{ People, UserAccounts, LoginIdentities }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.libs.Crypto
import play.api.Play.current
import scala.slick.lifted.Query
import scala.util.Random
import securesocial.core._

/**
 * Contains profile and authentication info for a SecureSocial Identity.
 */
case class LoginIdentity(uid: Option[Long], identityId: IdentityId, firstName: String, lastName: String, fullName: String,
  email: Option[String], avatarUrl: Option[String], authMethod: AuthenticationMethod,
  oAuth1Info: Option[OAuth1Info], oAuth2Info: Option[OAuth2Info],
  passwordInfo: Option[PasswordInfo] = None, twitterHandle: String, apiToken: String) extends Identity {

  def person = DB.withSession { implicit session: Session ⇒
    (for {
      account ← UserAccounts if account.twitterHandle === twitterHandle
      person ← People if person.id === account.personId

    } yield person).first
  }

  /** Returns the `UserAccount` for this identity **/
  def userAccount = DB.withSession { implicit session: Session ⇒
    (for { account ← UserAccounts if account.twitterHandle === twitterHandle } yield account).first
  }
}

object LoginIdentity {

  def apply(i: Identity): (String) ⇒ LoginIdentity = LoginIdentity(None, i.identityId, i.firstName, i.lastName, i.fullName, i.email,
    i.avatarUrl, i.authMethod, i.oAuth1Info, i.oAuth2Info, i.passwordInfo, _, generateApiToken(i))

  private def generateApiToken(i: Identity) = { Crypto.sign("%s-%s".format(i.identityId.userId, Random.nextInt())) }

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
        user.copy(uid = Some(uid))
      }
      case Some(existingUser) ⇒ {
        val userRow = for {
          u ← LoginIdentities
          if u.uid is existingUser.uid
        } yield u

        val updatedUser = user.copy(uid = existingUser.uid, apiToken = existingUser.apiToken)
        userRow.update(updatedUser)
        updatedUser
      }
    }
  }
}

