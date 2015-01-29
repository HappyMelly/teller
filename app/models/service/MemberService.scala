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
import models.database.{ People, Members, Organisations }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

/** Provides operations with database related to members */
class MemberService {

  def findAll: List[Member] = DB.withSession { implicit session ⇒
    val peopleQuery = for {
      m ← Members if m.person === true
      p ← People if p.id === m.objectId
    } yield (m, p)
    val result1 = peopleQuery.list
    result1.foreach(d ⇒ d._1.memberObj_=(d._2))

    val orgQuery = for {
      m ← Members if m.person === false
      o ← Organisations if o.id === m.objectId
    } yield (m, o)
    val result2 = orgQuery.list
    result2.foreach(d ⇒ d._1.memberObj_=(d._2))
    result1.map(_._1) ::: result2.map(_._1)
  }

  /** Returns member if it exists, otherwise - None */
  def find(id: Long): Option[Member] = DB.withSession { implicit session ⇒
    Query(Members).filter(_.id === id).firstOption
  }

  /**
   * Inserts the given member to database
   *
   * @param m Object to insert
   * @return Returns member object with updated id
   */
  def insert(m: Member): Member = DB.withSession { implicit session ⇒
    val id: Long = Members.forInsert.insert(m)
    m.copy(id = Some(id))
  }

  /**
   * Updates the given member in database
   * @param m Member to update
   * @return Updated member
   */
  def update(m: Member): Member = DB.withSession { implicit session ⇒
    Members.filter(_.id === m.id).update(m)
    m
  }
}

object MemberService {
  private val instance = new MemberService

  def get: MemberService = instance
}