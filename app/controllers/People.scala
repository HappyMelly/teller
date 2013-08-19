package controllers

import models._
import org.joda.time.DateTime
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints
import play.api.i18n.Messages
import securesocial.core.SecureSocial
import scala.Predef._
import scala.Some
import securesocial.core.SecuredRequest

object People extends Controller with SecureSocial {

  /**
   * HTML form mapping for a person’s address.
   */
  val addressMapping = mapping(
    "id" -> ignored(Option.empty[Long]),
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
    "twitterHandle" -> optional(text.verifying(Constraints.pattern("""[A-Za-z0-9_]{1,16}""".r, error = "error.twitter"))),
    "facebookUrl" -> optional(text),
    "linkedInUrl" -> optional(text),
    "googlePlusUrl" -> optional(text),
    "boardMember" -> default(boolean, false),
    "stakeholder" -> default(boolean, false),
    "active" -> ignored(true),
    "created" -> ignored(DateTime.now()),
    "createdBy" -> ignored(request.user.fullName),
    "updated" -> ignored(DateTime.now()),
    "updatedBy" -> ignored(request.user.fullName))(Person.apply)(Person.unapply))

  /**
   * Form target for toggling whether a person is active.
   */
  def activation(id: Long) = SecuredAction {
    implicit request ⇒

      Person.find(id).map { person ⇒
        Form("active" -> boolean).bindFromRequest.fold(
          form ⇒ {
            BadRequest("invalid form data")
          },
          active ⇒ {
            Person.activate(id, active)
            val activity = Activity.insert(request.user.fullName, if (active) Activity.Predicate.Activated else Activity.Predicate.Deactivated, person.fullName)
            Redirect(routes.People.details(id)).flashing("success" -> activity.toString)
          })
      } getOrElse {
        Redirect(routes.People.index).flashing("error" -> Messages("error.notFound", Messages("models.Person")))
      }
  }

  /**
   * Create page.
   */
  def add = SecuredAction { implicit request ⇒
    Ok(views.html.person.form(request.user, None, personForm(request)))
  }

  /**
   * Deletes an person’s organisation membership.
   */
  def addMembership = SecuredAction { implicit request ⇒

    val membershipForm = Form(tuple("page" -> text, "personId" -> longNumber, "organisationId" -> longNumber))

    membershipForm.bindFromRequest.fold(
      errors ⇒ BadRequest("organisationId missing"),
      {
        case (page, personId, organisationId) ⇒ {
          Person.find(personId).map { person ⇒
            Organisation.find(organisationId).map { organisation ⇒
              person.addMembership(organisationId)
              val activityObject = Messages("activity.relationship.create", person.fullName, organisation.name)
              val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, activityObject)

              // Redirect to the page we came from - either the person or organisation details page.
              val action = if (page == "person") routes.People.details(personId) else routes.Organisations.details(organisationId)
              Redirect(action).flashing("success" -> activity.toString)
            }.getOrElse(NotFound)
          }.getOrElse(NotFound)
        }
      })
  }

  /**
   * Create form submits to this action.
   */
  def create = SecuredAction { implicit request ⇒
    personForm(request).bindFromRequest.fold(
      formWithErrors ⇒
        BadRequest(views.html.person.form(request.user, None, formWithErrors)),
      person ⇒ {
        val updatedPerson = person.insert
        val activity = Activity.insert(request.user.fullName, Activity.Predicate.Created, updatedPerson.fullName)
        Redirect(routes.People.index()).flashing("success" -> activity.toString)
      })
  }

  /**
   * Deletes a person.
   */
  def delete(id: Long) = SecuredAction { request ⇒
    Person.find(id).map { person ⇒
      Person.delete(id)
      val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, person.fullName)
      Redirect(routes.People.index).flashing("success" -> activity.toString)
    }.getOrElse(NotFound)
  }

  /**
   * Deletes an person’s organisation membership.
   */
  def deleteMembership(page: String, personId: Long, organisationId: Long) = SecuredAction { request ⇒
    Person.find(personId).map { person ⇒
      Organisation.find(organisationId).map { organisation ⇒
        person.deleteMembership(organisationId)
        val activityObject = Messages("activity.relationship.delete", person.fullName, organisation.name)
        val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, activityObject)

        // Redirect to the page we came from - either the person or organisation details page.
        val action = if (page == "person") routes.People.details(personId) else routes.Organisations.details(organisationId)
        Redirect(action).flashing("success" -> activity.toString)
      }
    }.flatten.getOrElse(NotFound)
  }

  /**
   * Details page.
   * @param id Person ID
   */
  def details(id: Long) = SecuredAction { implicit request ⇒
    models.Person.find(id).map { person ⇒
      val otherOrganisations = Organisation.findActive.filterNot(organisation ⇒ person.membership.contains(organisation))
      val licenses = License.licenses(id)
      Ok(views.html.person.details(request.user, person, person.membership, otherOrganisations, licenses))
    } getOrElse {
      Redirect(routes.People.index).flashing("error" -> Messages("error.notFound", Messages("models.Person")))
    }
  }

  /**
   * Edit page.
   * @param id Person ID
   */
  def edit(id: Long) = SecuredAction { implicit request ⇒
    Person.find(id).map { person ⇒
      Ok(views.html.person.form(request.user, Some(id), personForm(request).fill(person)))
    }.getOrElse(NotFound)
  }

  /**
   * Edit form submits to this action.
   * @param id Person ID
   */
  def update(id: Long) = SecuredAction { implicit request ⇒
    personForm(request).bindFromRequest.fold(
      formWithErrors ⇒
        BadRequest(views.html.person.form(request.user, Some(id), formWithErrors)),
      person ⇒ {
        person.copy(id = Some(id)).update
        val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, person.fullName)
        Redirect(routes.People.details(id)).flashing("success" -> activity.toString)
      })
  }

  def index = SecuredAction { implicit request ⇒
    val people = models.Person.findAll
    Ok(views.html.person.index(request.user, people))
  }

}