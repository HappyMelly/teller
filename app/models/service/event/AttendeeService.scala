package models.service.event

import models.database.Evaluations.evaluationStatusTypeMapper
import models.database.PortableJodaSupport._
import models.database.event.Attendees
import models.database.{Evaluations, Events}
import models.event.{Attendee, AttendeeView}
import models.service.Services
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

/**
  * Created by sery0ga on 04/01/16.
  */
class AttendeeService extends Services {

  private val attendees = TableQuery[Attendees]

  /**
    * Deletes the given attendee from the database
    * @param attendeeId Attendee identifier
    * @param eventId Event identifier
    */
  def delete(attendeeId: Long, eventId: Long): Unit = DB.withSession { implicit session ⇒
    attendees.filter(_.id === attendeeId).filter(_.eventId === eventId).mutate(_.delete())
  }

  /**
    * Returns the given attendee if exists
    * @param attendeeId Attendee identifier
    * @param eventId Event identifier
    * @return
    */
  def find(attendeeId: Long, eventId: Long): Option[Attendee] = DB.withSession { implicit session ⇒
    attendees.filter(_.id === attendeeId).filter(_.eventId === eventId).firstOption
  }

  /**
    * Find all participants for all events of the specified brand
    * @param brandId Brand id
    * @return
    */
  def findByBrand(brandId: Option[Long]): List[AttendeeView] = DB.withSession { implicit session ⇒
    val baseQuery = for {
      ((part, e), ev) ← attendees innerJoin
        TableQuery[Events] on (_.eventId === _.id) leftJoin
        TableQuery[Evaluations] on (_._1.evaluationId === _.id)
    } yield (part, e, ev.id.?, ev.facilitatorImpression.?, ev.status.?, ev.created.?, ev.handled, ev.confirmationId)

    val brandQuery = brandId.map { value ⇒ baseQuery.filter(_._2.brandId === value) }.getOrElse(baseQuery)
    val rawList = brandQuery.mapResult(AttendeeView.tupled).list
    val withEvaluation = rawList.filterNot(obj ⇒ obj.evaluationId.isEmpty).distinct
    val withoutEvaluation = rawList.filter(obj ⇒ obj.evaluationId.isEmpty).
      map(obj ⇒ AttendeeView(obj.attendee, obj.event, None, None, None, None, None, None))
    withEvaluation.union(withoutEvaluation.distinct)
  }

  /**
    * Updates the given attendee in database
    * @param attendee Attendee
    */
  def update(attendee: Attendee) = DB.withSession { implicit session =>
    val forUpdate = (attendee.firstName, attendee.lastName, attendee.email, attendee.dateOfBirth, attendee.countryCode,
      attendee.city, attendee.street_1, attendee.street_2, attendee.province, attendee.postcode, attendee.role,
      attendee.recordInfo.updated, attendee.recordInfo.updatedBy)
    attendees.filter(_.id === attendee.id).map(_.forUpdate).update(forUpdate)
  }

  /**
    * Updates certificates data for the given attendee
    * @param attendee Attendee
    */
  def updateCertificate(attendee: Attendee) = DB.withSession { implicit session =>
    val forUpdate = (attendee.certificate, attendee.issued)
    attendees.filter(_.id === attendee.id).map(x => (x.certificate, x.issued)).update(forUpdate)
  }

  /**
    * Updates evaluation for the given attendee
    * @param attendeeId Attendee identifier
    * @param evaluationId Evaluation identifier
    */
  def updateEvaluation(attendeeId: Long, evaluationId: Option[Long]) = DB.withSession { implicit session =>
    attendees.filter(_.id === attendeeId).map(_.evaluationId).update(evaluationId)
  }
}

object AttendeeService {
  private val _instance = new AttendeeService

  def get: AttendeeService = _instance
}