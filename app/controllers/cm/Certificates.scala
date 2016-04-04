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

package controllers.cm

import javax.inject.{Inject, Named}

import akka.actor.ActorRef
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.{Files, Security}
import models.UserRole.Role
import models.cm.Certificate
import models.repository.Repositories
import org.joda.time.LocalDate
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment

class Certificates @Inject() (override implicit val env: TellerRuntimeEnvironment,
                              override val messagesApi: MessagesApi,
                              @Named("evaluation-mailer") val mailer: ActorRef,
                              val repos: Repositories,
                              deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env)
  with CertificateFactory
  with Files {

  /**
   * Generate new certificate
   *
   * @param eventId Event identifier
   * @param attendeeId Person identifier
   */
  def create(eventId: Long, attendeeId: Long) = EventAction(List(Role.Facilitator, Role.Coordinator), eventId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒ implicit event =>
      (for {
        attendee <- repos.cm.rep.event.attendee.find(attendeeId, eventId)
        brand <- repos.cm.brand.findWithSettings(event.brandId)
      } yield (attendee, brand)) flatMap {
        case (None, _) => jsonNotFound("Attendee not found")
        case (_, None) => jsonNotFound("Brand not found")
        case (Some(attendee), Some(withSettings)) =>
          val issued = attendee.issued.getOrElse(LocalDate.now())
          generateCertificate(Some(issued), event, withSettings, attendee, user.person)
      }
  }

  /**
   * Retrieve and cache a certificate
   *
   * @param certificateId Certificate identifier
   */
  def certificate(certificateId: String) = file(Certificate.file(certificateId))

}