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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package controllers

import play.api.mvc.{ Flash, Action, Controller }
import play.api.Play
import play.api.Play.current
import securesocial.core._
import securesocial.core.providers.utils.RoutesHelper

/**
 * This controller is a partial copy of securesocial.controllers.LoginPage
 *
 * Its logout method allows to pass error message to login page
 */
object LoginPage extends Controller {

  /**
   * The property that specifies the page the user is redirected to after logging out.
   */
  val onLogoutGoTo = "securesocial.onLogoutGoTo"

  /**
   * Logs out the user by clearing the credentials from the session.
   * The browser is redirected either to the login page or to the page specified
   * in the onLogoutGoTo property.
   *
   * The difference between this method and the one in securesocial.controllers.LoginPage
   * is that this method passes error message to login page using Flash object
   *
   * @param error Message which will be passed to login page
   */
  def logout(error: Option[String] = None) = Action { implicit request ⇒
    val to = Play.configuration.getString(onLogoutGoTo).getOrElse(RoutesHelper.login().absoluteURL(IdentityProvider.sslEnabled))
    val user = for (
      authenticator ← SecureSocial.authenticatorFromRequest;
      user ← UserService.find(authenticator.identityId)
    ) yield {
      Authenticator.delete(authenticator.id)
      user
    }
    val result = Redirect(to).
      discardingCookies(Authenticator.discardingCookie)
    val resultWithFlash = error map { e ⇒
      result.flashing("error" -> e)
    } getOrElse result
    user match {
      case Some(u) ⇒ resultWithFlash.withSession(session)
      case None ⇒ resultWithFlash
    }
  }
}
