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

import models.UserRole.Role._
import models._
import models.payment.{Payment, PaymentException, RequestException}
import models.service.Services
import org.joda.money.CurrencyUnit._
import org.joda.money.Money
import org.joda.time.DateTime
import play.api.Play.current
import play.api.cache.Cache
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc._
import play.api.{Logger, Play}
import securesocial.core.{RuntimeEnvironment, SecureSocial}
import views.Countries

/**
 * Contains registration data required to create a person object
 * @param firstName First name
 * @param lastName Last name
 * @param email Email address
 * @param country Country where the person lives
 */
case class UserData(firstName: String,
  lastName: String,
  email: String,
  country: String,
  org: Boolean = false,
  orgData: OrgData = OrgData("", ""))

/**
 * Contains registration data required to create an organization
 * @param name Name
 * @param country Country where the organization is registered
 */
case class OrgData(name: String, country: String)

/**
 * -v
 * Contains actions for a registration process
 */
class Registration(environment: RuntimeEnvironment[ActiveUser])
    extends Enrollment
    with Security
    with Activities
    with Services {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  val REGISTRATION_COOKIE = "registration"

  class ValidationException(msg: String) extends RuntimeException(msg) {}

  private def userForm = Form(mapping(
    "firstName" -> nonEmptyText,
    "lastName" -> nonEmptyText,
    "email" -> play.api.data.Forms.email,
    "country" -> nonEmptyText.verifying(
      "error.unknown_country",
      (value: String) ⇒ Countries.all.exists(_._1 == value)),
    "org" -> ignored(false),
    "orgData" -> ignored(OrgData("", "")))(UserData.apply)(UserData.unapply))

  private def orgForm = Form(mapping(
    "name" -> nonEmptyText,
    "country" -> nonEmptyText.verifying(
      "error.unknown_country",
      (value: String) ⇒ Countries.all.exists(_._1 == value)))(OrgData.apply)(OrgData.unapply))

  /**
   * The authentication flow for all providers starts here.
   *
   * @param provider The id of the provider that needs to handle the call
   */
  def authenticate(provider: String) = Action { implicit request ⇒
    val session = request.session -
      SecureSocial.OriginalUrlKey +
      (SecureSocial.OriginalUrlKey -> routes.Registration.step2().url)
    val route = env.routes.authenticationUrl(provider)
    Redirect(route).withSession(session)
  }

  /**
   * Renders welcome page for new users
   */
  def welcome = Action { implicit request ⇒
    Ok(views.html.v2.registration.welcome())
  }

  /**
   * Renders step 1 page of the registration process
   *
   * @param org Defines if a new Supporter is an organization or a person
   */
  def step1(org: Boolean = false) = Action { implicit request ⇒
    val cookie = Cookie(REGISTRATION_COOKIE, "org")
    val discardingCookie = DiscardingCookie(REGISTRATION_COOKIE)
    if (org)
      Ok(views.html.v2.registration.step1()).withCookies(cookie)
    else
      Ok(views.html.v2.registration.step1()).discardingCookies(discardingCookie)
  }

  /**
   * Renders step 2 page of the registration process
   * @return
   */
  def step2 = SecuredRestrictedAction(Unregistered) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      redirectViewer {
        val (firstName, lastName) = userNames(user.identity)
        val form = userForm.bind(Map(("firstName", firstName),
          ("lastName", lastName),
          ("email", user.identity.profile.email.getOrElse(""))))
        Ok(views.html.v2.registration.step2(user, form))
      }
  }

  /**
   * Renders step 3 page of the registration process
   */
  def step3 = SecuredRestrictedAction(Unregistered) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      redirectViewer {
        Ok(views.html.v2.registration.step3(user, orgForm))
      }
  }

  /**
   * Saves new person to cache
   */
  def savePerson = SecuredRestrictedAction(Unregistered) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      redirectViewer {
        userForm.bindFromRequest.fold(
          errForm ⇒ BadRequest(views.html.v2.registration.step2(user, errForm)),
          data ⇒ {
            val id = personCacheId(user.identity.profile.userId)
            Cache.set(id, data, 900)
            val paymentUrl = routes.Registration.payment().url
            val url: String = request.cookies.get(REGISTRATION_COOKIE) map { x ⇒
              if (x.value == "org")
                routes.Registration.step3().url
              else
                paymentUrl
            } getOrElse paymentUrl
            Redirect(url)
          })
      }
  }

  /**
   * Saves new org to cache
   */
  def saveOrg = SecuredRestrictedAction(Unregistered) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      redirectViewer {
        checkPersonData { implicit userData ⇒
          orgForm.bindFromRequest.fold(
            errForm ⇒ BadRequest(views.html.v2.registration.step3(user, errForm)),
            data ⇒ {
              val id = personCacheId(user.identity.profile.userId)
              Cache.set(id, userData.copy(org = true, orgData = data), 900)
              Redirect(routes.Registration.payment())
            })
        }
      }
  }

  /**
   * Renders Payment page of the registration process
   */
  def payment = SecuredRestrictedAction(Unregistered) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      redirectViewer {
        checkPersonData { implicit userData ⇒
          val publicKey = Play.configuration.getString("stripe.public_key").get
          val person = unregisteredPerson(userData, user.identity)
          val country = if (userData.org) userData.orgData.country else userData.country
          val org = if (userData.org)
            Some(Organisation(userData.orgData.name, userData.orgData.country))
          else
            None
          val fee = Payment.countryBasedFees(country)
          Ok(views.html.v2.registration.payment(paymentForm, person, publicKey, fee, org))
        }
      }
  }

  /**
   * Makes a transaction and creates all objects
   */
  def charge = SecuredRestrictedAction(Unregistered) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      redirectViewer {
        Cache.getAs[UserData](personCacheId(user.identity.profile.userId)) map { userData ⇒
          val person = unregisteredPerson(userData, user.identity).insert
          val org = if (userData.org) {
            val profile = SocialProfile(0, ProfileType.Organisation, userData.email)
            val view = orgService.insert(OrgView(unregisteredOrg(userData), profile))
            Some(view.org)
          } else {
            None
          }

          paymentForm.bindFromRequest.fold(
            hasError ⇒
              BadRequest(Json.obj("message" -> Messages("error.payment.unexpected_error"))),
            data ⇒ {
              try {
                if (data.fee < Payment.countryBasedFees(person.address.countryCode)._1) {
                  throw new ValidationException("error.payment.minimum_fee")
                }
                val customerId = subscribe(person, org, data)
                org map { x ⇒
                  orgService.update(x.copy(customerId = Some(customerId), active = true))
                  person.copy(active = true).update
                  person.addRelation(x.id.get)
                } getOrElse {
                  person.copy(customerId = Some(customerId), active = true).update
                }
                val fee = Money.of(EUR, data.fee)
                val member = org map { x ⇒
                  x.becomeMember(funder = false, fee, person.id.get)
                } getOrElse {
                  person.becomeMember(funder = false, fee)
                }
                updateActiveUser(user.identity, person, member)
                notify(person, org, member)
                subscribe(person, member)

                activity(member, person).becameSupporter.insert()

                val orgId = org map (_.id) getOrElse None
                Ok(Json.obj("redirect" -> routes.Registration.congratulations(orgId).url))
              } catch {
                case e: PaymentException ⇒
                  val error = e.code match {
                    case "card_declined" ⇒ "error.payment.card_declined"
                    case "incorrect_cvc" ⇒ "error.payment.incorrect_cvc"
                    case "expired_card" ⇒ "error.payment.expired_card"
                    case "processing_error" ⇒ "error.payment.processing_error"
                    case _ ⇒ "error.payment.unexpected_error"
                  }
                  clean(person, org)
                  BadRequest(Json.obj("message" -> Messages(error)))
                case e: RequestException ⇒
                  clean(person, org)
                  e.log.foreach(Logger.error(_))
                  BadRequest(Json.obj("message" -> Messages(e.getMessage)))
                case e: ValidationException ⇒
                  clean(person, org)
                  BadRequest(Json.obj("message" -> Messages(e.getMessage)))
              }
            })
        } getOrElse {
          Ok(Json.obj("redirect" -> routes.Registration.step2().url))
        }
      }
  }

  /**
   * Renders congratulations screen
   *
   * @param orgId Organisation identifier
   */
  def congratulations(orgId: Option[Long] = None) = Action { implicit request ⇒
    orgId map { id ⇒
      Ok(views.html.v2.registration.congratulations(routes.Organisations.details(id).url, true))
    } getOrElse {
      Ok(views.html.v2.registration.congratulations(routes.Dashboard.profile().url, false))
    }
  }

  /**
    * Adds new person as a member and updates cached object
   *
   * By updating cached object we give the user a full access to the system
   *  without relogging
   *
   * @param identity Identity object
   * @param person Person
   */
  protected def updateActiveUser(identity: UserIdentity,
    person: Person,
    member: Member)(implicit request: RequestHeader) = {
    val account = UserAccount(None, person.id.get,
      Some(person.socialProfile.email),
      None,
      person.socialProfile.twitterHandle,
      person.socialProfile.facebookUrl,
      person.socialProfile.linkedInUrl,
      person.socialProfile.googlePlusUrl,
      member = true, registered = true)
    val inserted = userAccountService.insert(account)
    env.authenticatorService.fromRequest.foreach(auth ⇒ auth.foreach {
      _.updateUser(ActiveUser(identity, inserted, person, Some(member)))
    })
  }

  /**
   * Returns an unique cache id for a person object of current user
   * @param userId User identifier from a social network
   */
  protected def personCacheId(userId: String): String = {
    "user_" + userId
  }

  /**
   * Redirects Viewer to an index page. Otherwise - run action
   */
  protected def redirectViewer(f: Result)(implicit request: Request[Any],
    handler: AuthorisationHandler,
    user: ActiveUser): Result = if (user.account.viewer)
    Redirect(routes.Dashboard.index())
  else
    f

  /**
   * Checks if person data are in cache and redirects to a person data form if not
   */
  protected def checkPersonData(f: UserData ⇒ Result)(implicit request: Request[Any],
    handler: AuthorisationHandler,
    user: ActiveUser): Result = {
    Cache.getAs[UserData](personCacheId(user.identity.profile.userId)) map { userData ⇒
      f(userData)
    } getOrElse {
      Redirect(routes.Registration.step2()).
        flashing("error" -> Messages("login.noUserData"))
    }
  }

  /**
   * Returns first and last names of the given user
   * @param user User object
   */
  protected def userNames(user: UserIdentity): (String, String) = {
    if (user.profile.firstName.exists(_.trim.isEmpty)) {
      val tokens: Array[String] = user.name.split(" ")
      tokens.length match {
        case 0 ⇒ ("", "")
        case 1 ⇒ (tokens(0), "")
        case _ ⇒ (tokens(0), tokens.slice(1, tokens.length).mkString(" "))
      }
    } else
      (user.profile.firstName.getOrElse(""),
        user.profile.lastName.getOrElse(""))
  }

  /**
   * Returns a person created from registration data
   * @param userData User data
   * @param user Social identity
   */
  private def unregisteredPerson(userData: UserData, user: UserIdentity): Person = {
    val photo = new Photo(None, None)
    val fullName = userData.firstName + " " + userData.lastName
    val dateStamp = new DateStamp(DateTime.now(), fullName, DateTime.now(), fullName)
    val person = new Person(None, userData.firstName, userData.lastName, None, photo,
      false, 0, None, None, webSite = None, blog = None, active = false,
      dateStamp = dateStamp)
    val address = new Address(countryCode = userData.country)
    person.address_=(address)
    val socialProfile = new SocialProfile(email = userData.email,
      twitterHandle = user.twitterHandle,
      facebookUrl = user.facebookUrl,
      googlePlusUrl = user.googlePlusUrl,
      linkedInUrl = user.linkedInUrl)
    person.socialProfile_=(socialProfile)
    person
  }

  /**
   * Returns an org created from registration data
   * @param userData User data
   * @return
   */
  private def unregisteredOrg(userData: UserData): Organisation = {
    val org = Organisation(userData.orgData.name, userData.orgData.country)
    val fullName = userData.firstName + " " + userData.lastName
    val dateStamp = new DateStamp(DateTime.now(), fullName, DateTime.now(), fullName)
    org.copy(dateStamp = dateStamp)
  }

  /**
   * Deletes person and org objects if something goes wrong during registration
   * process
   * @param person Person
   * @param org Organisation
   */
  private def clean(person: Person, org: Option[Organisation]) = {
    personService.delete(person.id.get)
    org foreach { x ⇒ orgService.delete(x.id.get) }
  }
}
