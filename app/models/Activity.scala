package models

import models.database.Activities
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current
import scala.slick.lifted.Query

/**
 * An activity stream entry, with is essentially a triple of (subject, predicate, object), in the grammatical sense of the words,
 * such as (Peter, created, organisation Acme Corp).
 *
 * @param id Database primary key
 * @param subject The name of the user who performed the action
 * @param predicate The action performed from the possible `Activity.Predicate` values
 * @param activityObject The name of the data the action was
 */
case class Activity(id: Option[Long], subject: String, predicate: String, activityObject: Option[String], created: DateTime = DateTime.now())

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
  }

  /**
   * Returns all activity stream entries in reverse chronological order.
   */
  def findAll: List[Activity] = withSession { implicit session ⇒
    Query(Activities).sortBy(_.created.desc).list
  }

  /**
   * Inserts a new activity stream entry.
   */
  def insert(subject: String, predicate: String) {
    withSession { implicit session ⇒
      Activities.forInsert.insert(Activity(None, subject, predicate, None))
    }
  }

  /**
   * Inserts a new activity stream entry.
   */
  def insert(subject: String, predicate: String, activityObject: String) {
    withSession { implicit session ⇒
      Activities.forInsert.insert(Activity(None, subject, predicate, Some(activityObject)))
    }
  }
}

