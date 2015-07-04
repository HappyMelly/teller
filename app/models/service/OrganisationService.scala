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

import models.{ Organisation, Member, Account, OrgView, ProfileType, Person }
import models.database.{ Members, Organisations, Accounts, SocialProfiles, OrganisationMemberships }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

import scala.slick.lifted.Query

class OrganisationService extends Services {

  /**
   * Activates the organisation, if the parameter is true, or deactivates it
   *
   * @param id Organisation identifier
   * @param active Activate/deactivate flag
   */
  def activate(id: Long, active: Boolean): Unit = DB.withSession {
    implicit session: Session ⇒
      val query = for {
        organisation ← Organisations if organisation.id === id
      } yield organisation.active
      query.update(active)
  }

  /**
   * Deletes the given organisation
   *
   * @param id Organisation identifier
   */
  def delete(id: Long): Unit = DB.withTransaction { implicit session: Session ⇒
    find(id) foreach { org ⇒
      org.account.delete()
      memberService.delete(id, person = false)
      socialProfileService.delete(id, ProfileType.Organisation)
    }
    Organisations.where(_.id === id).mutate(_.delete())
  }

  /** Returns list of active organisations */
  def findActive: List[Organisation] = DB.withSession {
    implicit session: Session ⇒
      Query(Organisations).
        filter(_.active === true).
        sortBy(_.name.toLowerCase).
        list
  }

  /** Returns list of all organisation in the system */
  def findAll: List[Organisation] = DB.withSession { implicit session: Session ⇒
    Query(Organisations).sortBy(_.name.toLowerCase).list
  }

  /** Returns list of organisations which are not members (yet!) */
  def findNonMembers: List[Organisation] = DB.withSession { implicit session ⇒
    import scala.language.postfixOps

    val members = for { m ← Members if m.person === false } yield m.objectId
    val ids = members.list
    Query(Organisations).filter(row ⇒ !(row.id inSet ids)).sortBy(_.name).list
  }

  /**
   * Returns organisation if exists, otherwise None
   * @param id Organisation id
   */
  def find(id: Long): Option[Organisation] = DB.withSession {
    implicit session: Session ⇒
      Query(Organisations).filter(_.id === id).firstOption
  }

  /**
   * Return the requested organisation with its social profile if exists
   *
   * @param id Organisation id
   */
  def findWithProfile(id: Long): Option[OrgView] = DB.withSession {
    implicit session: Session ⇒
      import models.database.SocialProfiles._

      val query = for {
        org ← Organisations if org.id === id
        profile ← SocialProfiles if profile.objectId === id && profile.objectType === ProfileType.Organisation
      } yield (org, profile)
      query.firstOption map { x ⇒ OrgView(x._1, x._2) }
  }

  /**
   * Inserts the given organisation into the database, with an inactive account
   *
   * @param view Organisation with its social profile
   * @return The Organisation as it is saved (with the id added) and social profile
   */
  def insert(view: OrgView): OrgView = DB.withTransaction {
    implicit session: Session ⇒
      val organisationId = Organisations.forInsert.insert(view.org)
      Accounts.insert(Account(organisationId = Some(organisationId)))
      socialProfileService.insert(view.profile.copy(objectId = organisationId))
      OrgView(view.org.copy(id = Some(organisationId)), view.profile)
  }

  /**
   * Returns member data if org is a member, false None
   * @param id Organisation id
   */
  def member(id: Long): Option[Member] = DB.withSession { implicit session ⇒
    Query(Members).
      filter(_.objectId === id).
      filter(_.person === false).firstOption
  }

  /**
   * Updates the given organisation in the database
   *
   * @param view Organisation with social profile
   * @return the given organisation with social profile
   */
  def update(view: OrgView): OrgView = DB.withTransaction { implicit session: Session ⇒
    import models.database.SocialProfiles._

    assert(view.org.id.isDefined, "Can only update Organisations that have an id")
    socialProfileService.update(view.profile, view.profile.objectType)

    OrgView(update(view.org), view.profile)
  }

  /**
   * Returns list of people working in the given organisation
   *
   * @param id OrganisationId
   */
  def people(id: Long): List[Person] = DB.withSession { implicit session: Session ⇒
    val query = for {
      relation ← OrganisationMemberships if relation.organisationId === id
      person ← relation.person
    } yield person
    query.sortBy(_.lastName.toLowerCase).list
  }

  /**
   * Returns list of organisations by part of their names
   *
   * @param needle Search string
   */
  def search(needle: String): List[Organisation] = DB.withSession {
    implicit session: Session ⇒
      val query = for {
        org ← Organisations if org.name.toLowerCase like "%" + needle.toLowerCase + "%"
      } yield org

      query.list
  }

  /**
   * Updates the given organisation in the database
   *
   * @param org Organisation
   * @return the given organisation
   */
  def update(org: Organisation): Organisation = DB.withSession {
    implicit session: Session ⇒

      assert(org.id.isDefined, "Can only update Organisations that have an id")
      val query = Query(Organisations).filter(_.id === org.id).map(_.forUpdate)

      // Skip the created, createdBy and active fields.
      val updateTuple = (org.id, org.name, org.street1,
        org.street2, org.city, org.province, org.postCode,
        org.countryCode, org.vatNumber, org.registrationNumber,
        org.webSite, org.blog, org.customerId, org.about,
        org.active, org.dateStamp.updated, org.dateStamp.updatedBy)
      query.update(updateTuple)
      org
  }

  /**
   * Updates if the given organisation has or doesn't have a logo
   *
   * @param id Organisation identifier
   * @param logo Activate/deactivate flag
   */
  def updateLogo(id: Long, logo: Boolean): Unit = DB.withSession {
    implicit session: Session ⇒
      val query = for {
        organisation ← Organisations if organisation.id === id
      } yield organisation.logo
      query.update(logo)
  }

}

object OrganisationService {
  private val instance = new OrganisationService

  def get: OrganisationService = instance
}