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
package stubs

import securesocial.core.{AuthenticationMethod, BasicProfile}

class FakeUserIdentity(
  override val uid: Option[Long],
  identity: (String, String),
  firstName: String,
  lastName: String,
  fullName: String,
  email: Option[String]) extends models.UserIdentity(uid,
  BasicProfile(identity._2, identity._1, Some(firstName), Some(lastName),
    Some(fullName), email, None, AuthenticationMethod.OAuth2, None,
    None, None), "api_token", None, None, None, None) {

}

object FakeUserIdentity {
  val unregistered: (String, String) = ("unregistered", "twitter")
  val viewer: (String, String) = ("viewer", "twitter")
  val coordinator: (String, String) = ("coordinator", "twitter")
  val facilitator: (String, String) = ("facilitator", "twitter")
  val editor: (String, String) = ("viewer", "twitter")
  val admin: (String, String) = ("admin", "twitter")
}
