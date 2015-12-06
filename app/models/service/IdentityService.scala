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

import models.database.PortableJodaSupport._
import models.JodaMoney._
import models._
import models.database._
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import securesocial.core.providers._

class IdentityService {

  private val identities = TableQuery[SocialIdentities]
  private val passwordIdentities = TableQuery[PasswordIdentities]

  /**
    * Returns true if the given email doesn't exist in the set of registered emails
    * @param email Email to check
    */
  def checkEmail(email: String): Boolean = DB.withSession { implicit session =>
    val query = for {
      (identity, registering) <- passwordIdentities leftJoin TableQuery[RegisteringUsers] on ((p, u) =>
        p.email === u.userId && u.providerId === UsernamePasswordProvider.UsernamePassword) if identity.email === email
    } yield (identity, registering.userId.?)
    query.firstOption.map { result =>
      result._2.isDefined
    } getOrElse true
  }

  /**
    * Returns a password identify for the given email if exists
    * @param email Email address
    */
  def findByEmail(email: String): Option[PasswordIdentity] = DB.withSession { implicit session ⇒
    passwordIdentities.filter(_.email === email).firstOption
  }

  /**
   * @param token
   * @return
   */
  def findBytoken(token: String): Option[SocialIdentity] = DB.withSession {
    implicit session ⇒
      identities.filter(_.apiToken === token).list.headOption
  }

  def findByUserId(userId: String, providerId: String): Option[SocialIdentity] =
    DB.withSession { implicit session ⇒
      identities.
        filter(_.userId === userId).
        filter(_.providerId === providerId).firstOption
    }

  /**
   * Returns user identity filled with account and person data if identity exists,
   * otherwise - None
   *
   * @param userId User identifier from a social network
   * @param providerId Provider type
   * @return
   */
  def findActiveUser(userId: String, providerId: String): Option[ActiveUser] = DB.withSession {
    implicit session: Session ⇒
      val accounts = TableQuery[UserAccounts]
      val people = TableQuery[People]
      val members = TableQuery[Members]
      val q = providerId match {
        case TwitterProvider.Twitter ⇒ for {
          identity ← identities
          if (identity.userId === userId) && (identity.providerId === providerId)
          a ← accounts if a.twitterHandle === identity.profileUrl
          (p, m) ← people leftJoin members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
        } yield (identity, a, p, m.id.?, m.funder.?, m.fee.?, m.renewal.?, m.since.?, m.until.?)

        case FacebookProvider.Facebook ⇒ for {
          identity ← identities
          if (identity.userId === userId) && (identity.providerId === providerId)
          a ← accounts if a.facebookUrl === identity.profileUrl
          (p, m) ← people leftJoin members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
        } yield (identity, a, p, m.id.?, m.funder.?, m.fee.?, m.renewal.?, m.since.?, m.until.?)

        case GoogleProvider.Google ⇒ for {
          identity ← identities
          if (identity.userId === userId) && (identity.providerId === providerId)
          a ← accounts if a.googlePlusUrl === identity.profileUrl
          (p, m) ← people leftJoin members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
        } yield (identity, a, p, m.id.?, m.funder.?, m.fee.?, m.renewal.?, m.since.?, m.until.?)

        case LinkedInProvider.LinkedIn ⇒ for {
          identity ← identities
          if (identity.userId === userId) && (identity.providerId === providerId)
          a ← accounts if a.linkedInUrl === identity.profileUrl
          (p, m) ← people leftJoin members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
        } yield (identity, a, p, m.id.?, m.funder.?, m.fee.?, m.renewal.?, m.since.?, m.until.?)
      }
      q.firstOption map { d ⇒
        val person: Person = d._3
        val member = if (d._4.nonEmpty)
          Some(Member(d._4, person.id.get, person = true,
            funder = d._5.get, "EUR" -> d._6.get, d._7.get,
            d._8.get, d._9.get, existingObject = true, reason = None,
            DateTime.now(), 0L, DateTime.now(), 0L))
        else
          None
        Some(ActiveUser(userId, d._2, person, member))
      } getOrElse None
  }

