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

import models.{ ActiveUser, Photo, Person }
import models.service.Services
import play.api.Play
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import securesocial.core.RuntimeEnvironment

class ProfilePhotos(environment: RuntimeEnvironment[ActiveUser])
    extends JsonController
    with Security
    with Services
    with Files {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  /**
   * Renders a screen for selecting a profile's photo
   *
   * @param id Person identifier
   */
  def choose(id: Long) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.find(id) map { person ⇒
          val active = person.photo.id getOrElse "nophoto"
          Ok(views.html.person.photoSelection(id,
            Photo.gravatarUrl(person.socialProfile.email),
            routes.ProfilePhotos.photo(id).url, active))
        } getOrElse NotFound
  }

  /**
   * Deletes photo of the given person
   *
   * @param id Person identifier
   */
  def delete(id: Long) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        Person.photo(id).remove()
        // orgService.updateLogo(id, false)
        val route = routes.People.details(id).url
        jsonOk(Json.obj("link" -> routes.Assets.at("images/happymelly-face-white.png").url))
  }

  /**
   * Retrieve and cache a photo of the given person
   *
   * @param id Person identifier
   */
  def photo(id: Long) = file(Person.photo(id))

  /**
   * Updates profile photo and may be a facebook profile link
   *
   * @param id Person identifier
   */
  def update(id: Long) = SecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        val form = Form(single("type" -> nonEmptyText)).bindFromRequest
        form.fold(
          withError ⇒ jsonBadRequest("No option is provided"),
          {
            case photoType ⇒
              personService.find(id) map { person ⇒
                val profile = person.socialProfile
                var photo = photoType match {
                  case "nophoto" ⇒ Photo.empty
                  case "gravatar" ⇒ Photo(photoType, profile)
                  case _ ⇒ Photo(Some(photoType),
                    Some(fullUrl(routes.ProfilePhotos.photo(id).url)))
                }
                personService.update(person.copy(photo = photo))
                jsonSuccess("ok")
              } getOrElse NotFound
          })
  }

  /**
   * Upload a new photo to Amazon
   *
   * @param id Person identifier
   */
  def upload(id: Long) = AsyncSecuredDynamicAction("person", "edit") {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        uploadFile(Person.photo(id), "photo") map { _ ⇒
          val route = routes.People.details(id).url
          jsonOk(Json.obj("link" -> routes.ProfilePhotos.photo(id).url))
        } recover {
          case e ⇒ jsonBadRequest(e.getMessage)
        }
  }

  /**
   * Returns an url with domain
   * @param url Domain-less part of url
   */
  private def fullUrl(url: String): String = {
    Play.configuration.getString("application.baseUrl").getOrElse("") + url
  }

}
