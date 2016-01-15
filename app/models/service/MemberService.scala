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
import models.database._
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/** Provides operations with database related to members */
class MemberService extends HasDatabaseConfig[JdbcProfile]
  with AddressTable
  with MemberTable
  with OrganisationTable
  with PersonTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._

  private val members = TableQuery[Members]

  /**
   * Returns a list of people and organisations which have member profiles
   *  both active and inactive
   */
  def findAll: Future[List[Member]] = {
    val peopleQuery = for {
      m ← members if m.person === true
      p ← TableQuery[People] if p.id === m.objectId
      a <- TableQuery[Addresses] if a.id === p.addressId
    } yield (m, p, a)

    val orgQuery = for {
      m ← members if m.person === false
      o ← TableQuery[Organisations] if o.id === m.objectId
    } yield (m, o)

    val actions = for {
      people <- peopleQuery.result
      orgs <- orgQuery.result
    } yield (people, orgs)

    db.run(actions).map { result =>
      val people = result._1.map { view =>
        view._2.address_=(view._3)
        view._1.memberObj_=(view._2)
        view._1
      }
      val orgs = result._2.map { view =>
        view._1.memberObj_=(view._2)
        view._1
      }
      people.toList ::: orgs.toList
    }
  }

  /**
   * Returns member if it exists, otherwise - None
   *
   * @param id Member identifier
   */
  def find(id: Long): Future[Option[Member]] = {
    db.run(members.filter(_.id === id).result).map(_.headOption.map { member =>
      if (member.person) {
        db.run(TableQuery[People].filter(_.id === member.objectId).result).map(_.head.map { person =>
          member.memberObj_=(person)
          Some(member)
        })
      } else {
        db.run(TableQuery[Organisations].filter(_.id === member.objectId).result).map(_.head.map { org =>
          member.memberObj_=(org)
          Some(member)
        })
      }
    })
  }

  /**
   * Returns list of members for the given ids
   *
   * @param ids List of identifiers
   */
  def find(ids: List[Long]): Future[List[Member]] = if (ids.isEmpty)
      Future.successful(List())
    else
      db.run(members.filter(_.id inSet ids).result).map(_.toList)

  /**
   * Returns list of members for the given ids
   *
   * @param objectIds List of identifiers
   */
  def findByObjects(objectIds: List[Long]): Future[List[Member]] = if (objectIds.isEmpty)
      Future.successful(List())
    else
      db.run(members.filter(_.objectId inSet objectIds).result).map(_.toList)

  /**
   * Returns member by related object, otherwise - None
   * @param objectId Object identifier
   * @param person Person if true, otherwise - organisation
   * @return
   */
  def findByObject(objectId: Long, person: Boolean): Future[Option[Member]] =
    db.run(members.filter(_.objectId === objectId).filter(_.person === person).result).map(_.headOption)

  /**
   * Inserts the given member to database
   *
   * @param m Object to insert
   * @return Returns member object with updated id
   */
  def insert(m: Member): Future[Member] = {
    val query = members returning members.map(_.id) into ((value, id) => value.copy(id = Some(id)))
    db.run(query += m)
  }

  /**
   * Updates the given member in database
   * @param m Member to update
   * @return Updated member
   */
  def update(m: Member): Future[Member] =
    db.run(members.filter(_.id === m.id).update(m)).map(_ => m)

  /**
   * Deletes a record from database
   * @param objectId Object id
   * @param person If true, object is a person, otherwise - org
   */
  def delete(objectId: Long, person: Boolean): Unit =
    db.run(members.filter(_.objectId === objectId).filter(_.person === person).delete)
}

object MemberService {
  private val instance = new MemberService

  def get: MemberService = instance
}