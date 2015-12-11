package models.database

import models.EmailToken
import models.database.PortableJodaSupport._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

/**
  * `EmailToken` database table mapping
  */
private[models] class EmailTokens(tag: Tag) extends Table[EmailToken](tag, "EMAIL_TOKEN") {
  def token = column[String]("TOKEN", O.DBType("VARCHAR(254)"))
  def email = column[String]("EMAIL", O.DBType("VARCHAR(254)"))
  def userId = column[Long]("USER_ID")
  def created = column[DateTime]("CREATED")
  def expire = column[DateTime]("EXPIRE")

  def * = (token, email, userId, created, expire) <> ((EmailToken.apply _).tupled, EmailToken.unapply)
}
