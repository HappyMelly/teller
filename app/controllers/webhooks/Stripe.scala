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
package controllers.webhooks

import javax.inject.Inject

import controllers.AsyncController
import models.repository.Repositories
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.mvc.Action

/**
  * Handles events triggered by Stripe
  */
class Stripe @Inject() (val services: Repositories, val messagesApi: MessagesApi) extends AsyncController {

  case class EventData(typ: String, customer: String)

  def event() = Action.async { implicit request =>
    request.body.asJson map { json =>
      // check IPs
      parseEvent(json) match  {
        case Left(data) =>
          data.typ match {
            case "charge.succeeded" => ok("")
            case _ => badRequest("Unsupported event type")
          }
        case Right(error) => badRequest(error)
      }
      // if the payment is successful, add a record
      // remove a record addition after the first payment
      // send an email (if the payment record is not the first one)
    } getOrElse {
      badRequest("Expecting Json data")
    }
  }

  protected def parseEvent(json: JsValue): Either[EventData, String] = {
    if ((json \ "object").asOpt[String].contains("event")) {
      try {
        val typ = (json \ "type").as[String]
        val customer = (((json \ "data") \ "object") \ "customer").as[String]
        Left(EventData(typ, customer))
      } catch {
        case e: JsResultException => Right(e.getMessage)
      }
    } else {
      Right("Missing parameter [object] equals to [event]")
    }
  }
}
