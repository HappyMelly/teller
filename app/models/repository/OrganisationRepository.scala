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
package models.repository

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.core.payment.CustomerType
import models.database._
import models.{Member, OrgView, Organisation, Person, ProfileType}
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OrganisationRepository(app: Application, repos: Repositories) extends HasDatabaseConfig[JdbcProfile]
  with MemberTable
  with OrganisationTable
  with OrganisationMembershipTable
  with SocialProfileTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._

  private val orgs = TableQuery[Organisations]

  /**
   * Activates the organisation, if the parameter is true, or deactivates it
   *
   * @param id Organisation identifier
   * @param active Activate/deactivate flag
   */
  def activate(id: Long, active: Boolean): Unit =
    db.run(orgs.filter(_.id === id).map(_.active).update(active))

  /**
   * Deletes the given organisation
   *
   * @param id Organisation identifier
   */
  def delete(id: Long): Future[Int] = {
    repos.member.delete(id, person = false)
    repos.core.customer.delete(id, CustomerType.Organisation)
    repos.socialProfile.delete(id, ProfileType.Organisation)
    db.run(orgs.filter(_.id === id).delete)
  }

  /**
   * Returns list of organisation for the given ids
   *
   * @param objectIds List of identifiers
   */
  def find(objectIds: Seq[Long]): Future[List[Organisation]] = if (objectIds.isEmpty)
    Future.successful(List())
  else
    db.run(orgs.filter(_.id inSet objectIds).result).map(_.toList)

  /** Returns list of active organisations */
  def findActive: Future[List[Organisation]] =
    db.run(orgs.filter(_.active === true).sortBy(_.name.toLowerCase).result).map(_.toList)

  /** Returns list of all organisation in the system */
  def findAll: Future[List[Organisation]] =
    db.run(orgs.sortBy(_.name.toLowerCase).result).map(_.toList)

  /** Returns list of organisations which are not members (yet!) */
  def findNonMembers: Future[List[Organisation]] = {
    import scala.language.postfixOps
    val actions = for {
      members <- TableQuery[Members].filter(_.person === false).map(_.objectId).result
      organisations <- orgs.filterNot(_.id inSet members).sortBy(_.name).result
    } yield organisations
    db.run(actions).map(_.toList)
  }

  /**
   * Returns organisation if exists, otherwise None
    *
    * @param id Organisation id
   */
  def find(id: Long): Future[Option[Organisation]] = db.run(orgs.filter(_.id === id).result).map(_.headOption)

  /**
   * Returns organisation if it exists, otherwise - None
   *
   * @param identifier Organisation identifier
   */
  def find(identifier: String): Future[Option[Organisation]] = {
    val transformed = identifier.replace(".", " ")
    val query = for {
      org ← orgs if org.name.toLowerCase like "%" + transformed + "%"
    } yield org
    db.run(query.result).map(_.headOption)
  }

  /**
   * Return the requested organisation with its social profile if exists
   *
   * @param id Organisation id
   */
  def findWithProfile(id: Long): Future[Option[OrgView]] = {
    import SocialProfilesStatic._

    val query = for {
      org ← orgs if org.id === id
      profile ← TableQuery[SocialProfiles] if profile.objectId === id &&
        profile.objectType === ProfileType.Organisation
    } yield (org, profile)
    db.run(query.result).map(_.headOption.map(x ⇒ OrgView(x._1, x._2)))
  }

  /**
    * Returns the requested organisation
    *
    * @param id Organisation id
    */
  def get(id: Long): Future[Organisation] = db.run(orgs.filter(_.id === id).result).map(_.head)

  /**
   * Inserts the given organisation into the database, with an inactive account
   *
   * @param view Organisation with its social profile
   * @return The Organisation as it is saved (with the id added) and social profile
   */
  def insert(view: OrgView): Future[OrgView] = {
    val query = orgs returning orgs.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += view.org).map { org =>
      repos.socialProfile.insert(view.profile.copy(objectId = org.identifier))
      OrgView(org, view.profile)
    }
  }

  /**
   * Returns member data if org is a member, false None
    *
    * @param id Organisation id
   */
  def member(id: Long): Future[Option[Member]] =
    db.run(TableQuery[Members].filter(_.objectId === id).filter(_.person === false).result).map(_.headOption)

  /**
   * Updates the given organisation in the database
   *
   * @param view Organisation with social profile
   * @return the given organisation with social profile
   */
  def update(view: OrgView): Future[OrgView] = {
    assert(view.org.id.isDefined, "Can only update Organisations that have an id")
    repos.socialProfile.update(view.profile, view.profile.objectType)
    update(view.org).map { value =>
      OrgView(value, view.profile)
    }
  }

  /**
   * Returns list of people working in the given organisation
   *
   * @param id OrganisationId
   */
  def people(id: Long): Future[List[Person]] = {
    val query = for {
      relation ← TableQuery[OrganisationMemberships] if relation.organisationId === id
      person ← relation.person
    } yield person
    db.run(query.sortBy(_.lastName.toLowerCase).result).map(_.toList)
  }

  /**
   * Returns list of organisations by part of their names
   *
   * @param needle Search string
   */
  def search(needle: String): Future[List[Organisation]] = {
    val query = for {
      org ← orgs if org.name.toLowerCase like "%" + needle.toLowerCase + "%"
    } yield org
    db.run(query.result).map(_.toList)
  }

  /**
   * Updates the given organisation in the database
   *
   * @param org Organisation
   * @return the given organisation
   */
  def update(org: Organisation): Future[Organisation] = {
    assert(org.id.isDefined, "Can only update Organisations that have an id")
    val query = orgs.filter(_.id === org.id).map(_.forUpdate)

    // Skip the created, createdBy and active fields.
    val updateTuple = (org.id, org.name, org.street1,
      org.street2, org.city, org.province, org.postCode,
      org.countryCode, org.vatNumber, org.registrationNumber,
      org.webSite, org.blog, org.contactEmail, org.about,
      org.active, org.dateStamp.updated, org.dateStamp.updatedBy)
    db.run(query.update(updateTuple)).map(_ => org)
  }

  /**
   * Updates if the given organisation has or doesn't have a logo
   *
   * @param id Organisation identifier
   * @param logo Activate/deactivate flag
   */
  def updateLogo(id: Long, logo: Boolean): Unit =
    db.run(orgs.filter(_.id === id).map(_.logo).update(logo))

}