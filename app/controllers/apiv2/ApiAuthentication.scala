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

package controllers.apiv2

import models.admin.ApiToken
import models.service.Services
import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.json._
import play.api.mvc._

/**
 * Provides token-based authentication for API actions.
 */
trait ApiAuthentication extends Controller with Services {

  val ApiTokenParam = "api_token"

  /**
   * Checks token authorization
   * @param readWrite If true, read-write authorization is required; otherwise, read-only authorization
   * @param f Function to run
   */
  def TokenSecuredAction(readWrite: Boolean)(f: Request[AnyContent] ⇒ ApiToken ⇒ Result) = Action {
    implicit request ⇒
      request.getQueryString(ApiTokenParam) map { value ⇒
        Cache.getAs[ApiToken](ApiToken.cacheId(value)) map { token ⇒
          authorize(readWrite, token)(f)
        } getOrElse {
          apiTokenService.find(value) map { token ⇒
            authorize(readWrite, token)(f)
          } getOrElse jsonUnauthorized
        }
      } getOrElse jsonUnauthorized
  }

  /**
   * Checks if token is authorized to run the given function and runs it
   *  if the check is successful
   * @param readWrite If true, read-write authorization is required; otherwise, read-only authorization
   * @param token Token of interest
   * @param f Function to run
   * @param request Request object
   * @return Returns the result of function execution or Unauthorized
   */
  protected def authorize(readWrite: Boolean,
    token: ApiToken)(f: Request[AnyContent] ⇒ ApiToken ⇒ Result)(implicit request: Request[AnyContent]): Result = {
    if (token.authorized(readWrite))
      f(request)(token)
    else
      jsonUnauthorized
  }

  protected def jsonUnauthorized = Unauthorized("Unauthorized")

  protected def jsonOk(data: JsValue) = Ok(Json.prettyPrint(data))

  protected def jsonNotFound(msg: String) = NotFound(msg)

  protected def jsonBadRequest(msg: String) = BadRequest(msg)
}
