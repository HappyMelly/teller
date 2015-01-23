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

import models.database.{ Accounts, OrganisationMemberships, Organisations }
import models.database.Organisations._
import models.service.ContributionService
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import scala.slick.lifted.Query

/**
 * Categories classifications that an organisation has zero or one of.
 */
object OrganisationCategory extends Enumeration {
  val LegalEntity = Value("legalentity")
  val BrandEntity = Value("brandentity")
}

/**
 * An organisation, usually a company, such as a Happy Melly legal entity.
 */
case class Organisation(
  id: Option[Long],
  name: String,
  street1: Option[String],
  street2: Option[String],
  city: Option[String],
  province: Option[String],
  postCode: Option[String],
  countryCode: String,
  vatNumber: Option[String],
  registrationNumber: Option[String],
  category: Option[OrganisationCategory.Value],
  webSite: Option[String],
  blog: Option[String],
  active: Boolean = true,
  created: DateTime = DateTime.now(),
  createdBy: String,
  updated: DateTime,
  updatedBy: String) extends AccountHolder {

  /**
   * Returns true if this person may be deleted.
   */
  lazy val deletable: Boolean = account.deletable && contributions.isEmpty && members.isEmpty

  lazy val members: List[Person] = DB.withSession { implicit session: Session ⇒
    val query = for {
      membership ← OrganisationMemberships if membership.organisationId === this.id
      person ← membership.person
    } yield person
    query.sortBy(_.lastName.toLowerCase).list
  }

  /**
   * Returns a list of this organisation's contributions.
   */
  lazy val contributions: List[ContributionView] = {
    ContributionService.get.contributions(this.id.get, isPerson = false)
  }

  /**
   * Inserts this organisation into the database, with an inactive account.
   * @return The Organisation as it is saved (with the id added)
   */
  def insert: Organisation = DB.withSession { implicit session: Session ⇒
    val organisationId = Organisations.forInsert.insert(this)
    Accounts.insert(Account(organisationId = Some(organisationId)))
    this.copy(id = Some(organisationId))
  }

  def update = DB.withSession { implicit session: Session ⇒
    assert(id.isDefined, "Can only update Organisations that have an id")
    val filter: Query[Organisations.type, Organisation] = Query(Organisations).filter(_.id === id)
    val q = filter.map { org ⇒ org.forUpdate }

    // Skip the created, createdBy and active fields.
    val updateTuple = (id, name, street1, street2, city, province, postCode, countryCode, vatNumber, registrationNumber,
      category, webSite, blog, updated, updatedBy)
    q.update(updateTuple)
    this
  }

}

object Organisation {

  /**
   * Activates the organisation, if the parameter is true, or deactivates it.
   */
  def activate(id: Long, active: Boolean): Unit = DB.withSession { implicit session: Session ⇒
    val query = for {
      organisation ← Organisations if organisation.id === id
    } yield organisation.active
    query.update(active)
  }

  /**
   * Deletes an organisation.
   */
  def delete(id: Long): Unit = DB.withSession { implicit session: Session ⇒
    find(id).map(_.account).map(_.delete)
    Organisations.where(_.id === id).mutate(_.delete())
  }

  def find(id: Long): Option[Organisation] = DB.withSession { implicit session: Session ⇒
    Query(Organisations).filter(_.id === id).list.headOption
  }

  def findAll: List[Organisation] = DB.withSession { implicit session: Session ⇒
    Query(Organisations).sortBy(_.name.toLowerCase).list
  }

}

