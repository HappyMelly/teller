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
import models.Member
import models.repository.Repositories
import org.joda.time.{DateTime, Duration, LocalDate}
import play.api.Logger
import services.integrations.{EmailComponent, Integrations}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Contains methods for notifying Happy Melly members about different membership-related events
  */
class MembershipReminder @Inject()(val email: EmailComponent, val repos: Repositories) extends Integrations {

  def sendOneMonthExpirationReminder(): Unit = repos.member.findAll map { members =>
    val totalNumber = members.count(_.active)
    val validMembers = renewedSupporters(members).filter(_.until == LocalDate.now().plusMonths(1))
    Logger.info(s"Start sending one month expiration reminders to ${validMembers.length} members")
    validMembers.foreach { member =>
      val subject = "Your Happy Melly Membership"
      val body = mail.members.html.oneMonthReminder(member, totalNumber, Members.profileUrl(member)).toString()

      val recipient = if (member.person)
        Future.successful(member.memberObj._1.get)
      else
        repos.org.people(member.objectId).map(_.head)
      recipient map { value =>
        email.sendSystem(Seq(value), subject, body)
      }
    }
    Logger.info(s"Stop sending one month expiration reminders to ${validMembers.length} members")
  }

  def sendTwoWeeksExpirationReminder(): Unit = repos.member.findAll map { members =>
    val validMembers = renewedSupporters(members).filter(_.until == LocalDate.now().plusWeeks(2))
    Logger.info(s"Start sending two weeks expiration reminders to ${validMembers.length} members")
    validMembers.foreach { member =>
      val subject = "Your Happy Melly Membership"
      val membershipDuration = new Duration(member.since.toDateTimeAtCurrentTime, DateTime.now())
      val membershipInDays = membershipDuration.getStandardDays
      val body = mail.members.html.twoWeeksReminder(member,
        membershipInDays,
        Utilities.fullUrl(Members.profileUrl(member))).toString()

      val recipient = if (member.person)
        Future.successful(member.memberObj._1.get)
      else
        repos.org.people(member.objectId).map(_.head)
      recipient map { value =>
        email.sendSystem(Seq(value), subject, body)
      }
    }
    Logger.info(s"Stop sending two weeks expiration reminders to ${validMembers.length} members")
  }

  protected def renewedSupporters(members: List[Member]): List[Member] =
    members.filter(_.active).filter(_.renewal).filter(_.funder == false)
}
