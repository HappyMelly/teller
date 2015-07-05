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
import models.Participant
import org.joda.time.LocalDate
import play.api.db.slick.Config.driver.simple._

/**
 * `Participant` database table mapping.
 */
private[models] class Participants(tag: Tag) extends Table[Participant](tag, "EVENT_PARTICIPANT") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def eventId = column[Long]("EVENT_ID")
  def personId = column[Long]("PERSON_ID")
  def evaluationId = column[Option[Long]]("EVALUATION_ID")
  def certificate = column[Option[String]]("CERTIFICATE")
  def issued = column[Option[LocalDate]]("ISSUED")
  def organisation = column[Option[String]]("ORGANISATION")
  def comment = column[Option[String]]("COMMENT")

  def event = foreignKey("EVENT_FK", eventId, TableQuery[Events])(_.id)
  def participant = foreignKey("PARTICIPANT_FK", personId, TableQuery[People])(_.id)

  def * = (id.?, eventId, personId, evaluationId, certificate, issued,
    organisation, comment) <> (
      (Participant.apply _).tupled, Participant.unapply)

  def forUpdate = (eventId, evaluationId, certificate, issued)
}
