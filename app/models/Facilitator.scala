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
  yearsOfExperience: Int = 0,
  numberOfEvents: Int = 0,
  rating: Float = 0.0f)

object Facilitator extends Services {
  val ratingActor = Akka.system.actorOf(Props[RatingCalculatorActor])

  /**
   * Updates facilitator rating
   */
  class RatingCalculatorActor extends Actor with Services {

    private val MGT30_IDENTIFIER = 1

    def receive = {
      case eventId: Long ⇒
        eventService.find(eventId) foreach { x ⇒
          x.facilitatorIds.foreach { id ⇒
            val events = eventService.findByFacilitator(id, Some(x.brandId)).map(_.id.get)
            val evaluations = evaluationService.findByEventsWithParticipants(events).filter(_._3.approved)
            val oldEvaluations = if (x.brandId == MGT30_IDENTIFIER)
              OldEvaluation.findByFacilitator(id)
            else 
              List()
            val rating = calculateRating(evaluations.map(_._3), oldEvaluations)
            val brand = brandService.find(x.brandId).get
            facilitatorService.update(Facilitator(None, id, brand.id.get, rating = rating))
          }
        }
    }

    /**
     * Calculates average rating from the given evaluations
     * @param evaluations Evaluations added to Teller
     * @param oldEvaluations Evaluations added to old Management 3.0 system
     */
    protected def calculateRating(evaluations: List[Evaluation], oldEvaluations: List[OldEvaluation]): Float = {
      val impressions = evaluations.map(_.impression) ::: oldEvaluations.map(_.impression)
      impressions.foldLeft(0.0f)(_ + _.toFloat / impressions.length)
    }
  }

  /**
   * Update number of events and years of experience for all facilitators
   */
  def updateFacilitatorExperience(): Unit = {
    licenseService.findActive.foreach { license =>
      val yearsOfExperience = license.length.getStandardDays / 365
      val facilitator = Facilitator(None, license.licenseeId, license.brandId,
        yearsOfExperience.toInt, calculateNumberOfEvents(license))
      facilitatorService.updateExperience(facilitator)
    }
  }

  /**
   * Returns number of events for the given license holder
   * @param license License
   */
  protected def calculateNumberOfEvents(license: License): Int = {
    val MGT30_IDENTIFIER = 1
    val numberOfOldEvents = if (license.brandId == MGT30_IDENTIFIER)
      OldEvaluation.findByFacilitator(license.licenseeId).map(_.eventId).distinct.length
    else
      0
    val numberOfEvents = eventService.
      findByFacilitator(license.licenseeId, Some(license.brandId)).
      count(_.confirmed)
    numberOfEvents + numberOfOldEvents
  }
}
