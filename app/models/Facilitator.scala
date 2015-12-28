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
  publicRating: Float = 0.0f,
  privateRating: Float = 0.0f,
  publicMedian: Float = 0.0f,
  privateMedian: Float = 0.0f,
  publicNps: Float = 0.0f,
  privateNps: Float = 0.0f,
  numberOfPublicEvaluations: Int = 0,
  numberOfPrivateEvaluations: Int = 0,
  badges: List[Long] = List())

object Facilitator extends Services {
  val ratingActor = Akka.system.actorOf(Props[RatingCalculatorActor])

  /**
   * Updates facilitator rating
   */
  class RatingCalculatorActor extends Actor with Services {

    private val MGT30_IDENTIFIER = 1

    def receive = {
      case "init" =>
        initialCalculation()
      case eventId: Long ⇒
        eventService.find(eventId) foreach { x ⇒
          x.facilitatorIds.foreach { id ⇒
            val events = eventService.findByFacilitator(id, Some(x.brandId)).map(_.id.get)
            val evaluations = evaluationService.findByEventsWithParticipants(events).filter(_._3.approved)
            val oldEvaluations = if (x.brandId == MGT30_IDENTIFIER)
              OldEvaluation.findByFacilitator(id)
            else 
              List()
            val facilitator = calculateRatings(evaluations, oldEvaluations)
            facilitatorService.update(facilitator.copy(personId = id, brandId = x.brandId))
          }
        }
    }

    def initialCalculation() = {
      facilitatorService.findAll.foreach { x =>
        val events = eventService.findByFacilitator(x.personId, Some(x.brandId)).map(_.id.get)
        val evaluations = evaluationService.findByEventsWithParticipants(events).filter(_._3.approved)
        val oldEvaluations = if (x.brandId == MGT30_IDENTIFIER)
          OldEvaluation.findByFacilitator(x.personId)
        else
          List()
        val facilitator = calculateRatings(evaluations, oldEvaluations)
        facilitatorService.update(facilitator.copy(personId = x.personId, brandId = x.brandId))
      }
    }

    /**
     * Calculates average rating from the given evaluations
     * @param evaluations Evaluations added to Teller
     * @param oldEvaluations Evaluations added to old Management 3.0 system
     * @return Returns facilitator object with calculated ratings
     */
    protected def calculateRatings(evaluations: List[(Event, Person, Evaluation)],
                                   oldEvaluations: List[OldEvaluation]): Facilitator = {
      val publicImpressions = evaluations.filterNot(_._1.notPublic).map(_._3.impression) :::
        oldEvaluations.filterNot(_.notPublic).map(_.impression)
      val privateImpressions = evaluations.filter(_._1.notPublic).map(_._3.impression) :::
        oldEvaluations.filter(_.notPublic).map(_.impression)
      Facilitator(None, 0, 0,
        publicRating = calculateAverage(publicImpressions),
        privateRating = calculateAverage(privateImpressions),
        publicMedian = calculateMedian(publicImpressions),
        privateMedian = calculateMedian(privateImpressions),
        publicNps = calculateNps(evaluations.filterNot(_._1.notPublic).map(_._3.recommendationScore)),
        privateNps = calculateNps(evaluations.filter(_._1.notPublic).map(_._3.recommendationScore)),
        numberOfPublicEvaluations = publicImpressions.length,
        numberOfPrivateEvaluations = privateImpressions.length)
    }

    /**
     * Returns median for the given list of impressions
     * @param impressions List of impressions
     */
    protected def calculateMedian(impressions: List[Int]): Float = {
      if (impressions.isEmpty) {
        0.0f
      } else {
        val sorted = impressions.sorted.toArray
        if (sorted.length % 2 == 0)
          (sorted(sorted.length / 2 - 1) + sorted(sorted.length / 2)) / 2
        else
          sorted((sorted.length - 1) / 2)
      }
    }

    /**
     * Returns NPS value for the given list of recommendations
     * @param recommendations Recommendations
     */
    protected def calculateNps(recommendations: List[Int]): Float = {
      if (recommendations.isEmpty) {
        0.0f
      } else {
        (recommendations.count(_ >= 9) - recommendations.count(_ <= 6)).toFloat / recommendations.length * 100
      }
    }

    /**
     * Returns average for the given sequence
     * @param impressions Impressions
     */
    protected def calculateAverage(impressions: List[Int]): Float = {
      if (impressions.isEmpty)
        0.0f
      else
        impressions.foldLeft(0.0f)(_ + _.toFloat / impressions.length)
    }
  }

  /**
   * Update number of events and years of experience for all facilitators
   */
  def updateFacilitatorExperience(): Unit = {
    licenseService.findAll.foreach { license =>
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
    eventService.
      findByFacilitator(license.licenseeId, Some(license.brandId)).
      count(_.confirmed)
  }
}
