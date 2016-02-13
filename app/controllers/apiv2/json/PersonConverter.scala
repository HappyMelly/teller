package controllers.apiv2.json

import controllers.apiv2.OrganisationsApi
import models._
import play.api.i18n.Messages
import play.api.libs.json.{JsValue, Json, Writes}

/**
  * Converts a person to JSON
  */
class PersonConverter(implicit val messages: Messages) {

  val personWrites = new Writes[Person] {
    def writes(person: Person): JsValue = {
      Json.obj(
        "id" -> person.id,
        "unique_name" -> person.uniqueName,
        "first_name" -> person.firstName,
        "last_name" -> person.lastName,
        "photo" -> person.photo.url,
        "country" -> person.address.countryCode)
    }
  }

  import OrganisationsApi.organisationWrites
  implicit val contributionWrites = (new ContributionConverter).contributionWrites

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

  val personDetailsWrites = new Writes[(Person, List[LicenseView], List[ContributionView], List[Organisation])] {
    def writes(view: (Person, List[LicenseView], List[ContributionView], List[Organisation])) = {
      Json.obj(
        "id" -> view._1.id.get,
        "unique_name" -> view._1.uniqueName,
        "first_name" -> view._1.firstName,
        "last_name" -> view._1.lastName,
        "email_address" -> view._1.email,
        "image" -> view._1.photo.url,
        "address" -> view._1.address,
        "bio" -> view._1.bio,
        "interests" -> view._1.interests,
        "twitter_handle" -> view._1.profile.twitterHandle,
        "facebook_url" -> view._1.profile.facebookUrl,
        "linkedin_url" -> view._1.profile.linkedInUrl,
        "google_plus_url" -> view._1.profile.googlePlusUrl,
        "website" -> view._1.webSite,
        "blog" -> view._1.blog,
        "active" -> view._1.active,
        "organizations" -> view._4,
        "licenses" -> view._2,
        "contributions" -> view._3)
    }
  }
}
