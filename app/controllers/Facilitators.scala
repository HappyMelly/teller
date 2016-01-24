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
import models.UserRole.Role
import org.joda.time.LocalDate
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc.Controller
import scala.concurrent.Future
import securesocial.core.RuntimeEnvironment
import views.Languages


/**
 * Facilitators pages
 */
class Facilitators(environment: RuntimeEnvironment[ActiveUser])
    extends JsonController
    with Security
    with Services
    with Utilities {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

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
    * Add a new country to a facilitator
    *
    * @param id Person identifier
    */
  def addCountry(id: Long) = SecuredProfileAction(id) {
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
   * Add a new language to a facilitator
   *
   * @param id Person identifier
   */
  def addLanguage(id: Long) = SecuredProfileAction(id) { implicit request ⇒
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
    * Retrieves badges for the given facilitator
    * @param personId Facilitator identifier
    * @param brandId Brand identifier
    */
  def badges(personId: Long, brandId: Long) = AsyncSecuredRestrictedAction(Role.Viewer) { implicit request =>
    implicit handler => implicit user =>
      facilitatorService.find(brandId, personId) map { facilitator =>
        val badges = brandBadgeService.findByBrand(brandId).filter(badge => facilitator.badges.contains(badge.id.get))
        Future.successful(Ok(views.html.v2.facilitator.badges(badges)))
      } getOrElse Future.successful(NotFound("Facilitator not found"))
  }

  /**
    * Remove a country from a facilitator
    *
    * @param id Person identifier
    * @param country Two-letters country identifier
    */
  def deleteCountry(id: Long, country: String) = SecuredProfileAction(id) {
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

  /**
   * Remove a language from a facilitator
   *
   * @param id Person identifier
   * @param language Two-letters language identifier
   */
  def deleteLanguage(id: Long, language: String) = SecuredProfileAction(id) {
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

  def details(id: Long, brandId: Long) = AsyncSecuredRestrictedAction(Role.Coordinator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒ Future.successful {
      facilitatorService.find(brandId, id) map { facilitator =>
        val badges = brandBadgeService.findByBrand(brandId)
        Ok(views.html.v2.facilitator.details(badges, facilitator))
      } getOrElse BadRequest("Unknown facilitator")
    }
  }

  /**
    * Returns list of facilitators for the given brand
    *
    * @param brandId Brand identifier
    */
  def index(brandId: Long) = AsyncSecuredRestrictedAction(List(Role.Facilitator, Role.Coordinator)) {
    implicit request => implicit handler => implicit user =>
      val licenses = licenseService.findByBrand(brandId)
      val facilitatorData = facilitatorService.findByBrand(brandId)
      val people = personService.find(licenses.map(_.licenseeId))
      val badges = brandBadgeService.findByBrand(brandId)
      PeopleCollection.addresses(people)
      Future.successful {
        roleDiffirentiator(user.account, Some(brandId)) { (view, brands) =>
          val facilitators = licenses.map { license =>
            val person = people.find(_.identifier == license.licenseeId).get
            val lastMonth = LocalDate.now().minusMonths(1)
            val joinedLastMonth = equalMonths(license.start, lastMonth)
            val leftLastMonth = equalMonths(license.end, lastMonth)
            val data = facilitatorData.find(_.personId == license.licenseeId).getOrElse {
              Facilitator(None, license.licenseeId, brandId)
            }
            val facilitatorBadges = badges.filter(x => data.badges.contains(x.id.get))
            (license, person, data, joinedLastMonth, leftLastMonth, facilitatorBadges)
          }
          Ok(views.html.v2.facilitator.forBrandCoordinators(user, view.brand, brands, facilitators))
        } { (view, brands) =>
          val facilitators = licenses.map { license =>
            val person = people.find(_.identifier == license.licenseeId).get
            val sameCountry = person.address.countryCode == user.person.address.countryCode
            val isNew = license.start.isAfter(LocalDate.now.minusMonths(3))
            val data = facilitatorData.find(_.personId == license.licenseeId).getOrElse {
              Facilitator(None, license.licenseeId, brandId)
            }
            val facilitatorBadges = badges.filter(x => data.badges.contains(x.id.get))
            (license, person, data, sameCountry, isNew, facilitatorBadges)
          }
          Ok(views.html.v2.facilitator.forFacilitators(user, view.get.brand, brands, facilitators))
        } {
          Redirect(routes.Dashboard.index())
        }
      }
  }

  /**
    * Returns a list of facilitators for the given brand on today,
    * including the coordinator of the brand
    */
  def list(brandId: Long) = SecuredRestrictedAction(Role.Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val facilitators = Brand.findFacilitators(brandId)
      if (facilitators.nonEmpty) {
        PeopleCollection.organisations(facilitators)
      }
      Ok(Json.toJson(facilitators))
  }

  /**
    * Updates badges for the given facilitator
    * @param personId Facilitator identifier
    * @param brandId Brand identifier
    */
  def updateBadges(personId: Long, brandId: Long) = AsyncSecuredBrandAction(brandId) { implicit request =>
    implicit handler => implicit user =>
      val form = Form(single("badges" -> play.api.data.Forms.list(longNumber)))
      form.bindFromRequest.fold(
        errors => Future.successful(jsonBadRequest("'badges' field doesn't exist")),
        badges =>
          facilitatorService.find(brandId, personId) map { facilitator =>
            val brandBadges = brandBadgeService.findByBrand(brandId).map(_.id.get)
            facilitatorService.update(facilitator.copy(badges = badges.filter(x => brandBadges.contains(x))))
            Future.successful(jsonSuccess("Badges were updated"))
          } getOrElse Future.successful(jsonNotFound("Facilitator not found"))
      )
  }

  protected def equalMonths(left: LocalDate, right: LocalDate): Boolean = {
    left.getYear == right.getYear && left.getMonthOfYear == right.getMonthOfYear
  }
}
