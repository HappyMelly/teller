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

import models._
import securesocial.core.SecureSocial
import play.api.mvc._
import play.api.mvc.Results.Redirect
import be.objectify.deadbolt.scala.{ DynamicResourceHandler, DeadboltActions, DeadboltHandler }
import securesocial.core.SecuredRequest
import scala.concurrent.Future
import scala.util.matching.Regex

/**
 * Integrates SecureSocial authentication with Deadbolt.
 */
trait Security extends SecureSocial with DeadboltActions {

  /**
   * A redirect to the login page, used when authorisation fails due to a missing user account, e.g. because the user
   * removed their own account while logged in.
   */
  val MissingUserAccountResult = Future.successful(Redirect(securesocial.controllers.routes.LoginPage.logout))

  /**
   * Defines an action that authenticates using SecureSocial, and uses Deadbolt to restrict access to the given role.
   */
  def SecuredRestrictedAction(role: UserRole.Role.Role)(f: SecuredRequest[AnyContent] ⇒ AuthorisationHandler ⇒ SimpleResult): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒
      try {
        // Use the authenticated user’s account details to construct a handler (to look up account role) for Deadbolt authorisation.
        val account = request.user.asInstanceOf[LoginIdentity].userAccount
        val handler = new AuthorisationHandler(Some(account))
        val restrictedAction = Restrict(Array(role.toString), handler)(SecuredAction(f(_)(handler)))
        val result: Future[SimpleResult] = restrictedAction(request)
        result
      } catch {
        case _: NoSuchElementException ⇒ MissingUserAccountResult
      }
    }
  }

  /**
   * Defines an action that authenticates using SecureSocial, and uses Deadbolt to restrict access to the given role.
   */
  def SecuredDynamicAction(name: String, meta: String)(f: SecuredRequest[AnyContent] ⇒ AuthorisationHandler ⇒ SimpleResult): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒

      try {
        // Use the authenticated user’s account details to construct a handler (to look up account role) for Deadbolt authorisation.
        val account = request.user.asInstanceOf[LoginIdentity].userAccount
        val handler = new AuthorisationHandler(Some(account))
        val restrictedAction = Dynamic(name, meta, handler)(SecuredAction(f(_)(handler)))
        val result: Future[SimpleResult] = restrictedAction(request)
        result
      } catch {
        case _: NoSuchElementException ⇒ MissingUserAccountResult
      }
    }
  }

  /**
   * Async version of SecuredRestrictedAction
   */
  def AsyncSecuredRestrictedAction(role: UserRole.Role.Role)(f: SecuredRequest[AnyContent] ⇒ AuthorisationHandler ⇒ Future[SimpleResult]): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒

      try {
        // Use the authenticated user’s account details to construct a handler (to look up account role) for Deadbolt authorisation.
        val account = request.user.asInstanceOf[LoginIdentity].userAccount
        val handler = new AuthorisationHandler(Some(account))
        val restrictedAction = Restrict(Array(role.toString), handler)(SecuredAction.async(f(_)(handler)))
        restrictedAction(request)
      } catch {
        case _: NoSuchElementException ⇒ MissingUserAccountResult
      }
    }
  }

  /**
   * Defines an action that authenticates using SecureSocial, and uses Deadbolt to restrict access to the given role
   *
   * @param name Name of the object
   * @param level Access right level
   * @return
   */
  def AsyncSecuredDynamicAction(name: String, level: String)(f: SecuredRequest[AnyContent] ⇒ AuthorisationHandler ⇒ Future[SimpleResult]): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒

      try {
        // Use the authenticated user’s account details to construct a handler (to look up account role) for Deadbolt authorisation.
        val account = request.user.asInstanceOf[LoginIdentity].userAccount
        val handler = new AuthorisationHandler(Some(account))
        val restrictedAction = Dynamic(name, level, handler)(SecuredAction.async(f(_)(handler)))
        restrictedAction(request)
      } catch {
        case _: NoSuchElementException ⇒ MissingUserAccountResult
      }
    }
  }
}

/**
 * A security handler to check if a user is allowed to work with the specific objects.
 *
 * The system supports three roles - Viewer, Editor and Admin. The event module has its specific roles - Facilitator
 *  and Brand Coordinator.
 *
 *  A Brand Coordinator is able to create events for his/her own brand even if he/she is a Viewer.
 *  A Facilitator is able to create events for any brand he/she has active content licenses even if he/she is a Viewer.
 */
class TellerResourceHandler(account: Option[UserAccount]) extends DynamicResourceHandler {

  def isAllowed[A](name: String, meta: String, handler: DeadboltHandler, request: Request[A]) = {
    account.exists { existingAccount ⇒
      val userId = existingAccount.personId
      name match {
        case "evaluation" ⇒
          meta match {
            case "add" ⇒ existingAccount.coordinator || existingAccount.editor
            case "edit" ⇒
              val evaluationId = """\d+""".r findFirstIn request.uri
              existingAccount.editor || Evaluation.find(evaluationId.get.toLong).exists(_.event.canAdministrate(userId))
            case "manage" ⇒
              val evaluationId = """\d+""".r findFirstIn request.uri
              existingAccount.editor || Evaluation.find(evaluationId.get.toLong).exists(_.event.canFacilitate(userId))
            case _ ⇒ true
          }
        case "event" ⇒
          meta match {
            case "add" ⇒ existingAccount.facilitator || existingAccount.editor
            case "edit" ⇒
              val eventId = """\d+""".r findFirstIn request.uri
              existingAccount.editor || Event.find(eventId.get.toLong).exists(_.canFacilitate(userId))
            case "admin" ⇒
              val eventId = """\d+""".r findFirstIn request.uri
              existingAccount.editor || Event.find(eventId.get.toLong).exists(_.canAdministrate(userId))
            case _ ⇒ true
          }
        case "person" ⇒
          meta match {
            case "edit" ⇒
              val personId = """\d+""".r findFirstIn request.uri
              // A User should have an Editor role, it should be her own profile or he's a facilitator of the event
              //   where the person was a participant
              existingAccount.editor || personId.get.toLong == existingAccount.personId || {
                Person.find(personId.get.toLong).exists { person ⇒
                  if (person.virtual) {
                    person.participateInEvents(userId).nonEmpty
                  } else {
                    false
                  }
                }
              }
            case "delete" ⇒
              val personId = """\d+""".r findFirstIn request.uri
              // A User should have an Editor role or he's a facilitator of the event where the person was a participant
              existingAccount.editor || {
                Person.find(personId.get.toLong).exists { person ⇒
                  if (person.virtual) {
                    person.participateInEvents(existingAccount.personId).nonEmpty
                  } else {
                    false
                  }
                }
              }
            case _ ⇒ true
          }
        case "organisation" ⇒
          meta match {
            case "edit" ⇒
              val organisationId = """\d+""".r findFirstIn request.uri
              // A User should have an Editor role or should be a member of the organisation
              existingAccount.editor ||
                (organisationId.nonEmpty && Organisation.find(organisationId.get.toLong).exists {
                  _.members.find(_.id == Some(existingAccount.personId)).nonEmpty
                })
            case _ ⇒ true
          }
        case _ ⇒ false
      }
    }
  }

  def checkPermission[A](permissionValue: String, deadboltHandler: DeadboltHandler, request: Request[A]) = {
    false
  }

}
