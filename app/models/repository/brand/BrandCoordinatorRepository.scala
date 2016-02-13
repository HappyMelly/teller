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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models.repository.brand

import models.UserAccount
import models.brand.BrandCoordinator
import models.database.UserAccountTable
import models.database.brand.BrandCoordinatorTable
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Contains a set of functions for managing team members in database
 */
class BrandCoordinatorRepository(app: Application) extends HasDatabaseConfig[JdbcProfile]
  with BrandCoordinatorTable
  with UserAccountTable {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)
  import driver.api._
  private val coordinators = TableQuery[BrandCoordinators]

  /**
   * Removes the given person from the given brand
   * @param brandId Brand identifier
   * @param personId Person identifier
   */
  def delete(brandId: Long, personId: Long) =
    db.run(coordinators.filter(_.brandId === brandId).filter(_.personId === personId).delete)

  /**
    * Adds new coordinator to database
    * @param coordinator Brand coordinator
    */
  def save(coordinator: BrandCoordinator): Future[BrandCoordinator] = {
    val actions = (for {
      _ <- coordinators += coordinator
      _ <- userAccountQuery.findByPerson(coordinator.personId).result.headOption.flatMap {
        case Some(account) => updateUserAccountQuery(coordinator.personId)
        case None => insertUserAccountQuery(coordinator.personId)
      }
    } yield ()).transactionally
    db.run(actions).map(_ => coordinator)
  }

  def update(brandId: Long, personId: Long, notification: String, value: Boolean) = {
    val query = coordinators.filter(_.brandId === brandId).filter(_.personId === personId)
    val action = notification match {
      case "event" ⇒ query.map(_.event).update(value)
      case "evaluation" ⇒ query.map(_.evaluation).update(value)
      case _ ⇒ query.map(_.certificate).update(value)
    }
    db.run(action)
  }


  protected def insertUserAccountQuery(personId: Long) = {
    val account = UserAccount.empty(personId).copy(coordinator = true, registered = true, activeRole = true)
    userAccountActions.insert(account)
  }

  protected def updateUserAccountQuery(personId: Long) =
    userAccountQuery.findByPerson(personId).map(x => (x.coordinator, x.activeRole)).update((true, true))
}
