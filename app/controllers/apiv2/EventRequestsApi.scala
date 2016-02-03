package controllers.apiv2

import javax.inject.Inject

import models.event.EventRequest
import models.service.Services
import models.{APIError, DateStamp}
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.libs.json._
import views.Countries

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * API for adding event requests
 */
class EventRequestsApi @Inject() (val messagesApi: MessagesApi) extends ApiAuthentication with Services {

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
    *
    * @param brandCode Brand string identifier
   */
  def create(brandCode: String) = TokenSecuredAction(readWrite = true) { implicit request ⇒ implicit token ⇒
    val name = token.appName

    brandService.find(brandCode) flatMap {
      case None => jsonNotFound(s"Brand $brandCode not found")
      case Some(brand) =>
        val requestData = form(brand.identifier, name).bindFromRequest()
        requestData.fold(
          erroneousData => {
            val json = Json.toJson(APIError.formValidationError(erroneousData.errors))
            badRequest(Json.prettyPrint(json))
          },
          eventRequest =>
            eventRequestService.insert(eventRequest) flatMap { value =>
              jsonOk(Json.obj("request_id" -> value.id))
            }
        )
    }
  }
}

