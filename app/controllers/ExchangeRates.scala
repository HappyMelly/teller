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

package controllers

import play.api.mvc.{ SimpleResult, Action, Controller }
import play.api.libs.json._
import services.CurrencyConverter
import org.joda.money.{ Money, CurrencyUnit }
import models.ExchangeRate
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.concurrent.Future
import play.api.Logger
import scala.util.{ Failure, Success, Try }

object ExchangeRates extends Controller {

  case class ConversionResult(rate: BigDecimal, result: Money)

  implicit val currencyUnitWrites: Writes[CurrencyUnit] = new Writes[CurrencyUnit] {
    def writes(o: CurrencyUnit): JsValue = Json.toJson(o.getCurrencyCode)
  }

  implicit val moneyWrites: Writes[Money] = new Writes[Money] {
    def writes(o: Money): JsValue = Json.toJson(o.toString)
  }

  implicit val exchangeRateWrites = Json.writes[ExchangeRate]

  implicit val ConversionResultFormat: Writes[ConversionResult] = Json.writes[ConversionResult]

  def rate = Action {
    import scala.concurrent.ExecutionContext.Implicits.global
    Async {
      for (Some(rate) ← CurrencyConverter.findRate(CurrencyUnit.EUR, CurrencyUnit.USD)) yield Ok(rate.rate.toString())
    }
  }

  import scala.concurrent.ExecutionContext.Implicits.global
  def rates(base: CurrencyUnit, required: List[CurrencyUnit] = Nil) = Action {
    Async {
      val ratesFromDB: Seq[ExchangeRate] = ExchangeRate.ratesFromDatabase(base)

      val currenciesToLookUp = required.toSet -- ratesFromDB.map(_.counter).toSet

      val futureRates: Future[Seq[ExchangeRate]] = Future.sequence(currenciesToLookUp.map{ counter ⇒
        CurrencyConverter.findRate(base, counter)
      }).map(_.flatten.toSeq)
      val bothRateLists: Future[List[ExchangeRate]] = futureRates.map(r ⇒ r.toList ::: ratesFromDB.toList ::: Nil)

      bothRateLists.map(rates ⇒ Ok(Json.toJson(rates)))
    }
  }

  def convert(amount: Money, target: CurrencyUnit) = Action {
    Async {
      for {
        Some(rate) ← CurrencyConverter.findRate(amount.getCurrencyUnit, target)
        result = rate.convert(amount)
      } yield Ok(Json.toJson(ConversionResult(rate.rate, result)))
    }
  }

}
