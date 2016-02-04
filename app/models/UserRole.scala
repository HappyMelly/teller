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

/**
 *
 */
case class UserRole(role: UserRole.Role.Role) extends be.objectify.deadbolt.core.models.Role {
  def getName: String = role.toString

  import UserRole._

  def admin: Boolean = role == Role.Admin
  def viewer: Boolean = role == Role.Viewer
  def unregistered: Boolean = role == Role.Unregistered || viewer
  def member: Boolean = role == Role.Member
  def facilitator: Boolean = role == Role.Facilitator
  def coordinator: Boolean = role == Role.Coordinator
  def brandViewer: Boolean = facilitator || coordinator

}

object UserRole {

  object Role extends Enumeration {
    type Role = Value
    val Unregistered = Value("unregistered")
    val Viewer = Value("viewer")
    val Admin = Value("admin")
    val Member = Value("member")
    val Facilitator = Value("facilitator")
    val Coordinator = Value("coordinator")
    val Funder = Value("funder")
    val ProfileEditor = Value("profile-editor")
    val OrgMember = Value("org-member")

  }

  def forName(name: String): UserRole = UserRole(Role.withName(name))
}
