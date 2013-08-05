package models.database

import com.github.tototoshi.slick.JodaSupport._
import models.License
import org.joda.time.LocalDate
import play.api.db.slick.Config.driver.simple._

/**
 * `License` database table mapping.
 */
private[models] object Licenses extends Table[License]("LICENSE") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def licenseeId = column[Long]("LICENSEE_ID")
  def brandId = column[Long]("BRAND_ID")
  def start = column[LocalDate]("START")
  def end = column[LocalDate]("END")

  def licensee = foreignKey("LICENSEE_FK", licenseeId, People)(_.id)
  def brand = foreignKey("BRAND_FK", brandId, Brands)(_.id)

  def * = id.? ~ licenseeId ~ brandId ~ start ~ end <> (License.apply _, License.unapply _)
  def forJoin = id.? ~ licenseeId.? ~ brandId.? ~ start.? ~ end.?

  def forInsert = * returning id
}