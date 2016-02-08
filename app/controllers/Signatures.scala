/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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

package controllers

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import models.Person
import models.service.Services
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

class Signatures @Inject() (override implicit val env: TellerRuntimeEnvironment,
                            override val messagesApi: MessagesApi,
                            val services: Services,
                            deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, services)(messagesApi, env)
  with Files
  with Activities {

  /**
   * Delete signature form submits to this action
   *
   * @param personId Person identifier
   */
  def delete(personId: Long) = AsyncSecuredProfileAction(personId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      services.personService.find(personId) flatMap {
        case None => notFound("Person not found")
        case Some(person) =>
          val result = if (person.signature) {
            Person.signature(personId).remove()
            services.personService.update(person.copy(signature = false))
          } else {
            Future.successful(None)
          }
          result flatMap { _ =>
            val route: String = routes.People.details(personId).url + "#facilitation"
            redirect(route, "success" -> "Signature was deleted")
          }
      }
  }

  /**
   * Retrieve and cache a signature of a person
   *
   * @param personId Person identifier
   */
  def signature(personId: Long) = file(Person.signature(personId))

  /**
   * Upload a new signature to Amazon
   *
   * @param personId Person identifier
   */
  def upload(personId: Long) = AsyncSecuredProfileAction(personId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      services.personService.find(personId) flatMap {
        case None => notFound("Person not found")
        case Some(person) =>
          val route: String = routes.People.details(personId).url + "#facilitation"
          uploadFile(Person.signature(personId), "signature") map { _ ⇒
            services.personService.update(person.copy(signature = true))
            Redirect(route).flashing("success" -> "Signature was uploaded")
          } recover {
            case e: RuntimeException ⇒ Redirect(route).flashing("error" -> e.getMessage)
          }
      }
  }
}
