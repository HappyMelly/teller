/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package controllers

import controllers.Forms._
import models.UserRole.DynamicRole
import models.UserRole.Role._
import models._
import models.payment.{GatewayWrapper, PaymentException, RequestException}
import models.service.Services
import org.joda.time.DateTime
import play.api.Play.current
import play.api.data.Forms._
import play.api.data.validation.Constraints
import play.api.data.{Form, FormError}
import play.api.i18n.Messages
import play.api.{Logger, Play}
import securesocial.core.RuntimeEnvironment
import services.integrations.Integrations

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class People(environment: RuntimeEnvironment[ActiveUser])
    extends JsonController
    with Security
    with Services
    with Integrations
    with Files
    with Activities
    with MemberNotifications {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  val contentType = "image/jpeg"

  /**
   * Form target for toggling whether a person is active
   *
   * @param id Person identifier
   */
  def activation(id: Long) = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      personService.find(id).map { person ⇒
        Form("active" -> boolean).bindFromRequest.fold(
          form ⇒ BadRequest("invalid form data"),
          active ⇒ {
            Person.activate(id, active)
            val log = if (active)
              activity(person, user.person).activated.insert()
            else
              activity(person, user.person).deactivated.insert()
            Redirect(routes.People.details(id)).flashing("success" -> log.toString)
          })
      } getOrElse {
        Redirect(routes.People.index()).flashing(
          "error" -> Messages("error.notFound", Messages("models.Person")))
      }
  }

  /**
   * Render a Create page
   */
  def add = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.v2.person.form(user, None, People.personForm(user.name)))
  }

  /**
   * Assign a person to an organisation
   */
  def addRelationship() = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      val relationshipForm = Form(tuple("page" -> text,
        "personId" -> longNumber,
        "organisationId" -> longNumber))

      relationshipForm.bindFromRequest.fold(
        errors ⇒ BadRequest("organisationId missing"),
        {
          case (page, personId, organisationId) ⇒
            personService.find(personId).map { person ⇒
              orgService.find(organisationId).map { organisation ⇒
                person.addRelation(organisationId)

                val log = activity(person, user.person,
                  Some(organisation)).connected.insert()
                // Redirect to the page we came from - either the person or organisation details page.
                val action = if (page == "person")
                  routes.People.details(personId).url
                else
                  routes.Organisations.details(organisationId).url
                Redirect(action).flashing("success" -> log.toString)
              }.getOrElse(NotFound)
            }.getOrElse(NotFound)
        })
  }

  /**
   * Create form submits to this action.
   */
  def create = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      People.personForm(user.name).bindFromRequest.fold(
        formWithErrors ⇒
          BadRequest(views.html.v2.person.form(user, None, formWithErrors)),
        person ⇒ {
          val updatedPerson = person.insert
          val log = activity(updatedPerson, user.person).created.insert()
          Redirect(routes.People.index()).flashing("success" -> log.toString)
        })
  }

  /**
   * Delete a person
   *
   * @param id Person identifier
   */
  def delete(id: Long) = SecuredRestrictedAction(Admin) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      personService.find(id).map { person ⇒
        if (!person.deletable) {
          Redirect(routes.People.index()).flashing("error" -> Messages("error.person.nonDeletable"))
        } else {
          personService.delete(id)
          val log = activity(person, user.person).deleted.insert()
          Redirect(routes.People.index()).flashing("success" -> log.toString)
        }
      }.getOrElse(NotFound)
  }

  /**
   * Delete a relationthip of a person and an organisation
   *
   * @param page Page identifier where the action was requested from
   * @param personId Person identifier
   * @param organisationId Org identifier
   */
  def deleteRelationship(page: String,
                         personId: Long,
                         organisationId: Long) = SecuredProfileAction(personId) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒

        personService.find(personId).map { person ⇒
          orgService.find(organisationId).map { organisation ⇒
            person.deleteRelation(organisationId)
            val log = activity(person, user.person,
              Some(organisation)).disconnected.insert()
            // Redirect to the page we came from - either the person or
            // organisation details page.
            val action = if (page == "person")
              routes.People.details(personId).url
            else
              routes.Organisations.details(organisationId).url
            Redirect(action).flashing("success" -> log.toString)
          }
        }.flatten.getOrElse(NotFound)
  }

  /**
   * Render Details page
   *
   * @param id Person identifier
   */
  def details(id: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      personService.find(id) map { person ⇒
        val licenses = licenseService.licenses(id)
        val facilitator = licenses.nonEmpty
        val memberships = person.organisations
        val otherOrganisations = orgService.findActive.filterNot(organisation ⇒
          memberships.contains(organisation))
        Ok(views.html.v2.person.details(user, person,
          memberships, otherOrganisations, facilitator))
      } getOrElse {
        Redirect(routes.People.index()).flashing(
          "error" -> Messages("error.notFound", Messages("models.Person")))
      }
  }

  /**
   * Render an Edit page
   *
   * @param id Person identifier
   */
  def edit(id: Long) = AsyncSecuredDynamicAction(DynamicRole.ProfileEditor, id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒

      personService.find(id).map { person ⇒
        Future.successful(
          Ok(views.html.v2.person.form(user, Some(id), People.personForm(user.name).fill(person))))
      } getOrElse Future.successful(NotFound)
  }

  /**
   * Edit form submits to this action
   *
   * @param id Person identifier
   */
  def update(id: Long) = AsyncSecuredDynamicAction(DynamicRole.ProfileEditor, id) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        personService.find(id) map { oldPerson ⇒
          People.personForm(user.name, Some(id)).bindFromRequest.fold(
            formWithErrors ⇒
              Future.successful(BadRequest(views.html.v2.person.form(user, Some(id), formWithErrors))),
            person ⇒ {
              checkDuplication(person, id, user.name) map { form ⇒
                Future.successful(BadRequest(views.html.v2.person.form(user, Some(id), form)))
              } getOrElse {
                val modified = resetReadOnlyAttributes(person, oldPerson)

                personService.member(id) foreach { x ⇒
                  val msg = connectMeMessage(oldPerson.socialProfile,
                    modified.socialProfile)
                  msg foreach { x => slack.send(updateMsg(modified.fullName, x)) }
                }
                modified.update
                if (modified.email != oldPerson.email) {
                  identityService.findByEmail(oldPerson.email) map { identity =>
                    identityService.delete(oldPerson.email)
                    identityService.insert(identity.copy(email = modified.email))
                  }
                }
                val log = activity(modified, user.person).updated.insert()
                Future.successful(
                  Redirect(routes.People.details(id)).flashing("success" -> log.toString))
              }
            })
        } getOrElse Future.successful(NotFound)
  }

  /**
   * Renders tab for the given person
   * @param id Person or Member identifier
   * @param tab Tab identifier
   */
  def renderTabs(id: Long, tab: String) = SecuredRestrictedAction(Viewer) {
    implicit request ⇒
      implicit handler ⇒ implicit user ⇒
        tab match {
          case "contributions" ⇒
            val contributions = contributionService.contributions(id, isPerson = true)
            Ok(views.html.v2.element.contributions("person", contributions))
          case "experience" ⇒
            personService.find(id) map { person ⇒
              val experience = retrieveByBrandStatistics(id)
              val endorsements = personService.endorsements(id).map {x =>
                (x, experience.find(_._1 == x.brandId).map(_._2).getOrElse(""))
              }
              val materials = personService.materials(id).sortBy(_.linkType)
              Ok(views.html.v2.person.tabs.experience(person, experience,
                endorsements, materials))
            } getOrElse NotFound("Person not found")
          case "facilitation" ⇒
            personService.find(id) map { person ⇒
              val licenses = licenseService.licenses(id)
              val facilitation = facilitatorService.findByPerson(id)
              val facilitatorData = licenses
                .map(x ⇒ (x, facilitation.find(_.brandId == x.license.brandId).get.publicRating))
              Ok(views.html.v2.person.tabs.facilitation(person, facilitatorData))
            } getOrElse NotFound("Person not found")
          case "membership" ⇒
            personService.find(id) map { person ⇒
              person.member map { v ⇒
                val payments = paymentRecordService.findByPerson(id)
                Ok(views.html.v2.person.tabs.membership(user, person, payments))
              } getOrElse Ok("Person is not a member")
            } getOrElse NotFound("Person not found")
          case _ ⇒ Ok("")
        }
  }


  /**
   * Render a list of people in the network
   *
   * @return
   */
  def index = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val people = models.Person.findAll
      Ok(views.html.v2.person.index(user, people))
  }

  /**
   * Cancels a subscription for yearly-renewing membership
   * @param id Person id
   */
  def cancel(id: Long) = SecuredProfileAction(id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val url = routes.People.details(id).url + "#membership"
      personService.find(id) map { person ⇒
        person.member map { m ⇒
          if (m.renewal) {
            val key = Play.configuration.getString("stripe.secret_key").get
            val gateway = new GatewayWrapper(key)
            try {
              gateway.cancel(person.customerId.get)
              m.copy(renewal = false).update
            } catch {
              case e: PaymentException ⇒
                Redirect(url).flashing("error" -> Messages(e.msg))
              case e: RequestException ⇒
                e.log.foreach(Logger.error(_))
                Redirect(url).flashing("error" -> Messages(e.getMessage))
            }
            Redirect(url).
              flashing("success" -> "Subscription was successfully canceled")
          } else {
            Redirect(url).
              flashing("error" -> Messages("error.membership.noSubscription"))
          }
        } getOrElse {
          Redirect(url).
            flashing("error" -> Messages("error.membership.noSubscription"))
        }
      } getOrElse NotFound
  }

  /**
   * Returns form with errors if a person with identical social networks exists
   *
   * @param person Person object with incomplete social profile
   * @param id Identifier of a person which is updated
   * @param editorName Name of a user who adds changes
   */
  protected def checkDuplication(person: Person, id: Long, editorName: String)(implicit user: ActiveUser): Option[Form[Person]] = {
    val base = person.socialProfile.copy(objectId = id, objectType = ProfileType.Person)
    socialProfileService.findDuplicate(base) map { duplicate ⇒
      var form = People.personForm(editorName).fill(person)
      compareSocialProfiles(duplicate, base).
        foreach(err ⇒ form = form.withError(err))
      Some(form)
    } getOrElse None
  }

  /**
   * Compares social profiles and returns a list of errors for a form
   * @param left Social profile object
   * @param right Social profile object
   */
  protected def compareSocialProfiles(left: SocialProfile,
    right: SocialProfile): List[FormError] = {

    val list = mutable.MutableList[FormError]()
    val msg = Messages("error.socialProfile.exist")
    if (left.twitterHandle.nonEmpty && left.twitterHandle == right.twitterHandle) {
      list += FormError("profile.twitterHandle", msg)
    }
    if (left.facebookUrl.nonEmpty && left.facebookUrl == right.facebookUrl) {
      list += FormError("profile.facebookUrl", msg)
    }
    if (left.linkedInUrl.nonEmpty && left.linkedInUrl == right.linkedInUrl) {
      list += FormError("profile.linkedInUrl", msg)
    }
    if (left.googlePlusUrl.nonEmpty && left.googlePlusUrl == right.googlePlusUrl) {
      list += FormError("profile.googlePlusUrl", msg)
    }
    list.toList
  }


  /**
   * Retrieve facilitator statistics by brand, including years of experience,
   *  number of events and rating
   * @param id Facilitator id
   */
  protected def retrieveByBrandStatistics(id: Long) = {
    val licenses = licenseService.licenses(id).sortBy(_.brand.name)
    val facilitation = facilitatorService.findByPerson(id)
    licenses.map { x ⇒
      val facilitator = facilitation.find(_.brandId == x.license.brandId).get
      (facilitator,
        x.brand.name)
    }
  }

  /**
    * Updates the attributes of a person that shouldn't be changed on update
    * @param updated Updated object
    * @param existing Existing object
    * @param user Current active user
    */
  protected def resetReadOnlyAttributes(updated: Person, existing: Person)(implicit user: ActiveUser): Person = {
    val modified = updated
      .copy(id = existing.id, active = existing.active)
      .copy(photo = existing.photo, customerId = existing.customerId)
      .copy(addressId = existing.addressId)
    val modifiedWithEmail = if (user.person.identifier == existing.identifier && user.account.emailAuthentication)
      modified.copy(email = existing.email)
    else
      modified
    modifiedWithEmail
  }

  protected def updateMsg(name: String, msg: String): String = {
    "%s updated her/his social profile. %s".format(name, msg)
  }
}

