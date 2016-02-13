package mail.reminder

import javax.inject.Inject
import models.repository.Repositories
import services.integrations.{Email, Integrations}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Contains methods for notifying Teller users about brand-related events
  */
class BrandReminder @Inject() (val email: Email, val services: Repositories) extends Integrations {

  def sendLicenseExpirationReminder(): Unit = {
    (for {
      brands <- services.brand.findAllWithSettings
      licenses <- services.license.findAll
    } yield (brands, licenses)) map { case (brands, licenses) =>
      brands.filter(_.settings.licenseExpirationEmail).foreach { view =>
        val facilitatorIds = licenses.filter(_.expiring).map(_.licenseeId).distinct
        services.person.find(facilitatorIds) map { people =>
          people.foreach { person =>
            val subject = s"Your ${view.brand.name} License Expires This Month"
            email.send(Set(person), None, None, subject,
              mail.templates.brand.html.licenseExpiring(view.brand, view.settings.licenseExpirationEmailBody.get).toString(),
              from = view.brand.name, richMessage = true)
          }
        }
      }
    }
  }
}
