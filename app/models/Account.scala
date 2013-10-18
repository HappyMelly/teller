/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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

import org.joda.money.{ Money, CurrencyUnit }
import models.database.{ Organisations, People, Accounts }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current

/**
 * Represents a (financial) Account. An account has an `AccountHolder`, which is either a `Person`, `Organisation` or
 * the `Levy`. Accounts have a currency set upon activation and may only be deactivated when their balance is zero.
 */
case class Account(id: Option[Long], organisationId: Option[Long], personId: Option[Long], currency: CurrencyUnit, active: Boolean) {

  /** Resolves the holder for this account **/
  def accountHolder = (organisationId, personId) match {
    case (Some(o), None) ⇒ Organisation.find(o)
      .orElse(throw new IllegalStateException(s"Organisation with id $o (account holder for account ${id.getOrElse("(NEW)")}) does not exist"))
      .get
    case (None, Some(p)) ⇒ Person.find(p)
      .orElse(throw new IllegalStateException(s"Person with id $p (account holder for account ${id.getOrElse("(NEW)")}) does not exist"))
      .get
    case (None, None) ⇒ Levy
    case _ ⇒ throw new IllegalStateException(s"Account $id has both organisation and person for holder")
  }

  def balance: Money = Money.of(currency, 0)

  /**
   * Checks if the given user has permission to (de)activate this account:
   * - An account for a person may only be (de)activated by that person
   * - An account for an organisation may only be (de)activated by members of that organisation
   * - The Levy account may only be (de)activated by admins
   */

  def canBeActivatedBy(user: UserAccount) = accountHolder match {
    case organisation: Organisation ⇒ organisation.members.map(_.id.get).contains(user.personId)
    case person: Person ⇒ person.id.get == user.personId
    case Levy ⇒ user.getPermissions.contains(UserRole.Role.Admin)
  }

  /** Activates this account and sets the balance currency **/
  def activate(currency: CurrencyUnit): Unit = {
    if (active) throw new IllegalStateException("Cannot activate an already active account")
    assert(balance.isZero, "Inactive account's balance should be zero")
    updateStatus(active = true, currency)
    copy(active = true, currency = currency)
  }

  /** Deactivates this account  **/
  def deactivate(): Unit = {
    if (!active) throw new IllegalStateException("Cannot deactivate an already inactive account")
    if (!balance.isZero) throw new IllegalStateException("Cannot deactivate with non-zero balance")
    updateStatus(active = false, currency)
    copy(active = false)
  }

  private def updateStatus(active: Boolean, currency: CurrencyUnit): Unit = withSession { implicit session ⇒
    val updateQuery = for { a ← Accounts if a.id === this.id } yield (a.id, a.active, a.currency)
    updateQuery.mutate(mutator ⇒ mutator.row = (mutator.row._1, active, currency))
  }
}

/**
 * Account summary for use in views.
 *
 * @param id Account ID
 * @param name Account holder name
 */
case class AccountSummary(id: Long, name: String, currencyCode: String)

object Account {
  def accountHolderName(firstName: Option[String], lastName: Option[String], organisation: Option[String]): String =
    (firstName, lastName, organisation) match {
      case (Some(first), Some(last), None) ⇒ first + " " + last
      case (None, None, Some(name)) ⇒ name
      case (None, None, None) ⇒ Levy.name
      case _ ⇒ throw new IllegalStateException(s"Invalid combination of first, last and organisation names ($firstName, $lastName, $organisation)")
    }

  def find(holder: AccountHolder): Account = withSession { implicit session ⇒
    val query = holder match {
      case o: Organisation ⇒ Query(Accounts).filter(_.organisationId === o.id)
      case p: Person ⇒ Query(Accounts).filter(_.personId === p.id)
      case Levy ⇒ Query(Accounts).filter(_.organisationId isNull).filter(_.personId isNull)
    }
    query.first()
  }

  def find(id: Long): Option[Account] = withSession { implicit session ⇒
    Query(Accounts).filter(_.id === id).firstOption()
  }

  /**
   * Returns a summary list of active accounts.
   */
  def findAllActive: List[AccountSummary] = withSession { implicit session ⇒
    val query = for {
      ((account, person), organisation) ← Accounts leftJoin
        People on (_.personId === _.id) leftJoin
        Organisations on (_._1.organisationId === _.id)
      if account.active === true
    } yield (account.id, account.currency, person.firstName.?, person.lastName.?, organisation.name.?)

    query.mapResult{
      case (id, currency, firstName, lastName, organisationName) ⇒
        AccountSummary(id, accountHolderName(firstName, lastName, organisationName), currency.getCode)
    }.list.sortBy(_.name.toLowerCase)
  }
}
