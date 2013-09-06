package models

import models.database.{ OrganisationMemberships, Organisations }
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.db.slick.DB
import play.api.Play.current
import scala.slick.lifted.Query

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
  legalEntity: Boolean = false,
  active: Boolean = true,
  created: DateTime = DateTime.now(),
  createdBy: String,
  updated: DateTime,
  updatedBy: String) {

  def members: List[Person] = withSession { implicit session ⇒
    val query = for {
      membership ← OrganisationMemberships if membership.organisationId === this.id
      person ← membership.person
    } yield person
    query.sortBy(_.lastName.toLowerCase).list
  }

  /**
   * Inserts or updates this organisation into the database.
   * @return The Organisation as it is saved (with the id added if this was an insert)
   */
  def save: Organisation = DB.withSession { implicit session ⇒
    if (id.isDefined) {
      val filter: Query[Organisations.type, Organisation] = Query(Organisations).filter(_.id === id)
      val q = filter.map { org ⇒ org.forUpdate }

      // Skip the created, createdBy and active fields.
      val updateTuple = (id, name, street1, street2, city, province, postCode, countryCode, vatNumber, registrationNumber,
        legalEntity, updated, updatedBy)
      q.update(updateTuple)
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
  def delete(id: Long): Unit = withSession { implicit session ⇒
    Organisations.where(_.id === id).mutate(_.delete())
  }

  def find(id: Long): Option[Organisation] = withSession { implicit session ⇒
    Query(Organisations).filter(_.id === id).list.headOption
  }

  def findAll: List[Organisation] = withSession { implicit session ⇒
    Query(Organisations).sortBy(_.name.toLowerCase).list
  }

  /**
   * Returns a list of active organisations, optionally filtered to only include legal entities.
   */
  def find(legalEntitiesOnly: Boolean): List[Organisation] = withSession { implicit session ⇒
    val query = if (legalEntitiesOnly) Query(Organisations).filter(_.legalEntity === true) else Query(Organisations)
    query.filter(_.active).sortBy(_.name.toLowerCase).list
  }

  def findActive: List[Organisation] = withSession { implicit session ⇒
    Query(Organisations).filter(_.active === true).sortBy(_.name.toLowerCase).list
  }
}

