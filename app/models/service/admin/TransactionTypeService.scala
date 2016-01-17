package models.service.admin

import models.admin.TransactionType
import models.database.BookingEntryTable
import models.database.admin.TransactionTypeTable
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Contains a set of methods for working with transaction types
  */
class TransactionTypeService extends HasDatabaseConfig[JdbcProfile]
  with BookingEntryTable
  with TransactionTypeTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._
  private val transactionTypes = TableQuery[TransactionTypes]

  /**
    * Delete the given transaction type from database
    * @param id Transaction type id
    */
  def delete(id: Long): Future[Unit] = {
    val actions = (for {
      _ <- TableQuery[BookingEntries].filter(_.transactionTypeId === id).map(_.transactionTypeId).update(None)
      _ <- transactionTypes.filter(_.id === id).delete
    } yield ()).transactionally
    db.run(actions)
  }

  /**
    * Returns true if a transaction type with the given value already exists.
    */
  def exists(value: String): Future[Boolean] =
    db.run(transactionTypes.filter(_.name === value).exists.result)

  def find(id: Long): Future[Option[TransactionType]] =
    db.run(transactionTypes.filter(_.id === id).result).map(_.headOption)

  def findAll: Future[List[TransactionType]] =
    db.run(transactionTypes.sortBy(_.name.toLowerCase).result).map(_.toList)

  /**
    * Inserts a new transaction type with the given value.
    */
  def insert(value: String): Future[Int] =
    db.run(transactionTypes += TransactionType(None, value))
}

object TransactionTypeService {
  private val _instance = new TransactionTypeService

  def get: TransactionTypeService = _instance
}