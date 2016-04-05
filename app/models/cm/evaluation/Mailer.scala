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
package models.cm.evaluation

import javax.inject.Inject

import akka.actor.Actor
import controllers.Utilities
import models.cm.event.Attendee
import models.cm.{CertificateFile, Evaluation, Event}
import models.repository.IRepositories
import models.{Brand, Person}
import play.api.i18n.{I18nSupport, MessagesApi}
import services.integrations.EmailComponent
import templates.Formatters._

import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Responsible for sending all evaluation-related emails
  */
class Mailer @Inject() (val email: EmailComponent,
                        val repos: IRepositories,
                        val messagesApi: MessagesApi) extends Actor with I18nSupport {

  def receive = {
    case ("approve", approver: Person, attendee: Attendee, brand: Brand, event: Event, file: CertificateFile) =>
      sendApproval(approver, attendee, brand, event, file)
    case ("approve", approver: Person, attendee: Attendee, brand: Brand, event: Event) =>
      sendApprovalWithoutCertificate(approver, attendee, brand, event)
    case ("confirm", evaluation: Evaluation) => sendConfirmation(evaluation)
    case ("confirm", attendee: Attendee, brand: Brand, hook: String) => sendConfirmation(attendee, brand, hook)
    case ("new", evaluation: Evaluation) => newEvaluation(evaluation)
    case ("reject", rejector: Person, attendee: Attendee, event: Event) => sendRejection(rejector, attendee, event)
    case ("request", attendee: Attendee, brand: Brand, body: String) =>
      sendRequest(attendee: Attendee, brand: Brand, body: String)
  }

  /**
    * Return content for approval email
    * @param event Event
    * @param attendee Attendee
    * @param default Default content
    * @return
    */
  protected def approvalEmail(event: Event, attendee: Attendee, brand: Brand, default: String): String = {
    val TOKEN = "{{ATTENDEE_NAME}}"
    event.postEventTemplate match {
      case None => default
      case Some(template) =>
        val content = mail.html.tmpl(s"Your ${brand.name} certificate", controllers.Brands.pictureUrl(brand)) {
          template.replace(TOKEN, attendee.firstName).markdown
        }
        content.toString
    }
  }

  /**
    * Sends confirmation email that evaluation was approved
    *
    * @param approver Person who approved the given evaluation
    * @param attendee Attendee
    * @param event Related event
    */
  protected def sendApproval(approver: Person, attendee: Attendee, brand: Brand, event: Event, file: CertificateFile) = {
    repos.cm.brand.coordinators(event.brandId) map { coordinators =>
      val bcc = coordinators.filter(_._2.notification.evaluation).map(_._1)
      val default = mail.evaluation.html.approved(brand, attendee, approver).toString()
      val body = approvalEmail(event, attendee, brand, default)
      val subject = s"Your ${brand.name} certificate"
      email.send(Seq(attendee), event.facilitators(repos), bcc,
        subject, body, brand.sender, attachment = Some((file.path, file.name)))
    }
  }

  /**
    * Sends confirmation email that evaluation was approved
    *
    * @param approver Person who approved the given evaluation
    * @param attendee Attendee
    * @param event Related event
    */
  protected def sendApprovalWithoutCertificate(approver: Person, attendee: Attendee, brand: Brand, event: Event) = {
    repos.cm.brand.coordinators(event.brandId) map { coordinators =>
      val bcc = coordinators.filter(_._2.notification.evaluation).map(_._1)
      val default = mail.evaluation.html.approvedNoCert(brand, attendee, approver).toString()
      val body = approvalEmail(event, attendee, brand, default)
      val subject = s"Your ${brand.name} event's evaluation approval"
      email.send(Seq(attendee), event.facilitators(repos), bcc,
        subject, body, from = brand.sender, richMessage = true)
    }
  }

  /**
   * Sends request to confirm an evaluation to the given attendee
   *
   * @param attendee Attendee
   * @param brand Brand
   * @param token Confirmation token
   */
  protected def sendConfirmation(attendee: Attendee, brand: Brand, token: String): Unit = {
    val url = controllers.cm.Evaluations.confirmationUrl(token)
    val subject = "Confirm your %s evaluation" format brand.name
    val content = mail.evaluation.html.confirm(brand, attendee.fullName, url).toString()
    email.send(Seq(attendee), subject, content, brand.sender)
  }

  /**
    * Sends confirmation request to the attendee of the given evaluation
    *
    * @param evaluation Evaluation
    */
  protected def sendConfirmation(evaluation: Evaluation): Unit = {
    (for {
      event <- repos.cm.event.get(evaluation.eventId)
      brand <- repos.cm.brand.get(event.brandId)
      attendee <- repos.cm.rep.event.attendee.find(evaluation.attendeeId, evaluation.eventId)
    } yield (brand, attendee)) map {
      case (_, None) => Unit
      case (brand, Some(attendee)) =>
        val token = evaluation.confirmationId getOrElse ""
        sendConfirmation(attendee, brand, token)
    }

  }

  protected def newEvaluation(evaluation: Evaluation): Unit = {
    (for {
      event <- repos.cm.event.get(evaluation.eventId)
      brand <- repos.cm.brand.get(event.brandId)
      attendee <- repos.cm.rep.event.attendee.find(evaluation.attendeeId, evaluation.eventId)
      coordinators <- repos.cm.brand.coordinators(event.brandId)
    } yield (event, brand, attendee, coordinators)) map {
      case (event, _, None, _) => this
      case (event, brand, Some(attendee), coordinators) =>
        val cc = coordinators.filter(_._2.notification.evaluation).map(_._1)
        val to = event.facilitators(repos)

        val impression = views.Evaluations.impression(evaluation.facilitatorImpression)
        val subject = s"New evaluation (General impression: $impression)"
        val url = Utilities.fullUrl(controllers.cm.routes.Evaluations.details(evaluation.identifier).url)
        val body = mail.evaluation.html.details(evaluation, event, attendee, brand, url)
        email.send(to, cc, Seq(), subject, body.toString(), brand.sender)
    }

  }

  /**
    * Sends email about rejected evaluation to the given attendee
    *
    * @param rejector Person who rejected evaluation
    * @param attendee Attendee
    * @param event Related event
    */
  protected def sendRejection(rejector: Person, attendee: Attendee, event: Event): Unit = {
    repos.cm.brand.findWithCoordinators(event.brandId).filter(_.isDefined).map(_.get) foreach { view ⇒
      val bcc = view.coordinators.filter(_._2.notification.evaluation).map(_._1)
      val subject = s"Your ${view.brand.name} certificate"
      val body = mail.evaluation.html.rejected(view.brand, attendee, rejector).toString()
      email.send(Seq(attendee), event.facilitators(repos), bcc, subject, body, view.brand.sender)
    }
  }

  /**
    * Sends request to evaluate an event to the given attendee
    */
  protected def sendRequest(attendee: Attendee, brand: Brand, body: String): Unit = {
    val subject = "Your Opinion Counts!"
    val content = mail.evaluation.html.request(brand, attendee, body).toString()
    email.send(Seq(attendee), subject, content, brand.sender)
  }

}
