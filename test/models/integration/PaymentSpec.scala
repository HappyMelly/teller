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

package models.integration

import helpers.PersonHelper
import models.{ PaymentException, Payment }
import org.specs2.mutable._

class PaymentSpec extends Specification {

  "Method `charge`" should {
    "throw PaymentException when API key is wrong" in {
      val payment = new Payment("wrong_key")
      val payer = PersonHelper.one()
      val msg = "error.payment.authorisation"
      payment.charge(BigDecimal(200), payer, Some("token")) must throwA[PaymentException](msg)
    }
    //    "throw PaymentException when the card number is incorrect" in {
    //      val payment = new Payment("")
    //      val payer = PersonHelper.one()
    //      val card = Map("number" -> "1242424242424242",
    //        "exp_month" -> "12",
    //        "exp_year" -> "2025",
    //        "cvc" -> "111",
    //        "name" -> "Donald Duck")
    //      val msg = "error.payment.incorrect_number"
    //      payment.charge(BigDecimal(200), payer, None, Some(card)) must throwA[PaymentException](msg)
    //    }
  }
}
