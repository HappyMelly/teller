/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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
package configuration

import javax.inject.Inject

import play.api.{Environment, Mode}
import play.api.http.Status._
import play.api.http._
import play.api.mvc._
import play.api.routing.Router
import play.core.actions.HeadAction

/**
  * Primary entry point for all HTTP requests on Play applications
  */
class RequestHandler @Inject() (errorHandler: HttpErrorHandler,
                                configuration: HttpConfiguration,
                                filters: HttpFilters,
                                router: Router,
                                val env: Environment)
  extends DefaultHttpRequestHandler(router, errorHandler, configuration, filters) {

  /**
    * Retrieve the (RequestHeader,Handler) to use to serve this request.
    * Default is: route, tag request, then apply filters
    *
    * This function was overriden to support POST request through API
    */
  override def handlerForRequest(request: RequestHeader): (RequestHeader, Handler) = {

    def notFoundHandler = Action.async(BodyParsers.parse.empty)(req =>
      errorHandler.onClientError(req, NOT_FOUND)
    )

    val (routedRequest, handler) = routeRequest(request) map {
      case handler: RequestTaggingHandler => (handler.tagRequest(request), handler)
      case otherHandler => (request, otherHandler)
    } getOrElse {
      // We automatically permit HEAD requests against any GETs without the need to
      // add an explicit mapping in Routes
      val missingHandler: Handler = request.method match {
        case HttpVerbs.HEAD =>
          val headAction = routeRequest(request.copy(method = HttpVerbs.GET)) match {
            case Some(action: EssentialAction) => action
            case _ => notFoundHandler
          }
          new HeadAction(headAction)
        case _ =>
          notFoundHandler
      }
      (request, missingHandler)
    }

    val api = """/api/v""".r findPrefixOf request.path
    val userpass = """/authenticate/userpass""".r findPrefixOf request.path
    if (api.nonEmpty || userpass.nonEmpty) {
      (routedRequest, handler)
    } else {
      (routedRequest, filterHandler(rh ⇒ handler)(routedRequest))
    }
  }

  override def routeRequest(request: RequestHeader) = {
    if (env.mode == Mode.Prod && !request.headers.get("x-forwarded-proto").getOrElse("").contains("https")) {
      Some(controllers.HttpsController.redirect)
    } else {
      super.routeRequest(request)
    }
  }
}
