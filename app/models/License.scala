package models

import com.github.tototoshi.slick.JodaSupport._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current
import scala.slick.lifted.Query
import org.joda.time.{ DateTime, LocalDate }
import play.api.Logger

/**
 * A content license - a person’s agreement with Happy Melly to use a `Brand`.
 */
case class License(id: Option[Long], licenseeId: Long, brandId: Long, start: LocalDate, end: LocalDate)

object License {

  def licensees(brandCode: String, date: LocalDate = LocalDate.now()): List[Person] = withSession { implicit session ⇒
    Logger.debug(s"date = $date")
    val query = for {
      license ← Licenses if license.start <= date && license.end >= date
      brand ← license.brand if brand.code === brandCode
      licensee ← license.licensee
    } yield licensee
    query.sortBy(_.lastName.toLowerCase).list
  }
}

object Licenses extends Table[License]("LICENSE") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def licenseeId = column[Long]("LICENSEE_ID")
  def brandId = column[Long]("BRAND_ID")
  def start = column[LocalDate]("START")
  def end = column[LocalDate]("END")

  def licensee = foreignKey("LICENSEE_FK", licenseeId, People)(_.id)
  def brand = foreignKey("BRAND_FK", brandId, Brands)(_.id)

  def * = id.? ~ licenseeId ~ brandId ~ start ~ end <> (License.apply _, License.unapply _)

  def forInsert = * returning id
}
