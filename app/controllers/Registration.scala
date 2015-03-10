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

import models._
import models.UserRole.Role._
import models.payment.{ RequestException, PaymentException, Payment }
import models.service.Services
import org.joda.money.CurrencyUnit._
import org.joda.money.Money
import org.joda.time.DateTime
import play.api.{ Logger, Play }
import play.api.Play.current
import play.api.cache.Cache
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc._
import securesocial.core.{ IdentityId, SecureSocial }
import services.notifiers.Notifiers
import views.Countries

/**
 * Contains registration data required to create a person object
 * @param firstName First name
 * @param lastName Last name
 * @param email Email address
 * @param country Country where the person lives
 */
case class User(firstName: String, lastName: String, email: String, country: String)

/**
 * Contains actions for a registration process
 */
trait Registration extends Controller
  with Security with Services with Notifiers {

  class ValidationException(msg: String) extends RuntimeException(msg) {}

  private def userForm = Form(mapping(
    "firstName" -> nonEmptyText,
    "lastName" -> nonEmptyText,
    "email" -> play.api.data.Forms.email,
    "country" -> nonEmptyText.verifying(
      "error.unknown_country",
      (value: String) ⇒ Countries.all.exists(_._1 == value)))(User.apply)(User.unapply))

  /**
   * The authentication flow for all providers starts here.
   *
   * @param provider The id of the provider that needs to handle the call
   */
  def authenticate(provider: String) = Action { implicit request ⇒
    val session = request.session -
      SecureSocial.OriginalUrlKey +
      (SecureSocial.OriginalUrlKey -> routes.Registration.step2().url)
    val route = securesocial.controllers.routes.ProviderController.authenticate(provider)
    Redirect(route).withSession(session)
  }

  /**
   * Renders welcome page for new users
   */
  def welcome = Action { implicit request ⇒
    Ok(views.html.registration.welcome())
  }
  /**
   * Renders step 1 page of the registration process
   */
  def step1 = Action { implicit request ⇒
    Ok(views.html.registration.step1())
  }

  /**
   * Renders step 2 page of the registration process
   * @return
   */
  def step2 = SecuredRestrictedAction(Unregistered) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      redirectViewer {
        val (firstName, lastName) = userNames(user)
        val form = userForm.bind(Map(("firstName", firstName),
          ("lastName", lastName),
          ("email", user.email.getOrElse(""))))
        Ok(views.html.registration.step2(user, form))
      }
  }

  /**
   * Saves new person to cache
   */
  def savePerson = SecuredRestrictedAction(Unregistered) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      redirectViewer {
        userForm.bindFromRequest.fold(
          errForm ⇒ BadRequest(views.html.registration.step2(user, errForm)),
          data ⇒ {
            val id = personCacheId(user.identityId)
            Cache.set(id, data, 900)
            Redirect(routes.Registration.step3())
          })
      }
  }
  /**
   * Renders step 3 page of the registration process
   */
  def step3 = SecuredRestrictedAction(Unregistered) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      redirectViewer {
        Cache.getAs[User](personCacheId(user.identityId)) map { userData ⇒
          val publicKey = Play.configuration.getString("stripe.public_key").get
          val person = unregisteredPerson(userData, user)
          val fee = Payment.countryBasedFees(userData.country)
          Ok(views.html.registration.step3(Membership.form, person, publicKey, fee))
        } getOrElse {
          Redirect(routes.Registration.step2()).
            flashing("error" -> Messages("login.noUserData"))
        }
      }
  }

  def charge = SecuredRestrictedAction(Unregistered) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      if (user.account.viewer) {
        Redirect(routes.Dashboard.index())
      } else {
        Cache.getAs[User](personCacheId(user.identityId)) map { userData ⇒
          val person = unregisteredPerson(userData, user).insert
          Membership.form.bindFromRequest.fold(
            hasError ⇒
              BadRequest(Json.obj("message" -> Messages("error.payment.unexpected_error"))),
            data ⇒ {
              try {
                if (data.fee < Payment.countryBasedFees(person.address.countryCode)._1) {
                  throw new ValidationException("error.payment.minimum_fee")
                }
                val key = Play.configuration.getString("stripe.secret_key").get
                val payment = new Payment(key)
                val customerId = payment.subscribe(person,
                  None,
                  data.token,
                  data.fee)

                val fee = Money.of(EUR, data.fee)

                person.copy(customerId = Some(customerId)).copy(active = true).update
                val account = UserAccount(None, person.id.get, "viewer",
                  person.socialProfile.twitterHandle,
                  person.socialProfile.facebookUrl,
                  person.socialProfile.linkedInUrl,
                  person.socialProfile.googlePlusUrl)
                UserAccount.insert(account)

                val member = person.becomeMember(funder = false, fee)
                val url = routes.People.details(person.id.get).url
                val fullUrl = Play.configuration.getString("application.baseUrl").getOrElse("") + url
                val text = "Hey @channel, we have *new Supporter*. %s, %s. <%s|View profile>".format(
                  person.fullName,
                  fee.toString,
                  fullUrl)
                slack.send(text)

                member.activity(person, Activity.Predicate.BecameSupporter).insert

                Ok(Json.obj("redirect" -> routes.Registration.congratulations().url))
              } catch {
                case e: PaymentException ⇒
                  val error = e.code match {
                    case "card_declined" ⇒ "error.payment.card_declined"
                    case "incorrect_cvc" ⇒ "error.payment.incorrect_cvc"
                    case "expired_card" ⇒ "error.payment.expired_card"
                    case "processing_error" ⇒ "error.payment.processing_error"
                    case _ ⇒ "error.payment.unexpected_error"
                  }
                  personService.delete(person.id.get)
                  BadRequest(Json.obj("message" -> Messages(error)))
                case e: RequestException ⇒
                  personService.delete(person.id.get)
                  e.log.foreach(Logger.error(_))
                  BadRequest(Json.obj("message" -> Messages(e.getMessage)))
                case e: ValidationException ⇒
                  personService.delete(person.id.get)
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
   */
  def congratulations = Action { implicit request ⇒
    Ok(views.html.registration.congratulations())
  }

  /**
   * Returns an unique cache id for a person object of current user
   * @param id Identity object
   */
  protected def personCacheId(id: IdentityId): String = {
    "user_" + id.userId
  }

  /**
   * Redirects Viewer to an index page. Otherwise - run action
   */
  protected def redirectViewer(f: SimpleResult)(implicit request: Request[Any],
    handler: AuthorisationHandler,
    user: UserIdentity): SimpleResult = if (user.account.viewer)
    Redirect(routes.Dashboard.index())
  else
    f

  /**
   * Returns first and last names of the given user
   * @param user User object
   */
  protected def userNames(user: UserIdentity): (String, String) = {
    if (user.firstName.length == 0) {
      val tokens: Array[String] = user.fullName.split(" ")
      tokens.length match {
        case 0 ⇒ ("", "")
        case 1 ⇒ (tokens(0), "")
        case _ ⇒ (tokens(0), tokens.slice(1, tokens.length).mkString(" "))
      }
    } else
      (user.firstName, user.lastName)
  }

  /**
   * Returns a person created from registration data
   * @param user User object
   */
  private def unregisteredPerson(user: User, remoteUser: UserIdentity): Person = {
    val photo = new Photo(None, None)
    val dateStamp = new DateStamp(DateTime.now(), "", DateTime.now(), "")
    val person = new Person(None, user.firstName, user.lastName, None, photo,
      false, 0, None, None, webSite = None, blog = None, active = false,
      dateStamp = dateStamp)
    val address = new Address(countryCode = user.country)
    person.address_=(address)
    val socialProfile = new SocialProfile(email = user.email,
      twitterHandle = remoteUser.twitterHandle,
      facebookUrl = remoteUser.facebookUrl,
      googlePlusUrl = remoteUser.googlePlusUrl,
      linkedInUrl = remoteUser.linkedInUrl)
    person.socialProfile_=(socialProfile)
    person
  }
}

object Registration extends Registration with Security with Services
