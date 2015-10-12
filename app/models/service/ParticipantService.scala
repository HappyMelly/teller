package models.service

import models.Participant
import models.database.{Events, Participants}
import org.joda.time.LocalDate
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

/**
 * Contains a set of methods for retrieving/updating data about participants
 *  from database
 */
class ParticipantService {


  /**
   * Find if a person took part in an event
   * @param personId Person identifier
   * @param eventId Event identifier
   * @return
   */
  def find(personId: Long, eventId: Long): Option[Participant] = DB.withSession {
    implicit session ⇒
      TableQuery[Participants].
        filter(_.personId === personId).
        filter(_.eventId === eventId).firstOption
  }

  /**
   * Returns list of participants for the given brand
   * @param id Brand identifier
   */
  def findByBrand(id: Long): List[(Participant, LocalDate)] = DB.withSession {
    implicit session =>
      val query = for {
        (p, e) <- TableQuery[Participants] innerJoin
          TableQuery[Events] on (_.eventId === _.id)
      } yield (p, e.brandId, e.start)
      query.filter(_._2 === id).map(x => (x._1, x._3)).list
  }
}

object ParticipantService {
  private val _instance = new ParticipantService

  def get: ParticipantService = _instance
}
