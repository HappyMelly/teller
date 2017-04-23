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

package cron

import javax.inject.Inject

import models.core.payment.{CustomerType, GatewayWrapper, Payment}
import models.repository.Repositories
import org.joda.time.{Duration, LocalDate}
import play.api.{Logger, Play}
import play.api.Play.current

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Manages membership subscription update
  */
class SubscriptionUpdater @Inject() (val repos: Repositories) {

  def update() = {
    val today = LocalDate.now()
    Logger.info(msg(s"Start updating subscriptions for $today"))
    repos.member.findAll map { members =>
      val membersOfInterest = members.filter(_.renewal).filterNot(_.funder).
        filter(_.plan.isEmpty).filter(x => x.until.isEqual(today))
      membersOfInterest.foreach { member =>
        val typ = if (member.person) CustomerType.Person else CustomerType.Organisation
        repos.core.customer.find(member.objectId, typ) map {
          case None => Logger.error(msg(s"Member ${member.identifier} doesn't have customer record"))
          case Some(customer) =>
            val countryCode = member.countryCode
            val planId = Payment.stripePlanId(countryCode, yearly = member.yearly)
            val key = Play.configuration.getString("stripe.secret_key").get

            try {
              val payment = new Payment(key)
              payment.updateSubscription(customer.remoteId, planId)
              val fee = Payment.countryBasedPlans(countryCode)._2

              val updated = member.copy(fee = fee.toDouble, plan = Some(planId))
              repos.member.update(updated)
            } catch {
              case e: RuntimeException => Logger.error(e.getMessage)
            }
        }
      }
      Logger.info(msg(s"${membersOfInterest.length} were updated"))
      Logger.info(msg("Stop updating subscriptions"))
    }
  }

  protected def msg(str: String): String = s"SubscriptionUpdater: $str"

}
