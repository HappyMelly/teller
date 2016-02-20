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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers.core

import javax.inject.Inject

import controllers.Security
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import be.objectify.deadbolt.scala.cache.HandlerCache
import models.Notification
import models.UserRole.Role._
import models.repository.Repositories
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.libs.json.{Json, Writes}
import services.TellerRuntimeEnvironment

/**
  * Manages notifications
  */
class Notifications @Inject() (override implicit val env: TellerRuntimeEnvironment,
                               override val messagesApi: MessagesApi,
                               val repos: Repositories,
                               deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env) {

  /**
    * Returns list of notifications for current user
    *
    * @param offset Offset
    * @param limit Number of notifications to retrieve
    * @return
    */
  def list(offset: Long = 0, limit: Long = 5) = RestrictedAction(Viewer) { implicit request => implicit handler =>
    implicit user =>
      implicit val notificationWrites = new Writes[Notification] {
        def writes(notification: Notification) = {
          Json.obj(
            "id" -> notification.id,
            "type" -> notification.typ,
            "body" -> notification.body,
            "unread" -> notification.unread)
        }
      }
      repos.notification.find(user.person.identifier, offset, limit) flatMap { notifications =>
        jsonOk(Json.arr(notifications))
      }
  }

  /**
    * Marks the given notifications as read
    */
  def read() = RestrictedAction(Viewer) { implicit request => implicit handler => implicit user =>
    val form = Form(single("ids" -> play.api.data.Forms.list(longNumber)))

    form.bindFromRequest.fold(
      errors => jsonBadRequest("Missing notification identifiers"),
      ids => repos.notification.read(ids, user.person.identifier) flatMap { _ =>
        jsonSuccess("Notifications were marked as read")
      }
    )
  }
}
