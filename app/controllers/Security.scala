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

package controllers

import models.{ UserAccount, LoginIdentity, UserRole }
import securesocial.core.{ SecureSocial, SecuredRequest }
import play.api.mvc.{ Action, Result, AnyContent }
import be.objectify.deadbolt.scala.DeadboltActions

/**
 * Integrates SecureSocial authentication with Deadbolt.
 */
trait Security extends SecureSocial with DeadboltActions {

  /**
   * Defines an action that authenticates using SecureSocial, and uses Deadbolt to restrict access to the given role.
   */
  def SecuredRestrictedAction(role: UserRole.Role.Role)(f: SecuredRequest[AnyContent] ⇒ AuthorisationHandler ⇒ Result): Action[AnyContent] = {
    SecuredAction { request ⇒

      // Look-up the authenticated user’s account details.
      val twitterHandle = request.user.asInstanceOf[LoginIdentity].twitterHandle
      val account = UserAccount.findByTwitterHandle(twitterHandle)

      // Use the account details to construct a handler (to look up account role) for Deadbolt authorisation.
      val handler = new AuthorisationHandler(account)
      val restrictedAction = Restrict(Array(role.toString), handler)(SecuredAction(f(_)(handler)))
      restrictedAction(request)
    }
  }
}