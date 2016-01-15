package models.database

import slick.driver.JdbcProfile

private[models] trait RegisteringUserTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * `RegisteringUser` table mapping
    */
  class RegisteringUsers(tag: Tag) extends Table[(String, String)](tag, "REGISTERING_USER") {

    def userId = column[String]("USER_ID")
    def providerId = column[String]("PROVIDER_ID")

    def * = (userId, providerId)
  }

}
