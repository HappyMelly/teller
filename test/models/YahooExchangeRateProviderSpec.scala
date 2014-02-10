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

import concurrent.duration._
import org.joda.money.CurrencyUnit._
import org.specs2.mutable._
import org.specs2.time.NoTimeConversions
import services.YahooExchangeRateProvider

class YahooExchangeRateProviderSpec extends Specification with NoTimeConversions {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  "The provider" should {
    "return a result for EUR-USD" in {
      pending("but needs a way to avoid failures caused by API throttling")
      // TODO: replace this with a way to test our app, not Yahoo’s API. Checking that Yahoo’s API is still working
      // requires a separate check that doesn’t happen at build time, e.g. a separate asynchronous job.
      YahooExchangeRateProvider(EUR, USD) must beSome[ExchangeRate].await(2, 3.seconds)
    }
  }

}
