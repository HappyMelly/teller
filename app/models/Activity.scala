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

import models.service.ActivityService
import org.joda.time.DateTime
import play.api.i18n.Messages

class InvalidActivityPredicate extends RuntimeException

abstract class BaseActivity {

  def signedUp: BaseActivity
  def created: BaseActivity
  def updated: BaseActivity
  def deleted: BaseActivity
  def activated: BaseActivity
  def deactivated: BaseActivity
  def added: BaseActivity
  def replaced: BaseActivity
  def balanced: BaseActivity
  def confirmed: BaseActivity
  def approved: BaseActivity
  def rejected: BaseActivity
  def sent: BaseActivity
  def connected: BaseActivity
  def disconnected: BaseActivity
  def uploadedSign: BaseActivity
  def deletedSign: BaseActivity
  def deletedImage: BaseActivity
  def made: BaseActivity
  def becameSupporter: BaseActivity

  def description: String
  def insert(): BaseActivity
}
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
    timestamp: DateTime = DateTime.now()) extends BaseActivity {

  // Full description including subject (current user’s name).
  override def description: String = {
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
  override def toString = {
    val what = objectType + " " + activityObject.getOrElse("")
    supportiveObject map { obj ⇒
      val whom = supportiveObjectType.getOrElse("") + " " + obj
      Messages("activity." + predicate, "", what, whom).trim.capitalize
    } getOrElse Messages("activity." + predicate, "", what).trim.capitalize
  }

  override def signedUp: Activity = this.copy(predicate = Activity.Predicate.SignedUp)
  override def created: Activity = this.copy(predicate = Activity.Predicate.Created)
  override def updated: Activity = this.copy(predicate = Activity.Predicate.Updated)
  override def deleted: Activity = this.copy(predicate = Activity.Predicate.Deleted)
  override def activated: Activity = this.copy(predicate = Activity.Predicate.Activated)
  override def deactivated: Activity = this.copy(predicate = Activity.Predicate.Deactivated)
  override def added: Activity = this.copy(predicate = Activity.Predicate.Added)
  override def replaced: Activity = this.copy(predicate = Activity.Predicate.Replaced)
  override def balanced: Activity = this.copy(predicate = Activity.Predicate.BalancedAccounts)
  override def confirmed: Activity = this.copy(predicate = Activity.Predicate.Confirmed)
  override def approved: Activity = this.copy(predicate = Activity.Predicate.Approved)
  override def rejected: Activity = this.copy(predicate = Activity.Predicate.Rejected)
  override def sent: Activity = this.copy(predicate = Activity.Predicate.Sent)
  override def connected: Activity = this.copy(predicate = Activity.Predicate.Connected)
  override def disconnected: Activity = this.copy(predicate = Activity.Predicate.Disconnected)
  override def uploadedSign: Activity = this.copy(predicate = Activity.Predicate.UploadedSign)
  override def deletedSign: Activity = this.copy(predicate = Activity.Predicate.DeletedSign)
  override def deletedImage: Activity = this.copy(predicate = Activity.Predicate.DeletedImage)
  override def made: Activity = this.copy(predicate = Activity.Predicate.Made)
  override def becameSupporter: Activity = this.copy(predicate = Activity.Predicate.BecameSupporter)

  override def insert(): Activity = if (predicate == Activity.Predicate.None)
    throw new InvalidActivityPredicate
  else
    ActivityService.get.insert(this)
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
}

/**
 * The possible activity stream actions, e.g. ‘deleted’.
 */
object Activity {

  object Predicate extends Enumeration {
    val None = "none"
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
    val DeletedImage = "img.delete"
    val Made = "make"
    val BecameSupporter = "become.supporter"
  }

  object Type extends Enumeration {
    val ApiToken = "token"
    val Account = "account"
    val Attendee = "attendee"
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

  def insert(subject: String, predicate: String): Activity = {
    insert(0L, subject, predicate, None)
  }

  /**
   * Inserts new activity record to database
   * @param subject User
   * @param predicate Action name
   * @param activityObject Action description
   */
  def insert(subject: Person,
    predicate: String,
    activityObject: String): Activity = {
    insert(subject.id.get, subject.fullName, predicate, Some(activityObject))
  }

  def insert(subject: String, predicate: String, activityObject: String): Activity = {
    insert(0L, subject, predicate, Some(activityObject))
  }

  /** Returns new activity record */
  def create(
    subject: String,
    predicate: String,
    activityObject: String): Activity = {
    new Activity(None, 0L, subject, predicate, "null", 0L, Some(activityObject))
  }

  /**
   * Inserts a new activity stream entry.
   */
  private def insert(subjectId: Long,
    subject: String,
    predicate: String,
    activityObject: Option[String]): Activity = {
    val activity = Activity(None, subjectId, subject,
      predicate,
      "null",
      0L,
      activityObject)
    ActivityService.get.insert(activity)
  }
}

