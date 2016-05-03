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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions, DeadboltHandler}
import controllers.hm.Enrollment
import models.UserRole.Role._
import models._
import models.core.payment._
import models.repository.Repositories
import org.joda.time.DateTime
import play.api.Play.current
import play.api.cache.Cache
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._
import play.api.{Logger, Play}
import securesocial.controllers.BaseRegistration
import securesocial.core.authenticator.CookieAuthenticator
import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core.providers.utils.PasswordValidator
import securesocial.core.services.SaveMode
import securesocial.core.utils._
import securesocial.core.{AuthenticationMethod, BasicProfile, SecureSocial}
import services.TellerRuntimeEnvironment
import services.integrations.Email
import views.Countries

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


/**
 * Contains registration data required to create a person object
  *
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
  orgData: OrgData = OrgData("", "")) {

  def feeCountry: String = if (org)
    orgData.country
  else
    country
}

/**
 * Contains registration data required to create an organization
  *
  * @param name Name
 * @param country Country where the organization is registered
 */
case class OrgData(name: String, country: String)

case class AuthenticationInfo(email: String, password: String)

/**
 * -v
 * Contains actions for a registration process
 */
class Registration @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment,
                                           override val messagesApi: MessagesApi,
                                           val repos: Repositories,
                                           val email: Email,
                                           deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env)
  with PasswordIdentities
  with Enrollment
  with Activities {

  val REGISTRATION_COOKIE = "registration"
  val SESSION_IN_SECONDS = 1800

  private def userForm = Form(mapping(
    "firstName" -> nonEmptyText,
    "lastName" -> nonEmptyText,
    "email" -> play.api.data.Forms.email.verifying("Email address is already in use", { suppliedEmail =>
      import scala.concurrent.duration._
      Await.result(repos.identity.checkEmail(suppliedEmail), 10.seconds)
    }),
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

  private def passwordForm = Form[AuthenticationInfo](
    mapping(
      "email" -> play.api.data.Forms.email.verifying("Email address is already in use", { suppliedEmail =>
        import scala.concurrent.duration._
        Await.result(repos.identity.checkEmail(suppliedEmail), 10.seconds)
      }),
      "password" ->
        tuple(
          "password1" -> nonEmptyText.verifying(PasswordValidator.constraint),
          "password2" -> nonEmptyText
        ).verifying(Messages(BaseRegistration.PasswordsDoNotMatch), passwords => passwords._1 == passwords._2)

    )((email, password) => AuthenticationInfo(email, password._1))((info: AuthenticationInfo) => Some((info.email, ("", "")))))

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

  def authenticateByEmail() = Action.async { implicit request =>
    passwordForm.bindFromRequest().fold(
      errors => Future.successful(BadRequest(views.html.v2.registration.step1(errors))),
      info => {
        val newUser = BasicProfile(
          UsernamePasswordProvider.UsernamePassword,
          info.email,
          None,
          None,
          None,
          Some(info.email),
          None,
          AuthenticationMethod.UserPassword,
          passwordInfo = Some(env.currentHasher.hash(info.password))
        )
        env.userService.save(newUser, SaveMode.SignUp).map { user =>
          env.authenticatorService.find(CookieAuthenticator.Id).map {
            _.fromUser(user).flatMap { authenticator =>
              Redirect(routes.Registration.step2().url).startingAuthenticator(authenticator)
            }
          } getOrElse {
            Logger.error(s"[securesocial] There isn't CookieAuthenticator registered in the RuntimeEnvironment")
            val url: String = core.routes.LoginPage.login().url
            redirect(url, "error" -> Messages("There was an error signing you up"))
          }
        }.flatMap(f => f)
      }
    )
  }

  /**
   * Renders welcome page for new users
   */
  def welcome = Action.async { implicit request ⇒
    ok(views.html.v2.registration.welcome())
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
      Ok(views.html.v2.registration.step1(passwordForm)).withCookies(cookie)
    else
      Ok(views.html.v2.registration.step1(passwordForm)).discardingCookies(discardingCookie)
  }

  /**
   * Renders step 2 page of the registration process
    *
    * @return
   */
  def step2 = redirectViewer { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    val orgData = OrgData("", "")
    val data = UserData(user.person.firstName, user.person.lastName, user.person.email, "", false, orgData)
    val form = userForm.fill(data)
    ok(views.html.v2.registration.step2(user, form))
  }

  /**
   * Renders step 3 page of the registration process
   */
  def step3 = redirectViewer { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    ok(views.html.v2.registration.step3(user, orgForm))
  }

  /**
   * Saves new person to cache
   */
  def savePerson = redirectViewer { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    userForm.bindFromRequest.fold(
      errForm ⇒ badRequest(views.html.v2.registration.step2(user, errForm)),
      data ⇒ {
        val id = personCacheId(user.id)
        Cache.set(id, data, SESSION_IN_SECONDS)
        val paymentUrl = routes.Registration.payment().url
        val url: String = request.cookies.get(REGISTRATION_COOKIE) map { x ⇒
          if (x.value == "org")
            routes.Registration.step3().url
          else
            paymentUrl
        } getOrElse paymentUrl
        redirect(url)
      })
  }

  /**
   * Saves new org to cache
   */
  def saveOrg = redirectViewer { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    isCachedDataAvailable { implicit userData ⇒
      orgForm.bindFromRequest.fold(
        errForm ⇒ badRequest(views.html.v2.registration.step3(user, errForm)),
        data ⇒ {
          val id = personCacheId(user.id)
          Cache.set(id, userData.copy(org = true, orgData = data), SESSION_IN_SECONDS)
          redirect(routes.Registration.payment())
        })
    }
  }

  /**
   * Renders Payment page of the registration process
   */
  def payment = redirectViewer { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    isCachedDataAvailable { implicit userData ⇒
      val publicKey = Play.configuration.getString("stripe.public_key").get
      val person = unregisteredPerson(userData, user)
      val org = if (userData.org)
        Some(Organisation(userData.orgData.name, userData.orgData.country))
      else
        None
      val fee = Payment.countryBasedFees(userData.feeCountry)
      ok(views.html.v2.registration.payment(paymentForm, person, publicKey, fee, org))
    }
  }

  /**
   * Makes a transaction and creates all objects
   */
  def charge = redirectViewer { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    isCachedDataAvailable { implicit userData ⇒
      validatePaymentForm(userData.feeCountry) match {
        case Left(result) => result
        case Right(data) =>
          val addRequest = addPersonWithOrg(userData, user)
          addRequest flatMap { case (person, org) =>
            try {
              payMembership(person, org, data)
              activateRecords(person, org)
              val futureMember = org map { x ⇒
                x.becomeMember(funder = false, data.fee, person.id.get, repos)
              } getOrElse {
                person.becomeMember(funder = false, data.fee, repos)
              }
              futureMember flatMap { member =>
                createUserAccount(user.id, user.providerId, person, member)
                notify(person, org, member)
                subscribe(person, member)

                activity(member, person).becameSupporter.insert(repos)

                val orgId = org map (_.id) getOrElse None
                ok(Json.obj("redirect" -> routes.Registration.congratulations(orgId).url))
              }
            } catch {
              case e: PaymentException ⇒
                clean(person, org)
                badRequest(Json.obj("message" -> Messages(e.getMessage)))
              case e: RequestException ⇒
                clean(person, org)
                e.log.foreach(Logger.error(_))
                badRequest(Json.obj("message" -> Messages(e.getMessage)))
            }
          }
      }
    }
  }

  /**
   * Renders congratulations screen
   *
   * @param orgId Organisation identifier
   */
  def congratulations(orgId: Option[Long] = None) = Action { implicit request ⇒
    orgId match {
      case None => Ok(views.html.v2.registration.congratulations(core.routes.Dashboard.profile().url, false))
      case Some(id) => Ok(views.html.v2.registration.congratulations(core.routes.Organisations.details(id).url, true))
    }
  }

  /**
    * Returns new user account for the given person
    *
    * @param person Person
    */
  protected def account(person: Person, remoteUserId: String, providerId: String): UserAccount =
    UserAccount(None, person.identifier, true,
      if (providerId == "twitter") Some(remoteUserId) else None,
      if (providerId == "facebook") Some(remoteUserId) else None,
      if (providerId == "linkedin") Some(remoteUserId) else None,
      if (providerId == "google") Some(remoteUserId) else None,
      member = true, registered = true)

  /**
    * Activates temporary records making them valid ones
    *
    * @param person Person
    * @param org Organisation
    */
  protected def activateRecords(person: Person, org: Option[Organisation]): Unit = {
    repos.person.update(person.copy(active = true))
    repos.socialProfile.update(person.profile.copy(objectId = person.identifier), ProfileType.Person)
    org map { value =>
      repos.org.update(value.copy(active = true))
      person.addRelation(value.identifier, repos)
    }
  }

  protected def addPersonWithOrg(registrationData: UserData, user: ActiveUser) = for {
    p <- repos.person.insert(unregisteredPerson(registrationData, user))
    o <- addOrganisation(registrationData)
  } yield (p, o)

  protected def addOrganisation(registrationData: UserData): Future[Option[Organisation]] =
    if (registrationData.org) {
      val profile = SocialProfile(0, ProfileType.Organisation)
      repos.org.insert(OrgView(unregisteredOrg(registrationData), profile)).map(x => Some(x.org))
    } else {
      Future.successful(None)
    }

  /**
    * Adds new person as a member and updates cached object
   *
   * By updating cached object we give the user a full access to the system
   *  without relogging
   *
   * @param id Identity object
   * @param person Person
   */
  protected def createUserAccount(id: String,
                                  providerId: String,
                                  person: Person,
                                  member: Member)(implicit request: RequestHeader) = {
    val futureInserted = repos.userAccount.insert(account(person, id, providerId))
    if (providerId == UsernamePasswordProvider.UsernamePassword) {
      Logger.info(s"End of registration of a user with ${id} id")
      repos.registeringUser.delete(id, providerId)
      repos.identity.findByEmail(id) flatMap {
        case None =>
          Logger.error(s"$id wasn't found in PasswordIdentity table on the final stage of registration")
          throw new RuntimeException("Internal error. Please contact support")
        case Some(identity) =>
          repos.identity.update(identity.copy(userId = person.id,
            firstName = Some(person.firstName),
            lastName = Some(person.lastName)))
      }
    } else {
      createToken(person.email, isSignUp = false).map { token =>
        setupLoginByEmailEnvironment(person, token)
        sendPasswordEmail(person, token.uuid)
      }
    }
    futureInserted map { inserted =>
      env.updateCurrentUser(ActiveUser(id, providerId, inserted, person, Some(member)))
    }
  }

  /**
   * Returns an unique cache id for a person object of current user
    *
    * @param userId User identifier from a social network
   */
  protected def personCacheId(userId: String): String = "user_" + userId

  /**
   * Redirects Viewer to an index page. Otherwise - run action
   */
  protected def redirectViewer(f: Request[Any] => DeadboltHandler => ActiveUser => Future[Result]) =
    RestrictedAction(Unregistered) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
      if (user.account.viewer)
        redirect(core.routes.Dashboard.index())
      else
        f(request)(handler)(user)
  }

  /**
   * Checks if person data are in cache and redirects to a person data form if not
   */
  protected def isCachedDataAvailable(f: UserData ⇒ Future[Result])(implicit request: Request[Any],
                                                                    handler: DeadboltHandler,
                                                                    user: ActiveUser): Future[Result] = {
    Cache.getAs[UserData](personCacheId(user.id)) map { userData ⇒
      f(userData)
    } getOrElse {
      redirect(routes.Registration.step2(), "error" -> Messages("login.noUserData"))
    }
  }

  /**
    * Sends a create new password email
    *
    * @param person Person
    * @param token Unique token for password creation
    */
  protected def sendPasswordEmail(person: Person, token: String)(implicit request: RequestHeader) = {
    env.mailer.sendEmail(s"Your Happy Melly Account",
      person.email,
      (None, Some(mail.password.html.member(person.firstName, token)))
    )
  }

  /**
   * Returns a person created from registration data
    *
    * @param userData User data
   * @param user ActiveUser
   */
  private def unregisteredPerson(userData: UserData, user: ActiveUser): Person = {
    val photo = new Photo(None, None)
    val fullName = userData.firstName + " " + userData.lastName
    val dateStamp = new DateStamp(DateTime.now(), fullName, DateTime.now(), fullName)
    val person = new Person(None, userData.firstName, userData.lastName, userData.email, None, photo,
      None, 0, None, None, webSite = None, blog = None, active = false,
      dateStamp = dateStamp)
    val address = new Address(countryCode = userData.country)
    person.address_=(address)
    person.profile_=(user.person.profile)
    person
  }

  protected def validatePaymentForm(country: String)(implicit request: Request[Any], user: ActiveUser) = {
    paymentForm.bindFromRequest.fold(
      hasError ⇒ Left(badRequest(Json.obj("message" -> Messages("error.payment.unexpected_error")))),
      data ⇒ {
        if (data.fee < Payment.countryBasedFees(country)._1) {
          Left(badRequest(Json.obj("message" -> Messages("error.payment.minimum_fee"))))
        } else {
          data.coupon match {
            case None => Right(data)
            case Some(couponCode) =>
              val couponCheck = repos.core.coupon.find(couponCode) map {
                case None => Left(badRequest(Json.obj("message" -> "Invalid coupon")))
                case Some(coupon) =>
                  if (coupon.valid)
                    Right(data)
                  else
                    Left(badRequest(Json.obj("message" -> "Invalid coupon")))
              }
              Await.result(couponCheck, 5.seconds)
          }
        }
      })
  }

  /**
   * Returns an org created from registration data
    *
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
    *
    * @param person Person
   * @param org Organisation
   */
  private def clean(person: Person, org: Option[Organisation]) = {
    repos.person.delete(person)
    org foreach { x ⇒ repos.org.delete(x.id.get) }
  }
}
