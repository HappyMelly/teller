package models.database

import play.api.db.slick.Config.driver.simple._

/**
  * `RegisteringUser` table mapping
  */
private[models] class RegisteringUsers(tag: Tag) extends Table[(String, String)](tag, "REGISTERING_USER"){
  def userId = column[String]("USER_ID")
  def providerId = column[String]("PROVIDER_ID")

  def * = (userId, providerId)
}
