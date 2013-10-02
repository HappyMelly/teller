/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
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

import models.database.Activities
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current
import scala.slick.lifted.Query
import play.api.i18n.Messages

/**
 * An activity stream entry, with is essentially a triple of (subject, predicate, object), in the grammatical sense of the words,
 * such as (Peter, created, organisation Acme Corp).
 *
 * @param id Database primary key
 * @param subject The name of the user who performed the action
 * @param predicate The action performed from the possible `Activity.Predicate` values
 * @param activityObject The name of the data the action was
 */
case class Activity(id: Option[Long], subject: String, predicate: String, activityObject: Option[String], created: DateTime = DateTime.now()) {

  override def toString = Messages("activity." + predicate, "", activityObject.getOrElse("")).trim.capitalize
}

/**
 * The possible activity stream actions, e.g. ‘deleted’.
 */
object Activity {

  object Predicate extends Enumeration {
    type Predicate = Value
    val SignedUp = Value("signup")
    val Created = Value("create")
    val Updated = Value("update")
    val Deleted = Value("delete")
    val Activated = Value("activate")
    val Deactivated = Value("deactivate")
  }

  import Predicate.Predicate

  /**
   * Returns all activity stream entries in reverse chronological order.
   */
  def findAll: List[Activity] = withSession { implicit session ⇒
    Query(Activities).sortBy(_.created.desc).take(50).list
  }

  def insert(subject: String, predicate: Predicate): Activity = {
    insert(subject, predicate, None)
  }

  def insert(subject: String, predicate: Predicate, activityObject: String): Activity = {
    insert(subject, predicate, Some(activityObject))
  }

  /**
   * Inserts a new activity stream entry.
   */
  private def insert(subject: String, predicate: Predicate, activityObject: Option[String]): Activity = {
    withSession { implicit session ⇒
      val activity = Activity(None, subject, predicate.toString, activityObject)
      val id = Activities.forInsert.insert(activity)
      activity.copy(id = Some(id))
    }
  }
}

