package models.database

import com.github.tototoshi.slick.JodaSupport._
import models.Brand
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

/**
 * `Brand` database table mapping.
 */
private[models] object Brands extends Table[Brand]("BRAND") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def code = column[String]("CODE")
  def name = column[String]("NAME")
  def coordinatorId = column[Long]("COORDINATOR_ID")

  def created = column[DateTime]("CREATED")
  def createdBy = column[String]("CREATED_BY")

  def updated = column[DateTime]("UPDATED")
  def updatedBy = column[String]("UPDATED_BY")

  def coordinator = foreignKey("COORDINATOR_FK", coordinatorId, People)(_.id)

  def * = id.? ~ code ~ name ~ coordinatorId ~
    created ~ createdBy ~ updated ~ updatedBy <> (Brand.apply _, Brand.unapply _)

  def forInsert = * returning id

  def forUpdate = code ~ name ~ coordinatorId ~ updated ~ updatedBy

  def uniqueCode = index("IDX_CODE", code, unique = true)
}
