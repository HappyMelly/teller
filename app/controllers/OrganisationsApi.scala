package controllers

import play.api.mvc.Controller
import play.api.libs.json._
import models.Organisation

/**
 * Organisations API.
 */
object OrganisationsApi extends Controller with ApiAuthentication {

  implicit val organisationListWrites = new Writes[Organisation] {
    def writes(organisation: Organisation): JsValue = {
      Json.obj(
        // "href" -> organisation.id.map(organisationId ⇒ routes.OrganisationsApi.licensee(organisationId).url),
        "name" -> organisation.name,
        "city" -> organisation.city,
        "country" -> organisation.countryCode)
    }
  }

  def organisations = TokenSecuredAction { implicit request ⇒
    Ok(Json.toJson(Organisation.findLegalEntities))
  }
}