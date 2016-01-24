package models.service.event

import org.joda.time.LocalDate

import models.database.PortableJodaSupport._
import models.database.event.EventRequests
import models.event.EventRequest
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

/**
 * Contains a set of methods for retrieving/updating event requests in database
 */
class EventRequestService {

  private val requests = TableQuery[EventRequests]

  /**
    * Deletes event requests which end date is less than the given date
    * @param expiration Expiration date
    */
  def deleteExpired(expiration: LocalDate) = DB.withSession { implicit session =>
    requests.filter(_.end <= expiration).mutate(_.delete())
  }

  /**
    * Returns event request if exists
    *
    * @param requestId Request id
    */
  def find(requestId: Long): Option[EventRequest] = DB.withSession { implicit session =>
    requests.filter(_.id === requestId).firstOption
  }

  /**
    * Returns event request if exists
    *
    * @param hashedId Request id
    */
  def find(hashedId: String): Option[EventRequest] = DB.withSession { implicit session =>
    requests.filter(_.hashedId === hashedId).firstOption
  }

  /**
   * Returns list of event requests belonged the given brand
   *
   * @param brandId Brand identifier
   */
  def findByBrand(brandId: Long): List[EventRequest] =
    DB.withSession { implicit session ⇒
      requests.filter(_.brandId === brandId).list
    }

  /**
    * Returns all event requests with one participant valid for upcoming event notifications
    */
  def findWithOneParticipant: List[EventRequest] = DB.withSession { implicit session =>
    requests.filter(_.participantsNumber === 1).filter(_.unsubscribed === false).list
  }

  /**
   * Inserts event request into database
   *
   * @param request EventRequest object
   * @return Updated object with id
   */
  def insert(request: EventRequest): EventRequest = DB.withSession {
    implicit session ⇒
      val id = (requests returning requests.map(_.id)) += request
      request.copy(id = Some(id))
  }

  /**
    * Update the given event request in dabase
    *
    * @param request Event request
    */
  def update(request: EventRequest): EventRequest = DB.withSession { implicit session =>
    requests.filter(_.id === request.id).update(request)
    request
  }
}

object EventRequestService {
  private val instance = new EventRequestService()

  def get: EventRequestService = instance
}