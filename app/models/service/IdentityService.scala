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
import models.database._
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import securesocial.core.providers._
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IdentityService extends HasDatabaseConfig[JdbcProfile]
  with MemberTable
  with PasswordIdentityTable
  with PersonTable
  with RegisteringUserTable
  with SocialIdentityTable
  with UserAccountTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._

  private val identities = TableQuery[SocialIdentities]
  private val passwordIdentities = TableQuery[PasswordIdentities]

  /**
    * Returns true if the given email doesn't exist in the set of registered emails
    * @param email Email to check
    * @param userId User identifier we exclude from an email check
    */
  def checkEmail(email: String, userId: Option[Long] = None): Future[Boolean] = {
    val query = if (userId.nonEmpty) {
      for {
        (identity, registering) <- passwordIdentities joinLeft TableQuery[RegisteringUsers] on ((p, u) =>
          p.email === u.userId && u.providerId === UsernamePasswordProvider.UsernamePassword) if identity.email === email && identity.userId =!= userId
      } yield (identity, registering)
    } else {
      for {
        (identity, registering) <- passwordIdentities joinLeft TableQuery[RegisteringUsers] on ((p, u) =>
          p.email === u.userId && u.providerId === UsernamePasswordProvider.UsernamePassword) if identity.email === email
      } yield (identity, registering)
    }
    db.run(query.result).map(_.headOption.map(_._2.isDefined).getOrElse(true))
  }

  /**
    * Deletes a password identity for the given email
    * @param email Email
    */
  def delete(email: String): Unit =
    db.run(passwordIdentities.filter(_.email === email).delete)

  /**
    * Returns a password identify for the given email if exists
    * @param email Email address
    */
  def findByEmail(email: String): Future[Option[PasswordIdentity]] =
    db.run(passwordIdentities.filter(_.email === email).result).map(_.headOption)

  /**
   * @param token
   */
  def findBytoken(token: String): Future[Option[SocialIdentity]] =
    db.run(identities.filter(_.apiToken === token).result).map(_.headOption)

  /**
    * Returns a password identity for the given user if exists
    * @param userId User identifier
    */
  def findByUserId(userId: Long): Future[Option[PasswordIdentity]] =
    db.run(passwordIdentities.filter(_.userId === userId).result).map(_.headOption)

  def findByUserId(userId: String, providerId: String): Future[Option[SocialIdentity]] = {
    val query = identities.filter(_.userId === userId).filter(_.providerId === providerId)
    db.run(query.result).map(_.headOption)
  }

    /**
      * Returns user identity filled with account and person data if identity exists,
      * otherwise - None
      *
      * @param userId User identifier from a social network
      * @param providerId Provider type
      * @return
      */
  def findActiveUser(userId: String, providerId: String): Future[Option[ActiveUser]] = {
      val accounts = TableQuery[UserAccounts]
      val people = TableQuery[People]
      val members = TableQuery[Members]
      val q = providerId match {
        case TwitterProvider.Twitter ⇒ for {
          identity ← identities
          if (identity.userId === userId) && (identity.providerId === providerId)
          a ← accounts if a.twitterHandle === identity.profileUrl
          (p, m) ← people joinLeft members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
        } yield (identity, a, p, m)

        case FacebookProvider.Facebook ⇒ for {
          identity ← identities
          if (identity.userId === userId) && (identity.providerId === providerId)
          a ← accounts if a.facebookUrl === identity.profileUrl
          (p, m) ← people joinLeft members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
        } yield (identity, a, p, m)

        case GoogleProvider.Google ⇒ for {
          identity ← identities
          if (identity.userId === userId) && (identity.providerId === providerId)
          a ← accounts if a.googlePlusUrl === identity.profileUrl
          (p, m) ← people joinLeft members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
        } yield (identity, a, p, m)

        case LinkedInProvider.LinkedIn ⇒ for {
          identity ← identities
          if (identity.userId === userId) && (identity.providerId === providerId)
          a ← accounts if a.linkedInUrl === identity.profileUrl
          (p, m) ← people joinLeft members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
        } yield (identity, a, p, m)
      }
      db.run(q.result).map(_.headOption.map { result =>
        ActiveUser(userId, providerId, result._2, result._3, result._4)
      })
    }

    /**
      * Returns active user filled with account and person data if identity exists,
      * otherwise - None
      *
      * @param email Email
      * @return
      */
  def findActiveUserByEmail(email: String): Future[Option[ActiveUser]] = {
      val accounts = TableQuery[UserAccounts]
      val people = TableQuery[People]
      val members = TableQuery[Members]
      val query = for {
        identity ← passwordIdentities if identity.email === email
        a ← accounts if a.personId === identity.userId
        (p, m) ← people joinLeft members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
      } yield (identity, a, p, m)
      db.run(query.result).map(_.headOption.map { result =>
        ActiveUser(email, UsernamePasswordProvider.UsernamePassword, result._2, result._3, result._4)
      })
    }

    /**
      * Returns account and person data for the given identity
      *
      * @todo cover with tests
      * @param identity Identity
      */
  def findActiveUserData(identity: SocialIdentity): Future[Option[(UserAccount, Person, Option[Member])]] = {
    val accounts = TableQuery[UserAccounts]
    val people = TableQuery[People]
    val members = TableQuery[Members]
    val q = identity.profile.providerId match {
      case TwitterProvider.Twitter ⇒ for {
        a ← accounts if a.twitterHandle === identity.profileUrl
        (p, m) ← people joinLeft members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
      } yield (a, p, m)

      case FacebookProvider.Facebook ⇒ for {
        a ← accounts if a.facebookUrl === identity.profileUrl
        (p, m) ← people joinLeft members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
      } yield (a, p, m)

      case GoogleProvider.Google ⇒ for {
        a ← accounts if a.googlePlusUrl === identity.profileUrl
        (p, m) ← people joinLeft members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
      } yield (a, p, m)

      case LinkedInProvider.LinkedIn ⇒ for {
        a ← accounts if a.linkedInUrl === identity.profileUrl
        (p, m) ← people joinLeft members on ((t1, t2) ⇒ t1.id === t2.objectId && t2.person === true) if p.id === a.personId
      } yield (a, p, m)
    }
    db.run(q.result).map(_.headOption)
  }

  /**
   * Inserts the given identity to database
   * @param identity Identity object
   * @return The given identity with updated id
   */
  def insert(identity: SocialIdentity): Future[SocialIdentity] = {
    val query = identities returning identities.map(_.uid) into ((value, id) => value.copy(uid = Some(id)))
    db.run(query += identity)
  }

  /**
    * Inserts the given password identity to database
    * @param identity Identity object
    * @return The given identity with updated id
    */
  def insert(identity: PasswordIdentity): Future[PasswordIdentity] =
    db.run(passwordIdentities += identity).map(_ => identity)

  /**
    * Updates the given identity in the database
    * @param identity Identity object
    */
  def update(identity: PasswordIdentity): Future[PasswordIdentity] =
    db.run(passwordIdentities.filter(_.email === identity.email).update(identity)).map(_ => identity)

  /**
   * Updates the given idenitity
   * @param updated Updated identity
   * @param existing Existing identity
   */
  def update(updated: SocialIdentity, existing: SocialIdentity): Future[SocialIdentity] = {
    val identity = updated.copy(uid = existing.uid, apiToken = existing.apiToken)
    db.run(identities.filter(_.uid === existing.uid).update(identity)).map(_ => identity)
  }

}

object IdentityService {
  private val instance = new IdentityService

  def get: IdentityService = instance
}