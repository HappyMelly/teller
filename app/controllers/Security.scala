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

package controllers

import _root_.security.HandlerKeys
import _root_.services.TellerRuntimeEnvironment
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions, DeadboltHandler}
import models.UserRole.Role._
import models.repository.IRepositories
import models.{ActiveUser, UserRole}
import play.api.i18n.MessagesApi
import play.api.mvc._
import securesocial.controllers.MailTokenBasedOperations
import securesocial.core._

import scala.concurrent.Future

/**
 * Integrates SecureSocial authentication with Deadbolt.
 */
class Security(deadbolt: DeadboltActions,
               handlers: HandlerCache,
               actionBuilder: ActionBuilders,
               repos: IRepositories)
              (val messagesApi: MessagesApi, override implicit val env: TellerRuntimeEnvironment)
  extends MailTokenBasedOperations with AsyncController {

  val handler = handlers(HandlerKeys.defaultHandler)

  /**
   * A redirect to the login page, used when authorisation fails due to
   * a missing user account, e.g. because the user
   * removed their own account while logged in
   */
  val MissingUserAccountResult = Future.successful(
    Redirect(core.routes.LoginPage.logout()))

  /**
    * Authenticates using SecureSocial
    * and uses Deadbolt to restrict access to the given role
    *
    * @param brandId Evaluation identifier
    */
  def BrandAction(brandId: Long)(
    f: Request[AnyContent] ⇒ DeadboltHandler ⇒ ActiveUser => Future[Result]): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit user =>
      val restrictedAction = deadbolt.Dynamic(Coordinator.toString, brandId.toString, handler)(
        Action.async(f(_)(handler)(user)))
      restrictedAction(request)
    }
  }

  /**
    * Authenticates using SecureSocial
    * and uses Deadbolt to restrict access to the given role
    *
    * @param personId Person identifier
    */
  def ProfileAction(personId: Long)(
    f: Request[AnyContent] ⇒ DeadboltHandler ⇒ ActiveUser => Future[Result]): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit user =>
      val restrictedAction = deadbolt.Dynamic(ProfileEditor.toString, personId.toString, handler)(
        Action.async(f(_)(handler)(user)))
      restrictedAction(request)
    }
  }

  /**
    * Asynchronously authenticates using SecureSocial, and uses Deadbolt
    * to restrict access to the given role
    *
    * @param role Allowed role
    */
  def RestrictedAction(role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ DeadboltHandler ⇒ ActiveUser ⇒ Future[Result]): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit user =>
      val handler = handlers(HandlerKeys.defaultHandler)
      val restrictedAction = deadbolt.Restrict(Array(role.toString), handler)(Action.async(f(_)(handler)(user)))
      restrictedAction(request)
    }
  }


  /**
    * Asynchronously authenticates using SecureSocial, and uses Deadbolt
    * to restrict access to the given role
    *
    * @param roles Allowed roles
    */
  def RestrictedAction(roles: List[UserRole.Role.Role])(
    f: Request[AnyContent] ⇒ DeadboltHandler ⇒ ActiveUser ⇒ Future[Result]): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit user =>
      val restrictedAction = deadbolt.Restrict(roles.map(x => Array(x.toString)),
        handler = handlers(HandlerKeys.defaultHandler))(Action.async(f(_)(handler)(user)))
      restrictedAction(request)
    }
  }

  /**
    * Asynchronously authenticates using SecureSocial and uses Deadbolt to
    * restrict access to the given role
    *
    * @param role Role name
    * @param id Object identifier
    * @return
    */
  def DynamicAction(role: UserRole.Role.Role, id: Long)(
    f: Request[AnyContent] ⇒ DeadboltHandler ⇒ ActiveUser ⇒ Future[Result]): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit user =>
      val restrictedAction = deadbolt.Dynamic(role.toString, id.toString, handler)(Action.async(f(_)(handler)(user)))
      restrictedAction(request)
    }
  }

  protected def asyncSecuredAction()(f: Request[AnyContent] ⇒ ActiveUser => Future[Result]): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒
      request.user match {
        case user: ActiveUser ⇒
          try {
            f(request)(user)
          } catch {
            case _: AuthenticationException => handler.onAuthFailure(request)
          }
        case _ ⇒ MissingUserAccountResult
      }
    }
  }

}

