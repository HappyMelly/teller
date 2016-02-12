package models.database

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.EmailToken
import org.joda.time.DateTime
import slick.driver.JdbcProfile

private[models] trait EmailTokenTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * `EmailToken` database table mapping
    */
  private[models] class EmailTokens(tag: Tag) extends Table[EmailToken](tag, "EMAIL_TOKEN") {

    def token = column[String]("TOKEN", O.Length(254, varying = true))
    def email = column[String]("EMAIL", O.Length(254, varying = true))
    def userId = column[Long]("USER_ID")
    def created = column[DateTime]("CREATED")
    def expire = column[DateTime]("EXPIRE")

    def * = (token, email, userId, created, expire) <>((EmailToken.apply _).tupled, EmailToken.unapply)
  }

}
