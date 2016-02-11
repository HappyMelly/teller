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
import models.EmailToken
import models.database.EmailTokenTable
import org.joda.time.DateTime
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Contains methods for managing EmailToken records in database
  */
class EmailTokenService(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with EmailTokenTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val tokens = TableQuery[EmailTokens]

  /**
    * Deletes a token for the given token identifier
    * @param token Token identifier
    */
  def delete(token: String): Unit = db.run(tokens.filter(_.token === token).delete)

  /**
    * Deletes all expired tokens
    */
  def deleteExpiredTokens(): Unit = db.run(tokens.filter(_.expire < DateTime.now()).delete)

  /**
    * Returns an email token if it exists
    * @param token Token identifier
    */
  def find(token: String): Future[Option[EmailToken]] =
    db.run(tokens.filter(_.token === token).result).map(_.headOption)

  /**
    * Adds new token to the database
    * @param token Token
    */
  def insert(token: EmailToken): Future[EmailToken] = db.run(tokens += token).map(_ => token)
}
