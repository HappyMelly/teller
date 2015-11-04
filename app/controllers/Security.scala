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
import models.service.Services
import models.{ActiveUser, UserRole}
import play.api.mvc._
import securesocial.core._

import scala.concurrent.Future

/**
 * Integrates SecureSocial authentication with Deadbolt.
 */
trait Security extends SecureSocial[ActiveUser]
  with DeadboltActions
  with Services {

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
   * @param role Allowed role
   */
  def SecuredRestrictedAction(role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Result): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒
      request.user match {
        case user: ActiveUser ⇒
          try {
            // Use the authenticated user’s account details to construct a handler
            // (to look up account role) for Deadbolt authorisation
            val handler = new AuthorisationHandler(user)
            val restrictedAction = Restrict(
              Array(role.toString),
              handler)(Action(f(_)(handler)(user)))
            val result: Future[Result] = restrictedAction(request)
            result
          } catch {
            case _: NoSuchElementException ⇒ MissingUserAccountResult
          }
        case _ ⇒ MissingUserAccountResult
      }
    }
  }

  /**
   * Authenticates using SecureSocial
   * and uses Deadbolt to restrict access to the given role
   *
   * @param name Object name
   * @param meta Access level description
   */
  def SecuredDynamicAction(name: String, meta: String)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Result): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒
      request.user match {
        case user: ActiveUser ⇒
          try {
            // Use the authenticated user’s account details to construct
            // a handler (to look up account role) for Deadbolt authorisation
            val handler = new AuthorisationHandler(user)
            val restrictedAction = Dynamic(name, meta, handler)(Action(f(_)(handler)(user)))
            val result: Future[Result] = restrictedAction(request)
            result
          } catch {
            case _: NoSuchElementException ⇒ MissingUserAccountResult
          }
        case _ ⇒ MissingUserAccountResult
      }
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
    SecuredAction.async { implicit request ⇒
      request.user match {
        case user: ActiveUser ⇒
          val userId = user.account.personId
          val handler = new AuthorisationHandler(user)
          if (brandService.isCoordinator(brandId, userId))
            Future.apply(f(request)(handler)(user))
          else
            handler.onAuthFailure(request)
        case _ ⇒ MissingUserAccountResult
      }
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
    SecuredAction.async { implicit request ⇒
      request.user match {
        case user: ActiveUser ⇒
          val userId = user.account.personId
          val handler = new AuthorisationHandler(user)
          if (brandService.isCoordinator(brandId, userId))
            f(request)(handler)(user)
          else
            handler.onAuthFailure(request)
        case _ ⇒ MissingUserAccountResult
      }
    }
  }

  /**
   * Authenticates using SecureSocial
   * and uses Deadbolt to restrict access to the given role
   *
   * @param evaluationId Evaluation identifier
   */
  def SecuredEvaluationAction(evaluationId: Long, role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ models.Event => Result): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒
      request.user match {
        case user: ActiveUser ⇒
          val userId = user.account.personId
          val handler = new AuthorisationHandler(user)
          eventService.findByEvaluation(evaluationId).map { event ⇒
            if (eventManager(userId, role, event))
              Future.apply(f(request)(handler)(user)(event))
            else
              handler.onAuthFailure(request)
          } getOrElse Future.successful(NotFound)
        case _ ⇒ MissingUserAccountResult
      }
    }
  }

  /**
   * Authenticates using SecureSocial
   * and uses Deadbolt to restrict access to the given role
   *
   * @param eventId Event identifier
   */
  def SecuredEventAction(eventId: Long, role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ models.Event => Result): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒
      request.user match {
        case user: ActiveUser ⇒
          val userId = user.account.personId
          val handler = new AuthorisationHandler(user)
          eventService.find(eventId).map { event ⇒
            if (eventManager(userId, role, event))
              Future.apply(f(request)(handler)(user)(event))
            else
              handler.onAuthFailure(request)
          } getOrElse Future.successful(NotFound)
        case _ ⇒ MissingUserAccountResult
      }
    }
  }

  /**
   * Authenticates using SecureSocial
   * and uses Deadbolt to restrict access to the given role
   *
   * @param eventId Event identifier
   */
  def AsyncSecuredEventAction(eventId: Long, role: UserRole.Role.Role)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ models.Event => Future[Result]): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒
      request.user match {
        case user: ActiveUser ⇒
          val userId = user.account.personId
          val handler = new AuthorisationHandler(user)
          eventService.find(eventId).map { event ⇒
            if (eventManager(userId, role, event))
              f(request)(handler)(user)(event)
            else
              handler.onAuthFailure(request)
          } getOrElse Future.successful(NotFound)
        case _ ⇒ MissingUserAccountResult
      }
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
    SecuredAction.async { implicit request ⇒
      request.user match {
        case user: ActiveUser ⇒
          try {
            // Use the authenticated user’s account details to construct
            // a handler (to look up account role) for Deadbolt authorisation.
            val handler = new AuthorisationHandler(user)
            val restrictedAction = Restrict(
              Array(role.toString),
              handler)(Action.async(f(_)(handler)(user)))
            restrictedAction(request)
          } catch {
            case _: NoSuchElementException ⇒ MissingUserAccountResult
          }
        case _ ⇒ MissingUserAccountResult
      }
    }
  }

  /**
   * Asynchronously authenticates using SecureSocial and uses Deadbolt to
   * restrict access to the given role
   *
   * @param name Name of the object
   * @param level Access right level
   * @return
   */
  def AsyncSecuredDynamicAction(name: String, level: String)(
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ ActiveUser ⇒ Future[Result]): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒
      request.user match {
        case user: ActiveUser ⇒
          try {
            // Use the authenticated user’s account details to construct
            // a handler (to look up account role) for Deadbolt authorisation.
            val handler = new AuthorisationHandler(user)
            val restrictedAction = Dynamic(name, level, handler)(Action.async(f(_)(handler)(user)))
            restrictedAction(request)
          } catch {
            case _: NoSuchElementException ⇒ MissingUserAccountResult
          }
        case _ ⇒ MissingUserAccountResult
      }
    }
  }

  protected def eventManager(userId: Long,
                             role: UserRole.Role.Role,
                             event: models.Event): Boolean = {
    val facilitator = event.isFacilitator(userId)
    if (role == UserRole.Role.Coordinator)
      facilitator || brandService.isCoordinator(event.brandId, userId)
    else
      facilitator
  }


}

