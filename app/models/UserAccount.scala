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

import be.objectify.deadbolt.core.models.{ Permission, Subject }
import models.database.UserAccounts
import models.service._
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.libs.Scala

import scala.collection.JavaConversions._

/**
 * A log-in user account.
 */
case class UserAccount(id: Option[Long],
  personId: Long,
  role: String,
  twitterHandle: Option[String],
  facebookUrl: Option[String],
  linkedInUrl: Option[String],
  googlePlusUrl: Option[String]) extends Subject with Services {

  private var _roles: Option[List[UserRole]] = None

  def roles_=(roles: List[UserRole]): Unit = {
    _roles = Some(roles)
  }

  /**
   * Returns a string list of role names, for the Subject interface.
   */
  def getRoles: java.util.List[UserRole] = if (_roles.isEmpty) {
    val accountRole = userAccountService.findRole(personId).map(role ⇒ UserRole(role))
    roles_=(accountRole.map(_.list).getOrElse(List()))
    _roles.get
  } else {
    _roles.get
  }

  lazy val admin = getRoles.contains(UserRole(UserRole.Role.Admin))
  lazy val editor = getRoles.contains(UserRole(UserRole.Role.Editor))
  lazy val viewer = getRoles.contains(UserRole(UserRole.Role.Viewer))

  def getIdentifier = personId.toString
  def getPermissions: java.util.List[Permission] = Scala.asJava(List.empty[Permission])

  /**
   * True if this person is a facilitator
   */
  lazy val facilitator: Boolean = licenseService.activeLicenses(personId).nonEmpty

  /**
   * True if this person coordinates at least one brand
   */
  def coordinator: Boolean = brands.nonEmpty

  lazy val brands: List[Brand] = brandService.findByCoordinator(personId)

  lazy val person: Option[Person] = personService.find(personId)
}

object UserAccount {

  def delete(personId: Long) = DB.withSession { implicit session: Session ⇒
    UserAccounts.where(_.personId === personId).mutate(_.delete)
  }

  def insert(account: UserAccount) = DB.withSession { implicit session: Session ⇒
    val id = UserAccounts.forInsert.insert(account)
    account.copy(id = Some(id))
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

  /**
   * Updates the social network authentication provider identifiers, used when these may have been edited for a person,
   * so that an existing account can be able to log in on a new provider or for a provider with a edited identifier.
   */
  def updateSocialNetworkProfiles(person: Person): Unit = DB.withSession { implicit session: Session ⇒
    val query = for {
      account ← UserAccounts if account.personId === person.id
    } yield account.twitterHandle ~ account.facebookUrl ~ account.googlePlusUrl ~ account.linkedInUrl
    query.update(person.socialProfile.twitterHandle, person.socialProfile.facebookUrl,
      person.socialProfile.googlePlusUrl, person.socialProfile.linkedInUrl)
  }
}
