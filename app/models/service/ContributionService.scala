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

import models.database.{ContributionTable, OrganisationTable, PersonTable}
import models.{Contribution, ContributionView, ContributorView}
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ContributionService(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with ContributionTable
  with OrganisationTable
  with PersonTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val contributions = TableQuery[Contributions]

  /**
   * Returns a list of all contributions for the given contributor
 *
   * @param contributorId Contributor identifier
   * @param isPerson If true this contributor is a person, otherwise - company
   */
  def contributions(contributorId: Long, isPerson: Boolean): Future[List[ContributionView]] = {
    val query = for {
      contribution ← contributions if contribution.contributorId === contributorId &&
        contribution.isPerson === isPerson
      product ← contribution.product
    } yield (contribution, product)

    db.run(query.sortBy(_._2.title.toLowerCase).result).map(_.toList.map {
      case (contribution, product) ⇒ ContributionView(product, contribution)
    })
  }

  /**
    * Returns a list of contributors for the given product
    */
  def contributors(productId: Long): Future[List[ContributorView]] = {
    val peopleQuery = for {
      contribution ← contributions if contribution.productId === productId && contribution.isPerson === true
      person ← TableQuery[People] if person.id === contribution.contributorId
    } yield (contribution, person)

    val orgQuery = for {
      contribution ← contributions if contribution.productId === productId && contribution.isPerson === false
      organisation ← TableQuery[Organisations] if organisation.id === contribution.contributorId
    } yield (contribution, organisation)

    val actions = for {
      p <- peopleQuery.result
      o <- orgQuery.result
    } yield (p, o)
    db.run(actions) map { case (people, orgs) =>
      val peopleView = people.toList.map {
        case (contribution, person) ⇒
          ContributorView(person.firstName + " " + person.lastName, person.uniqueName, person.id.get, person.photo.url, contribution)
      }
      val orgView = orgs.toList.map {
        case (contribution, organisation) ⇒ ContributorView(organisation.name, "", organisation.id.get, Some(""), contribution)
      }
      List.concat(peopleView, orgView).sortBy(_.name)
    }
  }

  /**
    * Deletes the given contribution from database
    *
    * @param id Contribution identifier
    */
  def delete(id: Long): Future[Int] = db.run(contributions.filter(_.id === id).delete)

  /**
    * Inserts new contribution to database
    * @param contribution Contribution
    */
  def insert(contribution: Contribution): Future[Contribution] = {
    val query = contributions returning contributions.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += contribution)
  }

  /**
    * Returns the given contribution if exists
 *
    * @param id Contribution identifier
    */
  def find(id: Long): Future[Option[Contribution]] =
    db.run(contributions.filter(_.id === id).result).map(_.headOption)
}
