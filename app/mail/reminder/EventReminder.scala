package mail.reminder

import models.Activity
import models.event.EventRequest
import models.service.Services
import org.joda.time.{Duration, LocalDate}
import play.api.Play
import play.api.Play.current
import services.integrations.Integrations

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Contains methods for notifying Teller users about the status of their events
 */
object EventReminder extends Services with Integrations {

  /**
   * Sends email notifications to facilitators asking to confirm or delete
   *  past events which are unconfirmed
   */
  def sendPostFactumConfirmation() = brandService.findAll map { brands =>
    brands.foreach { brand ⇒
      eventService.findByParameters(brandId = brand.id,future = Some(false),confirmed = Some(false)) map { events =>
        events.foreach { event ⇒
          val subject = "Confirm your event " + event.title
          val url = Play.configuration.getString("application.baseUrl").getOrElse("")
          val body = mail.templates.event.html.confirm(event, brand, url).toString()
          email.send(
            event.facilitators.toSet,
            None,
            None,
            subject,
            body,
            from = brand.name,
            richMessage = true)
          val msg = "confirmation email for event %s (id = %s)".format(
            event.title,
            event.id.get.toString)
          Activity.insert("Teller", Activity.Predicate.Sent, msg)
        }
      }
    }
  }

  /**
    * Sends email notifications about upcoming events to the users who left
    */
  def sendUpcomingEventsNotification() = eventRequestService.findWithOneParticipant map { results =>
    results.groupBy(_.brandId).foreach { case (brandId, requests) =>
      val endOfPeriod = LocalDate.now().plusMonths(3)
      brandService.get(brandId).foreach { brand =>
        val futureEvents = eventService.findByParameters(Some(brandId), public = Some(true), future = Some(true),
          archived = Some(false))
        futureEvents map { unfilteredEvents =>
          val events = unfilteredEvents.filter(_.schedule.start.isBefore(endOfPeriod))
          requests.filter(request => valid(request)).foreach { request =>
            val suitableEvents = events.filter(_.location.countryCode == request.countryCode)
            if (suitableEvents.nonEmpty) {
              val url = fullUrl(controllers.routes.EventRequests.unsubscribe(request.hashedId).url)
              val body = mail.templates.event.html.upcomingNotification(suitableEvents, brand, request, url)
              val subject = s"Upcoming ${brand.name} events"
              email.send(Set(request), None, None, subject, body.toString(), from = brand.name, richMessage = true)
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
  def sendUpcomingConfirmation() = brandService.findAll map { brands =>
    brands.foreach { brand ⇒
      val today = LocalDate.now().toDate.getTime
      eventService.findByParameters(brandId = brand.id,future = Some(true),confirmed = Some(false)) map { events =>
        events.filter { x =>
          val duration = new Duration(today, x.schedule.start.toDate.getTime)
          duration.getStandardDays == 7 || duration.getStandardDays == 30
        }.foreach { event ⇒
          val subject = "Confirm your event " + event.title
          val url = Play.configuration.getString("application.baseUrl").getOrElse("")
          val body = mail.templates.event.html.confirmUpcoming(event, brand, url).toString()
          email.send(
            event.facilitators.toSet,
            None,
            None,
            subject,
            body,
            richMessage = true)
          val msg = "upcoming confirmation email for event %s (id = %s)".format(
            event.title,
            event.id.get.toString)
          Activity.insert("Teller", Activity.Predicate.Sent, msg)
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
