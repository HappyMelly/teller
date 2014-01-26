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
import securesocial.core.{ Identity, SecureSocial, SecuredRequest, Authorization }
import play.api.mvc._
import be.objectify.deadbolt.scala.{ DynamicResourceHandler, DeadboltActions, DeadboltHandler }
import securesocial.core.SecuredRequest
import scala.concurrent.Future
import play.Logger

/**
 * Integrates SecureSocial authentication with Deadbolt.
 */
trait Security extends SecureSocial with DeadboltActions {

  /**
   * Defines an action that authenticates using SecureSocial, and uses Deadbolt to restrict access to the given role.
   */
  def SecuredRestrictedAction(role: UserRole.Role.Role)(f: SecuredRequest[AnyContent] ⇒ AuthorisationHandler ⇒ SimpleResult): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒

      // Look-up the authenticated user’s account details.
      val twitterHandle = request.user.asInstanceOf[LoginIdentity].twitterHandle
      val account = UserAccount.findByTwitterHandle(twitterHandle)

      // Use the account details to construct a handler (to look up account role) for Deadbolt authorisation.
      val handler = new AuthorisationHandler(account)
      val restrictedAction = Restrict(Array(role.toString), handler)(SecuredAction(f(_)(handler)))
      val result: Future[SimpleResult] = restrictedAction(request)
      result
    }
  }

  /**
   * Defines an action that authenticates using SecureSocial, and uses Deadbolt to restrict access to the given role.
   */
  def SecuredDynamicAction(name: String, meta: String)(f: SecuredRequest[AnyContent] ⇒ AuthorisationHandler ⇒ SimpleResult): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒

      // Look-up the authenticated user’s account details.
      val twitterHandle = request.user.asInstanceOf[LoginIdentity].twitterHandle
      val account = UserAccount.findByTwitterHandle(twitterHandle)

      // Use the account details to construct a handler (to look up account role) for Deadbolt authorisation.
      val handler = new AuthorisationHandler(account)
      val restrictedAction = Dynamic(name, meta, handler)(SecuredAction(f(_)(handler)))
      val result: Future[SimpleResult] = restrictedAction(request)
      result
    }
  }

  /**
   * Async version of SecuredRestrictedAction
   */
  def AsyncSecuredRestrictedAction(role: UserRole.Role.Role)(f: SecuredRequest[AnyContent] ⇒ AuthorisationHandler ⇒ Future[SimpleResult]): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒

      // Look-up the authenticated user’s account details.
      val twitterHandle = request.user.asInstanceOf[LoginIdentity].twitterHandle
      val account = UserAccount.findByTwitterHandle(twitterHandle)

      // Use the account details to construct a handler (to look up account role) for Deadbolt authorisation.
      val handler = new AuthorisationHandler(account)
      val restrictedAction = Restrict(Array(role.toString), handler)(SecuredAction.async(f(_)(handler)))
      restrictedAction(request)
    }
  }
}

class FacilitatorResourceHandler(account: Option[UserAccount]) extends DynamicResourceHandler {

  def isAllowed[A](name: String, meta: String, handler: DeadboltHandler, request: Request[A]) = {
    if (name == "event" && account.isDefined) {
      if (meta == "edit") {
        account.get.isFacilitator || UserRole.forName(account.get.role).editor
      } else {
        true
      }
    } else
      false
  }

  def checkPermission[A](permissionValue: String, deadboltHandler: DeadboltHandler, request: Request[A]) = {
    false
  }
}