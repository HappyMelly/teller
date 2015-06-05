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

import models.brand.BrandTestimonial
import models.service.Services
import models.UserRole.DynamicRole
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages

trait BrandTestimonials extends JsonController with Services with Security {

  case class TestimonialFormData(content: String,
    name: String,
    company: Option[String])

  val form = Form(mapping(
    "content" -> nonEmptyText,
    "name" -> nonEmptyText,
    "company" -> optional(nonEmptyText)) (TestimonialFormData.apply)(TestimonialFormData.unapply))

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
            error ⇒ jsonBadRequest(error.errorsAsJson),
            testimonialData ⇒ {
              val testimonial = BrandTestimonial(None, brandId,
                testimonialData.content, testimonialData.name,
                testimonialData.company)
              brandService.insertTestimonial(testimonial)
              jsonSuccess("ok")
            })
        } getOrElse jsonNotFound(Messages("error.brand.notFound"))
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
          error ⇒ jsonBadRequest(error.errorsAsJson),
          testimonialData ⇒ {
            val testimonial = BrandTestimonial(Some(id), brandId,
              testimonialData.content, testimonialData.name,
              testimonialData.company)
            brandService.updateTestimonial(testimonial)
            jsonSuccess("ok")
          })
  }
}

object BrandTestimonials extends BrandTestimonials