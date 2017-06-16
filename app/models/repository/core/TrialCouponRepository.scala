/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2017, Happy Melly http://www.happymelly.com
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

package models.repository.core

import models.core.TrialCoupon
import models.database.core.TrialCouponTable
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Contains methods for managing trial coupon records in database
  */
class TrialCouponRepository(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with TrialCouponTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._

  private val coupons = TableQuery[TrialCoupons]

  /**
    * Returns all existing coupons
    */
  def all: Future[Seq[TrialCoupon]] = db.run(coupons.result)

  /**
    * Deletes a coupon
    * @param code Coupon code
    */
  def delete(code: String): Future[Int] = db.run(coupons.filter(_.code === code.toUpperCase).delete)

  /**
    * Returns coupon with the given code if it exists
    *
    * @param code TrialCoupon code
    */
  def find(code: String): Future[Option[TrialCoupon]] =
    db.run(coupons.filter(_.code === code.toUpperCase).result.headOption)

  /**
    * Inserts the given coupon to database
    *
    * @param coupon Object to insert
    * @return Returns coupon object with updated id
    */
  def insert(coupon: TrialCoupon): Future[TrialCoupon] = {
    val withUpperCaseCode = coupon.copy(code = coupon.code.toUpperCase)
    val query = coupons returning coupons.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += withUpperCaseCode)
  }
}
