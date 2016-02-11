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

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import models.UserRole.Role._
import models.brand.CertificateTemplate
import models.service.Services
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{MessagesApi, I18nSupport}
import services.TellerRuntimeEnvironment
import views.Languages

import scala.concurrent.Future
import scala.io.Source

/**
 * This class exists to simplify form handling and generation
 */
case class FakeCertificateTemplate(language: String,
  template: Option[String],
  templateNoFacilitator: Option[String])

class CertificateTemplates @Inject() (override implicit val env: TellerRuntimeEnvironment,
                                      override val messagesApi: MessagesApi,
                                      val services: Services,
                                      deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, services)(messagesApi, env)
    with Activities
    with I18nSupport {

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
  def add(brandId: Long) = AsyncSecuredBrandAction(brandId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      (for {
        brand <- services.brandService.find(brandId)
        templates <- services.certificateService.findByBrand(brandId)
      } yield (brand, templates)) flatMap {
        case (None, _) => Future.successful(NotFound)
        case (Some(brand), templates) =>
          val languages = Languages.all.filter(lang ⇒ templates.find(_.language == lang._1).isEmpty)
          ok(views.html.v2.certificateTemplate.form(user, brand, languages, certificateFileForm))
      }
  }

  /**
   * Adds new certificate for the given brand
   *
   * @param brandId Unique brand identifier
   */
  def create(brandId: Long) = AsyncSecuredBrandAction(brandId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      (for {
        brand <- services.brandService.find(brandId)
        templates <- services.certificateService.findByBrand(brandId)
      } yield (brand, templates)) flatMap {
        case (None, _) => Future.successful(NotFound)
        case (Some(brand), templates) =>
          val languages = Languages.all.filter(lang ⇒ templates.find(_.language == lang._1).isEmpty)
          val form: Form[FakeCertificateTemplate] = certificateFileForm.bindFromRequest
          form.fold(
            errors ⇒ badRequest(views.html.v2.certificateTemplate.form(user, brand, languages, errors)),
            data ⇒ {
              templates.find(_.language == data.language).map { v ⇒
                badRequest(views.html.v2.certificateTemplate.form(user, brand, languages,
                  form.withError("language", "error.template.exist")))
              }.getOrElse {
                val template = request.body.asMultipartFormData.get.file("oneFacilitator")
                val templateOneFacilitator = request.body.asMultipartFormData.get.file("twoFacilitators")
                val validMimeTypes = List("image/jpeg", "image/pjpeg", "image/gif", "image/png")
                if (template.isEmpty || templateOneFacilitator.isEmpty
                  || !validMimeTypes.contains(template.get.contentType.getOrElse(""))
                  || !validMimeTypes.contains(templateOneFacilitator.get.contentType.getOrElse(""))) {
                  badRequest(views.html.v2.certificateTemplate.form(user,
                    brand,
                    languages,
                    form.withError("oneFacilitator","error.required").withError("twoFacilitators","error.required")))
                } else {
                  val firstSource = Source.fromFile(template.get.ref.file.getPath, encoding)
                  val secondSource = Source.fromFile(templateOneFacilitator.get.ref.file.getPath, encoding)
                  val tpl = new CertificateTemplate(None,
                    brandId,
                    data.language,
                    firstSource.toArray.map(_.toByte),
                    secondSource.toArray.map(_.toByte))
                  firstSource.close()
                  secondSource.close()
                  services.certificateService.insert(tpl) flatMap { _ =>
                    redirect(routes.Brands.details(brandId).url + "#templates", "success" -> "Template was added")
                  }
                }
              }
            })
      }
  }

  /**
   * Get a picture of a template
    *
    * @param id Unique template identifier
   * @param single Type of template to return: true - for single facilitator, false - for multiple facilitators
   * @return
   */
  def template(id: Long, single: Boolean) = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val contentType = "image/jpeg"
      services.certificateService.find(id) flatMap {
        case None => Future.successful(Ok(Array[Byte]()).as(contentType))
        case Some(template) =>
          if (single) {
            Future.successful(Ok(template.oneFacilitator).as(contentType))
          } else {
            Future.successful(Ok(template.twoFacilitators).as(contentType))
          }
      }
  }

  /**
   * Deletes a certificate template
   *
   * @param brandId Brand identifier
   * @param id Unique template identifier
   */
  def delete(brandId: Long, id: Long) = AsyncSecuredBrandAction(brandId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      services.certificateService.find(id) flatMap {
        case None => notFound("Template not found")
        case Some(template) =>
          services.certificateService.delete(id) flatMap { _ =>
            redirect(routes.Brands.details(template.brandId).url + "#templates", "success" -> "Template was deleted")
          }
      }
  }
}
