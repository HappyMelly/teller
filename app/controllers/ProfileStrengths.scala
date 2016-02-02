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

import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import be.objectify.deadbolt.scala.cache.HandlerCache
import models.UserRole.Role._
import models.service.Services
import models.{Person, ProfileStrength}
import play.api.i18n.MessagesApi
import scala.concurrent.Future
import services.TellerRuntimeEnvironment

class ProfileStrengths @Inject() (override implicit val env: TellerRuntimeEnvironment,
                                  override val messagesApi: MessagesApi,
                                  deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
    extends Security(deadbolt, handlers, actionBuilder)(messagesApi, env)
    with Services {

  /**
   * Returns profile strength widget for a person
   *
   * @param id Person identifier
   * @param steps If true completion steps are shown
   */
  def personWidget(id: Long, steps: Boolean) = AsyncSecuredRestrictedAction(Viewer) {
    implicit request ⇒ implicit handler => implicit user ⇒
      profileStrengthService.find(id) flatMap {
        case Some(strength) ⇒ ok(views.html.v2.profile.widget(id, strength, steps))
        case None =>
          if (id == user.person.identifier) {
            initializeProfileStrength(user.person) flatMap { profileStrength =>
              profileStrengthService.insert(profileStrength)
            } flatMap { profileStrength =>
              ok(views.html.v2.profile.widget(id, profileStrength, steps))
            }
          } else {
            badRequest("Widget was requested by a wrong person")
          }
      }
  }

  /**
   * Returns new profile strength based on the given person data
   *
   * @param person Person
   */
  protected def initializeProfileStrength(person: Person): Future[ProfileStrength] = {
    val id = person.identifier
    val strength = ProfileStrength.empty(id, org = false)
    val strengthWithMember = if (person.isMember)
      ProfileStrength.forMember(strength)
    else
      strength
    val query = for {
      licenses <- licenseService.activeLicenses(id)
      languages <- facilitatorService.languages(person.identifier)
    } yield (licenses, languages)
    val strengthWithFacilitator = query map { case (licenses, languages) =>
      if (licenses.nonEmpty) {
        val strengthWithLanguages = ProfileStrength.forFacilitator(strengthWithMember)
        if (languages.nonEmpty)
          strengthWithLanguages.markComplete("language")
        else
          strengthWithLanguages
      } else {
        strengthWithMember
      }
    }
    strengthWithFacilitator map { value =>
      ProfileStrength.forPerson(value, person)
    }
  }
}
