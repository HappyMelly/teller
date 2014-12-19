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

import fly.play.s3.{ BucketFile, S3Exception }
import models.database._
import org.joda.time.{ DateTime, LocalDate }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import scala.slick.lifted.Query
import scala.util.matching.Regex
import services.S3Bucket

/**
 * Represents a date stamp to track when an object was changed/created
 * @param created Date and time when the object was created
 * @param createdBy Name of a person who created the object
 * @param updated Date and time when the object was updated
 * @param updatedBy Name of a person who updated the object
 */
case class DateStamp(
  created: DateTime = DateTime.now(),
  createdBy: String,
  updated: DateTime,
  updatedBy: String)

case class Photo(id: Option[String], url: Option[String])

object Photo {

  def parse(url: Option[String]): Photo = {
    url.map {
      case s if s.contains("facebook") ⇒ Photo(Some("facebook"), url)
      case s if s.contains("gravatar") ⇒ Photo(Some("gravatar"), url)
      case _ ⇒ Photo(None, None)
    }.getOrElse(Photo(None, None))
  }
}

/**
 * Currently there're two roles for a person: stakeholer or board member
 */
object PersonRole extends Enumeration {
  val NoRole = Value("0")
  val Stakeholder = Value("1")
  val BoardMember = Value("2")

  implicit val personRoleTypeMapper = MappedTypeMapper.base[PersonRole.Value, Int](
    { role ⇒ role.id }, { id ⇒ PersonRole(id) })
}

/**
 * A person, such as the owner or employee of an organisation.
 */
