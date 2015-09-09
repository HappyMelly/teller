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

import models.ActiveUser
import models.UserRole.DynamicRole
import models.Material
import models.service.Services
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json.{ JsValue, Writes, Json }
import securesocial.core.RuntimeEnvironment

class Materials(environment: RuntimeEnvironment[ActiveUser])
    extends JsonController
    with Services
    with Security {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  implicit val MaterialWrites = new Writes[Material] {
    def writes(link: Material): JsValue = {
      Json.obj(
        "personId" -> link.personId,
        "linkType" -> link.linkType.capitalize,
        "link" -> link.link,
        "brandId" -> link.brandId,
        "id" -> link.id.get)
    }
  }

  /**
   * Adds new material if the link is valid
   *
   * @param personId Person identifier
   */
  def create(personId: Long) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.find(personId) map { person ⇒
          val form = Form(tuple("brandId" -> longNumber, "type" -> nonEmptyText, "url" -> nonEmptyText))
          form.bindFromRequest.fold(
            error ⇒ jsonBadRequest("Link cannot be empty"),
            materialData ⇒ {
              val material = Material(None,
                personId,
                materialData._1,
                materialData._2,
                materialData._3)
              val insertedLink = personService.insertMaterial(
                Material.updateType(material))
              jsonOk(Json.toJson(insertedLink))
            })
        } getOrElse jsonNotFound(Messages("error.person.notFound"))
  }

  /**
   * Deletes the given material if the material exists and is belonged to
   * the given person
   *
   * Person identifier is used to check access rights
   *
   * @param personId Person identifier
   * @param id Material identifier
   */
  def remove(personId: Long, id: Long) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.deleteMaterial(personId, id)
        jsonSuccess("ok")
  }

}
