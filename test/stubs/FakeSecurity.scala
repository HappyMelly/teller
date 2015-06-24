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

import controllers.{ AuthorisationHandler, Security }
import models.{ UserIdentity, UserRole, Person }
import play.api.mvc.{ Action, SimpleResult, AnyContent, Request }
import securesocial.core.SecuredRequest
import scala.concurrent.Future

trait FakeSecurity extends Security {

  /** Used to replace user object which is passed to action */
  private var _activeUser: Option[Person] = None

  def activeUser_=(user: Person) = _activeUser = Some(user)

  override def SecuredRestrictedAction(role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ UserIdentity ⇒ SimpleResult): Action[AnyContent] = {
    Action.async { implicit req ⇒
      val identity = new FakeUserIdentity(Some(123213L), FakeUserIdentity.viewer,
        "Sergey", "Kotlov", "Sergey Kotlov", None, _activeUser)
      val request: SecuredRequest[AnyContent] = SecuredRequest(identity, req)
      val user: UserIdentity = request.user.asInstanceOf[UserIdentity]
      val handler = new AuthorisationHandler(Some(user.account))
      Action(f(_)(handler)(user))(SecuredRequest(identity, request))
    }
  }

  override def AsyncSecuredRestrictedAction(role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ UserIdentity ⇒ Future[SimpleResult]): Action[AnyContent] = {
    Action.async { implicit req ⇒
      val identity = new FakeUserIdentity(Some(123213L), FakeUserIdentity.viewer,
        "Sergey", "Kotlov", "Sergey Kotlov", None, _activeUser)
      val request: SecuredRequest[AnyContent] = SecuredRequest(identity, req)
      val user: UserIdentity = request.user.asInstanceOf[UserIdentity]
      val handler = new AuthorisationHandler(Some(user.account))
      Action.async(f(_)(handler)(user))(SecuredRequest(identity, request))
    }
  }
}
