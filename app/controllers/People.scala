/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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

import Forms._
import models._
import org.joda.time.DateTime
import play.api.mvc._
import play.api.data.{ FormError, Form }
import play.api.data.Forms._
import play.api.data.validation.Constraints
import play.api.i18n.Messages
import models.UserRole.Role._
import securesocial.core.SecuredRequest
import play.api.data.format.Formatter
import scala.util.matching.Regex

object People extends Controller with Security {

  val photoFormatter = new Formatter[Photo] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Photo] = {
      // "data" lets you access all form data values
      data.get("photo").map { socialId ⇒
        socialId match {
          case "facebook" ⇒
            data.get("profile.facebookUrl").map { url ⇒
              ("""[\w\.]+$""".r findFirstIn url).map { userId ⇒
                Right(Photo(Some(socialId), Some("http://graph.facebook.com/" + userId + "/picture?type=large")))
              }.getOrElse(Left(List(FormError("profile.facebookUrl", "Profile URL is invalid. It can't be used to retrieve a photo"))))
            }.getOrElse(Left(List(FormError("profile.facebookUrl", "Profile URL is invalid. It can't be used to retrieve a photo"))))
          case _ ⇒ Right(Photo(None, None))
        }
      }.getOrElse(Right(Photo(None, None)))
    }

    override def unbind(key: String, value: Photo): Map[String, String] = {
      Map(key -> value.id.getOrElse(""))
    }
  }

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
  def personForm(request: SecuredRequest[_]) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "emailAddress" -> email,
      "photo" -> of(photoFormatter),
      "address" -> addressMapping,
      "bio" -> optional(text),
      "interests" -> optional(text),
      "profile" -> tuple(
        "twitterHandle" -> optional(text.verifying(Constraints.pattern("""[A-Za-z0-9_]{1,16}""".r, error = "error.twitter"))),
        "facebookUrl" -> optional(facebookProfileUrl),
        "linkedInUrl" -> optional(linkedInProfileUrl),
        "googlePlusUrl" -> optional(googlePlusProfileUrl)),
      "boardMember" -> default(boolean, false),
      "stakeholder" -> default(boolean, false),
      "webSite" -> optional(webUrl),
      "blog" -> optional(webUrl),
      "active" -> ignored(true),
      "created" -> ignored(DateTime.now()),
      "createdBy" -> ignored(request.user.fullName),
      "updated" -> ignored(DateTime.now()),
      "updatedBy" -> ignored(request.user.fullName)) (
        { (id, firstName, lastName, emailAddress, photo, address, bio, interests, profiles, boardMember, stakeholder,
          webSite, blog, active, created, createdBy, updated, updatedBy) ⇒
          Person(id, firstName, lastName, emailAddress, photo, address, bio, interests, profiles._1, profiles._2,
            profiles._3, profiles._4, boardMember, stakeholder, webSite, blog, false, active, created,
            createdBy, updated, updatedBy)
        })(
          { (p: Person) ⇒
            Some(
              (p.id, p.firstName, p.lastName, p.emailAddress, p.photo, p.address, p.bio, p.interests,
                (p.twitterHandle, p.facebookUrl, p.linkedInUrl, p.googlePlusUrl),
                p.boardMember, p.stakeholder, p.webSite, p.blog, p.active, p.created, p.createdBy,
                p.updated, p.updatedBy))
          }))
  }

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
  def delete(id: Long) = SecuredDynamicAction("person", "delete") { implicit request ⇒
    implicit handler ⇒

      Person.find(id).map { person ⇒
        if (!person.deletable) {
          Redirect(routes.People.index).flashing("error" -> Messages("error.notDeletablePerson"))
        } else {
          Person.delete(id)
          val activity = Activity.insert(request.user.fullName, Activity.Predicate.Deleted, person.fullName)
          Redirect(routes.People.index).flashing("success" -> activity.toString)
        }
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
        val contributions = Contribution.contributions(id, true)
        val products = Product.findAll

        Ok(views.html.person.details(request.user, person,
          memberships, otherOrganisations,
          contributions, products,
          licenses, accountRole,
          UserAccount.findDuplicateIdentity(person)))
      } getOrElse {
        Redirect(routes.People.index).flashing("error" -> Messages("error.notFound", Messages("models.Person")))
      }
  }

  /**
   * Edit page.
   * @param id Person ID
   */
  def edit(id: Long) = SecuredDynamicAction("person", "edit") { implicit request ⇒
    implicit handler ⇒

      Person.find(id).map { person ⇒
        Ok(views.html.person.form(request.user, Some(id), personForm(request).fill(person)))
      }.getOrElse(NotFound)
  }

  /**
   * Edit form submits to this action.
   * @param id Person ID
   */
  def update(id: Long) = SecuredDynamicAction("person", "edit") { implicit request ⇒
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
