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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package controllers

import play.api.http.Writeable
import play.api.i18n.I18nSupport
import play.api.libs.json.{Json, JsValue}
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.Future

/**
  * Provides a set of functions for handling JSON
  */
trait AsyncController extends Controller with I18nSupport {

  protected def jsonBadRequest(msg: String) = Future.successful(BadRequest(Json.obj("message" -> msg)))

  protected def jsonConflict(msg: String) = Future.successful(Conflict(Json.obj("message" -> msg)))

  protected def jsonForbidden(msg: String) = Future.successful(Forbidden(Json.obj("message" -> msg)))

  protected def jsonFormError(errors: JsValue) = Future.successful(BadRequest(Json.obj("data" -> errors)))

  protected def jsonInternalError(msg: String) = Future.successful(InternalServerError(Json.obj("message" -> msg)))

  protected def jsonNotFound(msg: String) = Future.successful(NotFound(Json.obj("message" -> msg)))

  protected def jsonOk(data: JsValue) = Future.successful(Ok(Json.prettyPrint(data)))

  protected def jsonRequest(status: Int, msg: String) = status match {
    case NOT_FOUND ⇒ jsonNotFound(msg)
    case CONFLICT ⇒ jsonConflict(msg)
    case _ ⇒ jsonBadRequest(msg)
  }
  protected def jsonSuccess(msg: String, data: Option[JsValue] = None) = {
    val reply = data map { x ⇒ Json.obj("message" -> msg, "data" -> x) } getOrElse Json.obj("message" -> msg)
    jsonOk(reply)
  }

  protected def jsonUnauthorized = Future.successful(Unauthorized("Unauthorized"))

  protected def badRequest[C](content: C)(implicit writeable: Writeable[C]) = Future.successful(BadRequest(content))

  protected def forbidden[C](content: C)(implicit writeable: Writeable[C]) = Future.successful(Forbidden(content))

  protected def notFound[C](content: C)(implicit writeable: Writeable[C]) = Future.successful(NotFound(content))

  protected def ok[C](content: C)(implicit writeable: Writeable[C]) = Future.successful(Ok(content))

  protected def redirect(url: String, flashing: (String, String)*) = if (flashing.isEmpty)
    Future.successful(Redirect(url))
  else
    Future.successful(Redirect(url).flashing(flashing:_*))

  protected def redirect(call: Call, flashing: (String, String)*) = if (flashing.isEmpty)
    Future.successful(Redirect(call))
  else
    Future.successful(Redirect(call).flashing(flashing:_*))

  protected def redirect(url: String, session: Session, flashing: (String, String)*) = if (flashing.isEmpty)
    Future.successful(Redirect(url).withSession(session))
  else
    Future.successful(Redirect(url).flashing(flashing:_*).withSession(session))

}
