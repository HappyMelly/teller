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

package controllers

import models.UserRole.DynamicRole
import models.service.Services
import models.{ActiveUser, Certificate}
import org.joda.time.LocalDate
import play.api.libs.json.Json
import securesocial.core.RuntimeEnvironment

class Certificates(environment: RuntimeEnvironment[ActiveUser])
    extends JsonController
    with Security
    with Services
    with Files {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  /**
   * Generate new certificate
   *
   * @param eventId Event identifier
   * @param personId Person identifier
   * @param ref Identifier of a page where a user should be redirected
   */
  def create(eventId: Long,
    personId: Long,
    ref: Option[String] = None) = SecuredDynamicAction("event", DynamicRole.Facilitator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        participantService.find(personId, eventId) map { participant ⇒
          val approver = user.person
          val evaluation = if (participant.evaluationId.nonEmpty)
            participant.evaluation
          else
            None
          val event = participant.event.get
          if (event.free) {
            NotFound
          } else {
            val brand = brandService.findWithCoordinators(event.brandId).get
            val person = participant.person.get
            val issued = participant.issued getOrElse LocalDate.now()
            val certificate = new Certificate(Some(issued), event, person, renew = true)
            certificate.generateAndSend(brand, approver)
            participant.copy(
              certificate = Some(certificate.id),
              issued = Some(issued)).update
            jsonOk(Json.obj("certificate" -> certificate.id))
          }
        } getOrElse NotFound
  }

  /**
   * Retrieve and cache a certificate
   *
   * @param certificateId Certificate identifier
   */
  def certificate(certificateId: String) = file(Certificate.file(certificateId))

}
