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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models

import models.database._
import models.service._
import org.joda.money.Money
import org.joda.time.{ DateTime, LocalDate }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

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
  webSite: Option[String],
  blog: Option[String],
  customerId: Option[String] = None,
  virtual: Boolean = false,
  active: Boolean = true,
  dateStamp: DateStamp) extends AccountHolder
    with ActivityRecorder
    with Services {

  private var _socialProfile: Option[SocialProfile] = None
  private var _address: Option[Address] = None
  private var _languages: Option[List[FacilitatorLanguage]] = None
  private var _countries: Option[List[FacilitatorCountry]] = None
  private var _organisations: Option[List[Organisation]] = None
  private var _member: Option[Member] = None

  def copy(id: Option[Long] = id,
    firstName: String = firstName,
    lastName: String = lastName,
    birthday: Option[LocalDate] = birthday,
    photo: Photo = photo,
    signature: Boolean = signature,
    addressId: Long = addressId,
    bio: Option[String] = bio,
    interests: Option[String] = interests,
    webSite: Option[String] = webSite,
    blog: Option[String] = blog,
    customerId: Option[String] = customerId,
    virtual: Boolean = virtual,
    active: Boolean = active,
    dateStamp: DateStamp = dateStamp): Person = {
    val person = Person(id, firstName, lastName, birthday, photo,
      signature, addressId, bio, interests, webSite, blog,
      customerId, virtual, active, dateStamp)
    this._socialProfile foreach { p ⇒
      person.socialProfile_=(this.socialProfile)
    }
    this._address foreach { a ⇒
      person.address_=(a)
    }
    person
  }

  def socialProfile: SocialProfile = if (_socialProfile.isEmpty) {
    DB.withSession { implicit session: Session ⇒
      socialProfile_=(socialProfileService.find(id.getOrElse(0), ProfileType.Person))
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
    languages_=(facilitatorService.languages(id.get))
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
  def organisations: List[Organisation] = if (_organisations.isEmpty) {
    organisations_=(PersonService.get.memberships(id.get))
    _organisations.get
  } else {
    _organisations.get
  }

  def organisations_=(organisations: List[Organisation]): Unit = {
    _organisations = Some(organisations)
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
      _member = personService.member(i)
      _member
    } getOrElse None
  }

  /** Returns true if a person is a member */
  def isMember: Boolean = _member.isDefined

  /**
   * Associates this person with given organisation.
   */
  def addRelation(organisationId: Long): Unit =
    personService.addRelation(this.id.get, organisationId)

  /**
   * Returns true if it is possible to grant log in access to this user.
   */
  def canHaveUserAccount: Boolean = socialProfile.defined

  /**
   * Returns true if this person may be deleted.
   */
  lazy val deletable: Boolean = account.deletable &&
    contributions.isEmpty &&
    organisations.isEmpty &&
    licenses.isEmpty

  /**
   * Deletes a relationship between this person and the given organisation
   *
   * @param organisationId Organisation identifier
   */
  def deleteRelation(organisationId: Long): Unit =
    personService.deleteRelation(this.id.get, organisationId)

  /**
   * Returns a list of this person’s content licenses.
   */
  lazy val licenses: List[LicenseView] = DB.withSession { implicit session ⇒
    val query = for {
      license ← TableQuery[Licenses] if license.licenseeId === this.id
      brand ← license.brand
    } yield (license, brand)

    query.sortBy(_._2.name.toLowerCase).list.map {
      case (license, brand) ⇒ LicenseView(brand, license)
    }
  }

  /**
   * Returns a list of this person's contributions.
   */
  lazy val contributions: List[ContributionView] =
    contributionService.contributions(this.id.get, isPerson = true)

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
   * Inserts this person into the database and returns the saved Person,
   * with the ID added
   */
  def insert: Person = personService.insert(this)

  /**
   * Updates related info about this person in database
   */
  def update: Person = personService.update(this)

  /**
   * Finds the active `Account`s that this `Person` has access rights to.
   *
   * Currently, ‘having access rights to an account’ means that:
   * - This person is the account‘s holder
   * - This person is a member of the organisation that is the account’s holder
   *
   * @return The list of accounts that this person has access to
   */
  def findAccessibleAccounts: List[AccountSummary] = DB.withSession { implicit session ⇒
    val query = for {
      account ← TableQuery[Accounts] if account.active
      organisation ← TableQuery[Organisations] if account.organisationId === organisation.id.?
      membership ← TableQuery[OrganisationMemberships] if membership.organisationId === organisation.id
      if membership.personId.? === this.id
    } yield (account.id, organisation.name, account.currency, account.active)

    account.summary :: query.list.map(AccountSummary.tupled)
  }

  def summary: PersonSummary = PersonSummary(id.get, firstName, lastName, active, address.countryCode)

  /**
   * Adds member record to database
   * @param funder Defines if this person becomes a funder
   * @param fee Amount of membership fee this person paid
   * @return Returns member object
   */
  def becomeMember(funder: Boolean, fee: Money): Member = {
    val m = memberService.insert(membership(funder, fee))
    profileStrengthService.find(id.get, false) map { x ⇒
      profileStrengthService.update(ProfileStrength.forMember(x))
    }
    m
  }

  /**
   * Returns a one-year membership object for the given parameters
   *
   * @param funder If true member is a funder
   * @param fee An amount of membership fee
   */
  protected def membership(funder: Boolean, fee: Money): Member =
    new Member(None, id.get, person = true,
      funder = funder, fee = fee,
      renewal = true,
      since = LocalDate.now(),
      until = LocalDate.now().plusYears(1),
      existingObject = true, reason = None,
      created = DateTime.now(), id.get,
      DateTime.now(), id.get)
}

case class PersonSummary(id: Long, firstName: String, lastName: String, active: Boolean, countryCode: String)

object Person {

  def apply(firstName: String, lastName: String): Person = {
    val creator = firstName + " " + lastName
    Person(None, firstName, lastName, None, Photo.empty, signature = false,
      addressId = 0, bio = None, interests = None, webSite = None, blog = None,
      dateStamp = DateStamp(DateTime.now(), creator, DateTime.now(), creator))
  }

  def cacheId(id: Long): String = s"signatures.$id"

  def fullFileName(id: Long): String = s"signatures/$id"

  def signature(id: Long): File =
    File.image(Person.fullFileName(id), Person.cacheId(id))

  def photo(id: Long): File =
    File.image(s"photos/$id", s"photos.$id")

  /**
   * Activates the person, if the parameter is true, or deactivates it.
   * During activization, a person also becomes a real person
   *
   * @param id Person identifier
   * @param active True if we activate a person, False if otherwise
   */
  def activate(id: Long, active: Boolean): Unit = DB.withSession { implicit session: Session ⇒
    TableQuery[People].filter(_.id === id)
      .map(p ⇒ (p.active, p.virtual))
      .update((active, false))
  }

  /**
   * Finds people, filtered by first/last name query
   *
   * @param active Only active members will be returned
   * @param query Only members with suitable name will be returned
   * @return
   */
  def findByParameters(active: Option[Boolean],
    query: Option[String]): List[Person] = DB.withSession {

    implicit session: Session ⇒
      val people = TableQuery[People]
      val baseQuery = query.map { q ⇒
        people.filter(p ⇒ p.firstName ++ " " ++ p.lastName.toLowerCase like "%" + q + "%")
      } getOrElse people
      val activeQuery = active.map { value ⇒
        baseQuery.filter(_.active === value)
      }.getOrElse(baseQuery)
      activeQuery.sortBy(_.firstName.toLowerCase).list
  }

  /**
   * Finds active people who have a user account with administrator role.
   */
  def findActiveAdmins: Set[Person] = DB.withSession { implicit session: Session ⇒
    val query = for {
      account ← TableQuery[UserAccounts] if account.role === UserRole.Role.Admin.toString
      person ← TableQuery[People] if person.id === account.personId
    } yield person
    query.list.toSet
  }

  /**
   * Retrieves a list of all people from the database
   */
  def findAll: List[PersonSummary] = DB.withSession { implicit session: Session ⇒
    (for {
      person ← TableQuery[People]
      address ← TableQuery[Addresses] if person.addressId === address.id
    } yield (person.id, person.firstName, person.lastName, person.active, address.countryCode))
      .sortBy(_._2.toLowerCase)
      .mapResult(PersonSummary.tupled).list
  }

}

object PeopleCollection {

  /**
   * Fill person objects with addresses (using only one query to database)
   * @param people List of people
   * @return
   */
  def addresses(people: List[Person]): Unit = DB.withSession { implicit session: Session ⇒
    val ids = people.map(_.addressId).distinct
    val query = for {
      address ← TableQuery[Addresses] if address.id inSet ids
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
    val ids = people.map(_.id.get).distinct
    val query = for {
      language ← TableQuery[FacilitatorLanguages] if language.personId inSet ids
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
    val ids = people.map(_.id.get).distinct
    val query = for {
      country ← TableQuery[FacilitatorCountries] if country.personId inSet ids
    } yield country
    val countries = query.list.groupBy(_.personId)
    people.foreach(p ⇒ {
      val countryOfResidence = FacilitatorCountry(p.id.get, p.address.countryCode)
      val c = countries.getOrElse(p.id.get, List()) ::: List(countryOfResidence)
      p.countries_=(c)
    })
  }

  /**
   * Fill person objects with organisations data (using only one query to database)
   * @param people List of people
   * @return
   */
  def organisations(people: List[Person]): Unit = DB.withSession { implicit session: Session ⇒
    val ids = people.map(_.id.get).distinct
    val query = for {
      membership ← TableQuery[OrganisationMemberships] if membership.personId inSet ids
      organisation ← membership.organisation
    } yield (membership.personId, organisation)
    val organisations = query.list.groupBy(_._1).map(o ⇒ (o._1, o._2.map(_._2)))
    people.foreach(p ⇒ p.organisations_=(organisations.getOrElse(p.id.get, List())))
  }
}
