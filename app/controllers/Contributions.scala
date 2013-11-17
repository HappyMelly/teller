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
import models.{ Contribution, Activity }
import play.api.mvc._
import org.joda.time._
import play.api.data._
import play.api.data.validation.Constraints._
import play.api.data.Forms._
import models.UserRole.Role._
import securesocial.core.SecuredRequest
import scala.Some
import play.api.data.format.Formatter
import play.api.i18n.Messages

object Contributions extends Controller with Security {

  /** HTML form mapping for creating and editing. */
  def contributionForm(implicit request: SecuredRequest[_]) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "contributorId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString),
    "productId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString),
    "isPerson" -> text.transform(_.toBoolean, (b: Boolean) ⇒ b.toString),
    "role" -> nonEmptyText)(Contribution.apply)(Contribution.unapply))

  def create = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      val boundForm: Form[Contribution] = contributionForm.bindFromRequest
      val contributorId = boundForm.data("contributorId").toLong
      boundForm.bindFromRequest.fold(
        formWithErrors ⇒ Redirect(routes.People.details(contributorId)).
          flashing("error" -> "A role for a contribution cannot be empty"),
        contribution ⇒ {
          contribution.insert
          val activityObject = Messages("activity.contribution.create", contribution.product.title)
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, activityObject)
          Redirect(routes.People.details(contribution.contributorId)).flashing("success" -> activity.toString)
        })
  }

  /** Delete a contribution **/
  def delete(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Contribution.find(id).map {
        contribution ⇒
          Contribution.delete(id)
          val activityObject = Messages("activity.contribution.delete",
            contribution.product.title)
          val activity = Activity.insert(request.user.fullName,
            Activity.Predicate.Deleted, activityObject)
          Redirect(routes.People.details(contribution.contributorId)).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

}
