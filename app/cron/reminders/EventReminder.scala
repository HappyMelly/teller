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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing
 * Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package cron.reminders

import javax.inject.Inject

import models.Activity
import models.cm.event.EventRequest
import models.repository.Repositories
import org.joda.time.{Duration, LocalDate}
import play.api.Play
import play.api.Play.current
import services.integrations.{Email, EmailComponent, Integrations}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Contains methods for notifying Teller users about the status of their events
 */
class EventReminder @Inject() (val email: EmailComponent, val repos: Repositories) extends Integrations {

  /**
   * Sends email notifications to facilitators asking to confirm or delete
   *  past events which are unconfirmed
   */
  def sendPostFactumConfirmation() = repos.cm.brand.findAll map { brands =>
    brands.foreach { brand ⇒
      repos.cm.event.findByParameters(brandId = brand.id,future = Some(false),confirmed = Some(false)) map { events =>
        events.foreach { event ⇒
          val subject = "Confirm your event " + event.title
          val url = Play.configuration.getString("application.baseUrl").getOrElse("")
          val body = mail.templates.event.html.confirm(event, brand, url).toString()
          email.send(event.facilitators(repos), subject, body, brand.sender)
          val msg = "confirmation email for event %s (id = %s)".format(event.title, event.id.get.toString)
          Activity.insert("Teller", Activity.Predicate.Sent, msg)(repos)
        }
      }
    }
  }

  /**
    * Sends email notifications about upcoming events to the users who left
    */
  def sendUpcomingEventsNotification() = repos.cm.rep.event.request.findWithOneParticipant map { results =>
    results.groupBy(_.brandId).foreach { case (brandId, requests) =>
      val endOfPeriod = LocalDate.now().plusMonths(3)
      repos.cm.brand.get(brandId).foreach { brand =>
        val futureEvents = repos.cm.event.findByParameters(Some(brandId), public = Some(true), future = Some(true),
          archived = Some(false))
        futureEvents map { unfilteredEvents =>
          val events = unfilteredEvents.filter(_.schedule.start.isBefore(endOfPeriod))
          requests.filter(request => valid(request)).foreach { request =>
            val suitableEvents = events.filter(_.location.countryCode == request.countryCode)
            if (suitableEvents.nonEmpty) {
              val url = fullUrl(controllers.routes.EventRequests.unsubscribe(request.hashedId).url)
              val body = mail.templates.event.html.upcomingNotification(suitableEvents, brand, request, url)(repos)
              val subject = s"Upcoming ${brand.name} events"
              email.send(Seq(request), subject, body.toString(), brand.sender)
            }
          }
        }
      }
    }
  }

  /**
   * Sends email notifications to facilitators asking to confirm
   *  upcoming events which are unconfirmed
   */
  def sendUpcomingConfirmation() = repos.cm.brand.findAll map { brands =>
    brands.foreach { brand ⇒
      val today = LocalDate.now().toDate.getTime
      repos.cm.event.findByParameters(brandId = brand.id,future = Some(true),confirmed = Some(false)) map { events =>
        events.filter { x =>
          val duration = new Duration(today, x.schedule.start.toDate.getTime)
          duration.getStandardDays == 7 || duration.getStandardDays == 30
        }.foreach { event ⇒
          val subject = "Confirm your event " + event.title
          val url = Play.configuration.getString("application.baseUrl").getOrElse("")
          val body = mail.templates.event.html.confirmUpcoming(event, brand, url).toString()
          email.send(event.facilitators(repos), subject, body, brand.sender)
          val msg = "upcoming confirmation email for event %s (id = %s)".format(
            event.title,
            event.id.get.toString)
          Activity.insert("Teller", Activity.Predicate.Sent, msg)(repos)
        }
      }
    }
  }

  protected def fullUrl(url: String) =
    Play.configuration.getString("application.baseUrl").getOrElse("") + url

  /**
    * Returns true if the given request is time valid
    *
    * @param request Event request
    */
  protected def valid(request: EventRequest): Boolean = {
    val now = LocalDate.now()
    request.start.map(_.isBefore(now)).getOrElse(true) && request.end.map(_.isAfter(now)).getOrElse(true)
  }
}
