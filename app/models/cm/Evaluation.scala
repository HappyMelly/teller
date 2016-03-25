/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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

package models.cm

import controllers.Utilities
import cron.reminders.EvaluationReminder
import models.cm.event.Attendee
import models.{Activity, ActivityRecorder, DateStamp}
import models.repository._
import org.joda.time.LocalDate
import play.api.i18n.Messages
import services.integrations.EmailComponent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

/**
 * A status of an evaluation which a participant gives to an event
 *
 *  - Evaluation is in progress when a participant created it but hasn't validated
 *  - Evaluation is pending when it's approved by a participant but not by
 *    a facilitator
 */
object EvaluationStatus extends Enumeration {
  val Pending = Value("0")
  val Approved = Value("1")
  val Rejected = Value("2")
  val Unconfirmed = Value("3")
}

/**
 * In most cases event data is required along with evaluation data. To decrease
 * a number of requests to database some requests return evalution with companion
 * objects
 *
 * @param eval Evaluation
 * @param event Related event
 */
case class EvaluationEventView(eval: Evaluation, event: Event)

/**
 * Represents an evaluation with the related participant
  *
  * @param evaluation Evaluation
 * @param attendee Participant
 */
case class EvaluationAttendeeView(evaluation: Evaluation, attendee: Attendee)

/**
 * An evaluation which a participant gives to an event
 */
case class  Evaluation(id: Option[Long],
                       eventId: Long,
                       attendeeId: Long,
                       reasonToRegister: String,
                       actionItems: String,
                       changesToContent: String,
                       facilitatorReview: String,
                       changesToHost: String,
                       facilitatorImpression: Int,
                       recommendationScore: Int,
                       changesToEvent: String,
                       contentImpression: Option[Int],
                       hostImpression: Option[Int],
                       status: EvaluationStatus.Value,
                       handled: Option[LocalDate],
                       confirmationId: Option[String],
                       recordInfo: DateStamp) extends ActivityRecorder {

  val impression: Int = facilitatorImpression

  /**
   * Returns identifier of the object
   */
  def identifier: Long = id.getOrElse(0)

  /**
   * Returns string identifier which can be understood by human
   *
   * For example, for object 'Person' human identifier is "[FirstName] [LastName]"
   */
  def humanIdentifier: String = "to event (id = %s) for person (id = %s)".format(eventId, attendeeId)

  /**
   * Returns type of this object
   */
  def objectType: String = Activity.Type.Evaluation

  /**
   * Returns true if the evaluation can be approved
   */
  def approvable: Boolean = Evaluation.approvable(status)

  /**
   * Returns true if the evaluation can be rejected
   */
  def rejectable: Boolean = Evaluation.rejectable(status)

  def approved: Boolean = status == EvaluationStatus.Approved

  def rejected: Boolean = status == EvaluationStatus.Rejected

  /**
   * Adds new evaluation to database and sends email notification
    *
   * @param withConfirmation If true, the evaluation should be confirmed first by the participant
   * @return Returns an updated evaluation with id
   */
  def add(withConfirmation: Boolean = false, email: EmailComponent, repos: Repositories)(implicit messages: Messages): Future[Evaluation] =
    if (withConfirmation) {
      val hash = Random.alphanumeric.take(64).mkString
      val confirmed = this.copy(status = EvaluationStatus.Unconfirmed, confirmationId = Some(hash))
      repos.cm.evaluation.add(confirmed) flatMap { evaluation =>
        evaluation.sendConfirmationRequest(email, repos)
      }
    } else {
      repos.cm.evaluation.add(this.copy(status = EvaluationStatus.Pending)) flatMap { evaluation =>
        evaluation.sendNewEvaluationNotification(email, repos, messages)
      }
    }

  /**
   * Updates the evaluation
   */
  def update(services: Repositories): Future[Evaluation] = services.cm.evaluation.update(this)

  def approve(services: Repositories): Future[Evaluation] = {
    this.
      copy(status = EvaluationStatus.Approved).
      copy(handled = Some(LocalDate.now)).update(services)
  }

  /**
   * Sets the evaluation to a Rejected state
   */
  def reject(services: Repositories): Future[Evaluation] = {
    this
      .copy(status = EvaluationStatus.Rejected)
      .copy(handled = Some(LocalDate.now)).update(services)
  }

  /**
   * Returns approved/rejected evaluation with the same impression and
   * a participant of the same name
   */
  def identical(repos: Repositories): Future[Option[Evaluation]] = {
    repos.cm.evaluation.findByEventsWithAttendees(List(this.eventId)) map { evaluations =>
      evaluations.find(_._3.identifier == this.identifier).map { view =>
        evaluations.
          filter(x => x._3.approved || x._3.rejected).
          filter(_._3.impression == this.impression).
          find(x => x._2.firstName.toLowerCase == view._2.firstName.toLowerCase &&
          x._2.lastName.toLowerCase == view._2.lastName.toLowerCase).
          flatMap(x => Some(x._3))
      } getOrElse None
    }
  }

  /**
   * Sets the evaluation to Pending state and returns the updated evaluation
   */
  def confirm(email: EmailComponent, services: Repositories)(implicit messages: Messages): Future[Evaluation] = {
    this.copy(status = EvaluationStatus.Pending).update(services) flatMap { evaluation =>
      evaluation.sendNewEvaluationNotification(email, services, messages)
    }
  }

  /**
   * Sends a confirmation request to the participant
   */
  def sendConfirmationRequest(email: EmailComponent, repos: Repositories): Future[Evaluation] = {
    (for {
      event <- repos.cm.event.get(eventId)
      brand <- repos.cm.brand.get(event.brandId)
      attendee <- repos.cm.rep.event.attendee.find(this.attendeeId, this.eventId)
    } yield (brand, attendee)) map {
      case (_, None) => this
      case (brand, Some(attendee)) =>
        val token = this.confirmationId getOrElse ""
        val url = controllers.Evaluations.confirmationUrl(token)
        (new EvaluationReminder(email, repos)).sendConfirmRequest(attendee, brand, url)
        this
    }
  }

  protected def sendNewEvaluationNotification(email: EmailComponent, repos: Repositories, messages: Messages) = {
    (for {
      event <- repos.cm.event.get(eventId)
      brand <- repos.cm.brand.get(event.brandId)
      attendee <- repos.cm.rep.event.attendee.find(this.attendeeId, this.eventId)
      coordinators <- repos.cm.brand.coordinators(event.brandId)
    } yield (event, brand, attendee, coordinators)) map {
      case (event, _, None, _) => this
      case (event, brand, Some(attendee), coordinators) =>
        val impression = views.Evaluations.impression(facilitatorImpression)
        val subject = s"New evaluation (General impression: $impression)"
        val cc = coordinators.filter(_._2.notification.evaluation).map(_._1)
        val url = Utilities.fullUrl(controllers.routes.Evaluations.details(this.identifier).url)
        val body = mail.evaluation.html.details(this, event, attendee, brand, url)(messages)
        email.send(event.facilitators(repos), cc, Seq(), subject, body.toString(), brand.sender)

        this
    }
  }

}

object Evaluation {

  /**
   * Returns true if the evaluation can be approved
    *
    * @param status Status of the evaluation
   */
  def approvable(status: EvaluationStatus.Value): Boolean =
    status == EvaluationStatus.Pending || status == EvaluationStatus.Rejected

  /**
   * Returns true if the evaluation can be rejected
    *
    * @param status Status of the evaluation
   */
  def rejectable(status: EvaluationStatus.Value): Boolean =
    status == EvaluationStatus.Pending || status == EvaluationStatus.Approved

}
