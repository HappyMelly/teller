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

import models._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.cache.Cache
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import securesocial.core.SecuredRequest
import scala.concurrent.Future

case class FakeCertificateTemplate(brandCode: String, language: String, template: Option[String], templateNoFacilitator: Option[String])

object CertificateTemplates extends Controller with Security {

  /** HTML form mapping for creating certificate templates */
  def certificateFileForm(implicit request: SecuredRequest[_]) = Form(mapping(
    "brandCode" -> nonEmptyText,
    "language" -> nonEmptyText,
    "template" -> optional(text),
    "templateNoFacilitator" -> optional(text))(FakeCertificateTemplate.apply)(FakeCertificateTemplate.unapply))

  /**
   * Add page
   *
   * @param code Unique text brand identifier
   * @return
   */
  def add(code: String) = SecuredDynamicAction("evaluation", "manage") { implicit request ⇒
    implicit handler ⇒
      Brand.find(code).map { brand ⇒
        Ok(views.html.certificateTemplate.form(request.user, brand.brand, certificateFileForm))
      }.getOrElse(NotFound)
  }

}
