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

package models.database

import models.Address
import slick.driver.JdbcProfile

private[models] trait AddressTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * `Address` table mapping
    */
  private[models] class Addresses(tag: Tag) extends Table[Address](tag, "ADDRESS") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def street1 = column[Option[String]]("STREET_1")
    def street2 = column[Option[String]]("STREET_2")
    def city = column[Option[String]]("CITY")
    def province = column[Option[String]]("PROVINCE")
    def postCode = column[Option[String]]("POST_CODE")
    def countryCode = column[String]("COUNTRY_CODE")

    def * = (id.?, street1, street2, city, province, postCode,
      countryCode) <>((Address.apply _).tupled, Address.unapply)

  }

}
