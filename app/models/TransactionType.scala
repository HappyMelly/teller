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

import models.database.{ BookingEntries, TransactionTypes }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current

/**
 * A category for a `BookingEntry`.
 */
case class TransactionType(id: Option[Long], name: String)

object TransactionType {

  def delete(id: Long): Unit = withSession { implicit session ⇒
    BookingEntries.filter(_.transactionTypeId === id).map(_.transactionTypeId).update(None)
    TransactionTypes.where(_.id === id).mutate(_.delete())
  }

  /**
   * Returns true if a transaction type with the given value already exists.
   */
  def exists(value: String): Boolean = withSession { implicit session ⇒
    Query(Query(TransactionTypes).filter(_.name === value).exists).first
  }

  def find(id: Long): Option[TransactionType] = withSession { implicit session ⇒
    Query(TransactionTypes).filter(_.id === id).firstOption
  }

  def findAll: List[TransactionType] = withSession { implicit session ⇒
    Query(TransactionTypes).sortBy(_.name.toLowerCase).list
  }

  /**
   * Inserts a new transaction type with the given value.
   */
  def insert(value: String): Unit = withSession { implicit session ⇒
    val transactionType = TransactionType(None, value)
    TransactionTypes.forInsert.insert(transactionType)
  }

}