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
import models.cm.facilitator.{MailChimpList, FacilitatorCountry, FacilitatorLanguage}
import models.database.{MailChimpListTable, FacilitatorCountryTable, FacilitatorLanguageTable, FacilitatorTable}
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Contains all database-related operations for facilitator settings (countries, languages, integrations)
 */
class FacilitatorSettingsRepository(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with FacilitatorCountryTable
  with FacilitatorLanguageTable
  with MailChimpListTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val countries = TableQuery[FacilitatorCountries]
  private val languages = TableQuery[FacilitatorLanguages]
  private val lists = TableQuery[MailChimpLists]

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
    * Deletes the given list from the given person
    *
    * @param personId Person identifier
    * @param listId List identifier
    */
  def deleteList(personId: Long, listId: Long): Future[Int] =
    db.run(lists.filter(_.personId === personId).filter(_.id === listId).delete)


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
    * Inserts the given list to DB
    */
  def insertList(list: MailChimpList): Future[MailChimpList] =
    db.run(lists += list).map(_ => list)

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
    * Returns list of MailChimp lists the given facilitator
    *
    * @param personId Person identifier
    */
  def lists(personId: Long): Future[Seq[MailChimpList]] = db.run(lists.filter(_.personId === personId).result)

}

