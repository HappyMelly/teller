package controllers

import be.objectify.deadbolt.scala.{ DynamicResourceHandler, DeadboltHandler }
import be.objectify.deadbolt.core.models.Subject
import models.UserAccount
import play.api.mvc.{ Request, Result }
import play.api.i18n.Messages
import play.api.mvc.Results.Redirect

/**
 * Deadbolt authorisation handler.
 */
class AuthorisationHandler(account: Option[UserAccount]) extends DeadboltHandler {

  override def getSubject[A](request: Request[A]): Option[Subject] = account

  def beforeAuthCheck[A](request: Request[A]) = None

  override def getDynamicResourceHandler[A](request: Request[A]): Option[DynamicResourceHandler] = None

  def onAuthFailure[A](request: Request[A]): Result = {
    Redirect(routes.Dashboard.index()).flashing("error" -> Messages("error.authorisation"))
  }
}