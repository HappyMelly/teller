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

import play.api.mvc.{ AnyContent, Request }
import play.mvc.Controller
import play.api.libs.json._
import models._

/**
 * Certificates API
 */
object CertificatesApi extends Controller with ApiAuthentication {

  /** Generate new certificate through API call **/
  def create(id: Long) = TokenSecuredActionWithIdentity {
    (request: Request[AnyContent], identity: UserIdentity) ⇒
      val approver = identity.account.person.get
      Evaluation.find(id).map {
        evaluation ⇒
          val brand = Brand.find(evaluation.event.brandCode).get
          val certificate = new Certificate(evaluation)
          certificate.generateAndSend(brand, approver)
          Ok(Json.obj("success" -> "Certificate generated"))
      }.getOrElse(NotFound)
  }

}
