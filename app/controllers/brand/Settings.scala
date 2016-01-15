/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional terms,
 * you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers.brand

import controllers.{JsonController, Security}
import models.service.Services
import play.api.data.Form
import play.api.data.Forms._
import services.TellerRuntimeEnvironment

/**
  * Manages brand settings
  */
class Settings @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment)
  extends JsonController
  with Services
  with Security {

  /**
    * Turns off license expiration reminder for the given brand
    * @param brandId Brand identifier
    */
  def turnLicenseExpirationReminderOff(brandId: Long) = SecuredBrandAction(brandId) { implicit request =>
    implicit handler => implicit user =>
      brandService.findWithSettings(brandId) map { view =>
        if (view.settings.licenseExpirationEmail) {
          brandService.updateSettings(view.settings.copy(licenseExpirationEmail = false))
        }
        jsonSuccess("Reminder is turned off")
      } getOrElse jsonNotFound("Brand not found")
  }

  /**
    * Turns on license expiration reminder for the given brand
    * @param brandId Brand identifier
    */
  def turnLicenseExpirationReminderOn(brandId: Long) = SecuredBrandAction(brandId) { implicit request =>
    implicit handler => implicit user =>
      Form(single("content" -> nonEmptyText)).bindFromRequest().fold(
        errors => jsonBadRequest("Email body is empty"),
        content => {
          brandService.findWithSettings(brandId) map { view =>
            if (view.settings.licenseExpirationEmail) {
              brandService.updateSettings(view.settings.copy(licenseExpirationEmailBody = Some(content)))
              jsonSuccess("Reminder email is updated")
            } else {
              val settings = view.settings.copy(licenseExpirationEmailBody = Some(content),
                licenseExpirationEmail = true)
              brandService.updateSettings(settings)
              jsonSuccess("Reminder is turned on")
            }
          } getOrElse jsonNotFound("Brand not found")
        }
      )
  }
}
