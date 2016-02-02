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
import models.{Person, Photo}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import services.TellerRuntimeEnvironment

class ProfilePhotos @Inject() (override implicit val env: TellerRuntimeEnvironment,
                               deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder)
  with Files
  with Utilities {

  /**
   * Renders a screen for selecting a profile's photo
   *
   * @param id Person identifier
   */
  def choose(id: Long) = AsyncSecuredProfileAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    personService.find(id) flatMap {
      case None => notFound("Person not found")
      case Some(person) =>
        val active = person.photo.id getOrElse "nophoto"
        ok(views.html.v2.person.photoSelection(id, Photo.gravatarUrl(person.email),
          routes.ProfilePhotos.photo(id).url, active))
    }
  }

  /**
   * Deletes photo of the given person
   *
   * @param id Person identifier
   */
  def delete(id: Long) = AsyncSecuredProfileAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    personService.find(id) flatMap {
      case None => notFound("Person not found")
      case Some(person) =>
        Person.photo(id).remove()
        personService.update(person.copy(photo = Photo.empty)) flatMap { _ =>
          val route = routes.People.details(id).url
          jsonOk(Json.obj("link" -> routes.Assets.at("images/add-photo.png").url))
        }
    }
  }

  /**
   * Retrieve and cache a photo of the given person
   *
   * @param id Person identifier
   */
  def photo(id: Long) = file(Person.photo(id))

  /**
    * Updates profile photo
   *
   * @param id Person identifier
   */
  def update(id: Long) = AsyncSecuredProfileAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val form = Form(single("type" -> nonEmptyText)).bindFromRequest
    form.fold(
      withError ⇒ jsonBadRequest("No option is provided"),
      photoType ⇒
        personService.find(id) flatMap {
          case None => notFound("Person not found")
          case Some(person) =>
            val photo = photoType match {
              case "nophoto" ⇒ Photo.empty
              case "gravatar" ⇒ Photo(photoType, person.email)
              case _ ⇒ Photo(Some(photoType), photoUrl(id))
            }
            personService.update(person.copy(photo = photo)) flatMap { _ =>
              jsonSuccess("ok")
            }
        })
  }

  /**
   * Upload a new photo to Amazon
   *
   * @param id Person identifier
   */
  def upload(id: Long) = AsyncSecuredProfileAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    uploadFile(Person.photo(id), "photo") flatMap { _ ⇒
      val route = routes.People.details(id).url
      jsonOk(Json.obj("link" -> routes.ProfilePhotos.photo(id).url))
    } recover {
      case e ⇒ BadRequest(Json.obj("message" -> e.getMessage))
    }
  }

  /**
    * Returns url to a person's photo
    *
    * @param id Person identifier
    */
  protected def photoUrl(id: Long): Option[String] = {
    val photo = Person.photo(id)
    cdnUrl(photo.name).orElse(Some(fullUrl(routes.ProfilePhotos.photo(id).url)))
  }
}
