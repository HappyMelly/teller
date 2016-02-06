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

package controllers

import play.api.i18n.{MessagesApi, Messages}
import play.api.mvc.Action
import play.filters.csrf.{CSRFAddToken, CSRFCheck}
import securesocial.controllers.{BasePasswordReset, BaseRegistration}
import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core.services.SaveMode
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

/**
  * Contains a set of methods for resetting user's password
  */
class PasswordReset @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment,
                                            val messagesApi: MessagesApi)
  extends BasePasswordReset
  with AsyncController {

  private val logger = play.api.Logger("securesocial.controllers.BasePasswordReset")

  override def handleStartResetPassword = Action.async { implicit request =>
    startForm.bindFromRequest.fold(
      errors => badRequest(env.viewTemplates.getStartResetPasswordPage(errors)),
      email => env.userService.findByEmailAndProvider(email, UsernamePasswordProvider.UsernamePassword).flatMap {
        maybeUser =>
          maybeUser match {
            case Some(user) =>
              createToken(email, isSignUp = false).map { token =>
                env.mailer.sendPasswordResetEmail(user, token.uuid)
                env.userService.saveToken(token)
              }
            case None =>
              env.mailer.sendUnkownEmailNotice(email)
          }
          jsonSuccess(Messages(BaseRegistration.ThankYouCheckEmail))
      }
    )
  }


  /**
    * Renders the reset password page
    *
    * @param token the token that identifies the user request
    */
  override def resetPassword(token: String) = CSRFAddToken {
    Action.async {
      implicit request =>
        executeForToken(token, false, {
          t =>
            Future.successful(Ok(views.html.v2.unauthorized.reset(changePasswordForm, t)))
        })
    }
  }

  /**
    * Handles the reset password page submission
    *
    * @param token the token that identifies the user request
    */
  override def handleResetPassword(token: String) = CSRFCheck {
    Action.async { implicit request =>
      executeForToken(token, false, {
        t =>
          changePasswordForm.bindFromRequest.fold(errors =>
            Future.successful(BadRequest(views.html.v2.unauthorized.reset(errors, t))),
            p =>
              env.userService.findByEmailAndProvider(t.email, UsernamePasswordProvider.UsernamePassword).flatMap {
                case Some(profile) =>
                  val hashed = env.currentHasher.hash(p._1)
                  for (
                    updated <- env.userService.save(profile.copy(passwordInfo = Some(hashed)), SaveMode.PasswordChange);
                    deleted <- env.userService.deleteToken(token)
                  ) yield {
                    env.mailer.sendPasswordChangedNotice(profile)
                    confirmationResult().withSession(request.session).flashing(Success -> Messages(PasswordUpdated))
                  }
                case _ =>
                  logger.error("[securesocial] could not find user with email %s during password reset".format(t.email))
                  Future.successful(confirmationResult().flashing(Error -> Messages(ErrorUpdatingPassword)))
              }
          )
      })
    }
  }
}
