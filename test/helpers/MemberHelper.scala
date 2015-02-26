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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package helpers

import models.Member
import org.joda.money.Money
import org.joda.money.CurrencyUnit._
import org.joda.time.{ DateTime, LocalDate }

object MemberHelper {

  def make(id: Option[Long] = None,
    objectId: Long,
    person: Boolean,
    funder: Boolean,
    money: Option[Money] = Some(Money.of(EUR, 100)),
    subscription: Boolean = true,
    since: Option[LocalDate] = Some(LocalDate.now().minusDays(4)),
    until: Option[LocalDate] = Some(LocalDate.now().plusYears(1)),
    existingObject: Option[Boolean] = Some(false)): Member = {
    new Member(id, objectId, person, funder, money.get, subscription, since.get,
      until.get, existingObject.get, DateTime.now(), 1L, DateTime.now(), 1L)
  }
}
