package models

import models.database.Activities
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current
import scala.slick.lifted.Query
import play.api.i18n.Messages
import play.api.Logger

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
    val SignedUp = Value("signup")
    val Created = Value("create")
    val Updated = Value("update")
    val Deleted = Value("delete")
    val Activated = Value("activate")
    val Deactivated = Value("deactivate")
  }

  /**
   * Returns all activity stream entries in reverse chronological order.
   */
  def findAll: List[Activity] = withSession { implicit session ⇒
    Query(Activities).sortBy(_.created.desc).list
  }

  def insert(subject: String, predicate: Predicate.Value): Activity = {
    insert(subject, predicate, None)
  }

  def insert(subject: String, predicate: Predicate.Value, activityObject: String): Activity = {
    insert(subject, predicate, Some(activityObject))
  }

  /**
   * Inserts a new activity stream entry.
   */
  private def insert(subject: String, predicate: Predicate.Value, activityObject: Option[String]): Activity = {
    withSession { implicit session ⇒
      val activity = Activity(None, subject, predicate.toString, activityObject)
      val id = Activities.forInsert.insert(activity)
      activity.copy(id = Some(id))
    }
  }
}

