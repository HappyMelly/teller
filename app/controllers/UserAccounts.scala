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
import play.api.i18n.Messages
import play.api.mvc._
import securesocial.controllers.{BaseRegistration, ChangeInfo}
import securesocial.core.providers.utils.PasswordValidator
import securesocial.core.{PasswordInfo, RuntimeEnvironment}

import scala.concurrent.{Await, Future}

/**
 * User administration controller.
 */
class UserAccounts(environment: RuntimeEnvironment[ActiveUser])
    extends Controller
    with Security
    with Services {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  val CurrentPassword = "currentPassword"
  val NewPassword = "newPassword"
  val InvalidPasswordMessage = "securesocial.passwordChange.invalidPassword"
  val Password1 = "password1"
  val Password2 = "password2"
  val OkMessage = "securesocial.passwordChange.ok"

  val newPasswordForm = Form[String](
    mapping(
      NewPassword ->
        tuple(
          Password1 -> nonEmptyText.verifying(PasswordValidator.constraint),
          Password2 -> nonEmptyText
        ).verifying(Messages(BaseRegistration.PasswordsDoNotMatch), passwords => passwords._1 == passwords._2)
    )((newPassword) => newPassword._1)((password: String) => Some(("", ""))))

  def changePasswordForm(implicit user: ActiveUser) = Form[ChangeInfo](
    mapping(
      CurrentPassword ->
        nonEmptyText.verifying(Messages(InvalidPasswordMessage), { suppliedPassword =>
          import scala.concurrent.duration._
          Await.result(checkCurrentPassword(suppliedPassword), 10.seconds)
        }),
      NewPassword ->
        tuple(
          Password1 -> nonEmptyText.verifying(PasswordValidator.constraint),
          Password2 -> nonEmptyText
        ).verifying(Messages(BaseRegistration.PasswordsDoNotMatch), passwords => passwords._1 == passwords._2)

    )((currentPassword, newPassword) => ChangeInfo(currentPassword, newPassword._1))((changeInfo: ChangeInfo) => Some(("", ("", ""))))
  )

  val userForm = Form(tuple(
    "personId" -> longNumber,
    "role" -> optional(text)))

  def account = AsyncSecuredRestrictedAction(Viewer) { implicit request =>
    implicit handler => implicit user => Future.successful {
      if (user.account.email.isEmpty) {
        Ok(views.html.v2.userAccount.emptyPasswordAccount(user, newPasswordForm))
      } else {
        Ok(views.html.v2.userAccount.account(user, changePasswordForm))
      }
    }
  }

  /**
    * checks if the supplied password matches the stored one
    * @param suppliedPassword the password entered in the form
    * @param user the current user
    * @return a future boolean
    */
  def checkCurrentPassword(suppliedPassword: String)(implicit user: ActiveUser): Future[Boolean] = {
    env.userService.passwordInfoFor(user).map {
      case Some(info) =>
        env.passwordHashers.get(info.hasher).exists {
          _.matches(info, suppliedPassword)
        }
      case None => false
    }
  }

  /**
    * Creates new password for a current user
    */
  def handleNewPassword = AsyncSecuredRestrictedAction(Viewer) { implicit request =>
    implicit handler => implicit user =>
      newPasswordForm.bindFromRequest().fold(
        errors => Future.successful(BadRequest(views.html.v2.userAccount.emptyPasswordAccount(user, errors))),
        password => {
          val account = createPasswordInfo(user, env.currentHasher.hash(password))
          env.mailer.sendEmail("New password", user.person.socialProfile.email,
            (None, Some(mail.templates.password.html.createdNotice(user.person.firstName))))
          env.authenticatorService.fromRequest.map(auth ⇒ auth.map {
            _.updateUser(ActiveUser(user.id, account, user.person, user.person.member))
          }).flatMap { _ =>
            Future.successful(Redirect(routes.UserAccounts.account()).flashing("success" -> Messages(OkMessage)))
          }
        }
      )
  }

  /**
    * Changes the password for the current user
    */
  def changePassword = AsyncSecuredRestrictedAction(Viewer) { implicit request =>
    implicit handler => implicit user =>
      changePasswordForm.bindFromRequest().fold(
        errors => Future.successful(BadRequest(views.html.v2.userAccount.account(user, errors))),
        info => {
          env.userService.updatePasswordInfo(user, env.currentHasher.hash(info.newPassword))
          Future.successful(Redirect(routes.UserAccounts.account()).flashing("success" -> Messages(OkMessage)))
        }
      )
  }

  /**
   * Switches active role to Facilitator if it was Brand Coordinator, and
   *  visa versa
   */
  def switchRole = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val account = user.account.copy(activeRole = !user.account.activeRole)
      userAccountService.updateActiveRole(user.account.personId, account.activeRole)
      env.authenticatorService.fromRequest.map(auth ⇒ auth.map {
        _.updateUser(ActiveUser(user.id, account, user.person, user.person.member))
      }).flatMap(_ => Future.successful(Redirect(request.headers("referer"))) )
  }

  /**
    * Creates new password identity record for a given user
    *
    * @param user a user instance
    * @param info the password info
    */
  protected def createPasswordInfo(user: ActiveUser, info: PasswordInfo): UserAccount = {
    val email = user.person.socialProfile.email
    val identity = PasswordIdentity(user.person.id, email, info.password, Some(user.person.firstName),
      Some(user.person.lastName), info.hasher)
    identityService.insert(identity)
    userAccountService.update(user.account.copy(email = Some(email)))
  }

}
