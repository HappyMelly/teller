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
package models.actors

import javax.inject.Inject

import akka.actor.Actor
import models.event.Attendee
import models.repository.Repositories
import models.{Evaluation, Event, Facilitator, OldEvaluation}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This actor recalculates the rating for all facilitators of the given event
  */
class FacilitatorRatingCalculator @Inject() (val repos: Repositories) extends Actor {

  private val MGT30_IDENTIFIER = 1

  def receive = {
    case "init" =>
      initialCalculation()
    case eventId: Long ⇒
      (for {
        e <- repos.event.get(eventId)
        f <- repos.event.facilitatorIds(eventId)
      } yield (e, f)) map { case (event, facilitators) =>
        facilitators.foreach { id =>
          val evaluationQuery = for {
            events <- repos.event.findByFacilitator(id, Some(event.brandId))
            evaluations <- repos.evaluation.findByEventsWithAttendees(events.map(_.id.get))
            oldEvaluations <- if (event.brandId == MGT30_IDENTIFIER)
              repos.evaluation.findOldEvaluations(id)
            else
              Future.successful(List())
          } yield (evaluations.filter(_._3.approved), oldEvaluations)
          evaluationQuery map { case (evaluations, oldEvaluations) =>
            val facilitator = calculateRatings(evaluations, oldEvaluations)
            repos.facilitator.update(facilitator.copy(personId = id, brandId = event.brandId))
          }
        }
      }
  }

  def initialCalculation() = repos.facilitator.findAll map { data =>
    data.foreach { x =>
      val evaluationQuery = for {
        events <- repos.event.findByFacilitator(x.personId, Some(x.brandId))
        evaluations <- repos.evaluation.findByEventsWithAttendees(events.map(_.id.get))
        oldEvaluations <- if (x.brandId == MGT30_IDENTIFIER)
          repos.evaluation.findOldEvaluations(x.personId)
        else
          Future.successful(List())
      } yield (evaluations.filter(_._3.approved), oldEvaluations)
      evaluationQuery map { case (evaluations, oldEvaluations) =>
        val facilitator = calculateRatings(evaluations, oldEvaluations)
        repos.facilitator.update(facilitator.copy(personId = x.personId, brandId = x.brandId))
      }
    }
  }

  /**
    * Calculates average rating from the given evaluations
    *
    * @param evaluations Evaluations added to Teller
    * @param oldEvaluations Evaluations added to old Management 3.0 system
    * @return Returns facilitator object with calculated ratings
    */
  protected def calculateRatings(evaluations: List[(Event, Attendee, Evaluation)],
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
    *
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
    *
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
    *
    * @param impressions Impressions
    */
  protected def calculateAverage(impressions: List[Int]): Float = {
    if (impressions.isEmpty)
      0.0f
    else
      impressions.foldLeft(0.0f)(_ + _.toFloat / impressions.length)
  }
}
