package models.database

import com.github.tototoshi.slick.JodaSupport._
import models.Activity
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

/**
 * `Activity` database table mapping.
 */
private[models] object Activities extends Table[Activity]("ACTIVITY") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def subject = column[String]("SUBJECT")
  def predicate = column[String]("PREDICATE")
  def activityObject = column[Option[String]]("OBJECT")
  def created = column[DateTime]("CREATED")
  def * = id.? ~ subject ~ predicate ~ activityObject ~ created <> (Activity.apply _, Activity.unapply _)

  def forInsert = * returning id
}