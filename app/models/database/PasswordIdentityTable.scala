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

import models.PasswordIdentity
import play.api.db.slick.Config.driver.simple._
import slick.driver.JdbcProfile

private[models] trait PasswordIdentityTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * Maps PasswordIdentity to its datase representation
    */
  class PasswordIdentities(tag: Tag) extends Table[PasswordIdentity](tag, "PASSWORD_IDENTITY") {

    def userId = column[Option[Long]]("USER_ID")
    def email = column[String]("EMAIL")
    def password = column[String]("PASSWORD")
    def firstName = column[Option[String]]("FIRST_NAME")
    def lastName = column[Option[String]]("LAST_NAME")
    def hasher = column[String]("HASHER")

    type PasswordIdentityFields = (Option[Long], String, String, Option[String], Option[String], String)

    def * = (userId, email, password, firstName, lastName, hasher) <>(
      (PasswordIdentity.apply _).tupled, PasswordIdentity.unapply)
  }

}