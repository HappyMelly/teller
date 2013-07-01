package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current
import scala.slick.lifted.Query

/**
 * An organisation, usually a company, such as a Happy Melly legal entity.
 */
case class Organisation(id: Option[Long], name: String, street1: Option[String], street2: Option[String],
  city: Option[String], province: Option[String], postCode: Option[String], countryCode: String,
  vatNumber: Option[String], registrationNumber: Option[String], legalEntity: Boolean = false, active: Boolean = true)

object Organisation {

  def findAll: List[Organisation] = withSession { implicit session ⇒
    Query(Organisations).sortBy(_.name.toLowerCase).list
  }
}

object Organisations extends Table[Organisation]("ORGANISATION") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")

  def street1 = column[Option[String]]("STREET_1")
  def street2 = column[Option[String]]("STREET_2")
  def city = column[Option[String]]("CITY")
  def province = column[Option[String]]("PROVINCE")
  def postCode = column[Option[String]]("POST_CODE")

  // TODO Change to a list of country codes (i.e. a foreign key)
  def countryCode = column[String]("COUNTRY_CODE")
  def vatNumber = column[Option[String]]("VAT_NUMBER")
  def registrationNumber = column[Option[String]]("REGISTRATION_NUMBER")
  def legalEntity = column[Boolean]("LEGAL_ENTITY")
  def active = column[Boolean]("ACTIVE")

  def * = id.? ~ name ~ street1 ~ street2 ~ city ~ province ~ postCode ~ countryCode ~ vatNumber ~ registrationNumber ~ legalEntity ~ active <>
    (Organisation.apply _, Organisation.unapply _)

  def forInsert = * returning id
}