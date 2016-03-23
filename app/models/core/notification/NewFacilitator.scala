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

import controllers.core.People
import models._
import play.api.libs.json.{Json, JsObject}

/**
  * Represents facilitator notification
 *
  * @param facilitator Person who became a facilitator
  * @param from Person identifier who added the facilitator
  * @param brand Brand this facilitator belongs to
  * @param version Badge markup version
  */
case class NewFacilitator(facilitator: Person, from: Long, brand: Brand, version: Int = 1) extends BrandNotification {

  val brandId: Long = brand.identifier
  val fromId: Long = from
  val toId: Long = facilitator.identifier
  val typ: String = NotificationType.Facilitator

  def body: JsObject = Json.obj("img" -> People.pictureUrl(facilitator),
    "name" -> facilitator.name,
    "brand" -> brand.name,
    "url" -> controllers.core.routes.People.details(facilitator.identifier).url)
}

object NewFacilitator {

  /**
    * Returns rendered notification of this type
    * @param notification Notification
    */
  def render(notification: Notification): String = {
    val img = (notification.body \ "img").get.as[String]
    val name = (notification.body \ "name").get.as[String]
    val brand = (notification.body \ "brand").get.as[String]
    val url = (notification.body \ "url").get.as[String]
    views.html.v2.core.notification.newFacilitator(img, name, brand, url).toString()
  }
}