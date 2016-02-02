/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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

import models.JodaMoney._
import models.Account
import org.joda.money.CurrencyUnit
import slick.driver.JdbcProfile

private[models] trait AccountTable extends PersonTable with OrganisationTable {

  protected val driver: JdbcProfile
  import driver.api._

  class Accounts(tag: Tag) extends Table[Account](tag, "ACCOUNT") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def personId = column[Option[Long]]("PERSON_ID")
    def organisationId = column[Option[Long]]("ORGANISATION_ID")
    def currency = column[CurrencyUnit]("CURRENCY", O.Length(3), O.Default(CurrencyUnit.of("EUR")))
    def active = column[Boolean]("ACTIVE", O.Default(false))

    def person = foreignKey("PERSON_FK", personId, TableQuery[People])(_.id.?)
    def organisation = foreignKey("PERSON_FK", organisationId, TableQuery[Organisations])(_.id.?)

    def * = (id.?, organisationId, personId, currency, active) <>(
      (Account.apply _).tupled, Account.unapply)

  }

}