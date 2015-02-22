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

package models.payment

import models.{ PaymentRecord, Person }
import org.joda.money.CurrencyUnit._
import org.joda.money.Money
import play.api.Logger
import utils.PaymentGatewayWrapper
import views.Countries

/**
 * Contains methods for working with credit cards
 */
case class Payment(key: String) {

  /**
   * Subscribes new member
   * @param person Member
   * @param token Card token
   * @param amount Fee amount
   * @return Returns remote customer identifier
   */
  def subscribe(person: Person, token: String, amount: Int): String = {
    val gateway = new PaymentGatewayWrapper(key)
    val plan = gateway.plan(amount)
    val customer = gateway.customer(person, plan, token)
    val userId = person.id.get
    val fee = Money.of(EUR, amount)
    PaymentRecord("", userId, userId, person = true, "", fee).insert
    val msg = "User %s (id = %s) paid membership fee EUR %s".format(
      person.fullName, userId, fee)
    Logger.info(msg)
    customer
  }
}

object Payment {
  private val DUTCH_VAT = 21.0
  val TAX_PERCENT_AMOUNT = DUTCH_VAT

  /**
   * Returns minimum and suggested fees for supporters based on country.
   * Fee amounts are taken from HM constitution: http://www.happymelly.com/constitution/
   * @param code Country code
   */
  def countryBasedFees(code: String): (Int, Int) = {
    Countries.gdp.get(code) map { index ⇒
      if (index <= 10)
        (25, 50)
      else if (index <= 25)
        (20, 40)
      else if (index <= 50)
        (15, 30)
      else if (index <= 100)
        (10, 20)
      else (5, 10)
    } getOrElse (5, 10)
  }
}
