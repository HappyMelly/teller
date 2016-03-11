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
package controllers.webhooks

import javax.inject.Inject

import controllers.{Utilities, AsyncController}
import controllers.community.Members
import models.core.payment.{Charge, Customer, CustomerType, Payment}
import models.repository.Repositories
import models.{Organisation, Person, Member, Recipient}
import play.api.Logger
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.mvc.{Action, Result}
import services.integrations.EmailComponent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Handles events triggered by Stripe
  */
class Stripe @Inject() (val repos: Repositories,
                        val messagesApi: MessagesApi,
                        val email: EmailComponent) extends AsyncController {

  case class EventData(typ: String, customer: String, chargeId: String)

  def event() = Action.async { implicit request =>
    request.body.asJson map { json =>
      // check IPs
      parseEvent(json) match  {
        case Left(data) =>
          data.typ match {
            case "charge.succeeded" => handleSuccessfulCharge(data)
            case "charge.failed" => handleFailedCharge(data)
            case _ => ok("Unsupported event type")
          }
        case Right(error) => ok(error)
      }
    } getOrElse {
      badRequest("Expecting Json data")
    }
  }

  /**
    * Sends notification to a customer that a payment has failed
 *
    * @param data Event data
    */
  protected def handleFailedCharge(data: EventData): Future[Result] =
    repos.core.customer.find(data.customer) flatMap {
      case None => ok("Unknown customer")
      case Some(customer) =>
        if (customer.objectType == CustomerType.Organisation) {
          retrieveOrganisationCustomer(customer, data) { (organisation, member, people) =>
            val msg = "Membership prolongation for organisation %s (id = %s) failed".format(
              organisation.name, organisation.identifier)
            Logger.info(msg)
            sendFailedProlongationEmail(people.head, organisation.name, member)
            ok("")
          }
        } else {
          retrievePersonCustomer(customer, data) { (person, member) =>
            val msg = "Membership prolongation for person %s (id = %s) failed".format(
              person.fullName, person.identifier)
            Logger.info(msg)
            sendFailedProlongationEmail(person, person.fullName, member)
            ok("")
          }
        }
    }

  /**
    * Prolongs membership and sends notification to a customer that a payment has succeed
 *
    * @param data Event data
    */
  protected def handleSuccessfulCharge(data: EventData): Future[Result] =
    repos.core.customer.find(data.customer) flatMap {
      case None => ok("Unknown customer")
      case Some(customer) =>
        if (customer.objectType == CustomerType.Organisation) {
          prolongOrganisationalMembership(customer, data)
        } else {
          prolongPersonalMembership(customer, data)
        }
    }

  /**
    * Retrieve customer-related information if a customer is an organisation
 *
    * @param customer Customer
    * @param data Event data
    * @param f Function to handle customer-related information
    */
  protected def retrieveOrganisationCustomer(customer: Customer, data: EventData)
                                            (f: (Organisation, Member, List[Person]) => Future[Result]): Future[Result] = {
    val request = for {
      o <- repos.org.get(customer.objectId)
      m <- repos.org.member(customer.objectId)
      p <- repos.org.people(customer.objectId)
    } yield (o, m, p)
    request flatMap {
      case (_, None, _) =>
        val msg = s"Database inconsistency. No member record for organisation id = ${customer.objectId}"
        Logger.error(msg)
        ok("Unknown customer")
      case (organisation, Some(member), people) =>
        f(organisation, member, people)
    }
  }

  /**
    * Retrieve customer-related information if a customer is a person
 *
    * @param customer Customer
    * @param data Event data
    * @param f Function to handle customer-related information
    */
  protected def retrievePersonCustomer(customer: Customer, data: EventData)
                                      (f: (Person, Member) => Future[Result]): Future[Result] = {
    val request = for {
      p <- repos.person.get(customer.objectId)
      m <- repos.person.member(customer.objectId)
    } yield (p, m)
    request flatMap {
      case (_, None) =>
        val msg = s"Database inconsistency. No member record for person id = ${customer.objectId}"
        Logger.error(msg)
        ok("Unknown customer")
      case (person, Some(member)) =>
        f(person, member)
    }
  }

  protected def parseEvent(json: JsValue): Either[EventData, String] = {
    if ((json \ "object").asOpt[String].contains("event")) {
      try {
        val typ = (json \ "type").as[String]
        val customer = (((json \ "data") \ "object") \ "customer").as[String]
        val chargeId = (((json \ "data") \ "object") \ "id").as[String]
        Left(EventData(typ, customer, chargeId))
      } catch {
        case e: JsResultException => Right(e.getMessage)
      }
    } else {
      Right("Missing parameter [object] equals to [event]")
    }
  }

  protected def prolongMembership(customerId: Long, chargeId: String, member: Member): Future[Boolean] = {
    val amount = member.fee.getAmount.floatValue()
    repos.core.charge.findByCustomer(customerId) flatMap { charges =>
      if (!charges.exists(_.remoteId == chargeId)) {
        val charge = Charge(None, chargeId, customerId, Payment.DESC, amount, Payment.TAX_PERCENT_AMOUNT)
        repos.core.charge.insert(charge)
      }
      if (validForProlongation(charges))
        repos.member.update(member.copy(until = member.until.plusYears(1))).map(_ => true)
      else
        Future.successful(false)
    }
  }

  protected def prolongOrganisationalMembership(customer: Customer, data: EventData): Future[Result] =
    retrieveOrganisationCustomer(customer, data) { (organisation, member, people) =>
      prolongMembership(customer.id.get, data.chargeId, member) flatMap { prolonged =>
        val msg = "Organisation %s (id = %s) paid membership fee EUR %s".format(
          organisation.name, organisation.identifier, member.fee.getAmount)
        Logger.info(msg)
        if (prolonged)
          sendSuccessfulProlongationEmail(people.head, organisation.name)
        ok("Membership was prolonged")
      }
    }

  protected def prolongPersonalMembership(customer: Customer, data: EventData): Future[Result] =
    retrievePersonCustomer(customer, data) { (person, member) =>
      prolongMembership(customer.id.get, data.chargeId, member) flatMap { prolonged =>
        val msg = "Person %s (id = %s) paid membership fee EUR %s".format(
          person.fullName, person.identifier, member.fee.getAmount)
        Logger.info(msg)
        if (prolonged)
          sendSuccessfulProlongationEmail(person, person.fullName)
        ok("Membership was prolonged")
      }
    }

  protected def sendFailedProlongationEmail(recipient: Recipient, name: String, member: Member) = {
    val subject = "Your Happy Melly Membership"
    val profileUrl = Utilities.fullUrl(Members.profileUrl(member))
    val expireDate = member.until.plusWeeks(2)
    val body = mail.templates.members.html.failedProlongation(name, profileUrl, expireDate).toString
    email.send(Set(recipient), subject = subject, body = body, richMessage = true)
  }

  protected def sendSuccessfulProlongationEmail(recipient: Recipient, name: String) = {
    val subject = "Your Happy Melly Membership"
    val body = mail.templates.members.html.successfulProlongation(name).toString
    email.send(Set(recipient), subject = subject, body = body, richMessage = true)
  }


  protected def validForProlongation(charges: Seq[Charge]): Boolean = charges.nonEmpty
}
