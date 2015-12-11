package mail.reminder

import models.service.Services
import models.{Activity, ProfileStrength}
import play.api.Play
import play.api.Play.current
import services.integrations.Integrations

/**
 * Contains methods for notifying Teller users about the quality of their
 * profile
 */
object ProfileStrengthReminder extends Services with Integrations {

  /**
   * Sends profile strength reminders to all facilitators with profile strength
   *  less than 80
   */
  def sendToFacilitators() = {
    val licenses = licenseService.findActive
    val profiles = profileStrengthService.find(licenses.map(_.licenseeId).distinct, org = false)
    val withRanks = ProfileStrength.calculateRanks(profiles).
      filter(_._1.progress < 80).
      filterNot(x => x._1.incompleteSteps.length == 1 && x._1.incompleteSteps.exists(_.name == "member")).
      sortBy(_._1.objectId)
    val people = personService.find(withRanks.map(_._1.objectId)).sortBy(_.identifier)
    val peopleWithRanks = people.zip(withRanks)
    for ((person, (strength, rank)) <- peopleWithRanks) {
      val subject = "Make your profile shine"
      val url = Play.configuration.getString("application.baseUrl").getOrElse("") + "/profile"
      val body = mail.templates.html.profileStrength(person.firstName, rank, strength, url).toString()
      email.send(Set(person), None, None, subject, body, richMessage = true)
      val msg = "profile strength reminder email for facilitator %s (id = %s)".format(
        person.fullName,
        person.id.get.toString)
      Activity.insert("Teller", Activity.Predicate.Sent, msg)
    }
  }
}
