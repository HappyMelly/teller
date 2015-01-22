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

import models.{ UserAccount, Person, UserRole }
import models.database.UserAccounts
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

class UserAccountService {

  /**
   * Returns the given person’s role
   * @param personId Person identifier
   */
  def findRole(personId: Long): Option[UserRole.Role.Role] = DB.withSession {
    implicit session: Session ⇒
      val query = for {
        account ← UserAccounts if account.personId === personId
      } yield account.role
      query.firstOption.map(role ⇒ UserRole.Role.withName(role))
  }

  /**
   * Returns the account for the person who has a duplicate social network
   * identity, if there is one
   *
   * @param person Person object
   */
  def findDuplicateIdentity(person: Person): Option[UserAccount] = DB.withSession {
    implicit session: Session ⇒
      val query = Query(UserAccounts).filter(_.personId =!= person.id).filter { account ⇒
        account.twitterHandle.toLowerCase === person.socialProfile.twitterHandle.map(_.toLowerCase) ||
          account.googlePlusUrl === person.socialProfile.googlePlusUrl ||
          (account.facebookUrl like "https?".r.replaceFirstIn(person.socialProfile.facebookUrl.getOrElse(""), "%")) ||
          (account.linkedInUrl like "https?".r.replaceFirstIn(person.socialProfile.linkedInUrl.getOrElse(""), "%"))
      }
      query.firstOption
  }

}

object UserAccountService {
  private val instance = new UserAccountService

  def get: UserAccountService = instance
}