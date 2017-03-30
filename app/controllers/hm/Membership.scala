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
package controllers.hm

import javax.inject.{Inject, Named}

import akka.actor.ActorRef
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import controllers._
import models.UserRole.Role._
import models._
import models.core.payment.{Payment, PaymentException, RequestException}
import models.repository.Repositories
import play.api.Play.current
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json._
import play.api.mvc._
import play.api.{Logger, Play}
import services.TellerRuntimeEnvironment
import services.integrations.Email

import scala.concurrent.Future

class Membership @Inject() (override implicit val env: TellerRuntimeEnvironment,
                            override val messagesApi: MessagesApi,
                            val repos: Repositories,
                            val email: Email,
                            @Named("slack-servant") val slackServant: ActorRef,
                            deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders)
  extends Security(deadbolt, handlers, actionBuilder, repos)(messagesApi, env)
  with Enrollment
  with Activities
  with I18nSupport {

  /**
   * Renders welcome screen for existing users with two options:
   * Become a funder and Become a supporter
   */
  def welcome = RestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    (for {
      o <- repos.person.memberships(user.person.identifier)
      m <- repos.member.findByObjects(o.map(_.identifier))
    } yield (o, m.filterNot(_.person))) flatMap { case (orgs, members) =>
      ok(views.html.v2.membership.welcome(user, orgs.filterNot(x => members.exists(_.objectId == x.identifier))))
    }
  }

  /**
   * Renders congratulations screen
   * If orgId is not empty payment is done for the organisation
   *
   * @param orgId Organisation identifier
   */
  def congratulations(orgId: Option[Long]) = RestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      ok(views.html.v2.membership.congratulations(user, orgId))
  }

  /**
   * Renders payment form
   * If orgId is not empty payment is done for the organisation
   *
   * @param orgId Organisation identifier
   */
  def payment(orgId: Option[Long]) = RestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val welcomeCall: Call = routes.Membership.welcome()
      val publicKey = Play.configuration.getString("stripe.public_key").get
      orgId map { id ⇒
        repos.org.find(id) flatMap {
          case None => redirect(welcomeCall, "error" -> Messages("error.organisation.notExist"))
          case Some(org) ⇒
            repos.person.memberships(user.person.identifier) flatMap { orgs =>
              if (orgs.exists(_.id == org.id)) {
                val fee = Payment.countryBasedFees(org.countryCode)
                ok(views.html.v2.membership.payment(user, paymentForm, publicKey, fee, Some(org)))
              } else {
                redirect(welcomeCall, "error" -> Messages("error.person.notOrgMember"))
              }
            }
        }
      } getOrElse {
        val code = user.person.address.countryCode
        val fee = Payment.countryBasedFees(code)
        ok(views.html.v2.membership.payment(user, paymentForm, publicKey, fee))
      }
  }

  /**
   * Charges card
   */
  def charge = RestrictedAction(Viewer) { implicit request ⇒ implicit handler ⇒ implicit user ⇒
    paymentForm.bindFromRequest.fold(
      hasError ⇒ badRequest(Json.obj("message" -> Messages("error.payment.unexpected_error"))),
      data ⇒ {
        try {
          data.orgId map { orgId =>
            repos.org.find(orgId) flatMap {
              case None => badRequest(Json.obj("message" -> "Organisation not found"))
              case Some(org) => processOrganisationMember(data, user, org) flatMap { _ =>
                ok(Json.obj("redirect" -> routes.Membership.congratulations(data.orgId).url))
              }
            }
          } getOrElse {
            processPersonMember(data, user) flatMap { _ =>
              ok(Json.obj("redirect" -> routes.Membership.congratulations(data.orgId).url))
            }
          }

        } catch {
          case e: RuntimeException => handleErrors(e)
        }
      })
  }

  /**
    * Adds new member organisation to the system
    *
    * @param data Payment data
    * @param user Person data
    * @param org Organisation
    */
  protected def processOrganisationMember(data: PaymentData, user: ActiveUser, org: Organisation): Future[Boolean] = {
    validatePaymentData(data, user.person, user.member, Some(org))

    val fee = paidFee(data, org.countryCode, isNewEra)
    val plan = if (isNewEra)
      Some(Payment.stripePlanId(org.countryCode, data.yearly))
    else
      None

    payMembership(user.person, Some(org), data)
    (for {
      m <- org.becomeMember(funder = false, fee, plan, data.yearly, user.person.identifier, repos)
    } yield m) map { member =>
      notify(user.person, Some(org), member)
      subscribe(user.person, member)

      activity(member, user.person).becameSupporter.insert(repos)
      true
    }
  }

  /**
    * Adds new member person to the system
    *
    * @param data Payment data
    * @param user Person data
    */
  protected def processPersonMember(data: PaymentData, user: ActiveUser)(
      implicit request: Request[AnyContent]): Future[Boolean] = {
    val person = user.person
    validatePaymentData(data, person, user.member, None)

    val fee = paidFee(data, person.address.countryCode, isNewEra)
    val plan = if (isNewEra)
      Some(Payment.stripePlanId(person.address.countryCode, data.yearly))
    else
      None

    payMembership(person, None, data)
    (for {
      p <- repos.socialProfile.find(person.identifier, ProfileType.Person)
      m <- person.becomeMember(funder = false, fee, plan, data.yearly, repos)
    } yield (p, m)) map { case (profile, member) =>
      env.updateCurrentUser(user.copy(member = Some(member)))
      person.profile_=(profile)
      notify(person, None, member)
      subscribe(person, member)

      activity(member, user.person).becameSupporter.insert(repos)
      true
    }
  }

  protected def handleErrors(e: RuntimeException) = e match {
    case e: PaymentException ⇒
      val error = e.code match {
        case "card_declined" ⇒ "error.payment.card_declined"
        case "incorrect_cvc" ⇒ "error.payment.incorrect_cvc"
        case "expired_card" ⇒ "error.payment.expired_card"
        case "processing_error" ⇒ "error.payment.processing_error"
        case _ ⇒ "error.payment.unexpected_error"
      }
      badRequest(Json.obj("message" -> Messages(error)))
    case e: RequestException ⇒
      e.log.foreach(Logger.error(_))
      badRequest(Json.obj("message" -> Messages(e.getMessage)))
    case e: Membership.ValidationException ⇒
      badRequest(Json.obj("message" -> Messages(e.getMessage)))
  }

  /**
   * Validates payments data
    *
    * @param data Data from payment form
   * @param person Current user
   * @param organisation Organisation which wants to become a member
   */
  protected def validatePaymentData(data: PaymentData,
    person: Person,
    member: Option[Member],
    organisation: Option[Organisation]) = {
    data.orgId foreach { orgId ⇒
      if (organisation.isEmpty) {
        throw new Membership.ValidationException("error.organisation.notExist")
      }
      if (data.fee < Payment.countryBasedFees(organisation.get.countryCode)._1) {
        throw new Membership.ValidationException("error.payment.minimum_fee")
      }
      if (member.nonEmpty) {
        throw new Membership.ValidationException("error.organisation.member")
      }
      if (!person.organisations(repos).exists(_.id == Some(orgId))) {
        throw new Membership.ValidationException("error.person.notOrgMember")
      }
    }
    if (organisation.isEmpty) {
      if (data.fee < Payment.countryBasedFees(person.address.countryCode)._1) {
        throw new Membership.ValidationException("error.payment.minimum_fee")
      }
      if (member.nonEmpty) {
        throw new Membership.ValidationException("error.person.member")
      }
    }
  }
}

object Membership {

  class ValidationException(msg: String) extends RuntimeException(msg) {}
}