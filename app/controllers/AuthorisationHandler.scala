/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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

import be.objectify.deadbolt.scala.{ DynamicResourceHandler, DeadboltHandler }
import be.objectify.deadbolt.core.models.Subject
import models.UserAccount
import play.api.mvc.{ SimpleResult, Request, Result }
import play.api.i18n.Messages
import play.api.mvc.Results.Redirect
import scala.concurrent.Future

/**
 * Deadbolt authorisation handler.
 */
class AuthorisationHandler(account: Option[UserAccount]) extends DeadboltHandler {

  override def getSubject[A](request: Request[A]): Option[Subject] = account

  def beforeAuthCheck[A](request: Request[A]) = None

  override def getDynamicResourceHandler[A](request: Request[A]): Option[DynamicResourceHandler] = None

  def onAuthFailure[A](request: Request[A]): Future[SimpleResult] = Future.successful {
    Redirect(routes.Dashboard.index()).flashing("error" -> Messages("error.authorisation"))
  }
}
