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

import be.objectify.deadbolt.core.models.Role
import play.libs.Scala
import scala.collection.mutable.ListBuffer

/**
 *
 */
case class UserRole(role: UserRole.Role.Role) extends Role {
  def getName: String = role.toString

  import UserRole.Role._

  def admin: Boolean = role == Admin
  def editor: Boolean = role == Editor || admin
  def viewer: Boolean = role == Viewer || editor

  /**
   * Returns the list of rules implied by this role.
   */
  def list: java.util.List[UserRole] = {
    val roles = ListBuffer[UserRole]()
    if (viewer) roles += UserRole(Viewer)
    if (editor) roles += UserRole(Editor)
    if (admin) roles += UserRole(Admin)
    Scala.asJava(roles)
  }
}

object UserRole {

  object Role extends Enumeration {
    type Role = Value
    val Viewer = Value("viewer")
    val Editor = Value("editor")
    val Admin = Value("admin")
  }

  def forName(name: String): UserRole = UserRole(Role.withName(name))
}
