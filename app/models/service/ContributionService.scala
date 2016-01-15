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

import models.ContributionView
import models.database.ContributionTable
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.Future

class ContributionService extends HasDatabaseConfig[JdbcProfile]
  with ContributionTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._

  /**
   * Returns a list of all contributions for the given contributor
   * @param contributorId Contributor identifier
   * @param isPerson If true this contributor is a person, otherwise - company
   */
  def contributions(contributorId: Long, isPerson: Boolean): Future[List[ContributionView]] = {
    val query = for {
      contribution ← TableQuery[Contributions] if contribution.contributorId === contributorId &&
        contribution.isPerson === isPerson
      product ← contribution.product
    } yield (contribution, product)

    db.run(query.sortBy(_._2.title.toLowerCase).result).map(_.toList.map {
      case (contribution, product) ⇒ ContributionView(product, contribution)
    })
  }
}

object ContributionService {
  private val instance = new ContributionService

  def get: ContributionService = instance
}