case class Person(
  id: Option[Long],
  firstName: String,
  lastName: String,
  birthday: Option[LocalDate],
  photo: Photo,
  signature: Boolean,
  address: Address,
  bio: Option[String],
  interests: Option[String],
  role: PersonRole.Value = PersonRole.Stakeholder,
  webSite: Option[String],
  blog: Option[String],
  virtual: Boolean = false,
  active: Boolean = true,
  dateStamp: DateStamp) extends AccountHolder {

  private var _socialProfile: Option[SocialProfile] = None

  def socialProfile: SocialProfile = if (_socialProfile.isEmpty) {
    DB.withSession { implicit session: Session ⇒
      socialProfile_=(SocialProfile.find(id.getOrElse(0), ProfileType.Person))
      _socialProfile.get
    }
  } else {
    _socialProfile.get
  }

  def socialProfile_=(socialProfile: SocialProfile): Unit = {
    _socialProfile = Some(socialProfile)
  }

  def fullName: String = firstName + " " + lastName

  def uniqueName: String = fullName.toLowerCase.replace(" ", ".")

  def name = fullName

  def fullNamePossessive = if (lastName.endsWith("s")) s"$fullName’" else s"$fullName’s"

  /**
   * Associates this person with given organisation.
   */
  def addMembership(organisationId: Long): Unit = DB.withSession { implicit session: Session ⇒
    OrganisationMemberships.forInsert.insert(this.id.get, organisationId)
  }

  /**
   * Returns true if it is possible to grant log in access to this user.
   */
  def canHaveUserAccount: Boolean = socialProfile.defined

  /**
   * Returns true if this person may be deleted.
   */
  lazy val deletable: Boolean = account.deletable && contributions.isEmpty && memberships.isEmpty && licenses.isEmpty

  /**
   * Removes this person’s membership in the given organisation
   *
   * @param organisationId Organisation identifier
   */
  def deleteMembership(organisationId: Long): Unit = DB.withSession { implicit session: Session ⇒
    OrganisationMemberships.filter(membership ⇒ membership.personId === id && membership.organisationId === organisationId).mutate(_.delete)
  }

  /**
   * Returns a list of this person’s content licenses.
   */
  lazy val licenses: List[LicenseView] = DB.withSession { implicit session: Session ⇒

    val query = for {
      license ← Licenses if license.licenseeId === this.id
      brand ← license.brand
    } yield (license, brand)

    query.sortBy(_._2.name.toLowerCase).list.map {
      case (license, brand) ⇒ LicenseView(brand, license)
    }
  }

  /**
   * Returns a list of this person's contributions.
   */
  lazy val contributions: List[ContributionView] = DB.withSession { implicit session: Session ⇒
    Contribution.contributions(this.id.get, isPerson = true)
  }

  /**
   * Returns a list of the organisations this person is a member of.
   */
  lazy val memberships: List[Organisation] = DB.withSession { implicit session: Session ⇒
    val query = for {
      membership ← OrganisationMemberships if membership.personId === this.id
      organisation ← membership.organisation
    } yield organisation
    query.sortBy(_.name.toLowerCase).list
  }

  /**
   * A list of languages a facilitator speaks
   */
  lazy val languages: List[FacilitatorLanguage] = DB.withSession { implicit session: Session ⇒
    FacilitatorLanguage.findByFacilitator(id.get)
  }

  /**
   * A list of countries where a facilitator is ready to run events
   */
  lazy val countries: List[FacilitatorCountry] = DB.withSession { implicit session: Session ⇒
    FacilitatorCountry.findByFacilitator(id.get)
  }

  /**
   * Inserts this person into the database and returns the saved Person, with the ID added.
   */
  def insert: Person = DB.withTransaction { implicit session: Session ⇒
    val newAddress = Address.insert(this.address)
    val personId = People.forInsert.insert(this.copy(address = newAddress))
    SocialProfile.insert(socialProfile.copy(objectId = personId))
    Accounts.insert(Account(personId = Some(personId)))
    this.copy(id = Some(personId))
  }

  /**
   * Updates this person in the database and returns the saved person.
   */
  def update: Person = DB.withSession { implicit session: Session ⇒
    session.withTransaction {

      val addressId = People.filter(_.id === this.id).map(_.addressId).first

      val addressQuery = for {
        address ← Addresses if address.id === addressId
      } yield address
      addressQuery.update(address.copy(id = Some(addressId)))

      val socialQuery = for {
        socialProfile ← SocialProfiles if socialProfile.objectId === id.get
      } yield socialProfile
      socialQuery.filter(_.objectType === socialProfile.objectType).update(socialProfile.copy(objectId = id.get))
      // Skip the id, created, createdBy and active fields.
      val personUpdateTuple = (firstName, lastName, birthday, photo.url, signature, bio, interests,
        role, webSite, blog, virtual, dateStamp.updated, dateStamp.updatedBy)
      val updateQuery = People.filter(_.id === id).map(_.forUpdate)
      updateQuery.update(personUpdateTuple)

      UserAccount.updateSocialNetworkProfiles(this)
      this
    }
  }

  /**
   * Find all events which were faciliated by a specified facilitator and
   * where the person participated
   *
   * @param facilitatorId Facilitator identifier
   */
  def participateInEvents(facilitatorId: Long): List[Event] = DB.withSession {
    implicit session: Session ⇒
      val query = for {
        facilitation ← EventFacilitators if facilitation.facilitatorId === facilitatorId
        event ← Events if facilitation.eventId === event.id
        participation ← Participants if participation.eventId === event.id
      } yield (event, facilitation, participation)

      query
        .filter(_._3.personId === this.id)
        .mapResult(_._1)
        .list
  }

  /**
   * Finds the active `Account`s that this `Person` has access rights to.
   *
   * Currently, ‘having access rights to an account’ means that:
   * - This person is the account‘s holder
   * - This person is a member of the organisation that is the account’s holder
   *
   * @return The list of accounts that this person has access to
   */
  def findAccessibleAccounts: List[AccountSummary] = DB.withSession { implicit session: Session ⇒
    val query = for {
      account ← Accounts if account.active
      organisation ← Organisations if account.organisationId === organisation.id.?
      membership ← OrganisationMemberships if membership.organisationId === organisation.id
      if membership.personId.? === this.id
    } yield (account.id, organisation.name, account.currency, account.active)

    account.summary :: query.list.map(AccountSummary.tupled)
  }

  def summary: PersonSummary = PersonSummary(id.get, firstName, lastName, active, address.countryCode)

}

case class PersonSummary(id: Long, firstName: String, lastName: String, active: Boolean, countryCode: String)

object Person {

  def cacheId(id: Long): String = s"signatures.$id"

  def fullFileName(id: Long): String = s"signatures/$id"

  /**
   * Removes a person's signature from the cloud
   * @param id Person identifier
   */
  def removeFromCloud(id: Long) {
    Cache.remove(Person.cacheId(id))
    S3Bucket.remove(Person.fullFileName(id))
  }

  /**
   * Downloads a person's signature form the cloud and puts it into cache
   * @param id Person identifier
   * @return
   */
  def downloadFromCloud(id: Long): Future[Array[Byte]] = {
    val contentType = "image/jpeg"
    val result = S3Bucket.get(Person.fullFileName(id))
    val pdf: Future[Array[Byte]] = result.map {
      case BucketFile(name, contentType, content, acl, headers) ⇒ content
    }.recover {
      case S3Exception(status, code, message, originalXml) ⇒ Array[Byte]()
    }
    pdf.map {
      case value ⇒
        Cache.set(Person.cacheId(id), value)
        value
    }
  }

