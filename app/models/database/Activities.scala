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
package models.database

import models.database.PortableJodaSupport._
import models.Activity
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

/**
 * `Activity` database table mapping.
 */
private[models] class Activities(tag: Tag) extends Table[Activity](tag, "ACTIVITY") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def subjectId = column[Long]("SUBJECT_ID")
  def subject = column[String]("SUBJECT")
  def predicate = column[String]("PREDICATE")
  def objectType = column[String]("OBJECT_TYPE")
  def objectId = column[Long]("OBJECT_ID")
  def activityObject = column[Option[String]]("OBJECT")
  def supportiveObjectType = column[Option[String]]("SUPPORTIVE_OBJECT_TYPE")
  def supportiveObjectId = column[Option[Long]]("SUPPORTIVE_OBJECT_ID")
  def supportiveObject = column[Option[String]]("SUPPORTIVE_OBJECT")
  def timestamp = column[DateTime]("CREATED")

  def * = (id.?, subjectId, subject, predicate, objectType, objectId,
    activityObject, supportiveObjectType, supportiveObjectId,
    supportiveObject, timestamp) <> ((Activity.apply _).tupled, Activity.unapply)
}