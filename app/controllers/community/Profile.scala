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
package controllers.community

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.Security
import models.{Organisation, Person, ActiveUser, Member}
import models.core.payment.{Payment, CustomerType}
import models.repository.Repositories
import models.UserRole.Role._
import play.api.mvc.{Result, Request}
import play.api.Play
import play.api.Play.current
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

/**
  * Manages community-related part of person or organisation profile
  */
class Profile @Inject() (override implicit val env: TellerRuntimeEnvironment,
                         override val messagesApi: MessagesApi,
                         val repos: Repositories,
                         deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env) {

  val apiPublicKey = Play.configuration.getString("stripe.public_key").get

  /**
    * Renders Membership tab for the given person
    *
    * @param id Person identifier
    */
  def membership(id: Long) = RestrictedAction(Viewer) { implicit request => implicit handler => implicit user =>
    (for {
      person <- repos.person.findComplete(id)
      member <- repos.person.member(id)
    } yield (person, member)) flatMap {
      case (_, None) => ok("Person is not a member")
      case (None, _) => notFound("Person not found")
      case (Some(person), Some(member)) =>
        if (member.funder)
          ok(views.html.v2.person.tabs.funder(user, person, member))
        else
          renderSupporterTab(user, person, member)
    }
  }

  /**
    * Renders Membership tab for the given person
    *
    * @param id Person identifier
    */
  def orgMembership(id: Long) = RestrictedAction(Viewer) { implicit request => implicit handler => implicit user =>
    (for {
      o <- repos.org.find(id)
      p <- repos.org.people(id)
      m <- repos.org.member(id)
    } yield (o, p, m)) flatMap {
      case (_, _, None) => ok("Organisation is not a member")
      case (None, _, _) => notFound("Organisation not found")
      case (Some(organisation), members, Some(member)) =>
        if (member.funder)
          ok(views.html.v2.organisation.tabs.funder(user, organisation, member))
        else
          renderOrgSupporterTab(user, organisation, member)
    }
  }

  protected def renderOrgSupporterTab(user: ActiveUser, org: Organisation, member: Member)(
    implicit request: Request[Any], handler: be.objectify.deadbolt.scala.DeadboltHandler): Future[Result] =

    (for {
      customer <- repos.core.customer.find(org.identifier, CustomerType.Organisation) if customer.nonEmpty
      charges <- repos.core.charge.findByCustomer(customer.get.id.get)
      cards <- repos.core.card.findByCustomer(customer.get.id.get)
    } yield (customer.get.id.get, charges, cards)) flatMap { case (customerId, charges, cards) =>
      val card = cards.filter(_.active).head
      val fee = Payment.countryBasedFees(org.countryCode)
      ok(views.html.v2.organisation.tabs.supporter(user, org, member, charges, card, customerId, fee, apiPublicKey))
    }

  protected def renderSupporterTab(user: ActiveUser, person: Person, member: Member)(
      implicit request: Request[Any], handler: be.objectify.deadbolt.scala.DeadboltHandler): Future[Result] =

    (for {
      customer <- repos.core.customer.find(person.identifier, CustomerType.Person) if customer.nonEmpty
      charges <- repos.core.charge.findByCustomer(customer.get.id.get)
      cards <- repos.core.card.findByCustomer(customer.get.id.get)
    } yield (customer.get.id.get, charges, cards)) flatMap { case (customerId, charges, cards) =>
      val card = cards.filter(_.active).head
      val fee = Payment.countryBasedFees(person.address.countryCode)
      ok(views.html.v2.person.tabs.supporter(user, person, member, charges, card, customerId, fee, apiPublicKey))
    }

}
