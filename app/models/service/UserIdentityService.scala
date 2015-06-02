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

import models._
import models.JodaMoney._
import models.database.{ Members, People, UserAccounts, UserIdentities }
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import securesocial.core.IdentityId
import securesocial.core.providers.{ FacebookProvider, GoogleProvider, LinkedInProvider, TwitterProvider }

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
          a ← UserAccounts if a.twitterHandle === identity.twitterHandle
          (p, m) ← People leftJoin Members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
        } yield (identity, a, p, m.id.?, m.funder.?, m.fee.?, m.renewal.?, m.since.?, m.until.?)

        case FacebookProvider.Facebook ⇒ for {
          identity ← UserIdentities
          if (identity.userId is identityId.userId) && (identity.providerId is identityId.providerId)
          a ← UserAccounts if a.facebookUrl like identity.facebookUrl
          (p, m) ← People leftJoin Members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
        } yield (identity, a, p, m.id.?, m.funder.?, m.fee.?, m.renewal.?, m.since.?, m.until.?)

        case GoogleProvider.Google ⇒ for {
          identity ← UserIdentities
          if (identity.userId is identityId.userId) && (identity.providerId is identityId.providerId)
          a ← UserAccounts if a.googlePlusUrl === identity.googlePlusUrl
          (p, m) ← People leftJoin Members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
        } yield (identity, a, p, m.id.?, m.funder.?, m.fee.?, m.renewal.?, m.since.?, m.until.?)

        case LinkedInProvider.LinkedIn ⇒ for {
          identity ← UserIdentities
          if (identity.userId is identityId.userId) && (identity.providerId is identityId.providerId)
          a ← UserAccounts if a.linkedInUrl like identity.linkedInUrl
          (p, m) ← People leftJoin Members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
        } yield (identity, a, p, m.id.?, m.funder.?, m.fee.?, m.renewal.?, m.since.?, m.until.?)
      }
      val data = q.firstOption
      data map { d ⇒
        val account = d._2
        val roles = UserRole.forName(account.role)
        account.roles_=(roles.list)

        d._1.account_=(Some(account))
        val person: Person = d._3
        if (d._4.nonEmpty) {
          val member = new Member(d._4, person.id.get, person = true,
            funder = d._5.get, "EUR" -> d._6.get, d._7.get,
            d._8.get, d._9.get, existingObject = true, reason = None,
            DateTime.now(), 0L, DateTime.now(), 0L)
          person.member_=(member)
        }
        d._1.person_=(person)
        Some(d._1)
      } getOrElse None
  }
}

object UserIdentityService {
  private val instance = new UserIdentityService

  def get: UserIdentityService = instance
}