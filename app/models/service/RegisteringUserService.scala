package models.service

import models.database.RegisteringUsers
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

/**
  *
  */
class RegisteringUserService extends Services {

  private val users = TableQuery[RegisteringUsers]

  /**
    * Removes the given user from a list of registering one
    * @param userId User identifier
    * @param providerId Provider identifier
    */
  def delete(userId: String, providerId: String): Unit = DB.withSession { implicit session =>
    users.filter(_.userId === userId).filter(_.providerId === providerId).mutate(_.delete())
  }

  /**
    * Returns true if a user with the given id and provider exists
    * @param userId User identifier
    * @param providerId Provider identifier
    */
  def exists(userId: String, providerId: String): Boolean = DB.withSession { implicit session =>
    users.filter(_.userId === userId).filter(_.providerId === providerId).firstOption.isDefined
  }

  /**
    * Adds new registering user
    * @param userId User identifier
    * @param providerId Provider id
    */
  def insert(userId: String, providerId: String): (String, String) = DB.withSession { implicit session =>
    users.insert((userId, providerId))
    (userId, providerId)
  }
}

object RegisteringUserService {
  private val _instance = new RegisteringUserService

  def get: RegisteringUserService = _instance
}
