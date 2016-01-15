package controllers.admin

import controllers.Security
import models.UserRole.Role
import models._
import models.service.Services
import org.joda.time.LocalDate
import play.api.mvc.Controller
import services.TellerRuntimeEnvironment
import views.Countries

/**
  * Pages for calculating the usage of Teller by brands
  */
class Usage @javax.inject.Inject() (override implicit val env: TellerRuntimeEnvironment) extends Controller
  with Security
  with Services {

  /**
    * Renders Teller usage fee for brands
    */
  def index() = SecuredRestrictedAction(Role.Admin) { implicit request => implicit handler => implicit user =>
    val brands = brandService.findAll
    val licenses = licenseUsageByMonth(licenseStatsByCountry(licensesInChargeablePeriod()))
    val events = eventUsageByMonth(eventStatsByCountry(eventsInChargeablePeriod()))
    val fees = brandUsageFeeByMonth(events, licenses).map { byBrand =>
      (byBrand._1, brandName(brands, byBrand._1), byBrand._2)
    }
    Ok(views.html.v2.usage.index(user, fees))
  }

  protected def brandName(brands: List[Brand], identifier: Long): String =
    brands.find(_.identifier == identifier).map(_.name).getOrElse("")

  /**
    * Calculates usage fee for brand
    * @param events Event usage fees per brand per month
    * @param licenses License usage fees per brand per month
    */
  protected def brandUsageFeeByMonth(events: List[(Long, LocalDate, Float)], licenses: List[(Long, LocalDate, Float)]) = {
    val fees = events.map(x => (x._1, x._2, x._3, true)) ::: licenses.map(x => (x._1, x._2, x._3, false))
    fees.groupBy(_._1).map { byBrand =>
      val usage = byBrand._2.groupBy(_._2).map { byMonth =>
        (byMonth._1, byMonth._2.filter(_._4).map(_._3).sum, byMonth._2.filterNot(_._4).map(_._3).sum)
      }.toList.sortBy(_._1.toString)(Ordering[String].reverse).map(x => (x._1.toString("MMMM yyyy"), x._2, x._3))
      (byBrand._1, usage)
    }
  }

  /**
    * Returns events with calculated usage fee
    * @param byLocation Events grouped by country
    * @return
    */
  protected def calculateEventFee(byLocation: (String, List[Event])): List[(Event, Float)] = {
    val fee = countryBasedEventFees(byLocation._1)
    byLocation._2.map(event => (event, event.schedule.totalHours * fee))
  }

  protected def chargeablePeriod(): (LocalDate, LocalDate) =
    (LocalDate.parse("2015-09-30"), LocalDate.now().plusMonths(1).withDayOfMonth(1))

  /**
    * Returns system fee per event per hour
    *
    * @param code Country code
    */
  protected def countryBasedEventFees(code: String): Float = {
    Countries.gdp.get(code) map { category ⇒
      category match {
        case 1 => 3.25f
        case 2 => 2.75f
        case 3 => 2.00f
        case 4 => 1.25f
        case _ => 0.75f
      }
    } getOrElse 0.75f
  }

  /**
    * Returns system fee per facilitator per month
    *
    * @param code Country code
    */
  protected def countryBasedFacilitatorFees(code: String): Float = {
    Countries.gdp.get(code) map { category ⇒
      category match {
        case 1 => 3.45f
        case 2 => 2.95f
        case 3 => 2.45f
        case 4 => 1.45f
        case _ => 0.95f
      }
    } getOrElse 0.95f
  }

  /**
    * Returns events valid in a chargeable period with tweaked start date
    */
  protected def eventsInChargeablePeriod(): List[Event] =
    filterEventsByDate(eventService.findAll).map { event =>
      val start = event.schedule.start.withDayOfMonth(1)
      event.copy(schedule = event.schedule.copy(start = start))
    }

  /**
    * Returns event usage fee per country
    * @param events Events
    */
  protected def eventStatsByCountry(events: List[Event]) =
    events.groupBy(_.location.countryCode).flatMap { byLocation =>
      calculateEventFee(byLocation)
    }

  /** Calculates usage fee for events per month **/
  protected def eventUsageByMonth(events: Map[Event, Float]): List[(Long, LocalDate, Float)] = {
    events.groupBy(_._1.brandId).flatMap { byBrand =>
      byBrand._2.groupBy(_._1.schedule.start).map { byMonth =>
        (byBrand._1, byMonth._1, byMonth._2.aggregate(0.0f)(_ + _._2, _ + _))
      }
    }.toList
  }

  /**
    * Returns only events in a chargeable period: from Oct 2015 to the end of current month
    * @param events Events
    */
  protected def filterEventsByDate(events: List[Event]): List[Event] = {
    val (start, end) = chargeablePeriod()
    events.filter(event => event.schedule.start.isAfter(start) && event.schedule.start.isBefore(end))
  }

  /**
    * Returns only licenses valid in a chargeable period: from Oct 2015 to the end of current month
    * @param licenses Licenses
    */
  protected def filterLicensesByDate(licenses: List[License]): List[License] = {
    val (start, end) = chargeablePeriod()
    licenses.filterNot(license => license.end.isBefore(start) || license.start.isAfter(end))
  }

  /**
    * Returns licenses valid in a chargeable period with tweaked start/end dates
    */
  protected def licensesInChargeablePeriod(): List[License] =
    filterLicensesByDate(licenseService.findAll).sortBy(_.start.toString).map { license =>
      val start = license.start.withDayOfMonth(1)
      license.copy(start = start, end = start.plusMonths(1).minusDays(1))
    }

  /**
    * Returns usage fee per country per month
    * @param licenses Licenses
    */
  protected def licenseStatsByCountry(licenses: List[License]) = {
    val (start, _) = chargeablePeriod()
    val facilitators = personService.find(licenses.map(_.licenseeId).distinct)
    PeopleCollection.addresses(facilitators)
    licenses.map { license =>
      (license, facilitators.find(_.identifier == license.licenseeId).map(_.address.countryCode).get)
    }.groupBy(_._1.brandId).map { byBrand =>
      val statsByLocation = byBrand._2.groupBy(_._2).map { byLocation =>
        (byLocation._1, License.numberPerMonth(byLocation._2.map(_._1)).filter(_._1.isAfter(start)))
      }
      (byBrand._1, statsByLocation)
    }
  }

  /**
    * Returns usage fee per brand per month
    * @param licenses License data
    */
  protected def licenseUsageByMonth(licenses: Map[Long, Map[String, List[(LocalDate, Int)]]]): List[(Long, LocalDate, Float)] = {
    licenses.flatMap { byBrand =>
      byBrand._2.flatMap { byCountry =>
        val fee = countryBasedFacilitatorFees(byCountry._1)
        byCountry._2.map(data => (byCountry._1, data._1, data._2 * fee))
      }.groupBy(_._2).map { byMonth =>
        (byBrand._1, byMonth._1, byMonth._2.aggregate(0.0f)(_ + _._3, _ + _))
      }
    }.toList
  }
}
