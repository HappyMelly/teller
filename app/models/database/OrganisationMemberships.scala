package models.database

import play.api.db.slick.Config.driver.simple._

/**
 * `Organisation` database table mapping.
 */
private[models] object OrganisationMemberships extends Table[(Option[Long], Long, Long)]("ORGANISATION_MEMBERSHIPS") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def personId = column[Long]("PERSON_ID")
  def organisationId = column[Long]("ORGANISATION_ID")

  def person = foreignKey("PERSON_FK", personId, People)(_.id)
  def organisation = foreignKey("ORGANISATION_FK", organisationId, Organisations)(_.id)

  def * = id.? ~ personId ~ organisationId
  def forInsert = personId ~ organisationId returning id
}