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
package libs.mailchimp

import com.fasterxml.jackson.core.JsonProcessingException
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSResponse, WSRequest}
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * MailChimp client
  */
class Client(endPoint: String, token: String) {
  protected val wsClient = NingWSClient()

  def createList(list: List): Future[Either[ApiError, List]] = {
    implicit val listFormats = Converter.listFormats
    post("lists", Json.toJson(list)) { json =>
      json.as[List]
    }
  }

  def lists(): Future[Either[ApiError, Seq[List]]] = {
    get("lists") { json =>
      implicit val listReads = Converter.listReads
      (json \ "lists").as[Seq[List]]
    }
  }

  def mergeFields(listId: String): Future[Either[ApiError, Seq[MergeField]]] = {
    val url = s"lists/$listId/merge-fields"
    get(url) { json =>
      implicit val mergeFieldReads = Converter.mergeField
      (json \ "merge_fields").as[Seq[MergeField]]
    }
  }

  def subscribe(listId: String, email: String, firstName: String, lastName: String): Future[Either[ApiError, Unit]] = {
    val url = s"lists/$listId/members"
    val params = Json.obj("status" -> "subscribed",
      "email_address" -> email,
      "merge_fields" -> Json.obj("FNAME" -> firstName, "LNAME" -> lastName))
    post(url, params) { json =>
      Nil
    }
  }

  protected def get[T](url: String)(f: JsValue => T) = request(url).get().map { response =>
    handleResponse(response)(f)
  }

  protected def post[T](url: String, params: JsValue)(f: JsValue => T) = request(url).post(params).map { response =>
    handleResponse(response)(f)
  }

  protected def handleResponse[T](response: WSResponse)(f: JsValue => T) = {
    implicit val apiErrorReads = Converter.apiErrorReads
    if (response.status == 200)
      try {
        Right(f(response.json))
      } catch {
        case e: JsonProcessingException => Left(jsonFormattingError(response.body))
      }
    else
      response.json.asOpt[ApiError] match {
        case None => Left(jsonFormattingError(response.body))
        case Some(error) => Left(error)
      }
  }

  protected def jsonFormattingError(detail: String): ApiError =
    ApiError("", "MailChimp Json Formatting Error", 600, detail, "")


  protected def request(url: String): WSRequest =
    wsClient.url(s"$endPoint/3.0/$url").withHeaders("Authorization" -> s"OAuth $token")
}
