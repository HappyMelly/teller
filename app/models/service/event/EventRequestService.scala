package models.service.event

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.database.event.EventRequestTable
import models.event.EventRequest
import org.joda.time.LocalDate
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
    * Deletes event requests which end date is less than the given date
    * @param expiration Expiration date
    */
  def deleteExpired(expiration: LocalDate) =
    db.run(requests.filter(_.end <= expiration).delete)

  /**
    * Returns event request if exists
    *
    * @param requestId Request id
    */
  def find(requestId: Long): Future[Option[EventRequest]] =
    db.run(requests.filter(_.id === requestId).result).map(_.headOption)

  /**
    * Returns event request if exists
    *
    * @param hashedId Request id
    */
  def find(hashedId: String): Future[Option[EventRequest]] =
    db.run(requests.filter(_.hashedId === hashedId).result).map(_.headOption)

  /**
   * Returns list of event requests belonged the given brand
   *
   * @param brandId Brand identifier
   */
  def findByBrand(brandId: Long): Future[List[EventRequest]] =
    db.run(requests.filter(_.brandId === brandId).result).map(_.toList)

  /**
    * Returns all event requests with one participant valid for upcoming event notifications
    */
  def findWithOneParticipant: Future[List[EventRequest]] =
    db.run(requests.filter(_.participantsNumber === 1).filter(_.unsubscribed === false).result).map(_.toList)

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

  /**
    * Update the given event request in dabase
    *
    * @param request Event request
    */
  def update(request: EventRequest): Future[EventRequest] =
    db.run(requests.filter(_.id === request.id).update(request)).map(_ => request)
}

object EventRequestService {
  private val instance = new EventRequestService()

  def get: EventRequestService = instance
}