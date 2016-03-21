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
import models.repository.Repositories
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Turns on/off peer credit parameter for accounts
  */
class PeerCreditsConfigurator @Inject() (val repos: Repositories) extends Actor {

  def receive = {
    case (personId: Long, brandId: Long, on: Boolean) => updateSingleAccount(personId, brandId, on)
    case (brandId: Long, on: Boolean) => updateMultipleAccounts(brandId, on)

  }

  /**
    * Turns on/off peer credit parameter for all brand coordinators and facilitators
    * @param brandId Brand identifier
    * @param on If true, peer credit parameters should be turned on
    */
  protected def updateMultipleAccounts(brandId: Long, on: Boolean) = {
    val request = for {
      c <- repos.cm.brand.coordinators(brandId)
      f <- repos.cm.license.licensees(brandId)
    } yield (c.map(_._1.identifier), f.map(_.identifier))
    request map { case (coordinators, facilitators) =>
      val ids = (coordinators ++ facilitators).distinct
      if (on)
        repos.userAccount.updateCredits(ids, on)
      else
        ids.foreach(personId => updateSingleAccount(personId, brandId, on))
    }
  }

  /**
    * Turns on/off peer credit parameter for the given person on brand action
    * @param personId Account of interest
    * @param brandId Brand identifier
    * @param on If true, peer credit parameters should be turned on
    */
  protected def updateSingleAccount(personId: Long, brandId: Long, on: Boolean) = {
    val f = for (a <- repos.userAccount.findByPerson(personId) if a.nonEmpty) yield a.get
    f map { account =>
      if (on && account.credits.isEmpty) {
        repos.userAccount.update(account.copy(credits = Some(0)))
      } else if (!on && account.credits.nonEmpty) {
        val request = for {
          c <- repos.cm.brand.findByCoordinator(personId)
          l <- repos.cm.brand.findByLicense(personId, onlyActive = true)
        } yield (c ++ l).distinct
        request map { brands =>
          val withCredits = brands.filterNot(_.brand.identifier == brandId).filter(_.settings.credits)
          if (withCredits.isEmpty)
            repos.userAccount.update(account.copy(credits = None))
        }
      }
    }
  }

}