/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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

import javax.inject.Named

import akka.actor.ActorRef
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import models.JodaMoney.jodaMoney
import models.UserRole.Role._
import models._
import models.cm.License
import models.cm.event.Attendee
import models.core.notification.NewFacilitator
import models.repository.Repositories
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json._
import play.api.mvc.RequestHeader
import securesocial.core.providers.MailToken
import services.TellerRuntimeEnvironment

import scala.concurrent.Future

/**
 * Content license pages and API.
 */
class Licenses @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment,
                                       override val messagesApi: MessagesApi,
                                       val repos: Repositories,
                                       @Named("notification") notificationDispatcher: ActorRef,
                                       deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env)
  with PasswordIdentities
  with Activities
  with I18nSupport {

  /**
   * HTML form mapping for creating and editing.
   */
  val licenseForm = Form(mapping(
    "id" -> ignored(Option.empty[Long]),
    "licenseeId" -> ignored(0L),
    "brandId" -> nonEmptyText.transform(_.toLong, (l: Long) ⇒ l.toString),
    "version" -> nonEmptyText,
    "signed" -> jodaLocalDate,
    "start" -> jodaLocalDate,
    "end" -> jodaLocalDate,
    "confirmed" -> default(boolean, false),
    "fee" -> jodaMoney(),
    "feePaid" -> optional(jodaMoney()))(License.apply)(License.unapply).verifying(
      "error.date.range", (license: License) ⇒ !license.start.isAfter(license.end)))

  implicit val personWrites = new Writes[Person] {
    def writes(person: Person): JsValue = {
      Json.obj(
        "first_name" -> person.firstName,
        "last_name" -> person.lastName,
        "id" -> person.id.get)
    }
  }

  /**
    * Renders add form for a new content license
   *
   * @param personId Person identifier
   */
  def add(personId: Long) = RestrictedAction(Coordinator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val form = licenseForm.fill(License.blank(personId))
      coordinatedBrands(user.account.personId) flatMap { brands =>
        ok(views.html.v2.license.addForm(user, form, brands, personId))
      }
  }

  /**
    * Renders add form for a new content license for attendee
    *
    * @param attendeeId Attendee identifier
    * @param eventId Event identifier
    */
  def addForAttendee(attendeeId: Long, eventId: Long) = RestrictedAction(Coordinator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      (for {
        attendee <- repos.cm.rep.event.attendee.find(attendeeId, eventId)
        event <- repos.cm.event.find(eventId)
        brands <- coordinatedBrands(user.account.personId)
      } yield (attendee, event, brands)) flatMap {
        case (None, _, _) => notFound("Attendee not found")
        case (_, None, _) => notFound("Event not found")
        case (Some(attendee), Some(event), brands) =>
          val filteredBrands = brands.filter(_.identifier == event.brandId)
          val form = licenseForm.fill(License.blank(attendeeId))
          ok(views.html.v2.license.attendeeForm(user, form, brands, attendee))
      }
  }

  /**
    * Adds a new content license for the given person
   *
   * @param personId Person identifier
   */
  def create(personId: Long) = RestrictedAction(Coordinator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      (for {
        person <- repos.person.find(personId)
        brands <- coordinatedBrands(user.account.personId)
      } yield (person, brands)) flatMap {
        case (None, _) => notFound("Person not found")
        case (Some(person), brands) =>
          val form = licenseForm.bindFromRequest
          form.fold(
            formWithErrors ⇒ badRequest(views.html.v2.license.addForm(user, formWithErrors, brands, personId)),
            license ⇒ {
              brands.find(_.identifier == license.brandId) map { brand =>
                checkOtherAccountEmail(person) flatMap { result =>
                  if (!result) {
                    repos.cm.license.add(license.copy(licenseeId = personId)) flatMap { addedLicense =>
                      val query = for {
                        strength <- repos.profileStrength.find(personId, org = false) if strength.isDefined
                      } yield strength.get
                      query.map { strength =>
                        repos.profileStrength.update(ProfileStrength.forFacilitator(strength))
                      }
                      createFacilitatorAccount(person, user.person.identifier, brand)
                      val route: String = core.routes.People.details(personId).url + "#facilitation"
                      redirect(route, "success" -> "License for brand %s was added".format(brand.name))
                    }
                  } else {
                    val msg = "The email of this facilitator is used in another account. This facilitator won't be able to login" +
                      " by email. Please update the email first and then proceed."
                    val errors = form.withGlobalError(msg)
                    badRequest(views.html.v2.license.addForm(user, errors, brands, personId))
                  }
                }
              } getOrElse {
                val formWithError = form.withError("brandId", "You are not a coordinator of the selected brand")
                badRequest(views.html.v2.license.addForm(user, formWithError, brands, personId))
              }
            })
      }
  }

  /**
    * Adds a new content license for the given attendee
    *
    * @param attendeeId Attendee identifier
    */
  def createFromAttendee(attendeeId: Long, eventId: Long) = RestrictedAction(Coordinator) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      (for {
        a <- repos.cm.rep.event.attendee.find(attendeeId, eventId)
        e <- repos.cm.event.get(eventId)
        b <- coordinatedBrands(user.account.personId)
      } yield (a, e, b)) flatMap {
        case (None, _, _) => notFound("Attendee not found")
        case (Some(attendee), event, unfilteredBrands) =>
          val brands = unfilteredBrands.filter(_.identifier == event.brandId)
          val form = licenseForm.bindFromRequest
          form.fold(
            formWithErrors ⇒ badRequest(views.html.v2.license.attendeeForm(user, formWithErrors, brands, attendee)),
            license ⇒ {
              brands.find(_.identifier == license.brandId) map { brand =>
                repos.identity.findByEmail(attendee.email) flatMap {
                  case Some(_) =>
                    val msg = "The email of this facilitator is used in another account. This facilitator won't be able to login" +
                      " by email. Please update the email first and then proceed."
                    val errors = form.withGlobalError(msg)
                    badRequest(views.html.v2.license.attendeeForm(user, errors, brands, attendee))
                  case None =>
                    val actions = for {
                      person <- createPersonFromAttendee(attendee)
                      license <- repos.cm.license.add(license.copy(licenseeId = person.identifier))
                    } yield (person, license)
                    actions flatMap { case (person, license) =>
                      repos.profileStrength.find(person.identifier, org = false).filter(_.isDefined) map { x ⇒
                        repos.profileStrength.update(ProfileStrength.forFacilitator(x.get))
                      }
                      createFacilitatorAccount(person, user.person.identifier, brand)
                      activity(license, user.person).created.insert(repos)
                      val route: String = core.routes.People.details(person.identifier).url + "#facilitation"
                      redirect(route, "success" -> "License for brand %s was added".format(brand.name))
                    }
                }
              } getOrElse {
                val formWithError = form.withError("brandId", "You are not a coordinator of the selected brand")
                badRequest(views.html.v2.license.attendeeForm(user, formWithError, brands, attendee))
              }
            })
      }
  }

  /**
   * Deletes a license
   *
    * @param brandId Brand identifier
   * @param id License identifier
   */
  def delete(brandId: Long, id: Long) = BrandAction(brandId) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      repos.cm.license.findWithBrandAndLicensee(id) flatMap {
        case None => notFound("License not found")
        case Some(view) =>
          val licenseeId = view.licensee.identifier
          repos.cm.license.delete(id)
          repos.cm.license.licenses(licenseeId) map { licenses =>
            if (licenses.isEmpty) {
              repos.userAccount.findByPerson(licenseeId).filter(_.isDefined).map(_.get) map { account =>
                repos.userAccount.update(account.copy(facilitator = false, activeRole = true))
              }

            }
          }
          activity(view.license, user.person).deleted.insert(repos)
          val route: String = core.routes.People.details(view.licensee.identifier).url + "#facilitation"
          redirect(route, "success" -> "License for brand %s was deleted".format(view.brand.name))
      }
  }

  /**
    * Renders a license edit form
    *
    * @param id License identifier
    */
  def edit(id: Long) = RestrictedAction(Coordinator) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    (for {
      license <- repos.cm.license.find(id)
      brands <- coordinatedBrands(user.account.personId)
    } yield (license, brands)) flatMap {
      case (None, _) => notFound("License not found")
      case (Some(license), brands) =>
        brands.find(_.identifier == license.brandId) map { brand =>
          ok(views.html.v2.license.editForm(user, license.id.get, licenseForm.fill(license), brands, brand.identifier))
        } getOrElse {
          redirect(core.routes.Dashboard.index(), "error" -> "You are not a coordinator of the selected brand")
        }
    }
  }

  /**
    * Updates existing license
   *
   * @param id License identifier
   */
  def update(id: Long) = RestrictedAction(Coordinator) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    (for {
      l <- repos.cm.license.findWithBrandAndLicensee(id)
      b <- coordinatedBrands(user.account.personId)
    } yield (l, b)) flatMap {
      case (None, _) => redirect(core.routes.Dashboard.index(), "error" -> Messages("error.notFound", Messages("models.License")))
      case (Some(view), brands) =>
        val form = licenseForm.bindFromRequest
        form.fold(
          formWithErrors ⇒
            badRequest(views.html.v2.license.editForm(user, id, formWithErrors, brands, view.brand.identifier)),
          license ⇒ {
            brands.find(_.identifier == license.brandId) map { brand =>
              val editedLicense = license.copy(id = Some(id), licenseeId = view.license.licenseeId)
              repos.cm.license.update(editedLicense)

              activity(license, user.person).updated.insert(repos)
              val route: String = core.routes.People.details(view.license.licenseeId).url + "#facilitation"
              redirect(route, "success" -> "License was updated")
            } getOrElse {
              val formWithError = form.withError("brandId", "You are not a coordinator of the selected brand")
              badRequest(views.html.v2.license.editForm(user, id, formWithError, brands, license.brandId))
            }
          })
    }
  }

  /**
    * Returns a list of brands coordinated by the given person
    *
    * @param coordinatorId Coordinator identifier
    */
  protected def coordinatedBrands(coordinatorId: Long): Future[List[Brand]] =
    repos.cm.brand.findByCoordinator(coordinatorId) map { brands =>
      brands.map(_.brand)
    }

  /**
    * Returns true if another registered user account with the same email exist
    *
    * @param person User
    */
  protected def checkOtherAccountEmail(person: Person): Future[Boolean] =
    repos.identity.findByEmail(person.email).map(_.exists(_.userId != person.id))

  /**
    * Returns person object for the given attendee
    *
    * @param attendee Attendee
    */
  protected def createPersonFromAttendee(attendee: Attendee): Future[Person] = {
    val person = Person(attendee.firstName, attendee.lastName, attendee.email).copy(birthday = attendee.dateOfBirth)
    person.address_=(Address(None, attendee.street_1, attendee.street_2, attendee.city, attendee.province,
      attendee.postcode, attendee.countryCode.getOrElse("XX")))
    person.profile_=(SocialProfile(objectType = ProfileType.Person))
    repos.person.insert(person) map { inserted =>
      repos.cm.rep.event.attendee.update(attendee.copy(personId = inserted.id))
      inserted
    }
  }

  /**
    * Creates an account with facilitator access
    * It also sends an email to the user inviting to create new password
    *
    * @param person Person
    * @param brand Brand
    */
  protected def createFacilitatorAccount(person: Person,
                                         from: Long,
                                         brand: Brand)(implicit request: RequestHeader): Unit = {
    createToken(person.email, isSignUp = false).map { token =>
      val unexpirable = unexpirableToken(token)
      repos.userAccount.findByPerson(person.identifier) map {
        case None =>
          val account = UserAccount.empty(person.identifier).copy(byEmail = true, facilitator = true, registered = true)
          repos.userAccount.insert(account)
          setupLoginByEmailEnvironment(person, unexpirable)
          sendFacilitatorWelcomeEmail(person, brand.name, unexpirable.uuid)
        case Some(account) =>
          if (!account.byEmail) {
            repos.userAccount.update(account.copy(byEmail = true, facilitator = true, registered = true))
            setupLoginByEmailEnvironment(person, unexpirable)
            sendFacilitatorWelcomeEmail(person, brand.name, unexpirable.uuid)
          } else {
            repos.userAccount.update(account.copy(facilitator = true, registered = true))
          }
      } map { _ =>
        notificationDispatcher ! NewFacilitator(person, from, brand)
      }
    }
  }

  /**
    * Sends a welcome email to a new facilitator
    *
    * @param person Person
    * @param brand Brand name
    * @param token Unique token for password creation
    */
  protected def sendFacilitatorWelcomeEmail(person: Person, brand: String, token: String)(implicit request: RequestHeader) = {
    env.mailer.sendEmail(s"Your Facilitator Account for $brand",
      person.email,
      (None, Some(mail.password.html.facilitator(person.firstName, token, brand)))
    )
  }

  /**
    * Returns a token which will expire only in 10 years
    *
    * @param token Token
    */
  protected def unexpirableToken(token: MailToken): MailToken =
    token.copy(expirationTime = token.expirationTime.plusYears(10))
}
