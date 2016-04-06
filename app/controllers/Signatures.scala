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

package controllers

import javax.inject.{Named, Inject}

import akka.actor.ActorRef
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import models.{Person, ProfileStrength}
import models.repository.Repositories
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment

import scala.concurrent.Future
import scala.util.Random

class Signatures @Inject() (override implicit val env: TellerRuntimeEnvironment,
                            override val messagesApi: MessagesApi,
                            val repos: Repositories,
                            @Named("profile-strength") recalculator: ActorRef,
                            deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env)
  with Files
  with Activities {

  /**
   * Delete signature form submits to this action
   *
   * @param personId Person identifier
   */
  def delete(personId: Long) = ProfileAction(personId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      repos.person.findComplete(personId) flatMap {
        case None => notFound("Person not found")
        case Some(person) =>
          val result = person.signatureId match {
            case None => Future.successful(None)
            case Some(signature) =>
              Person.signature(signature).remove()
              recalculator ! ("incomplete", personId, ProfileStrength.Steps.SIGNATURE)
              repos.person.updateSignature(personId, None)
          }
          result flatMap { _ =>
            val route: String = core.routes.People.details(personId).url + "#facilitation"
            redirect(route, "success" -> "Signature was deleted")
          }
      }
  }

  /**
   * Retrieve and cache a signature of a person
   *
   * @param signatureId Person signature identifier
   */
  def signature(signatureId: String) = file(Person.signature(signatureId))

  /**
   * Upload a new signature to Amazon
   *
   * @param personId Person identifier
   */
  def upload(personId: Long) = ProfileAction(personId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      repos.person.findComplete(personId) flatMap {
        case None => notFound("Person not found")
        case Some(person) =>
          val route: String = core.routes.People.details(personId).url + "#facilitation"
          val signatureId = Random.alphanumeric.take(32).mkString
          uploadFile(Person.signature(signatureId), "signature") map { _ ⇒
            repos.person.updateSignature(personId, Some(signatureId))
            recalculator ! ("complete", personId, ProfileStrength.Steps.SIGNATURE)
            Redirect(route).flashing("success" -> "Signature was uploaded")
          } recover {
            case e: RuntimeException ⇒ Redirect(route).flashing("error" -> e.getMessage)
          }
      }
  }
}
