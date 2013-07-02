package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current
import scala.slick.lifted.Query
import play.api.db.slick.DB

/**
 * An organisation, usually a company, such as a Happy Melly legal entity.
 */
case class Organisation(id: Option[Long], name: String, street1: Option[String], street2: Option[String],
  city: Option[String], province: Option[String], postCode: Option[String], countryCode: String,
  vatNumber: Option[String], registrationNumber: Option[String], legalEntity: Boolean = false, active: Boolean = true) {

  /**
   * Inserts or updates this organisation into the database.
   * @return The Organisation as it is saved (with the id added if this was an insert)
   */
  def save: Organisation = DB.withSession { implicit session ⇒
    if (id.isDefined) { // Update
      Query(Organisations).filter(_.id === id).update(this)
      this
    } else { // Insert
      val id = Organisations.forInsert.insert(this)
      this.copy(id = Some(id))
    }
  }

}

object Organisation {

  /**
   * Activates the organisation, if the parameter is true, or deactivates it.
   */
  def activate(id: Long, active: Boolean): Unit = withSession { implicit session ⇒
    val query = for {
      organisation ← Organisations if organisation.id === id
    } yield organisation.active
    query.update(active)
  }

  /**
   * Deletes an organisation.
   */
  def delete(id: Long) {
    withSession { implicit session ⇒
      Organisations.where(_.id === id).delete
    }
  }

  def find(id: Long): Option[Organisation] = withSession { implicit session ⇒
    Query(Organisations).filter(_.id === id).list.headOption
  }

  def findAll: List[Organisation] = withSession { implicit session ⇒
    Query(Organisations).sortBy(_.name.toLowerCase).list
  }

  def members(organisation: Organisation): List[Person] = withSession { implicit session ⇒
    val query = for {
      membership ← OrganisationMemberships if membership.organisationId === organisation.id
      person ← membership.person
    } yield person
    query.sortBy(_.lastName.toLowerCase).list
  }
}

object Organisations extends Table[Organisation]("ORGANISATION") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")

  def street1 = column[Option[String]]("STREET_1")
  def street2 = column[Option[String]]("STREET_2")
  def city = column[Option[String]]("CITY")
  def province = column[Option[String]]("PROVINCE")
  def postCode = column[Option[String]]("POST_CODE")

  def countryCode = column[String]("COUNTRY_CODE")
  def vatNumber = column[Option[String]]("VAT_NUMBER")
  def registrationNumber = column[Option[String]]("REGISTRATION_NUMBER")
  def legalEntity = column[Boolean]("LEGAL_ENTITY")
  def active = column[Boolean]("ACTIVE")

  def * = id.? ~ name ~ street1 ~ street2 ~ city ~ province ~ postCode ~ countryCode ~ vatNumber ~ registrationNumber ~ legalEntity ~ active <>
    (Organisation.apply _, Organisation.unapply _)

  def forInsert = * returning id
}

object OrganisationMemberships extends Table[(Option[Long], Long, Long)]("ORGANISATION_MEMBERSHIPS") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def personId = column[Long]("PERSON_ID")
  def organisationId = column[Long]("ORGANISATION_ID")

  def person = foreignKey("PERSON_FK", personId, People)(_.id)
  def organisation = foreignKey("ORGANISATION_FK", organisationId, People)(_.id)

  def * = id.? ~ personId ~ organisationId
}