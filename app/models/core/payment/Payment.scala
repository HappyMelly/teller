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

package models.core.payment

import models.{Organisation, Person}
import views.Countries

/**
 * Contains methods for working with credit cards
 */
case class Payment(key: String) {

  /**
   * Subscribes new member either a person or an organisation
 *
   * @param person Person making a payment (also may want to be a member)
   * @param org Organisation which wants to be a member
   * @param token Card token
   * @param amount Fee amount
   * @param coupon Coupon with discount
   * @return Returns remote customer identifier and credit card info
   */
  def subscribe(person: Person,
                org: Option[Organisation],
                token: String,
                amount: Int,
                coupon: Option[String] = None): (String, CreditCard) = {
    val gateway = new GatewayWrapper(key)
    val plan = gateway.plan(amount)
    val customerName = org map { _.name } getOrElse { person.fullName }
    val customerId = org map { _.id.get } getOrElse { person.id.get }
    gateway.customer(customerName, customerId, person.email, plan, token, coupon)
  }

  /**
    * Deletes old cards and add a new one to the given customer
    * @param customerId Customer identifier
    * @param cardToken New card token
    * @param cards Old cards
    * @return New card data
    */
  def updateCards(customerId: String, cardToken: String, cards: Seq[CreditCard]): CreditCard = {
    val gateway = new GatewayWrapper(key)
    gateway.updateCards(customerId, cardToken, cards)
  }
}

object Payment {
  private val DUTCH_VAT = 21.0f
  val TAX_PERCENT_AMOUNT = DUTCH_VAT
  val DESC = "One Year Membership"

  /**
   * Returns Token, Generous and Big hearted fees for supporters based on country.
   * Fee amounts are taken from HM constitution: http://www.happymelly.com/constitution/
 *
   * @param code Country code
   */
  def countryBasedFees(code: String): (Int, Int, Int) = {
    Countries.gdp.get(code) map { category ⇒
      category match {
        case 1 => (25, 50, 100)
        case 2 => (20, 40, 80)
        case 3 => (15, 30, 60)
        case 4 => (10, 20, 40)
        case _ => (5, 10, 20)
      }
    } getOrElse (5, 10, 20)
  }
}
