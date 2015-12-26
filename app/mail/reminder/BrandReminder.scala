package mail.reminder

import models.service.Services
import services.integrations.Integrations

/**
  * Contains methods for notifying Teller users about brand-related events
  */
object BrandReminder extends Services with Integrations {

  def sendLicenseExpirationReminder(): Unit = {
    brandService.findAllWithSettings.filter(_.settings.licenseExpirationEmail).foreach { view =>
      val facilitatorIds = licenseService.findAll.filter(_.expiring).map(_.licenseeId).distinct
      personService.find(facilitatorIds).foreach { person =>
        val subject = s"Your ${view.brand.name} License Expires This Month"
        email.send(Set(person), None, None, subject,
          mail.templates.brand.html.licenseExpiring(view.brand, view.settings.licenseExpirationEmailBody.get).toString(),
          from = view.brand.name, richMessage = true)
      }
    }
  }
}
