package models

import com.github.tototoshi.slick.JodaSupport._
import models.database.Licenses
import org.joda.time.LocalDate
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Logger
import play.api.Play.current

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

