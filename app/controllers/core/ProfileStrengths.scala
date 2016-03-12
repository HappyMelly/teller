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
package controllers.core

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.Security
import models.UserRole.Role._
import models.repository.Repositories
import models.{ProfileType, ActiveUser, ProfileStrength}
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

class ProfileStrengths @Inject()(override implicit val env: TellerRuntimeEnvironment,
                                 override val messagesApi: MessagesApi,
                                 val repos: Repositories,
                                 deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
    extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env) {

  /**
   * Returns profile strength widget for a person
   *
   * @param id Person identifier
   * @param steps If true completion steps are shown
   */
  def personWidget(id: Long, steps: Boolean) = RestrictedAction(Viewer) {
    implicit request ⇒ implicit handler => implicit user ⇒
      repos.profileStrength.find(id) flatMap {
        case Some(strength) ⇒ ok(views.html.v2.profile.widget(id, strength, steps))
        case None =>
          if (id == user.person.identifier) {
            initializeProfileStrength(user) flatMap { profileStrength =>
              repos.profileStrength.insert(profileStrength)
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
   * @param user Active user
   */
  protected def initializeProfileStrength(user: ActiveUser): Future[ProfileStrength] = {
    val id = user.person.identifier
    val strength = ProfileStrength.empty(id, org = false)
    val strengthWithMember = if (user.member.isDefined)
      ProfileStrength.forMember(strength)
    else
      strength
    val query = for {
      licenses <- repos.cm.license.activeLicenses(id)
      languages <- repos.cm.facilitator.languages(id)
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
    strengthWithFacilitator flatMap { value =>
      repos.socialProfile.find(user.person.identifier, ProfileType.Person) map { profile =>
        user.person.profile_=(profile)
        ProfileStrength.forPerson(value, user.person)
      }
    }
  }
}
