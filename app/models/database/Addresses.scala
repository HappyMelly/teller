package models.database

import models.Address
import play.api.db.slick.Config.driver.simple._

/**
 * `Address` table mapping
 */
private[models] object Addresses extends Table[Address]("ADDRESS") {

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