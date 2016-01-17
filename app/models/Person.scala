
/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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

import models.service._
import org.joda.money.Money
import org.joda.time.{DateTime, LocalDate}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * A person, such as the owner or employee of an organisation.
 */
case class Person(
  id: Option[Long],
  firstName: String,
  lastName: String,
  override val email: String,
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
  dateStamp: DateStamp) extends Recipient
    with AccountHolder
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
    email: String = email,
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
    val person = Person(id, firstName, lastName, email, birthday, photo,
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
    val profile = Await.result(socialProfileService.find(id.getOrElse(0), ProfileType.Person), 3.seconds)
    socialProfile_=(profile)
    _socialProfile.get
  } else {
    _socialProfile.get
  }

  def socialProfile_=(socialProfile: SocialProfile): Unit = {
    _socialProfile = Some(socialProfile)
  }

  def address: Address = if (_address.isEmpty) {
    import scala.concurrent.duration._
    address_=(Await.result(addressService.get(addressId), 3.seconds))
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
    val langs = Await.result(facilitatorService.languages(identifier), 3.seconds)
    languages_=(langs)
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
    val result = Await.result(facilitatorService.countries(identifier), 3.seconds)
    countries_=(result)
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
    val orgs = Await.result(personService.memberships(identifier), 3.seconds)
    organisations_=(orgs)
    _organisations.get
  } else {
    _organisations.get
  }

  def organisations_=(organisations: List[Organisation]): Unit = {
    _organisations = Some(organisations)
  }

  def fullName: String = firstName + " " + lastName

  def uniqueName: String = fullName.toLowerCase.replace(".", "_").replace(" ", ".")

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
      val result = Await.result(personService.member(i), 3.seconds)
      _member = result
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
  lazy val licenses: List[LicenseView] =
    Await.result(licenseService.licensesWithBrands(identifier), 3.seconds)

  /**
   * Returns a list of this person's contributions.
   */
  lazy val contributions: List[ContributionView] =
    Await.result(contributionService.contributions(this.id.get, isPerson = true), 3.seconds)

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
  def insert: Future[Person] = personService.insert(this)

  /**
   * Updates related info about this person in database
   */
  def update: Future[Person] = personService.update(this)

  def summary: PersonSummary = PersonSummary(id.get, firstName, lastName, active, address.countryCode)

  /**
   * Adds member record to database
   * @param funder Defines if this person becomes a funder
   * @param fee Amount of membership fee this person paid
   * @return Returns member object
   */
  def becomeMember(funder: Boolean, fee: Money): Future[Member] = {
    val m = memberService.insert(membership(funder, fee))
    profileStrengthService.find(id.get, false) map { case Some(x) ⇒
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

  def apply(firstName: String, lastName: String, email: String): Person = {
    val creator = firstName + " " + lastName
    Person(None, firstName, lastName, email, None, Photo.empty, signature = false,
      addressId = 0, bio = None, interests = None, webSite = None, blog = None,
      dateStamp = DateStamp(DateTime.now(), creator, DateTime.now(), creator))
  }

  def cacheId(id: Long): String = s"signatures.$id"

  def fullFileName(id: Long): String = s"signatures/$id"

  def signature(id: Long): File =
    File.image(Person.fullFileName(id), Person.cacheId(id))

  def photo(id: Long): File =
    File.image(s"photos/$id", s"photos.$id")
}
