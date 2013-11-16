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

import Forms._
import models.{ ProductView, Activity, Product, ProductCategory }
import play.api.mvc._
import org.joda.time._
import play.api.data._
import play.api.data.validation.Constraints._
import play.api.data.Forms._
import models.UserRole.Role._
import securesocial.core.SecuredRequest
import scala.Some
import play.api.data.format.Formatter

object Products extends Controller with Security {

  /**
   * Formatter used to define a form mapping for the `ProductCategory` enumeration.
   */
  implicit def categoryFormat: Formatter[ProductCategory.Value] = new Formatter[ProductCategory.Value] {

    def bind(key: String, data: Map[String, String]) = {
      try {
        data.get(key).map(ProductCategory.withName(_)).toRight(Seq.empty)
      } catch {
        case e: NoSuchElementException ⇒ Left(Seq(FormError(key, "error.invalid")))
      }
    }

    def unbind(key: String, value: ProductCategory.Value) = Map(key -> value.toString)
  }

  val categoryMapping = of[ProductCategory.Value]

  /** HTML form mapping for creating and editing. */
  def productForm(implicit request: SecuredRequest[_]) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "title" -> nonEmptyText,
    "subtitle" -> optional(text),
    "url" -> optional(text),
    "category" -> optional(categoryMapping),
    "parentId" -> optional(nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString)),
    "created" -> ignored(DateTime.now()),
    "createdBy" -> ignored(request.user.fullName),
    "updated" -> ignored(DateTime.now()),
    "updatedBy" -> ignored(request.user.fullName))(Product.apply)(Product.unapply))

  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒

      val products = models.Product.findAll
      Ok(views.html.product.index(request.user, products))
  }

  /** Show all products **/
  def add = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      Ok(views.html.product.form(request.user, None, productForm))
  }

  def create = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      val boundForm: Form[Product] = productForm.bindFromRequest
      boundForm.fold(
        formWithErrors ⇒ BadRequest(views.html.product.form(request.user, None, formWithErrors)),
        product ⇒ {
          if (Product.exists(product.title)) BadRequest(views.html.product.form(request.user, None,
            boundForm.withError("title", "constraint.product.title.exists", product.title)))

          val savedProduct = product.insert
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, savedProduct.title)
          Redirect(routes.Products.index()).flashing("success" -> activity.toString)
        })
  }

  /** Delete a product **/
  def delete(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Product.find(id).map {
        product ⇒
          Product.delete(id)
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, product.title)
          Redirect(routes.Products.index).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒

      Product.find(id).map {
        product ⇒
          val derivatives = Product.findDerivatives(id)
          val parent = if (product.parentId.isDefined) Product.find(product.parentId.get) else None
          // Ok(views.html.product.details(request.user, product, parent, brandIds))
          Ok(views.html.product.details(request.user, product, derivatives, parent))
      }.getOrElse(NotFound)

  }

  /** Edit page **/
  def edit(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Product.find(id).map {
        product ⇒
          Ok(views.html.product.form(request.user, Some(id), productForm.fill(product)))
      }.getOrElse(NotFound)

  }

  /** Edit form submits to this action **/
  def update(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      productForm.bindFromRequest.fold(
        formWithErrors ⇒
          BadRequest(views.html.product.form(request.user, Some(id), formWithErrors)),
        product ⇒ {
          product.copy(id = Some(id)).update
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, product.title)
          Redirect(routes.Products.details(id)).flashing("success" -> activity.toString)
        })

  }

}
