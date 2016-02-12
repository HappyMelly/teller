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

import models.UserAccount
import slick.driver.JdbcProfile

private[models] trait UserAccountTable extends PersonTable {
  protected val driver: JdbcProfile
  import driver.api._
  protected val accounts = TableQuery[UserAccounts]

  object userAccountQuery {
    def findByPerson(personId: Long) = accounts.filter(_.personId === personId)
  }

  object userAccountActions {
    def insert(account: UserAccount) =
      (accounts returning accounts.map(_.id) into ((a, id) => a.copy(id = Some(id)))) += account

    def update(account: UserAccount) = accounts.filter(_.id === account.id).update(account)
  }

  /**
    * `UserAccount` database table mapping.
    */
  class UserAccounts(tag: Tag) extends Table[UserAccount](tag, "USER_ACCOUNT") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def personId = column[Long]("PERSON_ID")
    def byEmail = column[Boolean]("BY_EMAIL")
    def twitter = column[Option[String]]("TWITTER")
    def facebook = column[Option[String]]("FACEBOOK")
    def google = column[Option[String]]("GOOGLE")
    def linkedin = column[Option[String]]("LINKEDIN")
    def coordinator = column[Boolean]("COORDINATOR")
    def facilitator = column[Boolean]("FACILITATOR")
    def admin = column[Boolean]("ADMIN")
    def member = column[Boolean]("MEMBER")
    def registered = column[Boolean]("REGISTERED")
    def activeRole = column[Boolean]("ACTIVE_ROLE")
    def person = foreignKey("PERSON_FK", personId, TableQuery[People])(_.id)

    def * = (id.?, personId, byEmail, twitter, facebook, linkedin,
      google, coordinator, facilitator, admin, member, registered,
      activeRole) <>((UserAccount.apply _).tupled, UserAccount.unapply)

    def uniquePerson = index("IDX_PERSON_ID", personId, unique = true)
    def uniqueEmail = index("IDX_EMAIL_HANDLE", byEmail, unique = true)
    def uniqueTwitter = index("IDX_TWITTER", twitter, unique = true)
    def uniqueFacebook = index("IDX_FACEBOOK", facebook, unique = true)
    def uniqueGooglePlus = index("IDX_GOOGLE", google, unique = true)
    def uniqueLinkedIn = index("IDX_LINKEDIN", linkedin, unique = true)
  }

}
