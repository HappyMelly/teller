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

import models.{ Person, Activity }
import models.service.Services
import play.api.Play
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import scala.concurrent.Future

trait Signatures extends JsonController
  with Security
  with Services
  with Files {

  /**
   * Delete signature form submits to this action
   *
   * @param personId Person identifier
   */
  def delete(personId: Long) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.find(personId).map { person ⇒
          if (person.signature) {
            Person.signature(personId).remove()
            personService.update(person.copy(signature = false))
          }
          val activity = person.activity(
            user.person,
            Activity.Predicate.DeletedSign).insert
          val route = routes.People.details(personId).url + "#facilitation"
          Redirect(route).flashing("success" -> activity.toString)
        } getOrElse NotFound
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
  def upload(personId: Long) = AsyncSecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒

        personService.find(personId).map { person ⇒
          val route = routes.People.details(personId).url + "#facilitation"
          uploadFile(Person.signature(personId), "signature") map { _ ⇒
            personService.update(person.copy(signature = true))
            val activity = person.activity(
              user.person,
              Activity.Predicate.UploadedSign).insert
            Redirect(route).flashing("success" -> activity.toString)
          } recover {
            case e: RuntimeException ⇒ Redirect(route).flashing("error" -> e.getMessage)
          }
        } getOrElse Future.successful(NotFound)
  }
}

object Signatures extends Signatures