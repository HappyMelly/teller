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

package controllers.api

import models.UserIdentity
import models.admin.ApiToken
import models.service.Services
import play.api.Play.current
import play.api.cache.Cache
import play.api.mvc._

/**
 * Provides token-based authentication for API actions.
 */
trait ApiAuthentication extends Controller with Services {

  /**
   * Make an action require token authentication
   *
   * @deprecated
   */
  def TokenSecuredAction(f: Request[AnyContent] ⇒ Result) = Action { implicit request ⇒
    request.getQueryString(ApiTokenParam).flatMap { token ⇒
      Cache.getAs[UserIdentity]("identity." + token).map { identity ⇒
        Some(f(request))
      }.getOrElse {
        UserIdentity.findBytoken(token).map { identity ⇒
          Cache.set("identity." + token, identity)
          f(request)
        }
      }
    }.getOrElse(Unauthorized("Unauthorized"))
  }

  /**
   * Make an action require token authentication
   * @deprecated
   */
  def TokenSecuredActionWithIdentity(f: (Request[AnyContent], UserIdentity) ⇒ Result) = Action { implicit request ⇒
    request.getQueryString(ApiTokenParam).flatMap { token ⇒
      Cache.getAs[UserIdentity]("identity." + token).map { identity ⇒
        Some(f(request, identity))
      }.getOrElse {
        UserIdentity.findBytoken(token).map { identity ⇒
          Cache.set("identity." + token, identity)
          f(request, identity)
        }
      }
    }.getOrElse(Unauthorized("Unauthorized"))
  }

  val ApiTokenParam = "api_token"

  /**
   * Checks token authorization
   * @param readWrite If true, read-write authorization is required; otherwise, read-only authorization
   * @param f Function to run
   */
  def TokenSecuredAction(readWrite: Boolean)(f: Request[AnyContent] ⇒ Result) = Action {
    implicit request ⇒
      request.getQueryString(ApiTokenParam) map { value ⇒
        Cache.getAs[ApiToken](ApiToken.cacheId(value)) map { token ⇒
          if (token.authorized(readWrite))
            f(request)
          else
            Unauthorized("Unauthorized")
        } getOrElse {
          apiTokenService.find(value) map { token ⇒
            if (token.authorized(readWrite))
              f(request)
            else
              Unauthorized("Unauthorized")
          } getOrElse Unauthorized("Unauthorized")
        }
      } getOrElse Unauthorized("Unauthorized")
  }
}
