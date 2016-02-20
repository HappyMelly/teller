/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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
package controllers.core

import javax.inject.Inject
import play.api.Play
import play.api.Play.current
import securesocial.controllers.BaseLoginPage
import securesocial.core._
import securesocial.core.utils._

import scala.concurrent.Future

/**
  * This controller is a partial copy of securesocial.controllers.LoginPage
  *
  * Its logout method allows to pass error message to login page
  */
class LoginPage @Inject() (override implicit val env: RuntimeEnvironment) extends BaseLoginPage {

  /**
    * Logs out the user by clearing the credentials from the session.
    * The browser is redirected either to the login page or to the page specified in the onLogoutGoTo property.
    *
    * @return
    */
  def logout(error: Option[String] = None, success: Option[String] = None) = UserAwareAction.async {
    implicit request ⇒
      val redirectTo = Redirect(Play.configuration.getString(onLogoutGoTo).getOrElse(env.routes.loginPageUrl))
      val result = for {
        user ← request.user
        authenticator ← request.authenticator
      } yield {
        redirectTo.discardingAuthenticator(authenticator).map { auth ⇒
          val withError = error map { value ⇒
            auth.flashing("error" -> value)
          } getOrElse auth
          val withSuccess = success map { value =>
            withError.flashing("success" -> value)
          } getOrElse withError
          withSuccess.withSession(Events.fire(new LogoutEvent(user)).getOrElse(request.session))
        }
      }
      result.getOrElse {
        Future.successful(redirectTo)
      }
  }
}
