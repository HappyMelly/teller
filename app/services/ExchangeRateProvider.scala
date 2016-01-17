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

import models.ExchangeRate
import models.service.Services
import math.BigDecimal.int2bigDecimal
import org.joda.money.CurrencyUnit
import org.joda.time.DateTime
import play.api.Play.current
import play.api.libs.ws.WS
import scala.concurrent.Future

/**
 * A trait for anything that can return the current exchange rate for two given currencies.
 */
trait ExchangeRateProvider {

  def apply(base: CurrencyUnit, counter: CurrencyUnit): Future[Option[ExchangeRate]]

}

/**
 * An ExchangeRateProvider that queries Yahoo’s finace API (using YQL).
 */
object YahooExchangeRateProvider extends ExchangeRateProvider{
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  private val serviceUrl = "http://query.yahooapis.com/v1/public/yql"
  private val query = "select id, Rate from yahoo.finance.xchange where pair = \"%s%s\""
    .format(_: CurrencyUnit, _: CurrencyUnit)

  /**
   * Returns the exchange rate from the API request, or `None` if the request didn’t return a rate.
   */
  override def apply(base: CurrencyUnit, counter: CurrencyUnit): Future[Option[ExchangeRate]] =
    {
      WS.url(serviceUrl)
        .withQueryString(
          "q" -> query(base, counter),
          "format" -> "json",
          "env" -> "store://datatables.org/alltableswithkeys")
        .get().map {
          response ⇒
            try {
              val json = response.json
              assert((json \ "query" \ "results" \ "rate" \ "id").as[String].equals(s"$base$counter"))
              val timestamp = DateTime.parse((json \ "query" \ "created").as[String])
              val rate = (json \ "query" \ "results" \ "rate" \ "Rate").as[BigDecimal]
              if (rate.compare(0) == 0) None
              else Some(ExchangeRate(None, base, counter, rate, timestamp))
            } catch {
              case _: Throwable ⇒ None
            }
        }
    }

}

/**
 * An ExchangeRateProvider that queries the database for today’s rate.
 */
object DatabaseExchangeRateProvider extends ExchangeRateProvider with Services {
  override def apply(base: CurrencyUnit, counter: CurrencyUnit): Future[Option[ExchangeRate]] =
    exchangeService.fromDatabase(base, counter)
}

/**
 * A wrapper for another `ExchangeRateProvider` that stores the results in the database.
  *
  * @param wrappedProvider The provider for which to store the results.
 */
case class PersistingExchangeRateProvider(private val wrappedProvider: ExchangeRateProvider)
  extends ExchangeRateProvider
  with Services {
  import scala.concurrent.ExecutionContext.Implicits.global

  override def apply(base: CurrencyUnit, counter: CurrencyUnit): Future[Option[ExchangeRate]] = {
    val futureMaybeRate: Future[Option[ExchangeRate]] = wrappedProvider(base, counter)
    futureMaybeRate flatMap { case Some(rate) =>
      if (rate.id.isEmpty)
        exchangeService.insert(rate).map(x => Some(x))
      else
        Future.successful(None)
    }
  }
}

