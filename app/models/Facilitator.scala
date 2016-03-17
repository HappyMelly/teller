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
package models

import models.repository.Repositories

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
                      badges: List[Long] = List(),
                       creditsGiven: Int = 0,
                       creditsReceived: Int = 0)

object Facilitator {

  /**
   * Update number of events and years of experience for all facilitators
   */
  def updateFacilitatorExperience(services: Repositories): Unit = services.license.findAll map { licenses =>
    licenses.foreach { license =>
      val yearsOfExperience = license.length.getStandardDays / 365
      calculateNumberOfEvents(license, services) map { number =>
        val facilitator = Facilitator(None, license.licenseeId, license.brandId,
          yearsOfExperience.toInt, number)
        services.facilitator.updateExperience(facilitator)
      }
    }
  }

  /**
   * Returns number of events for the given license holder
    *
    * @param license License
   */
  protected def calculateNumberOfEvents(license: License, services: Repositories): Future[Int] =
    services.event.findByFacilitator(license.licenseeId, Some(license.brandId)).map(_.count(_.confirmed))
}
