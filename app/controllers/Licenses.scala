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

import models.JodaMoney.jodaMoney
import models.UserRole.Role._
import models._
import models.service.Services
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc.Controller
import securesocial.core.RuntimeEnvironment

import scala.concurrent.Future

/**
 * Content license pages and API.
 */
class Licenses(environment: RuntimeEnvironment[ActiveUser])
    extends Controller with Security with Services {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  /**
   * HTML form mapping for creating and editing.
    * TODO Validate and brand ID
   */
  val licenseForm = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "licenseeId" -> ignored(0L),
    "brandId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString),
    "version" -> nonEmptyText,
    "signed" -> jodaLocalDate,
    "start" -> jodaLocalDate,
    "end" -> jodaLocalDate,
    "confirmed" -> default(boolean, false),
    "fee" -> jodaMoney(),
    "feePaid" -> optional(jodaMoney()))(License.apply)(License.unapply).verifying(
      "error.date.range", (license: License) ⇒ !license.start.isAfter(license.end)))

  implicit val personWrites = new Writes[Person] {
    def writes(person: Person): JsValue = {
      Json.obj(
        "first_name" -> person.firstName,
        "last_name" -> person.lastName,
        "id" -> person.id.get)
    }
  }

  /**
    * Renders add form for a new content license
   *
   * @param personId Person identifier
   */
  def add(personId: Long) = AsyncSecuredRestrictedAction(Coordinator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val form = licenseForm.fill(License.blank(personId))
      Future.successful(
        Ok(views.html.v2.license.addForm(user, form, coordinatedBrands(user.account.personId), personId)))
  }

  /**
    * Adds a new content license for the given person
   *
   * @param personId Person identifier
   */
  def create(personId: Long) = SecuredRestrictedAction(Coordinator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      personService.find(personId).map { person ⇒
        val brands = coordinatedBrands(user.account.personId)
        val form = licenseForm.bindFromRequest
        form.fold(
          formWithErrors ⇒ BadRequest(views.html.v2.license.addForm(user, formWithErrors, brands, personId)),
          license ⇒ {
            brands.find(_._1 == license.brandId) map { brand =>
              licenseService.add(license.copy(licenseeId = personId))
              profileStrengthService.find(personId, org = false) map { x ⇒
                profileStrengthService.update(ProfileStrength.forFacilitator(x))
              }
              val account = userAccountService.findByPerson(personId).getOrElse(
                UserAccount.empty(personId).copy(facilitator = true))
              account.id.map { id =>
                true
              } getOrElse userAccountService.insert(account)
              val activityObject = Messages("activity.relationship.create", brand._2, person.fullName)
              val activity = Activity.insert(user.name, Activity.Predicate.Created, activityObject)
              val route = routes.People.details(personId).url + "#facilitation"
              Redirect(route).flashing("success" -> activity.toString)
            } getOrElse {
              val formWithError = form.withError("brandId", "You are not a coordinator of the selected brand")
              BadRequest(views.html.v2.license.addForm(user, formWithError, brands, personId))
            }
          })
      } getOrElse {
        Redirect(routes.People.details(personId)).flashing("error" -> Messages("error.notFound", Messages("models.Person")))
      }
  }

  /**
   * Deletes a license
   *
    * @param brandId Brand identifier
   * @param id License identifier
   */
  def delete(brandId: Long, id: Long) = SecuredBrandAction(brandId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      licenseService.findWithBrandAndLicensee(id).map { view ⇒
        License.delete(id)
        val activityObject = Messages("activity.relationship.delete", view.brand.name, view.licensee.fullName)
        val activity = Activity.insert(user.name, Activity.Predicate.Deleted, activityObject)
        val route = routes.People.details(view.licensee.id.getOrElse(0)).url + "#facilitation"
        Redirect(route).flashing("success" -> activity.toString)
      } getOrElse NotFound
  }

  /**
    * Renders a license edit form
    *
    * @param id License identifier
    */
  def edit(id: Long) = SecuredRestrictedAction(Coordinator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      License.find(id).map { license ⇒
        val brands = coordinatedBrands(user.account.personId)
        brands.find(_._1 == license.brandId) map { brand =>
          Ok(views.html.v2.license.editForm(user, license.id.get, licenseForm.fill(license), brands, brand._1))
        } getOrElse {
          Redirect(routes.Dashboard.index()).flashing("error" -> "You are not a coordinator of the selected brand")
        }
      } getOrElse NotFound
  }

  /**
    * Updates existing license
   *
   * @param id License identifier
   */
  def update(id: Long) = SecuredRestrictedAction(Coordinator) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    licenseService.findWithBrandAndLicensee(id) map { view ⇒
      val brands = coordinatedBrands(user.account.personId)
      val form = licenseForm.bindFromRequest
      form.fold(
        formWithErrors ⇒
          BadRequest(views.html.v2.license.editForm(user, id, formWithErrors, brands, view.brand.identifier)),
        license ⇒ {
          brands.find(_._1 == license.brandId) map { brand =>
            val editedLicense = license.copy(id = Some(id), licenseeId = view.license.licenseeId)
            licenseService.update(editedLicense)

            //TODO: replace activity section
            val activityObject = Messages("activity.relationship.delete", view.brand.name, view.licensee.fullName)
            val activity = Activity.insert(user.name, Activity.Predicate.Updated, activityObject)
            val route = routes.People.details(view.license.licenseeId).url + "#facilitation"
            Redirect(route).flashing("success" -> "License was updated")
          } getOrElse {
            val formWithError = form.withError("brandId", "You are not a coordinator of the selected brand")
            BadRequest(views.html.v2.license.editForm(user, id, formWithError, brands, license.brandId))
          }
        })
    } getOrElse {
      Redirect(routes.Dashboard.index()).flashing("error" -> Messages("error.notFound", Messages("models.License")))
    }
  }

  /**
    * Returns a list of brands coordinated by the given person
    * @param coordinatorId Coordinator identifier
    */
  protected def coordinatedBrands(coordinatorId: Long): List[(Long, String)] =
    brandService.findByCoordinator(coordinatorId).map(x => (x.identifier, x.name))
}
