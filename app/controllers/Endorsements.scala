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
 * If you have questions concerning this license or the applicable additional terms,
 * you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers

import models.{Brand, ActiveUser, Endorsement}
import models.service.Services
import models.UserRole.DynamicRole
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json.{ JsValue, Writes, Json }
import securesocial.core.RuntimeEnvironment

case class EndorsementFormData(content: String,
  name: String,
  brandId: Long,
  company: Option[String])

class Endorsements(environment: RuntimeEnvironment[ActiveUser])
    extends JsonController
    with Services
    with Security {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  implicit val EndorsementWrites = new Writes[Endorsement] {
    def writes(endorsement: Endorsement): JsValue = {
      Json.obj(
        "brandId" -> endorsement.brandId,
        "personId" -> endorsement.personId,
        "content" -> endorsement.content,
        "name" -> endorsement.name,
        "company" -> endorsement.company,
        "id" -> endorsement.id.get)
    }
  }

  val form = Form(mapping(
    "content" -> nonEmptyText,
    "name" -> nonEmptyText,
    "brandId" -> longNumber,
    "company" -> optional(nonEmptyText))(EndorsementFormData.apply)(EndorsementFormData.unapply))

  /**
   * Renders endorsement add form
   * @param personId Person identifier
   */
  def add(personId: Long) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.find(personId) map { person ⇒
          Ok(views.html.v2.endorsement.form(user, personId, brands(personId), form))
        } getOrElse NotFound(Messages("error.person.notFound"))
  }

  /**
   * Renders endorsement edit form
   * @param personId Person identifier
   * @param id Endorsement identifier
   */
  def edit(personId: Long, id: Long) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.find(personId) map { person ⇒
          personService.findEndorsement(id) map { endorsement ⇒
            val formData = EndorsementFormData(endorsement.content,
              endorsement.name, endorsement.brandId, endorsement.company)
            Ok(views.html.v2.endorsement.form(user, personId, brands(personId), form.fill(formData), Some(id)))
          } getOrElse NotFound("Endorsement is not found")
        } getOrElse NotFound(Messages("error.person.notFound"))
  }

  /**
   * Adds new endorsement if the endorsement is valid
   *
   * @param personId Person identifier
   */
  def create(personId: Long) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.find(personId) map { person ⇒
          form.bindFromRequest.fold(
            error ⇒
              BadRequest(views.html.v2.endorsement.form(user, personId, brands(personId), error)),
            endorsementData ⇒ {
              val endorsement = Endorsement(None, personId, endorsementData.brandId,
                endorsementData.content, endorsementData.name,
                endorsementData.company)
              personService.insertEndorsement(endorsement)
              val url = routes.People.details(personId).url + "#experience"
              Redirect(url)
            })
        } getOrElse NotFound(Messages("error.person.notFound"))
  }

  /**
   * Deletes the given endorsement if the endorsement exists and is belonged
   * to the given person
   *
   * Person identifier is used to check access rights
   *
   * @param personId Person identifier
   * @param id endorsement identifier
   */
  def remove(personId: Long, id: Long) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.deleteEndorsement(personId, id)
        jsonSuccess("ok")
  }

  /**
   * Updates the given endorsement if it's valid
   *
   * @param personId Person identifier
   * @param id Endorsement identifier
   */
  def update(personId: Long, id: Long) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        form.bindFromRequest.fold(
          error ⇒ BadRequest(views.html.v2.endorsement.form(user, personId, brands(personId), error)),
          endorsementData ⇒ {
            val endorsement = Endorsement(Some(id), personId, endorsementData.brandId,
              endorsementData.content, endorsementData.name,
              endorsementData.company)
            personService.updateEndorsement(endorsement)
            val url = routes.People.details(personId).url + "#experience"
            Redirect(url)
          })
  }

  /**
   * Returns list of brands for which the given person has licenses
   * @param personId Person identifier
   */
  protected def brands(personId: Long): List[Brand] =
    licenseService.licenses(personId).map(_.brand)
}
