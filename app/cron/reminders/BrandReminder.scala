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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package cron.reminders

import javax.inject.Inject

import models.repository.Repositories
import services.integrations.{Email, EmailComponent, Integrations}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Contains methods for notifying Teller users about brand-related events
  */
class BrandReminder @Inject() (val email: EmailComponent, val repos: Repositories) extends Integrations {

  def sendLicenseExpirationReminder(): Unit = {
    (for {
      brands <- repos.cm.brand.findAllWithSettings
      licenses <- repos.cm.license.findAll
    } yield (brands, licenses)) map { case (brands, licenses) =>
      brands.filter(_.settings.licenseExpirationEmail).foreach { view =>
        val facilitatorIds = licenses.filter(_.expiring).map(_.licenseeId).distinct
        val body = mail.templates.brand.html.licenseExpiring(view.brand, view.settings.licenseExpirationEmailBody.get).toString()
        repos.person.find(facilitatorIds) map { people =>
          people.foreach { person =>
            val subject = s"Your ${view.brand.name} License Expires This Month"
            email.send(Seq(person), subject, body, view.brand.sender)
          }
        }
      }
    }
  }
}
