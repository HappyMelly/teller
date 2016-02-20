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

package services

import play.api.mvc.RequestHeader
import securesocial.core.services.RoutesService

/**
 * I had to implement a custom routes service as the default one
 *  causes runtime error during tests and on a production environment.
 *  The problem is in securesocial.controllers.routes.Assets which it cannot
 *  find
 */
class TellerRoutesService extends RoutesService.Default {

   override def loginPageUrl(implicit req: RequestHeader): String = {
     absoluteUrl(_root_.controllers.core.routes.LoginPage.login())
   }

  override def handleResetPasswordUrl(mailToken: String)(implicit req: RequestHeader): String = {
    absoluteUrl(controllers.routes.PasswordReset.handleResetPassword(mailToken))
  }

  override def startResetPasswordUrl(implicit request: RequestHeader): String = loginPageUrl(request)

  override def resetPasswordUrl(mailToken: String)(implicit req: RequestHeader): String = {
    absoluteUrl(controllers.routes.PasswordReset.resetPassword(mailToken))
  }

  override protected def valueFor(key: String, default: String) = {
    val value = conf.getString(key).getOrElse(default)
    _root_.controllers.routes.Assets.at(value)
  }
}
