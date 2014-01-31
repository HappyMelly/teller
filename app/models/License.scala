/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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
import models.database.{ Addresses, People, Brands, Licenses }
import org.joda.money.{ CurrencyUnit, Money }
import org.joda.time.{ Interval, LocalDate }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

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
   * Finds a license by ID, joined with brand and licensee.
   */
  def findWithBrandAndLicensee(id: Long): Option[LicenseLicenseeBrandView] = DB.withSession { implicit session: Session ⇒
    val query = for {
      license ← Licenses if license.id === id
      brand ← license.brand
      licensee ← license.licensee
    } yield (license, brand, licensee)

    query.mapResult {
      case (license, brand, licensee) ⇒
        LicenseLicenseeBrandView(license, brand, licensee)
    }.firstOption
  }

  /**
   * Returns the start date for a current license for the given licensee and brand.
   * If there are multiple current licenses, the earliest start date is returned.
   *
   * Start dates for previous licenses that join up with current licenses are not found. For example, if there is a
   * license for 1 January to 31 December every year, this function returns 1 January of this year, not the first year.
   */
  def licensedSince(licenseeId: Long, brandCode: String): Option[LocalDate] = DB.withSession { implicit session: Session ⇒
    val today = LocalDate.now()
    val query = for {
      license ← Licenses if license.start <= today && license.end >= today
      brand ← license.brand if brand.code === brandCode
      licensee ← license.licensee if licensee.id === licenseeId
    } yield license.start

    Query(query.min).first
  }

  def insert(license: License) = DB.withSession { implicit session: Session ⇒
    val id = Licenses.forInsert.insert(license)
    license.copy(id = Some(id))
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
   * Returns a list of people who are licensed for the given brand on the given date, usually today.
   */
  def licensees(brandCode: String, date: LocalDate = LocalDate.now()): List[Person] = DB.withSession { implicit session: Session ⇒
    val query = for {
      license ← Licenses if license.start <= date && license.end >= date
      brand ← license.brand if brand.code === brandCode
      licensee ← license.licensee
    } yield licensee
    query.sortBy(_.lastName.toLowerCase).list
  }

  /**
   * Returns a list of content licenses for the given person.
   */
  def licenses(personId: Long): List[LicenseView] = DB.withSession { implicit session: Session ⇒

    val query = for {
      license ← Licenses if license.licenseeId === personId
      brand ← license.brand
    } yield (license, brand)

    query.sortBy(_._2.name.toLowerCase).list.map {
      case (license, brand) ⇒ LicenseView(brand, license)
    }
  }

  /**
   * Returns a list of active content licenses for the given person.
   */
  def activeLicenses(personId: Long): List[LicenseView] = DB.withSession { implicit session: Session ⇒

    val query = for {
      license ← Licenses if license.licenseeId === personId && license.end >= LocalDate.now
      brand ← license.brand
    } yield (license, brand)

    query.sortBy(_._2.name.toLowerCase).list.map {
      case (license, brand) ⇒ LicenseView(brand, license)
    }
  }

  /**
   * Updates this license in the database.
   */
  def update(license: License): Unit = DB.withSession { implicit session: Session ⇒
    license.id.map { id ⇒
      Query(Licenses).filter(_.id === id).update(license)
    }
  }

}
