package mail.reminder

import controllers.routes
import models._
import models.event.Attendee
import models.service.Services
import org.joda.time.{Duration, LocalDate}
import services.integrations.Integrations

/**
 * Contains methods for notifying Teller users about their evaluations
 */
object EvaluationReminder extends Services with Integrations {

  /**
   * Sends evaluation and confirmation requests to participants of events
   * on the first, the thirds and the sevenths days after the event
   */
  def sendToAttendees(): Unit = brandService.findAll.foreach { brand ⇒
    if (brand.evaluationUrl.isDefined) {
      val today = LocalDate.now().toDate.getTime
      val events = eventService.
        findByParameters(brandId = brand.id, future = Some(false)).
        filter(_.followUp).
        filter { event =>
          val duration = (new Duration(event.schedule.end.toDate.getTime, today)).getStandardDays
          duration == 1 || duration == 3 || duration == 7
        }.map(_.id.get)
      val attendees = evaluationService.findEvaluationsByEvents(events)
      attendees.filter(view => view.status.isEmpty).foreach { view =>
        val welcomeMsg = s"Hi ${view.attendee.firstName},"
        val facilitatorId = view.event.facilitatorIds.head
        val body = mail.templates.evaluation.html.requestBody(welcomeMsg, view.event, facilitatorId, brand.evaluationUrl).toString()
        sendEvaluationRequest(view.attendee, brand, body)
      }
      attendees.filter(_.status.exists(_ == EvaluationStatus.Unconfirmed)).foreach { view =>
        val defaultHook = routes.Evaluations.confirm("").url
        view.confirmationToken.foreach { token =>
          sendConfirmRequest(view.attendee, brand, defaultHook, token)
        }
      }
    }
  }

  /**
   * Sends request to evaluate an event to the given attendee
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
