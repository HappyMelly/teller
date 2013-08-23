package controllers

import org.joda.time.LocalDate
import play.api.mvc.Controller
import play.api.libs.json.{ JsValue, Writes, Json }
import models.{ License, Brand, Person }
import scala.util.{ Failure, Success, Try }

/**
 * Content licenses API.
 */
object LicensesApi extends Controller with ApiAuthentication {

  case class LicenseeView(brandCode: String, licensee: Person)

  implicit def licenseeWrites = new Writes[LicenseeView] {
    def writes(view: LicenseeView): JsValue = {
      Json.obj(
        "href" -> view.licensee.id.map(licenseeId ⇒ routes.LicensesApi.licensee(licenseeId, view.brandCode).url),
        "first_name" -> view.licensee.firstName,
        "last_name" -> view.licensee.lastName,
        "country" -> view.licensee.address.countryCode)
    }
  }

  case class LicensedSinceView(licensee: Person, since: Option[LocalDate])

  implicit def licenseeDetailsWrites = new Writes[LicensedSinceView] {
    def writes(view: LicensedSinceView): JsValue = {
      Json.obj(
        "first_name" -> view.licensee.firstName,
        "last_name" -> view.licensee.lastName,
        "country" -> view.licensee.address.countryCode,
        "email_address" -> view.licensee.emailAddress,
        "twitter_handle" -> view.licensee.twitterHandle,
        "facebook_url" -> view.licensee.facebookUrl,
        "linked_in_url" -> view.licensee.linkedInUrl,
        "google_plus_url" -> view.licensee.googlePlusUrl,
        "bio_markdown" -> view.licensee.bio,
        "interests" -> view.licensee.interests,
        "valid" -> view.since.isDefined,
        "licensed_since" -> view.since)
    }
  }

  /**
   * API that returns a list of licensees for the given brand on the given date.
   */
  def licensees(brandCode: String, dateString: Option[String]) = TokenSecuredAction { implicit request ⇒
    if (Brand.exists(brandCode)) {
      val date = Try(dateString.map(d ⇒ new LocalDate(d)).getOrElse(LocalDate.now()))
      date match {
        case Success(d) ⇒ {
          val licensees = License.licensees(brandCode, d).map(licensee ⇒ LicenseeView(brandCode, licensee))
          Ok(Json.toJson(licensees))
        }
        case Failure(e) ⇒ BadRequest("Invalid date")
      }

    } else NotFound("Unknown brand")
  }

  /**
   * API that returns details of a licensee for a brand.
   */
  def licensee(licenseeId: Long, brandCode: String) = TokenSecuredAction { implicit request ⇒
    Person.find(licenseeId).map { person ⇒
      if (Brand.exists(brandCode)) {
        val view = LicensedSinceView(person, License.licensedSince(licenseeId, brandCode))
        Ok(Json.toJson(view))
      } else NotFound("Unknown brand")
    }.getOrElse(NotFound("Unknown licensee"))
  }
}