  /**
   * Activates the organisation, if the parameter is true, or deactivates it.
   * During activization, a person also becomes a real person
   *
   * @param id Person identifier
   * @param active True if we activate a person, False if otherwise
   */
  def activate(id: Long, active: Boolean): Unit = DB.withSession { implicit session: Session ⇒
    People.filter(_.id === id)
      .map(p ⇒ p.active ~ p.virtual)
      .update((active, false))
  }

  /**
   * Deletes the person with the given ID and their account.
   *
   * @param id Person Identifier
   */
  def delete(id: Long): Unit = DB.withSession { implicit session: Session ⇒
    find(id).map(_.account).map(_.delete)
    Participants.where(_.personId === id).mutate(_.delete())
    People.where(_.id === id).mutate(_.delete())
  }

  /**
   * Find a specified person
   * @param id Person Identifier
   * @return
   */
  def find(id: Long): Option[Person] = DB.withSession { implicit session: Session ⇒
    val query = for {
      person ← People if person.id === id
    } yield person

    query.firstOption
  }

  /**
   * Find a person
   * @param name Person Identifier
   * @return
   */
  def find(name: String): Option[Person] = DB.withSession { implicit session: Session ⇒
    val transformed = name.replace(".", " ")
    val query = for {
      person ← People if person.firstName ++ " " ++ person.lastName.toLowerCase like "%" + transformed + "%"
    } yield person

    query.firstOption
  }

  /**
   * Finds people, filtered by stakeholder, board member status and/or first/last name query
   *
   * @param stakeholdersOnly Only stakeholders will be returned
   * @param boardMembersOnly Only board members will be returned
   * @param active Only active members will be returned
   * @param query Only members with suitable name will be returned
   * @return
   */
  def findByParameters(stakeholdersOnly: Boolean, boardMembersOnly: Boolean, active: Option[Boolean],
    query: Option[String]): List[Person] = DB.withSession {
    implicit session: Session ⇒
      val baseQuery = query.map { q ⇒
        Query(People).filter(p ⇒ p.firstName ++ " " ++ p.lastName.toLowerCase like "%" + q + "%")
      }.getOrElse(Query(People))
      val activeQuery = active.map { value ⇒
        baseQuery.filter(_.active === value)
      }.getOrElse(baseQuery)
      val stakeholderFilteredQuery = if (stakeholdersOnly) baseQuery.filter(_.role === PersonRole.Stakeholder) else baseQuery
      val boardMembersFilteredQuery = if (boardMembersOnly) stakeholderFilteredQuery.filter(_.role === PersonRole.BoardMember) else stakeholderFilteredQuery
      boardMembersFilteredQuery.sortBy(_.firstName.toLowerCase).list
  }

  /**
   * Finds all active people, filtered by stakeholder and/or board member status
   *
   * @param stakeholdersOnly Only active stakeholders will be returned
   * @param boardMembersOnly Only active board members will be returned
   * @return
   */
  def findActive(stakeholdersOnly: Boolean, boardMembersOnly: Boolean): List[Person] = DB.withSession { implicit session: Session ⇒
    val baseQuery = Query(People).filter(_.active === true).sortBy(_.firstName.toLowerCase)
    if (boardMembersOnly) {
      baseQuery.filter(_.role === PersonRole.BoardMember).list
    } else if (stakeholdersOnly) {
      baseQuery.filter(_.role =!= PersonRole.NoRole).list
    } else {
      baseQuery.list
    }
  }

  /**
   * Finds active people who have a user account with administrator role.
   */
  def findActiveAdmins: Set[Person] = DB.withSession { implicit session: Session ⇒
    val query = for {
      account ← UserAccounts if account.role === UserRole.Role.Admin.toString
      person ← People if person.id === account.personId
    } yield person
    query.list.toSet
  }

  /**
   * Find all board members
   * @return
   */
  def findBoardMembers: Set[Person] = DB.withSession { implicit session: Session ⇒
    Query(People).filter(_.role === PersonRole.BoardMember).list.toSet
  }

  /**
   * Retrieves a list of all people from the database
   */
  def findAll: List[PersonSummary] = DB.withSession { implicit session: Session ⇒
    (for {
      person ← People
      address ← Addresses if person.addressId === address.id
    } yield (person.id, person.firstName, person.lastName, person.active, address.countryCode))
      .sortBy(_._2.toLowerCase)
      .mapResult(PersonSummary.tupled).list
  }

  /**
   * Find all active people
   * @return
   */
  def findActive: List[Person] = DB.withSession { implicit session: Session ⇒
    Query(People).filter(_.active === true).sortBy(_.firstName.toLowerCase).list
  }
}
