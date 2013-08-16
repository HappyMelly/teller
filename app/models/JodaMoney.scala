package models

import org.joda.money.{ CurrencyUnit, Money }
import java.math.RoundingMode
import play.api.data.Mapping
import play.api.data.Forms._
import scala.Some
import scala.Predef._
import scala.Some

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