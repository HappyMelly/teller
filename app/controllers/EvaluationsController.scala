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
import models.service.Services
import play.api.data.FormError
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.mvc.Controller

trait EvaluationsController extends JsonController with Services {

  /**
   * Formatter used to define a form mapping for the `EvaluationStatus` enumeration.
   */
  implicit def statusFormat: Formatter[EvaluationStatus.Value] = new Formatter[EvaluationStatus.Value] {

    def bind(key: String, data: Map[String, String]) = {
      try {
        data.get(key).map(EvaluationStatus.withName) match {
          case Some(status) ⇒ Right(status)
          case None ⇒ Right(EvaluationStatus.Pending)
        }
      } catch {
        case e: NoSuchElementException ⇒ Left(List(FormError(key, "error.authorisation")))
      }
    }

    def unbind(key: String, value: EvaluationStatus.Value) = Map(key -> value.toString)
  }

  val statusMapping = of[EvaluationStatus.Value]

}
