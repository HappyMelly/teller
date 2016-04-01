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

import javax.inject.{Inject, Named}

import akka.actor.ActorRef
import models._
import models.cm.EvaluationStatus
import models.cm.brand.ApiConfig
import models.cm.event.AttendeeView
import models.repository.Repositories
import org.joda.time.{Duration, LocalDate}
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Contains methods for notifying Teller users about their evaluations
 */
class EvaluationReminder @Inject() (val repos: Repositories, @Named("evaluation-mailer") mailer: ActorRef) {

  /**
   * Sends evaluation and confirmation requests to participants of events
   * on the first, the thirds and the sevenths days after the event
   */
  def sendToAttendees() = repos.cm.brand.findAll map { brands =>
    Logger.info("Sending evaluation and confirmation reminders to attendees")
    brands.foreach { brand ⇒
      val query = for {
        e <- validEvents(repos, brand.id)
        a <- repos.cm.evaluation.findEvaluationsByEvents(e)
        c <- repos.cm.rep.brand.config.findByBrand(brand.identifier)
      } yield (a, c)
      query map { case (attendees, apiConfig) =>
        if (isEvaluationModuleActive(apiConfig)) {
          handleAttendeesWithoutEvaluation(attendees, brand, apiConfig.get)
        }
        handleUnconfirmedAttendees(attendees, brand)
      }
    }
  }

  protected def isEvaluationModuleActive(apiConfig: Option[ApiConfig]): Boolean =
    apiConfig.exists(_.isEvaluationModuleActive)

  protected def handleAttendeesWithoutEvaluation(attendees: Seq[AttendeeView], brand: Brand, config: ApiConfig) = {
    attendees.filter(_.evaluation.isEmpty).foreach { view =>
      val welcomeMsg = s"Hi ${view.attendee.firstName},"
      val facilitatorId = view.event.facilitatorIds(repos).head
      val body = mail.evaluation.html.requestBody(config, welcomeMsg, view.event, facilitatorId).toString()
      mailer ! ("request", view.attendee, brand, body)
    }
  }

  protected def handleUnconfirmedAttendees(attendees: Seq[AttendeeView], brand: Brand) = {
    attendees.filter(_.evaluation.exists(_.status == EvaluationStatus.Unconfirmed)).foreach { view =>
      view.evaluation.get.confirmationId.foreach { token =>
        mailer ! ("confirm", view.attendee, brand, token)
      }
    }
  }

  protected def validEvents(repos: Repositories, brandId: Option[Long]): Future[List[Long]] = {
    val today = LocalDate.now().toDate.getTime
    val unfilteredEvents = repos.cm.event.findByParameters(brandId = brandId, future = Some(false))
    unfilteredEvents.map(_.filter(_.followUp).filter { event =>
      val duration = (new Duration(event.schedule.end.toDate.getTime, today)).getStandardDays
      duration == 1 || duration == 3 || duration == 7
    }.map(_.id.get))
  }

}
