package controllers

import play.api.mvc.Controller
import play.api.libs.json._
import models.{ Address, Organisation }

/**
 * Organisations API.
 */
object OrganisationsApi extends Controller with ApiAuthentication {

  implicit val organisationWrites = new Writes[Organisation] {
    def writes(organisation: Organisation): JsValue = {
      Json.obj(
        "href" -> organisation.id.map(organisationId ⇒ routes.OrganisationsApi.organisation(organisationId).url),
        "name" -> organisation.name,
        "city" -> organisation.city,
        "country" -> organisation.countryCode)
    }
  }

  import PeopleApi.personSummaryWrites

  val organisationDetailsWrites = new Writes[Organisation] {
    def writes(organisation: Organisation): JsValue = {
      val address = Address(None, organisation.street1, organisation.street2, organisation.city, organisation.province,
        organisation.postCode, organisation.countryCode)
      import PeopleApi.addressWrites

      Json.obj(
        "name" -> organisation.name,
        "address" -> Json.toJson(address),
        "vat_number" -> organisation.vatNumber,
        "registration_number" -> organisation.registrationNumber,
        "legal_entity" -> organisation.legalEntity,
        "members" -> organisation.members)
    }
  }

  /**
   * Organisation details API.
   */
  def organisation(id: Long) = TokenSecuredAction { implicit request ⇒
    Organisation.find(id).map { organisation ⇒
      Ok(Json.toJson(organisation)(organisationDetailsWrites))
    }.getOrElse(NotFound("Unknown organization"))
  }

  /**
   * Organisation list API.
   */
  def organisations(legalEntitiesOnly: Option[Boolean]) = TokenSecuredAction { implicit request ⇒
    Ok(Json.toJson(Organisation.find(legalEntitiesOnly.getOrElse(false))))
  }
}