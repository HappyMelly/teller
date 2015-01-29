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

import models.{ Organisation, Person }
import models.database.{ Organisations, Members, OrganisationMemberships, People }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

import scala.slick.lifted.Query

class PersonService {

  /**
   * Returns list of organizations this person is a member of
   * @param person Person object
   */
  def memberships(person: Person): List[Organisation] = DB.withSession { implicit session: Session ⇒
    val query = for {
      membership ← OrganisationMemberships if membership.personId === person.id.get
      organisation ← membership.organisation
    } yield organisation
    query.sortBy(_.name.toLowerCase).list
  }

  /**
   * Returns person if it exists, otherwise - None
   * @param id Person Identifier
   * @return
   */
  def find(id: Long): Option[Person] = DB.withSession { implicit session: Session ⇒
    val query = for {
      person ← People if person.id === id
    } yield person

    query.firstOption
  }

  /**
   * Returns person if it exists, otherwise - None
   *
   * @param name Person Identifier
   */
  def find(name: String): Option[Person] = DB.withSession { implicit session: Session ⇒
    val transformed = name.replace(".", " ")
    val query = for {
      person ← People if person.firstName ++ " " ++ person.lastName.toLowerCase like "%" + transformed + "%"
    } yield person

    query.firstOption
  }

  /** Returns list of people which are not members (yet!) */
  def findNonMembers: List[Person] = DB.withSession { implicit session ⇒
    import scala.language.postfixOps

    val members = for { m ← Members if m.person === true } yield m.objectId
    val ids = members.list
    Query(People).filter(row ⇒ !(row.id inSet ids)).sortBy(_.firstName).list
  }
}

object PersonService {
  private val instance = new PersonService()

  def get: PersonService = instance
}
