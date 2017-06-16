/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2017, Happy Melly http://www.happymelly.com
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

import javax.inject.Named

import akka.actor.ActorRef
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers.hm.Enrollment
import models._
import models.core.payment.{Payment, PaymentException, RequestException}
import models.repository.Repositories
import org.joda.time.{DateTime, LocalDate}
import play.api.Play.current
import play.api.cache.Cache
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Request, RequestHeader, Result}
import play.api.{Logger, Play}
import services.TellerRuntimeEnvironment
import services.integrations.Email
import views.Countries

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

case class TrialMemberData(firstName: String, lastName: String, email: String, country: String)

/**
  * Manages actions, related to a creation of trial members
  */
class TrialMembership @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment,
                                              override val messagesApi: MessagesApi,
                                              val repos: Repositories,
                                              val email: Email,
                                              @Named("slack-servant") val slackServant: ActorRef,
                                              deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env)
    with Enrollment
    with PasswordIdentities
    with Activities {

  protected val TRIAL_LENGTH_IN_MONTHS = 3

  private def membersForm = Form(tuple(
    "coupon" → nonEmptyText,
    "members" → seq(mapping(
      "first_name" → nonEmptyText,
      "last_name" -> nonEmptyText,
      "email" -> play.api.data.Forms.email.verifying("Email address is already in use", { suppliedEmail =>
        import scala.concurrent.duration._
        Await.result(repos.identity.checkEmail(suppliedEmail), 10.seconds)
      }),
      "country" -> nonEmptyText.verifying(
        "error.unknown_country",
        (value: String) ⇒ Countries.all.exists(_._1 == value))
    )(TrialMemberData.apply)(TrialMemberData.unapply))
    ))

  def trialPaymentForm = Form(tuple(
    "token" -> nonEmptyText,
    "coupon" -> nonEmptyText))

  /**
    * Renders trial page with coupon and new members form
    */
  def step1 = Action.async { implicit request ⇒
    ok(views.html.v2.trialMembership.step1(membersForm))
  }

  /**
    * Renders Payment page of the registration process
    */
  def payment = Action.async { implicit request ⇒
    val form = membersForm.bindFromRequest()
    form.fold(
      errors ⇒ {
        badRequest(views.html.v2.trialMembership.step1(errors))
      },
      { case (coupon, members) ⇒
        Cache.set(couponId(coupon), (coupon, members))
        val publicKey = Play.configuration.getString("stripe.public_key").get
        val price = calculatePrice(members)
        ok(views.html.v2.trialMembership.payment(members, publicKey, coupon, price))
      }
    )
  }

  /**
    * Charges the card and creates new trial member accounts
    */
  def charge = Action.async { implicit request ⇒
    validatePaymentForm() match {
      case Left(result) ⇒ result
      case Right((token, coupon)) ⇒
        isCachedDataAvailable(coupon.code) { members ⇒
          val amount = calculatePrice(members)
          try {
            payMembership(token, amount, coupon.email, members)
            val requests = members.map { memberData ⇒
              addPersonWithTrialMembership(memberData, coupon.owner)
            }
            Future.sequence(requests) flatMap { _ ⇒
              Cache.remove(couponId(coupon.code))
              ok(Json.obj("redirect" -> controllers.routes.TrialMembership.congratulations(coupon.code).url))
            }
          } catch {
            case e: PaymentException ⇒
              badRequest(Json.obj("message" -> Messages(e.getMessage)))
            case e: RequestException ⇒
              e.log.foreach(Logger.error(_))
              badRequest(Json.obj("message" -> Messages(e.getMessage)))
          }
        }
    }
  }

  /**
    * Renders congratulations screen
    */
  def congratulations(code: String) = Action.async { implicit request ⇒
    repos.core.trialCoupon.find(code) flatMap {
      case None ⇒ badRequest("Requested coupon wasn't found")
      case Some(coupon) ⇒
        ok(views.html.v2.trialMembership.congratulations(coupon))
    }
  }

  /**
    * Adds a new person with a trial membership
    * @param data Member's data
    * @param sponsor Name of the sponsor
    */
  protected def addPersonWithTrialMembership(data: TrialMemberData, sponsor: String)
                                            (implicit rh: RequestHeader): Future[_] = {
    val newPerson = Person(data.firstName, data.lastName, data.email)
    newPerson.address_=(Address(countryCode = data.country))
    newPerson.profile_=(SocialProfile())
    val fee = Payment.countryBasedPlans(data.country)._1
    val request = for {
      p ← repos.person.insert(newPerson)
      m ← addTrialMember(p)
      t ← createUserAccount(p)
    } yield (p, m, t)
    request map { case (person, member, mailToken) ⇒
      sendPasswordEmail(person, sponsor, mailToken)
      notify(person, None, member)
      subscribe(person, member)
    }
  }

  /**
    * Adds trial member record and updates profile strength for members for the given person
    * @param person Person
    * @return New member's record
    */
  protected def addTrialMember(person: Person): Future[Member] = {
    val today = LocalDate.now()
    val fee = Payment.countryBasedPlans(person.address.countryCode)._1
    val member = Member(None, person.identifier, person = true,
      funder = false, fee = BigDecimal(fee.toDouble), renewal = false, since = today,
      until = today.plusMonths(TRIAL_LENGTH_IN_MONTHS),
      created = DateTime.now(), createdBy = person.identifier,
      updated = DateTime.now(), updatedBy = person.identifier)
    for {
      m ← repos.member.insert(member)
      _ ← repos.profileStrength.find(person.identifier, false).filter(_.isDefined) map { x ⇒
        repos.profileStrength.update(ProfileStrength.forMember(x.get))
      }
    } yield m
  }

  /**
    * Returns trial price for all given members
    * @param members New member
    */
  protected def calculatePrice(members: Seq[TrialMemberData]): Float =
    members.map(x ⇒ Payment.countryBasedPlans(x.country)._1 * TRIAL_LENGTH_IN_MONTHS).sum

  /**
    * Returns an unique cache id for a coupon
    *
    * @param coupon Coupon identifier
    */
  protected def couponId(coupon: String): String = "coupon_" + coupon

  /**
    * Creates a new account for the given person and returns a mail token to create a new password
    *
    * @param person Person
    */
  protected def createUserAccount(person: Person)(implicit request: RequestHeader): Future[String] = {
    val account = UserAccount(None, person.identifier, byEmail = true,
      None, None, None, None, member = true, registered = true)

    val req = for {
      account ← repos.userAccount.insert(account)
      token ← createToken(person.email, isSignUp = false)
    } yield token
    req map { token ⇒
      setupLoginByEmailEnvironment(person, token)
      token.uuid
    }
  }

  /**
    * Checks if person data are in cache and redirects to a person data form if not
    */
  protected def isCachedDataAvailable(coupon: String)(f: Seq[TrialMemberData] ⇒ Future[Result])
                                     (implicit request: Request[Any]): Future[Result] = {
    Cache.getAs[(String, Seq[TrialMemberData])](couponId(coupon)) map { data ⇒
      f(data._2)
    } getOrElse {
      redirect(controllers.routes.TrialMembership.step1(), "error" -> "Members data were not found. Please, try again.")
    }
  }


  /**
    * Makes a payment through the payment gateway for a set of new trial members
    *
    * @param token Card token
    * @param amount Amount
    * @param email Payer email for the receipt
    * @param members New trial members
    * @return Returns customer identifier in the payment system and credit card info
    */
  protected def payMembership(token: String, amount: Float, email: String, members: Seq[TrialMemberData]): Unit = {
    val desc = "Trial membership for " + members.map(x ⇒ x.firstName + " " + x.lastName).mkString(",")
    val key = Play.configuration.getString("stripe.secret_key").get
    val payment = new Payment(key)
    payment.pay(token, amount, email, desc)
  }

  /**
    * Sends a create new password email
    *
    * @param person Person
    * @param token Unique token for password creation
    */
  protected def sendPasswordEmail(person: Person, sponsor: String, token: String)(implicit request: RequestHeader) = {
    env.mailer.sendEmail(s" Password reset required to start using your 3-month membership gift",
      person.email,
      (None, Some(mail.members.html.trial(person.firstName, sponsor, token)))
    )
  }

  /**
    * Checks the given payment data
    */
  protected def validatePaymentForm()(implicit request: Request[Any]) = {
    trialPaymentForm.bindFromRequest.fold(
      hasError ⇒ Left(badRequest(Json.obj("message" -> Messages("error.payment.unexpected_error")))),
      { case (token, couponCode) ⇒
          val couponCheck = repos.core.trialCoupon.find(couponCode) map {
            case None ⇒ Left(badRequest(Json.obj("message" -> "Invalid coupon")))
            case Some(coupon) ⇒ Right((token, coupon))
          }
          Await.result(couponCheck, 5.seconds)
      })
  }
}
