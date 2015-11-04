/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package controllers



import models.{Participant, ActiveUser, License, Event}
import models.UserRole.Role._
import models.service.Services
import org.joda.time.{ LocalDate, Months, Interval }
import play.api.libs.json.{ Json, JsValue, Writes }
import scala.language.postfixOps
import scala.util.Random
import securesocial.core.RuntimeEnvironment
import views.Countries

/**
 * Contains a set of functions for handling brand statistics
 */
class Statistics(environment: RuntimeEnvironment[ActiveUser])
    extends JsonController
    with Security
    with Services
    with Utilities {

  override implicit val env: RuntimeEnvironment[ActiveUser] = environment
  val TOP_LIMIT = 10
  val COLORS = Array(
    ("rgba(166,206,227,0.2)", "rgba(166,206,227,1)"),
    ("rgba(31,120,180,0.2)", "rgba(31,120,180,1)"),
    ("rgba(178,223,138,0.2)", "rgba(178,223,138,1)"),
    ("rgba(51,160,44,0.2)", "rgba(51,160,44,1)"),
    ("rgba(251,154,153,0.2)", "rgba(251,154,153,1)"),
    ("rgba(227,26,28,0.2)", "rgba(227,26,28,1)"),
    ("rgba(253,191,111,0.2)", "rgba(253,191,111,1)"),
    ("rgba(255,127,0,0.2)", "rgba(255,127,0,1)"),
    ("rgba(202,178,214,0.2)", "rgba(202,178,214,1)"),
    ("rgba(106,61,154,0.2)", "rgba(106,61,154,1)"),
    ("rgba(255,255,153,0.2)", "rgba(255,255,153,1)"),
    ("rgba(177,89,40,0.2)", "rgba(177,89,40,1)"))

  /**
   * Renders index page with statistics for brands
   * @param brandId Brand identifier
   */
  def index(brandId: Long) = SecuredRestrictedAction(BrandViewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      roleDiffirentiator(user.account, Some(brandId)) { (brand, brands) =>
        Ok(views.html.v2.statistics.index(user, brand, brands))
      } { (brand, brands) =>
        Ok(views.html.v2.statistics.index(user, brand.get, brands))
      } { Redirect(routes.Dashboard.index()) }
  }

  /**
   * Returns number facilitators per quarter for the given brand in a format
   * suitable for diagrams
   *
   * @param brandId Brand id
   */
  def byFacilitators(brandId: Long) = SecuredRestrictedAction(BrandViewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val licenses: List[License] = licenseService.findByBrand(brandId)

      val (joined, left) = calculatedJoinedLeftNumbers(licenses)
      val goodProfiles = profileStrengthService.
        find(licenses.map(_.licenseeId), false).
        filter(_.progress >= 80)

      val stats = if (licenses.isEmpty)
        List[(LocalDate, Int)]()
      else
        quarterStatsByFacilitators(licenses)

      Ok(Json.obj("joined" -> joined,
        "left" -> left,
        "withGoodProfiles" -> goodProfiles.length,
        "labels" -> stats.map(_._1.toString("MMM yyyy")),
        "datasets" -> List(
          Json.obj("label" -> "Number of facilitators",
            "fillColor" -> "rgba(220,220,220,0.2)",
            "strokeColor" -> "rgba(220,220,220,1)",
            "pointColor" -> "rgba(220,220,220,1)",
            "pointStrokeColor" -> "#fff",
            "pointHighlightFill" -> "#fff",
            "pointHighlightStroke" -> "rgba(220,220,220,1)",
            "data" -> stats.map(_._2)))))
  }

  /**
   * Returns accumulated number of events per quarter for the given brand in
   * a format suitable for diagrams
   *
   * @param brandId Brand id
   */
  def byEvents(brandId: Long) = SecuredRestrictedAction(BrandViewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val now = LocalDate.now()
      val events = eventService.findByParameters(Some(brandId))

      val (futurePaid, futureFree) = calculateFutureEventsNumbers(events)
      val (paid, free, rating, nps) = calculateLastMonthNumbers(events)
      val (canceledPaid, canceledFree) = calculatedCanceledEventsNumbers(brandId)
      val (facilitators, topFacilitators, organizers) = activityRangeNumbers(events)
      val stats = if (events.isEmpty)
        List[(LocalDate, Int)]()
      else
        quarterStatsByEvents(
          events.filter(_.schedule.end.isBefore(now)).filter(_.confirmed))

      implicit val topFacilitatorWrites = new Writes[(Long, String, Int)] {
        def writes(value: (Long, String, Int)): JsValue = {
          val color = COLORS(Random.nextInt(COLORS.length))
          Json.obj(
            "id" -> value._1,
            "label" -> value._2,
            "value" -> value._3,
            "color" -> color._2,
            "highlight" -> color._1)
        }
      }

      Ok(Json.obj(
        "events" -> Json.obj(
          "future" -> Json.obj("paid" -> futurePaid, "free" -> futureFree),
          "confirmed" -> Json.obj("paid" -> paid, "free" -> free),
          "canceled" -> Json.obj("paid" -> canceledPaid, "free" -> canceledFree),
          "rating" -> rating,
          "nps" -> nps,
          "labels" -> stats.map(_._1.toString("MMM yyyy")),
          "datasets" -> List(
              Json.obj("label" -> "Number of facilitators",
                "fillColor" -> "rgba(238,146,159,0.2)",
                "strokeColor" -> "rgba(238,146,159,1)",
                "pointColor" -> "rgba(238,146,159,1)",
                "pointStrokeColor" -> "#fff",
                "pointHighlightFill" -> "#fff",
                "pointHighlightStroke" -> "rgba(220,220,220,1)",
                "data" -> stats.map(_._2)))
          ),
        "activeFacilitators" -> facilitators,
        "topFacilitators" -> topFacilitators,
        "organizers" -> organizers))
  }

  /**
   * Returns number of events per country for the given brand in a format
   * suitable for diagrams
   *
   * @param brandId Brand id
   */
  def byCountries(brandId: Long) = SecuredRestrictedAction(BrandViewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val events = eventService.
        findByParameters(Some(brandId), confirmed = Some(true), future = Some(false))

      val perCountry = filterLastSixMonths(events).
        groupBy(_.location.countryCode).
        map(x ⇒ (Countries.name(x._1), x._2.length)).
        toList.sortBy(_._2).reverse.take(TOP_LIMIT)

      implicit val countryStat = new Writes[(String, Int)] {
        def writes(value: (String, Int)): JsValue = {
          val color = COLORS(Random.nextInt(COLORS.length))
          Json.obj(
            "value" -> value._2,
            "label" -> value._1,
            "color" -> color._2,
            "highlight" -> color._1)
        }
      }
      Ok(Json.toJson(perCountry))
  }

  /**
   * Returns number of participants per quarter for the given brand in a format
   * suitable for diagrams
   *
   * @param brandId Brand id
   */
  def byParticipants(brandId: Long) = SecuredRestrictedAction(BrandViewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val participants = participantService.findByBrand(brandId)

      val statsByRoles = participants.
        filter(_._1.role.exists(_.nonEmpty)).
        groupBy(_._1.role).map(x => (x._1.get, x._2.length)).toList

      val stats = if (participants.isEmpty)
        List[(LocalDate, Int)]()
      else
        quarterStatsByParticipants(participants)

      implicit val roleStats = new Writes[(String, Int)] {
        def writes(value: (String, Int)): JsValue = {
          val color = COLORS(Random.nextInt(COLORS.length))
          Json.obj(
            "value" -> value._2,
            "label" -> value._1,
            "color" -> color._2,
            "highlight" -> color._1)
        }
      }

      Ok(Json.obj(
        "roles" -> statsByRoles,
        "labels" -> stats.map(_._1.toString("MMM yyyy")),
        "datasets" -> List(
          Json.obj("label" -> "Number of participants",
            "fillColor" -> "rgba(220,220,220,0.2)",
            "strokeColor" -> "rgba(220,220,220,1)",
            "pointColor" -> "rgba(220,220,220,1)",
            "pointStrokeColor" -> "#fff",
            "pointHighlightFill" -> "#fff",
            "pointHighlightStroke" -> "rgba(220,220,220,1)",
            "data" -> stats.map(_._2)))))
  }

  /**
   * Returns number of facilitators who joined and left last month
   * @param licenses List of licenses
   */
  protected def calculatedJoinedLeftNumbers(licenses: List[License]): (Int, Int) = {
    (licenses.count(x => lastMonth.contains(x.start.toDate.getTime)),
      licenses.count(x => lastMonth.contains(x.end.toDate.getTime)))
  }

  /**
   * Returns number of paid and free cancelled events from last month
   * @param brandId Brand identifier
   */
  protected def calculatedCanceledEventsNumbers(brandId: Long): (Int, Int) = {
    val cancellations = eventCancellationService.
      findByBrands(List(brandId)).
      filter(x => lastMonth.contains(x.start.toDate.getTime) ||
        lastMonth.contains(x.end.toDate.getTime))

    (cancellations.count(!_.free), cancellations.count(_.free))
  }

  /**
   * Returns number of paid and free future events
   * @param events List of events
   */
  protected def calculateFutureEventsNumbers(events: List[Event]): (Int, Int) = {
    val futureEvents = filterFuture(events)
    (futureEvents.count(!_.free), futureEvents.count(_.free))
  }

  /**
   * Returns number of active organizers, active facilitators and a list
   * of top 10 most active facilitators
   * @param events List of events
   */
  protected def activityRangeNumbers(events: List[Event]): (Int, List[(Long, String, Int)], Int) = {
    val filtered = filterByActivityRange(events)
    val organizers = filtered.map(_.organizer.id).distinct.length
    eventService.applyFacilitators(filtered)
    val activeFacilitators = filtered.map(_.facilitators.map(_.id.get)).distinct.length
    (activeFacilitators, findTopFacilitators(filtered), organizers)
  }

  /**
   * Returns number of paid and free events from last month, average rating
   * and nps of events from last month
   * @param events List of events
   */
  protected def calculateLastMonthNumbers(events: List[Event]): (Int, Int, Float, Float) = {
    val filtered = filterLastMonth(events)
    if (filtered.isEmpty)
      (0, 0, 0.0f, 0.0f)
    else
      (filtered.count(!_.free),
        filtered.count(_.free),
        calculateAverageRating(filtered),
        calculateNPS(filtered))
  }

  /**
   * Returns a list of facilitators with most number of events in last six months
   * @param events List of events
   */
  protected def findTopFacilitators(events: List[Event]): List[(Long, String, Int)] = {
    events.filter(_.schedule.end.isBefore(LocalDate.now)).
      flatMap(_.facilitators).
      groupBy(_.id.get).map(y => (y._1, y._2.head.fullName, y._2.length)).toList.
      sortBy(_._3).takeRight(TOP_LIMIT).reverse
  }

  /**
   * Returns average rating for the given events
   * @param events List of events
   */
  protected def calculateAverageRating(events: List[Event]): Float = {
    val numberOfEvents = events.count(_.rating > 0.01)
    if (numberOfEvents == 0)
      0.0f
    else
      events.foldRight(0.0f)(_.rating + _) / numberOfEvents
  }

  /**
   * Returns NPS for evaluations of the given events
   * @param events List of events
   */
  protected def calculateNPS(events: List[Event]): Float = {
    val evaluations = evaluationService.findByEvents(events.map(_.id.get))
    if (evaluations.nonEmpty) {
      val promoters = evaluations.count(_.impression >= 9)
      val detractors = evaluations.count(_.impression <= 6)
      (promoters - detractors) / evaluations.length.toFloat * 100
    } else {
      0.0f
    }
  }

  /**
   * Return confirmed events happened in the last six months or scheduled events
   * @param events List of events
   */
  protected def filterByActivityRange(events: List[Event]): List[Event] =
    filterLastSixMonths(events) ::: filterFuture(events)

  /**
   * Returns list of confirmed events happened in the last month
   * @param events List of events
   */
  protected def filterLastMonth(events: List[Event]): List[Event] = events.
    filter(_.confirmed).
    filter(x => lastMonth.contains(x.schedule.start.toDate.getTime) ||
      lastMonth.contains(x.schedule.end.toDate.getTime))

  /**
   * Returns list of confirmed events happened in last six months
   * @param events List of events
   */
  protected def filterLastSixMonths(events: List[Event]): List[Event] = events.
    filter(_.schedule.end.isAfter(LocalDate.now.minusMonths(6))).
    filter(_.schedule.end.isBefore(LocalDate.now)).
    filter(_.confirmed)

  /**
   * Returns list of future events
   * @param events List of events
   */
  protected def filterFuture(events: List[Event]): List[Event] =
    events.filter(_.schedule.start.isAfter(LocalDate.now()))

  protected def lastMonth: Interval = new Interval(
    LocalDate.now().minusMonths(1).withDayOfMonth(1).toDate.getTime,
    LocalDate.now().withDayOfMonth(1).toDate.getTime - 1)

  /**
   * Returns accumulated number of participants per quarter starting from the first event
   * @param participants Participants
   */
  protected def quarterStatsByParticipants(participants: List[(Participant, LocalDate)]): List[(LocalDate, Int)] = {
    val perMonth = participants
      .map(x ⇒ (x._2.withDayOfMonth(1), 1))
      .groupBy(_._1)
      .map(x ⇒ (x._1, x._2.length))

    convertMonthStatsToQuarterStats(perMonth)
  }
  
  /**
   * Returns accumulated number of events per quarter starting from the first event
   * @param rawEvents Events
   */
  protected def quarterStatsByEvents(rawEvents: List[Event]): List[(LocalDate, Int)] = {
    val perMonth = rawEvents
      .map(x ⇒ (x.schedule.start.withDayOfMonth(1), 1))
      .groupBy(_._1)
      .map(x ⇒ (x._1, x._2.length))

    convertMonthStatsToQuarterStats(perMonth)
  }

  /**
   * Returns number of facilitators per quarter starting from the first license
   * @param rawLicenses Licenses
   */
  protected def quarterStatsByFacilitators(rawLicenses: List[License]): List[(LocalDate, Int)] = {
    val licenses: List[License] = rawLicenses
      .sortBy(_.start.toString)
      .map(x ⇒ x.copy(start = x.start.withDayOfMonth(1), end = x.end.withDayOfMonth(1).plusMonths(1)))

    val start = licenses.head.start
    val ends = licenses
      .groupBy(_.end)
      .filter(_._1.compareTo(LocalDate.now().withDayOfMonth(1)) <= 0)
      .map(x ⇒ x._1 -> x._2.length)
    val perMonthStart = licenses
      .groupBy(_.start)
      .map(x ⇒ x._1 -> (x._2.length, ends.getOrElse(x._1, 0)))
    val perMonthEnd = ends
      .filter(x ⇒ ends.keys.toSet.diff(perMonthStart.keys.toSet).contains(x._1))
      .map(x ⇒ x._1 -> (0, x._2))
    val perMonth = perMonthStart ++ perMonthEnd

    lazy val data: Stream[(LocalDate, Int)] = {
      def loop(d: LocalDate, num: Int): Stream[(LocalDate, Int)] =
        (d, num) #:: loop(d.plusMonths(1), perMonth.get(d.plusMonths(1)).map(x ⇒ num + x._1 - x._2).getOrElse(num))
      loop(start, perMonth.get(start).map(_._1).getOrElse(0))
    }
    val numberOfMonths = Months.monthsBetween(start, LocalDate.now()).getMonths + 1
    val rawStats: List[(LocalDate, Int)] = data take numberOfMonths toList

    rawStats.head :: rawStats.slice(1, rawStats.length - 1).filter(_._1.getMonthOfYear % 3 == 0) ++ List(rawStats.last)
  }

  /**
   * Converts monthly statistics to statistics by quarters
   * @param monthStats Monthly statistics
   */
  protected def convertMonthStatsToQuarterStats(monthStats: Map[LocalDate, Int]): List[(LocalDate, Int)] = {
    val start = monthStats.keys.toList.sortBy(_.toString).head

    lazy val data: Stream[(LocalDate, Int)] = {
      def loop(d: LocalDate, num: Int): Stream[(LocalDate, Int)] =
        (d, num) #:: loop(d.plusMonths(1), monthStats.get(d.plusMonths(1)).map(_ + num).getOrElse(num))
      loop(start, monthStats.getOrElse(start, 0))
    }
    val numberOfMonths = Months.monthsBetween(start, LocalDate.now()).getMonths + 1
    val rawStats: List[(LocalDate, Int)] = data take numberOfMonths toList

    rawStats.head :: rawStats.slice(1, rawStats.length - 1).filter(_._1.getMonthOfYear % 3 == 0) ++ List(rawStats.last)
  }
}
