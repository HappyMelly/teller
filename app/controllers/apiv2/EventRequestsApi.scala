package controllers.apiv2

import models.event.EventRequest
import models.service.Services
import models.{APIError, DateStamp}
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import views.Countries

/**
 * API for adding event requests
 */
trait EventRequestsApi extends ApiAuthentication with Services {

  def form(brandId: Long, appName: String) = Form(mapping(
    "country" -> nonEmptyText.verifying(
      "error.unknown_country",
      (country: String) ⇒ Countries.all.exists(_._1 == country)),
    "city" -> optional(nonEmptyText),
    "language" -> nonEmptyText,
    "start_date" -> optional(jodaLocalDate),
    "end_date" -> optional(jodaLocalDate),
    "number_of_participants" -> number,
    "comment" -> optional(text),
    "name" -> nonEmptyText,
    "email" -> email)({
    (country, city, language, start, end, participantsNumber, comment, name,
      email) => EventRequest(None, brandId, country, city, language, start,
      end, participantsNumber, comment, name, email,
      recordInfo = DateStamp(DateTime.now(), appName, DateTime.now(), appName))
  })({
    (r: EventRequest) => Some((r.countryCode, r.city, r.language, r.start,
      r.end, r.participantsNumber, r.comment, r.name, r.email))
  }))

  /**
   * Create an event request through API call
   * @param brandCode Brand string identifier
   */
  def create(brandCode: String) = TokenSecuredAction(readWrite = true) { implicit request ⇒
    implicit token ⇒
      val name = token.appName

      brandService.find(brandCode) map { brand =>
        val requestData = form(brand.identifier, name).bindFromRequest()
        requestData.fold(
          erroneousData => {
            val json = Json.toJson(APIError.formValidationError(erroneousData.errors))
            BadRequest(Json.prettyPrint(json))
          },
          eventRequest => {
            val requestId = eventRequestService.insert(eventRequest).id
            jsonOk(Json.obj("request_id" -> requestId))
          }
        )
      } getOrElse jsonNotFound(s"Brand $brandCode not found")
  }
}

object EventRequestsApi extends EventRequestsApi
