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

import _root_.services.LoginIdentityService
import helpers.PersonHelper
import models.{ UserAccount, ActiveUser }
import securesocial.core.BasicProfile
import securesocial.core.services.SaveMode
import scala.concurrent.Future

class StubLoginIdentityService extends LoginIdentityService {

  override def find(providerId: String, userId: String) = {
    val identity = new FakeUserIdentity(Some(123213L), (providerId, userId),
      "Sergey", "Kotlov", "Sergey Kotlov", None)
    Future.successful(Some(identity.profile))
  }

  override def save(profile: BasicProfile, mode: SaveMode): Future[ActiveUser] = {
    val identity = new FakeUserIdentity(Some(123213L), ("123", "twitter"),
      "Sergey", "kotlov", "Sergey Kotlov", None)
    val account = UserAccount(Some(1L), 1L, "viewer", None, None, None, None)
    val person = PersonHelper.one()
    Future.successful(ActiveUser(identity, account, person))
  }

}
