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

import models.database.{ Evaluations, Participants, Events, People }
import models.service.{ PersonService, EventService }
import org.joda.time.{ DateTime, LocalDate }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.cache.Cache
import play.api.Play.current
import play.api.i18n.Messages
import play.api.libs.Crypto
import scala.util.Random
import play.api.libs.concurrent.Execution.Implicits._
import services.EmailService

/**
 * A status of an evaluation which a participant gives to an event
 */
object EvaluationStatus extends Enumeration {
  val Pending = Value("1")
  val Approved = Value("2")
  val Rejected = Value("3")
}

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
  certificate: Option[String],
  created: DateTime,
  createdBy: String,
  updated: DateTime,
  updatedBy: String) {

  lazy val event: Event = EventService.find(eventId).get

  lazy val participant: Person = PersonService.get.find(personId).get

  def create: Evaluation = DB.withSession { implicit session: Session ⇒
    val id = Evaluations.forInsert.insert(this)
    val participant = Participant.find(personId, eventId).get
    participant.copy(evaluationId = Some(id)).update
    val created = this.copy(id = Some(id))

    val brand = Brand.find(event.brandCode).get
    val en = Translation.find("EN").get
    val impression = en.impressions.value(question6)
    val subject = s"New evaluation (General impression: $impression)"
    EmailService.send(event.facilitators.toSet,
      Some(Set(brand.coordinator)), None, subject,
      mail.html.evaluation(created, participant, en).toString(), richMessage = true)

    created
  }

  def delete(): Unit = DB.withSession { implicit session: Session ⇒
    Evaluations.where(_.id === id).mutate(_.delete())
    val participant = Participant.find(personId, eventId).get
    participant.copy(evaluationId = None).update
  }

  def update: Evaluation = DB.withSession { implicit session: Session ⇒
    val updateTuple = (eventId, personId, question1, question2, question3, question4, question5,
      question6, question7, question8, status, handled, certificate, updated, updatedBy)
    val updateQuery = Evaluations.filter(_.id === this.id).map(_.forUpdate)
    updateQuery.update(updateTuple)
    this
  }

  def certificateId: String = handled.map(_.toString("yyMM")).getOrElse("") + f"$personId%03d"

  def approve(approver: Person): Evaluation = {

    val evaluation = this.copy(status = EvaluationStatus.Approved).copy(handled = Some(LocalDate.now)).update

    val brand = Brand.find(evaluation.event.brandCode).get
    if ((evaluation.certificate.isEmpty && brand.brand.generateCert)) {
      val cert = new Certificate(evaluation)
      cert.generateAndSend(brand, approver)
    } else if (evaluation.certificate.isEmpty) {
      val body = mail.html.approvedNoCert(brand.brand, evaluation.participant, approver).toString()
      val subject = s"Your ${brand.brand.name} event's evaluation approval"
      EmailService.send(Set(evaluation.participant), Some(evaluation.event.facilitators.toSet),
        Some(Set(brand.coordinator)), subject, body, richMessage = true, None)
    } else {
      val cert = new Certificate(evaluation, renew = true)
      cert.send(brand, approver)
    }

    evaluation
  }

  def reject(): Evaluation = {
    Certificate.removeFromCloud(this.certificateId)
    this.copy(status = EvaluationStatus.Rejected).copy(handled = Some(LocalDate.now)).copy(certificate = None).update
  }

}

object Evaluation {

  def findByEventAndPerson(personId: Long, eventId: Long) = DB.withSession { implicit session: Session ⇒
    Query(Evaluations).filter(_.personId === personId).filter(_.eventId === eventId).firstOption
  }

  def findByEvent(eventId: Long): List[Evaluation] = DB.withSession { implicit session: Session ⇒
    Query(Evaluations).filter(_.eventId === eventId).list
  }

  def find(id: Long) = DB.withSession { implicit session: Session ⇒
    Query(Evaluations).filter(_.id === id).firstOption
  }

  /**
   * Retrieve the evaluations for a set of events
   * @param eventIds a list of event ids
   * @return
   */
  def findByEvents(eventIds: List[Long]) = DB.withSession { implicit session: Session ⇒
    val baseQuery = for {
      e ← Events if e.id inSet eventIds
      part ← Participants if part.eventId === e.id
      p ← People if p.id === part.personId
      ev ← Evaluations if ev.id === part.evaluationId
    } yield (e, p, ev)
    baseQuery.list
  }

  def findAll: List[Evaluation] = DB.withSession { implicit session: Session ⇒
    Query(Evaluations).sortBy(_.created).list
  }

}
