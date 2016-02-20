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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models.database

import models.Notification
import play.api.libs.json.Json
import slick.driver.JdbcProfile

private[models] trait NotificationTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * Connects Notification object with its database representation
    */
  class Notifications(tag: Tag) extends Table[Notification](tag, "NOTIFICATION") {

    def id = column[Option[Long]]("ID")
    def personId = column[Long]("PERSON_ID")
    def body = column[String]("BODY")
    def typ = column[String]("TYPE")
    def unread = column[Boolean]("UNREAD")

    type NotificationFields = (Option[Long], Long, String, String, Boolean)

    def * = (id, personId, body, typ, unread) <> (
      (n: NotificationFields) => Notification(n._1, n._2, Json.parse(n._3), n._4, n._5),
      (n: Notification) => Some((n.id, n.personId, n.body.toString(), n.typ, n.unread)))
  }

}