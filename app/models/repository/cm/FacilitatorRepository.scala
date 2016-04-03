/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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

package models.repository.cm

import models.cm.Facilitator
import models.cm.facilitator.{FacilitatorCountry, FacilitatorLanguage}
import models.database.{FacilitatorCountryTable, FacilitatorLanguageTable, FacilitatorTable, ProfileStrengthTable}
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Contains all database-related operations
 */
class FacilitatorRepository(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with FacilitatorTable
  with FacilitatorCountryTable
  with FacilitatorLanguageTable
  with ProfileStrengthTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val countries = TableQuery[FacilitatorCountries]
  private val facilitators = TableQuery[Facilitators]
  private val languages = TableQuery[FacilitatorLanguages]

  /**
    * Finds all countries for a particular facilitator
    */
  def countries(personId: Long): Future[List[FacilitatorCountry]] =
    db.run(countries.filter(_.personId === personId).result).map(_.toList)

  /**
    * Finds all countries for the set of facilitators
    */
  def countries(ids: List[Long]): Future[List[FacilitatorCountry]] =
    db.run(countries.filter(_.personId inSet ids).result).map(_.toList)

  /**
    * Deletes the given country from the given person
    *
    * @param personId Person identifier
    * @param country Country
    */
  def deleteCountry(personId: Long, country: String): Future[Int] =
    db.run(countries.filter(_.personId === personId).filter(_.country === country).delete)

  /**
    * Deletes the given language from the given person
    *
    * @param personId Person identifier
    * @param language Language
    */
  def deleteLanguage(personId: Long, language: String): Future[Int] =
    db.run(languages.filter(_.personId === personId).filter(_.language === language).delete)

  /**
   * Inserts the given facilitator to database
    *
    * @param facilitator Facilitator
   * @return Returns the updated facilitator with a valid id
   */
  def insert(facilitator: Facilitator): Future[Facilitator] = {
    val query = facilitators returning facilitators.map(_.id) into ((value, id) => value.copy(id = id))
    db.run(query += facilitator)
  }

  /**
    * Inserts the given country to DB
    */
  def insertCountry(country: FacilitatorCountry): Future[FacilitatorCountry] =
    db.run(countries += country).map(_ => country)

  /**
    * Inserts the given language to DB
    */
  def insertLanguage(language: FacilitatorLanguage): Future[FacilitatorLanguage] =
    db.run(languages += language).map(_ => language)

  /**
   * Returns facilitator if it exists, otherwise - None
    *
    * @param brandId Brand id
   * @param personId Person id
   */
  def find(brandId: Long, personId: Long): Future[Option[Facilitator]] =
    db.run(facilitators.filter(_.personId === personId).filter(_.brandId === brandId).result).map(_.headOption)

  /**
    * Returns facilitators for the given person ids
    *
    * @param brandId Brand id
    * @param personIds Person ids
    * @return
    */
  def find(brandId: Long, personIds: Seq[Long]): Future[List[Facilitator]] =
    db.run(facilitators.filter(_.brandId === brandId).filter(_.personId inSet personIds).result).map(_.toList)

  /**
   * Returns list of all facilitators
   */
  def findAll: Future[List[Facilitator]] = db.run(facilitators.result).map(_.toList)

  /**
   * Returns list of facilitator records for the given person
    *
    * @param personId Person id
   */
  def findByPerson(personId: Long): Future[List[Facilitator]] =
    db.run(facilitators.filter(_.personId === personId).result).map(_.toList)

  /**
   * Returns list of facilitator records for the given brand
    *
    * @param brandId Brand id
   * @return
   */
  def findByBrand(brandId: Long): Future[List[Facilitator]] =
    db.run(facilitators.filter(_.brandId === brandId).result).map(_.toList)

  /**
    * Returns list of languages the given facilitator talks
    *
    * @param personId Person identifier
    */
  def languages(personId: Long): Future[List[FacilitatorLanguage]] =
    db.run(languages.filter(_.personId === personId).result).map(_.toList)

  /**
    * Returns list of languages for the set of facilitators
    *
    * @param ids Facilitator identifiers
    */
  def languages(ids: List[Long]): Future[List[FacilitatorLanguage]] =
    db.run(languages.filter(_.personId inSet ids).result).map(_.toList)

  /**
   * Updates the given facilitator in database
    *
    * @param facilitator Facilitator
   * @return Retunrs the given facilitator
   */
  def update(facilitator: Facilitator): Future[Facilitator] = {
    val action = facilitators.
      filter(_.personId === facilitator.personId).
      filter(_.brandId === facilitator.brandId).
      map(_.forUpdate).
      update((facilitator.publicRating, facilitator.privateRating,
        facilitator.publicMedian, facilitator.privateMedian,
        facilitator.publicNps, facilitator.privateNps,
        facilitator.numberOfPublicEvaluations, facilitator.numberOfPrivateEvaluations,
        facilitator.creditsGiven, facilitator.creditsReceived, facilitator.postEventTemplate))
    db.run(action).map(_ => facilitator)
  }

  /**
    * Updates badges for the given facilitator in the database
    *
    * @param facilitator Facilitator
    */
  def updateBadges(facilitator: Facilitator): Future[Facilitator] = {
    val badges = if (facilitator.badges.isEmpty) None else Option[String](facilitator.badges.mkString(","))
    val action = facilitators.
      filter(_.personId === facilitator.personId).
      filter(_.brandId === facilitator.brandId).
      map(_.badges).update(badges)
    db.run(action).map(_ => facilitator)
  }

  /**
   * Updates the experience of the given facilitator in database
    *
    * @param facilitator Facilitator
   */
  def updateExperience(facilitator: Facilitator) = {
    val action = facilitators.
      filter(_.personId === facilitator.personId).
      filter(_.brandId === facilitator.brandId).
      map(record => (record.numberOfEvents, record.yearsOfExperience)).
      update((facilitator.numberOfEvents, facilitator.yearsOfExperience))
    db.run(action)
  }
}

