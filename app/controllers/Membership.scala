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

import models.{ PaymentException, Payment, RequestException }
import models.UserRole.Role._
import models.service.Services
import play.api.i18n.Messages
import play.api.mvc.{ Flash, Controller }
import play.api.data._
import play.api.data.Forms._
import play.api.Logger
import play.api.libs.json._
import play.api.Play
import play.api.Play.current

case class PaymentData(token: String,
  fee: Int) {}

trait Membership extends Controller with Security with Services {

  private def form = Form(mapping(
    "token" -> nonEmptyText,
    "fee" -> number(min = 1)) (PaymentData.apply)(PaymentData.unapply))

  /**
   * Renders welcome screen with two options: Become a funder and
   * Become a supporter
   */
  def welcome = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.membership.welcome(user))
  }

  /**
   * Renders payment form
   */
  def payment = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val publicKey = Play.configuration.getString("stripe.public_key").get
      Ok(views.html.membership.payment(user, form, publicKey))
  }

  /**
   * Charges card
   */
  def charge = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      form.bindFromRequest.fold(
        hasError ⇒ BadRequest(hasError.errorsAsJson),
        data ⇒ {
          try {
            val key = Play.configuration.getString("stripe.secret_key").get
            val payment = new Payment(key)
            payment.charge(data.fee, user.person, Some(data.token))
            Ok(Json.obj("redirect" -> routes.People.details(user.person.id.get).url))
          } catch {
            case e: PaymentException ⇒
              Logger.info("%s: %s, %s".format(e.code, e.getMessage, e.param))
              val error = e.code match {
                case "card_declined" ⇒ "error.payment.card_declined"
                case "incorrect_cvc" ⇒ "error.payment.incorrect_cvc"
                case "expired_card" ⇒ "error.payment.expired_card"
                case "processing_error" ⇒ "error.payment.processing_error"
                case _ ⇒ "error.payment.unexpected_error"
              }
              BadRequest(Json.obj("message" -> Messages(error)))
            case e: RequestException ⇒
              e.log.foreach(Logger.error(_))
              BadRequest(Json.obj("message" -> Messages(e.getMessage)))
          }
        })
  }
}

object Membership extends Membership with Security with Services
