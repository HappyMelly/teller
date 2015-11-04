/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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
import models.service.Services

/**
 * This class is responsible for checking dynamic access rights to objects in
 *  the system
 *
 * @param user UserAccount
 */
class DynamicResourceChecker(user: UserAccount) extends Services {

  /**
   * Returns true if the user is a brand coordinator
   *
   * @param brandId Brand identifier
   */
  def isBrandCoordinator(brandId: Long): Boolean = {
    brandService.isCoordinator(brandId, user.personId)
  }

  /**
   * Returns true if the user is a brand facilitator
   *
   * @param brandId Brand identifier
   */
  def isBrandFacilitator(brandId: Long): Boolean = {
    brandService.isCoordinator(brandId, user.personId) ||
      licenseService.activeLicense(brandId, user.personId).nonEmpty
  }

  /**
   * Returns true if the user is an event coordinator
   *
   * @param eventId Event identifier
   */
  def isEventCoordinator(eventId: Long): Boolean = {
    eventService.find(eventId).exists { x ⇒
      brandService.isCoordinator(x.brandId, user.personId)
    }
  }

  /**
   * Returns true if the user is an event facilitator
   *
   * @param eventId Event identifier
   */
  def isEventFacilitator(eventId: Long): Boolean = {
    val userId = user.personId
    eventService.find(eventId).exists { x ⇒
      x.isFacilitator(userId) || brandService.isCoordinator(x.brandId, userId)
    }
  }

  /**
   * Returns true if the user is an evaluation coordinator
   *
   * @param evaluationId Evaluation identifier
   */
  def isEvaluationCoordinator(evaluationId: Long): Boolean = {
    eventService.findByEvaluation(evaluationId).exists { x ⇒
      brandService.isCoordinator(x.brandId, user.personId)
    }
  }

  /**
   * Returns true if the user is an evaluation facilitator
   *
   * @param evaluationId Evaluation identifier
   */
  def isEvaluationFacilitator(evaluationId: Long): Boolean = {
    val userId = user.personId
    eventService.findByEvaluation(evaluationId).exists { x ⇒
      x.isFacilitator(userId) || brandService.isCoordinator(x.brandId, userId)
    }
  }

  /**
   * Returns true if the user is a member editor
   *
   * @param memberId Member identifier
   */
  def isMemberEditor(memberId: Long): Boolean = {
    user.admin || brandService.isCoordinator(memberId, user.personId)
  }

  /**
   * Returns true if the user can edit the given person
   * @param personId Person identifier
   */
  def canEditPerson(personId: Long): Boolean = {
    user.admin || user.personId == personId ||
      personService.find(personId).exists { person ⇒
        if (person.virtual)
          eventService.findByParticipation(personId, user.personId).nonEmpty
        else
          false
      }
  }

  /**
   * Returns true if the user can delete the given person
   * @param personId Person identifier
   */
  def canDeletePerson(personId: Long): Boolean = {
    user.admin || personService.find(personId).exists { person ⇒
      if (person.virtual)
        eventService.findByParticipation(personId, user.personId).nonEmpty
      else
        false
    }
  }
}