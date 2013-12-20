package models

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

import org.joda.money.{ Money, CurrencyUnit }
import CurrencyUnit.{ EUR, USD, GBP }
import org.joda.time.DateTime
import org.specs2.matcher.DataTables
import org.specs2.mutable._
import math.BigDecimal.int2bigDecimal

class ExchangeRateSpec extends Specification with Tables {

  "Exchange rates".title

  // format: OFF
  "Exchange rate conversion should give the expected results, rounded to currency amounts:" ^ {

    "Base" | "Counter" | "Rate" | "From currency" | "From amount" | "Result currency" | "Result amount" |
    "EUR" ! "USD" ! 1.35098 ! "EUR" ! 1.00 ! "USD" ! 1.35 |
    "EUR" ! "USD" ! 1.35098 ! "USD" ! 1.35 ! "EUR" ! 0.99 |
    "USD" ! "EUR" ! 0.740417 ! "USD" ! 1.00 ! "EUR" ! 0.74 |
    "USD" ! "EUR" ! 0.740417 ! "EUR" ! 0.74 ! "USD" ! 0.99 |> {
      (base, counter, rate, fromCurrency, fromAmount, resultCurrency, resultAmount) ⇒
        val baseCurrency = CurrencyUnit.of(base)
        val counterCurrency = CurrencyUnit.of(counter)
        val exchangeRate = ExchangeRate(None, baseCurrency, counterCurrency, BigDecimal(rate), DateTime.now)
        val from = Money.of(CurrencyUnit.of(fromCurrency), fromAmount)
        val result = Money.of(CurrencyUnit.of(resultCurrency), resultAmount)
        exchangeRate convert from must be equalTo result
    }
  } bt
  // format: ON

  val eurUsd = ExchangeRate(None, EUR, USD, BigDecimal("1.35098"), DateTime.now)
  val onePound = Money.of(GBP, 1.bigDecimal)

  s"The exchange rate $eurUsd" should {

    s"not be able to convert $onePound" in {
      eurUsd convert onePound must throwA[IllegalArgumentException]
    }
  }

  val usdEur = ExchangeRate(None, USD, EUR, BigDecimal("0.740417"), DateTime.now)

  "Exchange rate conversion" should {

    "reject a negative rate" in {
      ExchangeRate(None, EUR, USD, -1, DateTime.now) must throwAn[IllegalArgumentException]
    }

    "reject a zero rate" in {
      ExchangeRate(None, EUR, USD, 0, DateTime.now) must throwAn[IllegalArgumentException]
    }

    "only accept a rate of 1.0000 when both currencies are the same" in {
      ExchangeRate(None, EUR, EUR, 2.bigDecimal, DateTime.now) must throwAn[AssertionError]
      (ExchangeRate(None, EUR, EUR, 1.bigDecimal, DateTime.now) must not).throwAn[AssertionError]
    }

    "return the input when the base and counter currencies are the same" in {
      val eurEur = ExchangeRate(None, EUR, EUR, 1.bigDecimal, DateTime.now)
      val oneEuro = Money.of(EUR, 1.bigDecimal)
      eurEur convert oneEuro must be equalTo oneEuro
    }
  }
}
