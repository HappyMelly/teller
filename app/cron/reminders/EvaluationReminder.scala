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

import controllers.routes
import models._
import models.event.Attendee
import models.repository.Repositories
import org.joda.time.{Duration, LocalDate}
import services.integrations._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Contains methods for notifying Teller users about their evaluations
 */
class EvaluationReminder @Inject() (val email: EmailComponent, val repos: Repositories) extends Integrations {

  /**
   * Sends evaluation and confirmation requests to participants of events
   * on the first, the thirds and the sevenths days after the event
   */
  def sendToAttendees() = repos.brand.findAll map { brands =>
    brands.foreach { brand ⇒
      if (brand.evaluationUrl.isDefined) {
        val today = LocalDate.now().toDate.getTime
        val unfilteredEvents = repos.event.findByParameters(brandId = brand.id, future = Some(false))
        val events = unfilteredEvents.map(_.filter(_.followUp).filter { event =>
          val duration = (new Duration(event.schedule.end.toDate.getTime, today)).getStandardDays
          duration == 1 || duration == 3 || duration == 7
        }.map(_.id.get))
        val attendees = for {
          e <- events
          a <- repos.evaluation.findEvaluationsByEvents(e)
        } yield a
        attendees.map(_.filter(_.evaluation.isEmpty).foreach { view =>
          val welcomeMsg = s"Hi ${view.attendee.firstName},"
          val facilitatorId = view.event.facilitatorIds(repos).head
          val body = mail.templates.evaluation.html.requestBody(welcomeMsg, view.event, facilitatorId, brand.evaluationUrl).toString()
          sendEvaluationRequest(view.attendee, brand, body)
        })
        attendees.map(_.filter(_.evaluation.exists(_.status == EvaluationStatus.Unconfirmed)).foreach { view =>
          val defaultHook = routes.Evaluations.confirm("").url
          view.evaluation.get.confirmationId.foreach { token =>
            sendConfirmRequest(view.attendee, brand, defaultHook, token)
          }
        })
      }
    }
  }

  /**
   * Sends request to evaluate an event to the given attendee
    *
    * @param attendee Attendee
   * @param brand Brand
   * @param body Message
   */
  def sendEvaluationRequest(attendee: Attendee, brand: Brand, body: String): Unit = {
    val subject = "Your Opinion Counts!"
    email.send(Set(attendee), None, None, subject,
      mail.templates.evaluation.html.request(brand, attendee, body).toString(),
      from = brand.name, richMessage = true)
  }

  /**
   * Sends request to confirm an evaluation to the given attendee
   *
   * @param attendee Attendee
   * @param brand Brand
   * @param hook Confirmation url
   * @param token Confirmation unique token
   */
  def sendConfirmRequest(attendee: Attendee, brand: Brand, hook: String, token: String): Unit = {
    val subject = "Confirm your %s evaluation" format brand.name
    val url = brand.evaluationHookUrl.
      map(x ⇒ if (x.endsWith("/")) x else x + "/").
      getOrElse("https://" + hook).
      concat(token)
    email.send(Set(attendee), None, None, subject,
      mail.templates.evaluation.html.confirm(brand, attendee.fullName, url).toString(),
      from = brand.name, richMessage = true)
  }
}
