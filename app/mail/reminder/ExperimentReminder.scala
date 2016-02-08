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
package mail.reminder

import javax.inject.Inject

import models.service.Services
import play.api.Play
import play.api.Play.current
import services.integrations.{Email, Integrations}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Contains methods for notifying Teller users about their experiments
 */
class ExperimentReminder @Inject() (val email: Email, val services: Services) extends Integrations {

  def sendStatus(): Unit = {
    (for {
      experiments <- services.experimentService.findAll()
      members <- services.memberService.findAll
    } yield (experiments.groupBy(_.memberId), members)) map { case (experiments, unfilteredMembers) =>
      val members = unfilteredMembers.map(member =>
          (member, experiments.find(_._1 == member.identifier).map(_._2).getOrElse(List()))).
        filter(member => member._2.nonEmpty)
      members.foreach { member =>
        val url = if (member._1.person)
          controllers.routes.People.details(member._1.objectId).url
        else
          controllers.routes.Organisations.details(member._1.objectId).url
        val body = mail.templates.members.html.experimentStatus(member._1.name,
          member._2, fullUrl(url)).toString()
        val subject = "Update your experiments"
        val recipient = if (member._1.person)
          Future.successful(member._1.memberObj._1.get)
        else
          services.orgService.people(member._1.objectId).map(_.head)
        recipient map { value =>
          email.send(Set(value), None, None, subject, body, richMessage = true)
        }
      }
    }
  }

  /**
   * Returns an url with domain
    *
    * @param url Domain-less part of url
   */
  protected def fullUrl(url: String): String = {
    Play.configuration.getString("application.baseUrl").getOrElse("") + url
  }
}
