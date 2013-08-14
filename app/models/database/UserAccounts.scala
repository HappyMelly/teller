package models.database

import models.UserAccount
import play.api.db.slick.Config.driver.simple._

/**
 * `Brand` database table mapping.
 */
private[models] object UserAccounts extends Table[UserAccount]("USER_ACCOUNT") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def personId = column[Long]("PERSON_ID")
  def role = column[String]("ROLE")

  def person = foreignKey("PERSON_FK", personId, People)(_.id)

  def * = id.? ~ personId ~ role <> (UserAccount.apply _, UserAccount.unapply _)

  def forInsert = * returning id

  def uniqueCode = index("IDX_PERSON_ID", personId, unique = true)
}