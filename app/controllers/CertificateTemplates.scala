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

import models.ActiveUser
import models.UserRole.Role._
import models.brand.CertificateTemplate
import models.service.Services
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import securesocial.core.RuntimeEnvironment
import scala.io.Source
import views.Languages

/**
 * This class exists to simplify form handling and generation
 */
case class FakeCertificateTemplate(language: String,
  template: Option[String],
  templateNoFacilitator: Option[String])

class CertificateTemplates(environment: RuntimeEnvironment[ActiveUser])
    extends Controller
    with Security
    with Services
    with Activities {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  val encoding = "ISO-8859-1"

  /** HTML form mapping for creating certificate templates */
  def certificateFileForm = Form(mapping(
    "language" -> nonEmptyText,
    "oneFacilitator" -> optional(text),
    "twoFacilitators" -> optional(text))(
      FakeCertificateTemplate.apply)(FakeCertificateTemplate.unapply))

  /**
   * Renders an Add form
   *
   * @todo change access rights to all brand managers
   * @param brandId Unique text brand identifier
   */
  def add(brandId: Long) = SecuredBrandAction(brandId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      brandService.find(brandId) map { brand ⇒
        val templates = certificateService.findByBrand(brandId)
        val languages = Languages.all.filter(lang ⇒ templates.find(_.language == lang._1).isEmpty)
        Ok(views.html.certificateTemplate.form(user, brand, languages, certificateFileForm))
      } getOrElse NotFound
  }

  /**
   * Adds new certificate for the given brand
   *
   * @param brandId Unique brand identifier
   */
  def create(brandId: Long) = SecuredBrandAction(brandId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      brandService.find(brandId) map { brand ⇒
        val templates = certificateService.findByBrand(brandId)
        val languages = Languages.all.filter(lang ⇒ templates.find(_.language == lang._1).isEmpty)
        val form: Form[FakeCertificateTemplate] = certificateFileForm.bindFromRequest
        form.fold(
          formWithErrors ⇒ BadRequest(views.html.certificateTemplate.form(user,
            brand,
            languages,
            formWithErrors)),
          data ⇒ {
            templates.find(_.language == data.language).map { v ⇒
              BadRequest(views.html.certificateTemplate.form(user,
                brand,
                languages,
                form.withError("language", "error.template.exist")))
            }.getOrElse {
              val template = request.body.asMultipartFormData.get.file("oneFacilitator")
              val templateOneFacilitator = request.body.asMultipartFormData.get.file("twoFacilitators")
              val validMimeTypes = List("image/jpeg", "image/pjpeg", "image/gif", "image/png")
              if (template.isEmpty || templateOneFacilitator.isEmpty
                || !validMimeTypes.contains(template.get.contentType.getOrElse(""))
                || !validMimeTypes.contains(templateOneFacilitator.get.contentType.getOrElse(""))) {
                BadRequest(views.html.certificateTemplate.form(user,
                  brand,
                  languages,
                  form.withError(
                    "oneFacilitator",
                    "error.required").withError(
                      "twoFacilitators",
                      "error.required")))
              } else {
                val firstSource = Source.fromFile(template.get.ref.file.getPath, encoding)
                val secondSource = Source.fromFile(templateOneFacilitator.get.ref.file.getPath, encoding)
                val tpl = new CertificateTemplate(None,
                  brandId,
                  data.language,
                  firstSource.toArray.map(_.toByte),
                  secondSource.toArray.map(_.toByte)).insert
                firstSource.close()
                secondSource.close()
                val log = activity(tpl, user.person).created.insert()
                Redirect(routes.Brands.details(brandId).url + "#templates").flashing(
                  "success" -> log.toString)
              }
            }
          })
      } getOrElse NotFound
  }

  /**
   * Get a picture of a template
   * @param id Unique template identifier
   * @param single Type of template to return: true - for single facilitator, false - for multiple facilitators
   * @return
   */
  def template(id: Long, single: Boolean) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
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
   * Deletes a certificate template
   *
   * @param id Unique template identifier
   */
  def delete(id: Long) = SecuredBrandAction(id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      CertificateTemplate.find(id).map { tpl ⇒
        tpl.delete()
        val log = activity(tpl, user.person).deleted.insert()
        Redirect(routes.Brands.details(tpl.brandId).url + "#templates").flashing(
          "success" -> log.toString)
      }.getOrElse(NotFound)
  }
}
