/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package controllers

import models.UserRole.Role.{Admin, Viewer}
import models._
import models.service.Services
import org.joda.time.DateTime
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc._
import securesocial.core.RuntimeEnvironment

/**
 * User administration controller.
 */
class UserAccounts(environment: RuntimeEnvironment[ActiveUser])
    extends Controller
    with Security
    with Services {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  val userForm = Form(tuple(
    "personId" -> longNumber,
    "role" -> optional(text)))

  /**
   * Switches active role to Facilitator if it was Brand Coordinator, and
   *  visa versa
   */
  def switchRole = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val account = user.account.copy(activeRole = !user.account.activeRole)
      userAccountService.updateActiveRole(user.account.personId, account.activeRole)
      env.authenticatorService.fromRequest.foreach(auth ⇒ auth.foreach {
        _.updateUser(ActiveUser(user.identity, account, user.person,
          user.person.member))
      })
      Redirect(request.headers("referer"))
  }

  /**
   * Updates a person’s user role.
   */
  def update = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      userForm.bindFromRequest.fold(
        form ⇒ BadRequest("invalid form data"),
        account ⇒ {
          val (personId, role) = account
          personService.find(personId).map { person ⇒
            Logger.debug(s"update role for person (${person.fullName}}) to $role")
            val activityObject = Messages("object.UserAccount", person.fullNamePossessive)
            if (role.isDefined) {
              if (userAccountService.findRole(personId).isDefined) {
                userAccountService.updateRole(personId, role.get)
              } else {
                val account = UserAccount(None, personId, role.get, person.socialProfile.twitterHandle,
                  person.socialProfile.facebookUrl,
                  person.socialProfile.googlePlusUrl,
                  person.socialProfile.linkedInUrl)
                userAccountService.insert(account)
              }
            } else { // Remove the account
              userAccountService.delete(personId)
            }
            val activity = Activity.insert(user.name, Activity.Predicate.Updated, activityObject)
            val dateStamp = person.dateStamp.copy(updated = DateTime.now, updatedBy = user.name)
            person.copy(dateStamp = dateStamp).update
            Redirect(routes.People.details(person.id.getOrElse(0))).flashing("success" -> activity.toString)
          }.getOrElse(BadRequest("invalid form data - person not found"))
        })
  }

}
