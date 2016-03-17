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

import models.repository.Repositories
import models.{Activity, ProfileStrength}
import play.api.Play
import play.api.Play.current
import services.integrations.{Email, Integrations}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Contains methods for notifying Teller users about the quality of their
 * profile
 */
class ProfileStrengthReminder @Inject() (val email: Email, val repos: Repositories) extends Integrations {

  /**
   * Sends profile strength reminders to all facilitators with profile strength
   *  less than 80
   */
  def sendToFacilitators() = {
    val queries = for {
      l <- repos.license.findActive
      p <- repos.profileStrength.find(l.map(_.licenseeId).distinct, org = false)
    } yield p
    val withRanks = queries map { profiles =>
      ProfileStrength.calculateRanks(profiles).
        filter(_._1.progress < 80).
        filterNot(x => x._1.incompleteSteps.length == 1 && x._1.incompleteSteps.exists(_.name == "member")).
        sortBy(_._1.objectId)
    }
    val peopleWithRanks = for {
      r <- withRanks
      p <- repos.person.find(r.map(_._1.objectId))
    } yield p.sortBy(_.identifier).zip(r)
    peopleWithRanks map { people =>
      for ((person, (strength, rank)) <- people) {
        val subject = "Make your profile shine"
        val url = Play.configuration.getString("application.baseUrl").getOrElse("") + "/profile"
        val body = mail.templates.html.profileStrength(person.firstName, rank, strength, url).toString()
        email.send(Set(person), None, None, subject, body, richMessage = true)
        val msg = "profile strength reminder email for facilitator %s (id = %s)".format(
          person.fullName,
          person.id.get.toString)
        Activity.insert("Teller", Activity.Predicate.Sent, msg)(repos)
      }
    }
  }
}
