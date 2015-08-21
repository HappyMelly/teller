package mail.reminder

import models.service.Services
import models.Activity
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
  def sendConfirmation() = brandService.findAll.foreach { brand ⇒
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
        richMessage = true)
      val msg = "confirmation email for event %s (id = %s)".format(
        event.title,
        event.id.get.toString)
      Activity.insert("Teller", Activity.Predicate.Sent, msg)
    }
  }
}
