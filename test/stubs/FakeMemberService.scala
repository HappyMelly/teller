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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing Happy Melly One, Handelsplein 37, Rotterdam,
 * The Netherlands, 3071 PR
 */
package stubs

import helpers.{ OrganisationHelper, PersonHelper }
import models.Member
import models.service.MemberService
import org.joda.money.Money
import org.joda.money.CurrencyUnit._
import org.joda.time.LocalDate

class FakeMemberService extends MemberService {

  override def findAll: List[Member] = {
    val m1 = new Member(Some(1L), Some(1L), person = true, funder = true,
      eur(200), LocalDate.parse("2015-01-01"))
    m1.memberObj_=(PersonHelper.one())
    val m2 = new Member(Some(2L), Some(2L), person = true, funder = false,
      eur(20), LocalDate.parse("2015-01-10"))
    m2.memberObj_=(PersonHelper.two())
    val m3 = new Member(Some(3L), Some(1L), person = false, funder = true,
      eur(2000), LocalDate.parse("2015-01-11"))
    m3.memberObj_=(OrganisationHelper.one)
    val m4 = new Member(Some(4L), Some(2L), person = false, funder = false,
      eur(40), LocalDate.parse("2015-01-15"))
    m4.memberObj_=(OrganisationHelper.two)
    List(m1, m2, m3, m4)
  }

  private def eur(amount: Float) = Money.of(EUR, amount)
}
