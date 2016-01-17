package models.database

import com.github.tototoshi.slick.MySQLJodaSupport._
import org.joda.time.DateTime
import securesocial.core.providers.MailToken
import slick.driver.JdbcProfile

private[models] trait PasswordTokenTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * `MailToken` database table mapping
    */
  class PasswordTokens(tag: Tag) extends Table[MailToken](tag, "PASSWORD_TOKEN") {

    def userId = column[String]("USER_ID", O.Length(254, varying = true))
    def email = column[String]("EMAIL", O.Length(254, varying = true))
    def created = column[DateTime]("CREATED")
    def expire = column[DateTime]("EXPIRE")
    def signUp = column[Boolean]("SIGN_UP")

    def * = (userId, email, created, expire, signUp) <>((MailToken.apply _).tupled, MailToken.unapply)
  }

}