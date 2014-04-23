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

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models._
import models.UserRole.Role.Admin
import play.api.Logger
import play.api.i18n.Messages
import org.joda.time.DateTime

/**
 * User administration controller.
 */
object UserAccounts extends Controller with Security {

  val userForm = Form(tuple(
    "personId" -> longNumber,
    "role" -> optional(text)))

  /**
   * Updates a person’s user role.
   */
  def update = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒

      userForm.bindFromRequest.fold(
        form ⇒ BadRequest("invalid form data"),
        user ⇒ {
          val (personId, role) = user
          Person.find(personId).map { person ⇒
            Logger.debug(s"update role for person (${person.fullName}}) to ${role}")
            val activityObject = Messages("object.UserAccount", person.fullNamePossessive)
            if (role.isDefined) {
              if (UserAccount.findRole(personId).isDefined) {
                UserAccount.updateRole(personId, role.get)
              } else {
                val account = UserAccount(None, personId, role.get, person.twitterHandle, person.facebookUrl,
                  person.googlePlusUrl, person.linkedInUrl)
                UserAccount.insert(account)
              }
            } else { // Remove the account
              UserAccount.delete(personId)
            }
            val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, activityObject)
            person.copy(updated = DateTime.now, updatedBy = request.user.fullName).update
            Redirect(routes.People.details(person.id.getOrElse(0))).flashing("success" -> activity.toString)
          }.getOrElse(BadRequest("invalid form data - person not found"))
        })
  }

}
