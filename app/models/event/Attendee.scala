package models.event

import models.{Activity, ActivityRecorder, EvaluationStatus, Event}
import org.joda.time.{DateTime, LocalDate}

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
  role: Option[String]) extends ActivityRecorder {

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
  * @param evaluationId Evaluation identifier
  * @param impression A level of impression (taken from the evaluation)
  * @param status A status of evaluation (pending, approved or rejected)
  * @param date Date of the evaluation creation
  * @param handled Date when the evaluation was approved/rejected
  * @param confirmationToken Token used in a confirmation url to identify an evaluation
  */
case class AttendeeView(attendee: Attendee,
                        event: Event,
                        evaluationId: Option[Long],
                        impression: Option[Int],
                        status: Option[EvaluationStatus.Value],
                        date: Option[DateTime],
                        handled: Option[LocalDate],
                        confirmationToken: Option[String]) {

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