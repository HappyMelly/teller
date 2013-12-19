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

package models

import org.joda.money.{ CurrencyUnit, Money }
import java.math.RoundingMode
import play.api.data.Mapping
import play.api.data.Forms._
import scala.Predef._
import scala.slick.lifted.MappedTypeMapper

/**
 * Joda Money conversions
 */
object JodaMoney {

  /**
   * Form mapping for Joda Money values, using an implicit conversion from `currency -> amount` to `Money`.
   */
  def jodaMoney(precision: Int = 13, scale: Int = 2): Mapping[Money] = mapping[Money, String, BigDecimal](
    "currency" -> text,
    "amount" -> bigDecimal(precision, scale))(JodaMoney.apply)(JodaMoney.unapply)

  /**
   * Returns a `Money` value from a tuple, using the implicit conversion.
   */
  def apply(currency: String, amount: BigDecimal): Money = currency -> amount

  def unapply(money: Money): Option[(String, BigDecimal)] = Some(money.getCurrencyUnit.getCode, money.getAmount)

  implicit val CurrencyMapper = MappedTypeMapper.base[CurrencyUnit, String](_.toString, CurrencyUnit.of)

  /**
   * Returns a CurrencyUnit for a currency code
   */
  implicit def string2CurrencyUnit(currencyCode: String) = CurrencyUnit.getInstance(currencyCode)

  /**
   * Returns a money value for a decimal amount in the given currency, rounded to the currency’s scale.
   */
  implicit def tuple2Money(money: (String, BigDecimal)): Money = {
    val currency = CurrencyUnit.of(money._1)
    Money.of(currency, money._2.bigDecimal, RoundingMode.DOWN)
  }

  implicit def tuple2option(tuple: (Option[String], Option[BigDecimal])): Option[Money] = tuple match {
    case (Some(currency), Some(amount)) ⇒ Some(currency -> amount)
    case _ ⇒ None
  }
}
