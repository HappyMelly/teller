package models.database

import models.database.PortableJodaSupport._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import securesocial.core.providers.MailToken

/**
  * `MailToken` database table mapping
  */
private[models] class PasswordTokens(tag: Tag) extends Table[MailToken](tag, "PASSWORD_TOKEN") {
  def userId = column[String]("USER_ID", O.DBType("VARCHAR(254)"))
  def email = column[String]("EMAIL", O.DBType("VARCHAR(254)"))
  def created = column[DateTime]("CREATED")
  def expire = column[DateTime]("EXPIRE")
  def signUp = column[Boolean]("SIGN_UP")

  def * = (userId, email, created, expire, signUp) <> ((MailToken.apply _).tupled, MailToken.unapply)
}
