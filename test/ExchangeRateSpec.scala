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

import models.ExchangeRate
import org.joda.money.{ Money, CurrencyUnit }
import CurrencyUnit.{ EUR, USD, GBP }
import org.joda.time.DateTime
import org.specs2.mutable._
import math.BigDecimal.int2bigDecimal

class ExchangeRateSpec extends Specification {

  val oneEuro = Money.of(EUR, 1.bigDecimal)
  val eurUsd = ExchangeRate(None, EUR, USD, BigDecimal("1.35098"), DateTime.now)
  val onePound = Money.of(GBP, 1.bigDecimal)

  s"The exchange rate $eurUsd" should {

    val oneEurInUsd = Money.of(USD, 1.35)
    val oneEuroConvertedTwice = Money.of(EUR, 0.99)

    s"convert $oneEuro to $oneEurInUsd" in {
      eurUsd convert oneEuro must be equalTo oneEurInUsd
    }

    s"convert $oneEurInUsd to $oneEuroConvertedTwice" in {
      eurUsd convert oneEurInUsd must be equalTo oneEuroConvertedTwice
    }

    s"not be able to convert $onePound" in {
      eurUsd convert onePound must throwA[IllegalArgumentException]
    }
  }

  val usdEur = ExchangeRate(None, USD, EUR, BigDecimal("0.740417"), DateTime.now)

  s"The exchange rate $usdEur" should {

    val oneDollar = Money.of(USD, 1.bigDecimal)
    val oneDollarInEur = Money.of(EUR, 0.74)
    val oneDollarConvertedTwice = Money.of(USD, 0.99)

    s"convert $oneDollar to $oneDollarInEur" in {
      usdEur convert oneDollar must be equalTo oneDollarInEur
    }

    s"convert $oneDollarInEur to base" in {
      usdEur convert oneDollarInEur must be equalTo oneDollarConvertedTwice
    }
  }

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

    "return the input when both currencies are the same" in {
      val eurEur = ExchangeRate(None, EUR, EUR, 1.bigDecimal, DateTime.now)
      eurEur convert oneEuro must be equalTo oneEuro
    }
  }
}
