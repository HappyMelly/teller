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
    views.html.secure.login(form, msg)
  }

  def getNotAuthorizedPage[A](implicit request: Request[A]) = {
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
