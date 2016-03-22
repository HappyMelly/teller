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
package cron.reminders

import javax.inject.Inject

import controllers.Utilities
import controllers.hm.Members
import models.core.payment.Customer
import models.repository.Repositories
import models.{Member, Person}
import play.api.Logger
import services.integrations.{EmailComponent, Integrations}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Contains methods for notifying Teller users about card-related events
  */
class CardReminder @Inject()(val email: EmailComponent, val repos: Repositories) extends Integrations {

  def sendExpirationReminder(): Unit = {
    customersWithExpiringCards map { case (customers, members) =>
      val withOptionalMembers = customers.map(c => (c, members.find(_.relatedCustomer(c)))).filter(_._2.nonEmpty)
      val withMembers = withOptionalMembers.map(v => (v._1, v._2.get))
      sendReminderToPeople(withMembers.filter(_._1.isPerson))
      sendReminderToOrgs(withMembers.filter(_._1.isOrg))
    }
  }

  protected def customersWithExpiringCards: Future[(Seq[Customer], Seq[Member])] = for {
    cards <- repos.core.card.findExpiring
    customers <- repos.core.customer.find(cards.map(_.customerId))
    members <- repos.member.findByObjects(customers.map(_.objectId))
  } yield (customers, members)

  protected def msg(str: String): String = s"CardExpirationReminder: $str"

  protected def sendReminderToOrgs(customers: Seq[(Customer, Member)]) = {
    repos.org.find(customers.map(_._1.objectId)) map { orgs =>
      Logger.info(s"Sending card expiration reminder to ${orgs.length} organisations")
      orgs.foreach { org =>
        val member = customers.find(_._2.objectId == org.identifier)
        member.foreach { value =>
          repos.org.people(org.identifier) map { people => sendReminder(org.name, people.head, value._2) }
        }
      }
    }
  }

  protected def sendReminderToPeople(customers: Seq[(Customer, Member)]) = {
    repos.person.find(customers.map(_._1.objectId)) map { people =>
      Logger.info(s"Sending card expiration reminder to ${people.length} people")
      people.foreach { person =>
        val member = customers.find(_._2.objectId == person.identifier)
        member.foreach { value => sendReminder(person.firstName, person, value._2) }
      }
    }
  }

  protected def sendReminder(name: String, person: Person, member: Member) = {
    val subject = "Your Payment Method Has Expired"
    val url = Utilities.fullUrl(Members.profileUrl(member))
    val body = mail.templates.members.html.cardExpiring(name, member, url)
    email.sendSystem(Seq(person), subject, body.toString)
  }
}
