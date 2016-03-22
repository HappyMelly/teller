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
import models.cm.brand.PeerCredit
import models.{INotification, Notification, NotificationType, Person}
import play.api.libs.json.{JsObject, Json}

/**
  * Represents peer credit notification
  * @param credit Peer credit object
  * @param giver Name of the credit giver
  * @param receiver Person who receives the credit
  * @param version Badge markup version
  */
case class CreditReceived(credit: PeerCredit, giver: String, receiver: Person, version: Int = 1) extends INotification {
  override val typ: String = NotificationType.CreditReceived

  def body: JsObject = Json.obj("img" -> People.pictureUrl(receiver),
    "name" -> receiver.name,
    "amount" -> credit.amount,
    "giver" -> giver,
    "url" -> controllers.core.routes.People.details(receiver.identifier).url)
}

object CreditReceived {

  /**
    * Returns rendered notification of this type
    * @param notification Notification
    */
  def render(notification: Notification): String = {
    val img = (notification.body \ "img").get.as[String]
    val name = (notification.body \ "name").get.as[String]
    val giver = (notification.body \ "giver").get.as[String]
    val amount = (notification.body \ "amount").get.as[Int]
    val url = (notification.body \ "url").get.as[String]
    views.html.v2.core.notification.creditReceived(img, name, url, amount, giver).toString()
  }
}
