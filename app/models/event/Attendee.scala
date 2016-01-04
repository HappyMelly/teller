package models.event

import org.joda.time.LocalDate

/**
  * Created by sery0ga on 04/01/16.
  */
case class Attendee(
  id: Option[Long],
  eventId: Long,
  personId: Option[Long],
  firstName: String,
  lastName: String,
  email: String,
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
  role: Option[String]) {
}
