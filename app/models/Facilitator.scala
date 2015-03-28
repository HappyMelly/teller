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

import akka.actor.Actor
import models.service.Services

/**
 * Represents facilitator entity
 */
case class Facilitator(id: Option[Long] = None,
  personId: Long,
  brandId: Long,
  rating: Float = 0.0f)

object Facilitator {

  /**
   * Updates facilitator rating
   */
  class RatingCalculatorActor extends Actor with Services {
    def receive = {
      case msg: (Long, String) ⇒
        val events = eventService.findByFacilitator(msg._1, Some(msg._2)).map(_.id.get)
//        val evaluations = evaluationService.findByEvents(events)
//        val rating = evaluations.foldLeft(0.0f)(_ + _.question6.toFloat) / evaluations.length
//        eventService.updateRating(personId, rating)
    }
  }
}
