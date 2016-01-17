package models.service

import models.database.RegisteringUserTable
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  *
  */
class RegisteringUserService extends HasDatabaseConfig[JdbcProfile]
  with RegisteringUserTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._
  private val users = TableQuery[RegisteringUsers]

  /**
    * Removes the given user from a list of registering one
    * @param userId User identifier
    * @param providerId Provider identifier
    */
  def delete(userId: String, providerId: String): Unit =
    db.run(users.filter(_.userId === userId).filter(_.providerId === providerId).delete)

  /**
    * Returns true if a user with the given id and provider exists
    * @param userId User identifier
    * @param providerId Provider identifier
    */
  def exists(userId: String, providerId: String): Future[Boolean] =
    db.run(users.filter(_.userId === userId).filter(_.providerId === providerId).exists.result)

  /**
    * Adds new registering user
    * @param userId User identifier
    * @param providerId Provider id
    */
  def insert(userId: String, providerId: String): Future[(String, String)] =
    db.run(users += (userId, providerId)).map(_ => (userId, providerId))

}

object RegisteringUserService {
  private val _instance = new RegisteringUserService

  def get: RegisteringUserService = _instance
}
