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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing
 * Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models.cm.event

import models.cm.{Evaluation, Event}
import models.{Activity, ActivityRecorder, DateStamp, Recipient}
import org.joda.time.LocalDate

/**
  * Represents an attendee of event
  */
case class Attendee(
  id: Option[Long],
  eventId: Long,
  personId: Option[Long],
  firstName: String,
  lastName: String,
  override val email: String,
  dateOfBirth: Option[LocalDate],
  countryCode: Option[String],
  city: Option[String],
  street_1: Option[String],
  street_2: Option[String],
  province: Option[String],
  postcode: Option[String],
  evaluationId: Option[Long],
  certificate: Option[String],
  issued: Option[LocalDate],
  organisation: Option[String],
  comment: Option[String],
  role: Option[String],
  optOut: Boolean = false,
  recordInfo: DateStamp) extends Recipient with ActivityRecorder {

  def fullName: String = firstName + " " + lastName

  def identifier: Long = id.getOrElse(0)

  /**
    * Returns string identifier which can be understood by human
    *
    * For example, for object 'Person' human identifier is "[FirstName] [LastName]"
    */
  def humanIdentifier: String = fullName

  /**
    * Returns type of this object
    */
  def objectType: String = Activity.Type.Attendee
}

/**
  * This class represent a row in a table with attendees
  * @param attendee Attendee personal data
  * @param event Event info
  * @param evaluation Evaluation
  */
case class AttendeeView(attendee: Attendee,
                        event: Event,
                        evaluation: Option[Evaluation]) {

  override def equals(other: Any): Boolean =
    other match {
      case that: AttendeeView ⇒
        (that canEqual this) &&
          attendee.id == that.attendee.id &&
          event.id == that.event.id

      case _ ⇒ false
    }

  def canEqual(other: Any): Boolean = other.isInstanceOf[AttendeeView]

  override def hashCode: Int =
    41 * (41 + attendee.id.get.toInt) + event.id.get.toInt
}