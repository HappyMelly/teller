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

package models

import models.database.Evaluations
import models.service._
import org.joda.time.{ DateTime, LocalDate }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import services.integrations.Integrations

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
case class EvaluationPair(eval: Evaluation, event: Event)

/**
 * An evaluation which a participant gives to an event
 */
case class Evaluation(
    id: Option[Long],
    eventId: Long,
    personId: Long,
    question1: String,
    question2: String,
    question3: String,
    question4: String,
    question5: String,
    question6: Int,
    question7: Int,
    question8: String,
    status: EvaluationStatus.Value,
    handled: Option[LocalDate],
    confirmationId: Option[String],
    created: DateTime,
    createdBy: String,
    updated: DateTime,
    updatedBy: String) extends ActivityRecorder with Integrations with Services {

  lazy val event: Event = EventService.get.find(eventId).get

  lazy val participant: Person = PersonService.get.find(personId).get

  /**
   * Returns identifier of the object
   */
  def identifier: Long = id.getOrElse(0)

  /**
   * Returns string identifier which can be understood by human
   *
   * For example, for object 'Person' human identifier is "[FirstName] [LastName]"
   */
  def humanIdentifier: String = "to event (id = %s) for person (id = %s)".format(eventId, personId)

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

  /**
   * Adds new evaluation to database and sends email notification
   * @param defaultHook Link to a default confirmation page
   * @param withConfirmation If true, the evaluation should be confirmed first by the participant
   * @return Returns an updated evaluation with id
   */
  def add(defaultHook: String, withConfirmation: Boolean = false): Evaluation =
    if (withConfirmation) {
      val hash = Random.alphanumeric.take(64).mkString
      evaluationService.
        add(this.copy(status = EvaluationStatus.Unconfirmed, confirmationId = Some(hash))).
        sendConfirmationRequest(defaultHook)
    } else {
      evaluationService.
        add(this.copy(status = EvaluationStatus.Pending)).
        sendNewEvaluationNotification()
    }

  /**
   * @TEST
   */
  def delete(): Unit = DB.withSession { implicit session ⇒
    TableQuery[Evaluations].filter(_.id === id).delete
    val participant = Participant.find(personId, eventId).get
    participant.copy(evaluationId = None).update
  }

  /**
   * Updates the evaluation
   */
  def update(): Evaluation = evaluationService.update(this)

  def approve: Evaluation = {
    this.
      copy(status = EvaluationStatus.Approved).
      copy(handled = Some(LocalDate.now)).update()
  }

  /**
   * Sets the evaluation to a Rejected state
   */
  def reject(): Evaluation = {
    this
      .copy(status = EvaluationStatus.Rejected)
      .copy(handled = Some(LocalDate.now)).update()
  }

  /**
   * Sets the evaluation to Pending state and returns the updated evaluation
   */
  def confirm(): Evaluation =
    this.
      copy(status = EvaluationStatus.Pending).
      update().
      sendNewEvaluationNotification()

  protected def sendNewEvaluationNotification() = {
    val brand = brandService.findWithCoordinators(event.brandId).get
    val en = translationService.find("EN").get
    val impression = en.impressions.value(question6)
    val participant = Participant.find(this.personId, this.eventId).get
    val subject = s"New evaluation (General impression: $impression)"
    val cc = brand.coordinators.filter(_._2.notification.evaluation).map(_._1)
    email.send(event.facilitators.toSet,
      Some(cc.toSet), None, subject,
      mail.html.evaluation(this, participant, en).toString(), richMessage = true)

    this
  }

  /**
   * Sends a confirmation request to the participant
   * @param defaultHook Link to a default confirmation page
   * @return Returns the evaluation
   */
  protected def sendConfirmationRequest(defaultHook: String) = {
    val brand = brandService.find(event.brandId).get
    val participant = personService.find(this.personId).get
    val subject = "Confirm your %s evaluation" format brand.name
    val url = brand.evaluationHookUrl.
      map(x ⇒ if (x.endsWith("/")) x else x + "/").
      getOrElse("https://" + defaultHook).
      concat(this.confirmationId getOrElse "")
    email.send(Set(participant), None, None, subject,
      mail.evaluation.html.confirm(brand, participant.fullName, url).toString(),
      richMessage = true)
    this
  }
}

object Evaluation {

  /**
   * Returns true if the evaluation can be approved
   * @param status Status of the evaluation
   */
  def approvable(status: EvaluationStatus.Value): Boolean =
    status == EvaluationStatus.Pending || status == EvaluationStatus.Rejected

  /**
   * Returns true if the evaluation can be rejected
   * @param status Status of the evaluation
   */
  def rejectable(status: EvaluationStatus.Value): Boolean =
    status == EvaluationStatus.Pending || status == EvaluationStatus.Approved

  /**
   * @TEST
   * @param personId
   * @param eventId
   * @return
   */
  def findByEventAndPerson(personId: Long, eventId: Long) = DB.withSession {
    implicit session ⇒
      TableQuery[Evaluations].
        filter(_.personId === personId).
        filter(_.eventId === eventId).firstOption
  }

  def find(id: Long) = DB.withSession { implicit session ⇒
    TableQuery[Evaluations].filter(_.id === id).firstOption
  }

  def findAll: List[Evaluation] = DB.withSession { implicit session ⇒
    TableQuery[Evaluations].sortBy(_.created).list
  }

}
