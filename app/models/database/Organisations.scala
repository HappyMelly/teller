package models.database

import com.github.tototoshi.slick.JodaSupport._
import models.Organisation
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

/**
 * `Organisation` database table mapping.
 */
private[models] object Organisations extends Table[Organisation]("ORGANISATION") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")

  def street1 = column[Option[String]]("STREET_1")
  def street2 = column[Option[String]]("STREET_2")
  def city = column[Option[String]]("CITY")
  def province = column[Option[String]]("PROVINCE")
  def postCode = column[Option[String]]("POST_CODE")

  def countryCode = column[String]("COUNTRY_CODE")
  def vatNumber = column[Option[String]]("VAT_NUMBER")
  def registrationNumber = column[Option[String]]("REGISTRATION_NUMBER")
  def legalEntity = column[Boolean]("LEGAL_ENTITY")
  def active = column[Boolean]("ACTIVE")

  def created = column[DateTime]("CREATED")
  def createdBy = column[String]("CREATED_BY")
  def updated = column[DateTime]("UPDATED")
  def updatedBy = column[String]("UPDATED_BY")

  def * = id.? ~ name ~ street1 ~ street2 ~ city ~ province ~ postCode ~ countryCode ~ vatNumber ~ registrationNumber ~
    legalEntity ~ active ~ created ~ createdBy ~ updated ~ updatedBy <> (Organisation.apply _, Organisation.unapply _)

  def forInsert = * returning id

  def forUpdate = id.? ~ name ~ street1 ~ street2 ~ city ~ province ~ postCode ~ countryCode ~ vatNumber ~ registrationNumber ~
    legalEntity ~ active ~ updated ~ updatedBy
}