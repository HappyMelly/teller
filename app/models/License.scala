/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models

import org.joda.money.{CurrencyUnit, Money}
import org.joda.time.{Duration, Interval, LocalDate, Months}

import scala.language.postfixOps

/**
 * A content license - a person’s agreement with Happy Melly to use a `Brand`.
 */
case class License(
                    id: Option[Long],
                    licenseeId: Long,
                    brandId: Long,
                    version: String,
                    signed: LocalDate,
                    start: LocalDate,
                    end: LocalDate,
                    confirmed: Boolean,
                    fee: Money,
                    feePaid: Option[Money]) extends ActivityRecorder {

  def active: Boolean = new Interval(start.toDateTimeAtStartOfDay, end.toDateTimeAtStartOfDay).containsNow

  /** Returns true if the license is expired */
  def expired: Boolean = !active

  /** Returns true if the license is expiring this month */
  def expiring: Boolean = {
    val now = LocalDate.now()
    end.getYear == now.getYear && end.getMonthOfYear == now.getMonthOfYear
  }

  /**
    * Returns identifier of the object
    */
  def identifier: Long = id.getOrElse(0)

  /**
    * Returns string identifier which can be understood by human
    *
    * For example, for object 'Person' human identifier is "[FirstName] [LastName]"
    */
  def humanIdentifier: String = s"for %s brand and person %s".format(brandId, licenseeId)

  /** Returns current length of the license */
  def length: Duration = new Interval(start.toDateTimeAtStartOfDay, end.toDateTimeAtStartOfDay).toDuration

  /**
    * Returns type of this object
    */
  def objectType: String = Activity.Type.License

}

case class LicenseView(brand: Brand, license: License)

case class LicenseLicenseeView(license: License, licensee: Person)

case class LicenseLicenseeBrandView(license: License, brand: Brand, licensee: Person)

object License {

  /**
   * Returns a blanks license with default values, for the given licensee, for editing.
   */
  def blank(personId: Long) = {
    License(None, 0, personId, "", LocalDate.now, LocalDate.now, LocalDate.now.plusYears(1), false,
      Money.zero(CurrencyUnit.EUR), Some(Money.zero(CurrencyUnit.EUR)))
  }

  /**
    * Calculates number of licenses per month from the given set of licenses
    * @param licenses Licenses to process
    */
  def numberPerMonth(licenses: List[License]): List[(LocalDate, Int)] = {
    val start = licenses.head.start
    val ends = licenses
      .groupBy(_.end)
      .filter(_._1.isBefore(LocalDate.now().withDayOfMonth(1)))
      .map(x ⇒ x._1 -> x._2.length)
    val perMonthStart = licenses
      .groupBy(_.start)
      .map(x ⇒ x._1 ->(x._2.length, ends.getOrElse(x._1, 0)))
    val perMonthEnd = ends
      .filter(x ⇒ ends.keys.toSet.diff(perMonthStart.keys.toSet).contains(x._1))
      .map(x ⇒ x._1 ->(0, x._2))
    val perMonth = perMonthStart ++ perMonthEnd

    lazy val data: Stream[(LocalDate, Int)] = {
      def loop(d: LocalDate, num: Int): Stream[(LocalDate, Int)] =
        (d, num) #:: loop(d.plusMonths(1), perMonth.get(d.plusMonths(1)).map(x ⇒ num + x._1 - x._2).getOrElse(num))
      loop(start, perMonth.get(start).map(_._1).getOrElse(0))
    }
    val numberOfMonths = Months.monthsBetween(start, LocalDate.now()).getMonths + 1
    val rawStats: List[(LocalDate, Int)] = data take numberOfMonths toList

    rawStats
  }

}
