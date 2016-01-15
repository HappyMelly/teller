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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers

import javax.inject.Inject

import models.UserRole.Role._
import models.service.Services
import models.{Person, ProfileStrength}
import play.api.mvc._
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

class ProfileStrengths @Inject() (override implicit val env: TellerRuntimeEnvironment)
    extends Controller
    with Security
    with Services {

  /**
   * Returns profile strength widget for a person
   *
   * @param id Person identifier
   * @param steps If true completion steps are shown
   */
  def personWidget(id: Long, steps: Boolean) = AsyncSecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        profileStrengthService.find(id) map { x ⇒
          Future.successful(Ok(views.html.v2.profile.widget(id, x, steps)))
        } getOrElse {
          if (id == user.person.id.get) {
            val profileStrength = initializeProfileStrength(user.person)
            profileStrengthService.insert(profileStrength)
            Future.successful(Ok(views.html.v2.profile.widget(id, profileStrength, steps)))
          } else {
            Future.successful(BadRequest)
          }
        }
  }

  /**
   * Returns new profile strength based on the given person data
   *
   * @param person Person
   */
  protected def initializeProfileStrength(person: Person): ProfileStrength = {
    val id = person.id.get
    val strength = ProfileStrength.empty(id, org = false)
    val strengthWithMember = if (person.isMember)
      ProfileStrength.forMember(strength)
    else
      strength
    val strengthWithFacilitator = if (licenseService.activeLicenses(id).nonEmpty) {
      val strengthWithLanguages = ProfileStrength.forFacilitator(strengthWithMember)
      if (facilitatorService.languages(person.id.get).nonEmpty)
        strengthWithLanguages.markComplete("language")
      else
        strengthWithLanguages
    } else {
      strengthWithMember
    }
    ProfileStrength.forPerson(strengthWithFacilitator, person)
  }
}
