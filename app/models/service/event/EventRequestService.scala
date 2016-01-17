package models.service.event

import models.database.event.EventRequestTable
import models.event.EventRequest
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Contains a set of methods for retrieving/updating event requests in database
 */
class EventRequestService extends HasDatabaseConfig[JdbcProfile]
  with EventRequestTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._
  private val requests = TableQuery[EventRequests]

  /**
    * Returns event request if exists
    * @param requestId Request id
    */
  def find(requestId: Long): Future[Option[EventRequest]] =
    db.run(requests.filter(_.id === requestId).result).map(_.headOption)

  /**
   * Returns list of event requests belonged the given brand
   *
   * @param brandId Brand identifier
   */
  def findByBrand(brandId: Long): Future[List[EventRequest]] =
    db.run(requests.filter(_.brandId === brandId).result).map(_.toList)

  /**
   * Inserts event request into database
   *
   * @param request EventRequest object
   * @return Updated object with id
   */
  def insert(request: EventRequest): Future[EventRequest] = {
    val query = requests returning requests.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += request)
  }

}

object EventRequestService {
  private val instance = new EventRequestService()

  def get: EventRequestService = instance
}