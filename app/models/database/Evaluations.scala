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
import models.{DateStamp, Evaluation, EvaluationStatus}
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
  def reasonToRegister = column[String]("REASON_TO_REGISTER")
  def actionItems = column[String]("ACTION_ITEMS")
  def changesToContent = column[String]("CHANGES_TO_CONTENT")
  def facilitatorReview = column[String]("FACILITATOR_REVIEW")
  def changesToHost = column[String]("CHANGES_TO_HOST")
  def facilitatorImpression = column[Int]("FACILITATOR_IMPRESSION")
  def recommendationScore = column[Int]("RECOMMENDATION_SCORE")
  def changesToEvent = column[String]("CHANGES_TO_EVENT")
  def contentImpression = column[Option[Int]]("CONTENT_IMPRESSION")
  def hostImpression = column[Option[Int]]("HOST_IMPRESSION")
  def status = column[EvaluationStatus.Value]("STATUS")
  def handled = column[Option[LocalDate]]("HANDLED")
  def confirmationId = column[Option[String]]("CONFIRMATION_ID", O.DBType("CHAR(64)"))

  def created = column[DateTime]("CREATED")
  def createdBy = column[String]("CREATED_BY")
  def updated = column[DateTime]("UPDATED")
  def updatedBy = column[String]("UPDATED_BY")

  type EvaluationFields = (Option[Long], Long, Long, String, String, String,
    String, String, Int, Int, String, Option[Int], Option[Int],
    EvaluationStatus.Value, Option[LocalDate], Option[String], DateTime,
    String, DateTime, String)


  def * = (id.?, eventId, personId, reasonToRegister, actionItems, changesToContent,
    facilitatorReview, changesToHost, facilitatorImpression,
    recommendationScore, changesToEvent, contentImpression, hostImpression,
    status, handled, confirmationId, created, createdBy, updated,
    updatedBy) <> ((e: EvaluationFields) => Evaluation(e._1, e._2, e._3, e._4,
      e._5, e._6, e._7, e._8, e._9, e._10, e._11, e._12, e._13, e._14, e._15,
      e._16, DateStamp(e._17, e._18, e._19, e._20)),
    (e: Evaluation) => Some((e.id, e.eventId, e.personId, e.reasonToRegister,
      e.actionItems, e.changesToContent, e.facilitatorReview, e.changesToHost,
      e.facilitatorImpression, e.recommendationScore, e.changesToEvent,
      e.contentImpression, e.hostImpression, e.status, e.handled, e.confirmationId,
      e.recordInfo.created, e.recordInfo.createdBy,
      e.recordInfo.updated, e.recordInfo.updatedBy)))

  def forUpdate = (eventId, personId, reasonToRegister, actionItems, changesToContent,
    facilitatorReview, changesToHost, facilitatorImpression, recommendationScore,
    changesToEvent, contentImpression, hostImpression, status,
    handled, updated, updatedBy)

}

object Evaluations {

  implicit val evaluationStatusTypeMapper = MappedColumnType.base[EvaluationStatus.Value, Int](
    { status ⇒ status.id }, { id ⇒ EvaluationStatus(id) })
}