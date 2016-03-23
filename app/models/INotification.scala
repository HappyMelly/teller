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

import models.core.notification.{CreditReceived, NewBadge, NewFacilitator, NewPersonalBadge}
import org.joda.time.DateTime
import play.api.libs.json.{JsObject, JsValue}

case class Notification(id: Option[Long],
                        personId: Long,
                        body: JsValue,
                        typ: String,
                        version: Int = 1,
                        unread: Boolean = true,
                        created: DateTime = DateTime.now()) {

  def render: String = typ match {
    case NotificationType.Badge => NewBadge.render(this)
    case NotificationType.Facilitator => NewFacilitator.render(this)
    case NotificationType.PersonalBadge => NewPersonalBadge.render(this)
    case NotificationType.CreditReceived => CreditReceived.render(this)
  }
}

trait INotification {
  val typ: String
  val version: Int
  val toId: Long
  val fromId: Long
  def body: JsObject
  def toNotification(personId: Long): Notification = Notification(None, personId, body, typ, version)
}

trait BrandNotification extends INotification {
  val brandId: Long
}

object NotificationType {
  val CreditReceived = "credit-received"
  val Badge = "new-badge"
  val Facilitator = "new-facilitator"
  val PersonalBadge = "new-personal-badge"
}
