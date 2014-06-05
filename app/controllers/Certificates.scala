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

import fly.play.s3.{ BucketFile, S3Exception }
import models.{ LoginIdentity, Brand, Certificate, Evaluation }
import models.UserRole.Role._
import play.api.mvc._
import play.api.cache.Cache
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import scala.concurrent.Future
import scala.Some
import services.S3Bucket
import securesocial.core.SecuredRequest

object Certificates extends Controller with Security {

  /** Generate new certificate **/
  def create(id: Long) = SecuredDynamicAction("evaluation", "manage") { implicit request ⇒
    implicit handler ⇒

      Evaluation.find(id).map {
        evaluation ⇒
          val approver = request.user.asInstanceOf[LoginIdentity].userAccount.person.get
          val brand = Brand.find(evaluation.event.brandCode).get
          val certificate = new Certificate(evaluation)
          certificate.generateAndSend(brand, approver)
          Redirect(routes.Participants.index).flashing("success" -> "Certificate was generated")
      }.getOrElse(NotFound)
  }

  /**
   * Retrieve and cache a certificate
   *
   * Attention: `id` could be `id.pdf`. It's a dirty hack to make Apache Common
   *       handle urls correctly (it doesn't understand urls without extension)
   *       See 'EmailService.scala' for additional info
   */
  def certificate(id: String) = Action.async {
    val certificateId = ("""\d+""".r findFirstIn id).get

    val contentType = "application/pdf"
    val empty = Array[Byte]()
    val cached = Cache.getAs[Array[Byte]](Certificate.cacheId(certificateId))
    if (cached.isDefined) {
      Future.successful(Ok(cached.get).as(contentType))
    } else {
      val result = S3Bucket.get(Certificate.fullFileName(certificateId))
      val pdf: Future[Array[Byte]] = result.map {
        case BucketFile(name, contentType, content, acl, headers) ⇒ content
      }.recover {
        case S3Exception(status, code, message, originalXml) ⇒ empty
      }
      pdf.map {
        case value ⇒
          Cache.set(Certificate.cacheId(certificateId), value)
          Ok(value).as(contentType)
      }
    }
  }

}
