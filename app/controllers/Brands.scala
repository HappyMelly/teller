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

import models.{ EventType, BrandView, Activity, Brand }
import org.joda.time._
import play.api.mvc._
import play.api.data.Form
import play.api.data.validation.Constraints._
import play.api.data.Forms._
import models.UserRole.Role._
import securesocial.core.SecuredRequest
import scala.Some

object Brands extends Controller with Security {

  /** HTML form mapping for creating and editing. */
  def brandsForm(implicit request: SecuredRequest[_]) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "code" -> nonEmptyText.verifying(pattern("[A-Z0-9]*".r, "constraint.brand.code", "constraint.brand.code.error"), maxLength(5)),
    "name" -> nonEmptyText,
    "coordinatorId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString),
    "created" -> ignored(DateTime.now()),
    "createdBy" -> ignored(request.user.fullName),
    "updated" -> ignored(DateTime.now()),
    "updatedBy" -> ignored(request.user.fullName))(Brand.apply)(Brand.unapply))

  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒

      val brands = models.Brand.findAll
      Ok(views.html.brand.index(request.user, brands))
  }

  /** Show all brands **/
  def add = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      Ok(views.html.brand.form(request.user, None, brandsForm))
  }

  def create = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      val boundForm: Form[Brand] = brandsForm.bindFromRequest
      boundForm.fold(
        formWithErrors ⇒ BadRequest(views.html.brand.form(request.user, None, formWithErrors)),
        brand ⇒ {
          if (Brand.exists(brand.code)) BadRequest(views.html.brand.form(request.user, None,
            boundForm.withError("code", "constraint.brand.code.exists", brand.code)))

          val savedBrand = brand.insert
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, savedBrand.name)
          Redirect(routes.Brands.index()).flashing("success" -> activity.toString)
        })
  }

  /** Delete a brand **/
  def delete(code: String) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      Brand.find(code).map {
        case BrandView(brand, _, _) ⇒
          brand.delete()
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, brand.name)
          Redirect(routes.Brands.index()).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  def details(code: String) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒
      Brand.find(code).map {
        case BrandView(brand, coordinator, licenseIds) ⇒ {
          val eventTypes = EventType.findByBrand(brand.id.get)
          Ok(views.html.brand.details(request.user, brand, coordinator, eventTypes))
        }
      }.getOrElse(NotFound)

  }

  /** Edit page **/
  def edit(code: String) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      Brand.find(code).map { brandView ⇒
        val filledForm: Form[Brand] = brandsForm.fill(brandView.brand)
        Ok(views.html.brand.form(request.user, Some(code), filledForm))
      }.getOrElse(NotFound)
  }

  /** Edit form submits to this action **/
  def update(code: String) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Brand.find(code).map { originalBrandView ⇒
        brandsForm.bindFromRequest.fold(
          form ⇒ BadRequest(views.html.brand.form(request.user, Some(code), form)),
          brand ⇒ {
            brand.copy(id = originalBrandView.brand.id).update
            val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, brand.name)
            Redirect(routes.Brands.details(brand.code)).flashing("success" -> activity.toString)
          })
      }.getOrElse(NotFound)

  }

}
