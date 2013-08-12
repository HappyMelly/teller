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
   * Returns a message describing an activity, used for user-interface info messages.
   */
  private def message(predicate: String, activityObject: String): String = {
    Messages("activity." + predicate, "", activityObject).trim.capitalize
  }

  def createMessage(activityObject: String): String = message(Predicate.Created, activityObject)
  def deleteMessage(activityObject: String): String = message(Predicate.Deleted, activityObject)
  def updateMessage(activityObject: String): String = message(Predicate.Updated, activityObject)

  /**
   * Returns a message describing an ‘activated’ or ‘deactivated’ activity.
   */
  def activateMessage(activated: Boolean, activityObject: String): String = {
    message(if (activated) Predicate.Activated else Predicate.Deactivated, activityObject)
  }

  def createRelationshipMessage(activityObject1: String, activityObject2: String): String = {
    createMessage(Messages("activity.relationship.create", activityObject1, activityObject2))
  }

  def deleteRelationshipMessage(activityObject1: String, activityObject2: String): String = {
    deleteMessage(Messages("activity.relationship.delete", activityObject1, activityObject2))
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

