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
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.functional.syntax._
import play.api.libs.json._
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

  case class VATSuccessResponse(country: String, vat: String, valid: String, name: String, address: String) {
    val isValid: Boolean = valid == "true"
  }

  case class VATErrorResponse(code: String, text: String) {
    val isServerError: Boolean = code == "vies-unavailable" || code == "member-state-unavailable"
  }

  implicit val vatSuccessResponseReads: Reads[VATSuccessResponse] = (
        (JsPath \ "country_code").read[String] and
        (JsPath \ "vat_number").read[String] and
        (JsPath \ "valid").read[String] and
        (JsPath \ "name").read[String] and
        (JsPath \ "address").read[String]
    )(VATSuccessResponse.apply _)

  implicit val vatErrorResponseReads: Reads[VATErrorResponse] = (
      (JsPath \ "code").read[String] and
      (JsPath \ "text").read[String]
    )(VATErrorResponse.apply _)

  def validateVAT(vatNumber: String) = Action.async { implicit request =>
    val url = makeVATUrl(vatNumber)
    WS.url(url).withHeaders("Accept" -> "application/json").get().flatMap { response =>
      val responseResult = (response.json \ "response").validate[VATSuccessResponse]
      responseResult match {
        case s: JsSuccess[VATSuccessResponse] => handleSuccessResponse(s.get)
        case e: JsError =>
          val errorResult = (response.json \ "error").validate[VATErrorResponse]
          handleErrorResponse(errorResult)
      }
    }.recover { case _ =>
      Ok(Json.obj("message" -> "VAT check is not possible at this time"))
    }
  }

  protected def handleSuccessResponse(response: VATSuccessResponse) = if (response.isValid)
    jsonSuccess(response.name)
  else
    jsonBadRequest("Invalid VAT number")

  protected def handleErrorResponse(response: JsResult[VATErrorResponse]) = response match {
    case s: JsSuccess[VATErrorResponse] =>
      if (s.get.isServerError)
        jsonSuccess("VAT check service is unavailable right now")
      else
        jsonBadRequest("Invalid VAT number")
    case e: JsError => jsonBadRequest("Internal error. Please contact a support team")
  }

  protected def makeVATUrl(vatNumber: String): String = {
    val countryCode = vatNumber.trim.substring(0, 2)
    val number = vatNumber.trim.substring(2, vatNumber.length)
    s"http://vatid.eu/check/$countryCode/$number"
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
 *
    * @param form Form
    */
  def errorsToJson[U](form: Form[U])(implicit messages: Messages): JsValue = {
    val errors = form.errors.map { error =>
      Json.obj("name" -> error.key, "error" -> error.messages.map(x => Messages(x)).mkString(","))
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