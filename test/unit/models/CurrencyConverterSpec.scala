package unit.models.models

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

import models.ExchangeRate
import org.joda.money.CurrencyUnit._
import org.joda.money.{ CurrencyUnit, Money }
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import services.CurrencyConverter.NoExchangeRateException
import services.{ CurrencyConverter, ExchangeRateProvider }

import scala.concurrent.Future

class CurrencyConverterSpec extends Specification {

  val eurUsd = ExchangeRate(None, EUR, USD, BigDecimal("1.35098"), DateTime.now())
  val oneEuro = Money.of(EUR, 1)

  val someProvider = new ExchangeRateProvider {
    def apply(base: CurrencyUnit, counter: CurrencyUnit): Future[Option[ExchangeRate]] = Future.successful(Some(eurUsd))
  }

  val noneProvider = new ExchangeRateProvider {
    def apply(base: CurrencyUnit, counter: CurrencyUnit): Future[Option[ExchangeRate]] = Future.successful(None)
  }

  val someFirst = someProvider :: noneProvider :: Nil
  val noneFirst = noneProvider :: someProvider :: Nil
  val noneOnly = noneProvider :: Nil

  "The currency converter" should {
    "pick the first provider that can provide an exchange rate" in {
      CurrencyConverter.findRate(EUR, USD, someFirst) should beSome(eurUsd).await
      CurrencyConverter.findRate(EUR, USD, noneFirst) should beSome(eurUsd).await
      CurrencyConverter.findRate(EUR, USD, noneOnly) should beNone.await
    }

    "convert from a currency to itself without a provider" in {
      CurrencyConverter.convert(oneEuro, EUR, noneOnly) must beAnInstanceOf[Money].await
    }

    "convert between currencies it has a provider for" in {
      CurrencyConverter.convert(oneEuro, USD, someFirst) must beAnInstanceOf[Money].await
    }

    "fail conversions for which there is no exchange rate" in skipped {
      CurrencyConverter.convert(oneEuro, USD, noneOnly) must throwA[NoExchangeRateException].await
    }
  }

}
