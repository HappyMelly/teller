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

import models.cm.brand.BrandTestimonial
import models.database.brand.BrandTestimonialTable
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Contains methods for managing brand testimonials in database
  */
class TestimonialRepository(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with BrandTestimonialTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val testimonials = TableQuery[BrandTestimonials]

  /**
    * Deletes the given link from database
    *
    * Brand identifier is for security reasons. If a user passes security
    * check for the brand, the user cannot delete testimonials which aren't belonged to
    * another brand.
    *
    * @param brandId Brand identifier
    * @param id Link identifier
    */
  def delete(brandId: Long, id: Long): Future[Int] = {
    val action = testimonials.filter(_.id === id).filter(_.brandId === brandId).delete
    db.run(action)
  }

  /**
    * Return list of testimonials for the given brand
    *
    * @param testimonialId Brand identifier
    */
  def find(testimonialId: Long): Future[Option[BrandTestimonial]] =
    db.run(testimonials.filter(_.id === testimonialId).result).map(_.headOption)

  /**
    * Return list of testimonials for the given brand
    *
    * @param brandId Brand identifier
    */
  def findByBrand(brandId: Long): Future[List[BrandTestimonial]] =
    db.run(testimonials.filter(_.brandId === brandId).result).map(_.toList)

  /**
    * Inserts the given testimonial to database
    *
    * @param testimonial Brand testimonial
    */
  def insert(testimonial: BrandTestimonial): Future[BrandTestimonial] = {
    val query = testimonials returning testimonials.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += testimonial)
  }

  /**
    * Updates brand testimonial in database
    *
    * @param testimonial Testimonital to update
    */
  def update(testimonial: BrandTestimonial): Future[Int] = {
    val action = testimonials.
      filter(_.id === testimonial.id.get).
      filter(_.brandId === testimonial.brand).
      update(testimonial)
    db.run(action)
  }
}
