package controllers

import models.{ Activity, Address, Person }
import org.joda.time.DateTime
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import securesocial.core.{ SecuredRequest, SecureSocial }
import play.api.i18n.Messages

object People extends Controller with SecureSocial {

  /**
   * HTML form mapping for a person’s address.
   */
  val addressMapping = mapping(
    "street1" -> optional(text),
    "street2" -> optional(text),
    "city" -> optional(text),
    "province" -> optional(text),
    "postCode" -> optional(text),
    "country" -> nonEmptyText)(Address.apply)(Address.unapply)

  /**
   * HTML form mapping for creating and editing.
   */
  def personForm(request: SecuredRequest[_]) = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "firstName" -> nonEmptyText,
    "lastName" -> nonEmptyText,
    "emailAddress" -> email,
    "address" -> addressMapping,
    "bio" -> optional(text),
    "interests" -> optional(text),
    "twitterHandle" -> optional(text),
    "facebookUrl" -> optional(text),
    "linkedInUrl" -> optional(text),
    "googlePlusUrl" -> optional(text),
    "boardMember" -> default(boolean, false),
    "stakeholder" -> default(boolean, true),
    "active" -> ignored(true),
    "created" -> ignored(DateTime.now()),
    "createdBy" -> ignored(request.user.fullName)) (Person.apply)(Person.unapply))

  /**
   * Create page.
   */
  def add = SecuredAction { implicit request ⇒
    Ok(views.html.person.form(request.user, None, personForm(request)))
  }

  /**
   * Create form submits to this action.
   */
  def create = SecuredAction { implicit request ⇒
    personForm(request).bindFromRequest.fold(
      formWithErrors ⇒
        BadRequest(views.html.person.form(request.user, None, formWithErrors)),
      person ⇒ {
        val updatedPerson = person.save
        Activity.insert(request.user.fullName, Activity.Predicate.Created, updatedPerson.fullName)
        val message = Messages("success.insert", Messages("models.Person"), updatedPerson.fullName)
        Redirect(routes.People.index()).flashing("success" -> message)
      })
  }

  def index = SecuredAction { implicit request ⇒
    val people = models.Person.findAll
    Ok(views.html.person.index(request.user, people))
  }

}