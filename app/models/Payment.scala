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

package models

import com.stripe.Stripe
import com.stripe.exception._
import com.stripe.model.Charge
import scala.collection.JavaConversions._

class RequestException(msg: String, logMsg: Option[String] = None)
  extends RuntimeException(msg) {

  def log: Option[String] = logMsg
}

/**
 * Wrapper around stripe.CardException
 * @param msg Human-readable message
 * @param code Unique error code
 * @param param Optional parameter
 */
case class PaymentException(msg: String, code: String, param: String)
  extends RuntimeException(msg)

/**
 * Contains the logic required for working with payment gateway
 */
class Payment(apiKey: String) {

  def charge(sum: Int,
    payer: Person,
    token: Option[String],
    card: Option[Map[String, String]] = None) = {
    val params = Map("amount" -> Int.box(sum * 100),
      "currency" -> "eur",
      "card" -> token.getOrElse(card.getOrElse(Map())),
      "description" -> "One Year Membership Fee",
      "receipt_email" -> payer.socialProfile.email)
    try {
      Stripe.apiKey = apiKey
      val response = Charge.create(params)
    } catch {
      case e: CardException ⇒
        throw new PaymentException(e.getMessage, e.getCode, e.getParam)
      case e: InvalidRequestException ⇒
        throw new RequestException("error.payment.invalid_request", Some(e.toString))
      case e: AuthenticationException ⇒
        throw new RequestException("error.payment.authorisation")
      case e: APIConnectionException ⇒
        throw new RequestException("error.payment.api.connection", Some(e.toString))
      case e: APIException ⇒
        throw new RequestException("error.payment.api", Some(e.toString))
    }
  }
}
