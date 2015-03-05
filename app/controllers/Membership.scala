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

import models._
import models.UserRole.Role._
import models.payment.{ PaymentException, RequestException, Payment }
import models.service.Services
import org.joda.money.CurrencyUnit._
import org.joda.money.Money
import play.api.i18n.Messages
import play.api.mvc.{ Controller, Action }
import play.api.data._
import play.api.data.Forms._
import play.api.Logger
import play.api.libs.json._
import play.api.Play
import play.api.Play.current
import services.notifiers.Notifiers

case class PaymentData(token: String,
  fee: Int,
  orgId: Option[Long] = None) {}

trait Membership extends Controller with Security with Services with Notifiers {
  class ValidationException(msg: String) extends RuntimeException(msg) {}

  def form = Form(mapping(
    "token" -> nonEmptyText,
    "fee" -> number,
    "orgId" -> optional(longNumber))(PaymentData.apply)(PaymentData.unapply))

  /**
   * Renders welcome screen for existing users with two options:
   * Become a funder and Become a supporter
   */
  def welcome = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val orgs = user.person.organisations.filter(_.member.isEmpty)
      Ok(views.html.membership.welcome(user, orgs))
  }

  /**
   * Renders welcome screen for new users
   */
  def welcomeNewUser = Action { implicit request ⇒
    Ok(views.html.membership.welcomeNewUser())
  }

  /**
   * Renders congratulations screen
   * If orgId is not empty payment is done for the organisation
   *
   * @param orgId Organisation identifier
   */
  def congratulations(orgId: Option[Long]) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.membership.congratulations(user, orgId))
  }

  /**
   * Renders payment form
   * If orgId is not empty payment is done for the organisation
   *
   * @param orgId Organisation identifier
   */
  def payment(orgId: Option[Long]) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        val publicKey = Play.configuration.getString("stripe.public_key").get
        orgId map { id ⇒
          orgService.find(id) map { org ⇒
            if (user.person.organisations.exists(_.id == org.id)) {
              val fee = Payment.countryBasedFees(org.countryCode)
              Ok(views.html.membership.payment(user, form, publicKey, fee, Some(org)))
            } else {
              Redirect(routes.Membership.welcome()).
                flashing("error" -> Messages("error.person.notOrgMember"))
            }
          } getOrElse {
            Redirect(routes.Membership.welcome()).
              flashing("error" -> Messages("error.organisation.notExist"))
          }
        } getOrElse {
          val code = user.person.address.countryCode
          val fee = Payment.countryBasedFees(code)
          Ok(views.html.membership.payment(user, form, publicKey, fee))
        }
  }

  /**
   * Charges card
   */
  def charge = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      form.bindFromRequest.fold(
        hasError ⇒
          BadRequest(Json.obj("message" -> Messages("error.payment.unexpected_error"))),
        data ⇒ {
          try {
            val org = data.orgId map { orgId ⇒ orgService.find(orgId) } getOrElse None
            validatePaymentData(data, user.person, org)

            val key = Play.configuration.getString("stripe.secret_key").get
            val payment = new Payment(key)
            val customerId = payment.subscribe(user.person,
              org,
              data.token,
              data.fee)

            val fee = Money.of(EUR, data.fee)
            val member = org map { o ⇒
              o.becomeMember(funder = false, fee, user.person.id.get)
              o.copy(customerId = Some(customerId)).update
            } getOrElse {
              user.person.copy(customerId = Some(customerId)).update
              user.person.becomeMember(funder = false, fee)
            }
            val url = org map { o ⇒
              routes.Organisations.details(o.id.get).url
            } getOrElse {
              routes.People.details(user.person.id.get).url
            }
            val fullUrl = Play.configuration.getString("application.baseUrl").getOrElse("") + url
            val text = "Hey @channel, we have *new Supporter*. %s, %s. <%s|View profile>".format(
              org map { o ⇒ o.name } getOrElse { user.person.fullName },
              fee.toString,
              fullUrl)
            slack.send(text)

            member.activity(
              user.person,
              Activity.Predicate.BecameSupporter).insert

            Ok(Json.obj("redirect" -> routes.Membership.congratulations(data.orgId).url))
          } catch {
            case e: PaymentException ⇒
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
            case e: ValidationException ⇒
              BadRequest(Json.obj("message" -> Messages(e.getMessage)))
          }
        })
  }

  /**
   * Validates payments data
   * @param data Data from payment form
   * @param person Current user
   * @param organisation Organisation which wants to become a member
   */
  protected def validatePaymentData(data: PaymentData,
    person: Person,
    organisation: Option[Organisation]) = {
    data.orgId map { orgId ⇒
      if (organisation.isEmpty) {
        throw new ValidationException("error.organisation.notExist")
      }
      if (data.fee < Payment.countryBasedFees(organisation.get.countryCode)._1) {
        throw new ValidationException("error.payment.minimum_fee")
      }
      if (organisation.get.member.nonEmpty) {
        throw new ValidationException("error.organisation.member")
      }
      if (!person.organisations.exists(_.id == Some(orgId))) {
        throw new ValidationException("error.person.notOrgMember")
      }
    }
    if (organisation.isEmpty) {
      if (data.fee < Payment.countryBasedFees(person.address.countryCode)._1) {
        throw new ValidationException("error.payment.minimum_fee")
      }
      if (person.member.nonEmpty) {
        throw new ValidationException("error.person.member")
      }
    }
  }
}

object Membership extends Membership with Security with Services
