package controllers.admin

import controllers.Security
import models.{Brand, Event, ActiveUser}
import models.UserRole.Role
import models.service.Services
import org.joda.time.LocalDate
import play.api.mvc.Controller
import securesocial.core.RuntimeEnvironment
import views.Countries

/**
  * Pages for calculating the usage of Teller by brands
  */
class Usage(environment: RuntimeEnvironment[ActiveUser]) extends Controller
with Security
with Services {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment

  def index() = SecuredRestrictedAction(Role.Admin) { implicit request => implicit handler => implicit user =>
    val brands = brandService.findAll
    val eventsFee = filterByDate(eventService.findAll).map { event =>
      (event.brandId, event.location.countryCode, event.schedule.start, event.schedule.totalHours)
    }.groupBy(_._2).flatMap { byLocation =>
      calculateEventFee(byLocation)
    }.groupBy(_._1).map { byBrand =>
      (byBrand._1, groupByMonths(byBrand._2.toList))
    }.map { byBrand =>
      (byBrand._1, brandName(brands, byBrand._1), byBrand._2)
    }
    Ok(views.html.v2.usage.index(user, eventsFee))
  }

  protected def brandName(brands: List[Brand], identifier: Long): String =
    brands.find(_.identifier == identifier).map(_.name).getOrElse("")

  protected def calculateEventFee(byLocation: (String, List[(Long, String, LocalDate, Int)])): List[(Long, Int, Int, Float)] = {
    val fee = countryBasedEventFees(byLocation._1)
    byLocation._2.map(event => (event._1, event._3.getYear, event._3.getMonthOfYear, event._4 * fee))
  }

  protected def calculateMonthlyFee(byYear: (Int, List[(Long, Int, Int, Float)])): Map[String, (String, Float)] =
    byYear._2.groupBy(_._3).map { byMonth =>
      val name = month(byMonth._1) + " " + byYear._1
      val sortableName = byYear._1 + "-" + byMonth._1
      (sortableName, (name, byMonth._2.aggregate(0.0f)(_ + _._3, _ + _)))
    }

  protected def groupByMonths(data: List[(Long, Int, Int, Float)]): List[(String, Float)] = {
    data.groupBy(_._2).flatMap { byYear =>
      calculateMonthlyFee(byYear)
    }.toList.sortBy(_._1)(Ordering[String].reverse).map(_._2)
  }

  protected def filterByDate(events: List[Event]): List[Event] = {
    val (start, end) = (LocalDate.parse("2015-09-30"), LocalDate.now().plusMonths(1).withDayOfMonth(1))
    events.filter(event => event.schedule.start.isAfter(start) && event.schedule.start.isBefore(end))
  }

  /**
    * Returns system fee per event per hour
    *
    * @param code Country code
    */
  protected def countryBasedEventFees(code: String): Float = {
    Countries.gdp.get(code) map { index ⇒
      if (index <= 10)
        3.25f
      else if (index <= 25)
        2.75f
      else if (index <= 50)
        2.00f
      else if (index <= 100)
        1.25f
      else 0.75f
    } getOrElse 0.75f
  }

  /**
    * Returns name of the given month
    * @param index Month index
    */
  protected def month(index: Int): String = {
    val months = Array("January", "February", "March", "April", "May", "June", "July", "August", "September",
      "October", "November", "December")
    months(index - 1)
  }

}
