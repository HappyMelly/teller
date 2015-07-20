/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional terms,
 * you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers

import models.ActiveUser
import models.brand.BrandTestimonial
import models.service.Services
import models.UserRole.DynamicRole
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json.{ JsValue, Writes, Json }
import securesocial.core.RuntimeEnvironment

case class TestimonialFormData(content: String,
  name: String,
  company: Option[String])

class BrandTestimonials(environment: RuntimeEnvironment[ActiveUser])
    extends JsonController
    with Services
    with Security {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  implicit val brandTestimonialWrites = new Writes[BrandTestimonial] {
    def writes(testimonial: BrandTestimonial): JsValue = {
      Json.obj(
        "brandId" -> testimonial.brand,
        "content" -> testimonial.content,
        "name" -> testimonial.name,
        "company" -> testimonial.company,
        "id" -> testimonial.id.get)
    }
  }

  val form = Form(mapping(
    "content" -> nonEmptyText,
    "name" -> nonEmptyText,
    "company" -> optional(nonEmptyText))(TestimonialFormData.apply)(TestimonialFormData.unapply))

  def add(brandId: Long) = SecuredDynamicAction("brand", DynamicRole.Coordinator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        brandService.find(brandId) map { brand ⇒
          Ok(views.html.testimonial.form(user, brandId, form))
        } getOrElse NotFound(Messages("error.brand.notFound"))
  }

  def edit(brandId: Long, id: Long) = SecuredDynamicAction("brand", DynamicRole.Coordinator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        brandService.find(brandId) map { brand ⇒
          brandService.findTestimonial(id) map { testimonial ⇒
            val formData = TestimonialFormData(testimonial.content,
              testimonial.name, testimonial.company)
            Ok(views.html.testimonial.form(user, brandId, form.fill(formData), Some(id)))
          } getOrElse NotFound("Testimonial is not found")
        } getOrElse NotFound(Messages("error.brand.notFound"))
  }

  /**
   * Adds new brand testimonial if the testimonial is valid
   *
   * @param brandId Brand identifier
   */
  def create(brandId: Long) = SecuredDynamicAction("brand", DynamicRole.Coordinator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        brandService.find(brandId) map { brand ⇒
          form.bindFromRequest.fold(
            error ⇒ BadRequest(views.html.testimonial.form(user, brandId, error)),
            testimonialData ⇒ {
              val testimonial = BrandTestimonial(None, brandId,
                testimonialData.content, testimonialData.name,
                testimonialData.company)
              val testimonialWithId = brandService.insertTestimonial(testimonial)
              val url = routes.Brands.details(brandId).url + "#testimonials"
              Redirect(url)
            })
        } getOrElse NotFound(Messages("error.brand.notFound"))
  }

  /**
   * Deletes the given brand testimonial if the testimonial exists and is belonged to the given
   * brand
   *
   * Brand identifier is used to check access rights
   *
   * @param brandId Brand identifier
   * @param id Testimonial identifier
   */
  def remove(brandId: Long, id: Long) = SecuredDynamicAction("brand", DynamicRole.Coordinator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        brandService.deleteTestimonial(brandId, id)
        jsonSuccess("ok")
  }

  /**
   * Updates the given brand testimonial if it's valid
   *
   * @param brandId Brand identifier
   * @param id Testimonial identifier
   */
  def update(brandId: Long, id: Long) = SecuredDynamicAction("brand", DynamicRole.Coordinator) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        form.bindFromRequest.fold(
          error ⇒ BadRequest(views.html.testimonial.form(user, brandId, error)),
          testimonialData ⇒ {
            val testimonial = BrandTestimonial(Some(id), brandId,
              testimonialData.content, testimonialData.name,
              testimonialData.company)
            brandService.updateTestimonial(testimonial)
            val url = routes.Brands.details(brandId).url + "#testimonials"
            Redirect(url)
          })
  }
}
