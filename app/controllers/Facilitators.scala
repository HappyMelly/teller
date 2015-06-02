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

import models._
import models.service.Services
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc.Controller
import models.UserRole.Role._
import views.Languages

/**
 * Facilitators pages
 */
object Facilitators extends Controller with Security with Services {

  implicit val organizationWrites = new Writes[Organisation] {
    def writes(data: Organisation): JsValue = {
      Json.obj(
        "id" -> data.id.get,
        "name" -> data.name)
    }
  }

  implicit val personWrites = new Writes[Person] {
    def writes(data: Person): JsValue = {
      Json.obj(
        "first_name" -> data.firstName,
        "last_name" -> data.lastName,
        "id" -> data.id.get,
        "memberships" -> data.organisations)
    }
  }

  /**
   * Returns a list of facilitators for the given brand on today,
   * including the coordinator of the brand
   */
  def index(brandId: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val facilitators = Brand.findFacilitators(brandId)
      if (facilitators.length > 0) {
        PeopleCollection.organisations(facilitators)
      }
      Ok(Json.toJson(facilitators))
  }

  /**
   * Add a new language to a facilitator
   *
   * @param id Person identifier
   */
  def addLanguage(id: Long) = SecuredDynamicAction("person", "edit") { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val membershipForm = Form(single("language" -> nonEmptyText))

      membershipForm.bindFromRequest.fold(
        errors ⇒ BadRequest("Language is not chosen"),
        {
          case (language) ⇒
            personService.find(id).map { person ⇒
              if (!facilitatorService.languages(id).exists(_.language == language)) {
                FacilitatorLanguage(id, language).insert
                profileStrengthService.find(id, false) map { x ⇒
                  profileStrengthService.update(x.markComplete("language"))
                }
              }
              val languageName = Languages.all.getOrElse(language, "")
              val desc = Messages("activity.relationship.create",
                languageName,
                person.fullName)
              val activity = Activity.insert(user.person, Activity.Predicate.Created, desc)

              Redirect(routes.People.details(id).url + "#facilitation").
                flashing("success" -> activity.toString)
            }.getOrElse(NotFound)
        })
  }

  /**
   * Remove a language from a facilitator
   *
   * @param id Person identifier
   * @param language Two-letters language identifier
   */
  def deleteLanguage(id: Long, language: String) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.find(id).map { person ⇒
          val languages = facilitatorService.languages(id)
          if (languages.exists(_.language == language)) {
            FacilitatorLanguage(id, language).delete()
            if (languages.length == 1) {
              profileStrengthService.find(id, false) map { x ⇒
                profileStrengthService.update(x.markIncomplete("language"))
              }
            }
          }
          val languageName = Languages.all.getOrElse(language, "")

          val desc = Messages("activity.relationship.delete", languageName, person.fullName)
          val activity = Activity.insert(user.person, Activity.Predicate.Deleted, desc)

          Redirect(routes.People.details(id).url + "#facilitation").
            flashing("success" -> activity.toString)
        }.getOrElse(NotFound)
  }

  /**
   * Add a new country to a facilitator
   *
   * @param id Person identifier
   */
  def addCountry(id: Long) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒

        val membershipForm = Form(single("country" -> nonEmptyText))

        membershipForm.bindFromRequest.fold(
          errors ⇒ BadRequest("Country is not chosen"),
          {
            case (country) ⇒
              personService.find(id).map { person ⇒
                if (!FacilitatorCountry.findByFacilitator(id).exists(_.country == country)) {
                  FacilitatorCountry(id, country).insert
                }
                val desc = Messages("activity.relationship.create",
                  Messages("country." + country),
                  person.fullName)
                val activity = Activity.insert(user.person,
                  Activity.Predicate.Created,
                  desc)

                Redirect(routes.People.details(id).url + "#facilitation").
                  flashing("success" -> activity.toString)
              }.getOrElse(NotFound)
          })
  }

  /**
   * Remove a country from a facilitator
   *
   * @param id Person identifier
   * @param country Two-letters country identifier
   */
  def deleteCountry(id: Long, country: String) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒

        personService.find(id).map { person ⇒
          if (FacilitatorCountry.findByFacilitator(id).exists(_.country == country)) {
            FacilitatorCountry(id, country).delete()
          }
          val desc = Messages("activity.relationship.delete",
            Messages("country." + country),
            person.fullName)
          val activity = Activity.insert(user.person,
            Activity.Predicate.Deleted,
            desc)

          Redirect(routes.People.details(id).url + "#facilitation").
            flashing("success" -> activity.toString)
        }.getOrElse(NotFound)
  }
}
