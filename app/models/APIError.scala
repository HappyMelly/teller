package models

import play.api.data.FormError
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Represents an API error
 *
 * @param code Error code
 * @param message Help message
 * @param field A field with the error
 * @param subErrors A set of errors for the children (example: a validation error and the errors for each form field)
 */
case class APIError(code: Int,
  message: String,
  field: Option[String] = None,
  subErrors: Option[Seq[APIError]] = None) {
}

object APIError {

  implicit lazy val apiErrorWrites: Writes[APIError] = (
    (__ \ "code").write[Int] and
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

  def unknownError(field: String, message: String) = new APIError(1000, message, Some(field))

  /**
   * @param field A field with the error
   * @return APIError
   */
  def requiredFieldMissingError(field: String) = new APIError(1001, "error.api.required", Some(field))

  /**
   * @param subErrors A set of errors for the children
   * @return APIError
   */
  def validationError(subErrors: Seq[APIError]) = new APIError(1002, "error.api.validation", None, Some(subErrors))

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