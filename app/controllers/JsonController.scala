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

package controllers

import play.api.libs.json.{ Json, JsValue }
import play.api.mvc._

/**
 * Provides a set of functions for handling JSON
 */
trait JsonController extends Controller {

  protected def jsonUnauthorized = Unauthorized("Unauthorized")

  protected def jsonOk(data: JsValue) = Ok(Json.prettyPrint(data))

  protected def jsonSuccess(msg: String, data: Option[JsValue] = None) = {
    val reply = data map { x ⇒ Json.obj("message" -> msg, "data" -> x)
    } getOrElse Json.obj("message" -> msg)
    jsonOk(reply)
  }

  protected def jsonNotFound(msg: String) = NotFound(Json.obj("message" -> msg))

  protected def jsonBadRequest(msg: String) = BadRequest(Json.obj("message" -> msg))

  protected def jsonConflict(msg: String) = Conflict(Json.obj("message" -> msg))

  protected def jsonRequest(status: Int, msg: String) = status match {
    case NOT_FOUND ⇒ jsonNotFound(msg)
    case CONFLICT ⇒ jsonConflict(msg)
    case _ ⇒ jsonBadRequest(msg)
  }

}
