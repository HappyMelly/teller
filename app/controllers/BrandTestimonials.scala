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

import javax.inject.Inject

import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import be.objectify.deadbolt.scala.cache.HandlerCache
import models.cm.brand.BrandTestimonial
import models.repository.Repositories
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{MessagesApi, I18nSupport, Messages}
import play.api.libs.json.{JsValue, Json, Writes}
import services.TellerRuntimeEnvironment

case class TestimonialFormData(content: String,
  name: String,
  company: Option[String])

class BrandTestimonials @Inject() (override implicit val env: TellerRuntimeEnvironment,
                                   override val messagesApi: MessagesApi,
                                   val services: Repositories,
                                   deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, services)(messagesApi, env) {

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

  def add(brandId: Long) = BrandAction(brandId) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    services.cm.brand.find(brandId) flatMap {
      case None => notFound(Messages("error.brand.notFound"))
      case Some(brand) ⇒ ok(views.html.v2.testimonial.form(user, brandId, form))
    }
  }

  def edit(brandId: Long, id: Long) = BrandAction(brandId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      (for {
        brand <- services.cm.brand.find(brandId)
        testimonial <- services.cm.brand.findTestimonial(id)
      } yield (brand, testimonial)) flatMap {
        case (None, _) => notFound(Messages("error.brand.notFound"))
        case (_, None) => notFound("Testimonial not found")
        case (Some(brand), Some(testimonial)) =>
          val formData = TestimonialFormData(testimonial.content, testimonial.name, testimonial.company)
          ok(views.html.v2.testimonial.form(user, brandId, form.fill(formData), Some(id)))
      }
  }

  /**
   * Adds new brand testimonial if the testimonial is valid
   *
   * @param brandId Brand identifier
   */
  def create(brandId: Long) = BrandAction(brandId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      services.cm.brand.find(brandId) flatMap {
        case None => notFound(Messages("error.brand.notFound"))
        case Some(brand) =>
          form.bindFromRequest.fold(
            error ⇒ badRequest(views.html.v2.testimonial.form(user, brandId, error)),
            testimonialData ⇒ {
              val testimonial = BrandTestimonial(None, brandId,
                testimonialData.content, testimonialData.name,
                testimonialData.company)
              services.cm.brand.insertTestimonial(testimonial) flatMap { _ =>
                redirect(routes.Brands.details(brandId).url + "#testimonials")
              }
            })
      }
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
  def remove(brandId: Long, id: Long) = BrandAction(brandId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      services.cm.brand.deleteTestimonial(brandId, id) flatMap { _ =>
        jsonSuccess("Testimonial was successfully removed")
      }
  }

  /**
   * Updates the given brand testimonial if it's valid
   *
   * @param brandId Brand identifier
   * @param id Testimonial identifier
   */
  def update(brandId: Long, id: Long) = BrandAction(brandId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      form.bindFromRequest.fold(
        error ⇒ badRequest(views.html.v2.testimonial.form(user, brandId, error)),
        testimonialData ⇒ {
          val testimonial = BrandTestimonial(Some(id), brandId, testimonialData.content, testimonialData.name,
            testimonialData.company)
          services.cm.brand.updateTestimonial(testimonial) flatMap { _ =>
            redirect(routes.Brands.details(brandId).url + "#testimonials")
          }
        })
  }
}
