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
import models._
import models.core.payment.CustomerType
import models.database._
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PersonRepository(app: Application, repos: Repositories) extends HasDatabaseConfig[JdbcProfile]
  with AddressTable
  with MemberTable
  with OrganisationMembershipTable
  with PasswordIdentityTable
  with PersonTable
  with ProfileStrengthTable
  with SocialProfileTable
  with UserAccountTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val people = TableQuery[People]

  /**
    * Activates/deactivate the given person
    *
    * @param personId Person identifier
    * @param active Activity flag
    */
  def activate(personId: Long, active: Boolean): Future[Int] =
    db.run(people.filter(_.id === personId).map(p ⇒ (p.active, p.virtual)).update((active, false)))

  /**
   * Associates this person with given organisation.
    *
   */
  def addRelation(personId: Long, organisationId: Long): Unit =
    db.run(TableQuery[OrganisationMemberships] += ((None, personId, organisationId)))

  /**
   * Deletes the person with the given ID and their account.
   *
   * @param person Person
   */
  def delete(person: Person): Unit = {
    repos.member.delete(person.identifier, person = true)
    repos.core.customer.delete(person.identifier, CustomerType.Person)
    repos.userAccount.delete(person.identifier)
    repos.socialProfile.delete(person.identifier, ProfileType.Person)
    val actions = (for {
      _ <- TableQuery[People].filter(_.id === person.identifier).delete
      _ <- TableQuery[Addresses].filter(_.id === person.addressId).delete
      _ <- TableQuery[ProfileStrengths].filter(_.objectId === person.id.get).filter(_.org === false).delete
      _ <- TableQuery[PasswordIdentities].filter(_.userId === person.identifier).delete
    } yield ()).transactionally
    db.run(actions)
  }

  /**
   * Deletes a relationship between this person and the given organisation
   *
   * @param organisationId Organisation identifier
   */
  def deleteRelation(personId: Long, organisationId: Long): Unit = {
    val query = TableQuery[OrganisationMemberships].
      filter(_.personId === personId).filter(_.organisationId === organisationId)
    db.run(query.delete)
  }

  /**
   * Inserts new person object into database
    *
    * @param value Person object
   * @return Returns saved person
   */
  def insert(value: Person): Future[Person] = {
    val query = people returning people.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    val request = for {
      address <- repos.address.insert(value.address)
      person <- db.run(query += value.copy(addressId = address.id.get))
      _ <- repos.socialProfile.insert(SocialProfile(objectId = person.identifier))
      _ <- repos.profileStrength.insert(ProfileStrength.empty(person.identifier, false))
    } yield (address, person)
    request map { case (address, person) =>
      person.address_=(address)
      person
    }
  }

  /**
   * Returns person if it exists, otherwise - None
    *
    * @param id Person Identifier
   * @return
   */
  def find(id: Long): Future[Option[Person]] = db.run(people.filter(_.id === id).result).map(_.headOption)

  /**
   * Returns person if it exists, otherwise - None
   *
   * @param name Person Identifier
   */
  def find(name: String): Future[Option[Person]] = {
    val transformed = name.replace(".", " ").replace("_", ".")

    import SocialProfilesStatic._

    val query = for {
      person <- people if person.firstName ++ " " ++ person.lastName.toLowerCase like "%" + transformed + "%"
      social <- TableQuery[SocialProfiles] if social.objectType === ProfileType.Person && social.objectId === person.id
      address <- TableQuery[Addresses] if address.id === person.addressId
    } yield (person, social, address)
    db.run(query.result).map(_.headOption.map { result =>
      result._1.profile_=(result._2)
      result._1.address_=(result._3)
      result._1
    })
  }

  /**
   * Returns list of people for the given ids
   *
   * @param ids List of people identifiers
   */
  def find(ids: Seq[Long]): Future[List[Person]] = db.run(people.filter(_.id inSet ids).result).map(_.toList)

  /**
   * Returns a list of active people
   */
  def findActive: Future[List[Person]] =
    db.run(people.filter(_.active === true).sortBy(_.firstName.toLowerCase).result).map(_.toList)


  /**
    * Finds active people who have a user account with administrator role.
    */
  def findActiveAdmins: Future[Set[Person]] = {
    val query = for {
      account ← TableQuery[UserAccounts] if account.admin === true
      person ← people if person.id === account.personId
    } yield person
    db.run(query.result).map(_.toSet)
  }

  /**
    * Retrieves a list of all people from the database
    */
  def findAll: Future[List[PersonSummary]] = {
    val query = for {
      person ← people
      address ← TableQuery[Addresses] if person.addressId === address.id
    } yield (person.id, person.firstName, person.lastName, person.active, address.countryCode)
    db.run(query.sortBy(_._2.toLowerCase).result).map(_.toList.map(PersonSummary.tupled))
  }

  def findByEmail(email: String): Future[Option[Person]] =
    db.run(people.filter(_.email === email).result).map(_.headOption)

  /**
   * Returns list of people for the given names
    *
    * @param names List of names
   */
  def findByNames(names: List[String]): Future[List[Person]] = {
    val transformed = names.map(name => name.replace(".", " "))
    val query = for {
      person ← people if person.firstName ++ " " ++ person.lastName.toLowerCase inSet transformed
    } yield person

    db.run(query.result).map(_.toList)
  }

  /**
    * Finds people, filtered by first/last name query
    *
    * @param active Only active members will be returned
    * @param query Only members with suitable name will be returned
    * @return
    */
  def findByParameters(active: Option[Boolean], query: Option[String]): Future[List[Person]] = {
    val baseQuery = query.map { q ⇒
      people.filter(p ⇒ p.firstName ++ " " ++ p.lastName.toLowerCase like "%" + q + "%")
    } getOrElse people
    val activeQuery = active.map { value ⇒ baseQuery.filter(_.active === value) }.getOrElse(baseQuery)
    db.run(activeQuery.sortBy(_.firstName.toLowerCase).result).map(_.toList)
  }

  /**
    * Returns person with an address and social profile if the person exists,
    * otherwise - None
    *
    * @param id Person Identifier
    */
  def findComplete(id: Long): Future[Option[Person]] = {
    import SocialProfilesStatic._

    val query = for {
      person <- people if person.id === id
      social <- TableQuery[SocialProfiles] if social.objectType === ProfileType.Person && social.objectId === id
      address <- TableQuery[Addresses] if address.id === person.addressId
    } yield (person, social, address)
    db.run(query.result).map(_.headOption.map { result =>
      result._1.profile_=(result._2)
      result._1.address_=(result._3)
      result._1
    })
  }

  /** Returns list of people which are not members (yet!) */
  def findNonMembers: Future[List[Person]] = {
    val actions = for {
      members <- TableQuery[Members].filter(_.person).map(_.objectId).result
      people <- people.filter(_.id inSet members).sortBy(_.firstName).result
    } yield people
    db.run(actions).map(_.toList)
  }

  /**
    * Returns the requested person
    *
    * @param id Person Identifier
    */
  def get(id: Long): Future[Person] = db.run(people.filter(_.id === id).result).map(_.head)

  /**
   * Returns member data if person is a member, false None
    *
    * @param id Person id
   */
  def member(id: Long): Future[Option[Member]] =
    db.run(TableQuery[Members].filter(_.objectId === id).filter(_.person === true).result).map(_.headOption)

  /**
   * Returns list of organizations this person is a member of
    *
    * @param personId Person identifier
   */
  def memberships(personId: Long): Future[List[Organisation]] = {
    val memberships = TableQuery[OrganisationMemberships]
    val query = for {
      membership ← memberships if membership.personId === personId
      organisation ← membership.organisation
    } yield organisation
    db.run(query.sortBy(_.name.toLowerCase).result).map(_.toList)
  }

  def update(person: Person): Future[Person] = {
    val addressQuery = for {
      address ← TableQuery[Addresses] if address.id === person.addressId
    } yield address
    db.run(addressQuery.update(person.address.copy(id = Some(person.addressId))))

    val personUpdateTuple = (person.firstName, person.lastName, person.email, person.birthday,
      person.bio, person.interests, person.webSite, person.blog, person.virtual,
      person.active, person.dateStamp.updated, person.dateStamp.updatedBy)
    val updateQuery = people.filter(_.id === person.id).map(_.forUpdate)
    db.run(updateQuery.update(personUpdateTuple))

    updateProfileStrength(person)

    Future.successful(person)
  }

  def updatePhoto(personId: Long, photoId: Option[String], url: Option[String]) =
    db.run(people.filter(_.id === personId).map(x => (x.photoId, x.photo)).update((photoId, url)))

  def updateSignature(personId: Long, signatureId: Option[String]) =
    db.run(people.filter(_.id === personId).map(_.signatureId).update(signatureId))

  object collection {
    /**
      * Fill person objects with addresses (using only one query to database)
      *
      * @param people List of people
      * @return
      */
    def addresses(people: List[Person]): Future[Unit] = {
      val ids = people.map(_.addressId).distinct
      val query = for {
        address ← TableQuery[Addresses] if address.id inSet ids
      } yield address
      db.run(query.result).map(_.toList).map { addresses =>
        people.foreach(p ⇒ p.address_=(addresses.find(_.id.get == p.addressId).get))
      }
    }

    /**
      * Fill person objects with organisations data (using only one query to database)
      *
      * @param people List of people
      * @return
      */
    def organisations(people: List[Person]): Unit = {
      val ids = people.map(_.id.get).distinct
      val query = for {
        membership ← TableQuery[OrganisationMemberships] if membership.personId inSet ids
        organisation ← membership.organisation
      } yield (membership.personId, organisation)
      db.run(query.result).map(_.toList.groupBy(_._1).map(o ⇒ (o._1, o._2.map(_._2)))).map { organisations =>
        people.foreach(p ⇒ p.organisations_=(organisations.getOrElse(p.id.get, List())))
      }
    }
  }

  /**
   * Updates profile strength depending
   *
   * @param person Person object to update profile strength for
   */
  protected def updateProfileStrength(person: Person): Unit = {
    repos.profileStrength.find(person.id.get, false).filter(_.isDefined) map { strength ⇒
      repos.profileStrength.update(ProfileStrength.forPerson(strength.get, person))
    }
  }
}
