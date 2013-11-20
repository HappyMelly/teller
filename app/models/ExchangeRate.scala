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

import org.joda.money.{ Money, CurrencyUnit }
import org.joda.time.DateTime
import math.BigDecimal.int2bigDecimal
import java.math.RoundingMode

case class ExchangeRate(base: CurrencyUnit, counter: CurrencyUnit, rate: BigDecimal, timestamp: DateTime) {
  assert(!base.equals(counter) || (base.equals(counter) && rate.compare(1.bigDecimal) == 0))

  private lazy val inverseRate = 1.bigDecimal.setScale(rate.scale).divide(rate.bigDecimal, RoundingMode.DOWN)

  /**
   * Converts an amount from `base` or `counter` or vice versa, using `rate`.
   * @param amount The amount to convert
   * @return The converted amount.
   */
  def convert(amount: Money): Money = amount.getCurrencyUnit match {
    case currency if base.equals(counter) && currency.equals(base) ⇒ amount
    case currency if currency.equals(base) ⇒ amount.convertedTo(counter, rate.bigDecimal, RoundingMode.DOWN)
    case currency if currency.equals(counter) ⇒ amount.convertedTo(base, inverseRate, RoundingMode.DOWN)
    case currency ⇒ throw new IllegalArgumentException(s"Exchange rate is for $base $counter, tried to convert $currency")
  }

  /**
   * Checks if this ExchangeRate can convert the given amount.
   * @param amount
   * @return
   */
  def canConvert(amount: Money): Boolean = base.equals(amount.getCurrencyUnit) || counter.equals(amount.getCurrencyUnit)

  override def toString = s"$base$counter $rate"
}
