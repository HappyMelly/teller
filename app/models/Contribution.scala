/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models

import models.database.{ Contributions, People, Organisations }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current

/**
 * A contribution - an impact made by a person or an organisation to
 *   a development of a Product
 */
case class Contribution(
  id: Option[Long],
  contributorId: Long,
  productId: Long,
  isPerson: Boolean,
  role: String) {

  def product: Product = withSession { implicit session ⇒
    Product.find(this.productId).get
  }

  def insert: Contribution = withSession { implicit session ⇒
    val id = Contributions.forInsert.insert(this)
    this.copy(id = Some(id))
  }

}

case class ContributorView(name: String, id: Long, contribution: Contribution)

case class ContributionView(product: Product, contribution: Contribution)

object Contribution {

  /**
   * Returns a list of all contributions for the given contributor.
   */
  def contributions(contributorId: Long, isPerson: Boolean): List[ContributionView] = withSession { implicit session ⇒

    val query = for {
      contribution ← Contributions if contribution.contributorId === contributorId && contribution.isPerson === isPerson
      product ← contribution.product
    } yield (contribution, product)

    query.sortBy(_._2.title.toLowerCase).list.map {
      case (contribution, product) ⇒ ContributionView(product, contribution)
    }
  }

  def contributors(productId: Long): List[ContributorView] = withSession { implicit session ⇒
    val peopleQuery = for {
      contribution ← Contributions if contribution.productId === productId && contribution.isPerson === true
      person ← People if person.id === contribution.contributorId
    } yield (contribution, person)

    val people = peopleQuery.list.map {
      case (contribution, person) ⇒ ContributorView(person.firstName + " " + person.lastName, person.id.get, contribution)
    }

    val orgQuery = for {
      contribution ← Contributions if contribution.productId === productId && contribution.isPerson === false
      organisation ← Organisations if organisation.id === contribution.contributorId
    } yield (contribution, organisation)

    val organisations = orgQuery.list.map {
      case (contribution, organisation) ⇒ ContributorView(organisation.name, organisation.id.get, contribution)
    }

    List.concat(people, organisations).sortBy(_.name)
  }

  /**
   * Finds a contribution by ID.
   */
  def find(id: Long): Option[Contribution] = withSession { implicit session ⇒
    Query(Contributions).filter(_.id === id).firstOption
  }

  def delete(id: Long): Unit = withSession { implicit session ⇒
    Contributions.filter(_.id === id).mutate(_.delete)
  }

}
