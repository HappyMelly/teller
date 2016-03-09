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
import models.core.payment.CustomerType
import models.repository.Repositories
import models.UserRole.Role._
import play.api.Play
import play.api.Play.current
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment

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
      person <- repos.person.find(id)
      member <- repos.person.member(id)
      customer <- repos.core.customer.find(id, CustomerType.Person) if customer.nonEmpty
      charges <- repos.core.charge.findByCustomer(customer.get.id.get)
      cards <- repos.core.card.findByCustomer(customer.get.id.get)
    } yield (person, member, charges, cards)) flatMap {
      case (_, None, _, _) => ok("Person is not a member")
      case (None, _, _, _) => notFound("Person not found")
      case (Some(person), Some(member), charges, cards) =>
        val card = cards.filter(_.active).head
        ok(views.html.v2.person.tabs.membership(user, person, member, charges, card, apiPublicKey))
    }
  }

}
