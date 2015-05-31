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

import models._
import models.database._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

import scala.slick.lifted.Query

class PersonService extends Services {

  /**
   * Deletes the person with the given ID and their account.
   *
   * @param id Person Identifier
   */
  def delete(id: Long): Unit = DB.withTransaction { implicit session: Session ⇒
    find(id) map { person ⇒
      Accounts.where(_.personId === id).mutate(_.delete())
      MemberService.get.delete(id, person = true)
      PaymentRecordService.get.delete(id, person = true)
      UserAccount.delete(id)
      //TODO add evaluation removal
      Participants.where(_.personId === id).mutate(_.delete())
      SocialProfileService.get.delete(id, ProfileType.Person)
      People.where(_.id === id).mutate(_.delete())
      Addresses.where(_.id === person.address.id.get).mutate(_.delete())
    }
  }

  /**
   * Inserts new person object into database
   * @param person Person object
   * @return Returns saved person
   */
  def insert(person: Person): Person = DB.withTransaction {
    implicit session: Session ⇒
      val address = Address.insert(person.address)
      val id = People.forInsert.insert(person.copy(addressId = address.id.get))
      SocialProfileService.get.insert(person.socialProfile.copy(objectId = id))
      Accounts.insert(Account(personId = Some(id)))

      val saved = person.copy(id = Some(id))
      saved.address_=(address)
      saved
  }

  /**
   * Returns person if it exists, otherwise - None
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
   * Returns person if it exists, otherwise - None
   *
   * @param name Person Identifier
   */
  def find(name: String): Option[Person] = DB.withSession { implicit session: Session ⇒
    val transformed = name.replace(".", " ")
    val query = for {
      person ← People if person.firstName ++ " " ++ person.lastName.toLowerCase like "%" + transformed + "%"
    } yield person

    query.firstOption
  }

  /**
   * Returns a list of active people
   */
  def findActive: List[Person] = DB.withSession { implicit session: Session ⇒
    Query(People).filter(_.active === true).sortBy(_.firstName.toLowerCase).list
  }

  /** Returns list of people which are not members (yet!) */
  def findNonMembers: List[Person] = DB.withSession { implicit session ⇒
    import scala.language.postfixOps

    val members = for { m ← Members if m.person === true } yield m.objectId
    val ids = members.list
    Query(People).filter(row ⇒ !(row.id inSet ids)).sortBy(_.firstName).list
  }

  /**
   * Returns member data if person is a member, false None
   * @param id Person id
   */
  def member(id: Long): Option[Member] = DB.withSession { implicit session ⇒
    Query(Members).
      filter(_.objectId === id).
      filter(_.person === true).firstOption
  }

  /**
   * Returns list of organizations this person is a member of
   * @param person Person object
   */
  def memberships(person: Person): List[Organisation] = DB.withSession { implicit session: Session ⇒
    val query = for {
      membership ← OrganisationMemberships if membership.personId === person.id.get
      organisation ← membership.organisation
    } yield organisation
    query.sortBy(_.name.toLowerCase).list
  }

  def update(person: Person): Person = DB.withTransaction { implicit session: Session ⇒
    import models.database.SocialProfiles._

    val addressQuery = for {
      address ← Addresses if address.id === person.addressId
    } yield address
    addressQuery.update(person.address.copy(id = Some(person.addressId)))

    val socialQuery = for {
      p ← SocialProfiles if p.objectId === person.id.get &&
        p.objectType === person.socialProfile.objectType
    } yield p

    socialQuery.update(person.socialProfile.copy(objectId = person.id.get))

    // Skip the id, created, createdBy and active fields.
    val personUpdateTuple = (person.firstName, person.lastName, person.birthday,
      person.photo.url, person.signature, person.bio, person.interests,
      person.webSite, person.blog, person.customerId, person.virtual,
      person.active, person.dateStamp.updated, person.dateStamp.updatedBy)
    val updateQuery = People.filter(_.id === person.id).map(_.forUpdate)
    updateQuery.update(personUpdateTuple)

    UserAccount.updateSocialNetworkProfiles(person)
    updateProfileCompletion(person)

    person
  }

  /**
   * Updates profile completion depending
   *
   * @param person Person object to update profile completion for
   */
  protected def updateProfileCompletion(person: Person): Unit = {
    profileCompletionService.find(person.id.get, false) map { completion ⇒
      val completionWithDesc = if (person.bio.isDefined)
        completion.markComplete("about")
      else
        completion.markIncomplete("about")
      val completionWithSocial = if (person.socialProfile.complete)
        completionWithDesc.markComplete("social")
      else
        completionWithDesc.markIncomplete("social")
      val completionWithPhoto = if (person.photo.id.isDefined)
        completionWithSocial.markComplete("photo")
      else
        completionWithSocial.markIncomplete("photo")
      val completionWithSignature = if (person.signature)
        completionWithPhoto.markComplete("signature")
      else
        completionWithPhoto.markIncomplete("signature")
      profileCompletionService.update(completionWithSignature)
    }
  }
}

object PersonService {
  private val instance = new PersonService()

  def get: PersonService = instance
}
