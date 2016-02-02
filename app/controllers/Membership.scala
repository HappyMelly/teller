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

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import models.UserRole.Role._
import models._
import models.payment.{Payment, PaymentException, RequestException}
import org.joda.money.CurrencyUnit._
import org.joda.money.Money
import play.api.Play.current
import play.api.i18n.{MessagesApi, I18nSupport, Messages}
import play.api.libs.json._
import play.api.mvc._
import play.api.{Logger, Play}
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

class Membership @Inject() (override implicit val env: TellerRuntimeEnvironment,
                            override val messagesApi: MessagesApi,
                            deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder)(messagesApi, env)
  with Enrollment
  with Activities
  with I18nSupport {

  /**
   * Renders welcome screen for existing users with two options:
   * Become a funder and Become a supporter
   */
  def welcome = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    personService.memberships(user.person.identifier) flatMap { orgs =>
      ok(views.html.membership.welcome(user, orgs.filter(_.member.isEmpty)))
    }
  }

  /**
   * Renders congratulations screen
   * If orgId is not empty payment is done for the organisation
   *
   * @param orgId Organisation identifier
   */
  def congratulations(orgId: Option[Long]) = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      ok(views.html.membership.congratulations(user, orgId))
  }

  /**
   * Renders payment form
   * If orgId is not empty payment is done for the organisation
   *
   * @param orgId Organisation identifier
   */
  def payment(orgId: Option[Long]) = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val welcomeCall: Call = routes.Membership.welcome()
      val publicKey = Play.configuration.getString("stripe.public_key").get
      orgId map { id ⇒
        orgService.find(id) flatMap {
          case None => redirect(welcomeCall, "error" -> Messages("error.organisation.notExist"))
          case Some(org) ⇒
            personService.memberships(user.person.identifier) flatMap { orgs =>
              if (orgs.exists(_.id == org.id)) {
                val fee = Payment.countryBasedFees(org.countryCode)
                ok(views.html.membership.payment(user, paymentForm, publicKey, fee, Some(org)))
              } else {
                redirect(welcomeCall, "error" -> Messages("error.person.notOrgMember"))
              }
            }
        }
      } getOrElse {
        val code = user.person.address.countryCode
        val fee = Payment.countryBasedFees(code)
        ok(views.html.membership.payment(user, paymentForm, publicKey, fee))
      }
  }

  /**
   * Charges card
   */
  def charge = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    paymentForm.bindFromRequest.fold(
      hasError ⇒ badRequest(Json.obj("message" -> Messages("error.payment.unexpected_error"))),
      data ⇒ {
        try {
          data.orgId map { orgId =>
            orgService.find(orgId) flatMap {
              case None => badRequest(Json.obj("message" -> "Organisation not found"))
              case Some(org) => processOrganisationMember(data, user, org) flatMap { _ =>
                ok(Json.obj("redirect" -> routes.Membership.congratulations(data.orgId).url))
              }
            }
          } getOrElse {
            processPersonMember(data, user) flatMap { _ =>
              ok(Json.obj("redirect" -> routes.Membership.congratulations(data.orgId).url))
            }
          }

        } catch {
          case e: RuntimeException => handleErrors(e)
        }
      })
  }

  /**
    * Adds new member organisation to the system
    *
    * @param data Payment data
    * @param user Person data
    * @param org Organisation
    */
  protected def processOrganisationMember(data: PaymentData, user: ActiveUser, org: Organisation): Future[Boolean] = {
    validatePaymentData(data, user.person, Some(org))

    val customerId = subscribe(user.person, Some(org), data)
    val fee = Money.of(EUR, data.fee)
    (for {
      _ <- orgService.update(org.copy(customerId = Some(customerId)))
      m <- org.becomeMember(funder = false, fee, user.person.identifier)
    } yield m) map { member =>
      notify(user.person, Some(org), member)
      subscribe(user.person, member)

      activity(member, user.person).becameSupporter.insert()
      true
    }
  }

  /**
    * Adds new member person to the system
    *
    * @param data Payment data
    * @param user Person data
    */
  protected def processPersonMember(data: PaymentData, user: ActiveUser)(
      implicit request: Request[AnyContent]): Future[Boolean] = {
    val person = user.person
    validatePaymentData(data, person, None)

    val customerId = subscribe(person, None, data)
    val fee = Money.of(EUR, data.fee)
    (for {
      _ <- person.copy(customerId = Some(customerId)).update
      m <- person.becomeMember(funder = false, fee)
    } yield m) map { member =>
      env.authenticatorService.fromRequest.foreach(auth ⇒ auth.foreach {
        _.updateUser(ActiveUser(user.id, user.providerId, user.account, user.person, Some(member)))
      })
      notify(person, None, member)
      subscribe(person, member)

      activity(member, user.person).becameSupporter.insert()
      true
    }
  }

  protected def handleErrors(e: RuntimeException) = e match {
    case e: PaymentException ⇒
      val error = e.code match {
        case "card_declined" ⇒ "error.payment.card_declined"
        case "incorrect_cvc" ⇒ "error.payment.incorrect_cvc"
        case "expired_card" ⇒ "error.payment.expired_card"
        case "processing_error" ⇒ "error.payment.processing_error"
        case _ ⇒ "error.payment.unexpected_error"
      }
      badRequest(Json.obj("message" -> Messages(error)))
    case e: RequestException ⇒
      e.log.foreach(Logger.error(_))
      badRequest(Json.obj("message" -> Messages(e.getMessage)))
    case e: Membership.ValidationException ⇒
      badRequest(Json.obj("message" -> Messages(e.getMessage)))
  }

  /**
   * Validates payments data
    *
    * @param data Data from payment form
   * @param person Current user
   * @param organisation Organisation which wants to become a member
   */
  protected def validatePaymentData(data: PaymentData,
    person: Person,
    organisation: Option[Organisation]) = {
    data.orgId foreach { orgId ⇒
      if (organisation.isEmpty) {
        throw new Membership.ValidationException("error.organisation.notExist")
      }
      if (data.fee < Payment.countryBasedFees(organisation.get.countryCode)._1) {
        throw new Membership.ValidationException("error.payment.minimum_fee")
      }
      if (organisation.get.member.nonEmpty) {
        throw new Membership.ValidationException("error.organisation.member")
      }
      if (!person.organisations.exists(_.id == Some(orgId))) {
        throw new Membership.ValidationException("error.person.notOrgMember")
      }
    }
    if (organisation.isEmpty) {
      if (data.fee < Payment.countryBasedFees(person.address.countryCode)._1) {
        throw new Membership.ValidationException("error.payment.minimum_fee")
      }
      if (person.member.nonEmpty) {
        throw new Membership.ValidationException("error.person.member")
      }
    }
  }
}

object Membership {

  class ValidationException(msg: String) extends RuntimeException(msg) {}
}