  /**
    * Returns active user filled with account and person data if identity exists,
    * otherwise - None
    *
    * @param email Email
    * @return
    */
  def findActiveUserByEmail(email: String): Option[ActiveUser] = DB.withSession {
    implicit session: Session ⇒
      val accounts = TableQuery[UserAccounts]
      val people = TableQuery[People]
      val members = TableQuery[Members]
      val query = for {
        identity ← passwordIdentities if identity.email === email
        a ← accounts if a.personId === identity.userId
        (p, m) ← people leftJoin members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
      } yield (identity, a, p, m.id.?, m.funder.?, m.fee.?, m.renewal.?, m.since.?, m.until.?)
      query.firstOption map { d ⇒
        val person: Person = d._3
        val member = if (d._4.nonEmpty)
          Some(Member(d._4, person.id.get, person = true,
            funder = d._5.get, "EUR" -> d._6.get, d._7.get,
            d._8.get, d._9.get, existingObject = true, reason = None,
            DateTime.now(), 0L, DateTime.now(), 0L))
        else
          None
        Some(ActiveUser(email, d._2, person, member))
      } getOrElse None
  }

  /**
   * Returns account and person data for the given identity
   *
   * @todo cover with tests
   * @param identity Identity
   */
  def findActiveUserData(identity: SocialIdentity): Option[(UserAccount, Person, Option[Member])] =
    DB.withSession {
      implicit session ⇒
        val accounts = TableQuery[UserAccounts]
        val people = TableQuery[People]
        val members = TableQuery[Members]
        val q = identity.profile.providerId match {
          case TwitterProvider.Twitter ⇒ for {
            a ← accounts if a.twitterHandle === identity.profileUrl
            (p, m) ← people leftJoin members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
          } yield (a, p, m.id.?, m.funder.?, m.fee.?, m.renewal.?, m.since.?, m.until.?)

          case FacebookProvider.Facebook ⇒ for {
            a ← accounts if a.facebookUrl === identity.profileUrl
            (p, m) ← people leftJoin members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
          } yield (a, p, m.id.?, m.funder.?, m.fee.?, m.renewal.?, m.since.?, m.until.?)

          case GoogleProvider.Google ⇒ for {
            a ← accounts if a.googlePlusUrl === identity.profileUrl
            (p, m) ← people leftJoin members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
          } yield (a, p, m.id.?, m.funder.?, m.fee.?, m.renewal.?, m.since.?, m.until.?)

          case LinkedInProvider.LinkedIn ⇒ for {
            a ← accounts if a.linkedInUrl === identity.profileUrl
            (p, m) ← people leftJoin members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
          } yield (a, p, m.id.?, m.funder.?, m.fee.?, m.renewal.?, m.since.?, m.until.?)
        }
        q.firstOption map { d ⇒
          val person: Person = d._2
          val member = if (d._3.nonEmpty)
            Some(Member(d._3, person.id.get, person = true,
              funder = d._4.get, "EUR" -> d._5.get, d._6.get,
              d._7.get, d._8.get, existingObject = true, reason = None,
              DateTime.now(), 0L, DateTime.now(), 0L))
          else
            None
          Some((d._1, person, member))
        } getOrElse None
    }

  /**
   * Inserts the given identity to database
   * @param identity Identity object
   * @return The given identity with updated id
   */
  def insert(identity: SocialIdentity): SocialIdentity = DB.withSession {
    implicit session ⇒
      val id = (identities returning identities.map(_.uid)) += identity
      identity.copy(uid = Some(id))
  }

  /**
    * Inserts the given password identity to database
    * @param identity Identity object
    * @return The given identity with updated id
    */
  def insert(identity: PasswordIdentity): PasswordIdentity = DB.withSession { implicit session ⇒
    TableQuery[PasswordIdentities].insert(identity)
    identity
  }

  /**
    * Updates the given identity in the database
    * @param identity Identity object
    */
  def update(identity: PasswordIdentity): PasswordIdentity = DB.withSession { implicit session =>
    TableQuery[PasswordIdentities].filter(_.email === identity.email).update(identity)
    identity
  }

  /**
   * Updates the given idenitity
   * @param updated Updated identity
   * @param existing Existing identity
   */
  def update(updated: SocialIdentity, existing: SocialIdentity): SocialIdentity =
    DB.withSession {
      implicit session ⇒
        val identity = updated.copy(uid = existing.uid, apiToken = existing.apiToken)
        identities.filter(_.uid === existing.uid).update(identity)
        identity
    }

}

object IdentityService {
  private val instance = new IdentityService

  def get: IdentityService = instance
}