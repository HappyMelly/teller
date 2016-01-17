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

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.{DateStamp, Experiment}
import org.joda.time.DateTime
import slick.driver.JdbcProfile

private[models] trait ExperimentTable {

  protected val driver: JdbcProfile
  import driver.api._

  /**
    * `Experiment` database table mapping.
    */
  class Experiments(tag: Tag) extends Table[Experiment](tag, "EXPERIMENT") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def memberId = column[Long]("MEMBER_ID")
    def name = column[String]("NAME")
    def picture = column[Boolean]("PICTURE")
    def description = column[String]("DESCRIPTION")
    def url = column[Option[String]]("URL")
    def created = column[DateTime]("CREATED")
    def createdBy = column[String]("CREATED_BY")
    def updated = column[DateTime]("UPDATED")
    def updatedBy = column[String]("UPDATED_BY")

    type ExperimentFields = (Option[Long], Long, String, Boolean, String,
      Option[String], DateTime, String, DateTime, String)

    def * = (id.?, memberId, name, picture, description, url, created, createdBy, updated, updatedBy) <>(
      (e: ExperimentFields) => Experiment(e._1, e._2, e._3, e._5, e._4, e._6,
        DateStamp(e._7, e._8, e._9, e._10)),
      (e: Experiment) => Some(e.id, e.memberId, e.name, e.picture, e.description,
        e.url, e.recordInfo.created, e.recordInfo.createdBy, e.recordInfo.updated,
        e.recordInfo.updatedBy))

    def forUpdate = (name, description, picture, url, updated, updatedBy)
  }

}