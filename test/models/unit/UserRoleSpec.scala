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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models.unit

import models.UserRole
import org.specs2.mutable.Specification

class UserRoleSpec extends Specification {

  "User" should {
    "not be Editor, Admin or Unregistered if she is a Viewer" in {
      val role = new UserRole(UserRole.Role.Viewer)
      role.viewer must_== true
      role.editor must_== false
      role.admin must_== false
      role.unregistered must_== true
    }
    "not be Admin or Unregistered if he is an Editor" in {
      val role = new UserRole(UserRole.Role.Editor)
      role.viewer must_== true
      role.editor must_== true
      role.admin must_== false
      role.unregistered must_== true
    }
    "not be Unregistered if he is an Admin" in {
      val role = new UserRole(UserRole.Role.Admin)
      role.viewer must_== true
      role.editor must_== true
      role.admin must_== true
      role.unregistered must_== true
    }
    "not be Editor, Admin or Viewer if she is unregistered" in {
      val role = new UserRole(UserRole.Role.Unregistered)
      role.viewer must_== false
      role.editor must_== false
      role.admin must_== false
      role.unregistered must_== true
    }
  }
  "List of roles" should {
    "contain Viewer and Unregistered role if a user is a Viewer" in {
      val role = new UserRole(UserRole.Role.Viewer)
      role.list.length must_== 2
      role.list.exists(_.viewer == true) must_== true
      role.list.exists(_.unregistered == true) must_== true
    }
    "contain Unregistered, Viewer and Editor roles if a user is an Editor" in {
      val role = new UserRole(UserRole.Role.Editor)
      role.list.length must_== 3
      role.list.exists(_.editor == true) must_== true
      role.list.exists(_.admin == true) must_== false
      role.list.exists(_.unregistered == true) must_== true
    }
    "contain Unregistered, Viewer, Editor and Admin roles if a user is an Admin" in {
      val role = new UserRole(UserRole.Role.Admin)
      role.list.length must_== 4
      role.list.exists(_.admin == true) must_== true
      role.list.exists(_.unregistered == true) must_== true
    }
    "contain only Unregistered role if a user is unregistered" in {
      val role = new UserRole(UserRole.Role.Unregistered)
      role.list.length must_== 1
      role.list.exists(_.unregistered == true) must_== true
    }
  }
}
