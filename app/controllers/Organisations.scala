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

import Forms._
import models.{ Person, Activity, Organisation, Contribution, Product }
import play.api.mvc._
import securesocial.core.{ SecuredRequest, SecureSocial }
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import org.joda.time.DateTime
import models.UserRole.Role._
import models.OrganisationCategory
import securesocial.core.SecuredRequest
import scala.Some
import play.api.data.format.Formatter

object Organisations extends Controller with Security {

  /**
   * Formatter used to define a form mapping for the `OrganisationCategory` enumeration.
   */
  implicit def categoryFormat: Formatter[OrganisationCategory.Value] = new Formatter[OrganisationCategory.Value] {

    def bind(key: String, data: Map[String, String]) = {
      try {
        data.get(key).map(OrganisationCategory.withName(_)).toRight(Seq.empty)
      } catch {
        case e: NoSuchElementException ⇒ Left(Seq(FormError(key, "error.invalid")))
      }
    }

    def unbind(key: String, value: OrganisationCategory.Value) = Map(key -> value.toString)
  }

  val categoryMapping = of[OrganisationCategory.Value]

  /**
   * HTML form mapping for creating and editing.
   */
  def organisationForm(implicit request: SecuredRequest[_]) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "name" -> nonEmptyText,
    "street1" -> optional(text),
    "street2" -> optional(text),
    "city" -> optional(text),
    "province" -> optional(text),
    "postCode" -> optional(text),
    "country" -> nonEmptyText,
    "vatNumber" -> optional(text),
    "registrationNumber" -> optional(text),
    "category" -> optional(categoryMapping),
    "webSite" -> optional(webUrl),
    "blog" -> optional(webUrl),
    "active" -> ignored(true),
    "created" -> ignored(DateTime.now()),
    "createdBy" -> ignored(request.user.fullName),
    "updated" -> ignored(DateTime.now()),
    "updatedBy" -> ignored(request.user.fullName))(Organisation.apply)(Organisation.unapply))

  /**
   * Form target for toggling whether an organisation is active.
   */
  def activation(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Organisation.find(id).map { organisation ⇒
        Form("active" -> boolean).bindFromRequest.fold(
          form ⇒ {
            BadRequest("invalid form data")
          },
          active ⇒ {
            Organisation.activate(id, active)
            val activity = Activity.insert(request.user.fullName, if (active) Activity.Predicate.Activated else Activity.Predicate.Deactivated, organisation.name)
            Redirect(routes.Organisations.details(id)).flashing("success" -> activity.toString)
          })
      } getOrElse {
        Redirect(routes.Organisations.index).flashing("error" -> Messages("error.notFound", Messages("models.Organisation")))
      }
  }

  /**
   * Create page.
   */
  def add = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Ok(views.html.organisation.form(request.user, None, organisationForm))
  }

  /**
   * Create form submits to this action.
   */
  def create = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      organisationForm.bindFromRequest.fold(
        formWithErrors ⇒
          BadRequest(views.html.organisation.form(request.user, None, formWithErrors)),
        organisation ⇒ {
          val org = organisation.insert
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, organisation.name)
          Redirect(routes.Organisations.index()).flashing("success" -> activity.toString)
        })
  }

  /**
   * Deletes an organisation.
   * @param id Organisation ID
   */
  def delete(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Organisation.find(id).map {
        organisation ⇒
          Organisation.delete(id)
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, organisation.name)
          Redirect(routes.Organisations.index).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  /**
   * Details page.
   * @param id Organisation ID
   */
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒

      Organisation.find(id).map {
        organisation ⇒
          val members = organisation.members
          val otherPeople = Person.findActive.filterNot(person ⇒ members.contains(person))
          val contributions = Contribution.contributions(id, false)
          val products = Product.findAll

          Ok(views.html.organisation.details(request.user, organisation,
            members, otherPeople,
            contributions, products))
      }.getOrElse(NotFound)
  }

  /**
   * Edit page.
   * @param id Organisation ID
   */
  def edit(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Organisation.find(id).map {
        organisation ⇒
          Ok(views.html.organisation.form(request.user, Some(id), organisationForm.fill(organisation)))
      }.getOrElse(NotFound)
  }

  /**
   * List page.
   */
  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒

      val organisations = Organisation.findAll
      Ok(views.html.organisation.index(request.user, organisations))
  }

  /**
   * Edit form submits to this action.
   * @param id Organisation ID
   */
  def update(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      organisationForm.bindFromRequest.fold(
        formWithErrors ⇒
          BadRequest(views.html.organisation.form(request.user, Some(id), formWithErrors)),
        organisation ⇒ {
          organisation.copy(id = Some(id)).update
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, organisation.name)
          Redirect(routes.Organisations.details(id)).flashing("success" -> activity.toString)
        })
  }

}
