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

package models.database.event

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.DateStamp
import models.database.EventTable
import models.event.Attendee
import org.joda.time.{DateTime, LocalDate}
import slick.collection.heterogeneous._
import slick.collection.heterogeneous.syntax._
import slick.driver.JdbcProfile

private[models] trait AttendeeTable extends EventTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * `Attendee` database table mapping.
    */
  class Attendees(tag: Tag) extends Table[Attendee](tag, "EVENT_ATTENDEE") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def eventId = column[Long]("EVENT_ID")
    def personId = column[Option[Long]]("PERSON_ID")
    def firstName = column[String]("FIRST_NAME")
    def lastName = column[String]("LAST_NAME")
    def email = column[String]("EMAIL")
    def dateOfBirth = column[Option[LocalDate]]("DATE_OF_BIRTH")
    def countryCode = column[Option[String]]("COUNTRY_CODE")
    def city = column[Option[String]]("CITY")
    def street_1 = column[Option[String]]("STREET_1")
    def street_2 = column[Option[String]]("STREET_2")
    def province = column[Option[String]]("PROVINCE")
    def postcode = column[Option[String]]("POSTCODE")
    def evaluationId = column[Option[Long]]("EVALUATION_ID")
    def certificate = column[Option[String]]("CERTIFICATE")
    def issued = column[Option[LocalDate]]("ISSUED")
    def organisation = column[Option[String]]("ORGANISATION")
    def comment = column[Option[String]]("COMMENT")
    def role = column[Option[String]]("ROLE")
    def created = column[DateTime]("CREATED")
    def createdBy = column[String]("CREATED_BY")
    def updated = column[DateTime]("UPDATED")
    def updatedBy = column[String]("UPDATED_BY")

    type AttendeeFields = Option[Long] :: Long :: Option[Long] :: String :: String :: String :: Option[LocalDate] ::
      Option[String] :: Option[String] :: Option[String] :: Option[String] :: Option[String] :: Option[String] ::
      Option[Long] :: Option[String] :: Option[LocalDate] :: Option[String] :: Option[String] :: Option[String] ::
      DateTime :: String :: DateTime :: String :: HNil

    def event = foreignKey("EVENT_FK", eventId, TableQuery[Events])(_.id)

    def * = (id.? :: eventId :: personId :: firstName :: lastName :: email :: dateOfBirth ::
      countryCode :: city :: street_1 :: street_2 :: province :: postcode ::
      evaluationId :: certificate :: issued :: organisation :: comment :: role ::
      created :: createdBy :: updated :: updatedBy :: HNil) <>(createAttendee, extractAttendee)

    def forUpdate = (personId, firstName, lastName, email, dateOfBirth, countryCode, city, street_1, street_2, province,
      postcode, role, updated, updatedBy)

    def createAttendee(a: AttendeeFields): Attendee = a match {
      case id :: eventId :: personId :: firstName :: lastName :: email :: dateOfBirth :: countryCode :: city :: street_1 ::
        street_2 :: province :: postcode :: evaluationId :: certificate :: issued :: organisation :: comment :: role ::
        created :: createdBy :: updated :: updatedBy :: HNil =>
        Attendee(id, eventId, personId, firstName, lastName, email, dateOfBirth, countryCode, city, street_1,
          street_2, province, postcode, evaluationId, certificate, issued, organisation, comment, role,
          DateStamp(created, createdBy, updated, updatedBy))
    }

    def extractAttendee(a: Attendee): Option[AttendeeFields] =
      Some(a.id :: a.eventId :: a.personId :: a.firstName :: a.lastName :: a.email :: a.dateOfBirth :: a.countryCode ::
        a.city :: a.street_1 :: a.street_2 :: a.province :: a.postcode :: a.evaluationId :: a.certificate :: a.issued ::
        a.organisation :: a.comment :: a.role :: a.recordInfo.created :: a.recordInfo.createdBy ::
        a.recordInfo.updated :: a.recordInfo.updatedBy :: HNil)

  }

}