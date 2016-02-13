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
package models.repository

import com.github.tototoshi.slick.MySQLJodaSupport._
import models._
import models.database._
import org.joda.time.LocalDate
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LicenseRepository(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with BrandTable
  with FacilitatorTable
  with LicenseTable
  with PersonTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val licenses = TableQuery[Licenses]

  /**
   * Adds the given license to database and updates all related tables
   * @param license License
   * @return Returns the updated license with a valid id
   */
  def add(license: License): Future[License] = {
    val insertAction = licenses returning licenses.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    val profileQuery = TableQuery[Facilitators].filter(_.brandId === license.brandId).
      filter(_.personId === license.licenseeId).exists
    val actions = (for {
      result <- insertAction += license
      status <- profileQuery.result
      _ <- if (status) {
        DBIO.successful(true)
      } else {
        val facilitator = Facilitator(None, license.licenseeId, license.brandId)
        TableQuery[Facilitators] += facilitator
      }
    } yield result).transactionally
    db.run(actions)
  }

  /**
   * Returns a list of active content licenses for the given person
   * @param personId Person identifier
   */
  def activeLicenses(personId: Long): Future[List[LicenseView]] = {
    val query = for {
      license ← licenses if license.licenseeId === personId && license.end >= LocalDate.now
      brand ← license.brand
    } yield (license, brand)

    db.run(query.sortBy(_._2.name.toLowerCase).result).map(_.toList.map {
      case (license, brand) ⇒ LicenseView(brand, license)
    })
  }

  /**
   * Returns active license for the given person and brand if it exists
   *
   * @param brandId Brand identifier
   * @param personId Person identifier
   */
  def activeLicense(brandId: Long, personId: Long): Future[Option[License]] = {
    val query = licenses
      .filter(_.licenseeId === personId)
      .filter(_.brandId === brandId)
      .filter(_.start <= LocalDate.now())
      .filter(_.end >= LocalDate.now())
    db.run(query.result).map(_.headOption)
  }

  /**
    * Returns a list of all people who have ever been licensed for the given brand
    */
  def allLicensees(brandId: Long): Future[List[Person]] = {
    val query = for {
      license ← licenses if license.brandId === brandId
      licensee ← license.licensee if licensee.active === true
    } yield licensee
    db.run(query.sortBy(_.lastName.toLowerCase).result).map(_.toList)
  }

  /**
    * Deletes the given license from database
    * @param id License identifier
    */
  def delete(id: Long): Unit = db.run(licenses.filter(_.id === id).delete)

  /**
   * Returns list of licenses expiring this month for the given brands
   *
   * @param brands List of brands we want expiring license data from
   */
  def expiring(brands: List[Long]): Future[List[LicenseLicenseeView]] = {
    val firstDay = LocalDate.now().withDayOfMonth(1)
    val lowerLimit = firstDay.minusMonths(1)
    val upperLimit = firstDay.plusMonths(1)
    val query = for {
      license ← licenses if license.end >= lowerLimit && license.end < upperLimit
      person ← TableQuery[People] if person.id === license.licenseeId
    } yield (license, person)
    db.run(query.filter(_._1.brandId inSet brands).result).map(_.toList.map {v ⇒
      LicenseLicenseeView(v._1, v._2)
    }.sortBy(_.licensee.fullName))
  }

  /**
    * Returns the requested license if exists
    * @param id License id
    */
  def find(id: Long): Future[Option[License]] = db.run(licenses.filter(_.id === id).result).map(_.headOption)

  /**
   * Returns list of all licenses
   */
  def findAll: Future[List[License]] = db.run(licenses.result).map(_.toList)

  /**
   * Returns list of active licenses
   */
  def findActive: Future[List[License]] =
    db.run(licenses.filter(_.start <= LocalDate.now()).filter(_.end >= LocalDate.now()).result).map(_.toList)

  /**
   * Returns list of licenses for the given brand
   * @param brandId Brand id
   */
  def findByBrand(brandId: Long): Future[List[License]] =
    db.run(licenses.filter(_.brandId === brandId).result).map(_.toList)

  /**
   * Finds a license by ID, joined with brand and licensee
   *
   * @param id License id
   */
  def findWithBrandAndLicensee(id: Long): Future[Option[LicenseLicenseeBrandView]] = {
    val query = for {
      license ← licenses if license.id === id
      brand ← license.brand
      licensee ← license.licensee
    } yield (license, brand, licensee)
    db.run(query.result).map(_.headOption.map { result =>
      LicenseLicenseeBrandView(result._1, result._2, result._3)
    })
  }

  /**
   * Returns a list of content licenses for the given person
   * @param personId Person identifier
   */
  def licenses(personId: Long): Future[List[License]] =
    db.run(licenses.filter(_.licenseeId === personId).result).map(_.toList)

  /**
    * Returns a list of content licenses for the given person
    * @param personId Person identifier
    */
  def licensesWithBrands(personId: Long): Future[List[LicenseView]] = {
    val query = for {
      license ← licenses if license.licenseeId === personId
      brand ← license.brand
    } yield (license, brand)
    db.run(query.sortBy(_._2.name.toLowerCase).result).map(_.toList.map { result =>
      LicenseView(result._2, result._1)
    })
  }

  /**
   * Returns a list of people who are licensed for the given brand on the given
   * date, usually today
   *
   * @param brandId Brand id
   * @param date Date of interest
   */
  def licensees(brandId: Long, date: LocalDate = LocalDate.now()): Future[List[Person]] = {
    val query = for {
      license ← licenses if license.start <= date && license.end >= date && license.brandId === brandId
      licensee ← license.licensee if licensee.active === true
    } yield licensee
    db.run(query.sortBy(_.lastName.toLowerCase).result).map(_.toList)
  }

  /**
   * Updates the given license in the database.
   *
   * @param license License object
   */
  def update(license: License): Future[Int] = db.run(licenses.filter(_.id === license.id).update(license))

}