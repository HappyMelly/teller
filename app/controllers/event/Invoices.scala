/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers.event

import javax.inject.Inject

import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import be.objectify.deadbolt.scala.cache.HandlerCache
import controllers.{Activities, Security}
import models.UserRole.Role
import models.service.Services
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment

/**
  * Manages event invoices
  */
class Invoices @Inject() (override implicit val env: TellerRuntimeEnvironment,
                          override val messagesApi: MessagesApi,
                          val services: Services,
                          deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, services)(messagesApi, env)
  with Activities
  with Helpers {


  /**
    * Updates invoice data for the given event
    *
    * @param id Event ID
    * @return
    */
  def update(id: Long) = AsyncSecuredEventAction(List(Role.Coordinator), id) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      services.eventService.findWithInvoice(id) flatMap {
        case None => notFound("")
        case Some(view) =>
          EventForms.invoice.bindFromRequest.fold(
            formWithErrors ⇒ error(id, "Invoice data are wrong. Please try again"),
            invoiceData ⇒ {
              val (invoiceBy, number) = invoiceData
              services.orgService.find(invoiceBy) flatMap {
                case None => notFound("Organisation not found")
                case Some(_) =>
                  val invoice = view.invoice.copy(invoiceBy = Some(invoiceBy), number = number)
                  services.eventInvoiceService.update(invoice)
                  activity(view.event, user.person).updated.insert(services)
                  success(id, "Invoice data was successfully updated")
              }
            })
      }
  }
}
