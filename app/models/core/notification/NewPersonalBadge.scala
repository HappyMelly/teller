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

package models.core.notification

import controllers.brand.Badges
import models.{Notification, NotificationType, INotification, Person}
import models.cm.brand.Badge
import play.api.libs.json.{Json, JsObject}

/**
  * Represents personal badge notification
  * @param person Person who got the badge
  * @param badge Badge itself
  * @param version Badge markup version
  */
case class NewPersonalBadge(person: Person, badge: Badge, version: Int = 1) extends INotification {

  val fromId: Long = 0
  val toId: Long = 0
  val typ: String = NotificationType.PersonalBadge

  def body: JsObject = Json.obj("img" -> Badges.pictureUrl(badge, "icon"),
    "badge" -> badge.name)

  def notification: Notification = toNotification(person.identifier)
}

object NewPersonalBadge {

  /**
    * Returns rendered notification of this type
    * @param notification Notification
    */
  def render(notification: Notification): String = {
    val img = (notification.body \ "img").get.as[String]
    val badge = (notification.body \ "badge").get.as[String]
    views.html.v2.core.notification.newPersonalBadge(img, badge).toString()
  }
}
