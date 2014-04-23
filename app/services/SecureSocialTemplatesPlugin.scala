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

package services

import play.api.Application
import securesocial.controllers.TemplatesPlugin
import play.api.templates.Html
import play.api.data.Form
import play.api.mvc.{ RequestHeader, Request }
import securesocial.core.{ SecuredRequest, Identity }
import securesocial.controllers.PasswordChange.ChangeInfo
import securesocial.controllers.Registration.RegistrationInfo

/**
 * Renders templates for SecureSocial.
 * @param application
 */
class SecureSocialTemplatesPlugin(application: Application) extends TemplatesPlugin {

  override def getLoginPage[A](implicit request: Request[A], form: Form[(String, String)],
    msg: Option[String] = None): Html = {
    implicit val flash = request.flash
    views.html.secure.login(form, msg)
  }

  def getNotAuthorizedPage[A](implicit request: Request[A]) = {
    implicit val flash = request.flash
    views.html.secure.notAuthorized()
  }

  def getSignUpPage[A](implicit request: Request[A], form: Form[RegistrationInfo], token: String) = ???

  def getStartSignUpPage[A](implicit request: Request[A], form: Form[String]) = ???

  def getResetPasswordPage[A](implicit request: Request[A], form: Form[(String, String)], token: String) = ???

  def getStartResetPasswordPage[A](implicit request: Request[A], form: Form[String]) = ???

  def getPasswordChangePage[A](implicit request: SecuredRequest[A], form: Form[ChangeInfo]) = ???

  def getSignUpEmail(token: String)(implicit request: RequestHeader) = ???

  def getAlreadyRegisteredEmail(user: Identity)(implicit request: RequestHeader) = ???

  def getWelcomeEmail(user: Identity)(implicit request: RequestHeader) = ???

  def getUnknownEmailNotice()(implicit request: RequestHeader) = ???

  def getSendPasswordResetEmail(user: Identity, token: String)(implicit request: RequestHeader) = ???

  def getPasswordChangedNoticeEmail(user: Identity)(implicit request: RequestHeader) = ???
}
