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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing
 * Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models.service

import com.github.tototoshi.slick.JodaSupport._
import models.database.{ Licenses, People }
import models.{ Facilitator, LicenseLicenseeView, LicenseView, License }
import org.joda.time.LocalDate
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

class LicenseService extends Services {

  /**
   * Adds the given license to database and updates all related tables
   * @param license License
   * @return Returns the updated license with a valid id
   */
  def add(license: License) = DB.withTransaction { implicit session: Session ⇒
    val id = Licenses.forInsert.insert(license)
    if (facilitatorService.find(license.brandId, license.licenseeId).isEmpty) {
      val facilitator = Facilitator(None, license.licenseeId, license.brandId)
      facilitatorService.insert(facilitator)
    }
    license.copy(id = Some(id))
  }

  /**
   * Returns a list of active content licenses for the given person
   * @param personId Person identifier
   */
  def activeLicenses(personId: Long): List[LicenseView] = DB.withSession {
    implicit session: Session ⇒
      val query = for {
        license ← Licenses if license.licenseeId === personId && license.end >= LocalDate.now
        brand ← license.brand
      } yield (license, brand)

      query.sortBy(_._2.name.toLowerCase).list.map {
        case (license, brand) ⇒ LicenseView(brand, license)
      }
  }

  /**
   * Returns a list of content licenses for the given person
   * @param personId Person identifier
   */
  def licenses(personId: Long): List[LicenseView] = DB.withSession {
    implicit session: Session ⇒
      val query = for {
        license ← Licenses if license.licenseeId === personId
        brand ← license.brand
      } yield (license, brand)

      query.sortBy(_._2.name.toLowerCase).list.map {
        case (license, brand) ⇒ LicenseView(brand, license)
      }
  }

  /**
   * Returns list of licenses expiring this month
   */
  def expiring(): List[LicenseLicenseeView] = DB.withSession {
    implicit session: Session ⇒
      val lowerLimit = LocalDate.now().withDayOfMonth(1)
      val upperLimit = lowerLimit.plusMonths(1)
      val query = for {
        license ← Licenses if license.end >= lowerLimit && license.end < upperLimit
        person ← People if person.id === license.licenseeId
      } yield (license, person)
      query.list
        .map(v ⇒ LicenseLicenseeView(v._1, v._2))
        .sortBy(_.licensee.fullName)
  }
}

object LicenseService {
  private val instance = new LicenseService

  def get: LicenseService = instance
}