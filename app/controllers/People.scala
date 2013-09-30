/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package controllers

import models._
import org.joda.time.DateTime
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints
import play.api.i18n.Messages
import models.UserRole.Role._
import scala.Some
import securesocial.core.SecuredRequest

object People extends Controller with Security {

  // URL schemes and domains, in lower-case, for validation.
  private val ValidURLSchemes = Set("http", "https")
  private val FacebookDomain = "facebook.com"
  private val LinkedInDomain = "linkedin.com"
  private val GooglePlusDomain = "google.com"

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
   * Validate whether the given URL has the given lower-case scheme and domain, to prevent script injection attacks.
   */
  private def validateDomain(url: String, domain: String): Boolean = {
    try {
      val uri = new java.net.URI(url)
      val validScheme = ValidURLSchemes.contains(Option(uri.getScheme).getOrElse("").toLowerCase)
      val host = Option(uri.getHost).getOrElse("").toLowerCase
      val validDomain = host == domain || host.endsWith("." + domain)
      validScheme && validDomain
    } catch {
      case _: Throwable ⇒ false
    }
  }

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
    "facebookUrl" -> optional(text.verifying(error = "error.url.profile", validateDomain(_, FacebookDomain))),
    "linkedInUrl" -> optional(text.verifying(error = "error.url.profile", validateDomain(_, LinkedInDomain))),
    "googlePlusUrl" -> optional(text.verifying(error = "error.url.profile", validateDomain(_, GooglePlusDomain))),
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
  def activation(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

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
  def add = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒
      Ok(views.html.person.form(request.user, None, personForm(request)))
  }

  /**
   * Deletes an person’s organisation membership.
   */
  def addMembership = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

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
  def create = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

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
  def delete(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Person.find(id).map { person ⇒
        Person.delete(id)
        val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, person.fullName)
        Redirect(routes.People.index).flashing("success" -> activity.toString)
      }.getOrElse(NotFound)
  }

  /**
   * Deletes an person’s organisation membership.
   */
  def deleteMembership(page: String, personId: Long, organisationId: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

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
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒

      models.Person.find(id).map { person ⇒
        val memberships = person.memberships
        val otherOrganisations = Organisation.findActive.filterNot(organisation ⇒ memberships.contains(organisation))
        val licenses = License.licenses(id)
        val accountRole = UserAccount.findRole(id)

        Ok(views.html.person.details(request.user, person, memberships, otherOrganisations, licenses, accountRole, person.findUserWithSameTwitter))
      } getOrElse {
        Redirect(routes.People.index).flashing("error" -> Messages("error.notFound", Messages("models.Person")))
      }
  }

  /**
   * Edit page.
   * @param id Person ID
   */
  def edit(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      Person.find(id).map { person ⇒
        Ok(views.html.person.form(request.user, Some(id), personForm(request).fill(person)))
      }.getOrElse(NotFound)
  }

  /**
   * Edit form submits to this action.
   * @param id Person ID
   */
  def update(id: Long) = SecuredRestrictedAction(Editor) { implicit request ⇒
    implicit handler ⇒

      personForm(request).bindFromRequest.fold(
        formWithErrors ⇒
          BadRequest(views.html.person.form(request.user, Some(id), formWithErrors)),
        person ⇒ {
          person.copy(id = Some(id)).update
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Updated, person.fullName)
          Redirect(routes.People.details(id)).flashing("success" -> activity.toString)
        })
  }

  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒
      val people = models.Person.findAll
      Ok(views.html.person.index(request.user, people))
  }

}