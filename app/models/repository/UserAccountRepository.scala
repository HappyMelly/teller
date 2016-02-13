/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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
package models.repository

import models.database.UserAccountTable
import models.{Person, UserAccount}
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import securesocial.core.providers.{LinkedInProvider, GoogleProvider, TwitterProvider, FacebookProvider}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserAccountRepository(app: Application) extends HasDatabaseConfig[JdbcProfile] with UserAccountTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._

  /**
   * @todo cover with tests
   * @param personId Person identifier
   */
  def delete(personId: Long) =
    db.run(accounts.filter(_.personId === personId).delete)

  /**
    * Returns user account for the given remote user id and profile if exists
    * @param remoteUserId Remote user id
    * @param profileId Profile identifier e.g. facebook, google, etc
    */
  def find(remoteUserId: String, profileId: String): Future[Option[UserAccount]] = {
    val query = profileId match {
      case FacebookProvider.Facebook => accounts.filter(_.facebook === remoteUserId)
      case TwitterProvider.Twitter => accounts.filter(_.twitter === remoteUserId)
      case GoogleProvider.Google => accounts.filter(_.google === remoteUserId)
      case LinkedInProvider.LinkedIn => accounts.filter(_.linkedin === remoteUserId)
    }
    db.run(query.result).map(_.headOption)
  }

  /**
    * Returns an account for the given person if exists
 *
    * @param personId Person identifier
    */
  def findByPerson(personId: Long): Future[Option[UserAccount]] =
    db.run(userAccountQuery.findByPerson(personId).result).map(_.headOption)

  /**
   * Inserts the given account to database
 *
   * @param account Account object
   * @return The given account with updated id
   */
  def insert(account: UserAccount): Future[UserAccount] = db.run(userAccountActions.insert(account))

  /**
    * Updates the given account in database
 *
    * @param account User account
    */
  def update(account: UserAccount) = db.run(userAccountActions.update(account)).map(_ => account)

  /**
   * Updates active role for the given user
   */
  def updateActiveRole(personId: Long, role: Boolean): Unit =
    db.run(accounts.filter(_.personId === personId).map(_.activeRole).update(role))

  /**
   * Updates the social network authentication provider identifiers, used when these may have been edited for a person,
   * so that an existing account can be able to log in on a new provider or for a provider with a edited identifier.
   */
  def updateSocialNetworkProfiles(person: Person): Unit = {
    val query = for {
      account ← accounts if account.personId === person.id
    } yield (account.twitter,
      account.facebook,
      account.google,
      account.linkedin)
    val action = query.update(person.profile.twitterHandle,
      person.profile.facebookUrl,
      person.profile.googlePlusUrl,
      person.profile.linkedInUrl)
    db.run(action)
  }
}