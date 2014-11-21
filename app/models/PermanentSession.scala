/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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
package models

import models.database.PermanentSessions
import org.joda.time.DateTime
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.Play.current
import scala.slick.lifted.Query

import securesocial.core._

case class PermanentSession(id: String,
  userId: String,
  providerId: String,
  creationDate: DateTime,
  lastUsed: DateTime,
  expirationDate: DateTime) {

  def update() = DB.withSession { implicit session: Session ⇒
    PermanentSessions.filter(_.id === this.id).map(_.forUpdate).update((this.lastUsed, this.expirationDate))
  }
}

object PermanentSession {

  /**
   * Save data to DB for later retrieval
   *
   * @param auth User data for authentication
   * @return
   */
  def save(auth: Authenticator): PermanentSession = DB.withSession { implicit session: Session ⇒
    val obj = new PermanentSession(auth.id, auth.identityId.userId, auth.identityId.providerId, auth.creationDate,
      auth.lastUsed, auth.expirationDate)
    Query(PermanentSessions).filter(_.id === auth.id).list.headOption.map { v ⇒
      v.copy(lastUsed = auth.lastUsed).copy(expirationDate = auth.expirationDate).update()
    }.getOrElse {
      PermanentSessions.insert(obj)
    }
    obj
  }

  /**
   * Find session object by its identifier
   * @param id Session identifier
   * @return
   */
  def find(id: String): Option[Authenticator] = DB.withSession { implicit session: Session ⇒
    Query(PermanentSessions).filter(_.id === id).list.headOption.map { v ⇒
      Option(new Authenticator(v.id, new IdentityId(v.userId, v.providerId), v.creationDate, v.lastUsed, v.expirationDate))
    }.getOrElse(None)
  }

  /**
   * Delete a session object
   * @param id Session identifier
   */
  def delete(id: String): Unit = DB.withSession { implicit session: Session ⇒
    Query(PermanentSessions).filter(_.id === id).delete
  }
}