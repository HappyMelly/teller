/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing
 * Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models.service

import models.{ UserIdentity, UserRole }
import models.database.{ People, UserAccounts, UserIdentities }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import securesocial.core.IdentityId
import securesocial.core.providers.{ LinkedInProvider, GoogleProvider, FacebookProvider, TwitterProvider }

class UserIdentityService {

  /**
   * Returns user identity filled with account and person data if identity exists,
   * otherwise - None
   *
   * @param identityId Identity object from SecureSocial
   * @return
   */
  def findByUserId(identityId: IdentityId): Option[UserIdentity] = DB.withSession {
    implicit session: Session ⇒

      val q = identityId.providerId match {
        case TwitterProvider.Twitter ⇒ for {
          identity ← UserIdentities
          if (identity.userId is identityId.userId) && (identity.providerId is identityId.providerId)
          account ← UserAccounts if account.twitterHandle === identity.twitterHandle
          person ← People if person.id === account.personId
        } yield (identity, account, person)

        case FacebookProvider.Facebook ⇒ for {
          identity ← UserIdentities
          if (identity.userId is identityId.userId) && (identity.providerId is identityId.providerId)
          account ← UserAccounts if account.facebookUrl like identity.facebookUrl
          person ← People if person.id === account.personId
        } yield (identity, account, person)

        case GoogleProvider.Google ⇒ for {
          identity ← UserIdentities
          if (identity.userId is identityId.userId) && (identity.providerId is identityId.providerId)
          account ← UserAccounts if account.googlePlusUrl === identity.googlePlusUrl
          person ← People if person.id === account.personId
        } yield (identity, account, person)

        case LinkedInProvider.LinkedIn ⇒ for {
          identity ← UserIdentities
          if (identity.userId is identityId.userId) && (identity.providerId is identityId.providerId)
          account ← UserAccounts if account.linkedInUrl like identity.linkedInUrl
          person ← People if person.id === account.personId
        } yield (identity, account, person)
      }
      val data = q.firstOption
      data map { d ⇒
        val account = d._2
        val roles = UserRole.forName(account.role)
        account.roles_=(roles.list)

        d._1.account_=(Some(account))
        d._1.person_=(d._3)
        Some(d._1)
      } getOrElse None
  }
}

object UserIdentityService {
  private val instance = new UserIdentityService

  def get: UserIdentityService = instance
}