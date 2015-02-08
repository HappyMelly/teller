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

package models

import models.database.{ BookingEntryActivities, Activities }
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
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
case class Activity(id: Option[Long],
  subjectId: Long,
  subject: String,
  predicate: String,
  objectType: String,
  objectId: Long,
  activityObject: Option[String],
  supportiveObjectType: Option[String] = None,
  supportiveObjectId: Option[Long] = None,
  supportiveObject: Option[String] = None,
  created: DateTime = DateTime.now()) {

  // Full description including subject (current user’s name).
  def description = {
    val who = subject + " (id = %s)".format(subjectId)
    val what = activityObject map { a ⇒
      "%s (id = %s) %s".format(objectType, objectId, a)
    } getOrElse ""
    supportiveObject map { obj ⇒
      val whom = "%s (id = %s) %s".format(
        supportiveObjectType.getOrElse(""),
        supportiveObjectId.getOrElse(0),
        obj)
      Messages("activity." + predicate, who, what, whom)
    } getOrElse {
      Messages("activity." + predicate, who, what)
    }
  }

  // Short description for use in Flash messages.
  override def toString = Messages("activity." + predicate, "", activityObject.getOrElse("")).trim.capitalize

  def insert: Activity = DB.withSession { implicit session: Session ⇒
    val id = Activities.forInsert.insert(this)
    this.copy(id = Some(id))
  }
}

/**
 * Class with this trait takes part in operations which should be recorded
 */
trait ActivityRecorder {

  /**
   * Returns identifier of the object
   */
  def identifier: Long

  /**
   * Returns string identifier which can be understood by human
   *
   * For example, for object 'Person' human identifier is "[FirstName] [LastName]"
   */
  def humanIdentifier: String

  /**
   * Returns type of this object
   */
  def objectType: String

  /**
   * Returns activity object with data from a particular object
   * @param subject Person who does an activity
   * @param action Name of action
   * @return
   */
  def activity(subject: Person,
    action: String,
    supportiveObj: Option[ActivityRecorder] = None): Activity = {
    supportiveObj map { obj ⇒
      new Activity(None,
        subject.id.get,
        subject.fullName,
        action.toString,
        objectType,
        identifier,
        Some(humanIdentifier),
        Some(obj.objectType),
        Some(obj.identifier),
        Some(obj.humanIdentifier))
    } getOrElse {
      new Activity(None,
        subject.id.get,
        subject.fullName,
        action.toString,
        objectType,
        identifier,
        Some(humanIdentifier))
    }
  }
}

/**
 * The possible activity stream actions, e.g. ‘deleted’.
 */
object Activity {

  object Predicate extends Enumeration {
    val SignedUp = "signup"
    val Created = "create"
    val Updated = "update"
    val Deleted = "delete"
    val Activated = "activate"
    val Deactivated = "deactivate"
    val Added = "add"
    val Replaced = "replace"
    val BalancedAccounts = "balance"
    val Confirmed = "confirm"
    val Approved = "approve"
    val Rejected = "reject"
    val Sent = "send"
    val Connected = "connect"
    val Disconnected = "disconnect"
    val UploadedSign = "sign.upload"
    val DeletedSign = "sign.delete"
  }

  object Type extends Enumeration {
    val Account = "account"
    val BookingEntry = "bookingentry"
    val Brand = "brand"
    val CertificateTemplate = "certificatetmpl"
    val Certificate = "certificate"
    val Contribution = "contribution"
    val Evaluation = "evaluation"
    val Event = "event"
    val EventType = "eventtype"
    val License = "license"
    val Member = "member"
    val Org = "organisation"
    val Participant = "participant"
    val Person = "person"
    val Product = "product"
    val Report = "report"
    val Translation = "translation"
  }

  /**
   * Returns all activity stream entries in reverse chronological order.
   */
  def findAll: List[Activity] = DB.withSession { implicit session ⇒
    Query(Activities).sortBy(_.created.desc).take(50).list
  }

  /**
   * Returns activity stream entries for the given booking entry
   */
  def findForBookingEntry(bookingEntryId: Long): List[Activity] = DB.withSession { implicit session ⇒
    val query = for {
      entryActivity ← BookingEntryActivities if entryActivity.bookingEntryId === bookingEntryId
      activity ← Activities if activity.id === entryActivity.activityId
    } yield activity
    query.sortBy(_.created.desc).list
  }

  def insert(subject: String, predicate: String): Activity = {
    insert(subject, predicate, None)
  }

  def insert(subject: String, predicate: String, activityObject: String): Activity = {
    insert(subject, predicate, Some(activityObject))
  }

  /** Returns new activity record */
  def create(
    subject: String,
    predicate: String,
    activityObject: String): Activity = {
    new Activity(None, 0L, subject, predicate, "null", 0L, Some(activityObject))
  }

  /**
   * Links the given booking entry and activity.
   */
  def link(entry: BookingEntry, activity: Activity): Unit = DB.withSession { implicit session: Session ⇒
    for (entryId ← entry.id; activityId ← activity.id) {
      BookingEntryActivities.insert(entryId, activityId)
    }
  }

  /**
   * Inserts a new activity stream entry.
   */
  private def insert(subject: String, predicate: String, activityObject: Option[String]): Activity = {
    DB.withSession { implicit session ⇒
      val activity = Activity(None, 0L, subject,
        predicate,
        "null",
        0L,
        activityObject)
      val id = Activities.forInsert.insert(activity)
      activity.copy(id = Some(id))
    }
  }
}

