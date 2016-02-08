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

import java.util.UUID

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import models.UserRole.Role.Viewer
import models._
import models.service.Services
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{MessagesApi, I18nSupport, Messages}
import play.api.mvc._
import securesocial.controllers.{BaseRegistration, ChangeInfo}
import securesocial.core.PasswordInfo
import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core.providers.utils.PasswordValidator
import services.TellerRuntimeEnvironment

import scala.concurrent.{Await, Future}

/**
 * User administration controller.
 */
class UserAccounts @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment,
                                           override val messagesApi: MessagesApi,
                                           val services: Services,
                                           deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, services)(messagesApi, env)
  with I18nSupport {

  val CurrentPassword = "currentPassword"
  val NewPassword = "newPassword"
  val InvalidPasswordMessage = "securesocial.passwordChange.invalidPassword"
  val Password1 = "password1"
  val Password2 = "password2"
  val OkMessage = "securesocial.passwordChange.ok"

  def changeEmailForm(implicit user: ActiveUser) = Form[(String, String)](
    mapping(
      "email" -> play.api.data.Forms.email.verifying("Email address is already in use", { suppliedEmail =>
        import scala.concurrent.duration._
        Await.result(services.identityService.checkEmail(suppliedEmail), 10.seconds)
      }),
      "password" -> nonEmptyText
    )((email, password) => (email, password))((data: (String, String)) => Some(data._1, "")))

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

  /**
    * Renders form for creating or changing the password
    */
  def account = AsyncSecuredRestrictedAction(Viewer) { implicit request =>
    implicit handler => implicit user =>
      if (user.account.byEmail) {
        ok(views.html.v2.userAccount.account(user, user.person.email, changeEmailForm, changePasswordForm))
      } else {
        ok(views.html.v2.userAccount.emptyPasswordAccount(user, newPasswordForm))
      }
  }

  /**
    * checks if the supplied password matches the stored one
    *
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
    * Updates the email for the given token
    *
    * @param tokenId Token
    */
  def handleEmailChange(tokenId: String) = Action.async { implicit request =>
    services.emailToken.find(tokenId) flatMap {
      case None => redirect(routes.Dashboard.index(), "error" -> "Requested token is not found")
      case Some(token) =>
        if (token.isExpired) {
          redirect(routes.Dashboard.index(), "error" -> "The confirmation link has expired")
        } else {
          (for {
            i <- services.identityService.findByUserId(token.userId)
            p <- services.personService.find(token.userId)
          } yield (i, p)) flatMap {
            case (None, _) => redirect(routes.Dashboard.index(), "error" -> "Internal error. Please contact support")
            case (_, None) => redirect(routes.Dashboard.index(), "error" -> "Internal error. Please contact support")
            case (Some(identity), Some(person)) =>
              services.identityService.delete(identity.email)
              services.identityService.insert(identity.copy(email = token.email))
              services.emailToken.delete(tokenId)
              services.personService.update(person.copy(email = token.email))
              val msg = "Your email was successfully updated. Please log in with your new email"
              redirect(routes.LoginPage.logout(success = Some(msg)))
          }
        }
    }
  }

  /**
    * Creates new password for a current user
    */
  def handleNewPassword = AsyncSecuredRestrictedAction(Viewer) { implicit request =>
    implicit handler => implicit user =>
      newPasswordForm.bindFromRequest().fold(
        errors => badRequest(views.html.v2.userAccount.emptyPasswordAccount(user, errors)),
        password => {
          services.identityService.checkEmail(user.person.email) flatMap {
            case true =>
              createPasswordInfo(user, env.currentHasher.hash(password)) flatMap { account =>
                env.mailer.sendEmail("New password", user.person.email,
                  (None, Some(mail.templates.password.html.createdNotice(user.person.firstName))))
                env.authenticatorService.fromRequest.map(auth ⇒ auth.map {
                  _.updateUser(ActiveUser(user.id, user.providerId, account, user.person, user.member))
                }).flatMap { _ =>
                  redirect(routes.UserAccounts.account(), "success" -> Messages(OkMessage))
                }
              }
            case false =>
              val msg = "Your email address is used by another account. Please contact a support team if it's a mistake"
              val errors = newPasswordForm.withGlobalError(msg)
              badRequest(views.html.v2.userAccount.emptyPasswordAccount(user, errors))
          }
        }
      )
  }

  /**
    * Sends confirmation email with a link to change email
    */
  def changeEmail = AsyncSecuredRestrictedAction(Viewer) { implicit request =>
    implicit handler => implicit user =>
      val form = changeEmailForm.bindFromRequest()
      form.fold(
        errors => badRequest(views.html.v2.userAccount.account(user, user.person.email, errors, changePasswordForm)),
        info => {
          services.identityService.findByEmail(user.person.email) flatMap { maybeIdentity =>
            val response = for (
              identity <- maybeIdentity;
              pinfo <- identity.profile.passwordInfo;
              hasher <- env.passwordHashers.get(identity.hasher) if hasher.matches(pinfo, info._2)
            ) yield {
              val now = DateTime.now
              val token = EmailToken(UUID.randomUUID().toString, info._1, user.person.identifier, now, now.plusMinutes(60))
              services.emailToken.insert(token)
              env.mailer.sendEmail("Confirm your email", info._1,
                (None, Some(mail.templates.password.html.confirmEmail(
                  user.person.firstName,
                  Utilities.fullUrl(routes.UserAccounts.handleEmailChange(token.token).url),
                  user.person.email)))
              )
              val msg = "Confirmation email was sent to your new email address"
              redirect(routes.UserAccounts.account(), "success" -> msg)
            }
            response getOrElse {
              val errors = form.withError("password", "Wrong password")
              Future.successful(
                BadRequest(views.html.v2.userAccount.account(user, user.person.email, errors, changePasswordForm))
              )
            }
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
        errors => badRequest(views.html.v2.userAccount.account(user, user.person.email, changeEmailForm, errors)),
        info => {
          env.userService.updatePasswordInfo(user, env.currentHasher.hash(info.newPassword))
          redirect(routes.UserAccounts.account(), "success" -> Messages(OkMessage))
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
      services.userAccountService.updateActiveRole(user.account.personId, account.activeRole)
      env.authenticatorService.fromRequest.map(auth ⇒ auth.map {
        _.updateUser(ActiveUser(user.id, user.providerId, account, user.person, user.member))
      }).flatMap(_ => Future.successful(Redirect(request.headers("referer"))) )
  }

  /**
    * Creates new password identity record for the given user
    *
    * @param user a user instance
    * @param info the password info
    */
  protected def createPasswordInfo(user: ActiveUser, info: PasswordInfo): Future[UserAccount] = {
    val email = user.person.email
    val identity = PasswordIdentity(user.person.id, email, info.password, Some(user.person.firstName),
      Some(user.person.lastName), info.hasher)
    services.identityService.findByEmail(email) flatMap {
      case None => services.identityService.insert(identity)
      case Some(existingIdentity) =>
        services.registeringUserService.delete(email, UsernamePasswordProvider.UsernamePassword)
        services.identityService.update(identity)
    }
    services.userAccountService.update(user.account.copy(byEmail = true))
  }

}

