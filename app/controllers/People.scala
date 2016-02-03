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

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.Forms._
import models.UserRole.{Role, DynamicRole}
import models.UserRole.Role._
import models._
import models.payment.{GatewayWrapper, PaymentException, RequestException}
import models.service.Services
import org.joda.time.DateTime
import play.api.Play.current
import play.api.data.Forms._
import play.api.data.validation.Constraints
import play.api.data.{Form, FormError}
import play.api.i18n.{MessagesApi, Messages}
import play.api.mvc._
import play.api.{Logger, Play}
import services.TellerRuntimeEnvironment
import services.integrations.Integrations

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class People @javax.inject.Inject()(override implicit val env: TellerRuntimeEnvironment,
                                    override val messagesApi: MessagesApi,
                                    deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder)(messagesApi, env)
  with Integrations
  with Files
  with Activities
  with MemberNotifications {

  val contentType = "image/jpeg"
  val indexCall: Call = routes.People.index()

  /**
   * Form target for toggling whether a person is active
   *
   * @param id Person identifier
   */
  def activation(id: Long) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    personService.find(id) flatMap {
      case None => redirect(indexCall, "error" -> "Person not found")
      case Some(person) =>
        Form("active" -> boolean).bindFromRequest.fold(
          form ⇒ badRequest("invalid form data"),
          active ⇒ {
            personService.activate(id, active)
            val log = if (active)
              activity(person, user.person).activated.insert()
            else
              activity(person, user.person).deactivated.insert()
            redirect(routes.People.details(id), "success" -> log.toString)
          })
    }
  }

  /**
   * Render a Create page
   */
  def add = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    ok(views.html.v2.person.form(user, None, People.personForm(user.name)))
  }

  /**
   * Assign a person to an organisation
   */
  def addRelationship() = AsyncSecuredRestrictedAction(List(Role.Admin, Role.Coordinator)) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val relationshipForm = Form(tuple("page" -> text,
        "personId" -> longNumber,
        "organisationId" -> longNumber))

      relationshipForm.bindFromRequest.fold(
        errors ⇒ badRequest("organisationId missing"),
        { case (page, personId, organisationId) ⇒
          (for {
            p <- personService.find(personId)
            o <- orgService.find(organisationId)
          } yield (p, o)) flatMap {
            case (None, _) => notFound("Person not found")
            case (_, None) => notFound("Organisation not found")
            case (Some(person), Some(organisation)) =>
              person.addRelation(organisationId)

              val log = activity(person, user.person, Some(organisation)).connected.insert()
              // Redirect to the page we came from - either the person or organisation details page.
              val action: String = if (page == "person")
                routes.People.details(personId).url
              else
                routes.Organisations.details(organisationId).url
              redirect(action, "success" -> log.toString)
          }
        })
  }

  /**
   * Create form submits to this action.
   */
  def create = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    People.personForm(user.name).bindFromRequest.fold(
      formWithErrors ⇒ badRequest(views.html.v2.person.form(user, None, formWithErrors)),
      person ⇒ {
        personService.insert(person) flatMap { updatedPerson =>
          val log = activity(updatedPerson, user.person).created.insert()
          redirect(indexCall, "success" -> log.toString)
        }
      })
  }

  /**
   * Delete a person
   *
   * @param id Person identifier
   */
  def delete(id: Long) = AsyncSecuredRestrictedAction(Admin) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    personService.find(id) flatMap {
      case None => notFound("Person not found")
      case Some(person) =>
        if (!person.deletable) {
          redirect(indexCall, "error" -> Messages("error.person.nonDeletable"))
        } else {
          personService.delete(person)
          val log = activity(person, user.person).deleted.insert()
          redirect(indexCall, "success" -> log.toString)
        }
    }
  }

  /**
   * Delete a relationthip of a person and an organisation
   *
   * @param page Page identifier where the action was requested from
   * @param personId Person identifier
   * @param organisationId Org identifier
   */
  def deleteRelationship(page: String, personId: Long, organisationId: Long) = AsyncSecuredProfileAction(personId) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      (for {
        p <- personService.find(personId)
        o <- orgService.find(organisationId)
      } yield (p, o)) flatMap {
        case (None, _) => notFound("Person not found")
        case (_, None) => notFound("Organisation not found")
        case (Some(person), Some(organisation))=>
          person.deleteRelation(organisationId)
          val log = activity(person, user.person,
            Some(organisation)).disconnected.insert()
          // Redirect to the page we came from - either the person or
          // organisation details page.
          val action: String = if (page == "person")
            routes.People.details(personId).url
          else
            routes.Organisations.details(organisationId).url
          redirect(action, "success" -> log.toString)
      }
  }

  /**
   * Render Details page
   *
   * @param id Person identifier
   */
  def details(id: Long) = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    (for {
      p <- personService.find(id)
      f <- facilitatorService.findByPerson(id)
      m <- personService.memberships(id)
      o <- orgService.findActive
    } yield (p, f, m, o)) flatMap {
      case (None, _, _, _) => redirect(indexCall, "error" -> "Person not found")
      case (Some(person), facilitators, memberships, orgs) =>
        val facilitator = facilitators.nonEmpty
        val otherOrganisations = orgs.filterNot(organisation ⇒ memberships.contains(organisation))
        val badgesInfo = if (facilitator) {
          val query = for {
            badges <- brandBadgeService.find(facilitators.flatMap(_.badges))
            brands <- brandService.find(badges.map(_.brandId).distinct)
          } yield (badges, brands)
          query map { case (badges, brands) =>
            badges.map { badge =>
              (badge, brands.find(_.brand.identifier == badge.brandId).map(_.brand.name).getOrElse(""))
            }
          }
        } else {
          Future.successful(List())
        }
        badgesInfo flatMap { info =>
          ok(views.html.v2.person.details(user, person, memberships, otherOrganisations, facilitator, info))
        }
    }
  }

  /**
   * Render an Edit page
   *
   * @param id Person identifier
   */
  def edit(id: Long) = AsyncSecuredDynamicAction(DynamicRole.ProfileEditor, id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      personService.find(id) flatMap {
        case None => notFound("Person not found")
        case Some(person) ⇒
          ok(views.html.v2.person.form(user, Some(id), People.personForm(user.name).fill(person)))
      }
  }

  /**
   * Edit form submits to this action
   *
   * @param id Person identifier
   */
  def update(id: Long) = AsyncSecuredDynamicAction(DynamicRole.ProfileEditor, id) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      personService.find(id) flatMap {
        case None => notFound("Person not found")
        case Some(oldPerson) ⇒
          People.personForm(user.name, Some(id)).bindFromRequest.fold(
            errors ⇒ badRequest(views.html.v2.person.form(user, Some(id), errors)),
            person ⇒ {
              checkDuplication(person, id, user.name) flatMap {
                case Some(form) => badRequest(views.html.v2.person.form(user, Some(id), form))
                case None =>
                  val modified = resetReadOnlyAttributes(person, oldPerson)

                  personService.member(id).filter(_.isDefined) map { _ =>
                    val msg = connectMeMessage(oldPerson.socialProfile, modified.socialProfile)
                    msg foreach { x => slack.send(updateMsg(modified.fullName, x)) }
                  }
                  modified.update flatMap { _ =>
                    if (modified.email != oldPerson.email) {
                      identityService.findByEmail(oldPerson.email).filter(_.isDefined) map { identity =>
                        if (identity.get.userId.exists(_ == id)) {
                          identityService.delete(oldPerson.email)
                          identityService.insert(identity.get.copy(email = modified.email))
                        }
                      }
                    }
                    val log = activity(modified, user.person).updated.insert()
                    val url: String = routes.People.details(id).url
                    redirect(url, "success" -> log.toString)
                  }
              }
            })
      }
  }

  /**
   * Renders tab for the given person
    *
    * @param id Person or Member identifier
   * @param tab Tab identifier
   */
  def renderTabs(id: Long, tab: String) = AsyncSecuredRestrictedAction(Viewer) {
    implicit request ⇒ implicit handler ⇒ implicit user ⇒
      tab match {
        case "contributions" ⇒
          contributionService.contributions(id, isPerson = true) flatMap { contributions =>
            ok(views.html.v2.element.contributions("person", contributions))
          }
        case "experience" ⇒
          (for {
            person <- personService.find(id)
            experience <- retrieveByBrandStatistics(id)
            endorsements <- personService.endorsements(id)
            materials <- personService.materials(id)
          } yield (person, experience, endorsements, materials)) flatMap {
            case (None, _, _, _) => notFound("Person not found")
            case (Some(person), experience, endorsements, materials) =>
              val endorsementsWithExp = endorsements.map {x =>
                (x, experience.find(_._1 == x.brandId).map(_._2).getOrElse(""))
              }
              val sortedMaterials = materials.sortBy(_.linkType)
              ok(views.html.v2.person.tabs.experience(person, experience, endorsementsWithExp, sortedMaterials))
          }
        case "facilitation" ⇒
          (for {
            p <- personService.find(id)
            l <- licenseService.licensesWithBrands(id)
            f <- facilitatorService.findByPerson(id)
          } yield (p, l, f)) flatMap {
            case (None, _, _) => notFound("Person not found")
            case (Some(person), licenses, facilitation) =>
              val facilitatorData = licenses
                .map(x ⇒ (x, facilitation.find(_.brandId == x.license.brandId).get.publicRating))
              ok(views.html.v2.person.tabs.facilitation(person, facilitatorData))
          }
        case "membership" ⇒
          (for {
            person <- personService.find(id)
            payments <- paymentRecordService.findByPerson(id)
          } yield (person, payments)) flatMap {
            case (None, _) => notFound("Person not found")
            case (Some(person), payments) =>
              person.member map { v ⇒
                ok(views.html.v2.person.tabs.membership(user, person, payments))
              } getOrElse ok("Person is not a member")
          }
        case _ ⇒ ok("")
      }
  }


  /**
   * Render a list of people in the network
   *
   * @return
   */
  def index = AsyncSecuredRestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    personService.findAll flatMap { people =>
      ok(views.html.v2.person.index(user, people))
    }
  }

  /**
   * Cancels a subscription for yearly-renewing membership
    *
    * @param id Person id
   */
  def cancel(id: Long) = AsyncSecuredProfileAction(id) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val url: String = routes.People.details(id).url + "#membership"
    personService.find(id) flatMap {
      case None => notFound("Person not found")
      case Some(person) ⇒
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
            redirect(url, "success" -> "Subscription was successfully canceled")
          } else {
            redirect(url, "error" -> Messages("error.membership.noSubscription"))
          }
        } getOrElse {
          redirect(url, "error" -> Messages("error.membership.noSubscription"))
      }
    }
  }

  /**
   * Returns form with errors if a person with identical social networks exists
   *
   * @param person Person object with incomplete social profile
   * @param id Identifier of a person which is updated
   * @param editorName Name of a user who adds changes
   */
  protected def checkDuplication(person: Person, id: Long, editorName: String)(
    implicit user: ActiveUser): Future[Option[Form[Person]]] = {
    val base = person.socialProfile.copy(objectId = id, objectType = ProfileType.Person)
    socialProfileService.findDuplicate(base) map {
      case None => None
      case Some(duplicate) ⇒
        var form = People.personForm(editorName).fill(person)
        compareSocialProfiles(duplicate, base).foreach(err ⇒ form = form.withError(err))
        Some(form)
    }
  }

  /**
   * Compares social profiles and returns a list of errors for a form
    *
    * @param left Social profile object
   * @param right Social profile object
   */
  protected def compareSocialProfiles(left: SocialProfile, right: SocialProfile): List[FormError] = {

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
    *
    * @param id Facilitator id
   */
  protected def retrieveByBrandStatistics(id: Long) = {
    (for {
      l <- licenseService.licensesWithBrands(id)
      f <- facilitatorService.findByPerson(id)
    } yield (l, f)) map { case (licenses, facilitations) =>
      licenses.sortBy(_.brand.name).map { view ⇒
        val facilitator = facilitations.find(_.brandId == view.brand.identifier).get
        (facilitator, view.brand.name)
      }
    }
  }

  /**
    * Updates the attributes of a person that shouldn't be changed on update
    *
    * @param updated Updated object
    * @param existing Existing object
    * @param user Current active user
    */
  protected def resetReadOnlyAttributes(updated: Person, existing: Person)(implicit user: ActiveUser): Person = {
    val modified = updated
      .copy(id = existing.id, active = existing.active)
      .copy(photo = existing.photo, customerId = existing.customerId)
      .copy(addressId = existing.addressId)
    val modifiedWithEmail = if (user.person.identifier == existing.identifier && user.account.byEmail)
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
        Await.result(identityService.checkEmail(suppliedEmail, userId), 10.seconds)
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
