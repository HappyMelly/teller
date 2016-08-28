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

package controllers.api

import controllers.AsyncController
import models.repository.Repositories
import play.api.Play
import play.api.Play.current
import play.api.i18n.MessagesApi
import play.api.mvc._

import scala.concurrent.Future

/**
 * Provides token-based authentication for API actions.
 */
class ApiAuthentication(repos: Repositories,
                        val messagesApi: MessagesApi) extends AsyncController {

  val ApiTokenParam = "api_token"

  /**
   * Checks token authorization
 *
   * @param readWrite If true, read-write authorization is required; otherwise, read-only authorization
   * @param f Function to run
   */
  def TokenSecuredAction(readWrite: Boolean)(f: Request[AnyContent] ⇒ Future[Result]) = Action.async {
    implicit request ⇒
      val query: Option[Future[Result]] = (for {
        r <- request.getQueryString(ApiTokenParam)
        t <- Play.configuration.getString("api.token")
      } yield (r, t)) map { case (requestedToken, allowedToken) =>
        if (requestedToken == allowedToken) {
          f(request)
        } else {
          jsonUnauthorized
        }
      }
      query getOrElse jsonUnauthorized
  }

}
