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

package security

import be.objectify.deadbolt.core.models.Subject
import be.objectify.deadbolt.scala.{DeadboltHandler, DynamicResourceHandler}
import controllers.routes
import models.ActiveUser
import models.service.{IServices, Services}
import play.api.i18n.{MessagesApi, I18nSupport, Messages}
import play.api.mvc.{Request, Result}
import securesocial.core.SecureSocial
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

/**
 * Deadbolt authorisation handler.
 */
class AuthorisationHandler @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment,
                                                   val messagesApi: MessagesApi,
                                                   val services: IServices)
  extends DeadboltHandler
  with I18nSupport
  with SecureSocial {

  /**
   * Invoked prior to a constraint's test.  If Option.None is returned, the constraint is applied. If
   * the option contains a result, the constraint will not be applied and the wrapped action will not
   * be invoked.
   *
   * @return an option possible containing a Result.
   */
  def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = Future.successful(None)

  /**
   * Gets the current subject e.g. the current user.
   *
   * @return an option containing the current subject
   */
  override def getSubject[A](request: Request[A]): Future[Option[Subject]] = {
    try {
      val securedRequest = request.asInstanceOf[SecuredRequest[A]]
      securedRequest.user match {
        case user: ActiveUser ⇒ Future.successful(Some(user.account))
        case _ => Future.successful(None)
      }
    } catch {
      case e: ClassCastException => Future.successful(None)
    }
  }

  /**
   * Gets the handler used for dealing with resources restricted to specific users/groups.
   *
   * @return an option containing the handler for restricted resources
   */
  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = {
    try {
      val securedRequest = request.asInstanceOf[SecuredRequest[A]]
      securedRequest.user match {
        case user: ActiveUser ⇒ Future.successful(Some(new ResourceHandler(user, services)))
        case _ => Future.successful(None)
      }
    } catch {
      case e: ClassCastException => Future.successful(None)
    }
  }

  /**
   * Invoked when an authorisation failure is detected for the request.
   *
   * @return the action
   */
  def onAuthFailure[A](request: Request[A]): Future[Result] = Future.successful {
    Redirect(routes.Dashboard.index()).flashing("error" -> Messages("error.authorisation"))
  }
}
