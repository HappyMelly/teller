
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

import models.repository._
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
  virtual: Boolean = false,
  active: Boolean = true,
  dateStamp: DateStamp) extends Recipient with ActivityRecorder {

  private var _profile: Option[SocialProfile] = None
  private var _address: Option[Address] = None
  private var _organisations: Option[List[Organisation]] = None

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
    virtual: Boolean = virtual,
    active: Boolean = active,
    dateStamp: DateStamp = dateStamp): Person = {

    val person = Person(id, firstName, lastName, email, birthday, photo,
      signature, addressId, bio, interests, webSite, blog,
      virtual, active, dateStamp)

    this._profile foreach { p ⇒
      person.profile_=(this.profile)
    }

    this._address foreach { a ⇒
      person.address_=(a)
    }
    person
  }

  def profile: SocialProfile = _profile.get

  def profile_=(socialProfile: SocialProfile): Unit = {
    _profile = Some(socialProfile)
  }

  def address: Address = _address.get

  def address_=(address: Address): Unit = {
    _address = Some(address)
  }

  /**
   * Returns a list of organisations this person is a member of
   */
  def organisations(implicit services: Repositories): List[Organisation] = if (_organisations.isEmpty) {
    val orgs = Await.result(services.person.memberships(identifier), 3.seconds)
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
   * Associates this person with given organisation.
   */
  def addRelation(organisationId: Long, services: Repositories): Unit =
    services.person.addRelation(this.id.get, organisationId)

  /**
   * Returns true if it is possible to grant log in access to this user.
   */
  def canHaveUserAccount: Boolean = profile.defined

  /**
   * Deletes a relationship between this person and the given organisation
   *
   * @param organisationId Organisation identifier
   */
  def deleteRelation(organisationId: Long, services: Repositories): Unit =
    services.person.deleteRelation(this.id.get, organisationId)

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


  def summary: PersonSummary = PersonSummary(id.get, firstName, lastName, active, address.countryCode)

  /**
   * Adds member record to database
    *
    * @param funder Defines if this person becomes a funder
   * @param fee Amount of membership fee this person paid
   * @return Returns member object
   */
  def becomeMember(funder: Boolean, fee: BigDecimal, services: Repositories): Future[Member] = {
    val m = services.member.insert(membership(funder, fee))
    services.profileStrength.find(id.get, false).filter(_.isDefined) foreach { x ⇒
      services.profileStrength.update(ProfileStrength.forMember(x.get))
    }
    m
  }

  /**
   * Returns a one-year membership object for the given parameters
   *
   * @param funder If true member is a funder
   * @param fee An amount of membership fee
   */
  protected def membership(funder: Boolean, fee: BigDecimal): Member =
    new Member(None, id.get, person = true,
      funder = funder, fee = fee, newFee = None,
      renewal = true,
      since = LocalDate.now(),
      until = LocalDate.now().plusYears(1),
      reason = None,
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

  def deletable(id: Long, services: Repositories): Future[Boolean] = for {
    c <- services.contribution.contributions(id, isPerson = true)
    l <- services.cm.license.licensesWithBrands(id)
    o <- services.person.memberships(id)
  } yield c.isEmpty && l.isEmpty && o.isEmpty


  def fullFileName(id: Long): String = s"signatures/$id"

  def signature(id: Long): File = File.image(Person.fullFileName(id), Person.cacheId(id))

  def photo(id: Long): File = File.image(s"photos/$id", s"photos.$id")
}
