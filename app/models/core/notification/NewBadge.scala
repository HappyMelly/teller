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

import controllers.cm.brand.Badges
import controllers.core.People
import models._
import models.cm.brand.Badge
import play.api.libs.json.{JsObject, Json}

/**
  * Represents badge notification
  *
  * @param to Person to whom the badge is assigned
  * @param from Person identifier who assigned the badge
  * @param badge Badge itself
  * @param version Badge markup version
  */
case class NewBadge(to: Person, from: Long, badge: Badge, version: Int = 2) extends BrandNotification {

  val brandId: Long = badge.brandId
  val fromId: Long = from
  val toId: Long = to.identifier
  val typ: String = NotificationType.Badge

  def body: JsObject = version match {
    case 1 => v1
    case _ => v2
  }

  protected def v2: JsObject = Json.obj("img" -> People.pictureUrl(to),
    "name" -> to.name,
    "badge" -> badge.name,
    "badgeImg" -> Badges.pictureUrl(badge, "icon"),
    "url" -> controllers.core.routes.People.details(to.identifier).url)

  protected def v1: JsObject = Json.obj("img" -> Badges.pictureUrl(badge, "icon"),
    "name" -> to.name,
    "badge" -> badge.name,
    "url" -> controllers.core.routes.People.details(to.identifier).url)
}

object NewBadge {

  /**
    * Returns rendered notification of this type
    *
    * @param notification Notification
    */
  def render(notification: Notification): String = notification.version match {
    case 1 => v1(notification)
    case _ => v2(notification)
  }

  protected def v1(notification: Notification): String = {
    val img = (notification.body \ "img").get.as[String]
    val name = (notification.body \ "name").get.as[String]
    val badge = (notification.body \ "badge").get.as[String]
    val url = (notification.body \ "url").get.as[String]
    views.html.v2.core.notification.newBadgeV1(img, name, badge, url).toString()
  }

  protected def v2(notification: Notification): String = {
    val img = (notification.body \ "img").get.as[String]
    val name = (notification.body \ "name").get.as[String]
    val badge = (notification.body \ "badge").get.as[String]
    val badgeImg = (notification.body \ "badgeImg").get.as[String]
    val url = (notification.body \ "url").get.as[String]
    views.html.v2.core.notification.newBadgeV2(img, name, badge, badgeImg, url).toString()
  }
}
