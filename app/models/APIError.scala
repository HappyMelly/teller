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
package models

import play.api.data.FormError
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.libs.functional.syntax._
import utils.EnumUtils

/**
 * Represents an API error
 *
 * @param code Error code
 * @param message Help message
 * @param field A field with the error
 * @param subErrors A set of errors for the children (example: a validation error and the errors for each form field)
 */
class APIError(val code: ErrorCode.Value,
    val message: String,
    val field: Option[String] = None,
    val subErrors: Option[Seq[APIError]] = None) {
}

object ErrorCode extends Enumeration {
  type ErrorCode = Value

  val UnknownError = Value(1000)
  val RequiredFieldMissingError = Value(1001)
  val ValidationError = Value(1002)
  val ObjectNotExistError = Value(1003)
  val DuplicateObjectError = Value(1004)

  implicit val enumReads: Reads[ErrorCode] = EnumUtils.enumReads(ErrorCode)

  implicit def enumWrites: Writes[ErrorCode] = EnumUtils.enumWrites
}

object APIError {

  def unapply(e: APIError) = {
    new Some(e.code, Messages(e.message), e.field, e.subErrors)
  }

  implicit lazy val apiErrorWrites: Writes[APIError] = (
    (__ \ "code").write[ErrorCode.Value] and
    (__ \ "message").write[String] and
    (__ \ "field").write[Option[String]] and
    (__ \ "errors").lazyWriteNullable(Writes.seq[APIError](apiErrorWrites)))(unlift(APIError.unapply))

  /**
   * Create an APIError object based on type of the error
   * @param identifier Error identifier for look-up
   * @param field A field with the error
   * @return APIError
   */
  def factory(identifier: String, field: String): APIError = {
    identifier match {
      case "error.required" ⇒ requiredFieldMissingError(field)
      case _ ⇒ unknownError(field, identifier)
    }
  }

  def unknownError(field: String, message: String) = new APIError(ErrorCode.UnknownError, message, Some(field))

  /**
   * @param field A field with the error
   * @return APIError
   */
  def requiredFieldMissingError(field: String) = new APIError(ErrorCode.RequiredFieldMissingError, "error.api.required", Some(field))

  /**
   * @param subErrors A set of errors for the children
   * @return APIError
   */
  def validationError(subErrors: Seq[APIError]) = new APIError(ErrorCode.ValidationError, "error.api.validation", None, Some(subErrors))

  /**
   * Create a validation error object from FormError sequence
   * @param subErrors A set of errors for the children
   * @return APIError
   */
  def formValidationError(subErrors: Seq[FormError]): APIError = {
    val errors: Seq[APIError] = subErrors.map(v ⇒ factory(v.message, v.key))
    validationError(errors)
  }
}