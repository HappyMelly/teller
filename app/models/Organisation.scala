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
import models.service.{ Services, OrganisationService, MemberService }
import org.joda.money.Money
import org.joda.time.{ DateTime, LocalDate }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import scala.slick.lifted.Query

case class OrgView(org: Organisation, profile: SocialProfile)

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
    webSite: Option[String],
    blog: Option[String],
    contactEmail: Option[String],
    customerId: Option[String] = None,
    about: Option[String] = None,
    logo: Boolean = false,
    active: Boolean = true,
    dateStamp: DateStamp) extends AccountHolder with ActivityRecorder with Services {

  /** Contains a list of people working in this organisation */
  private var _people: Option[List[Person]] = None
  private var _member: Option[Member] = None

  /**
   * Returns true if this person may be deleted.
   */
  lazy val deletable: Boolean = account.deletable && contributions.isEmpty && people.isEmpty

  /**
   * Sets a new list of employees
   * @param people New employees
   */
  def people_=(people: List[Person]) = {
    _people = Some(people)
  }

  /**
   * Returns a list of people working in this organisation
   */
  def people: List[Person] = _people getOrElse {
    val people = orgService.people(this.id.get)
    people_=(people)
    people
  }

  /**
   * Sets member data
   * @param member Member data
   */
  def member_=(member: Member): Unit = _member = Some(member)

  /** Returns member data if person is a member, false None */
  def member: Option[Member] = _member map { Some(_) } getOrElse {
    id map { i ⇒
      _member = orgService.member(i)
      _member
    } getOrElse None
  }

  /**
   * Adds member record to database
   * @param funder Defines if this org becomes a funder
   * @param fee Amount of membership fee this org paid
   * @param userId Id of the user executing the action
   * @return Returns member object
   */
  def becomeMember(funder: Boolean, fee: Money, userId: Long): Member = {
    val m = new Member(None, id.get, person = false, funder = funder, fee = fee,
      renewal = true, since = LocalDate.now(),
      until = LocalDate.now().plusYears(1), existingObject = true,
      reason = None, created = DateTime.now(), userId, DateTime.now(), userId)
    memberService.insert(m)
  }

  /**
   * Returns a list of this organisation's contributions.
   */
  lazy val contributions: List[ContributionView] = {
    contributionService.contributions(this.id.get, isPerson = false)
  }

  /**
   * Returns identifier of the object
   */
  def identifier: Long = id.getOrElse(0)

  /**
   * Returns string identifier which can be understood by human
   *
   * For example, for object 'Person' human identifier is "[FirstName] [LastName]"
   */
  def humanIdentifier: String = name

  /**
   * Returns type of this object
   */
  def objectType: String = Activity.Type.Org

}

object Organisation {

  /**
   * Returns logo for the given organisation
   *
   * @param id Organisation identifier
   */
  def logo(id: Long): File =
    File.image(s"organisations/$id", s"organisations.$id")

  /**
   * Returns an organisation with only two required fields filled
   * @param name Organisation name
   * @param countryCode Country of residence
   */
  def apply(name: String, countryCode: String): Organisation = {
    val date = DateStamp(createdBy = "", updated = DateTime.now(), updatedBy = "")
    Organisation(None, name, None, None, None, None, None, countryCode,
      None, None, None, None, None, None, None, logo = false, active = false, date)
  }
}

