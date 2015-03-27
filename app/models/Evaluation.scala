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
import services.notifiers.Notifiers

import scala.util.Random

/**
 * A status of an evaluation which a participant gives to an event
 *
 *  - Evaluation is in progress when a participant created it but hasn't validated
 *  - Evaluation is pending when it's approved by a participant but not by
 *    a facilitator
 */
object EvaluationStatus extends Enumeration {
  val Unconfirmed = Value("0")
  val Pending = Value("1")
  val Approved = Value("2")
  val Rejected = Value("3")
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
  updatedBy: String) extends ActivityRecorder with Notifiers with Services {

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
  def approvable: Boolean = status == EvaluationStatus.Pending ||
    status == EvaluationStatus.Rejected

  /**
   * Returns true if the evaluation can be rejected
   */
  def rejectable: Boolean = status == EvaluationStatus.Pending ||
    status == EvaluationStatus.Approved

  def approved: Boolean = status == EvaluationStatus.Approved

  /**
   * Adds new evaluation to database and sends email notification
   * @return Returns an updated evaluation with id
   */
  def add(confirmationUrl: String, withConfirmation: Boolean = false): Evaluation =
    if (withConfirmation) {
      val hash = Random.alphanumeric.take(64).mkString
      evaluationService.
        add(this.copy(status = EvaluationStatus.Unconfirmed, confirmationId = Some(hash))).
        sendConfirmationRequest(confirmationUrl)
    } else {
      evaluationService.
        add(this.copy(status = EvaluationStatus.Pending)).
        sendNewEvaluationNotification()
    }

  def delete(): Unit = DB.withSession { implicit session: Session ⇒
    Evaluations.where(_.id === id).mutate(_.delete())
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
    val brand = Brand.find(event.brandCode).get
    val en = translationService.find("EN").get
    val impression = en.impressions.value(question6)
    val participant = Participant.find(this.personId, this.eventId).get
    val subject = s"New evaluation (General impression: $impression)"
    email.send(event.facilitators.toSet,
      Some(Set(brand.coordinator)), None, subject,
      mail.html.evaluation(this, participant, en).toString(), richMessage = true)

    this
  }

  protected def sendConfirmationRequest(confirmationUrl: String) = {

    val brand = brandService.find(event.brandCode).get
    val en = translationService.find("EN").get
    val participant = personService.find(this.personId).get
    val subject = "Confirm your %s evaluation".format(brand.name)
    val url = confirmationUrl + this.confirmationId.getOrElse("")
    println(url)
    email.send(Set(participant), None, None, subject,
      mail.evaluation.html.confirm(brand, participant.fullName, url).toString(),
      richMessage = true)
    this
  }
}

object Evaluation {

  def findByEventAndPerson(personId: Long, eventId: Long) = DB.withSession {
    implicit session: Session ⇒
      Query(Evaluations).
        filter(_.personId === personId).
        filter(_.eventId === eventId).firstOption
  }

  def findByEvent(eventId: Long): List[Evaluation] = DB.withSession {
    implicit session: Session ⇒
      Query(Evaluations).filter(_.eventId === eventId).list
  }

  def find(id: Long) = DB.withSession { implicit session: Session ⇒
    Query(Evaluations).filter(_.id === id).firstOption
  }

  def findAll: List[Evaluation] = DB.withSession { implicit session: Session ⇒
    Query(Evaluations).sortBy(_.created).list
  }

}
