package models

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB._
import views.Countries

case class Address(
  id: Option[Long] = None,
  street1: Option[String] = None,
  street2: Option[String] = None,
  city: Option[String] = None,
  province: Option[String] = None,
  postCode: Option[String] = None,
  countryCode: String = Countries.UnknownRegion)

object Address {

  def insert(address: Address): Address = withSession { implicit session ⇒
    val id = Addresses.forInsert.insert(address)
    address.copy(id = Some(id))
  }

  def update(address: Address): Unit = withSession { implicit session ⇒
    Addresses.filter(_.id === address.id).update(address)
  }
}

/**
 * Address table mapping
 */
object Addresses extends Table[Address]("ADDRESS") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def street1 = column[Option[String]]("STREET_1")
  def street2 = column[Option[String]]("STREET_2")
  def city = column[Option[String]]("CITY")
  def province = column[Option[String]]("PROVINCE")
  def postCode = column[Option[String]]("POST_CODE")
  def countryCode = column[String]("COUNTRY_CODE")

  def * = id.? ~ street1 ~ street2 ~ city ~ province ~ postCode ~ countryCode <> (Address.apply _, Address.unapply _)

  def forInsert = * returning id
}