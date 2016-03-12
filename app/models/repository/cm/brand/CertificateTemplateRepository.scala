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

package models.repository.cm.brand

import models.cm.brand.CertificateTemplate
import models.database.brand.CertificateTemplateTable
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Contains a set of functions for managing templates in database
 */
class CertificateTemplateRepository(app: Application)  extends HasDatabaseConfig[JdbcProfile]
  with CertificateTemplateTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val templates = TableQuery[CertificateTemplates]

  /**
    * Deletes the given certificate template from database
 *
    * @param id Template identifier
    */
  def delete(id: Long): Future[Int] = db.run(templates.filter(_.id === id).delete)

  /**
    * Find a certificate file
    *
    * @param id Unique identifier
    * @return
    */
  def find(id: Long): Future[Option[CertificateTemplate]] =
    db.run(templates.filter(_.id === id).result).map(_.headOption)

  /**
   * Returns list of certificate templates for the given brand
   *
   * @param brandId Unique brand identifier
   */
  def findByBrand(brandId: Long): Future[List[CertificateTemplate]] =
    db.run(templates.filter(_.brandId === brandId).sortBy(_.language).result).map(_.toList)

  /**
    * Adds the given template to the database
 *
    * @param template Template
    */
  def insert(template: CertificateTemplate): Future[CertificateTemplate] = {
    val query = templates returning templates.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += template)
  }

}
