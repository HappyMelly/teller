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
import models.{NewPersonalBadge, NewBadge, NewFacilitator}
import models.repository.Repositories
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Adds notifications to database depending on their type
  */
class NotificationDispatcher @Inject() (val repos: Repositories) extends Actor {

  def receive = {
    case notification: NewFacilitator => addNewFacilitatorNotifications(notification)
    case notification: NewBadge => addNewBadgeNotification(notification)
    case notification: NewPersonalBadge =>
      repos.notification.insert(Seq(notification.notification))
  }

  protected def addNewBadgeNotification(notification: NewBadge) = {
    (for {
      c <- repos.brandCoordinator.find(notification.badge.brandId)
      f <- repos.license.findByBrand(notification.badge.brandId)
    } yield (c.map(_.personId), f.map(_.licenseeId))) map { case (coordinators, facilitators) =>
      val ids = (coordinators.toList ::: facilitators).distinct.filterNot(_ == notification.person.identifier)
      val records = ids.map(personId => notification.notification(personId))
      repos.notification.insert(records)
    }
  }

  protected def addNewFacilitatorNotifications(notification: NewFacilitator) = {
    (for {
      c <- repos.brandCoordinator.find(notification.brand.identifier)
      f <- repos.license.findByBrand(notification.brand.identifier)
    } yield (c.map(_.personId), f.map(_.licenseeId))) map { case (coordinators, facilitators) =>
      val ids = (coordinators.toList ::: facilitators).distinct.filterNot(_ == notification.person.identifier)
      val records = ids.map(personId => notification.notification(personId))
      repos.notification.insert(records)
    }
  }
}
