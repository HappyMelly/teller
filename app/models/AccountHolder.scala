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

import org.joda.time.DateTime

/** The 'owner' of an account **/
trait AccountHolder {
  def name: String
  def levy: Boolean = false
  lazy val account: Account = Account.find(this)

  /** Updates the `updatedBy` and `updated` properties, if applicable **/
  def updated(updatedBy: String): AccountHolder = {
    this match {
      case p: Person ⇒ p.copy(dateStamp = p.dateStamp.copy(updated = DateTime.now(), updatedBy = updatedBy)).update
      case o: Organisation ⇒ o.copy(updated = DateTime.now(), updatedBy = updatedBy).update
      case _ ⇒ this
    }
  }
}

/** Special 'system' account **/
object Levy extends AccountHolder {
  def name = "Happy Melly Levy"
  override def levy = true

  // Nothing to update
  def update = Levy
}
