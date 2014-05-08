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

package services

import org.joda.money.{ CurrencyUnit, Money }
import models.ExchangeRate
import scala.concurrent.Future

/**
 * Looks up exchange rates and converts amounts between currencies.
 */
object CurrencyConverter {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  class NoExchangeRateException(message: String) extends RuntimeException(message)

  private val defaultProviders: List[ExchangeRateProvider] = DatabaseExchangeRateProvider :: PersistingExchangeRateProvider(YahooExchangeRateProvider) :: Nil

  /**
   * Converts `amount` to `targetCurrency`, using the default exchange rate providers.
   * @param amount The amount to convert
   * @param targetCurrency The currency to convert the amount to
   * @param exchangeRateProviders The list of `ExchangeRateProvider`s to use. Defaults to `defaultProviders`.
   * @return The converted amount if successful, or `NoExchangeRateException`
   */
  def convert(amount: Money, targetCurrency: CurrencyUnit, exchangeRateProviders: Seq[ExchangeRateProvider] = defaultProviders): Future[Money] = {
    if (targetCurrency == amount.getCurrencyUnit) {
      Future.successful(amount)
    } else {
      findRate(amount.getCurrencyUnit, targetCurrency, exchangeRateProviders).map {
        case Some(rate) ⇒ rate.apply(amount)
        case None ⇒ throw new NoExchangeRateException(s"No exchange rate found for ${amount.getCurrencyUnit} - $targetCurrency")
      }
    }
  }

  /**
   * Find the exchange rate between `base` and `currency` by sequentially querying a sequence of `ExchangeRateProviders`,
   * returning the result of the first provider in the list to answer.
   * @param base The base currency
   * @param counter The counter currency
   * @param exchangeRateProviders a sequence of `ExchangeRateProvider`, in order of preference. Defaults to `defaultProviders`.
   * @return The first answer found, or None if no providers can provide a rate
   */
  def findRate(base: CurrencyUnit, counter: CurrencyUnit, exchangeRateProviders: Seq[ExchangeRateProvider] = defaultProviders): Future[Option[ExchangeRate]] = {
    exchangeRateProviders match {
      case provider :: tail ⇒
        val rateMaybeFuture: Future[Option[ExchangeRate]] = provider.apply(base, counter)
        rateMaybeFuture.flatMap {
          case Some(rate) ⇒ Future.successful(Some(rate))
          case None ⇒ findRate(base, counter, tail)
        }.recover {
          case _ ⇒ None
        }
      case Nil ⇒ Future.successful(None)
    }
  }

}
