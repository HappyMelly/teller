package models

import models.database.{ Licenses, Brands }
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current

/**
 * A person, such as the owner or employee of an organisation.
 */
case class Brand(id: Option[Long], code: String, name: String, coordinatorId: Long,
  created: DateTime, createdBy: String, updated: DateTime, updatedBy: String) {

  def insert = withSession { implicit session ⇒
    val id = Brands.forInsert.insert(this)
    this.copy(id = Some(id))
  }

  def delete = Brand.delete(this.id.get)
}

case class BrandView(id: Long, code: String, name: String, coordinator: Person, licenses: Seq[Long])

case object BrandView {
  def apply(brand: Brand, coordinator: Person, licenses: Seq[Long]) = new BrandView(brand.id.get, brand.code, brand.name, coordinator, licenses)
}

object Brand {

  /**
   * Returns true if and only if there is a brand with the given code.
   */
  def exists(code: String): Boolean = withSession { implicit session ⇒
    Query(Query(Brands).filter(_.code === code).exists).first
  }

  /** Finds a brand by ID **/
  def find(id: Long) = withSession { implicit session ⇒
    Query(Brands).filter(_.id === id).firstOption
  }

  def findAll: List[BrandView] = withSession { implicit session ⇒
    val query = for {
      (brand, license) ← Brands leftJoin Licenses on (_.id === _.brandId)
      coordinator ← brand.coordinator
    } yield (brand, coordinator, license.id.?)

    // Transform results to BrandView
    // TODO Preserve query order, currently lost by the groupBy
    query.sortBy(_._1.name).list.groupBy {
      case (brand, coordinator, _) ⇒ brand -> coordinator
    }.mapValues(_.flatMap(_._3)).map {
      case ((brand, coordinator), licenseIDs) ⇒
        BrandView(brand, coordinator, licenseIDs)
    }.toList
  }

  def delete(id: Long) = withSession { implicit session ⇒
    Brands.where(_.id === id).mutate(_.delete)
  }

}

