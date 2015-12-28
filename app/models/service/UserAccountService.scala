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

import models.database.UserAccounts
import models.{Person, UserAccount, SocialIdentity}
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import securesocial.core.providers._

class UserAccountService {

  /**
   * @todo cover with tests
   * @param personId
   */
  def delete(personId: Long) = DB.withSession { implicit session ⇒
    val accounts = TableQuery[UserAccounts]
    accounts.filter(_.personId === personId).delete
  }

  /**
    * Returns an account for the given person if exists
    * @param personId Person identifier
    */
  def findByPerson(personId: Long): Option[UserAccount] = DB.withSession { implicit session =>
    TableQuery[UserAccounts].filter(_.personId === personId).firstOption
  }

  /**
   * Inserts the given account to database
   * @param account Account object
   * @return The given account with updated id
   */
  def insert(account: UserAccount) = DB.withSession { implicit session ⇒
    val accounts = TableQuery[UserAccounts]
    val id = (accounts returning accounts.map(_.id)) += account
    account.copy(id = Some(id))
  }

  /**
    * Updates the given account in database
    * @param account User account
    */
  def update(account: UserAccount) = DB.withSession { implicit session =>
    TableQuery[UserAccounts].filter(_.id === account.id).update(account)
    account
  }

  /**
   * Updates active role for the given user
   */
  def updateActiveRole(personId: Long, role: Boolean): Unit = DB.withSession {
    implicit session ⇒
      val accounts = TableQuery[UserAccounts]
      accounts.filter(_.personId === personId).map(_.activeRole).update(role)
  }

  /**
   * Updates the social network authentication provider identifiers, used when these may have been edited for a person,
   * so that an existing account can be able to log in on a new provider or for a provider with a edited identifier.
   */
  def updateSocialNetworkProfiles(person: Person): Unit = DB.withSession {
    implicit session ⇒
      val accounts = TableQuery[UserAccounts]
      val query = for {
        account ← accounts if account.personId === person.id
      } yield (account.twitterHandle,
        account.facebookUrl,
        account.googlePlusUrl,
        account.linkedInUrl)
      query.update(person.socialProfile.twitterHandle,
        person.socialProfile.facebookUrl,
        person.socialProfile.googlePlusUrl,
        person.socialProfile.linkedInUrl)
  }

}

object UserAccountService {
  private val instance = new UserAccountService

  def get: UserAccountService = instance
}