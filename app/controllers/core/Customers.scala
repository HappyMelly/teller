/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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
package controllers.core

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.Security
import models.{Member, Person, isNewEra}
import models.UserRole.Role._
import models.core.payment._
import models.repository.Repositories
import org.joda.time.LocalDate
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.Result
import play.api.{Logger, Play}
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

/**
  * Manages all operations related to customers
  */
class Customers @Inject() (override implicit val env: TellerRuntimeEnvironment,
                           override val messagesApi: MessagesApi,
                           val repos: Repositories,
                           deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env) {

  val apiSecretKey = Play.configuration.getString("stripe.secret_key").get

  def updateSubscription(customerId: Long) = RestrictedAction(Viewer) { implicit request =>implicit handler =>
    implicit user =>
      repos.core.customer.find(customerId) flatMap {
        case None => jsonNotFound("Customer not found")
        case Some(customer) =>
          isAllowed(user.person, customer) flatMap { allowed =>
            if (allowed) {
              val form = Form(single("yearly" -> boolean))
              form.bindFromRequest().fold(
                error => jsonBadRequest("Subscription is not chosen"),
                yearly => changeSubscription(user.person, customer, yearly)
              )
            } else {
              jsonForbidden("You are not allowed to update card details")
            }
          }
      }
  }

  /**
    * Adds new card for the given customer and removes the old ones
    *
    * @param customerId Customer identifier
    */
  def updateCard(customerId: Long) = RestrictedAction(Viewer) { implicit request => implicit handler => implicit user =>
    repos.core.customer.find(customerId) flatMap {
      case None => jsonNotFound("Customer not found")
      case Some(customer) =>
        isAllowed(user.person, customer) flatMap { allowed =>
          if (allowed) {
            val form = Form(single("token" -> nonEmptyText))
            form.bindFromRequest().fold(
              error => jsonBadRequest("Token parameter is empty or doesn't exist"),
              token => replaceCard(customer, token)
            )
          } else {
            jsonForbidden("You are not allowed to update card details")
          }
        }
    }
  }

  /**
    * Returns true if the given person is allowed to manage the given customer
    *
    * @param person Person
    * @param customer Customer
    */
  protected def isAllowed(person: Person, customer: Customer): Future[Boolean] = {
    if (customer.objectType == CustomerType.Person)
      Future.successful(customer.objectId == person.identifier)
    else
      repos.org.people(customer.objectId).map(_.exists(_.identifier == person.identifier))
  }

  protected def replaceCard(customer: Customer, cardToken: String): Future[Result] = {
    repos.core.card.findByCustomer(customer.id.get) flatMap { cards =>
      val payment = Payment(apiSecretKey)
      val card = payment.updateCards(customer.remoteId, cardToken, cards)
      (for {
        _ <- repos.core.card.insert(card.copy(customerId = customer.id.get))
        _ <- repos.core.card.delete(cards.map(_.id.get))
      } yield ()) flatMap { _ =>
        jsonSuccess("Your card was changed")
      }
    }
  }

  protected def changeSubscription(person: Person, customer: Customer, yearly: Boolean): Future[Result] = {
    repos.member.findByObject(customer.objectId, customer.objectType == CustomerType.Person) flatMap {
      case None => badRequest("Member not found")
      case Some(member) =>
        if (member.yearly == yearly && member.plan.nonEmpty) {
          jsonSuccess("Your subscription was changed")
        } else {
            val gateway = new GatewayWrapper(apiSecretKey)
            try {
              val key = Play.configuration.getString("stripe.secret_key").get
              val payment = new Payment(key)
              val (_, org) = member.memberObj
              val countryCode = org match {
                case None => person.address.countryCode
                case Some(organisation) => organisation.countryCode
              }
              val planId = Payment.stripePlanId(countryCode, yearly)
              payment.updateSubscription(customer.remoteId, planId)

              val until = LocalDate.now().plusYears(1)
              val fee = if (yearly)
                Payment.countryBasedPlans(countryCode)._2
              else
                Payment.countryBasedPlans(countryCode)._1

              repos.member.update(member.copy(yearly = yearly, until = until, plan = Some(planId), fee = fee.toDouble)) flatMap { _ =>
                jsonSuccess("Your subscription was changed")
              }
            } catch {
              case e: PaymentException ⇒
                jsonBadRequest(Messages(e.msg))
              case e: RequestException ⇒
                e.log.foreach(Logger.error(_))
                jsonBadRequest(Messages(e.getMessage))
            }
        }
    }
  }

}
