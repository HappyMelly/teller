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

import models.{ Member, Organisation, Person }
import models.service.Services
import models.payment.Payment
import org.joda.money.Money
import play.api.Play
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{ AnyContent, Controller, Action }
import services.integrations.Integrations
import play.api.Play.current

case class PaymentData(token: String,
  fee: Int,
  orgId: Option[Long] = None) {}

/**
 * Defines an interface for enrollment classes
 */
trait Enrollment extends Controller
    with Services
    with Integrations
    with Utilities {

  def paymentForm = Form(mapping(
    "token" -> nonEmptyText,
    "fee" -> number,
    "orgId" -> optional(longNumber))(PaymentData.apply)(PaymentData.unapply))

  /**
   * Sends Slack and email notifications
   * @param person Person making all membership-related actions
   * @param org Organisation which want to become a member
   * @param fee Membership fee
   * @param member Member data
   */
  protected def notify(person: Person,
    org: Option[Organisation],
    fee: Money,
    member: Member) = {
    val url = org map { x ⇒ routes.Organisations.details(x.id.get).url
    } getOrElse routes.People.details(person.id.get).url
    val name = org map (_.name) getOrElse person.fullName
    val memberType = if (member.funder) "Funder" else "Supporter"
    val text = "Hey @channel, we have *new %s*. %s, %s. <%s|View profile>".format(
      memberType,
      name,
      fee.toString,
      fullUrl(url))
    slack.send(text)
    val shortName = org map (_.name) getOrElse person.firstName
    email.send(Set(person),
      subject = "Welcome to Happy Melly network",
      body = mail.templates.html.welcome(fullUrl(url), member.profileUrl, shortName).toString(),
      richMessage = true)
  }

  /**
   * Subscribes the given person to a membership list
   * @param person Person
   * @param member Member data
   * @return Returns true if the person is successfully subscribed
   */
  protected def subscribe(person: Person, member: Member): Boolean = {
    val listId = Play.configuration.getString("mailchimp.listId").getOrElse("")
    mailChimp.subscribe(listId, person, member.funder)
  }

  /**
   * Makes a payment through the payment gateway and creates
   * an yearly subscription
   *
   * @param person Person making all membership-related actions
   * @param org Organisation which want to become a member
   * @param data Payment data
   * @return Returns customer identifier in the payment system
   */
  protected def subscribe(person: Person,
    org: Option[Organisation],
    data: PaymentData): String = {
    val key = Play.configuration.getString("stripe.secret_key").get
    val payment = new Payment(key)
    payment.subscribe(person, org, data.token, data.fee)
  }
}
