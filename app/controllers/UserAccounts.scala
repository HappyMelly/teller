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

import models.UserRole.Role.Viewer
import models._
import models.service.Services
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import securesocial.core.RuntimeEnvironment

/**
 * User administration controller.
 */
class UserAccounts(environment: RuntimeEnvironment[ActiveUser])
    extends Controller
    with Security
    with Services {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  val userForm = Form(tuple(
    "personId" -> longNumber,
    "role" -> optional(text)))

  /**
   * Switches active role to Facilitator if it was Brand Coordinator, and
   *  visa versa
   */
  def switchRole = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val account = user.account.copy(activeRole = !user.account.activeRole)
      userAccountService.updateActiveRole(user.account.personId, account.activeRole)
      env.authenticatorService.fromRequest.foreach(auth ⇒ auth.foreach {
        _.updateUser(ActiveUser(user.identity, account, user.person,
          user.person.member))
      })
      Redirect(request.headers("referer"))
  }

}
