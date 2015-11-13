package mail.reminder

import models.Activity
import models.service.Services
import org.joda.time.{Duration, LocalDate}
import play.api.Play
import play.api.Play.current
import services.integrations.Integrations

/**
 * Contains methods for notifying Teller users about the status of their events
 */
object EventReminder extends Services with Integrations {

  /**
   * Sends email notifications to facilitators asking to confirm or delete
   *  past events which are unconfirmed
   */
  def sendPostFactumConfirmation() = brandService.findAll.foreach { brand ⇒
    eventService.findByParameters(
      brandId = brand.id,
      future = Some(false),
      confirmed = Some(false)).foreach { event ⇒
      val subject = "Confirm your event " + event.title
      val url = Play.configuration.getString("application.baseUrl").getOrElse("")
      val body = mail.templates.html.confirm(event, brand, url).toString()
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

  /**
   * Sends email notifications to facilitators asking to confirm
   *  upcoming events which are unconfirmed
   */
  def sendUpcomingConfirmation() = brandService.findAll.foreach { brand ⇒
    val today = LocalDate.now().toDate.getTime
    eventService.findByParameters(
      brandId = brand.id,
      future = Some(true),
      confirmed = Some(false)).filter { x =>
      val duration = new Duration(today, x.schedule.start.toDate.getTime)
      duration.getStandardDays == 7 || duration.getStandardDays == 30
    }.foreach { event ⇒
      val subject = "Confirm your event " + event.title
      val url = Play.configuration.getString("application.baseUrl").getOrElse("")
      val body = mail.templates.html.confirmUpcoming(event, brand, url).toString()
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
