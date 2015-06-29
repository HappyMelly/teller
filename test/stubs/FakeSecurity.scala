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

  override def AsyncSecuredRestrictedAction(role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ UserIdentity ⇒ Future[SimpleResult]): Action[AnyContent] = {
    Action.async { implicit req ⇒
      val request: SecuredRequest[AnyContent] = SecuredRequest(viewer, req)
      val user: UserIdentity = request.user.asInstanceOf[UserIdentity]
      val handler = new AuthorisationHandler(user)
      Action.async(f(_)(handler)(user))(SecuredRequest(viewer, request))
    }
  }

  override def AsyncSecuredDynamicAction(name: String, level: String)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ UserIdentity ⇒ Future[SimpleResult]): Action[AnyContent] = {
    Action.async { implicit req ⇒
      val request: SecuredRequest[AnyContent] = SecuredRequest(viewer, req)
      val user: UserIdentity = request.user.asInstanceOf[UserIdentity]
      val handler = new AuthorisationHandler(user)
      Action.async(f(_)(handler)(user))(SecuredRequest(viewer, request))
    }
  }

  override def SecuredDynamicAction(name: String, level: String)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ UserIdentity ⇒ SimpleResult): Action[AnyContent] = {
    Action.async { implicit req ⇒
      val request: SecuredRequest[AnyContent] = SecuredRequest(viewer, req)
      val user: UserIdentity = request.user.asInstanceOf[UserIdentity]
      val handler = new AuthorisationHandler(user)
      Action(f(_)(handler)(user))(SecuredRequest(viewer, request))
    }
  }

  override def SecuredRestrictedAction(role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ UserIdentity ⇒ SimpleResult): Action[AnyContent] = {
    Action.async { implicit req ⇒
      val request: SecuredRequest[AnyContent] = SecuredRequest(viewer, req)
      val user: UserIdentity = request.user.asInstanceOf[UserIdentity]
      val handler = new AuthorisationHandler(user)
      Action(f(_)(handler)(user))(SecuredRequest(viewer, request))
    }
  }

  private def viewer: UserIdentity = new FakeUserIdentity(Some(123213L),
    FakeUserIdentity.viewer, "Sergey", "Kotlov", "Sergey Kotlov", None, _activeUser)
}

/**
 * This trait is used only to record which security actions were called.
 */
trait AccessCheckSecurity extends Security {

  var checkedRole: Option[UserRole.Role.Role] = None
  var checkedDynamicObject: Option[String] = None
  var checkedDynamicLevel: Option[String] = None

  override def AsyncSecuredDynamicAction(name: String, level: String)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ UserIdentity ⇒ Future[SimpleResult]): Action[AnyContent] = {
    cleanTrace()
    checkedDynamicObject = Some(name)
    checkedDynamicLevel = Some(level)
    Action({ Ok("") })
  }

  override def AsyncSecuredRestrictedAction(role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ UserIdentity ⇒ Future[SimpleResult]): Action[AnyContent] = {
    cleanTrace()
    checkedRole = Some(role)
    Action({ Ok("") })
  }

  override def SecuredDynamicAction(name: String, level: String)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ UserIdentity ⇒ SimpleResult): Action[AnyContent] = {
    cleanTrace()
    checkedDynamicObject = Some(name)
    checkedDynamicLevel = Some(level)
    Action({ Ok("") })
  }

  override def SecuredRestrictedAction(role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ UserIdentity ⇒ SimpleResult): Action[AnyContent] = {
    cleanTrace()
    checkedRole = Some(role)
    Action({ Ok("") })
  }

  /** Clean side-effects of previous calls */
  private def cleanTrace(): Unit = {
    checkedRole = None
    checkedDynamicLevel = None
    checkedDynamicObject = None
  }
}