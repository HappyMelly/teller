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

package controllers.admin

import controllers.Security
import models.Activity
import models.UserRole.Role._
import models.admin.TransactionType
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc.Controller

/**
 * Pages for application configuration and administration.
 */
object Administration extends Controller with Security {

  val transactionTypeForm = Form(single("name" -> nonEmptyText))

  /**
   * Application settings page.
   */
  def settings = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.admin.settings(user, TransactionType.findAll, transactionTypeForm))
  }

  /**
   * Adds a new transaction type and redirects to the settings page.
   */
  def createTransactionType = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val boundForm = transactionTypeForm.bindFromRequest
      boundForm.fold(
        formWithErrors ⇒ BadRequest(views.html.admin.settings(user, TransactionType.findAll, formWithErrors)),
        value ⇒ {
          val transactionType = value.trim
          if (TransactionType.exists(transactionType)) {
            val form = boundForm.withError("name", "constraint.transactionType.exists")
            BadRequest(views.html.admin.settings(user, TransactionType.findAll, form))
          } else {
            TransactionType.insert(transactionType)
            val activityObject = Messages("models.TransactionType.name", transactionType)
            val activity = Activity.insert(user.fullName, Activity.Predicate.Created, activityObject)
            Redirect(routes.Administration.settings()).
              flashing("success" -> activity.toString)
          }
        })
  }

  /**
   * Deletes a transaction type and redirects to the settings page.
   */
  def deleteTransactionType(id: Long) = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      TransactionType.find(id).map { transactionType ⇒
        TransactionType.delete(id)
        val activityObject = Messages("models.TransactionType.name", transactionType.name)
        val activity = Activity.insert(user.fullName, Activity.Predicate.Deleted, activityObject)
        Redirect(routes.Administration.settings()).
          flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }
}
