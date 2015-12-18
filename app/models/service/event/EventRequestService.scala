package models.service.event

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
    * Returns event request if exists
    * @param requestId Request id
    */
  def find(requestId: Long): Option[EventRequest] = DB.withSession { implicit session =>
    requests.filter(_.id === requestId).firstOption
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

}

object EventRequestService {
  private val instance = new EventRequestService()

  def get: EventRequestService = instance
}