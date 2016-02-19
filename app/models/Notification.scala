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
package models

import controllers.brand.Badges
import models.brand.Badge
import spray.json._

/**
  * Represents instant notification
  */
case class Notification(image: String, body: String, exclude: Option[String] = None)

class NotificationWriter extends JsonWriter[Notification] {
  def write(obj: Notification) = JsObject(
    "image" -> JsString(obj.image),
    "body" -> JsString(obj.body),
    "exclude" -> obj.exclude.map(x => JsString(x)).getOrElse(JsNull)
  )
}

object Notification {

  def badge(person: Person, badge: Badge): Notification = {
    val body = s"${person.fullName} got a badge ${badge.name}"
    Notification(person.photo.url.getOrElse(""), body, Some(person.identifier.toString))
  }

  def newFacilitator(person: Person, brand: String) = {
    val body = s"${person.fullName} joined as a new $brand facilitator"
    Notification(person.photo.url.getOrElse(""), body, Some(person.identifier.toString))
  }

  def personalBadge(person: Person, badge: Badge): Notification = {
    val body = s"Congratulations! You've got ${badge.name} badge"
    Notification(Badges.pictureUrl(badge, "icon"), body)
  }

  object Events {
    val facilitator = "new-facilitator"
    val badge = "new-badge"
  }
}
