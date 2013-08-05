package models

import models.database.Addresses
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB._
import play.api.Play.current

case class Address(
  id: Option[Long] = None,
  street1: Option[String] = None,
  street2: Option[String] = None,
  city: Option[String] = None,
  province: Option[String] = None,
  postCode: Option[String] = None,
  countryCode: String)

object Address {

  def find(id: Long): Address = withSession { implicit session ⇒
    Query(Addresses).filter(_.id === id).first
  }

  def insert(address: Address): Address = withSession { implicit session ⇒
    val id = Addresses.forInsert.insert(address)
    address.copy(id = Some(id))
  }

  def update(address: Address): Unit = withSession { implicit session ⇒
    Addresses.filter(_.id === address.id).update(address)
  }
}

