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

import scala.util.Random

import models.{ License, Event }
import models.UserRole.Role._
import models.service.Services
import org.joda.time.{ LocalDate, Months }
import play.api.libs.json.{ Json, JsValue, Writes }
import views.Countries

import scala.language.postfixOps

/**
 * Contains a set of functions for handling brand statistics
 */
trait Statistics extends JsonController with Security with Services {

  /**
   * Renders index page with statistics for brands
   */
  def index() = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      Ok(views.html.statistics.index(user))
  }

  /**
   * Returns number facilitators per quarter for the given brand in a format
   * suitable for diagrams
   *
   * @param brandId Brand id
   */
  def byFacilitators(brandId: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val licenses: List[License] = licenseService.findByBrand(brandId)

      val stats = if (licenses.isEmpty)
        List[(LocalDate, Int)]()
      else
        quarterStatsByFacilitators(licenses)

      Ok(Json.obj("labels" -> stats.map(_._1.toString("MMM yyyy")),
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
  def byEvents(brandId: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val events = eventService.findByParameters(Some(brandId),
        confirmed = Some(true),
        future = Some(false))

      val stats = if (events.isEmpty)
        List[(LocalDate, Int)]()
      else
        quarterStatsByEvents(events)

      Ok(Json.obj("labels" -> stats.map(_._1.toString("MMM yyyy")),
        "datasets" -> List(
          Json.obj("label" -> "Number of facilitators",
            "fillColor" -> "rgba(238,146,159,0.2)",
            "strokeColor" -> "rgba(238,146,159,1)",
            "pointColor" -> "rgba(238,146,159,1)",
            "pointStrokeColor" -> "#fff",
            "pointHighlightFill" -> "#fff",
            "pointHighlightStroke" -> "rgba(220,220,220,1)",
            "data" -> stats.map(_._2)))))
  }

  /**
   * Returns number of events per country for the given brand in a format
   * suitable for diagrams
   *
   * @param brandId Brand id
   */
  def byCountries(brandId: Long) = SecuredRestrictedAction(Viewer) { implicit request ⇒
    implicit handler ⇒ implicit user ⇒
      val perCountry = eventService
        .findByParameters(Some(brandId), confirmed = Some(true), future = Some(false))
        .groupBy(_.location.countryCode)
        .map(x ⇒ (Countries.name(x._1), x._2.length))
        .toList.sortBy(_._2).reverse

      val colors = Array(
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

      implicit val countryStat = new Writes[(String, Int)] {
        def writes(value: (String, Int)): JsValue = {
          val color = colors(Random.nextInt(colors.length))
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
   * Returns accumulated number of events per quarter starting from the first event
   * @param rawEvents Events
   */
  protected def quarterStatsByEvents(rawEvents: List[Event]): List[(LocalDate, Int)] = {
    val perMonth = rawEvents
      .map(x ⇒ (x.schedule.start.withDayOfMonth(1), 1))
      .groupBy(_._1)
      .map(x ⇒ (x._1, x._2.length))

    val start = perMonth.keys.toList.sortBy(_.toString).head

    lazy val data: Stream[(LocalDate, Int)] = {
      def loop(d: LocalDate, num: Int): Stream[(LocalDate, Int)] =
        (d, num) #:: loop(d.plusMonths(1), perMonth.get(d.plusMonths(1)).map(_ + num).getOrElse(num))
      loop(start, perMonth.getOrElse(start, 0))
    }
    val numberOfMonths = Months.monthsBetween(start, LocalDate.now()).getMonths + 1
    val rawStats: List[(LocalDate, Int)] = data take numberOfMonths toList

    rawStats.head :: rawStats.slice(1, rawStats.length - 1).filter(_._1.getMonthOfYear % 3 == 0) ++ List(rawStats.last)
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
}

object Statistics extends Statistics
