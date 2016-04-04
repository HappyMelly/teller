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

package controllers.cm

import akka.actor.ActorRef
import controllers.AsyncController
import models.cm.event.Attendee
import models.{UploadException, BrandWithSettings, Person}
import models.cm.{Certificate, Event}
import models.repository.IRepositories
import org.joda.time.LocalDate
import play.api.libs.json.Json
import play.api.mvc.Result
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Provides a method for generating certificates and handling all related logic
  */
trait CertificateFactory extends AsyncController {
  val repos: IRepositories
  val mailer: ActorRef

  def generateCertificate(issued: Option[LocalDate],
                          event: Event,
                          brandView: BrandWithSettings,
                          attendee: Attendee,
                          approver: Person): Future[Result] = {

    if (brandView.settings.certificates && !event.free) {
      val cert = new Certificate(issued, event, attendee)
      cert.file(Certificate.name(brandView.brand), repos) match {
        case Some(file) =>
          file.file.upload().flatMap { _ =>
            val withCertificate = attendee.copy(certificate = Some(cert.id), issued = cert.issued)
            repos.cm.rep.event.attendee.updateCertificate(withCertificate)
            mailer ! ("approve", approver, attendee, brandView.brand, event, file)
            jsonOk(Json.obj("date" -> issued))
          }.recover {
            case e: UploadException => InternalServerError(Json.obj("message" -> e.msg))
          }
        case None =>
          mailer ! ("approve", approver, attendee, brandView.brand, event)
          jsonOk(Json.obj("date" -> issued))
      }
    } else {
      jsonForbidden("Certificate generation for this event is not possible")
    }

  }
}
