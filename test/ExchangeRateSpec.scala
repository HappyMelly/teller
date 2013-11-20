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
//import java.math.BigDecimal
import math.BigDecimal.int2bigDecimal

class ExchangeRateSpec extends Specification {

  val eurUsd = ExchangeRate(EUR, USD, BigDecimal("1.35098"), DateTime.now())
  val usdEur = ExchangeRate(USD, EUR, BigDecimal("0.740417"), DateTime.now())

  val oneEuro = Money.of(EUR, 1.bigDecimal)
  val oneDollar = Money.of(USD, 1.bigDecimal)
  val onePound = Money.of(GBP, 1.bigDecimal)

  "Exchange rate conversion " should {

    "correctly convert from base to counter" in {
      eurUsd convert oneEuro must be equalTo Money.of(USD, 1.35)
      usdEur convert oneDollar must be equalTo Money.of(EUR, 0.74)
    }

    "correctly convert from counter to base" in {
      eurUsd convert Money.of(USD, 1.35) must be equalTo Money.of(EUR, 0.99)
      usdEur convert Money.of(EUR, 0.74) must be equalTo Money.of(USD, 0.99)
    }

    "accept only a rate of 1 when both currencies are the same" in {
      ExchangeRate(EUR, EUR, 2.bigDecimal, DateTime.now()) must throwAn[AssertionError]
      (ExchangeRate(EUR, EUR, 1.bigDecimal, DateTime.now()) must not).throwAn[AssertionError]
    }

    "return the input when both currencies are the same" in {
      val eurEur = ExchangeRate(EUR, EUR, 1.bigDecimal, DateTime.now())
      eurEur convert oneEuro must be equalTo oneEuro
    }

    "not be able to convert other currencies" in {
      eurUsd convert onePound must throwA[IllegalArgumentException]
    }
  }
}
