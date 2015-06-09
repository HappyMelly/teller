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

import com.github.tototoshi.slick.JodaSupport._
import models.database.Licenses
import org.joda.money.{ CurrencyUnit, Money }
import org.joda.time.{ Interval, LocalDate }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

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
  feePaid: Option[Money]) {

  def active: Boolean = new Interval(start.toDateMidnight, end.toDateMidnight).containsNow
}

case class LicenseView(brand: Brand, license: License)

case class LicenseLicenseeView(license: License, licensee: Person)

case class LicenseLicenseeBrandView(license: License, brand: Brand, licensee: Person)

object License {

  def delete(id: Long): Unit = DB.withSession { implicit session: Session ⇒
    Licenses.filter(_.id === id).mutate(_.delete)
  }

  /**
   * Returns a blanks license with default values, for the given licensee, for editing.
   */
  def blank(personId: Long) = {
    License(None, 0, personId, "", LocalDate.now, LocalDate.now, LocalDate.now.plusYears(1), false,
      Money.zero(CurrencyUnit.EUR), Some(Money.zero(CurrencyUnit.EUR)))
  }

  /**
   * Finds a license by ID.
   */
  def find(id: Long): Option[License] = DB.withSession { implicit session: Session ⇒
    Query(Licenses).filter(_.id === id).firstOption
  }

  /**
   * Returns the start date for a current license for the given licensee and brand.
   * If there are multiple current licenses, the earliest start date is returned.
   *
   * Start dates for previous licenses that join up with current licenses are not found. For example, if there is a
   * license for 1 January to 31 December every year, this function returns 1 January of this year, not the first year.
   */
  def licensedSince(licenseeId: Long, brandId: Long): Option[LocalDate] = DB.withSession { implicit session: Session ⇒
    val today = LocalDate.now()
    val query = for {
      license ← Licenses if license.start <= today && license.end >= today && license.brandId === brandId
      licensee ← license.licensee if licensee.id === licenseeId
    } yield license.start

    Query(query.min).first
  }

  /** Finds a licensee by license ID **/
  def licensee(licenseId: Long): Option[Person] = DB.withSession { implicit session: Session ⇒
    val query = for {
      license ← Licenses if license.id === licenseId
      licensee ← license.licensee
    } yield licensee
    query.firstOption
  }

  /**
   * Returns a list of all people who have ever been licensed for the given brand
   */
  def allLicensees(brandId: Long): List[Person] = DB.withSession {
    implicit session: Session ⇒
      val query = for {
        license ← Licenses if license.brandId === brandId
        licensee ← license.licensee
      } yield licensee
      query.sortBy(_.lastName.toLowerCase).list
  }

}
