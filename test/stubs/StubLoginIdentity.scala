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
package stubs

import helpers.PersonHelper
import models.{ UserRole, UserAccount }
import securesocial.core.AuthenticationMethod
import securesocial.core.IdentityId

class StubLoginIdentity(
  override val uid: Option[Long],
  override val identityId: IdentityId,
  override val firstName: String,
  override val lastName: String,
  override val fullName: String,
  override val email: Option[String]) extends models.LoginIdentity(uid, identityId,
  firstName, lastName, fullName, email, None, AuthenticationMethod.OAuth2, None,
  None, None, "123", None, None, None, None) {

  override def person = PersonHelper.one()

  override def userAccount = {
    val account = new UserAccount(Some(1L), person.id.get, "viewer",
      None, None, None, None)
    account.roles_=(UserRole.forName("viewer").list)
    account
  }

}
