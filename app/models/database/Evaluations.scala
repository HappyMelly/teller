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

package models.database

import models.database.PortableJodaSupport._
import models.{ Evaluation, EvaluationStatus }
import org.joda.time.{ DateTime, LocalDate }
import play.api.db.slick.Config.driver.simple._

/**
 * `Evaluation` database table mapping.
 */
private[models] class Evaluations(tag: Tag) extends Table[Evaluation](tag, "EVALUATION") {

  implicit val evaluationStatusTypeMapper = MappedColumnType.base[EvaluationStatus.Value, Int](
    { status ⇒ status.id },
    { id ⇒ EvaluationStatus(id) })

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def eventId = column[Long]("EVENT_ID")
  def personId = column[Long]("PERSON_ID")
  def question1 = column[String]("QUESTION_1")
  def question2 = column[String]("QUESTION_2")
  def question3 = column[String]("QUESTION_3")
  def question4 = column[String]("QUESTION_4")
  def question5 = column[String]("QUESTION_5")
  def question6 = column[Int]("QUESTION_6")
  def question7 = column[Int]("QUESTION_7")
  def question8 = column[String]("QUESTION_8")
  def status = column[EvaluationStatus.Value]("STATUS")
  def handled = column[Option[LocalDate]]("HANDLED")
  def confirmationId = column[Option[String]]("CONFIRMATION_ID", O.DBType("CHAR(64)"))
  def created = column[DateTime]("CREATED")
  def createdBy = column[String]("CREATED_BY")

  def updated = column[DateTime]("UPDATED")
  def updatedBy = column[String]("UPDATED_BY")

  def * = (id.?, eventId, personId, question1, question2, question3,
    question4, question5, question6, question7, question8, status,
    handled, confirmationId, created, createdBy, updated,
    updatedBy) <> ((Evaluation.apply _).tupled, Evaluation.unapply)

  def forUpdate = (eventId, personId, question1, question2, question3,
    question4, question5, question6, question7, question8, status,
    handled, updated, updatedBy)

}

object Evaluations {

  implicit val evaluationStatusTypeMapper = MappedColumnType.base[EvaluationStatus.Value, Int](
    { status ⇒ status.id }, { id ⇒ EvaluationStatus(id) })
}