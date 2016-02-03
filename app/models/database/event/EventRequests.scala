package models.database.event

import models.DateStamp
import models.database.PortableJodaSupport._
import models.event.EventRequest
import org.joda.time.{DateTime, LocalDate}
import play.api.db.slick.Config.driver.simple._

/**
 * `EventRequest` database table mapping.
 */
private[models] class EventRequests(tag: Tag)
  extends Table[EventRequest](tag, "EVENT_REQUEST") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def brandId = column[Long]("BRAND_ID")
  def countryCode = column[String]("COUNTRY_CODE")
  def city = column[Option[String]]("CITY")
  def language = column[String]("LANGUAGE")
  def start = column[Option[LocalDate]]("START_DATE")
  def end = column[Option[LocalDate]]("END_DATE")
  def participantsNumber = column[Int]("NUMBER_OF_PARTICIPANTS")
  def comment = column[Option[String]]("COMMENT")
  def name = column[String]("NAME")
  def email = column[String]("EMAIL")
  def hashedId = column[String]("HASHED_ID")
  def unsubscribed = column[Boolean]("UNSUBSCRIBED")
  def created = column[DateTime]("CREATED")
  def createdBy = column[String]("CREATED_BY")
  def updated = column[DateTime]("UPDATED")
  def updatedBy = column[String]("UPDATED_BY")

  type EventRequestFields = (Option[Long], Long, String, Option[String], String,
    Option[LocalDate], Option[LocalDate], Int, Option[String], String, String, String, Boolean,
    DateTime, String, DateTime, String)

  def * = (id.?, brandId, countryCode, city, language, start, end,
    participantsNumber, comment, name, email, hashedId, unsubscribed, created, createdBy, updated,
    updatedBy) <> ((e: EventRequestFields) => EventRequest(e._1, e._2, e._3,
    e._4, e._5, e._6, e._7, e._8, e._9, e._10, e._11, e._12, e._13,
    DateStamp(e._14, e._15, e._16, e._17)),
    (e: EventRequest) => Some((e.id, e.brandId, e.countryCode, e.city,
      e.language, e.start, e.end, e.participantsNumber, e.comment, e.name,
      e.email, e.hashedId, e.unsubscribed, e.recordInfo.created, e.recordInfo.createdBy,
      e.recordInfo.updated, e.recordInfo.updatedBy)))

}
