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
package models.repository.brand

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.brand.PeerCredit
import models.database.brand.PeerCreditTable
import org.joda.time.DateTime
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Contains a set of methods for managing peer credit database records
  */
class PeerCreditRepository(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with PeerCreditTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val credits = TableQuery[PeerCredits]

  /**
    * Returns all peer credits for the given brand
 *
    * @param brandId Brand identifier
    */
  def find(brandId: Long): Future[Seq[PeerCredit]] =
    db.run(credits.filter(_.brandId === brandId).sortBy(_.created.desc).result)

  /**
    * Returns peer credits given by the given person in the given month
    * @param brandId Brand identifier
    * @param giverId Giver identifier
    * @param month Month
    */
  def find(brandId: Long, giverId: Long, month: DateTime): Future[Seq[PeerCredit]] = {
    val start = month.withDayOfMonth(1)
    val end = month.plusMonths(1).withDayOfMonth(1).minusDays(1)
    val query = credits.
      filter(_.brandId === brandId).
      filter(_.giverId === giverId).
      filter(_.created >= start).
      filter(_.created <= end)
    db.run(query.result)
  }
}
