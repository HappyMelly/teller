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
package controllers.core

import javax.inject.{Named, Inject}

import akka.actor.ActorRef
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.{Files, Security, Utilities}
import models.repository.Repositories
import models.{ProfileStrength, Person, Photo}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import services.TellerRuntimeEnvironment

import scala.util.Random

class ProfilePhotos @Inject() (override implicit val env: TellerRuntimeEnvironment,
                               override val messagesApi: MessagesApi,
                               val repos: Repositories,
                               @Named("profile-strength") recalculator: ActorRef,
                               deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env)
    with Files {

  /**
    * Renders a screen for selecting a profile's photo
    *
    * @param id Person identifier
    */
  def choose(id: Long) = ProfileAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    repos.person.find(id) flatMap {
      case None => notFound("Person not found")
      case Some(person) =>
        val active = person.photo.typ getOrElse "nophoto"
        val photoUrl = person.photo.photoId.map(x => routes.ProfilePhotos.photo(x).url).getOrElse("")
        ok(views.html.v2.person.photoSelection(id, Photo.gravatarUrl(person.email), photoUrl, active))
    }
  }

  /**
    * Deletes photo of the given person
    *
    * @param id Person identifier
    */
  def delete(id: Long) = ProfileAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    repos.person.find(id) flatMap {
      case None => notFound("Person not found")
      case Some(person) =>
        person.signatureId.foreach(x => Person.photo(x).remove())
        repos.person.updatePhoto(id, None, None) flatMap { _ =>
          recalculator ! ("incomplete", id, ProfileStrength.Steps.PHOTO)
          jsonOk(Json.obj("link" -> controllers.routes.Assets.at("images/add-photo.png").url))
        }
    }
  }

  /**
    * Retrieve and cache a photo of the given person
    *
    * @param id Person photo identifier
    */
  def photo(id: String) = file(Person.photo(id))

  /**
    * Updates profile photo
    *
    * @param id Person identifier
    */
  def update(id: Long) = ProfileAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val form = Form(single("type" -> nonEmptyText)).bindFromRequest
    form.fold(
      withError ⇒ jsonBadRequest("No option is provided"),
      photoType ⇒
        repos.person.find(id) flatMap {
          case None => notFound("Person not found")
          case Some(person) =>
            val oldPhotoId = person.photo.photoId.getOrElse("")
            val photo = photoType match {
              case "nophoto" ⇒ Photo.empty
              case "gravatar" ⇒ Photo(photoType, person.email).copy(photoId = Some(oldPhotoId))
              case _ ⇒ Photo(Some(photoType), photoUrl(oldPhotoId), Some(oldPhotoId))
            }
            repos.person.updatePhoto(id, photo.photoId, photo.url) flatMap { _ =>
              if (photo.typ.nonEmpty) {
                recalculator ! ("complete", id, ProfileStrength.Steps.PHOTO)
              }
              jsonSuccess("ok")
            }
        })
  }

  /**
    * Upload a new photo to Amazon
    *
    * @param id Person identifier
    */
  def upload(id: Long) = ProfileAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    repos.person.find(id) flatMap {
      case None => jsonNotFound("Person not found")
      case Some(person) =>
        val photoId = Random.alphanumeric.take(32).mkString
        uploadFile(Person.photo(photoId), "photo") flatMap { _ ⇒
          val url = photoUrl(photoId)
          repos.person.updatePhoto(id, Some(photoId), url) flatMap { _ =>
            jsonOk(Json.obj("link" -> url))
          }
        } recover {
          case e ⇒ BadRequest(Json.obj("message" -> e.getMessage))
        }
    }
  }

  /**
    * Returns url to a person's photo
    *
    * @param id Person photo identifier
    */
  protected def photoUrl(id: String): Option[String] = {
    val photo = Person.photo(id)
    Utilities.cdnUrl(photo.name).orElse(Some(Utilities.fullUrl(routes.ProfilePhotos.photo(id).url)))
  }
}
