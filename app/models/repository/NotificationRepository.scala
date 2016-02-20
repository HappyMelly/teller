/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
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
package models.repository

import models.database.NotificationTable
import models.Notification
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Provides operations with database related to notifications
  */
class NotificationRepository(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with NotificationTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._

  private val notifications = TableQuery[Notifications]

  /**
    * Inserts the given notification to database
    *
    * @param notification Object to insert
    * @return Returns notification object with updated id
    */
  def insert(notification: Notification): Future[Notification] = {
    val query = notifications returning notifications.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += notification)
  }

  def find(personId: Long, offset: Long = 0, limit: Long = 5): Future[Seq[Notification]] =
    db.run(notifications.filter(_.personId === personId).drop(offset).take(limit).result)

  /**
    * Marks the given notifications as Read
    * @param ids Notification id
    * @param personId Person id
    */
  def read(ids: Seq[Long], personId: Long): Future[Int] =
    db.run(notifications.filter(_.id inSet ids).filter(_.personId === personId).map(_.unread).update(true))
}
