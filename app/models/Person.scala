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
import models.service.{ MemberService, ContributionService, PersonService, SocialProfileService }
import org.joda.time.{ DateTime, LocalDate }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import scala.slick.lifted.Query
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
  addressId: Long,
  bio: Option[String],
  interests: Option[String],
  role: PersonRole.Value = PersonRole.Stakeholder,
  webSite: Option[String],
  blog: Option[String],
  virtual: Boolean = false,
  active: Boolean = true,
  dateStamp: DateStamp) extends AccountHolder with ActivityRecorder {

  private var _socialProfile: Option[SocialProfile] = None
  private var _address: Option[Address] = None
  private var _languages: Option[List[FacilitatorLanguage]] = None
  private var _countries: Option[List[FacilitatorCountry]] = None
  private var _memberships: Option[List[Organisation]] = None
  private var _member: Option[Member] = None

  def socialProfile: SocialProfile = if (_socialProfile.isEmpty) {
    DB.withSession { implicit session: Session ⇒
      socialProfile_=(SocialProfileService.find(id.getOrElse(0), ProfileType.Person))
      _socialProfile.get
    }
  } else {
    _socialProfile.get
  }

  def socialProfile_=(socialProfile: SocialProfile): Unit = {
    _socialProfile = Some(socialProfile)
  }

  def address: Address = if (_address.isEmpty) {
    address_=(Address.find(addressId))
    _address.get
  } else {
    _address.get
  }

  def address_=(address: Address): Unit = {
    _address = Some(address)
  }

  /**
   * A list of languages a facilitator speaks
   */
  def languages: List[FacilitatorLanguage] = if (_languages.isEmpty) {
    languages_=(FacilitatorLanguage.findByFacilitator(id.get))
    _languages.get
  } else {
    _languages.get
  }

  def languages_=(languages: List[FacilitatorLanguage]): Unit = {
    _languages = Some(languages)
  }

  /**
   * A list of countries a facilitator speaks
   */
  def countries: List[FacilitatorCountry] = if (_countries.isEmpty) {
    countries_=(FacilitatorCountry.findByFacilitator(id.get))
    _countries.get
  } else {
    _countries.get
  }

  def countries_=(countries: List[FacilitatorCountry]): Unit = {
    _countries = Some(countries)
  }

  /**
   * Returns a list of organisations this person is a member of
   */
  def memberships: List[Organisation] = if (_memberships.isEmpty) {
    memberships_=(PersonService.get.memberships(this))
    _memberships.get
  } else {
    _memberships.get
  }

  def memberships_=(memberships: List[Organisation]): Unit = {
    _memberships = Some(memberships)
  }

  def fullName: String = firstName + " " + lastName

  def uniqueName: String = fullName.toLowerCase.replace(" ", ".")

  def name = fullName

  def fullNamePossessive = if (lastName.endsWith("s")) s"$fullName’" else s"$fullName’s"

  /**
   * Sets member data
   * @param member Member data
   */
  def member_=(member: Member): Unit = _member = Some(member)

  /** Returns member data if person is a member, false None */
  def member: Option[Member] = _member map { Some(_) } getOrElse {
    id map { i ⇒
      _member = PersonService.get.member(i)
      _member
    } getOrElse None
  }

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
  lazy val deletable: Boolean = account.deletable &&
    contributions.isEmpty &&
    memberships.isEmpty &&
    licenses.isEmpty

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
  lazy val contributions: List[ContributionView] = {
    ContributionService.get.contributions(this.id.get, isPerson = true)
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
  def humanIdentifier: String = fullName

  /**
   * Returns type of this object
   */
  def objectType: String = Activity.Type.Person

  /**
   * Inserts this person into the database and returns the saved Person, with the ID added.
   */
  def insert: Person = DB.withTransaction { implicit session: Session ⇒
    val newAddress = Address.insert(this.address)
    val personId = People.forInsert.insert(this.copy(addressId = newAddress.id.get))
    SocialProfileService.insert(socialProfile.copy(objectId = personId))
    Accounts.insert(Account(personId = Some(personId)))
    this.copy(id = Some(personId))
  }

  /**
   * Updates this person in the database and returns the saved person.
   */
  def update: Person = DB.withSession { implicit session: Session ⇒
    session.withTransaction {
      import models.database.SocialProfiles._
      val addressId = People.filter(_.id === this.id).map(_.addressId).first

      val addressQuery = for {
        address ← Addresses if address.id === addressId
      } yield address
      addressQuery.update(address.copy(id = Some(addressId)))

      val socialQuery = for {
        p ← SocialProfiles if p.objectId === id.get && p.objectType === socialProfile.objectType
      } yield p
      socialQuery.update(socialProfile.copy(objectId = id.get))
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
    import models.service.PersonService
    PersonService.get.find(id).map(_.account).map(_.delete())
    MemberService.get.delete(id, person = true)
    Participants.where(_.personId === id).mutate(_.delete())
    People.where(_.id === id).mutate(_.delete())
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
    import models.database.People.personRoleTypeMapper

    implicit session: Session ⇒
      val baseQuery = query.map { q ⇒
        Query(People).filter(p ⇒ p.firstName ++ " " ++ p.lastName.toLowerCase like "%" + q + "%")
      }.getOrElse(Query(People))
      val activeQuery = active.map { value ⇒
        baseQuery.filter(_.active === value)
      }.getOrElse(baseQuery)
      val stakeholderFilteredQuery = if (stakeholdersOnly)
        baseQuery.filter(_.role === PersonRole.Stakeholder)
      else
        baseQuery
      val boardMembersFilteredQuery = if (boardMembersOnly)
        stakeholderFilteredQuery.filter(_.role === PersonRole.BoardMember)
      else
        stakeholderFilteredQuery
      boardMembersFilteredQuery.sortBy(_.firstName.toLowerCase).list
  }

  /**
   * Finds all active people, filtered by stakeholder and/or board member status
   *
   * @param stakeholdersOnly Only active stakeholders will be returned
   * @param boardMembersOnly Only active board members will be returned
   * @return
   */
  def findActive(stakeholdersOnly: Boolean, boardMembersOnly: Boolean): List[Person] = DB.withSession {
    import models.database.People._
    implicit session: Session ⇒
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
    import models.database.People._
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

object PeopleCollection {

  /**
   * Fill person objects with addresses (using only one query to database)
   * @param people List of people
   * @return
   */
  def addresses(people: List[Person]): Unit = DB.withSession { implicit session: Session ⇒
    val ids = people.map(_.addressId).distinct.toList
    val query = for {
      address ← Addresses if address.id inSet ids
    } yield address
    val addresses = query.list
    people.foreach(p ⇒ p.address_=(addresses.find(_.id.get == p.addressId).get))
  }

  /**
   * Fill person objects with languages (using only one query to database)
   * @param people List of people
   * @return
   */
  def languages(people: List[Person]): Unit = DB.withSession { implicit session: Session ⇒
    val ids = people.map(_.id.get).distinct.toList
    val query = for {
      language ← FacilitatorLanguages if language.personId inSet ids
    } yield language
    val lanuages = query.list.groupBy(_.personId)
    people.foreach(p ⇒ p.languages_=(lanuages.getOrElse(p.id.get, List())))
  }

  /**
   * Fill person objects with countries (using only one query to database)
   * @param people List of people
   * @return
   */
  def countries(people: List[Person]): Unit = DB.withSession { implicit session: Session ⇒
    val ids = people.map(_.id.get).distinct.toList
    val query = for {
      country ← FacilitatorCountries if country.personId inSet ids
    } yield country
    val countries = query.list.groupBy(_.personId)
    people.foreach(p ⇒ p.countries_=(countries.getOrElse(p.id.get, List())))
  }

  /**
   * Fill person objects with organisations data (using only one query to database)
   * @param people List of people
   * @return
   */
  def organisations(people: List[Person]): Unit = DB.withSession { implicit session: Session ⇒
    val ids = people.map(_.id.get).distinct.toList
    val query = for {
      membership ← OrganisationMemberships if membership.personId inSet ids
      organisation ← membership.organisation
    } yield (membership.personId, organisation)
    val organisations = query.list.groupBy(_._1).map(o ⇒ (o._1, o._2.map(_._2)))
    people.foreach(p ⇒ p.memberships_=(organisations.getOrElse(p.id.get, List())))
  }
}
