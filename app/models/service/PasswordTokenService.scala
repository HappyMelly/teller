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
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing
 * Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models.service

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.database.PasswordTokenTable
import org.joda.time.DateTime
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import securesocial.core.providers.MailToken
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Contains methods for managing PasswordToken records in database
  */
class PasswordTokenService extends HasDatabaseConfig[JdbcProfile]
  with PasswordTokenTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._

  private val tokens = TableQuery[PasswordTokens]

  /**
    * Deletes a token for the given token identifier
    * @param userId Token identifier
    */
  def delete(userId: String): Unit = db.run(tokens.filter(_.userId === userId).delete)

  /**
    * Deletes all expired tokens
    */
  def deleteExpiredTokens(): Unit = db.run(tokens.filter(_.expire < DateTime.now()).delete)

  /**
    * Returns a mail token if it exists
    * @param userId Token identifier
    */
  def find(userId: String): Future[Option[MailToken]] =
    db.run(tokens.filter(_.userId === userId).result).map(_.headOption)

  /**
    * Adds new token to the database
    * @param token Token
    */
  def insert(token: MailToken): Future[MailToken] = db.run(tokens += token).map(_ => token)
}

object PasswordTokenService {
  private val _instance = new PasswordTokenService

  def get: PasswordTokenService = _instance
}