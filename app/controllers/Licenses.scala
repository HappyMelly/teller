package controllers

import play.api.mvc.{ Action, Controller }
import securesocial.core.SecureSocial
import play.api.libs.json._
import models.Person
import org.joda.time.LocalDate
import scala.util.{ Failure, Success, Try }

/**
 * Content license API.
 */
object Licenses extends Controller with ApiAuthentication {

  implicit def licenseeWrites = new Writes[Person] {
    def writes(licensee: Person): JsValue = {
      Json.obj(
        "first_name" -> licensee.firstName,
        "last_name" -> licensee.lastName,
        "country" -> licensee.address.countryCode)
    }
  }

  /**
   * Returns a list of licensees for the given brand on the given date.
   */
  def licensees(brandCode: String, dateString: Option[String]) = TokenSecuredAction { implicit request ⇒
    if (models.Brand.exists(brandCode)) {
      val date = Try(dateString.map(d ⇒ new LocalDate(d)).getOrElse(LocalDate.now()))
      date match {
        case Success(d) ⇒ Ok(Json.toJson(models.License.licensees(brandCode, d)))
        case Failure(e) ⇒ BadRequest("Invalid date")
      }

    } else NotFound
  }
}
