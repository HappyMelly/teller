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

import controllers.Utilities
import models.cm.Event
import models.cm.brand.ApiConfig
import models.cm.event.EventRequest
import models.repository.Repositories
import models.{Activity, BrandWithSettings}
import org.joda.time.LocalDate
import services.integrations.{EmailComponent, Integrations}

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
          val url = Utilities.fullUrl(controllers.cm.routes.Events.details(event.identifier).url)
          val body = mail.event.html.confirm(event, brand, url).toString()
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
      validUpcomingEvents(brandId) { (events, brandWithSettings, apiConfig) =>
        if (isEventRequestsActive(apiConfig)) {
          requests.filter(request => valid(request)).foreach { request =>
            val suitableEvents = events.filter(_.location.countryCode == request.countryCode)
            val brand = brandWithSettings.brand
            if (suitableEvents.nonEmpty) {
              val url = Utilities.fullUrl(controllers.routes.EventRequests.unsubscribe(request.hashedId).url)
              val body = mail.event.html.upcomingNotification(suitableEvents, brand, request, apiConfig.get, url)(repos)
              val subject = s"Upcoming ${brand.name} events"
              email.send(Seq(request), subject, body.toString(), brand.sender)
            }
          }
        }
      }
    }
  }

  protected def isEventRequestsActive(apiConfig: Option[ApiConfig]): Boolean = apiConfig.exists(_.event.nonEmpty)

  /**
    * Returns true if the given request is time valid
    *
    * @param request Event request
    */
  protected def valid(request: EventRequest): Boolean = {
    val now = LocalDate.now()
    request.start.forall(_.isBefore(now)) && request.end.forall(_.isAfter(now))
  }

  protected def validUpcomingEvents(brandId: Long)(f: (Seq[Event], BrandWithSettings, Option[ApiConfig]) => Unit) = {
    val endOfPeriod = LocalDate.now().plusMonths(3)
    repos.cm.brand.getWithApiConfig(brandId).foreach { case (brandWithSettings, apiConfig) =>
      val futureEvents = repos.cm.event.findByParameters(Some(brandId), public = Some(true), future = Some(true),
        archived = Some(false))
      futureEvents map { unfilteredEvents =>
        val events = unfilteredEvents.filter(_.schedule.start.isBefore(endOfPeriod))
        f(events, brandWithSettings, apiConfig)
      }
    }
  }
}
