/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models

import models.database.Addresses
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
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

  def find(id: Long): Address = DB.withSession { implicit session: Session ⇒
    Query(Addresses).filter(_.id === id).first
  }

  def insert(address: Address): Address = DB.withSession { implicit session: Session ⇒
    val id = Addresses.forInsert.insert(address)
    address.copy(id = Some(id))
  }

  def update(address: Address): Unit = DB.withSession { implicit session: Session ⇒
    Addresses.filter(_.id === address.id).update(address)
  }
}

