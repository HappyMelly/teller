package controllers

import models._
import models.JodaMoney.jodaMoney
import org.joda.time.LocalDate
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc.Controller
import scala.util.{ Failure, Success, Try }
import securesocial.core.SecureSocial

/**
 * Content license pages and API.
 */
object Licenses extends Controller with ApiAuthentication with SecureSocial {

  /**
   * HTML form mapping for creating and editing.
   * TODO Validate licensee ID and brand ID
   */
  val licenseForm = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "licenseeId" -> ignored(0L),
    "brandId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString),
    "version" -> nonEmptyText,
    "signed" -> jodaLocalDate,
    "start" -> jodaLocalDate,
    "end" -> jodaLocalDate,
    "confirmed" -> default(boolean, false),
    "fee" -> jodaMoney(13, 0), // Set scale to zero to force whole numbers.
    "feePaid" -> optional(jodaMoney()))(License.apply)(License.unapply).verifying(
      "error.date.range", (license: License) ⇒ !license.start.isAfter(license.end)))

  implicit def licenseeWrites = new Writes[Person] {
    def writes(licensee: Person): JsValue = {
      Json.obj(
        "first_name" -> licensee.firstName,
        "last_name" -> licensee.lastName,
        "country" -> licensee.address.countryCode)
    }
  }

  /**
   * Page for adding a new content license.
   */
  def add(personId: Long) = SecuredAction { implicit request ⇒
    Person.find(personId).map { person ⇒
      val form = licenseForm.fill(License.blank(personId))
      Ok(views.html.license.form(request.user, form, person))
    } getOrElse {
      Redirect(routes.People.index).flashing("error" -> Messages("error.notFound", Messages("models.Person")))
    }
  }

  /**
   * POST handler for adding a new content license.
   */
  def create(personId: Long) = SecuredAction { implicit request ⇒
    Person.find(personId).map { person ⇒
      licenseForm.bindFromRequest.fold(
        form ⇒ BadRequest(views.html.license.form(request.user, form, person)),
        license ⇒ {
          val newLicense = License.insert(license.copy(licenseeId = personId))
          val brand = Brand.find(newLicense.brandId).get

          val activityObject = Messages("models.License", brand.name)
          Activity.insert(request.user.fullName, Activity.Predicate.Created, activityObject)
          val message = Messages("success.insert.license", activityObject)
          Redirect(routes.People.details(personId)).flashing("success" -> message)
        })
    } getOrElse {
      Redirect(routes.People.details(personId)).flashing("error" -> Messages("error.notFound", Messages("models.Person")))
    }
  }

  /**
   * API that returns a list of licensees for the given brand on the given date.
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
