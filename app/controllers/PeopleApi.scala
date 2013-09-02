package controllers

import play.mvc.Controller
import models.Person
import play.api.libs.json._

object PeopleApi extends Controller with ApiAuthentication {

  implicit val personWrites = new Writes[Person] {
    def writes(person: Person): JsValue = {
      Json.obj(
        // "href" -> person.id.map(personId ⇒ routes.PeopleApi.person(personId).url),
        "first_name" -> person.firstName,
        "last_name" -> person.lastName,
        "country" -> person.address.countryCode)
    }
  }

  def people(stakeholdersOnly: Option[Boolean], boardmembersOnly: Option[Boolean]) = TokenSecuredAction { implicit request ⇒
    val people: List[Person] = Person.findActive(stakeholdersOnly.getOrElse(false), boardmembersOnly.getOrElse(false))
    Ok(Json.toJson(people))
  }

}
