package models.service

import models.Address
import models.database._
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by sery0ga on 25/01/16.
  */
class AddressService extends HasDatabaseConfig[JdbcProfile]
  with AddressTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._
  private val addresses = TableQuery[Addresses]

  /**
    * Return the requested address
    * @param id Address identifier
    */
  def get(id: Long): Future[Address] = db.run(addresses.filter(_.id === id).result).map(_.head)

  def insert(address: Address): Future[Address] = {
    val query = addresses returning addresses.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += address)
  }

  def update(address: Address): Future[Int] =
    db.run(addresses.filter(_.id === address.id).update(address))
}

object AddressService {
  private val _instance = new AddressService

  def get: AddressService = _instance
}