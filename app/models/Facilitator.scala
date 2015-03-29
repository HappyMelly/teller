/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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

package models

import akka.actor.{ Actor, Props }
import models.service.Services
import play.api.Play.current
import play.api.libs.concurrent.Akka

/**
 * Represents facilitator entity
 */
case class Facilitator(id: Option[Long] = None,
  personId: Long,
  brandId: Long,
  rating: Float = 0.0f)

object Facilitator {
  val ratingActor = Akka.system.actorOf(Props[RatingCalculatorActor])

  /**
   * Updates facilitator rating
   */
  class RatingCalculatorActor extends Actor with Services {
    def receive = {
      case eventId: Long ⇒
        eventService.find(eventId) foreach { x ⇒
          x.facilitatorIds.foreach { id ⇒
            val events = eventService.findByFacilitator(id, Some(x.brandCode)).map(_.id.get)
            val evaluations = evaluationService.findByEvents(events).filter(_._3.approved)
            val rating = evaluations.foldLeft(0.0f)(_ + _._3.question6.toFloat / evaluations.length)
            val brand = brandService.find(x.brandCode).get
            println(evaluations.toString())
            facilitatorService.update(Facilitator(None, id, brand.id.get, rating))
          }
        }
    }
  }
}
