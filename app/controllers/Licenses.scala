/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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
import models.JodaMoney.jodaMoney
import org.joda.time.LocalDate
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc.Controller
import scala.util.{ Failure, Success, Try }
import models.UserRole.Role._

/**
 * Content license pages and API.
 */
object Licenses extends Controller with Security {

  /**
   * HTML form mapping for creating and editing.
   * TODO Validate licensee ID and brand ID
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
    "fee" -> jodaMoney(13, 0), // Set scale to zero to force whole numbers.
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
   * Page for adding a new content license.
   */
  def add(personId: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Person.find(personId).map { person ⇒
        val form = licenseForm.fill(License.blank(personId))
        Ok(views.html.license.form(request.user, None, form, person))
      } getOrElse {
        Redirect(routes.People.index).flashing("error" -> Messages("error.notFound", Messages("models.Person")))
      }
  }

  /**
   * POST handler for adding a new content license.
   */
  def create(personId: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Person.find(personId).map { person ⇒
        licenseForm.bindFromRequest.fold(
          form ⇒ BadRequest(views.html.license.form(request.user, None, form, person)),
          license ⇒ {
            val newLicense = License.insert(license.copy(licenseeId = personId))
            val brand = Brand.find(newLicense.brandId).get

            val activityObject = Messages("activity.relationship.create", brand.name, person.fullName)
            val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, activityObject)
            Redirect(routes.People.details(personId)).flashing("success" -> activity.toString)
          })
      } getOrElse {
        Redirect(routes.People.details(personId)).flashing("error" -> Messages("error.notFound", Messages("models.Person")))
      }
  }

  /**
   * Deletes a license.
   */
  def delete(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      License.findWithBrandAndLicensee(id).map { view ⇒
        License.delete(id)
        val activityObject = Messages("activity.relationship.delete", view.brand.name, view.licensee.fullName)
        val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, activityObject)
        Redirect(routes.People.details(view.licensee.id.getOrElse(0))).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  /**
   * POST handler for updating an existing content license.
   */
  def update(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      License.findWithBrandAndLicensee(id).map { view ⇒
        licenseForm.bindFromRequest.fold(
          form ⇒ BadRequest(views.html.license.form(request.user, None, form, view.licensee)),
          editedLicense ⇒ {
            License.update(editedLicense.copy(id = Some(id), licenseeId = view.licensee.id.get))

            val activityObject = Messages("activity.relationship.delete", view.brand.name, view.licensee.fullName)
            val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, activityObject)
            Redirect(routes.People.details(view.licensee.id.get)).flashing("success" -> activity.toString)
          })
      } getOrElse {
        Redirect(routes.People.index()).flashing("error" -> Messages("error.notFound", Messages("models.License")))
      }
  }

  /**
   * Edit page.
   */
  def edit(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      License.find(id).map { license ⇒
        Person.find(license.licenseeId).map { licensee ⇒
          Ok(views.html.license.form(request.user, license.id, licenseForm.fill(license), licensee))
        }.getOrElse {
          throw new Exception(s"No person with ID ${license.licenseeId} found, for license with ID ${license.id}")
        }
      }.getOrElse(NotFound)
  }
}