object People extends Services {

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
   * HTML form mapping for a person’s social profile.
   */
  val socialProfileMapping = mapping(
    "twitterHandle" -> optional(text.verifying(Constraints.pattern("""[A-Za-z0-9_]{1,16}""".r, error = "error.twitter"))),
    "facebookUrl" -> optional(facebookProfileUrl),
    "linkedInUrl" -> optional(linkedInProfileUrl),
    "googlePlusUrl" -> optional(googlePlusProfileUrl))({
      (twitterHandle, facebookUrl, linkedInUrl, googlePlusUrl) ⇒
        SocialProfile(0, ProfileType.Person, twitterHandle, facebookUrl, linkedInUrl, googlePlusUrl)
    })({
      (s: SocialProfile) ⇒
        Some(s.twitterHandle, s.facebookUrl,
          s.linkedInUrl, s.googlePlusUrl)
    })

  /**
   * HTML form mapping for creating and editing.
   */
  def personForm(editorName: String, userId: Option[Long] = None)(implicit user: ActiveUser) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "emailAddress" -> play.api.data.Forms.email.verifying("Email address is already in use", { suppliedEmail =>
        import scala.concurrent.duration._
        Await.result(Future.successful(identityService.checkEmail(suppliedEmail, userId)), 10.seconds)
      }),
      "birthday" -> optional(jodaLocalDate),
      "signature" -> boolean,
      "address" -> addressMapping,
      "bio" -> optional(text),
      "interests" -> optional(text),
      "profile" -> socialProfileMapping,
      "webSite" -> optional(webUrl),
      "blog" -> optional(webUrl),
      "active" -> ignored(true),
      "dateStamp" -> mapping(
        "created" -> ignored(DateTime.now()),
        "createdBy" -> ignored(editorName),
        "updated" -> ignored(DateTime.now()),
        "updatedBy" -> ignored(editorName))(DateStamp.apply)(DateStamp.unapply))(
        { (id, firstName, lastName, emailAddress, birthday, signature,
          address, bio, interests, profile, webSite, blog, active, dateStamp) ⇒
          {
            val person = Person(id, firstName, lastName, emailAddress, birthday, Photo.empty,
              signature, address.id.getOrElse(0), bio, interests,
              webSite, blog, customerId = None, virtual = false, active, dateStamp)
            person.address_=(address)
            person.socialProfile_=(profile)
            person
          }
        })(
          { (p: Person) ⇒
            Some(
              (p.id, p.firstName, p.lastName, p.email, p.birthday,
                p.signature, p.address, p.bio, p.interests,
                p.socialProfile, p.webSite, p.blog, p.active, p.dateStamp))
          }))
  }
}
