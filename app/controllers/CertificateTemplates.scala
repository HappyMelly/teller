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
import models.UserRole.Role._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import scala.io.Source
import securesocial.core.SecuredRequest
import views.Languages

/**
 * This class was created to simplify form handling and generation
 */
case class FakeCertificateTemplate(language: String, template: Option[String], templateNoFacilitator: Option[String])

object CertificateTemplates extends Controller with Security {

  val encoding = "ISO-8859-1"

  /** HTML form mapping for creating certificate templates */
  def certificateFileForm(implicit request: SecuredRequest[_]) = Form(mapping(
    "language" -> nonEmptyText,
    "oneFacilitator" -> optional(text),
    "twoFacilitators" -> optional(text))(FakeCertificateTemplate.apply)(FakeCertificateTemplate.unapply))

  /**
   * Add page
   *
   * @param code Unique text brand identifier
   * @return
   */
  def add(code: String) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      Brand.find(code).map { brand ⇒
        val templates = CertificateTemplate.findByBrand(code)
        val languages = Languages.all.filter(lang ⇒ templates.find(_.language == lang._1).isEmpty)
        Ok(views.html.certificateTemplate.form(request.user, brand.brand, languages, certificateFileForm))
      }.getOrElse(NotFound)
  }

  /**
   * Add form submits to this action
   *
   * @param code Unique text brand identifier
   * @return
   */
  def create(code: String) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      Brand.find(code).map { brand ⇒
        val templates = CertificateTemplate.findByBrand(code)
        val languages = Languages.all.filter(lang ⇒ templates.find(_.language == lang._1).isEmpty)
        val form: Form[FakeCertificateTemplate] = certificateFileForm.bindFromRequest
        form.fold(
          formWithErrors ⇒ BadRequest(views.html.certificateTemplate.form(request.user, brand.brand, languages, formWithErrors)),
          data ⇒ {
            templates.find(_.language == data.language).map { v ⇒
              BadRequest(views.html.certificateTemplate.form(request.user, brand.brand, languages, form.withError("language", "error.template.exist")))
            }.getOrElse {
              val template = request.body.asMultipartFormData.get.file("oneFacilitator")
              val templateOneFacilitator = request.body.asMultipartFormData.get.file("twoFacilitators")
              val validMimeTypes = List("image/jpeg", "image/pjpeg", "image/gif", "image/png")
              if (template.isEmpty || templateOneFacilitator.isEmpty
                || !validMimeTypes.contains(template.get.contentType.getOrElse(""))
                || !validMimeTypes.contains(templateOneFacilitator.get.contentType.getOrElse(""))) {
                BadRequest(views.html.certificateTemplate.form(request.user, brand.brand, languages,
                  form.withError("oneFacilitator", "error.required").withError("twoFacilitators", "error.required")))
              } else {
                val firstSource = Source.fromFile(template.get.ref.file.getPath, encoding)
                val secondSource = Source.fromFile(templateOneFacilitator.get.ref.file.getPath, encoding)
                new CertificateTemplate(None, code, data.language, firstSource.toArray.map(_.toByte), secondSource.toArray.map(_.toByte)).insert
                firstSource.close()
                secondSource.close()
                val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, "new certificate template")
                Redirect(routes.Brands.details(code).url + "#templates").flashing("success" -> activity.toString)
              }
            }
          })
      }.getOrElse(NotFound)
  }

  /**
   * Get a picture of a template
   * @param id Unique template identifier
   * @param single Type of template to return: true - for single facilitator, false - for multiple facilitators
   * @return
   */
  def template(id: Long, single: Boolean) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒
      val contentType = "image/jpeg"

      CertificateTemplate.find(id).map { template ⇒
        if (single) {
          Ok(template.oneFacilitator).as(contentType)
        } else {
          Ok(template.twoFacilitators).as(contentType)
        }
      }.getOrElse(Ok(Array[Byte]()).as(contentType))
  }

  /**
   * Delete a certificate template
   *
   * @param id Unique template identifier
   * @return
   */
  def delete(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      CertificateTemplate.find(id).map { template ⇒
        template.delete()
        val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, "certificate template")
        Redirect(routes.Brands.details(template.brandCode).url + "#templates").flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }
}
