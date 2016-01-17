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

import be.objectify.deadbolt.scala.DeadboltActions
import models.UserRole.DynamicRole
import models.service.Services
import models.{ActiveUser, UserAccount, UserRole}
import play.api.mvc._
import securesocial.core._

import scala.concurrent.Future


/**
 * Integrates SecureSocial authentication with Deadbolt.
 */
trait Security extends SecureSocial with DeadboltActions with Services with AsyncController {

  /**
   * A redirect to the login page, used when authorisation fails due to
   * a missing user account, e.g. because the user
   * removed their own account while logged in
   */
  val MissingUserAccountResult = Future.successful(
    Redirect(routes.LoginPage.logout()))

  /**
   * Authenticates using SecureSocial, and uses Deadbolt to restrict access to
   * the given role
   *
    * @deprecated Use Async version instead of this one
   * @param role Allowed role
   */
  def SecuredRestrictedAction(role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Result): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit handler => implicit user =>
      val restrictedAction = Restrict(
        Array(role.toString),
        handler)(Action(f(_)(handler)(user)))
      restrictedAction(request)
    }
  }

  /**
    * Authenticates using SecureSocial, and uses Deadbolt to restrict access to
    * the given role
    *
    * @deprecated Use Async version instead of this one
    * @param roles Allowed role
    */
  def SecuredRestrictedAction(roles: List[UserRole.Role.Role])(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Result): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit handler => implicit user =>
      val restrictedAction = Restrict(roles.map(x => Array(x.toString)), handler)(Action(f(_)(handler)(user)))
      restrictedAction(request)
    }
  }

  /**
   * Authenticates using SecureSocial
   * and uses Deadbolt to restrict access to the given role
   *
    * @param brandId Evaluation identifier
   */
  def SecuredBrandAction(brandId: Long)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser => Result): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit handler => implicit user =>
      val restrictedAction = Dynamic(DynamicRole.Coordinator, brandId.toString, handler)(
        Action(f(_)(handler)(user)))
      restrictedAction(request)
    }
  }

  /**
    * Authenticates using SecureSocial
    * and uses Deadbolt to restrict access to the given role
    *
    * @param brandId Evaluation identifier
    */
  def AsyncSecuredBrandAction(brandId: Long)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser => Future[Result]): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit handler => implicit user =>
      val restrictedAction = Dynamic(DynamicRole.Coordinator, brandId.toString, handler)(
        Action.async(f(_)(handler)(user)))
      restrictedAction(request)
    }
  }

  /**
    * Authenticates using SecureSocial
    * and uses Deadbolt to restrict access to the given role
    *
    * @param roles Allowed roles
    * @param evaluationId Evaluation identifier
    */
  def AsyncSecuredEvaluationAction(roles: List[UserRole.Role.Role], evaluationId: Long)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ models.Event => Future[Result]): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit handler => implicit user =>
      eventService.findByEvaluation(evaluationId) flatMap {
        case None => notFound("Evaluation not found")
        case Some(event) =>
          eventManager(user.account, roles, event) flatMap {
            case false => throw new AuthenticationException
            case true => f(request)(handler)(user)(event)
          }
      }
    }
  }

  /**
    * Authenticates using SecureSocial
    * and uses Deadbolt to restrict access to the given role
    *
    * @param roles Allowed roles
    * @param eventId Event identifier
    */
  def AsyncSecuredEventAction(roles: List[UserRole.Role.Role], eventId: Long)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ models.Event => Future[Result]): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit handler => implicit user =>
      eventService.find(eventId) flatMap {
        case None => notFound("Event not found")
        case Some(event) =>
          eventManager(user.account, roles, event) flatMap {
            case false => throw new AuthenticationException
            case true => f(request)(handler)(user)(event)
          }
      }
    }
  }

  /**
    * Authenticates using SecureSocial
    * and uses Deadbolt to restrict access to the given role
    *
    * @param personId Person identifier
    */
  def AsyncSecuredProfileAction(personId: Long)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser => Future[Result]): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit handler => implicit user =>
      val restrictedAction = Dynamic(DynamicRole.ProfileEditor, personId.toString, handler)(
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
  def SecuredProfileAction(personId: Long)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser => Result): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit handler => implicit user =>
      val restrictedAction = Dynamic(DynamicRole.ProfileEditor, personId.toString, handler)(Action(f(_)(handler)(user)))
      restrictedAction(request)
    }
  }

  /**
   * Asynchronously authenticates using SecureSocial, and uses Deadbolt
   * to restrict access to the given role
   *
   * @param role Allowed role
   */
  def AsyncSecuredRestrictedAction(role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Future[Result]): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit handler => implicit user =>
      val restrictedAction = Restrict(
        Array(role.toString),
        handler)(Action.async(f(_)(handler)(user)))
      restrictedAction(request)
    }
  }

  /**
    * Asynchronously authenticates using SecureSocial, and uses Deadbolt
    * to restrict access to the given role
    *
    * @param roles Allowed roles
    */
  def AsyncSecuredRestrictedAction(roles: List[UserRole.Role.Role])(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Future[Result]): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit handler => implicit user =>
      val restrictedAction = Restrict(roles.map(x => Array(x.toString)), handler)(Action.async(f(_)(handler)(user)))
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
  def AsyncSecuredDynamicAction(role: String, id: Long)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Future[Result]): Action[AnyContent] = {
    asyncSecuredAction() { implicit request => implicit handler => implicit user =>
      val restrictedAction = Dynamic(role, id.toString, handler)(Action.async(f(_)(handler)(user)))
      restrictedAction(request)
    }
  }

  protected def eventManager(account: UserAccount,
                             roles: List[UserRole.Role.Role], event: models.Event): Future[Boolean] = {
    if (account.isCoordinatorNow && roles.contains(UserRole.Role.Coordinator))
      brandService.isCoordinator(event.brandId, account.personId)
    else if (account.isFacilitatorNow && roles.contains(UserRole.Role.Facilitator))
      Future.successful(event.isFacilitator(account.personId))
    else
      Future.successful(false)
  }

  protected def asyncSecuredAction()(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser => Future[Result]): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒
      request.user match {
        case user: ActiveUser ⇒
          val handler = new AuthorisationHandler(user)
          try {
            f(request)(handler)(user)
          } catch {
            case _: AuthenticationException => handler.onAuthFailure(request)
          }
        case _ ⇒ MissingUserAccountResult
      }
    }
  }

  protected def securedAction()(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser => Result): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒
      println(request)
      request.user match {
        case user: ActiveUser ⇒
          val handler = new AuthorisationHandler(user)
          try {
            Future.apply(f(request)(handler)(user))
          } catch {
            case _: AuthenticationException => handler.onAuthFailure(request)
          }
        case _ ⇒ MissingUserAccountResult
      }
    }
  }

}

