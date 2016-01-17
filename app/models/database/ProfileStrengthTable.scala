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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models.database

import models.ProfileStrength
import play.api.libs.json._
import slick.driver.JdbcProfile

private[models] trait ProfileStrengthTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * `ProfileStrength` table mapping
    */
  class ProfileStrengths(tag: Tag) extends Table[ProfileStrength](tag, "PROFILE_STRENGTH") {

    implicit val jsArrayMapper = MappedColumnType.base[JsArray, String](
      { array ⇒ array.toString() }, { str ⇒ Json.parse(str).as[JsArray] })

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def objectId = column[Long]("OBJECT_ID")
    def org = column[Boolean]("ORG")
    def steps = column[JsArray]("STEPS")

    type ProfileStrengthsFields = (Option[Long], Long, Boolean, JsArray)

    def * = (id.?, objectId, org, steps) <>(
      (f: ProfileStrengthsFields) ⇒ ProfileStrength(f._1, f._2, f._3, f._4),
      (p: ProfileStrength) ⇒ Some((p.id, p.objectId, p.org, p.stepsInJson)))

    def forUpdate = steps
  }

  object ProfileStrengths {

    implicit val jsArrayMapper = MappedColumnType.base[JsArray, String](
      { array ⇒ array.toString() }, { str ⇒ Json.parse(str).as[JsArray] })

  }
}