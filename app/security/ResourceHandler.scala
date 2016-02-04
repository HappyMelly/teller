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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package security

import be.objectify.deadbolt.scala.{DeadboltHandler, DynamicResourceHandler}
import models.{UserRole, ActiveUser}
import models.UserRole.Role._
import models.service.Services
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * A security handler to check if a user is allowed to work with the specific objects.
 *
 * The system supports three roles - Viewer, Editor and Admin.
 */
class ResourceHandler(user: ActiveUser) extends DynamicResourceHandler with Services {

  def isAllowed[A](name: String, meta: String, handler: DeadboltHandler, request: Request[A]) = {
    val objectId = meta.toLong
    UserRole.forName(name) match {
      case Coordinator => brandService.isCoordinator(objectId, user.account.personId)
      case Member ⇒ checkMemberPermission(user, objectId)
      case Funder => Future.successful(user.account.admin || user.member.exists(_.funder))
      case ProfileEditor => Future.successful(user.account.admin || user.account.personId == objectId)
      case OrgMember => orgMember(objectId, user.account.personId).map(_ || user.account.admin)
      case _ ⇒ Future.successful(false)
    }
  }

  def checkPermission[A](permissionValue: String, deadboltHandler: DeadboltHandler, request: Request[A]) = {
    Future.successful(false)
  }

  /**
   * Returns true if the given user is allowed to execute a member-related action
    *
    * @param user Active user
    * @param memberId Member id to check
   */
  protected def checkMemberPermission(user: ActiveUser, memberId: Long): Future[Boolean] = {
    if (user.account.admin || user.person.member.exists(_.identifier == memberId))
      Future.successful(true)
    else
      memberService.find(memberId) flatMap {
        case None => Future.successful(false)
        case Some(member) =>
          if (member.person)
            Future.successful(false)
          else
            orgMember(member.objectId, user.account.personId)
      }
  }

  /**
    * Returns true if the given person is a member of the given organisation
    *
    * @param orgId Organisation identifier
    * @param personId Person identifier
    */
  private def orgMember(orgId: Long, personId: Long): Future[Boolean] =
    orgService.people(orgId) map { people =>
      people.exists(_.identifier == personId)
    }
}