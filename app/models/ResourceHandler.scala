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
package models

import be.objectify.deadbolt.scala.{ DeadboltHandler, DynamicResourceHandler }
import models.UserRole.DynamicRole
import models.service.Services
import play.api.mvc._

/**
 * A security handler to check if a user is allowed to work with the specific objects.
 *
 * The system supports three roles - Viewer, Editor and Admin.
 */
class ResourceHandler(user: ActiveUser)
    extends DynamicResourceHandler
    with Services {

  def isAllowed[A](name: String, meta: String, handler: DeadboltHandler, request: Request[A]) = {
    val objectId = meta.toLong
    name match {
      case DynamicRole.Coordinator => checkBrandPermission(user.account, objectId)
      case DynamicRole.Member ⇒ checkMemberPermission(user, objectId)
      case DynamicRole.ProfileEditor =>
        user.account.admin || user.account.personId == objectId
      case DynamicRole.OrgMember =>
        orgService.people(objectId).exists(_.identifier == user.account.personId)
      case _ ⇒ false
    }
  }

  def checkPermission[A](permissionValue: String, deadboltHandler: DeadboltHandler, request: Request[A]) = {
    false
  }

  /**
   * Returns true if the given user is allowed to execute a brand-related action
   * @param account User account
   * @param brandId Brand identifier
   */
  protected def checkBrandPermission(account: UserAccount, brandId: Long): Boolean = {
    checker(account).isBrandCoordinator(brandId)
  }

  /**
   * Returns true if the given user is allowed to execute a member-related action
   * @param user Active user
   * @param memberId Member id to check
   */
  protected def checkMemberPermission(user: ActiveUser, memberId: Long): Boolean = {
    if (user.account.admin || user.person.member.exists(_.identifier == memberId))
      true
    else
      memberService.find(memberId) exists { member ⇒
        if (member.person)
          false
        else
          orgService.people(member.objectId).exists(_.identifier == user.account.personId)
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