/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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
import play.api.db.slick.DB
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
  role: String) extends ActivityRecorder {

  /**
   * Returns identifier of the object
   */
  def identifier: Long = id.getOrElse(0)

  /**
   * Returns string identifier which can be understood by human
   *
   * For example, for object 'Person' human identifier is "[FirstName] [LastName]"
   */
  def humanIdentifier: String = "product with id = %s as %s".format(productId, role)

  /**
   * Returns type of this object
   */
  def objectType: String = Activity.Type.Contribution

  def product: Product = DB.withSession { implicit session: Session ⇒
    Product.find(this.productId).get
  }

  def insert: Contribution = DB.withSession { implicit session: Session ⇒
    val id = Contributions.forInsert.insert(this)
    this.copy(id = Some(id))
  }

}

case class ContributorView(name: String, uniqueName: String, id: Long, photo: Option[String], contribution: Contribution)

case class ContributionView(product: Product, contribution: Contribution)

object Contribution {

  /**
   * Returns a list of contributors for the given product
   */
  def contributors(productId: Long): List[ContributorView] = DB.withSession { implicit session: Session ⇒
    val peopleQuery = for {
      contribution ← Contributions if contribution.productId === productId && contribution.isPerson === true
      person ← People if person.id === contribution.contributorId
    } yield (contribution, person)

    val people = peopleQuery.list.map {
      case (contribution, person) ⇒
        ContributorView(person.firstName + " " + person.lastName, person.uniqueName, person.id.get, person.photo.url, contribution)
    }

    val orgQuery = for {
      contribution ← Contributions if contribution.productId === productId && contribution.isPerson === false
      organisation ← Organisations if organisation.id === contribution.contributorId
    } yield (contribution, organisation)

    val organisations = orgQuery.list.map {
      case (contribution, organisation) ⇒ ContributorView(organisation.name, "", organisation.id.get, Some(""), contribution)
    }

    List.concat(people, organisations).sortBy(_.name)
  }

  /**
   * Finds a contribution by ID.
   */
  def find(id: Long): Option[Contribution] = DB.withSession { implicit session: Session ⇒
    Query(Contributions).filter(_.id === id).firstOption
  }

  def delete(id: Long): Unit = DB.withSession { implicit session: Session ⇒
    Contributions.filter(_.id === id).mutate(_.delete)
  }

}
