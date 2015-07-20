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

package models.service

import models.{ Activity, BookingEntry }
import models.database.{ BookingEntryActivities, Activities }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

/**
 * Contains a set of activity-related functions to work with database
 */
class ActivityService {

  /**
   * Inserts the given activity to database
   * @param activity Activity object
   * @return The given activity with updated id
   */
  def insert(activity: Activity): Activity = DB.withSession { implicit session ⇒
    val activities = TableQuery[Activities]
    val id = (activities returning activities.map(_.id)) += activity
    activity.copy(id = Some(id))
  }

  /**
   * Returns 50 latest activity stream entries in reverse chronological order
   */
  def findAll: List[Activity] = DB.withSession { implicit session ⇒
    val activities = TableQuery[Activities]
    activities.sortBy(_.timestamp.desc).take(50).list
  }

  /**
   * Returns activity stream entries for the given booking entry
   * @TEST
   */
  def findForBookingEntry(bookingEntryId: Long): List[Activity] = DB.withSession {
    implicit session ⇒
      val activities = TableQuery[Activities]
      val entries = TableQuery[BookingEntryActivities]
      val query = for {
        entryActivity ← entries if entryActivity.bookingEntryId === bookingEntryId
        activity ← activities if activity.id === entryActivity.activityId
      } yield activity
      query.sortBy(_.timestamp.desc).list
  }

  /**
   * Links the given booking entry and activity.
   * @TEST
   */
  def link(entry: BookingEntry, activity: Activity): Unit = DB.withSession {
    implicit session ⇒
      val entries = TableQuery[BookingEntryActivities]
      for (entryId ← entry.id; activityId ← activity.id) {
        entries.insert(entryId, activityId)
      }
  }
}

object ActivityService {
  private val instance = new ActivityService

  def get: ActivityService = instance
}
