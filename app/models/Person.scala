package models

import models.database.{ Licenses, People, OrganisationMemberships, Addresses }
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.db.slick.DB.withSession
import play.api.Play.current
import scala.slick.lifted.Query

/**
 * A person, such as the owner or employee of an organisation.
 */
case class Person(
  id: Option[Long],
  firstName: String,
  lastName: String,
  emailAddress: String,
  address: Address,
  bio: Option[String],
  interests: Option[String],
  twitterHandle: Option[String],
  facebookUrl: Option[String],
  linkedInUrl: Option[String],
  googlePlusUrl: Option[String],
  boardMember: Boolean = false,
  stakeholder: Boolean = true,
  active: Boolean = true,
  created: DateTime = DateTime.now(),
  createdBy: String,
  updated: DateTime,
  updatedBy: String) {

  def fullName: String = firstName + " " + lastName

  def fullNamePossessive = if (lastName.endsWith("s")) s"$fullName’" else s"$fullName’s"

  /**
   * Associates this person with given organisation.
   */
  def addMembership(organisationId: Long): Unit = {
    withSession { implicit session ⇒
      OrganisationMemberships.forInsert.insert(this.id.get, organisationId)
    }
  }

  /**
   * Removes this person’s membership in the given organisation.
   */
  def deleteMembership(organisationId: Long): Unit = {
    withSession { implicit session ⇒
      OrganisationMemberships.filter(membership ⇒ membership.personId === id && membership.organisationId === organisationId).mutate(_.delete)
    }
  }

  /**
   * Returns a user for a different person to this one who has the same Twitter handle, if there is one.
   * This is used to check for duplicate Twitter handles when creating accounts.
   */
  def findUserWithSameTwitter: Option[UserAccount] = {
    val userSameTwitter = this.twitterHandle.map(handle ⇒ UserAccount.findByTwitterHandle(handle)).flatten
    if (userSameTwitter.map(_.personId) == this.id) None else userSameTwitter
  }

  /**
   * Returns a list of this person’s content licenses.
   */
  def licenses: List[LicenseView] = withSession { implicit session ⇒

    val query = for {
      license ← Licenses if license.licenseeId === this.id
      brand ← license.brand
    } yield (license, brand)

    query.sortBy(_._2.name.toLowerCase).list.map {
      case (license, brand) ⇒ LicenseView(brand, license)
    }
  }

  /**
   * Returns a list of the organisations this person is a member of.
   */
  def memberships: List[Organisation] = withSession { implicit session ⇒
    val query = for {
      membership ← OrganisationMemberships if membership.personId === this.id
      organisation ← membership.organisation
    } yield organisation
    query.sortBy(_.name.toLowerCase).list
  }

  /**
   * Inserts this person into the database and returns the saved Person, with the ID added.
   */
  def insert: Person = DB.withSession { implicit session ⇒
    val newAddress = Address.insert(this.address)
    val newId = People.forInsert.insert(this.copy(address = newAddress))
    this.copy(id = Some(newId))
  }

  /**
   * Updates this person in the database and returns the saved person.
   */
  def update: Person = DB.withSession { implicit session ⇒
    session.withTransaction {

      val addressId = People.filter(_.id === this.id).map(_.addressId).first

      val addressQuery = for {
        address ← Addresses if address.id === addressId
      } yield address
      addressQuery.update(address.copy(id = Some(addressId)))

      // Skip the id, created, createdBy and active fields.
      val personUpdateTuple = (firstName, lastName, emailAddress, bio, interests, twitterHandle, facebookUrl,
        linkedInUrl, googlePlusUrl, boardMember, stakeholder, updated, updatedBy)
      val updateQuery = People.filter(_.id === id).map(_.forUpdate)
      updateQuery.update(personUpdateTuple)
      this
    }
  }
}

object Person {

  /**
   * Activates the organisation, if the parameter is true, or deactivates it.
   */
  def activate(id: Long, active: Boolean): Unit = withSession { implicit session ⇒
    val query = for {
      person ← People if person.id === id
    } yield person.active
    query.update(active)
  }

  def delete(id: Long): Unit = {
    withSession { implicit session ⇒
      People.where(_.id === id).delete
    }
  }

  def find(id: Long): Option[Person] = withSession { implicit session ⇒
    val query = for {
      person ← People if person.id === id
    } yield person

    query.firstOption
  }

  /** Finds all active people, filtered by stakeholder and/or board member status **/
  def findActive(stakeholdersOnly: Boolean, boardMembersOnly: Boolean): List[Person] = withSession { implicit session ⇒
    val baseQuery = Query(People).filter(_.active === true).sortBy(_.lastName.toLowerCase)
    val stakeholderFilteredQuery = if (stakeholdersOnly) baseQuery.filter(_.stakeholder) else baseQuery
    val boardMembersFilteredQuery = if (boardMembersOnly) stakeholderFilteredQuery.filter(_.boardMember) else stakeholderFilteredQuery

    boardMembersFilteredQuery.list
  }

  /** Retrieves a list of all people from the database **/
  def findAll: List[Person] = withSession { implicit session ⇒
    Query(People).sortBy(_.lastName.toLowerCase).list
  }

  def findActive: List[Person] = withSession { implicit session ⇒
    Query(People).filter(_.active === true).sortBy(_.lastName.toLowerCase).list
  }

}

