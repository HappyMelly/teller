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
package controllers

import javax.inject.Inject

import play.api.Play
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WS
import play.api.mvc.Action
import templates.Formatters._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

class Utilities @Inject()(override val messagesApi: MessagesApi) extends AsyncController{


  /**
    * Compiles the given markdown to html
    */
  def markdown() = Action.async { implicit request =>
    val form = Form(single("data" -> nonEmptyText))
    form.bindFromRequest.fold(
      error => ok(""),
      data => ok(data.markdown)
    )
  }

  /**
   * Validates the given url points to an existing page
   *
   * @param url Url to check
   */
  def validate(url: String) = Action.async { implicit request ⇒
    WS.url(url).head().flatMap { response =>
      if (response.status >= 200 && response.status < 300)
        jsonOk(Json.obj("result" -> "valid"))
      else
        jsonOk(Json.obj("result" -> "invalid"))
    }.recover { case _ =>
      Ok(Json.prettyPrint(Json.obj("result" -> "invalid")))
    }
  }
}

object Utilities {

  /**
    * Returns CDN url to image if CDN is set
    *
    * @param path Path to image in Amazon S3 bucket
    */
  def cdnUrl(path: String): Option[String] = {
    Play.configuration.getString("cdn.url").map(url => Some(url + path)).getOrElse(None)
  }

  /**
    * Converts form errors to a format readable by frontend
    * @param form Form
    */
  def errorsToJson[U](form: Form[U]): JsValue = {
    val errors = form.errors.map { error =>
      Json.obj("name" -> error.key, "error" -> error.messages.mkString(","))
    }
    Json.obj("errors" -> errors)
  }

  /**
    * Returns an url with domain
    *
    * @param url Domain-less part of url
    */
  def fullUrl(url: String): String = {
    Play.configuration.getString("application.baseUrl").getOrElse("") + url
  }
}