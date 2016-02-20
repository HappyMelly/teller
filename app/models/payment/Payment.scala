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

import models.repository.Repositories
import models.{ Organisation, Person }
import org.joda.money.CurrencyUnit._
import org.joda.money.Money
import play.api.Logger
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
   * @return Returns remote customer identifier
   */
  def subscribe(person: Person,
                org: Option[Organisation],
                token: String, amount: Int)(implicit services: Repositories): String = {
    val gateway = new GatewayWrapper(key)
    val plan = gateway.plan(amount)
    val customerName = org map { _.name } getOrElse { person.fullName }
    val customerId = org map { _.id.get } getOrElse { person.id.get }
    val customer = gateway.customer(customerName, customerId, person.email, plan, token)
    val fee = Money.of(EUR, amount)
    val invoices = gateway.invoices(customer)
    if (invoices.length != 1) {
      Logger.error("A number of invoices for a new customer = %s".format(invoices.length))
    }
    val invoiceId = if (invoices.length >= 1)
      invoices(0)
    else
      "Internal error. Please inform the support stuff"
    val userId = person.id.get
    val record = Record(invoiceId, userId, customerId, person = org.isEmpty, Payment.DESC, fee)
    services.paymentRecord.insert(record)
    val msg = org map { o ⇒
      "Organisation %s (id = %s) paid membership fee EUR %s".format(
        customerName, customerId, fee)
    } getOrElse {
      "User %s (id = %s) paid membership fee EUR %s".format(
        customerName, customerId, fee)
    }
    Logger.info(msg)
    customer
  }
}

object Payment {
  private val DUTCH_VAT = 21.0
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
