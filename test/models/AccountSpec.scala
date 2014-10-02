package models

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

import org.joda.money.{ Money, CurrencyUnit }
import org.joda.money.CurrencyUnit._
import org.specs2.mutable.Specification

class AccountSpec extends Specification {

  val SEK = CurrencyUnit.of("SEK")

  val accounts = List(
    AccountSummaryWithAdjustment(1, "Happy Melly Levy", Money.zero(EUR), Money.zero(EUR), Money.zero(EUR)),
    AccountSummaryWithAdjustment(2, "Happy Melly One BV", Money.of(EUR, -440.0), Money.of(EUR, -440.0), Money.zero(EUR)),
    AccountSummaryWithAdjustment(3, "Happy Melly Levy", Money.of(SEK, 4000.0), Money.of(EUR, 450.0), Money.zero(EUR)))

  val eur10 = Money.of(EUR, 10.0)
  val eur0 = Money.of(EUR, 0)

  "Balancing accounts" should {
    "calculate the total balance from converted balances" in {
      Account.calculateTotalBalance(EUR, accounts) must be equalTo eur10
    }
    "equally divide the adjustment across accounts (rounding down)" in {
      Account.calculateAdjustment(eur10, accounts) must be equalTo Money.of(EUR, 3.33)
    }
    "generate booking entries with a zero from amount" in {
      val levy = Account(id = Some(0), currency = EUR)
      Account.adjustmentBookingEntry(0, 0, levy, eur10, eur10).fromAmount must be equalTo eur0
    }
  }

}
