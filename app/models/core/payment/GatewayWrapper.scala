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

import com.stripe.Stripe
import com.stripe.exception._
import com.stripe.model._
import models.Person

import scala.collection.JavaConversions._

class RequestException(msg: String, logMsg: Option[String] = None) extends RuntimeException(msg) {

  def log: Option[String] = logMsg
}

/**
 * Wrapper around stripe.CardException
  *
  * @param msg Human-readable message
 * @param code Unique error code
 * @param param Optional parameter
 */
case class PaymentException(msg: String, code: String, param: String) extends RuntimeException(msg)

/**
 * Contains the logic required for working with payment gateway
 */
class GatewayWrapper(apiKey: String) {

  /**
    * Creates new coupon
    * @param coupon Coupon
    */
  def createCoupon(coupon: models.core.Coupon) = stripeCall {
    val params = Map(
      "id" -> coupon.code,
      "duration" -> "once",
      "percent_off" -> Int.box(coupon.discount)
    )
    com.stripe.model.Coupon.create(params)
  }

  /**
   * Cancels subscriptions
    *
    * @param customerId Customer
   */
  def cancel(customerId: String) = stripeCall {
    val subscriptions = com.stripe.model.Customer.retrieve(customerId).getSubscriptions.getData
    subscriptions.foreach(_.cancel(Map[String, AnyRef]()))
  }

  /**
   * Charges user's card using Stripe
    *
    * @param sum Amount to be charged
   * @param payer User object
   * @param token Stripe token for a single session
   * @return Returns Stripe JSON response
   */
  def charge(sum: Int, payer: Person, token: Option[String]) = stripeCall {
    val params = Map("amount" -> Int.box(sum * 100),
      "currency" -> "eur",
      "card" -> token.getOrElse(""),
      "description" -> Payment.DESC,
      "receipt_email" -> payer.email)
    com.stripe.model.Charge.create(params)
  }

  /**
    * Creates a new customer and subscribes him/her to the given plan
    *
    * @param customerName Name of the customer
    * @param customerId Internal id of the customer
    * @param payerEmail Email of the person who pays
    * @param plan Plan identifier
    * @param token Card token
    * @param coupon Coupon with discount
    * @return Returns customer identifier and credit card info
    */
  def customer(customerName: String,
               customerId: Long,
               payerEmail: String,
               plan: String,
               token: String,
               coupon: Option[String] = None): (String, CreditCard) = stripeCall {
    val params = Map(
      "description" -> "Customer %s (id = %s) ".format(customerName, customerId),
      "email" -> payerEmail,
      "source" -> token)
    val customer = com.stripe.model.Customer.create(params)
    try {
      val subscriptionParams = Map("plan" -> plan, "tax_percent" -> Payment.TAX_PERCENT_AMOUNT.toString)
      val withCoupon = coupon match {
        case None => subscriptionParams
        case Some(value) => subscriptionParams + ("coupon" -> value)
      }
      customer.createSubscription(withCoupon)
      (customer.getId, creditCard(customer))
    } catch {
      case e: StripeException =>
        customer.delete()
        throw e
    }
  }

  /**
    * Deletes a coupon
    * @param couponId Coupon id
    */
  def deleteCoupon(couponId: String) = stripeCall {
    val coupon = com.stripe.model.Coupon.retrieve(couponId)
    coupon.delete()
  }

  /**
    * Returns a list of invoices for the given customer
    *
    * @param customerId Customer
    * @return
    */
  def invoices(customerId: String): List[String] = stripeCall {
    Invoice.all(Map("customer" -> customerId)).getData.map(_.getId).toList
  }

  /**
    * Retrieves plan or creates a plan if the required one doesn't exist
    *
    * @param fee Plan amount
    * @return Returns plan identifier
    */
  def plan(fee: Int): String = stripeCall {
    val amount = fee * 100
    val params = Map[String, AnyRef]("limit" -> 100.asInstanceOf[AnyRef])
    val plans = Plan.all(params).getData
    val plan = plans.
      find(p ⇒ p.getCurrency == "eur" && p.getAmount == amount).
      getOrElse {
        val params = Map("amount" -> Int.box(amount),
          "interval" -> "year",
          "currency" -> "eur",
          "name" -> Payment.DESC,
          "id" -> "custom_%s".format(fee))
        Plan.create(params)
      }
    plan.getId
  }

  /**
    * Creates new subscription for the given customer
    * @param customerId Customer identifier
    * @param fee Subscription amount
    */
  def subscribe(customerId: String, fee: BigDecimal) = stripeCall {
    val customer = com.stripe.model.Customer.retrieve(customerId)
    val planId = plan(fee.intValue())
    customer.createSubscription(Map("plan" -> planId, "tax_percent" -> Payment.TAX_PERCENT_AMOUNT.toString))
  }

  /**
    * Creates new subscription for the given customer
    * @param customerId Customer identifier
    * @param newPlan Plan identifier
    */
  def changeSubscription(customerId: String, newPlan: String) = stripeCall {
    val customer = com.stripe.model.Customer.retrieve(customerId)
    val subscriptions = customer.getSubscriptions.getData
    if (subscriptions.isEmpty) {
      customer.createSubscription(Map("plan" -> newPlan, "tax_percent" -> Payment.TAX_PERCENT_AMOUNT.toString))
    } else {
      val subscription = subscriptions.head
      subscription.update(Map("plan" -> newPlan, "prorate" -> true.toString))
    }
  }

  /**
    * Deletes old cards and add a new one to the given customer
    *
    * @param customerId Customer identifier
    * @param cardToken New card token
    * @param cards Old cards
    * @return New card data
    */
  def updateCards(customerId: String, cardToken: String, cards: Seq[CreditCard]): CreditCard = stripeCall {
    val customer = com.stripe.model.Customer.retrieve(customerId)
    val remoteCards = customer.getCards
    cards.foreach { card =>
      remoteCards.retrieve(card.remoteId).delete()
    }
    val card = customer.createCard(cardToken)
    customer.setDefaultCard(card.getId)
    CreditCard(None, 0, card.getId, card.getBrand, card.getLast4, card.getExpMonth, card.getExpYear)
  }

  protected def stripeCall[A](f: => A) = {
    try {
      Stripe.apiKey = apiKey
      f
    } catch {
      case e: CardException ⇒
        throw new PaymentException(codeToMsg(e.getCode), e.getCode, e.getParam)
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

  protected def codeToMsg(code: String): String = code match {
    case "card_declined" ⇒ "error.payment.card_declined"
    case "incorrect_cvc" ⇒ "error.payment.incorrect_cvc"
    case "expired_card" ⇒ "error.payment.expired_card"
    case "processing_error" ⇒ "error.payment.processing_error"
    case _ ⇒ "error.payment.unexpected_error"
  }

  /**
    * Returns default credit card info
    *
    * @param customer Stripe customer object
    */
  protected def creditCard(customer: com.stripe.model.Customer): CreditCard = {
    val card = customer.getCards.retrieve(customer.getDefaultCard)
    CreditCard(None, 0, card.getId, card.getBrand, card.getLast4, card.getExpMonth, card.getExpYear)
  }
}
