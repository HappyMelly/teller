package models.service.event

import models.database.Evaluations.evaluationStatusTypeMapper
import models.database.PortableJodaSupport._
import models.database.event.Attendees
import models.database.{Evaluations, Events}
import models.event.AttendeeView
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

}

object AttendeeService {
  private val _instance = new AttendeeService

  def get: AttendeeService = _instance
}