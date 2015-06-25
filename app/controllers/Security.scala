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

import models.{ DynamicResourceChecker, UserAccount, UserIdentity, UserRole }
import models.UserRole.DynamicRole
import models.service.Services
import securesocial.core._
import play.api.mvc._
import be.objectify.deadbolt.scala.{ DynamicResourceHandler, DeadboltActions, DeadboltHandler }
import scala.concurrent.Future

/**
 * Integrates SecureSocial authentication with Deadbolt.
 */
trait Security extends SecureSocial with DeadboltActions {

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
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ UserIdentity ⇒ SimpleResult): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒
      request.user match {
        case user: UserIdentity ⇒
          try {
            // Use the authenticated user’s account details to construct a handler
            // (to look up account role) for Deadbolt authorisation
            val handler = new AuthorisationHandler(user)
            val restrictedAction = Restrict(
              Array(role.toString),
              handler)(Action(f(_)(handler)(user)))
            val result: Future[SimpleResult] = restrictedAction(request)
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
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ UserIdentity ⇒ SimpleResult): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒
      request.user match {
        case user: UserIdentity ⇒
          try {
            // Use the authenticated user’s account details to construct
            // a handler (to look up account role) for Deadbolt authorisation
            val handler = new AuthorisationHandler(user)
            val restrictedAction = Dynamic(name, meta, handler)(Action(f(_)(handler)(user)))
            val result: Future[SimpleResult] = restrictedAction(request)
            result
          } catch {
            case _: NoSuchElementException ⇒ MissingUserAccountResult
          }
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
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ UserIdentity ⇒ Future[SimpleResult]): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒
      request.user match {
        case user: UserIdentity ⇒
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
    f: Request[AnyContent] ⇒ AuthorisationHandler ⇒ UserIdentity ⇒ Future[SimpleResult]): Action[AnyContent] = {
    SecuredAction.async { implicit request ⇒
      request.user match {
        case user: UserIdentity ⇒
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
}

/**
 * A security handler to check if a user is allowed to work with the specific objects.
 *
 * The system supports three roles - Viewer, Editor and Admin.
 */
class TellerResourceHandler(identity: UserIdentity)
  extends DynamicResourceHandler
  with Services {

  def isAllowed[A](name: String, meta: String, handler: DeadboltHandler, request: Request[A]) = {
    val userId = identity.account.personId
    name match {
      case "brand" ⇒ checkBrandPermission(identity.account, meta, request.uri)
      case "evaluation" ⇒ checkEvaluationPermission(identity.account, meta, request.uri)
      case "event" ⇒ checkEventPermission(identity.account, meta, request.uri)
      case "member" ⇒ checkMemberPermission(identity, request.uri)
      case "person" ⇒
        meta match {
          case "edit" ⇒
            val personId = """\d+""".r findFirstIn request.uri
            // A User should have an Editor role, it should be her own profile or he's a facilitator of the event
            //   where the person was a participant
            identity.account.editor || personId.get.toLong == userId || {
              personService.find(personId.get.toLong).exists { person ⇒
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
            identity.account.editor || {
              personService.find(personId.get.toLong).exists { person ⇒
                if (person.virtual) {
                  person.participateInEvents(identity.account.personId).nonEmpty
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
            identity.account.editor ||
              (organisationId.nonEmpty && orgService.find(organisationId.get.toLong).exists {
                _.people.find(_.id == Some(identity.account.personId)).nonEmpty
              })
          case _ ⇒ true
        }
      case _ ⇒ false
    }
  }

  def checkPermission[A](permissionValue: String, deadboltHandler: DeadboltHandler, request: Request[A]) = {
    false
  }

  /**
   * Returns true if the given user is allowed to execute an evaluation-related action
   * @param account User account
   * @param meta Action identifier
   * @param url Request url
   */
  protected def checkEvaluationPermission(account: UserAccount, meta: String, url: String): Boolean = {
    val userId = account.personId
    meta match {
      case "add" ⇒ account.editor || account.coordinator
      case DynamicRole.Coordinator ⇒
        id(url) exists { evaluationId ⇒
          checker(account).isEvaluationCoordinator(evaluationId)
        }
      case DynamicRole.Facilitator ⇒
        id(url) exists { evaluationId ⇒
          checker(account).isEvaluationFacilitator(evaluationId)
        }
      case _ ⇒ false
    }
  }

  /**
   * Returns true if the given user is allowed to execute an event-related action
   * @param account User account
   * @param meta Action identifier
   * @param url Request url
   */
  protected def checkEventPermission(account: UserAccount, meta: String, url: String): Boolean = {
    val userId = account.personId
    meta match {
      case "add" ⇒ account.editor || account.facilitator || account.coordinator
      case DynamicRole.Facilitator ⇒
        id(url) exists { eventId ⇒ checker(account).isEventFacilitator(eventId) }
      case DynamicRole.Coordinator ⇒
        id(url) exists { eventId ⇒ checker(account).isEventCoordinator(eventId) }
      case _ ⇒ false
    }
  }

  /**
   * Returns true if the given user is allowed to execute a brand-related action
   * @param account User account
   * @param meta Action identifier
   * @param url Request url
   */
  protected def checkBrandPermission(account: UserAccount, meta: String, url: String): Boolean = {
    meta match {
      case DynamicRole.Coordinator ⇒
        id(url) exists { brandId ⇒ checker(account).isBrandCoordinator(brandId) }
      case _ ⇒ false
    }
  }

  /**
   * Returns true if the given user is allowed to execute a member-related action
   * @param identity User identity
   * @param url Request url
   */
  protected def checkMemberPermission(identity: UserIdentity, url: String): Boolean = {
    id(url) exists { memberId ⇒
      if (identity.person.member.exists(_.id == Some(memberId)))
        true
      else
        memberService.find(memberId) exists { member ⇒
          if (member.person)
            false
          else
            orgService.people(member.objectId).exists(_.id == Some(identity.account.personId))
        }
    }
  }

  /**
   * Returns the first long number from the given url
   * @param url Url
   */
  protected def id(url: String): Option[Long] = """\d+""".r findFirstIn url flatMap { x ⇒
    try { Some(x.toLong) } catch { case _: NumberFormatException ⇒ None }
  }

  /**
   * Returns new resource checker
   *
   * @param account User account
   */
  protected def checker(account: UserAccount): DynamicResourceChecker =
    new DynamicResourceChecker(account)

}
