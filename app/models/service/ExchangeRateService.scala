package models.service

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.JodaMoney._
import models.ExchangeRate
import models.database.ExchangeRateTable
import org.joda.money.CurrencyUnit
import org.joda.time.DateTimeZone._
import org.joda.time.LocalDate
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Set of methods for managing exchange rate records
  */
class ExchangeRateService extends HasDatabaseConfig[JdbcProfile]
  with ExchangeRateTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._
  val rates = TableQuery[ExchangeRates]

  /**
    * Returns the exchange rate from the database, or inverts a stored rate, for the given base and counter.
    */
  def fromDatabase(base: CurrencyUnit,
                   counter: CurrencyUnit,
                   date: LocalDate = LocalDate.now(UTC)): Future[Option[ExchangeRate]] = {
    val query = for {
      rate <- rates
      if (rate.base === base && rate.counter === counter) || (rate.base === counter && rate.counter === base)
      if rate.timestamp >= date.toDateTimeAtStartOfDay(UTC)
      if rate.timestamp < date.plusDays(1).toDateTimeAtStartOfDay(UTC)
    } yield rate


    // Invert any rates that were found with base and counter the wrong way around.
    val results = db.run(query.result).map(_.toList.map {
      case rate @ ExchangeRate(_, resultBase, resultCounter, _, _) if resultBase == counter && resultCounter == base ⇒ rate.inverse
      case rate ⇒ rate
    })
    results.map(_.headOption)
  }

  /**
    * Adds new rate to database
    *
    * @param rate Rate
    */
  def insert(rate: ExchangeRate): Future[ExchangeRate] = {
    val query = rates returning rates.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += rate)
  }

  def ratesFromDatabase(base: CurrencyUnit, date: LocalDate = LocalDate.now(UTC)): Future[List[ExchangeRate]] = {
    val query = for {
      rate ← rates
      if rate.base === base || rate.counter === base
      if rate.timestamp >= date.toDateTimeAtStartOfDay(UTC)
      if rate.timestamp < date.plusDays(1).toDateTimeAtStartOfDay(UTC)
    } yield rate

    // Invert any rates that were found with base and counter the wrong way around.
    db.run(query.result).map(_.toList.map {
      case rate @ ExchangeRate(_, _, resultCounter, _, _) if resultCounter == base ⇒ rate.inverse
      case rate ⇒ rate
    })
  }
}

object ExchangeRateService {
  private val _instance = new ExchangeRateService

  def get: ExchangeRateService = _instance
}