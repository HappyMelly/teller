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
package models.actors

import javax.inject.Inject

import akka.actor.Actor
import models.BrandNotification
import models.core.notification.{CreditReceived, NewBadge, NewFacilitator, NewPersonalBadge}
import models.repository.Repositories

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Adds notifications to database depending on their type
  */
class NotificationDispatcher @Inject() (val repos: Repositories) extends Actor {

  def receive = {
    case notification: CreditReceived => addBrandNotification(notification)
    case notification: NewFacilitator => addBrandNotification(notification)
    case notification: NewBadge => addBrandNotification(notification)
    case notification: NewPersonalBadge =>
      repos.notification.insert(Seq(notification.notification))
  }

  protected def addBrandNotification(notif: BrandNotification) = {
    (for {
      c <- repos.cm.rep.brand.coordinator.find(notif.brandId)
      f <- repos.cm.license.findByBrand(notif.brandId)
    } yield (c.map(_.personId).toList, f.map(_.licenseeId))) map { case (coordinators, facilitators) =>
      val ids = (coordinators ::: facilitators).distinct.filterNot(_ == notif.toId).filterNot(_ == notif.fromId)
      val records = ids.map(personId => notif.toNotification(personId))
      repos.notification.insert(records)
    }
  }
}
