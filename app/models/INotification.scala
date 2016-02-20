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
import controllers.core.People
import models.brand.Badge
import play.api.libs.json.{JsValue, Json, JsObject}

case class Notification(id: Option[Long],
                        personId: Long,
                        body: JsValue,
                        typ: String,
                        unread: Boolean = true) {

  def render: String = typ match {
    case NotificationType.NewBadge =>
      val img = (body \ "img").get.as[String]
      val name = (body \ "name").get.as[String]
      val badge = (body \ "badge").get.as[String]
      val url = (body \ "url").get.as[String]
      views.html.v2.core.notification.newBadge(img, name, badge, url).toString()

    case NotificationType.NewFacilitator =>
      val img = (body \ "img").get.as[String]
      val name = (body \ "name").get.as[String]
      val brand = (body \ "brand").get.as[String]
      val url = (body \ "url").get.as[String]
      views.html.v2.core.notification.newFacilitator(img, name, brand, url).toString()

    case NotificationType.NewPersonalBadge =>
      val img = (body \ "img").get.as[String]
      val badge = (body \ "badge").get.as[String]
      views.html.v2.core.notification.newPersonalBadge(img, badge).toString()
  }
}

trait INotification {
  val typ: String
  def notification(personId: Long): Notification
}

object NotificationType {
  val NewBadge = "new-badge"
  val NewFacilitator = "new-facilitator"
  val NewPersonalBadge = "new-personal-badge"
}

case class NewBadge(person: Person, badge: Badge) extends INotification {
  override val typ: String = NotificationType.NewBadge

  def body: JsObject = Json.obj("img" -> Badges.pictureUrl(badge, "icon"),
    "name" -> person.name,
    "badge" -> badge.name,
    "url" -> controllers.core.routes.People.details(person.identifier).url)

  def notification(personId: Long): Notification = Notification(None, personId, body, typ)
}

case class NewFacilitator(person: Person, brand: Brand) extends INotification {
  override val typ: String = NotificationType.NewFacilitator

  def body: JsObject = Json.obj("img" -> People.pictureUrl(person),
    "name" -> person.name,
    "brand" -> brand.name,
    "url" -> controllers.core.routes.People.details(person.identifier).url)

  def notification(personId: Long): Notification = Notification(None, personId, body, typ)
}

case class NewPersonalBadge(person: Person, badge: Badge) extends INotification {
  override val typ: String = NotificationType.NewPersonalBadge

  def body: JsObject = Json.obj("img" -> Badges.pictureUrl(badge, "icon"),
    "badge" -> badge.name)

  def notification: Notification = notification(person.identifier)

  def notification(personId: Long): Notification = Notification(None, personId, body, typ)
}

