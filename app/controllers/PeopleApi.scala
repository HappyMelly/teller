package controllers

import play.mvc.Controller
import models._
import play.api.libs.json._
import models.LicenseView

object PeopleApi extends Controller with ApiAuthentication {

  implicit val personWrites = new Writes[Person] {
    def writes(person: Person): JsValue = {
      Json.obj(
        "href" -> routes.PeopleApi.person(person.id.get).url,
        "first_name" -> person.firstName,
        "last_name" -> person.lastName,
        "country" -> person.address.countryCode)
    }
  }

  import OrganisationsApi.organisationWrites

  implicit val licenseSummaryWrites = new Writes[LicenseView] {
    def writes(license: LicenseView) = {
      Json.obj(
        "brand" -> license.brand.code,
        "start" -> license.license.start,
        "end" -> license.license.end)
    }
  }

  implicit val addressWrites = new Writes[Address] {
    def writes(address: Address) = Json.obj(
      "street1" -> address.street1,
      "street2" -> address.street2,
      "city" -> address.city,
      "post_code" -> address.postCode,
      "province" -> address.province,
      "country" -> address.countryCode)
  }

  val personDetailsWrites = new Writes[Person] {
    def writes(person: Person) = {
      Json.obj(
        "first_name" -> person.firstName,
        "last_name" -> person.lastName,
        "email_address" -> person.emailAddress,
        "address" -> person.address,
        "stakeholder" -> person.stakeholder,
        "board_member" -> person.boardMember,
        "bio" -> person.bio,
        "interests" -> person.interests,
        "twitter_handle" -> person.twitterHandle,
        "facebook_url" -> person.facebookUrl,
        "linkedin_url" -> person.linkedInUrl,
        "google_plus_url" -> person.googlePlusUrl,
        "organizations" -> Json.toJson(person.memberships),
        "licenses" -> Json.toJson(person.id.map { personId ⇒
          License.licenses(personId)
        }),
        "active" -> person.active,
        "created" -> person.created.toString(),
        "createdBy" -> person.createdBy,
        "updated" -> person.updated.toString(),
        "updatedBy" -> person.updatedBy,
        "organizations" -> person.memberships,
        "licenses" -> person.licenses)
    }
  }

  def people(stakeholdersOnly: Option[Boolean], boardmembersOnly: Option[Boolean]) = TokenSecuredAction { implicit request ⇒
    val people: List[Person] = Person.findActive(stakeholdersOnly.getOrElse(false), boardmembersOnly.getOrElse(false))
    Ok(Json.toJson(people))
  }

  def person(id: Long) = TokenSecuredAction { implicit request ⇒
    val person = Person.find(id)
    person.map(person ⇒ Ok(Json.toJson(person)(personDetailsWrites))).getOrElse(NotFound)
  }

}
