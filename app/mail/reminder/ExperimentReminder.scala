package mail.reminder

import models.service.Services
import play.api.Play
import play.api.Play.current
import services.integrations.Integrations

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Contains methods for notifying Teller users about their experiments
 */
object ExperimentReminder extends Services with Integrations {

  def sendStatus(): Unit = {
    (for {
      experiments <- experimentService.findAll()
      members <- memberService.findAll
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
          member._1.memberObj._1.get
        else
          member._1.memberObj._2.get.people.head
        email.send(Set(recipient), None, None, subject, body, richMessage = true)
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
