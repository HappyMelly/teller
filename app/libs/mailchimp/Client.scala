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

import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WS}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * MailChimp client
  */
class Client(endPoint: String, token: String) {

  def createList(list: List): Future[List] = {
    val url = endPoint + "/3.0/lists"
    implicit val listFormats = Convertions.listFormats

    request(url).post(Json.toJson(list)).map { response =>
      response.json.as[List]
    }
  }


  def lists(): Future[Seq[List]] = {
    val url = endPoint + "/3.0/lists"
    implicit val listReads = Convertions.listReads
    request(url).get().map { response =>
      (response.json \ "lists").as[Seq[List]]
    }
  }

  def mergeFields(listId: String): Future[Seq[MergeField]] = {
    val url = s"$endPoint/3.0/lists/$listId/merge-fields"
    implicit val mergeFieldReads = Convertions.mergeField
    request(url).get().map { response =>
      (response.json \ "merge_fields").as[Seq[MergeField]]
    }
  }

  def subscribe(listId: String, email: String, firstName: String, lastName: String): Future[Unit] = {
    val url = s"$endPoint/3.0/lists/$listId/members"
    val params = Json.obj("status" -> "subscribed",
      "email_address" -> email,
      "merge_fields" -> Json.obj("FNAME" -> firstName, "LNAME" -> lastName))
    request(url).post(params).map { response =>
      Nil
    }
  }

  protected def request(url: String): WSRequest = WS.url(url).withHeaders("Authorization" -> s"OAuth $token")
}
