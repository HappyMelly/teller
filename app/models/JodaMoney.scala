/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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

import java.math.RoundingMode
import java.sql.{ResultSet, PreparedStatement}

import org.joda.money.{CurrencyUnit, IllegalCurrencyException, Money}
import play.api.data.Forms._
import play.api.data.Mapping
import slick.driver.JdbcDriver

import scala.Predef._
import scala.language.implicitConversions

class JodaCurrencyMapper(val driver: JdbcDriver) {

  object TypeMapper extends driver.DriverJdbcType[CurrencyUnit] {
    def sqlType = java.sql.Types.VARCHAR
    def setValue(v: CurrencyUnit, p: PreparedStatement, idx: Int): Unit = p.setString(idx, v.toString)
    def getValue(r: ResultSet, idx: Int): CurrencyUnit = CurrencyUnit.of(r.getString(idx))
    def updateValue(v: CurrencyUnit, r: ResultSet, idx: Int): Unit =
      r.updateString(idx, v.toString)
    override def valueToSQLLiteral(value: CurrencyUnit) = value.toString
  }

}

/**
 * Joda Money conversions
 */
object JodaMoney {
  private val mapper = new JodaCurrencyMapper(slick.driver.MySQLDriver)

  /**
   * Form mapping for Joda Money values, using an implicit conversion from `currency -> amount` to `Money`.
   */
  def jodaMoney(precision: Int = 13, scale: Int = 2): Mapping[Money] = mapping[Money, String, BigDecimal](
    "currency" -> text.verifying("error.currency.unknown",
      (cur: String) ⇒ try { CurrencyUnit.of(cur); true } catch { case _: IllegalCurrencyException ⇒ false }),
    "amount" -> bigDecimal(precision, scale))(JodaMoney.apply)(JodaMoney.unapply)

  /**
   * Returns a `Money` value from a tuple, using the implicit conversion.
   */
  def apply(currency: String, amount: BigDecimal): Money = currency -> amount

  def unapply(money: Money): Option[(String, BigDecimal)] =
    Some((money.getCurrencyUnit.getCode, BigDecimal(money.getAmount)))

  implicit val CurrencyMapper = mapper.TypeMapper

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
