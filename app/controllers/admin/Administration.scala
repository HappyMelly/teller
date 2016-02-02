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

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.Security
import models.UserRole.Role._
import models.service.admin.TransactionTypeService
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{MessagesApi, I18nSupport}
import play.twirl.api.Html
import services.TellerRuntimeEnvironment

/**
 * Pages for application configuration and administration.
 */
class Administration @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment,
                                             val messagesApi: MessagesApi,
                                             deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder)
  with I18nSupport {

  val service = new TransactionTypeService
  val transactionTypeForm = Form(single("name" -> nonEmptyText))

  /**
   * Application settings page.
   */
  def settings = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    service.findAll flatMap { types =>
      ok(views.html.admin.settings(user, types, transactionTypeForm))
    }
  }

  /**
   * Adds a new transaction type and redirects to the settings page.
   */
  def createTransactionType = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    service.findAll flatMap { types =>
      val boundForm = transactionTypeForm.bindFromRequest
      boundForm.fold(
        formWithErrors ⇒ badRequest(views.html.admin.settings(user, types, formWithErrors)),
        value ⇒ {
          val transactionType = value.trim
          service.exists(transactionType) flatMap {
            case true =>
              val form = boundForm.withError("name", "constraint.transactionType.exists")
              badRequest(views.html.admin.settings(user, types, form))
            case false =>
              service.insert(transactionType) flatMap { _ =>
                val msg = "Transaction type was added"
                redirect(routes.Administration.settings(), "success" -> msg)
              }
          }
        })
    }
  }

  /**
   * Deletes a transaction type and redirects to the settings page.
   */
  def deleteTransactionType(id: Long) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      service.find(id) flatMap {
        case None => notFound(Html(""))
        case Some(transactionType) =>
          service.delete(id) flatMap { _ =>
            val msg = "Transaction type was deleted"
            redirect(routes.Administration.settings(), "success" -> msg)
          }
      }
  }
}
