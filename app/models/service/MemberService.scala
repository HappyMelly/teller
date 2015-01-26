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

import models._
import models.database.{ People, Members }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

/** Provides operations with database related to members */
class MemberService {

  def findAll: List[Member] = DB.withSession { implicit session ⇒
    List()
    //    val query = for {
    //      m <- Members if m.person === true
    //      p <- People if p.id === m.objectId
    //    } yield (m, p)
    //    query.list.map { m =>
    //
    //    }
  }

  /** Returns member if it exists, otherwise - None */
  def find(id: Long): Option[Member] = DB.withSession { implicit session ⇒
    Query(Members).filter(_.id === id).firstOption
  }

  /**
   * Returns member which doesn't have complete data
   *
   * Members are created in two steps. On the first one fee data are added.
   * On the second one - person or organisation is added. If the second step
   * is not finished when member is in incomplete state.
   *
   * @param isPerson Only human members
   * @param editorId Only members added by the given editor
   * @return
   */
  def findIncompleteMember(isPerson: Boolean, editorId: Long): Option[Member] = {
    import scala.language.postfixOps
    DB.withSession {
      implicit session ⇒
        Query(Members).
          filter(_.createdBy === editorId).
          filter(_.person === isPerson).
          filter(_.objectId isNull).
          firstOption
    }
  }

  /**
   * Inserts the given member to database
   *
   * @return Returns member object with updated id
   */
  def insert(m: Member): Member = DB.withSession { implicit session ⇒
    val id: Long = Members.forInsert.insert(m)
    m.copy(id = Some(id))
  }
}

object MemberService {
  private val instance = new MemberService

  def get: MemberService = instance
}