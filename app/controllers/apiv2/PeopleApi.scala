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

package controllers.apiv2

import java.net.URLDecoder
import javax.inject.Inject

import controllers.apiv2.json.PersonConverter
import models.service.Services
import play.api.i18n.MessagesApi
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global

class PeopleApi @Inject() (val services: Services,
                           override val messagesApi: MessagesApi) extends ApiAuthentication(services, messagesApi) {

  private val converter = new PersonConverter
  implicit val personWrites = converter.personWrites

  /**
   * Get a list of people
    *
    * @param active If true only active members are retrieved
   * @param query Retrieve only people whose name meets the pattern
   * @return
   */
  def people(active: Option[Boolean], query: Option[String]) = TokenSecuredAction(readWrite = false) { 
    implicit request ⇒ implicit token ⇒
      services.personService.findByParameters(active, query) flatMap { people =>
        services.personService.collection.addresses(people)
        ok(Json.prettyPrint(Json.toJson(people)))
      }
  }

  /**
   * Get a person
    *
    * @param identifier Person identifier
   * @return
   */
  def person(identifier: String) = TokenSecuredAction(readWrite = false) { implicit request ⇒ implicit token ⇒
    val mayBePerson = try {
      val id = identifier.toLong
      services.personService.find(id)
    } catch {
      case e: NumberFormatException ⇒ services.personService.find(URLDecoder.decode(identifier, "ASCII"))
    }
    mayBePerson flatMap {
      case None => notFound("Person not found")
      case Some(person) =>
        (for {
          l <- services.licenseService.activeLicenses(person.identifier)
          c <- services.contributionService.contributions(person.identifier, isPerson = true)
          o <- services.personService.memberships(person.identifier)
        } yield (l, c, o)) flatMap { case (licenses, contributions, organisations) =>
          ok(Json.prettyPrint(Json.toJson((person, licenses, contributions, organisations))(converter.personDetailsWrites)))
        }
    }
  }
}
