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

import be.objectify.deadbolt.core.models.{ Permission, Subject }
import models.database.UserAccounts
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import play.libs.Scala

/**
 * A log-in user account.
 */
case class UserAccount(id: Option[Long], personId: Long, twitterHandle: String, role: String) extends Subject {

  /**
   * Returns a string list of role names.
   */
  def getRoles: java.util.List[UserRole] = {
    val accountRole = UserAccount.findRole(personId).map(role ⇒ UserRole(role))
    accountRole.map(_.list).getOrElse(java.util.Collections.emptyList())
  }

  def getIdentifier = personId.toString
  def getPermissions: java.util.List[Permission] = Scala.asJava(List.empty[Permission])

  lazy val person: Option[Person] = Person.find(personId)
  lazy val account: Option[Account] = Account.findByPerson(personId)
}

object UserAccount {

  def delete(personId: Long) = DB.withSession { implicit session: Session ⇒
    UserAccounts.where(_.personId === personId).mutate(_.delete)
  }

  /**
   * Returns the account for the person with the given Twitter handle.
   */
  def findByTwitterHandle(twitterHandle: String): Option[UserAccount] = DB.withSession { implicit session: Session ⇒
    val query = for {
      account ← UserAccounts if account.twitterHandle.toLowerCase === twitterHandle.toLowerCase
    } yield account
    query.firstOption
  }

  def insert(account: UserAccount) = DB.withSession { implicit session: Session ⇒
    val id = UserAccounts.forInsert.insert(account)
    account.copy(id = Some(id))
  }

  /**
   * Returns the given person’s role.
   */
  def findRole(personId: Long): Option[UserRole.Role.Role] = DB.withSession { implicit session: Session ⇒
    val query = for {
      account ← UserAccounts if account.personId === personId
    } yield account.role
    query.firstOption.map(role ⇒ UserRole.Role.withName(role))
  }

  /**
   * Returns the given person’s role.
   */
  def findRoleByTwitterHandle(twitterHandle: String): Option[UserRole] = DB.withSession { implicit session: Session ⇒
    val query = for {
      account ← UserAccounts
      person ← account.person if person.twitterHandle === twitterHandle
    } yield account.role
    query.firstOption.map(role ⇒ UserRole.forName(role))
  }

  /**
   * Updates the user’s role.
   */
  def updateRole(personId: Long, role: String): Unit = DB.withSession { implicit session: Session ⇒
    val query = for {
      account ← UserAccounts if account.personId === personId
    } yield account.role
    query.update(role)
  }
}
